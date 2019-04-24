### 1 支持大小写

	lower_case_table_names=1

### 2 分组查询可返回非分组查询字段

	sql_mode=STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION

### 3 设置编码

	character-set-server=utf8



### 4 其他

	安装： apt-get mysql-server
    新建用户：CREATE USER 'username'@'localhost' IDENTIFIED BY 'password';

	分配访问权限：
    GRANT ALL PRIVILEGES ON *.* TO 'king'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION; //可在任意客户端登录
	flush privileges; //刷新配置