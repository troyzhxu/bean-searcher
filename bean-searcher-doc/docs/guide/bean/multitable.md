# 多表关联

注解 `@SearchBean` 的 `tables` 属性，可以很容易的指定多张表的关联关系。

## 内连接

```java
@SearchBean(
    tables = "user u, role r",  // 两表关联
    where = "u.role_id = r.id", // 静态 Where 条件（v3.8.0 之前的写法是 joinCond）
    autoMapTo = "u"             // 字段默认自动映射到 u
) 
public class User {

    private Long username;      // 自动映射到 u.username

    @DbField("r.name")          // 映射到 r.name
    private String rolename;

    // Getter and Setter ...
}
```

或者：

```java
@SearchBean(
    tables = "user u inner join role r on u.role_id = r.id",
    autoMapTo = "u"
) 
public class User {

    private Long username;      // 自动映射到 u.username

    @DbField("r.name")          // 映射到 r.name
    private String rolename;

    // Getter and Setter ...
}
```

## 左连接

```java
@SearchBean(
    tables = "user u left join user_detail d on u.id = d.user_id",
    autoMapTo = "u"
) 
public class User {

    private Long username;  // 自动映射到 u.username

    @DbField(mapTo = "d")   // 映射到 d.address
    private String address;

    // Getter and Setter ...
}
```

## 右连接

```java
@SearchBean(tables = "user_detail d right join user u on u.id = d.user_id")
public class User {
    // ...
}
```

## From 子查询

```java
@SearchBean(
    tables = "(select id, name from user) t"
) 
public class User {
    // ...
}
```

## 关联 From 子查询

```java
@SearchBean(
    tables = "user u, (select user_id, ... from ...) t", 
    where = "u.id = t.user_id"
) 
public class User {
    // ...
}
```
