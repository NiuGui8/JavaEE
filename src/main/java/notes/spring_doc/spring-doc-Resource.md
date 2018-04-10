# Resource

## 简介
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

## Resource 最重要的几个方法

+ getInputStream()
+ exists()
+ isOpen()
+ getDescription()
---

### getInputStream()
定位和打开资源，返回一个可供读资源的的输入流InputStream,需要及时关闭流。
---

### exists()
判断资源是否确实存在
---

### isOpen()
这个资源是否代表一个带有开放流的句柄，如果为true，inputSream不能被多次读取，读取完一次就立马关闭流，防止资源内存泄漏。
---

### getDescription()
返回资源的描述信息，这通常是完全限定的文件名或资源的实际URL。
---

## Resource实现类
### UrlResource
UrlResource，包含了java.util.URL,可以用来访问任何通常通过URL访问的对象，例如：文件，http目标，ftp目标等；
所有URL都有标准的字符表达，通过这些标准可以前缀，可以标识这些资源的类型。例如：

+ file： 通过文件系统来访问资源 
+ http: 通过http协议访问资源
+ ftp: 通过FTP通道来访问资源等等
---

PropertyEditor 可以决定创建哪个实现类,当资源字符串的前缀为已知的种类时，可以创建更适合的实现类，当无法识别时，默认为普通的URL字符串，即创建UrlResource。
---

### ClassPathResource
从类路径中获取资源，但是不能获取jar中没有扩展到类路径的资源。它使用线程上下文类加载器、给定的类加载器或载入资源的给定类。
处理classpath:前缀字符串时可用此实现。
---

### FileSystemResource
This is a Resource implementation for java.io.File handles. It obviously supports resolution as a File, and as a URL.
---

### ServletContextResource
用来处理ServletContext相关的资源，在相关web应用程序的根目录中解释相关路径。支持URL和流操作。
---

### InputStreamResource
为给定InputStream的实现，当没有其他合适的实现类可用时才用此类，如果您需要将资源描述符保留在某个地方，或者您需要多次读取一条流，则不要使用它。
---

### ByteArrayResource
为给定的字节数组的实现，为给定的字节数组创建了一个ByteArrayInputStream ；它对于从任何给定的字节数组中加载内容非常有用，而不需要使用单一的InputStreamResource。
---

## ResourceLoader
### 说明
ResourceLoader的实现类，可以返回一个资源实体：

	public interface ResourceLoader {

		Resource getResource(String location);

	}
---

### 注意
所有的ApplicationContext都实现了 ResourceLoader 接口，因此所有的ApplicationContext都能用来获取Resource的实例。

	Resource template = ctx.getResource("some/resource/path/myTemplate.txt");

如果你在特定的 ApplicationContext 中调用 `getResource()` ，并且传入的路径没有特别的前缀；你会根据特定的 ApplicationContext 得到特定的 Resource的实例。
因此，您可以以适合于特定应用程序上下文的方式加载资源。

如果你想强制使用某一种实现类，可以在路径中加上前缀，如：想强制使用 `ClassPathResource`，就加上 classpath:

	Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");
---


<table>
  <tr>
    <th width=330%>前缀</th>
    <th width=33%,>例子</th>
    <th width="34%">说明</th>
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
