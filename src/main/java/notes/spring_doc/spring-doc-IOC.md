##################### spring 官方文档阅读笔记 （@Configuration 的使用）###########

1 说明
  @Configuration 是一个累计别的注解，它指明了一个bean 定义的来源的一个对象； 
@Configuration 通过被 @bean注解的方法来声明bean。

2 只有在 @Configuration 注解上的类中，且使用了 @Bean 的方法中，才能声明内部bean（即调用其他 @bean方法）：

  @Configuration
  public class AppConfig {
    @Bean
    public Foo foo() {
        return new Foo(bar());
    }
    @Bean
    public Bar bar() {
        return new Bar();
    }
  }


##################### spring 官方文档阅读笔记 （@Import 的使用）###########
1 说明 
  @Import 可以在 @Bean 注解的方法中引用另一个 @Configuration 类的实例：
  @Configuration
  public class ConfigA {

    @Bean
    public A a() {
        return new A();
    }
  }

  @Configuration
  @Import(ConfigA.class)
  public class ConfigB {

    @Bean
    public B b() {
        return new B();
    }
  }

  以上，实例化AplicationContext的时候只要提供 B 类：
  public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigB.class);

    // now both beans A and B will be available...
    A a = ctx.getBean(A.class);
    B b = ctx.getBean(B.class);
}


*注 ： spring 4.2 开始，支持导入其他组件类。

2 @ImportResource 可一直接导入xml文件，以引用xml重定义的 bean：
  @Configuration
  @ImportResource("classpath:/com/acme/properties-config.xml")
  public class AppConfig {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(url, username, password);
    }
  }

  ##properties-config.xml
  <beans>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>
  </beans>

  ##jdbc.properties
  jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
  jdbc.username=sa
  jdbc.password=

  public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
  }