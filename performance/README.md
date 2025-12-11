# 性能对比测试

> Bean Searcher 专为效率而生，性能并不是它的主要目的。虽然如此，但性能上也不输其它 ORM，甚至会更强。

### 测试结论

* 比 Spring Data Jdbc 高 **5 ~ 10** 倍
* 比 Spring Data JPA 高 **2 ~ 3** 倍
* 比 MyBatis Plus 高 **2 ~ 3** 倍
* 比 原生 MyBatis 高 **1 ~ 2** 倍

> 注：以下测试均基于嵌入式数据库 H2 进行，它执行 SQL 的耗时很少，因此 ORM 框架的性能差距才能凸显。
> 实际在生产环境下用 MySQL 等传统远程数据库服务时，各框架的整体表现差距并没有这么大。
> 但如果您使用的是内存数据库 或者诸如 ClickHouse 这种高性能数据库，那么框架性能的差距就会非常明显！

下面是具体测试案例，大家可以下载代码运行验证：

## vs Spring Data Jdbc

对比结果：

![](../assets/vs_data_jdbc.png)

[查看测试代码](./vs-data-jdbc/src/test/java/com/example/demo)

## vs Spring Data JPA

对比结果：

![](../assets/vs_data_jpa.png)

[查看测试代码](./vs-data-jpa/src/test/java/com/example/demo)

## vs 原生 MyBatis

对比结果：

![](../assets/vs_mybatis.png)

[查看测试代码](./vs-mybatis/src/test/java/com/example/demo)

## vs MyBatis Plus

对比结果：

![](../assets/vs_mybatis_plus.png)

[查看测试代码](./vs-mybatis-plus/src/test/java/com/example/demo)

