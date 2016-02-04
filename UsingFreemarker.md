Download FreeMarker -- http://freemarker.org

Here's how you make it work:

```
public class MyFilter extends LiteMvcFilter {

    private static Configuration cfg = new Configuration();

    static {
        cfg.setClassForTemplateLoading(Templates.class, "templates");
    }

    @Override
    public void configure() {
        map("/test", TestHandler.class).templateResult("OK", "test.ftl");
    }

    @Override
    public void processTemplate(HttpServletRequest request,
            HttpServletResponse response, String templateName, Object handler) {

        try {
            Template temp = cfg.getTemplate(templateName);
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put("request", request);
            rootMap.put("response", response);
            rootMap.put("handler", handler);
            temp.process(rootMap, response.getWriter());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

class TestHandler implements Handler<String> {

    private String someProperty;

    public String get() {
        return "OK";
    }

    public String getSomeProperty() {
        return someProperty;
    }

    public void setSomeProperty(String someProperty) {
        this.someProperty = someProperty;
    }
}

```


feel free to use all the FreeMarker magic... you can access the handler bean ${handler} and all it's properties.

## Writing FreeMarker templates ##

It's always nice when you can focus just on the content of the page and leave the headers, footers, navigation, ads and other stuff surrounding the content to the framework.


### test.ftl ###
```
<#assign pageTitle>Test Page</#assign>
<#assign mainContent>
  
   <h1>Test Page</h1>

   <div>
       someProperty = ${handler.someProperty!''}
   </div>

</#assign>
<#include "mainLayout.ftl"> 
```

### mainLayout.ftl ###

```
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<head>
  <title>
    <#if pageTitle??>${pageTitle}<#else>Default Title</#if>
  </title>
  <link rel="stylesheet" type="text/css" href="/css/main.css" />
</head>
<body>
  <div id="header">
    <a href="/">My Website</a>
  </div>
  <div id="mainContent">
    <#if mainContent??>
      ${mainContent}
    <#else>
      Default Content... not interesting...
    </#if>
  </div>
  <div id="footer">
    &copy; 2009 Me
  </div>
</body>
</html>
```

mainLayout.ftl is totally reusable for other pages. Enjoy!