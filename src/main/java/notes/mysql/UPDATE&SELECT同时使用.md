在遇到需要 `update` 设置的参数来自从其他表 `select` 出的结果时,需要把 `update` 和 `select` 结合使用,不同数据库支持的形式不一样,在mysql中如下:

	update A inner join(select id,name from B) c on A.id = c.id set A.name = c.name;

根据AB两个表的id相同为条件,把A表的name修改为B的sql语句就如上所示
