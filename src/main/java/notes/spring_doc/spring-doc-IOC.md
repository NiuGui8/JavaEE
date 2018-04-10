# spring 官方文档阅读笔记 （@Configuration 的使用）

## 说明
  +@Configuration 是一个累计别的注解，它指明了一个bean 定义的来源的一个对象； 
  +@Configuration 通过被 @bean注解的方法来声明bean。

## 只有在 @Configuration 注解上的类中，且使用了 @Bean 的方法中，才能声明内部bean（即调用其他 @bean方法）：

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


# spring 官方文档阅读笔记 （@Import 的使用）
## 说明 

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

## @ImportResource 可一直接导入xml文件，以引用xml重定义的 bean：

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

### properties-config.xml 
  
	<beans>
		<context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>
	</beans>

### jdbc.properties 
  
	jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
	jdbc.username=sa
	jdbc.password=

	public static void main(String[] args) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
		TransferService transferService = ctx.getBean(TransferService.class);
		// ...
	}



# @Profile 
## 说明 

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

## 激活配置文件 

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
	ctx.getEnvironment().setActiveProfiles("development");
	ctx.register(SomeConfig.class, StandaloneDataConfig.class, JndiDataConfig.class);
	ctx.refresh();

## 可同时激活多个：
  
	ctx.getEnvironment().setActiveProfiles("profile1", "profile2");

## 指定默认配置文件 

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

## 配置优先级

For a common StandardServletEnvironment, the full hierarchy looks as follows, with the highest-precedence entries at the top:

	ServletConfig parameters (if applicable, e.g. in case of a DispatcherServlet context)

	ServletContext parameters (web.xml context-param entries)

	JNDI environment variables ("java:comp/env/" entries)

	JVM system properties ("-D" command-line arguments)

	JVM system environment (operating system environment variables)
	
## @PropertySource 可获取.property文件中的值 

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

## 注意 ${} 占位符能获取已经注册的属性值：

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

## 事件监听 

### 标准监听事件： 

+ ContextRefreshedEvent
+ ContextStartedEvent
+ ContextStoppedEvent
+ ContextClosedEvent
+ RequestHandledEvent

### 事件监听注解 @EventListener

#### 可以使用在有参或无参方法中：
          
	public class BlackListNotifier {
		private String notificationAddress;
		public void setNotificationAddress(String notificationAddress) {
		this.notificationAddress = notificationAddress;
		}
		@EventListener
		public void processBlackListEvent(BlackListEvent event) {
		// notify appropriate parties via notificationAddress...
		}
		}
		@EventListener({ContextStartedEvent.class, ContextRefreshedEvent.class})
		public void handleContextStart() {
		 ...
	}

### 也可以通过注解的条件属性来添加额外的运行时过滤，该属性定义了一个SpEL表达式，该表达式应该与实际调用特定事件的方法相匹配。 

  原文： 
  
/*It is also possible to add additional runtime filtering via the condition attribute of the annotation  
that defines a SpEL expression that should match to actually invoke the method for a particular event.*/ 
	
	@EventListener(condition = "#blEvent.test == 'foo'")
	public void processBlackListEvent(BlackListEvent blEvent) {
	 // notify appropriate parties via notificationAddress...
	}

详情 [查看](https://docs.spring.io/spring/docs/5.0.4.RELEASE/spring-framework-reference/core.html#context-functionality-events-annotation "监听事件注解")

添加 @Async 可以以异步的方式处理事件，但是需注意以下两点： 

1 调用者不能捕获处理事件过程中跑出的异常 
2 不能发送回复信息 

*事件执行排序 @Order(number) <

## Generic events（通用事件） 

You may also use generics to further define the structure of your event. Consider an EntityCreatedEvent<T>  <br/>
where T is the type of the actual entity that got created. You can create the following listener definition  <br/>
to only receive EntityCreatedEvent for a Person: <br/>

	@EventListener
	public void onPersonCreated(EntityCreatedEvent<Person> event) {
	    ...
	}

# 以Java EE RAR 的形式部署spring 应用 详情 [查看](https://docs.spring.io/spring/docs/5.0.4.RELEASE/spring-framework-reference/core.html#context-deploy-rar "java EE RAR形式部署") 

注意 ApplicationContext 包含了 BeanFactory 所有的功能，普通的BeanFactory 不支持例如aop等很多的特性，且实现一些功能特别的繁琐。
