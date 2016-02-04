# Getting Started #

**Step 0:** Download latest version... -- [Downloading](Downloading.md)

**Step 1:** Extend filter

```

package org.example;

import org.example.TestHandler.Result;

import com.litemvc.LiteMvcFilter;

public class MyFilter extends LiteMvcFilter {

   @Override
   public void configure() {
      map("/test", TestHandler.class)
         .dispatchResult("OK", "/test.jsp");
   }
}

```

**Step 2:** Create handler

```
package org.example;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class TestHandler {

   private String someProperty;

   /**
    * Handles HTTP GET requests
    * 
    * @return OK if everything runs smooth
    */
   public String get() {
      return "OK";
   }

   /**
    * Handles HTTP POST requests.
    * 
    * Note that this method does not return anything. This is because we
    * decided to write the response content in the method as opposed to
    * using JSP or any other template.
    */
   public void post(HttpServletResponse response) throws IOException {
      response.setContentType("text/html");
      response.getWriter().write("<h1>Hello World! (Post)</h1>");
   }

   public String getSomeProperty() {
      return someProperty;
   }

   public void setSomeProperty(String someProperty) {
      this.someProperty = someProperty;
   }
}
```

**Step 3:** Write template (JSP)

```

   // test.jsp
   <h1>Hello World!</h1>
   <%
      org.example.TestHandler handler = (org.example.TestHandler) request.getAttribute("handler");
   %>
   someProperty=<%= handler.getSomeProperty() %>

```


**Step 4:** Add filter to web.xml

```

  <filter>
    <filter-name>MyLiteMvcFilter</filter-name>
    <filter-class>org.example.MyFilter</filter-class>
  </filter>
 
  <filter-mapping>
    <filter-name>MyLiteMvcFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>

```

**Step 5:** browse away... http://localhost:8080/test?someProperty=test%20value

you should see:
# Hello World! #
someProperty=test value

if you do form POST to http://localhost:8080/test, you'll see:
# Hello World! (Post) #