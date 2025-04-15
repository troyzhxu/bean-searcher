# Multi-table Association

The `tables` attribute of the annotation `@SearchBean` can easily specify the association relationships between multiple tables.

## Inner Join

```java
@SearchBean(
    tables = "user u, role r",  // Association between two tables
    where = "u.role_id = r.id", // Static Where condition (Before v3.8.0, it was written as joinCond)
    autoMapTo = "u"             // Fields are automatically mapped to u by default
) 
public class User {

    private Long username;      // Automatically mapped to u.username

    @DbField("r.name")          // Mapped to r.name
    private String rolename;

    // Getter and Setter ...
}
```

Or:

```java
@SearchBean(
    tables = "user u inner join role r on u.role_id = r.id",
    autoMapTo = "u"
) 
public class User {

    private Long username;      // Automatically mapped to u.username

    @DbField("r.name")          // Mapped to r.name
    private String rolename;

    // Getter and Setter ...
}
```

## Left Join

```java
@SearchBean(
    tables = "user u left join user_detail d on u.id = d.user_id",
    autoMapTo = "u"
) 
public class User {

    private Long username;  // Automatically mapped to u.username

    @DbField(mapTo = "d")   // Mapped to d.address
    private String address;

    // Getter and Setter ...
}
```

## Right Join

```java
@SearchBean(tables = "user_detail d right join user u on u.id = d.user_id")
public class User {
    // ...
}
```

## Subquery in the From Clause

```java
@SearchBean(
    tables = "(select id, name from user) t"
) 
public class User {
    // ...
}
```

## Association with a Subquery in the From Clause

```java
@SearchBean(
    tables = "user u, (select user_id, ... from ...) t", 
    where = "u.id = t.user_id"
) 
public class User {
    // ...
}
```