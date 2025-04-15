# Custom SQL Conditions

> since v3.8

To ensure the security of the system, custom SQL conditions can only be implemented by the backend through the `sql(...)` method of the parameter builder.

> Custom SQL conditions are actually a special type of [field operator](/en/guide/param/field#Field Operators). Therefore, the parameters formed in this way are also [field parameters](/en/guide/param/field), and can thus be used in [logical grouping](/en/guide/param/group).

## Field Reference

In the custom SQL snippet, use `$n` to represent the `n`-th field to be referenced. For example:

```java
Map<String, Object> params = MapUtils.builder()
       // Generate SQL condition: u.id in (select user_id from xxx)
       .field(User::getId).sql("$1 in (select user_id from xxx)")
       .build();
List<User> users = searcher.searchList(User.class, params);
```

Another example:

```java
Map<String, Object> params = MapUtils.builder()
       // Generate SQL condition: id < 100 or age > 10
       .field(User::getId, User::getAge).sql("$1 < 100 or $2 > 10")
       .build();
List<User> users = searcher.searchList(User.class, params);
```

## SQL Parameters

We can also use placeholders (as JDBC parameters) in the custom SQL snippet. For example:

```java
 Map<String, Object> params = MapUtils.builder()
       // Generate SQL condition: id < ? or age > ?, and the two placeholder parameters are: 100, 10
       .field(User::getId, User::getAge).sql("$1 < ? or $2 > ?", 100, 10)
       .build();
List<User> users = searcher.searchList(User.class, params);
```
