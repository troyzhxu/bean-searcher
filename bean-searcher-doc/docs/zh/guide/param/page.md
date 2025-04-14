# 分页参数

Bean Searcher 提供了两种分页：**Page 分页** 与 **Offset 分页**。

## 可配置项

在 SpringBoot / Grails 项目中，若使用了 `bean-searcher-boot-starter` 依赖，则可在项目配置文件 `application.properties` 或 `application.yml` 中对分页进行个性化配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.pagination.type` | 分页类型 | `page`、`offset` | `page`
`bean-searcher.params.pagination.default-size` | 默认每页查询条数 | `正整数` | `15`
`bean-searcher.params.pagination.max-allowed-size` | 每页最大查询条数（分页保护） | `正整数` | `100`
`bean-searcher.params.pagination.page` | 页码参数名（在 type = page 时有效） | `字符串` | `page`
`bean-searcher.params.pagination.size` | 每页大小参数名 | `字符串` | `size`
`bean-searcher.params.pagination.offset` | 偏移参数名（在 type = offset 时有效） | `字符串` | `offset`
`bean-searcher.params.pagination.start` | 起始页码 或 起始偏移量 | `自然数` | `0`

## Page 分页

> 分页类型为 `page` 时生效

Page 分页提供两个分页参数（参数名可配置）：

* `page`: 页码
* `size`: 每页查询条数

用法示例（默认配置下）：

```java
Map<String, Object> params = MapUtils.builder()
        .page(0, 15)                    // 第 0 页，每页 15 条（推荐写法）
        .put("page", 0)                 // 等效写法
        .put("size", 15)                // 等效写法
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

## Offset 分页

> 分页类型为 `offset` 时生效

Offset 分页也提供两个分页参数（参数名可配置）：

* `offset`: 偏移量
* `size`: 每页查询条数

用法示例（默认配置下）：

```java
Map<String, Object> params = MapUtils.builder()
        .limit(0, 15)                   // 偏移 0 条，查询 15 条（推荐写法）
        .put("offset", 0)               // 等效写法
        .put("size", 15)                // 等效写法
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

## 起始 页码/偏移量

配置项 起始页码/偏移量（`bean-searcher.params.pagination.start`）默认是 `0`，在 Page 分页机制下，`page` 参数为 0 表示查询第 1 页。当把 起始页码 配置为 `1` 时，则 `page` 参数为 1 才表示查询第 1 页。Offset 分页同理。

::: warning 注意
* **v3.7.0** 以前 `参数构建工具` 的 `page(long page, int size)` 与 `limit(long offset, int size)` 方法不受该配置影响。
* **v3.7.0** 及以后版本该配置则对  `参数构建工具`  同样有作用。
:::

## 最大查询条数

配置项 最大查询条数（`bean-searcher.params.pagination.max-allowed-size`）默认是 `100`，它可以风控一些恶意查询：比如黑客想通过一次查询 **1 亿** 条数据从而让我们系统崩溃时，Bean Searcher 会自动把它缩小为 `100`。

## 默认分页大小

配置项 默认分页大小（`bean-searcher.params.pagination.default-size`）默认是 `15`，在用户为添加分页参数时，默认每页查询 15 条数据。

::: tip
`Searcher` 实例的 `searchAll(...)` 方法不受分页参数影响
:::
