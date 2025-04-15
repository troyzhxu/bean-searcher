# Embedded Parameters

In addition to implementing various forms of SQL as described above, the retrieval entity class can also embed **dynamic** parameters within the SQL snippets of the annotations `@SearchBean` and `@DbField`.

## Use Cases

* Dynamically specify the table fields for querying or dynamically specify the database table names for querying.
* You want to retrieve data based on a certain table field but don't want to make this table field a field property of the entity class.

## Parameter Types

Two forms of parameters can be embedded within the annotations of the entity class:

* Ordinary embedded parameters in the form of `:name`, which can be used as JDBC parameters ([Ordinary Embedded Parameters](/en/guide/param/embed#Ordinary-Embedded-Parameters)). These parameters have no risk of SQL injection and should be used as the first choice.
* Concatenated parameters in the form of `:name:` ([Concatenated Parameters](/en/guide/param/embed#Concatenated-Parameters)). These parameters will be concatenated within the SQL. Developers should **first check the legitimacy of the parameter values during retrieval to avoid SQL injection vulnerabilities**.

## Embedding into @SearchBean.tables

Example (dynamically retrieve based on a certain field):

```java
@SearchBean(
    tables = "(select id, name from user where age = :age) t"   // The value of the parameter age is dynamically specified during retrieval.
) 
public class User {
    
    @DbField("t.id")
    private long id;

    @DbField("t.name")
    private String name;

}
```

Example (dynamically specify the retrieval table name):

```java
@SearchBean(
    tables = ":table:"      // The parameter table is dynamically specified during retrieval. This is very useful for sharded table retrieval.
) 
public class Order {
    
    @DbField("id")
    private long id;

    @DbField("order_no")
    private String orderNo;

}
```

Refer to the [Scenario > Large Table Scrolling](/en/guide/usage/tables) section.

## Embedding into @SearchBean.where

Example (query only students of a certain age):

```java
@SearchBean(
    tables = "student", 
    where = "age = :age"
) 
public class Student {

    @DbField("s.name")
    private String name;

    // ...
}
```

Example (query only students of specified ages):

```java
@SearchBean(
    tables = "student", 
    where = "age in (:ages:)"    // The parameter ages is in the form of: "18,20,25"
) 
public class Student {

    @DbField("s.name")
    private String name;

    // ...
}
```

## Embedding into @SearchBean.groupBy

Dynamically specify the grouping conditions:

```java
@SearchBean(
    tables = "student", 
    groupBy = ":groupBy:"           // Dynamically specify the grouping conditions
) 
public class StuAge {

    @DbField("avg(age)")
    private int avgAge;

}
```

## Embedding into @DbField

Dynamically specify the retrieval fields

```java
@SearchBean(tables = "sutdent") 
public class StuAge {

    @DbField(":field:")
    private String value;

}
```

Dynamically specify the conditions for a Select subquery

```java
@SearchBean(tables = "student s") 
public class Student {

    @DbField("s.name")
    private String name;

    // Query the score of a certain course (which course is specified by the parameter courseId during retrieval)
    @DbField("select sc.score from student_course sc where sc.student_id = s.id and sc.course_id = :courseId")
    private int score;

    // ...
}
```

::: warning Note
The **attributes** of entity classes with embedded parameters only support participating in filtering conditions and field statistics in versions `v3.4.2+`.
:::

## Prefix Escape (since v3.6.0)

Since Bean Searcher uses `:` as the prefix for embedded parameters by default, any `:` used in the SQL snippets of the `@SearchBean` annotation will be treated as an embedded parameter by Bean Searcher. However, the SQL syntax of some databases does contain the `:` symbol. For example, the json syntax of PostgreSQL:

```sql
select '{"name":"Jack"}'::json->'name'  -- Here, `:json` should not be treated as an embedded parameter.
```

To be compatible with such situations, Bean Searcher has added an escape semantics since v3.6.0:

* **Use `\\:` to represent an original `:` symbol (it will not be treated as the prefix for an embedded parameter).**

For example:

```java
@DbField("data\\:\\:json->'name'")      // The final generated SQL snippet: data::json->'name'
private String name;
```

Refer to: https://github.com/troyzhxu/bean-searcher/issues/30
