# Constraints and Risk Control

## Retrieval Condition Constraints

From the previous [Field Parameters](/en/guide/param/field) section, we know that Bean Searcher directly supports many retrieval methods for each field in the entity class. However, sometimes we may not need so many, and in some cases, we need to prohibit certain methods. Bean Searcher uses [Operator Constraints](#Operator Constraints) and [Condition Constraints](#Condition Constraints) to achieve this requirement.

### Operator Constraints

For example, if the field `name` only allows **exact matching** and **suffix matching**, you can use the following annotation on the SearchBean:

```java
public class User {

    @DbField(onlyOn = {Equal.class, StartWith.class})           // v3.3.0+ syntax
    @DbField(onlyOn = {FieldOps.Equal, FieldOps.StartWith})     // Syntax before v3.3.0
    private String name;

    // Omit other fields to reduce length...
}
```

As shown above, through the `onlyOn` attribute of the `@DbField` annotation, it is specified that the `name` field can only be used with **exact matching** and **suffix matching** methods, and other retrieval methods will be directly ignored.

::: tip Default Operator
* If `@DbField.onlyOn` is empty, the **default operator** for this field is **Equal**.
* If `@DbField.onlyOn` is not empty, its **first value** is the default operator for this field.
:::

The above code restricts `name` to only two retrieval methods. If we want to be more strict and **only allow exact matching**, there are actually two ways to write it.

#### (1) Still use operator constraints:

```java
@SearchBean(tables="user u, role r", where="u.role_id = r.id", autoMapTo="u") 
public class User {

    @DbField(onlyOn = Equal.class)
    private String name;

    // Omit other fields to reduce length...
}
```

#### (2) Override the operator parameter in the interface method of the Controller:

```java
@GetMapping("/index")
public SearchResult<Map<String, Object>> index(HttpServletRequest request) {
    Map<String, Object> params = MapUtils.flatBuilder(request.getParameterMap())
        .field(User::getName).op(Equal.class)   // Directly override the operator of the name field to Equal
        .build()
    return mapSearcher.search(User.class, params);
}
```

### Condition Constraints

Sometimes we don't want a certain field to participate in the where condition, we can do this:

```java
public class User {

    @DbField(conditional = false)
    private int age;

    // Omit other fields to reduce length...
}
```

As shown above, through the `conditional` attribute of the `@DbField` annotation, the `age` field is directly not allowed to participate in the condition. No matter how the front end passes parameters, Bean Searcher will ignore it.

#### Configuration method for other projects:

```java
DefaultParamResolver paramResolver = new DefaultParamResolver();
// Add parameter filters
paramResolver.setParamFilters(new ParamFilter[] { 
    new MyParamFilter1(),
    new MyParamFilter2(),
});
// Build a Map searcher
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Omit other configurations
        .paramResolver(paramResolver)   // The BeanSearcher searcher also has the same configuration
        .build();
```

## Risk Control Configuration Items

Bean Searcher provides some risk control items by default and supports configuration.

In SpringBoot / Grails / Solon projects, if the `bean-searcher-boot-starter` or `bean-searcher-solon-plugin` dependency is used, the following configuration items can be used in the project configuration file (e.g., `application.properties`):

Configuration Key Name | Meaning | Optional Values | Default Value | Starting Version
-|-|-|-|-
`bean-searcher.params.pagination.max-allowed-size` | Maximum number of items per page for querying (pagination protection) | `Positive integer` | `100` | v2.0.0
`bean-searcher.params.pagination.max-allowed-offset` | Maximum pagination depth | `Positive integer` | `20000` | v3.8.1
`bean-searcher.params.filter.max-para-map-size` | Maximum allowed number of key-value pairs for retrieval parameters | `Positive integer` | `150` | v3.8.1
`bean-searcher.params.group.max-expr-length` | Maximum length (number of characters) of the logical grouping expression | `Positive integer` | `50` | v3.8.1

### Per-Entity Risk Control Override (since v4.5.0)

The global configuration above applies to all entity classes. However, certain special scenarios may need to relax or tighten these limits — for example, a data export scenario may need to allow a larger page size and a deeper pagination offset.

Since `v4.5.0`, you can use the `maxSize` and `maxOffset` attributes of the `@SearchBean` annotation to set risk control values for individual entity classes, **overriding the global configuration**:

```java
@SearchBean(
    tables = "order",
    maxSize = 2000,             // Allow up to 2000 items per page (overrides global 100)
    maxOffset = Long.MAX_VALUE  // No pagination depth limit (overrides global 20000)
)
public class OrderExportVO {
    // ...
}
```

::: tip Typical Use Case
**Data export** usually requires fetching all data in batches of `batchSize` items (default 1000). As pages turn, the offset keeps growing, so the global risk control limit must be lifted. It is recommended to define a dedicated SearchBean for export scenarios rather than sharing one with normal retrieval interfaces, to avoid security vulnerabilities.
:::

Both attributes default to `0`, which means use the global configuration value.
