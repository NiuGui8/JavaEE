# 简介
	标准的java.util.URL 及其处理方式，不能很好的根据URL来处理底层资源，例如：
		*不能通过类路径来处理各种资源
		*或相对于ServletContext 
# Resource
	Spring 的 Resource接口能更好的处理底层资源.

``public interface Resource extends InputStreamSource {	<br/>
	boolean exists(); <br/>
	boolean isOpen(); <br/>
	URL getURL() throws IOException; <br/>
	File getFile() throws IOException; <br/>
	Resource createRelative(String relativePath) throws IOException; <br/>
	String getFilename(); <br/>
	String getDescription(); <br/>
}``

``public interface InputStreamSource { <br/>
	InputStream getInputStream() throws IOException; <br/>
}``
