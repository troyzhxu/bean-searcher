# Sorting Parameters

## Configurable Items

In a SpringBoot / Grails project, if the `bean-searcher-boot-starter` dependency is used, you can customize the sorting parameters through the following configuration items in the `application.properties` or `application.yml` file of the project:

Configuration Key Name | Meaning | Optional Values | Default Value
-|-|-|-
`bean-searcher.params.sort` | Parameter name for the sorting field | `String` | `sort`
`bean-searcher.params.order` | Parameter name for the sorting method | `String` | `order`
`bean-searcher.params.order-by` | Parameter name for sorting (since v3.4.0) | `String` | `orderBy`

## Single-field Sorting

Usage example (under default configuration):

```java
Map<String, Object> params = MapUtils.builder()
        .orderBy(User::getAge).desc()               // age field, descending order (since v3.7.1) (recommended way)
        .orderBy(User::getAge, "desc")              // Equivalent way 1
        .put("sort", "age")                         // Equivalent way 2
        .put("order", "desc")                       // Equivalent way 2
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

## Multi-field Sorting (since v3.4)

```java
Map<String, Object> params = MapUtils.builder()
        .orderBy(User::getAge).asc()                // age field, ascending order
        .orderBy(User::getTime).desc()              // time field, descending order (call the orderBy method multiple times)
        .put("orderBy", "age:asc,time:desc")        // Equivalent way      
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```
