# Pagination Parameters

Bean Searcher provides two types of pagination: **Page Pagination** and **Offset Pagination**.

## Configurable Items

In SpringBoot / Grails projects, if the `bean-searcher-boot-starter` dependency is used, you can customize the pagination configuration in the project configuration file `application.properties` or `application.yml`:

Configuration Key Name | Meaning | Optional Values | Default Value
-|-|-|-
`bean-searcher.params.pagination.type` | Pagination type | `page`, `offset` | `page`
`bean-searcher.params.pagination.default-size` | Default number of items per page | `Positive integer` | `15`
`bean-searcher.params.pagination.max-allowed-size` | Maximum number of items per page (pagination protection) | `Positive integer` | `100`
`bean-searcher.params.pagination.page` | Page number parameter name (valid when type = page) | `String` | `page`
`bean-searcher.params.pagination.size` | Number of items per page parameter name | `String` | `size`
`bean-searcher.params.pagination.offset` | Offset parameter name (valid when type = offset) | `String` | `offset`
`bean-searcher.params.pagination.start` | Starting page number or starting offset | `Natural number` | `0`

## Page Pagination

> Effective when the pagination type is `page`

Page pagination provides two pagination parameters (parameter names can be configured):

* `page`: Page number
* `size`: Number of items per page

Usage example (under default configuration):

```java
Map<String, Object> params = MapUtils.builder()
        .page(0, 15)                    // Page 0, 15 items per page (recommended way)
        .put("page", 0)                 // Equivalent way
        .put("size", 15)                // Equivalent way
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

## Offset Pagination

> Effective when the pagination type is `offset`

Offset pagination also provides two pagination parameters (parameter names can be configured):

* `offset`: Offset
* `size`: Number of items per page

Usage example (under default configuration):

```java
Map<String, Object> params = MapUtils.builder()
        .limit(0, 15)                   // Offset 0 items, query 15 items (recommended way)
        .put("offset", 0)               // Equivalent way
        .put("size", 15)                // Equivalent way
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

## Starting Page Number/Offset

The configuration item Starting Page Number/Offset (`bean-searcher.params.pagination.start`) defaults to `0`. In the Page pagination mechanism, a `page` parameter of 0 means querying the first page. When the starting page number is configured to `1`, then a `page` parameter of 1 means querying the first page. The same applies to Offset pagination.

::: warning Attention
* Before **v3.7.0**, the `page(long page, int size)` and `limit(long offset, int size)` methods of the `Parameter Building Tool` are not affected by this configuration.
* In versions **v3.7.0** and later, this configuration also affects the `Parameter Building Tool`.
:::

## Maximum Number of Query Items

The configuration item Maximum Number of Query Items (`bean-searcher.params.pagination.max-allowed-size`) defaults to `100`. It can control some malicious queries. For example, when a hacker wants to query **100 million** items at once to crash our system, Bean Searcher will automatically reduce it to `100`.

## Default Pagination Size

The configuration item Default Pagination Size (`bean-searcher.params.pagination.default-size`) defaults to `15`. When the user does not add pagination parameters, the default number of items per page is 15.

::: tip
The `searchAll(...)` method of the `Searcher` instance is not affected by pagination parameters.
:::
