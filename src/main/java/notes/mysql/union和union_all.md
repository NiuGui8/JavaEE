`union` 和 `union al`l 的作用为使两表联合查询，区别为 `union` 去重， `union all` 不去重，使用这两个关键字有以下约束：

- UNION 结果集中的列名总是等于 UNION 中第一个 SELECT 语句中的列名
- UNION 内部的 SELECT 语句必须拥有相同数量的列，列也必须拥有相似的数据类型
- 每条 SELECT 语句中的列的顺序必须相同
- 如果子句中有order by,limit，需用括号()包起来，推荐放到所有子句之后，即对最终合并的结果来排序或筛选


		SELECT column_name FROM table1
		UNION
		SELECT column_name FROM table2

		(select * from a order by id) union (select * from b order id)；