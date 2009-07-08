/*
 * Copyright 2009 Pavel Jbanov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.litemvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

public abstract class LiteMvcFilter implements Filter {
    
    private static Map<Pattern, Binding> bindingsMap = new HashMap<Pattern, Binding>();
    
    private static Map<String, Action> globalResults = new HashMap<String, Action>();

    private static Map<Class<? extends Exception>, String> exceptionsMap = new HashMap<Class<? extends Exception>, String>();
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        
        RequestHelper.setHttpServletRequest(request);
        RequestHelper.setHttpServletResponse(response);

        try {
            Binding binding = null;
            Matcher matcher = null;
            String servletPath = request.getServletPath();
            for (Pattern p : bindingsMap.keySet()) {
                matcher = p.matcher(servletPath);
                if (matcher.matches()) {
                    binding = bindingsMap.get(p);
                    break;
                }
            }

            if (binding != null) {
                Object handler = createObject(binding.getHandlerClass());
                
                if (tryToExecuteMethod(request, response, matcher, binding, handler)) {
                    return;
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            RequestHelper.clean();
        }
        chain.doFilter(req, resp);
    }

    @SuppressWarnings("unchecked")
    private boolean tryToExecuteMethod(HttpServletRequest request,
            HttpServletResponse response, Matcher matcher,
            Binding binding, Object handler)
            throws Exception {
        
        Method[] methods = binding.getHandlerClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(request.getMethod().toLowerCase())) {
                Class<?>[] parmTypes = method.getParameterTypes();
                
                int matchCount = 1;
                ArrayList<Object> args = new ArrayList<Object>();
                for (Class<?> clazz : parmTypes) {
                    if (clazz.equals(HttpServletRequest.class)) {
                        args.add(request);
                    }
                    if (clazz.equals(HttpServletResponse.class)) {
                        args.add(response);
                    }
                    if (clazz.equals(String.class)) {
                        args.add(matcher.group(matchCount));
                        matchCount++;
                    }
                }
                Map handlerDescr = BeanUtils.describe(handler);
                
                for (Object oParmName : request.getParameterMap().keySet()) {
                    String parmName = (String) oParmName;
                    if (handlerDescr.containsKey(parmName)) {
                        BeanUtils.setProperty(handler, parmName, request.getParameter(parmName));
                    }
                }
                
                boolean isError = false;
                String result = null; 
                try {
                    result = (String) method.invoke(handler, args.toArray());
                } catch (Exception e) {
                    if (exceptionsMap.containsKey(e.getClass())) {
                        result = exceptionsMap.get(e.getClass());
                        isError = true;
                    } else {
                        throw e;
                    }
                }
                
                if (result == null) {
                    return true;
                }
                
                Action action = null; 
                
                if (!isError) { // we only check global results in case of an error.
                    action = binding.getAction(result);
                }
                
                if (action == null) {
                    action = globalResults.get(result);
                }
                
                if (action == null) {
                    throw new UnmappedResultException(result, isError);
                }
                
                if (action instanceof TemplateAction) {
                    processTemplate(request, response, ((TemplateAction) action).getTemplateName(), handler);
                    return true;
                }
                
                if (action instanceof DispatcherAction) {
                    request.setAttribute("handler", handler);
                    request.getRequestDispatcher(((DispatcherAction) action).getLocation()).forward(request, response);
                    return true;
                }
                
                if (action instanceof RedirectAction) {
                    RedirectAction redirectAction = (RedirectAction) action;
                    String location = redirectAction.getLocation();
                    if (redirectAction.isEvaluate()) { 
                        Map<String, Object> context = new HashMap<String, Object>();
                        context.put("handler", handler);
                        JexlContext jc = JexlHelper.createContext();
                        jc.getVars().put("handler", handler);

                        location = "" + redirectAction.getExpression().evaluate(jc);
                    }
                    response.sendRedirect(location);
                    return true;
                }
                
                if (!customActionProcessor(binding, request, response, action)) {
                    throw new RuntimeException("unkown action type: " + action.getClass().getName());
                }
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void init(FilterConfig arg0) throws ServletException { 
        configure();
    }
    
    @Override
    public void destroy() { }

    
    public Object createObject(Class<?> clazz) throws Exception {
        return clazz.newInstance();
    }
    
    public void processTemplate(HttpServletRequest request,
            HttpServletResponse response, String templateName, Object handler) {

        try {
            request.getRequestDispatcher(templateName).include(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    protected final Binding map(String regex, Class<?> handler) {
        Binding binding = new Binding(regex, handler);
        bindingsMap.put(binding.getPattern(), binding);
        return binding;
    }
    
    protected void mapException(Class<? extends Exception> ex, String globalResult) {
        exceptionsMap.put(ex, globalResult);
    }

    protected void globalTemplateResult(String result, String templateName) {
        globalResults.put(result, new TemplateAction(templateName));
    }
    
    protected void globalDispatchResult(String result, String location) {
        globalResults.put(result, new DispatcherAction(location));
    }
    
    protected void globalRedirectResult(String result, String location) {
        globalResults.put(result, new RedirectAction(location));
    }
    
    protected void globalResult(String result, Action action) {
        globalResults.put(result, action);
    }
    
    protected abstract void configure();
    
    public boolean customActionProcessor(Binding binding, HttpServletRequest request, HttpServletResponse response, Action action) {
        return false;
    }
}
