# Constraints and Risk Control

## Retrieval Condition Constraints

From the previous [Field Parameters](/en/guide/param/field) section, we know that Bean Searcher directly supports many retrieval methods for each field in the entity class. However, sometimes we may not need so many, and in some cases, we need to prohibit certain methods. Bean Searcher uses [Operator Constraints](#Operator Constraints) and [Condition Constraints](#Condition Constraints) to achieve this requirement.

### Operator Constraints

For example, if the field `name` only allows **exact matching** and **post-fuzzy matching**, you can use the following annotation on the SearchBean:

```java
public class User {

    @DbField(onlyOn = {Equal.class, StartWith.class})           // v3.3.0+ syntax
    @DbField(onlyOn = {FieldOps.Equal, FieldOps.StartWith})     // Syntax before v3.3.0
    private String name;

    // Omit other fields to reduce length...
}
```

As shown above, through the `onlyOn` attribute of the `@DbField` annotation, it is specified that the `name` field can only be used with **exact matching** and **post-fuzzy matching** methods, and other retrieval methods will be directly ignored.

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
