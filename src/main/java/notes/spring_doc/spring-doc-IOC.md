##################### spring �ٷ��ĵ��Ķ��ʼ� ��@Configuration ��ʹ�ã�###########

1 ˵��
  @Configuration ��һ���ۼƱ��ע�⣬��ָ����һ��bean �������Դ��һ������ 
@Configuration ͨ���� @beanע��ķ���������bean��

2 ֻ���� @Configuration ע���ϵ����У���ʹ���� @Bean �ķ����У����������ڲ�bean������������ @bean��������

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


##################### spring �ٷ��ĵ��Ķ��ʼ� ��@Import ��ʹ�ã�###########
1 ˵�� 
  @Import ������ @Bean ע��ķ�����������һ�� @Configuration ���ʵ����
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

  ���ϣ�ʵ����AplicationContext��ʱ��ֻҪ�ṩ B �ࣺ
  public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigB.class);

    // now both beans A and B will be available...
    A a = ctx.getBean(A.class);
    B b = ctx.getBean(B.class);
}


*ע �� spring 4.2 ��ʼ��֧�ֵ�����������ࡣ

2 @ImportResource ��һֱ�ӵ���xml�ļ���������xml�ض���� bean��
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
#1 ˵��
  ���ж�������ļ�ʱ������ʹ��@Profileע�⣬ָ��ʹ���ĸ������ļ���
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

#2 ���������ļ�
  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
  ctx.getEnvironment().setActiveProfiles("development");
  ctx.register(SomeConfig.class, StandaloneDataConfig.class, JndiDataConfig.class);
  ctx.refresh();

  *ע�⣺��ͬʱ��������
  ctx.getEnvironment().setActiveProfiles("profile1", "profile2");

#3 ָ��Ĭ�������ļ�
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

#4 �������ȼ�
	For a common StandardServletEnvironment, the full hierarchy looks as follows, with the highest-precedence entries at the top:

	ServletConfig parameters (if applicable, e.g. in case of a DispatcherServlet context)

	ServletContext parameters (web.xml context-param entries)

	JNDI environment variables ("java:comp/env/" entries)

	JVM system properties ("-D" command-line arguments)

	JVM system environment (operating system environment variables)
#5 @PropertySource �ɻ�ȡ.property�ļ��е�ֵ
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

  *ע�� ${} ռλ���ܻ�ȡ�Ѿ�ע�������ֵ��
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