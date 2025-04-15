# Field Parameters

Field parameters are a series of parameters derived from the [conditional attributes](/en/guide/bean/fields.html) that have database field mappings in the retrieval entity class. They play a role in **filtering** the query results.

> Conditional attributes include two types: [field attributes](/en/guide/bean/fields.html#Field Attributes) and [additional attributes](/en/guide/bean/fields.html#Additional Attributes - Since v4.1.0);
> For whether the field parameters specifically generate a `where` condition or a `having` condition, please refer to the [Conditional Attributes > Where or Having](/en/guide/bean/fields.html#Where or Having) section.

## Derived Field Parameters

### Derivation Rules

For example, for the following entity class:

```java
public class User {
    private String name;
    // Omit other..
}
```

Taking its `name` field as an example, the following series of field parameters can be derived:

* **`name-{n}`**: The nth parameter value of the `name` field, such as `name-0`, `name-1`, `name-2`, etc. (If you can't understand, [refer here](/en/guide/start/use#_11-Field Filtering - field-op-bt))
* **`name`**: Equivalent to `name-0`, the 0th parameter value of the `name` field
* **`name-op`**: The [field operator](#Field Operators) of `name`, such as `Equal`, `GreaterEqual`, `GreaterThan`, etc.
* **`name-ic`**: Whether the case should be ignored when retrieving the `name` field

When deriving field parameters above, the hyphen (`-`) is used as a connector. If you prefer the underscore (`_`), you can configure `bean-searcher.params.separator` to the underscore. After configuring it to the underscore, the derived parameters will be `name_{n}`, `name`, `name_op`, `name_ic`. Similarly, you can also customize the `op` and `ic` suffixes.

::: tip
Field parameters are derived from the **JAVA field names** (not table fields) in the entity class and are decoupled from the database table fields.
:::

### Configurable Items

In a SpringBoot / Grails project, if the `bean-searcher-boot-starter` dependency is used, you can customize the field parameters through the following configuration items in the `application.properties` or `application.yml` file of the project:

Configuration Key Name | Meaning | Optional Values | Default Value
-|-|-|-
`bean-searcher.params.separator` | Separator for field parameter names | `String` | `-`
`bean-searcher.params.operator-key` | Suffix for the field operator parameter name | `String` | `op`
`bean-searcher.params.ignore-case-key` | Suffix for the field parameter name indicating whether to ignore case | `String` | `ic`

### Extended Derived Parameters

In addition, if you find the default derived field parameters not very useful, you can use the [parameter filter](/en/guide/advance/filter#Parameter Filter) to customize new rules. In fact, since `v4.3.0`, Bean Searcher has come with some filters for simplifying derived parameters:

* [ArrayValueParamFilter](/en/guide/advance/filter#ArrayValueParamFilter)
* [SuffixOpParamFilter](/en/guide/advance/filter#SuffixOpParamFilter)
* [JsonArrayParamFilter](/en/guide/advance/filter#JsonArrayParamFilter)

## Field Operators

Field operators are used to describe the retrieval method of a certain field, that is, the SQL splicing method. Bean Searcher provides a total of **22** different field operators by default, as shown in the following table:

> The meaning of `Ignore empty values` in the following table is: if the parameter value of this field is `null` or an `empty string`, whether to ignore this condition.

Operator | Abbreviation | SQL Snippet | Whether to Ignore Empty Values | Meaning
-|-|-|-|-
`Equal` | `eq` | `x = ?` | Yes | Equal to (the default operator)
`NotEqual` | `ne` | `x != ?` | Yes | Not equal to
`GreaterThan` | `gt` | `x > ?` | Yes | Greater than
`GreaterEqual` | `ge` | `x >= ?` | Yes | Greater than or equal to
`LessThan` | `lt` | `x < ?` | Yes | Less than
`LessEqual` | `le` | `x <= ?` | Yes | Less than or equal to
`Between` | `bt` | `x between ?1 and ?2` / `x >= ?1` / `x <= ?2` | Yes | Between... (range query)
`NotBetween` | `nb` | `x not between ?1 and ?2` / `x < ?1` / `x > ?2` | Yes | Not between... (range query) (**since v3.3**)
`Contain` | `ct` | `x like '%?%'` | Yes | Contain (fuzzy query) (**since v3.2**)
`StartWith` | `sw` | `x like '?%'` | Yes | Start with... (fuzzy query)
`EndWith` | `ew` | `x like '%?'` | Yes | End with... (fuzzy query)
`OrLike` | `ol` | `x like ?1 or x like ?2 or ...` | Yes | Fuzzy or match (can have multiple parameter values) (**since v3.7**)
`NotLike` | `nk` | `x not like ?` | Yes | Anti-fuzzy match (**since v3.8**)
`InList`  | `il` | `x in (?, ?, ...)` | Yes | Multi-value query (**New since v3.3**, previously `MultiValue` / `mv`)
`NotIn` | `ni` | `x not in (?, ?, ...)` | Yes | Multi-value query (**since v3.3**)
`IsNull` | `nl` | `x is null` | No | Is null (**since v3.3**)
`NotNull` | `nn` | `x is not null` | No | Is not null (**since v3.3**)
`Empty` | `ey` | `x is null or x = ''` | No | Is empty (only applicable to fields of **string** type)
`NotEmpty` | `ny` | `x is not null and x != ''` | No | Is not empty (only applicable to fields of **string** type)
`AlwaysTrue` | `at` | `1` | No | Always true (**since v4.3**)
`AlwaysFalse` | `af` | `0` | No | Always false (**since v4.3**)
`SqlCond` | `sql` | `Custom SQL condition` | No | Can only be used in the parameter builder. Refer to the [Custom SQL Condition](/en/guide/param/sql) section (**since v3.8.0**)

::: tip In addition
You can also customize operators. Refer to the [Advanced > Mastering Operators](/en/guide/advance/fieldop) section.
:::

Since Bean Searcher provides full names and abbreviations for operators, there are several equivalent usages for each operator.

For example, to query users whose `name` is equal to Jack:

* Backend parameter construction:

```java
Map<String, Object> params = MapUtils.builder()
        .field("name", "Jack")          // (1) The value of the field `name` is Jack
        .field(User::getName, "Jack")   // Equivalent to (1) 
        .op("eq")                       // (2) Specify the operator of the `name` field as `eq` (the default is `eq`, so it can also be omitted)
        .op("Equal")                    // Equivalent to (2) 
        .op(FieldOps.Equal)             // Equivalent to (2) 
        .op(Equal.class)                // Equivalent to (2) 
        .build();
User jack = searcher.searchFirst(User.class, params);           // Execute the query
```

* Front-end parameter passing form:

```js
GET /users ? name=Jack & name-op=eq        // (1) The value of the field `name` is Jack, and the operator is `eq`
GET /users ? name=Jack & name-op=Equal     // Equivalent to (1) 
GET /users ? name-0=Jack & name-op=eq      // Equivalent to (1) 
GET /users ? name-0=Jack & name-op=Equal   // Equivalent to (1) 
GET /users ? name=Jack          // (2) When there is no operator constraint for `name`, or the first operator constraint is `Equal`, it is equivalent to (1) 
GET /users ? name-0=Jack        // Equivalent to (2) 
```
