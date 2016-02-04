Download Velocity -- http://velocity.apache.org

Here's how you make it work:

```
public class MyFilter extends LiteMvcFilter {

    @Override
    public void configure() {
        map("/test", TestHandler.class).templateResult("OK", "/templates/test.vm");
    }

    @Override
    public void processTemplate(HttpServletRequest request,
            HttpServletResponse response, String templateName, Object handler) {

        VelocityContext context = new VelocityContext();
        context.put("request", request);
        context.put("response", response);
        context.put("handler", handler);
        
        Template template =  null;
        try {
            template = Velocity.getTemplate(templateName);
            
            template.merge(context, response.getWriter());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);

        try {
            Properties props = new Properties();
            props.setProperty("resource.loader", "class");
            props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
            /*
            // you'll need to write your own logger on appengine... otherwise it blows up

            props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
              "org.example.MyVelcityLogger");
            */
            
            Velocity.init(props);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
```