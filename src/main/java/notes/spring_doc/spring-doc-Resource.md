# Resource

## 1 简介
标准的java.util.URL 及其处理方式，不能很好的根据URL来处理底层资源，例如：

+ 不能通过类路径来处理各种资源
+ 或相对于ServletContext 

Spring 的 Resource接口能更好的处理底层资源:

	public interface Resource extends InputStreamSource {
		boolean exists();
		boolean isOpen();
		URL getURL() throws IOException;
		File getFile() throws IOException;
		Resource createRelative(String relativePath) throws IOException;
		String getFilename();
		String getDescription();
	}

	public interface InputStreamSource {
		InputStream getInputStream() throws IOException;
	}

## 2 Resource 最重要的几个方法

+ getInputStream()
+ exists()
+ isOpen()
+ getDescription()



### 2.1 getInputStream()
定位和打开资源，返回一个可供读资源的的输入流InputStream,需要及时关闭流。


### 2.2 exists()
判断资源是否确实存在


### 2.3 isOpen()
这个资源是否代表一个带有开放流的句柄，如果为true，inputSream不能被多次读取，读取完一次就立马关闭流，防止资源内存泄漏。


### 2.4 getDescription()
返回资源的描述信息，这通常是完全限定的文件名或资源的实际URL。

---

## 3 Resource实现类
### 3.1 UrlResource
UrlResource，包含了java.util.URL,可以用来访问任何通常通过URL访问的对象，例如：文件，http目标，ftp目标等；
所有URL都有标准的字符表达，通过这些标准可以前缀，可以标识这些资源的类型。例如：

+ file： 通过文件系统来访问资源 
+ http: 通过http协议访问资源
+ ftp: 通过FTP通道来访问资源等等


PropertyEditor 可以决定创建哪个实现类,当资源字符串的前缀为已知的种类时，可以创建更适合的实现类，当无法识别时，默认为普通的URL字符串，即创建UrlResource。



### 3.2 ClassPathResource
从类路径中获取资源，但是不能获取jar中没有扩展到类路径的资源。它使用线程上下文类加载器、给定的类加载器或载入资源的给定类。
处理classpath:前缀字符串时可用此实现。


### 3.3 FileSystemResource
This is a Resource implementation for java.io.File handles. It obviously supports resolution as a File, and as a URL.


### 3.4 ServletContextResource
用来处理ServletContext相关的资源，在相关web应用程序的根目录中解释相关路径。支持URL和流操作。


### 3.5 InputStreamResource
为给定InputStream的实现，当没有其他合适的实现类可用时才用此类，如果您需要将资源描述符保留在某个地方，或者您需要多次读取一条流，则不要使用它。


### 3.6 ByteArrayResource
为给定的字节数组的实现，为给定的字节数组创建了一个ByteArrayInputStream ；它对于从任何给定的字节数组中加载内容非常有用，而不需要使用单一的InputStreamResource。

---

## 4 ResourceLoader
### 4.1 说明
ResourceLoader的实现类，可以返回一个资源实体：

	public interface ResourceLoader {

		Resource getResource(String location);
	}


### 4.2 注意
所有的ApplicationContext都实现了 ResourceLoader 接口，因此所有的ApplicationContext都能用来获取Resource的实例。

	Resource template = ctx.getResource("some/resource/path/myTemplate.txt");

如果你在特定的 ApplicationContext 中调用 `getResource()` ，并且传入的路径没有特别的前缀；你会根据特定的 ApplicationContext 得到特定的 Resource的实例。
因此，您可以以适合于特定应用程序上下文的方式加载资源。

如果你想强制使用某一种实现类，可以在路径中加上前缀，如：想强制使用 `ClassPathResource`，就加上 classpath:

	Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");


<table>
  <tr>
    <th width=30%>前缀</th>
    <th width=30%,>例子</th>
    <th width="40%">说明</th>
  </tr>
  <tr>
    <td> classpath: </td>
    <td> classpath:com/myapp/config.xml  </td>
    <td> 从类路径加载  </td>
  </tr>
  <tr>
    <td>file: </td>
    <td> file:///data/config.xml </td>
    <td> 从文件系统加载，加载为URL </td>
  <tr>
    <td>http: </td>
    <td> http://myserver/logo.png </td>
    <td>  从http加载，加载为一个URL </td>
  </tr>
   <tr>
    <td>(none) </td>
    <td> /data/config.xml </td>
    <td>  根据 ApplicationContext决定 </td>
  </tr>
</table>

---

## 5 ResourceLoaderAware 
ResourceLoaderAware是一个特殊的标记接口，他能识别需要提供 ResourceLoader 引用的对象： 

	public interface ResourceLoaderAware {
		void setResourceLoader(ResourceLoader resourceLoader);
	}

当一个被spring管理的bean实现了ResourceLoaderAware接口，这个类会被应用上下文识别为ResourceLoaderAware，然后应用上下文会调用`setResourceLoader(ResourceLoader)`，并将自己作为参数传递（任何 ApplitionContext 都实现了 ResourceLoader 接口）。

### 5.1 可以直接通过属性注入资源，属性需为资源模板类：

	<bean id="myBean" class="...">
	    <property name="template" value="some/resource/path/myTemplate.txt"/>
	</bean>

如果需要制定特定的资源模板类：

	<property name="template" value="classpath:some/resource/path/myTemplate.txt">
	<property name="template" value="file:///some/resource/path/myTemplate.txt"/>

---

## 6 ApplitionContexts 和资源路径
### 6.1 构造ApplicationContext 
ApplicationContext 构造里往往会传入一个字符串或字符串数组，例如通过xml文件来构造ApplicationContext,如果传入一个没有特殊前缀的字符串，会根据实现类的类型来选择从哪里加载文件，并使用哪个资源模板类。如，当你通过如下构造方法时：
	ApplicationContext ctx = new ClassPathXmlApplicationContext("conf/appContext.xml");

通过类路径来加载配置文件，并使用了 `ClassPathResource `，如果通过如下构造方法：
	
	ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/appContext.xml");
通过文件系统来加载配置文件，以当前工作空间为相对路径。当然，也可以加特定前缀，这样就会使用特定前缀使用特定的ResourceLoader：

	ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:conf/appContext.xml");
#### 6.1.1 构造ClassPathXmlApplicationContext 的快捷方式
`ClassPathXmlApplicationContext` 提供了很多方便实例化的构造方法，一种基本思路是，提供配置文件的文件名称，另一种是提供一个`ClassPathXmlApplicationContext ` 能从中获取配置文件的类。 <br/><br/>
假如有一下路径：

	com/
	  foo/
	    services.xml
	    daos.xml
	    MessengerService.class

ClassPathXmlApplicationContext  需要通过 `service.xml` 和 `dao.xml` 类实例化类定义组件， 可以这样实例化：

	ApplicationContext ctx = new ClassPathXmlApplicationContext(
	    new String[] {"services.xml", "daos.xml"}, MessengerService.class);

### 6.2 ApplicationContext 构造资源路径中的通配符
#### 6.2.1 ant 风格的通配符

	 /WEB-INF/*-context.xml
	  com/mycompany/**/applicationContext.xml
	  file:C:/some/path/*-context.xml
	  classpath:com/mycompany/**/applicationContext.xml

#### 6.2.2 classpath* 前缀

	ApplicationContext ctx =
   		 new ClassPathXmlApplicationContext("classpath*:conf/appContext.xml");

