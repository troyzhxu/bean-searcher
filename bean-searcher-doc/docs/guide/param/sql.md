# 自定义 SQL 条件

> since v3.8

为了保障系统的安全性，自定义 SQL 条件只允许后端通过 参数构建器的 `sql(...)` 方法来实现。

> 自定义 SQL 条件，实际上是一种特殊的 [字段运算符](/guide/param/field#字段运算符)，所以这种方式形成的参数也是 [字段参数](/guide/param/field), 从而也可以在 [逻辑分组](/guide/param/group) 中使用。

## 字段引用

在自定义的 SQL 片段中，用 `$n` 来表示所需引用的第 `n` 个字段，例如：

```java
Map<String, Object> params = MapUtils.builder()
       // 生成 SQL 条件：u.id in (select user_id from xxx)
       .field(User::getId).sql("$1 in (select user_id from xxx)")
       .build();
List<User> users = searcher.searchList(User.class, params);
```

再如：

```java
Map<String, Object> params = MapUtils.builder()
       // 生成 SQL 条件：id < 100 or age > 10
       .field(User::getId, User::getAge).sql("$1 < 100 or $2 > 10")
       .build();
List<User> users = searcher.searchList(User.class, params);
```

## SQL 参数

我们也可以在自定义的 SQL 片段中使用占位符（作为 JDBC 参数），例如：

```java
 Map<String, Object> params = MapUtils.builder()
       // 生成 SQL 条件：id < ? or age > ?，两个占位符参数分别为：100，10
       .field(User::getId, User::getAge).sql("$1 < ? or $2 > ?", 100, 10)
       .build();
List<User> users = searcher.searchList(User.class, params);
```
