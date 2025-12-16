# Core Annotations

This document explains how to use Bean Searcher's annotation system to map Java classes to database queries. It covers the core annotations `@SearchBean`, `@DbField`, and `@DbIgnore`, along with the types they support and their processing mechanisms.

## Overview

Bean Searcher uses a declarative annotation system to transform ordinary Java classes into searchable entities. The framework analyzes these annotations at runtime to generate corresponding SQL queries, handle field mapping, and manage database interactions.

A SearchBean is any Java class that can be used with Bean Searcher to perform database searches. A class can become a SearchBean either by explicit annotation or through automatic mapping when the `@SearchBean` annotation is omitted.

## @SearchBean

The `@SearchBean` annotation marks a class as retrievable and defines its database mapping configuration. If this annotation is omitted, Bean Searcher will attempt automatic mapping based on the class name and field names.

### Core Attributes

Attribute | Type | Default | Meaning | since
-|-|-|-|-
tables | String | `""` | Database tables and aliases | v1.0
dataSource | String | `""` | Data source identifier | v3.0
where | String | `""` | Static WHERE condition | v3.8
fields | @DbField[] | `""` | [Additional attributes](/en/guide/bean/fields.html#additional-attributes-since-v4-1-0) for dynamic conditions | v4.1
groupBy | String | `""` | GROUP BY clause | v1.0
having | String | `""` | HAVING clause for grouped queries | v3.8
orderBy | String | `""` | Default ORDER BY clause | v3.6
autoMapTo | String | `""` | Default table for field mapping | v3.0
distinct | boolean | `false` | Whether to enable DISTINCT for the result set | v1.0
inheritType | InheritType | `DEFAULT` | Domain inheritance strategy | v3.2
ignoreFields | String[] | `{}` | Fields to exclude from mapping | v3.4
sortType | SortType | `DEFAULT` | Parameter sorting constraints, [Reference](/en/guide/bean/otherform.html#sorting-constraints-since-v3-6-0) | v3.6
timeout | int | `0` | Maximum SQL execution time (seconds), 0 means no limit | v4.0
maxSize | int | `0` | Maximum records per page, 0 means using the [global configuration value](/en/guide/advance/safe.html#risk-control-configuration-items) | v4.5
maxOffset | long | `0` | Maximum pagination depth, 0 means using the [global configuration value](/en/guide/advance/safe.html#risk-control-configuration-items) | v4.5

### Data Table Mapping

The `tables` attribute supports multiple formats:

* Single table: `"users"`
* Table with alias: `"users u"`
* Multiple tables: `"users u, roles r, user_roles ur"`
* Complex joins: `"users u LEFT JOIN roles r ON u.role_id = r.id"`

When `tables` is empty, Bean Searcher uses the configured naming strategy to automatically map the class name to a table name.

### Multi-Table Mapping Strategy

For multi-table configurations, the `autoMapTo` attribute can specify which table unmapped fields should point to, for example:

```java
@SearchBean(
    tables = "users u, roles r", 
    where = "u.role_id = r.id",
    autoMapTo = "u"
)
public class UserWithRole {
    private Long id;         // Automatically mapped to u.id
    private String name;     // Automatically mapped to u.name
    @DbField("r.name")
    private String roleName; // Explicitly mapped to r.name
}
```

## @DbField

The `@DbField` annotation provides fine-grained control over field-to-column mapping and Bean Searcher's retrieval behavior. It can be applied to class fields or declared within `@SearchBean.fields` for dynamic conditions.

### Core Attributes

Attribute | Type | Default | Meaning | since
-|-|-|-|-
value | String | `""` | SQL column expression | v1.0
name | String | `""` | Field parameter name | v4.1
mapTo | String | `""` | Target table/alias | v4.1
conditional | `boolean` | true | Whether the field can be used as a search parameter | v3.0
onlyOn | Class[] | `{}` | Allowed [operators](/en/guide/param/field.html#field-operators) during search | v3.0
alias | String | `""` | SQL column alias | v3.5
type | DbType | `UNKNOWN` | Database column type | v3.8
cluster | Cluster | `AUTO` | Aggregate column marker, [Reference](/en/guide/bean/fields.html#where-or-having) | v4.1

### Attribute Mapping Strategy

This annotation supports multiple mapping modes:

#### Simple Column Mapping

1. Simple column: `@DbField("name")`
2. Specified table: `@DbField("r.name")`
3. Using `mapTo`: `@DbField(value="name", mapTo="r")`

If this attribute is a [field attribute](/en/guide/bean/fields.html#field-attributes) and its Java field name matches the table column name, the `value` attribute does not need to be specified.

4. Column mapping: `@DbField(mapTo="r")`

#### Expression Mapping

Any valid SQL expression can be directly mapped:

1. Column concatenation: `@DbField("CONCAT(u.first_name, ' ', u.last_name)")`
2. Date formatting: `@DbField("date_format(date_created, '%Y-%m-%d')")`
3. Using aggregate functions: `@DbField("avg(u.score)")`
4. Using `case` expression: `@DbField("sum(case when status = 0 then 1 else 0 end)")`

#### Subquery Mapping

Can also map directly to a subquery:

1. Subquery: `@DbField("(SELECT COUNT(*) FROM orders WHERE user_id = u.id)")`

### Conditional Field Control

The `conditional` attribute controls whether a field can be used as a search parameter:

```java
// Specifies that this field cannot generate search conditions, it only carries search results.
@DbField(conditional = false)
private Date createdAt;
```

The `onlyOn` attribute restricts the operators allowed for a conditional field:


```java
// Only allows > and < operators (the first one is the default operator)
@DbField(onlyOn = {GreaterThan.class, LessThan.class})
private Integer age;
```

## @DbIgnore

The `@DbIgnore` annotation excludes certain fields from database mapping. It cannot be applied to the same field with `@DbField`.

Reference: [Field Ignore](/en/guide/bean/fignore.html) section.
