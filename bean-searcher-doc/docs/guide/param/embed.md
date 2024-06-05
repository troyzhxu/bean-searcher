# 内嵌参数

内嵌参数，即是：嵌入到实体类注解内的参数（参见：[实体类 > 嵌入参数](/guide/bean/params) 章节），它可分为 普通内嵌参数 与 拼接参数，他们可以轻松处理各种复杂的 SQL 检索问题。

## 普通内嵌参数

普通内嵌参数，是以一个冒号（`:`）前缀（形如 `:name`）的形式嵌入到实体类注解的 SQL 片段中的参数。

例如，有这样的一个 SearchBean：

```java
@SearchBean(where = "age = :age") 
public class Student {
    // 省略 ...
}
```

则我们可以用如下方式检索年龄为 20 的学生：

```java
Map<String, Object> params = MapUtils.builder()
        // 注意：这里不能使用 field 方法，因为 age 字段不是实体类的属性
        .put("age", 20)         // 指定内嵌参数 age 的值为 20  
        .build();
List<User> users = searcher.searchList(User.class, params);
```

::: tip
普通内嵌参数 最终会被 Bean Searcher 处理为一种 JDBC 参数，无需担心 SQL 注入问题。
:::

## 拼接参数

拼接参数（since v2.1），是以一个冒号（`:`）为前缀一个冒号（`:`）为后缀（形如 `:name:`）的形式嵌入到实体类注解的 SQL 片段中的参数。

拼接参数的用武之地非常广：能用 普通内嵌参数 的地方肯定能用 拼接参数，而 普通内嵌参数 搞不定的地方 拼接参数 则可以轻松搞定，它可以做到 **动态生成 SQL**。

::: tip 特别注意
拼接参数会直接拼接在 SQL 内，开发者在检索时应 **先检查该参数值的合法性，以免产生 SQL 注入漏洞**。如果某个需求用 普通内嵌参数 和 拼接参数 都可以解决，我们推荐您使用 普通内嵌参数 去实现它。
::: 

* 参考：[实体类 > 嵌入参数](/guide/bean/params.html) 章节；
<!-- * 参考：[实践 > 动态检索 > 分表检索](/simples.html#分表检索) 案例。[TODO] -->
