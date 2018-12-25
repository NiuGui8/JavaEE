相关配置文件：

	<!-- 开启注解 -->
    <context:annotation-config />

    <!-- 注解扫描包路径 -->
	<context:component-scan base-package="com.szlabsun.hj212.common.*" >
		<!-- 如果所注解的类也包含在以下包中，可使用
		<context:exclude-filter type="annotation" expression="org.springframework.stereotype.Service" /> -->
	</context:component-scan>

	<!-- 配置事务管理器 -->
	<bean id="transactionManager"
		  class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>

	<!-- 注解方式配置事务 -->
	<tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true"/>

注解的事务配置注意事项：

- 注解的方法必须为 public
- 注解的方法必须为 spring 容器中bean 中的方法
- 调用本类方法不会触发
- 默认unchecked 方法能触发，checked方法无法触发（可配置）
- 重复扫描的包无法触发，但可以用 `@EnableTransactionManagement` 触发

