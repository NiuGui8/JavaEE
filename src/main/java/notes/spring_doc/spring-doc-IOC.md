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