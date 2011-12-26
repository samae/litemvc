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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Binding {

    private Class<?> handlerClass;
    private Pattern pattern;
    private String methodName;
    
    private Map<String, Action> result2action = new HashMap<String, Action>();
    
    Binding(String regex, Class<?> handlerClass) {
        this.pattern = Pattern.compile(regex);
        this.handlerClass = handlerClass;
    }
    
    Binding(String regex, Class<?> handlerClass, String methodName) {
        this.pattern = Pattern.compile(regex);
        this.handlerClass = handlerClass;
        this.methodName = methodName;
    }
    
    public Class<?> getHandlerClass() {
        return handlerClass;
    }
    
    public Pattern getPattern() {
        return pattern;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public Binding templateResult(String result, String templateName) {
        result2action.put(result, new TemplateAction(templateName));
        return this;
    }
    
    public Binding templateResult(String result, String templateName, boolean evaluate) {
    	result2action.put(result, new TemplateAction(templateName, evaluate));
    	return this;
    }
    
    public Binding dispatchResult(String result, String location) {
        result2action.put(result, new DispatcherAction(location));
        return this;
    }
    
    public Binding redirectResult(String result, String location) {
        return redirectResult(result, location, false);
    }
    
    public Binding redirectResult(String result, String location, boolean evaluate) {
        result2action.put(result, new RedirectAction(location, evaluate));
        return this;
    }
    
    public Binding customResult(String result, Action action) {
        result2action.put(result, action);
        return this;
    }
    
    public Action getAction(String result) {
        return result2action.get(result);
    }
}
