#日常记录

##### 1 查询并返回主键

	<insert id="" useGeneratedKeys="true" keyProperty="id"> </insert>
`useGeneratedKeys` 设置为主自增长为 true , `keyProperty` 指定主键对应实体类中的属性，查询结束后可 `getXxx` 主键属性获取

##### 2 parameterMap

和 `resultMap` 类似，映射出入的参数属性，较少使用。