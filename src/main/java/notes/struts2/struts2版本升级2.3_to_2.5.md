## struts2 由2.3.X升级到2.5.X 注意事项
### 1 jar包做相应升级

- struts-XXX 等jar包版本的直接升级
- 可能需注意的jar包 asm freemarker commons-lang3 等



### 2 struts.xml 配置文件

	<!-- 让struts2支持动态方法调用 -->
	<constant name="struts.enable.DynamicMethodInvocation" value="true" />
	<!-- Action名称中是否还是用斜线 -->
	<constant name="struts.enable.SlashesInActionNames" value="true" />
package 标签里

	<global-allowed-methods>regex:.*</global-allowed-methods>

### 3 web.xml

	<filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</filter-class>
改成

	<filter-class>org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter</filter-class>
### 4 jsp里的struts标签
`<s:iterator>`、` <s:bean>`、` <s:peoperty>` 等标签的属性 id 改成 var

  * 注意： 控制台通常会报`ClassNotFoundException` 如果有某个相关jar包需同步升级，通过这个异常很容找到需要升级的jar包