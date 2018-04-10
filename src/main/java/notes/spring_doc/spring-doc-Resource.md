# 简介
	标准的java.util.URL 及其处理方式，不能很好的根据URL来处理底层资源，例如：<br/>
		*不能通过类路径来处理各种资源
		*或相对于ServletContext <br/>
# Resource
	Spring 的 Resource接口能更好的处理底层资源 <br/>