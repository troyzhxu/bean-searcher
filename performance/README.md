# 性能对比测试

> Bean Searcher 专为效率而生，性能并不是它的主要目的。虽然如此，但性能上也不输其它 ORM，甚至会更强。

### 测试结论

* 比 Spring Data Jdbc 高 **5 ~ 10** 倍
* 比 Spring Data JPA 高 **2 ~ 3** 倍
* 比 MyBatis Plus 高 **2 ~ 3** 倍
* 比 原生 MyBatis 高 **1 ~ 2** 倍

下面是测试案例，大家可以运行验证：

## vs Spring Data Jdbc

对比结果：

![](../assets/vs_data_jdbc.png)

[查看测试代码](./vs-data-jdbc/src/test/java/com/example/demo/DemoApplicationTests.java)

## vs Spring Data JPA

对比结果：

![](../assets/vs_data_jpa.png)

[查看测试代码](./vs-data-jpa/src/test/java/com/example/demo/DemoApplicationTests.java)

## vs 原生 MyBatis

对比结果：

![](../assets/vs_mybatis.png)

[查看测试代码](./vs-mybatis/src/test/java/com/example/demo/DemoApplicationTests.java)

## vs MyBatis Plus

对比结果：

![](../assets/vs_mybatis_plus.png)

[查看测试代码](./vs-mybatis-plus/src/test/java/com/example/demo/DemoApplicationTests.java)

