# Embedded Parameters

Embedded parameters refer to the parameters embedded within the annotations of entity classes (see the [Entity Class > Embedded Parameters](/en/guide/bean/params) section). They can be divided into ordinary embedded parameters and splicing parameters, which can easily handle various complex SQL retrieval problems.

> [!IMPORTANT] Important Note
> Embedded parameters are not field parameters, so you cannot use the `field(..)` method of the parameter builder to add parameter values to them.

## Ordinary Embedded Parameters

Ordinary embedded parameters are parameters prefixed with a colon (`:`) (in the form of `:name`) and embedded in the SQL fragments of entity class annotations.

For example, consider the following SearchBean:

```java
@SearchBean(where = "age = :age") 
public class Student {
    // Omitted...
}
```

We can retrieve students aged 20 in the following way:

```java
Map<String, Object> params = MapUtils.builder()
        // Note: You cannot use the field method here because the age field is not an attribute of the entity class
        .put("age", 20)         // Specify the value of the embedded parameter age as 20  
        .build();
List<User> users = searcher.searchList(User.class, params);
```

::: tip
Ordinary embedded parameters will ultimately be processed by Bean Searcher as a type of JDBC parameter, so there is no need to worry about SQL injection issues.
:::

## Splicing Parameters

Splicing parameters (since v2.1) are parameters prefixed and suffixed with a colon (`:`) (in the form of `:name:`) and embedded in the SQL fragments of entity class annotations.

Splicing parameters have a wide range of applications: wherever ordinary embedded parameters can be used, splicing parameters can definitely be used, and where ordinary embedded parameters cannot handle the situation, splicing parameters can easily solve the problem. They can achieve **dynamic SQL generation**.

::: tip Special Attention
Splicing parameters will be directly spliced into the SQL. Developers should **first check the legitimacy of the parameter values during retrieval to avoid SQL injection vulnerabilities**. If a requirement can be solved using both ordinary embedded parameters and splicing parameters, we recommend using ordinary embedded parameters to implement it.
::: 

* Reference: [Entity Class > Embedded Parameters](/en/guide/bean/params.html) section;
<!-- * Reference: [Practice > Dynamic Retrieval > Sharded Table Retrieval](/simples.html#Sharded Table Retrieval) case. [TODO] -->

### Collection Parameter Values (since v4.3.0)

Since `v4.3.0`, Bean Searcher supports directly adding collection parameter values to splicing parameters. The framework will automatically connect them into a string separated by commas. For example:

```java
@SearchBean(where = "age in (:ages:)")
public class User {
    // ...
}
```

You can directly add collection parameter values to it:

```java
Map<String, Object> params = MapUtils.builder()
        .put("ages", Arrays.asList(20,30,40))  // Directly use collection parameter values
        .put("ages", new int[] {20,30,40})     // You can also use an array
        .put("ages", "20,30,40")               // Usage before v4.3.0, can only pass a string
        .build();
List<User> users = searcher.searchList(User.class, params);
```
