# Spring 官方文档阅读笔记 【 IOC 容器 】 部分

IOC容器是 `Spring` 框架中最重要的部分之一，紧接着是面向切面变成的 AOP 技术，Spring 框架中有自己的 AOP 框架，除了容易理解之外，还解决了 java 企业级项目中百分之八十的 AOP 处理需求。

## 1.  IOC 容器和 beans 的介绍
`IOC` 控制反转，也被人们称为依赖注入，是一个定义相关类依赖关系的一个过程（仅仅通过构造器参数、对工厂方法的参数，或者在从工厂方法构造或返回后在对象实例上设置的属性。），然后容器在创建bean时注入这些依赖项。<br/>

`org.springframework.beans` 和 `org.springframework.context` 两个包是 `IOC` 的基础包 ， `BeanFactory` 接口提供了高级的配置机制来管理任何的对象 ； `ApplicationContext` 是 `BeanFactory` 的一个父类 ， 它加入了 AOP 的特性； 消息处理 ，甚至是 `publication` ，以及应用层的上下文 如使用与 web 应用中的 `WebApplicationContext`。

- `ApplicationContext` 完全包含 `BeanFactory`
- `BeanFactory` 提供了配置框架和基础功能
- `ApplicationContext` 添加了更多企业级的功能

在 `Spring` 中，被 IOC 容器管理的对象，被称为 beans , 一个 `bean` 就是被 IOC 容器管理的一个对象、一个集合，简单来说一个 `bean` 就是应用中众多对象之一。 `beans` 和他们之间的依赖，被容器反射在配置元数据里。

## 2. 容器概览

`org.springframework.context.ApplicationContext` 就代表了 Spring 的 IOC 容器，他负责实例化、配置和组装上述提到的 `beans`;
容器通过配置元数据来得到指令去实例化、配置、组装哪些 `beans` ; 配置元数据一般描述在 XML 文件、java 注解或者 java 代码中，它可以允许你表达应用构建的对象或者他们之间的依赖关系。

Spring 提供了几个开箱即用的 `ApplicationContext` 的实现类，在独立的应用中，我们通常使用 `ClassPathXmlApplicationContext` 或
`FileSystemXmlApplicationContext`.

当 XML 配置格式提供了传统的配置元数据描述方式，我们也可以通过 java 注解或者代码来描述配置元数据，此时我们只需要提供很少的 XML配置。

在大多数应用场景中，我们并不需要在用户代码中显示的实例化一个或多个应用上下文实例。例如，在web中，一个简单的8行左右的 web 描述符XML模板，通常就足够了 。

下图是 Spring 如何工作的一张高级视图，您的应用程序类与配置元数据相结合，以便在创建和初始化ApplicationContext之后，您就拥有了一个完全配置和可执行的系统或应用程序。

![图片不见了](../images/container-magic.png 'Spring 工作原理图')

### 2.1 配置元数据

如上图所示，Spring IOC 容器使用了一种配置元数据，这个元数据代表了作为一个容器开发者如何来实例化、配置和组装应用中的对象。Spring配置至少包含一个容器必须管理的bean定义，通常不止一个bean定义。基于xml的配置元数据显示这些bean被配置为顶级元素/元素中的元素。Java配置通常在@configuration类中使用@bean注释的方法。

这些 bean 定义相当于，组成你应用的实际的对象，比如服务层对象 、 数据处理层对象（DAO）、表现层对象（如Struts 中的 action 对象）。

下面是一个通过 XML 文件来配置 bean 的一个例子：

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
	    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	    xsi:schemaLocation="http://www.springframework.org/schema/beans
	        http://www.springframework.org/schema/beans/spring-beans.xsd">
	
	    <bean id="..." class="...">
	        <!-- collaborators and configuration for this bean go here -->
	    </bean>
	
	    <bean id="..." class="...">
	        <!-- collaborators and configuration for this bean go here -->
	    </bean>
	
	    <!-- more bean definitions go here -->
	
	</beans>

`id` 为 `bean` 的一个唯一标识， `class` 为这个 `bean` 所隐射的类的全限定路径名，值写的是对其他 `bean` 的依赖关系。  

### 2.2 实例化一个容器

实例化一个容器非常简单，提供给 `ApplicationContext` 构造的是允许容器加载的真实资源字符串，如从本地文件系统、从 java 的 `CLASSPATH` 等等

	ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

下面的例子展示了服务层的配置：

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
	    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	    xsi:schemaLocation="http://www.springframework.org/schema/beans
	        http://www.springframework.org/schema/beans/spring-beans.xsd">
	
	    <!-- services -->
	
	    <bean id="petStore" class="org.springframework.samples.jpetstore.services.PetStoreServiceImpl">
	        <property name="accountDao" ref="accountDao"/>
	        <property name="itemDao" ref="itemDao"/>
	        <!-- additional collaborators and configuration for this bean go here -->
	    </bean>
	
	    <!-- more bean definitions for services go here -->
	
	</beans>

DAO 层的配置例子：

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
	    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	    xsi:schemaLocation="http://www.springframework.org/schema/beans
	        http://www.springframework.org/schema/beans/spring-beans.xsd">
	
	    <bean id="accountDao"
	        class="org.springframework.samples.jpetstore.dao.jpa.JpaAccountDao">
	        <!-- additional collaborators and configuration for this bean go here -->
	    </bean>
	
	    <bean id="itemDao" class="org.springframework.samples.jpetstore.dao.jpa.JpaItemDao">
	        <!-- additional collaborators and configuration for this bean go here -->
	    </bean>
	
	    <!-- more bean definitions for data access objects go here -->
	
	</beans>

你可以定义多个 XML 文件，然后通过 `import` 标签引入相关的配置：

	<beans>
	    <import resource="services.xml"/>
	    <import resource="resources/messageSource.xml"/>
	    <import resource="/resources/themeSource.xml"/>
	
	    <bean id="bean1" class="..."/>
	    <bean id="bean2" class="..."/>
	</beans>

*The Groovy Bean Definition DSL

也可以使用 `.groovy` 文件来配置 bean, 例子：

	beans {
	    dataSource(BasicDataSource) {
	        driverClassName = "org.hsqldb.jdbcDriver"
	        url = "jdbc:hsqldb:mem:grailsDB"
	        username = "sa"
	        password = ""
	        settings = [mynew:"setting"]
	    }
	    sessionFactory(SessionFactory) {
	        dataSource = dataSource
	    }
	    myService(MyService) {
	        nestedBean = { AnotherBean bean ->
	            dataSource = dataSource
	        }
	    }
	}

### 2.3 容器的使用

`ApplicationContext ` 是一个高级工厂的接口，它有能力维护不同 `beans` 和他们之间依赖的注册表，使用 `T getBean(String name, Class<T> requiredType)` 可以获取 `bean` 的实例。

`ApplicationContext ` 能够让你读取 bean 定义并处理他们，如下所示：

	// create and configure beans
	ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
	
	// retrieve configured instance
	PetStoreService service = context.getBean("petStore", PetStoreService.class);
	
	// use configured instance
	List<String> userList = service.getUsernameList();


如果是读取 `Groovy ` 配置, 唯一的不同就是实现类不同：

	ApplicationContext context = new GenericGroovyApplicationContext("services.groovy", "daos.groovy");

最灵活的变体是 `GenericApplicationContext `，与读者代表相结合,如，`XmlBeanDefinitionReader` <text style="color:red;">(for XML)</text>:

	GenericApplicationContext context = new GenericApplicationContext();
	new XmlBeanDefinitionReader(context).loadBeanDefinitions("services.xml", "daos.xml");
	context.refresh();

或者是 ` GroovyBeanDefinitionReader`<text style="color:red;">(for Groovy)</text> :

	GenericApplicationContext context = new GenericApplicationContext();
	new GroovyBeanDefinitionReader(context).loadBeanDefinitions("services.groovy", "daos.groovy");
	context.refresh();

这样的读者委托可以在相同的应用程序上下文中进行混合和匹配，如果需要的话，可以从不同的配置源读取bean定义。

你可以使用 `getBean ` 方法获取 bean 的实例 ，`ApplicationContext ` 还有一些获取 bean 实例的方法，但是应用代码中不会用到他们。甚至，在应用代码中连 `getBean` 也完全用不到，也不依赖任何的 Spring API 。Spring与web框架的集成为各种web框架组件提供了依赖注入，例如控制器和jsf-托管bean，允许您通过元数据（例如自动装配注释）声明对特定bean的依赖。

## 3. bean 概览


 





