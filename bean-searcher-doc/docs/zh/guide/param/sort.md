# 排序参数

## 可配置项

在 SpringBoot / Grails 项目中，若使用了 `bean-searcher-boot-starter` 依赖，可在项目的 `application.properties` 或 `application.yml` 文件中通过如下配置项对排序参数进行定制：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.sort` | 排序字段参数名 | `字符串` | `sort`
`bean-searcher.params.order` | 排序方法参数名 | `字符串` | `order`
`bean-searcher.params.order-by` | 排序参数名（since v3.4.0） | `字符串` | `orderBy`

## 单字段排序

用法示例（默认配置下）：

```java
Map<String, Object> params = MapUtils.builder()
        .orderBy(User::getAge).desc()               // age 字段，降序（since v3.7.1）（推荐写法）
        .orderBy(User::getAge, "desc")              // 等效写法 1
        .put("sort", "age")                         // 等效写法 2
        .put("order", "desc")                       // 等效写法 2
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

## 多字段排序（since v3.4）

```java
Map<String, Object> params = MapUtils.builder()
        .orderBy(User::getAge).asc()                // age 字段 升序
        .orderBy(User::getTime).desc()              // time 字段 降序（多次调佣 orderBy 方法）
        .put("orderBy", "age:asc,time:desc")        // 等效写法      
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```
