Download Guice -- http://code.google.com/p/google-guice

Here's how you make it work:

```
public class MyFilter extends LiteMvcFilter {

   private static Injector injector = Guice.createInjector(new MyModule());
	
   @Override
   public void configure() {
      map("/test", TestHandler.class)
         .dispatchResult("OK", "/test.jsp");
   }
   
   @Override
   public Object createObject(Class<?> clazz) throws Exception {
      return injector.getInstance(clazz);
   }
}

class TestHandler implements Handler<String> {

   // now you can inject stuff or attach interceptors
   @Inject
   private MyService myService;
	
   public String get() {
      myService.doStuff();
      return "OK";
   }
}

```