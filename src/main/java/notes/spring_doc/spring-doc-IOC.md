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



################ @Profile #################################
#1 说明
  当有多个配置文件时，可以使用@Profile注解，指定使用哪个配置文件：
  @Configuration
  @Profile("development")
  public class StandaloneDataConfig {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .addScript("classpath:com/bank/config/sql/test-data.sql")
            .build();
    }
  }

  @Configuration
  @Profile("production")
  public class JndiDataConfig {
    @Bean(destroyMethod="")
    public DataSource dataSource() throws Exception {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
    }
  }

#2 激活配置文件
  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
  ctx.getEnvironment().setActiveProfiles("development");
  ctx.register(SomeConfig.class, StandaloneDataConfig.class, JndiDataConfig.class);
  ctx.refresh();

  *注意：可同时激活多个：
  ctx.getEnvironment().setActiveProfiles("profile1", "profile2");

#3 指定默认配置文件
  @Configuration
  @Profile("default")
  public class DefaultDataConfig {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .build();
    }
  }

#4 配置优先级
	For a common StandardServletEnvironment, the full hierarchy looks as follows, with the highest-precedence entries at the top:

	ServletConfig parameters (if applicable, e.g. in case of a DispatcherServlet context)

	ServletContext parameters (web.xml context-param entries)

	JNDI environment variables ("java:comp/env/" entries)

	JVM system properties ("-D" command-line arguments)

	JVM system environment (operating system environment variables)
#5 @PropertySource 可获取.property文件中的值
  @Configuration
  @PropertySource("classpath:/com/myco/app.properties")
  public class AppConfig {
    @Autowired
    Environment env;

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setName(env.getProperty("testbean.name"));
        return testBean;
    }
  }

  *注意 ${} 占位符能获取已经注册的属性值：
  @Configuration
  @PropertySource("classpath:/com/${my.placeholder:default/path}/app.properties")
  public class AppConfig {
    @Autowired
    Environment env;

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setName(env.getProperty("testbean.name"));
        return testBean;
    }
  }