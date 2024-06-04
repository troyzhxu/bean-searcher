# 多表关联

注解 `@SearchBean` 的 `tables` 属性，可以很容易的指定多张表的关联关系。

## 内连接

```java
@SearchBean(
    tables = "user u, role r",          // 两表关联
    where = "u.role_id = r.id"          // Where 条件（v3.8.0 及之后的写法）
    joinCond = "u.role_id = r.id"       // Where 条件（v3.8.0 之前的写法）
) 
public class User {

    @DbField("u.name")                  // 字段映射
    private Long username;

    @DbField("r.name")                  // 字段映射
    private String rolename;

    // Getter and Setter ...
}
```

或者：

```java
@SearchBean(tables = "user u inner join role r on u.role_id = r.id") 
public class User {
    // ...
}
```

## 左连接

```java
@SearchBean(tables = "user u left join user_detail d on u.id = d.user_id") 
public class User {

    @DbField("u.name")
    private Long username;

    @DbField("d.address")
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
