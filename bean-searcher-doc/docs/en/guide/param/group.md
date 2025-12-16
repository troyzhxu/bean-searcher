# Logical Grouping

> since v3.5

By default, when multiple field parameters are used in a search, the relationship between these parameters is **AND**. So, how can we express **OR** and more complex **logical combinations** between **AND** and **OR**?

![](/group_requirement.png)

Finally, in `v3.5.0`, Bean Searcher introduced the logical grouping feature. Its main ideas are as follows:

* Group field parameters by prefixing them with a **group name** (the default separator between the group name and the field parameter is `.`, and the group name can consist of letters and numbers).
* Use a new parameter, the **logical expression**, to represent the logical relationship between groups (the default parameter name is `gexpr`, which is a shorthand for `Group Expression`).

## Usage Examples

When passing parameters, divide them into groups `A`, `B`, and `C`:

```properties
# Group A: Name is Jack, case-insensitive, and is male
A.name = Jack
A.name-ic = true
A.gender = Male
# Group B: Name is Alice, and is female
B.name = Alice
B.gender = Female
# Group C: Age is greater than or equal to 20
C.age = 20
C.age-op = ge 
```

Then, pass a new parameter, the group expression `gexpr`, to represent the logical relationship between the three groups:

```properties
# Group expression: (A or B) and C
gexpr = (A|B)&C
```

This represents a more complex query condition: (a male with the name Jack (case-insensitive) or a female with the name Alice) and the age is greater than or equal to 20.

### Written as URL Parameters

```txt
?A.name=Jack&A.name-ic=true&A.gender=Male&B.name=Alice&B.gender=Female&C.age=20&C.age-op=ge&gexpr=(A%7CB)%26C
```

::: tip Note
Since `&` and `|` are special characters, the value of the parameter `gexpr` needs to be **URLEncoded** in the URL.
:::

### Using the Parameter Builder

We can also use the parameter builder to express the same logical relationship:

```java
Map<String, Object> params = MapUtils.builder()
        .group("A")             // Start of Group A
        .field(User::getName, "Jack").ic()
        .field(User::getGender, "Male")
        .group("B")             // Start of Group B
        .field(User::getName, "Alice")
        .field(User::getGender, "Female")
        .group("C")             // Start of Group C
        .field(User::getAge, "20").op(GreateEqual.class)
        .groupExpr("(A|B)&C")   // Inter-group logical relationship (group expression)
        .build();
```

Since `v4.3.0`, the parameter builder has added the `and(..)` and `or(..)` methods, which can more conveniently build logical groupings. So, the above code is also equivalent to:

```java
Map<String, Object> params = MapUtils.builder()
        .or(o -> o
            .and(a -> a
                .field(User::getName, "Jack").ic()
                .field(User::getGender, "Male")
            )
            .and(a -> a
                .field(User::getName, "Alice")
                .field(User::getGender, "Female")
            )
        )
        .field(User::getAge, "20").op(GreateEqual.class)
        .build();
// There is no need to call the groupExpr(..) method. The and(..) and or(..) methods will automatically generate group names and group expressions.
```

::: tip Hint
Only [field parameters](/en/guide/param/field) and [custom SQL conditions](/en/guide/param/sql) can be grouped. [Pagination parameters](/en/guide/param/page), [sorting parameters](/en/guide/param/sort), [embedded parameters](/en/guide/param/embed), and [Select parameters](/en/guide/param/select) cannot be grouped.
:::

## Logical Expressions

As seen above, a logical expression is a formula composed of **group names**, **logical operators** (OR `|`, AND `&`), and **parentheses** to represent the logical relationship between groups of field parameters.

It can be very simple or nested multiple layers. For example:

* When it is empty, it means the parameters are not grouped.
* It can consist of a single group name. For example, `A` represents the conditions of Group A.
* `A|B` means A or B.
* `A&B` means A and B.
* `(A&(B|C)|D)&F` is also a valid nested logical expression.
* There can be spaces between group names, logical operators, and parentheses.
* Parts of the expression other than logical operators, parentheses, and spaces will be regarded as group names.
* When the left and right parentheses do not match, it will be regarded as an illegal expression, such as `(A&B`.
* Illegal expressions will output a warning during the search and will be ignored by the search.

::: warning Note
The group expression cannot contain the `$` symbol because it is a built-in group (root group) in the framework. If the expression contains the `$` symbol, it will be regarded as invalid and ignored. Refer to the [Root Parameters](#root-parameters-since-v3-8-0) section.
:::

### Logical Priority

* The priority of the logical operator **AND** is higher than that of **OR**.

For example, `A | B & C` is equivalent to `A | (B & C)`.

### Intelligent Simplification

Bean Searcher also has a built-in optimizer. When your logical expression is redundant and complex, it will automatically optimize it to the simplest form, thereby simplifying the final generated SQL statement.

For example:

Original Expression | Optimized
-|-
`(( A ))` | `A`
`A & A & A` | `A`
`A \| A \| A` | `A`
`A & ( A \| B )` | `A`
`A \| ( A & B )` | `A`
`A \| ( B \| C )` | `A \| B \| C`
`A & ( B & C )` | `A & B & C`
`(A \| B & (( C \| (D \| E))) & D) \| (F)` | `A \| B & D \| F`
`A \| (A \| C) & B & (A \| D)` | `A \| D & C & B`

### Expression Merging (since v4.3.0)

Sometimes, the front-end passes a group expression parameter, but the back-end also uses the `groupExpr(..)` method to specify a new expression. In this case, which expression should be used? Before `v4.3.0`, the expression specified by the back-end would overwrite the expression specified by the front-end.

Since `v4.3.0`, users can choose whether to **overwrite** or **merge** (the default behavior after v4.3.0, see the `bean-searcher.params.group.mergeable` configuration in the [Configuration Items](#configuration-items)) these two expressions.

#### Merging Rules

Merge with an **AND** relationship. For example, if the front-end passes the expression `A|B` and the back-end uses the `groupExpr(..)` method to specify the expression `A|C`, the final expression will be `(A|B)&(A|C)`.

## Root Parameters (since v3.8.0)

When a group expression is specified, all **field parameters** that are not in the groups specified by the expression will be ignored by the searcher. For example:

```properties
# Group A
A.name = Jack
# Field parameter outside the group, will be ignored when gexpr is non-empty and valid
age = 20
# Group expression
gexpr = A
```

### Frontend Grouping Parameters, Backend Additional Parameters

However, sometimes the group expression `gexpr` needs to be specified by the front-end, and the back-end also needs to inject some parameters that **cannot be ignored**. What should be done in this case? Just inject an additional root group (represented by `$`) parameter:

```properties
# Field parameter outside the group, valid when gexpr is empty or invalid
age = 20
# Root group parameter, valid when gexpr is non-empty and valid
$.age = 20
```

Group `$` is a built-in group in the framework, and its relationship with `gexpr` is always `AND`.

To ensure that a field parameter is not ignored, we must inject two parameters into the search parameters (such as `age` and `$.age` above), which is a bit cumbersome. Therefore, in `v3.8.0`, the parameter builder was enhanced so that its `field(..)` method will automatically add the corresponding root parameter before a group is **explicitly specified**. For example:

```java
Map<String, Object> params = MapUtils.builder()
        // Call the field method before explicitly specifying a group
        .field(User::getAge, 20) 
        // Equivalent to the following two lines of code:
        //   .put("age", 20) 
        //   .put("$.age", 20) 
        .group("A")
        // Call the field method after explicitly specifying a group
        .field(User::getName, "Jack") 
        // Only equivalent to:
        //   .put("A.name", "Jack")
        .build()
```

So, when the back-end needs to manually add search conditions, we recommend using the parameter builder.

### Frontend Regular Parameters, Backend Grouping Additional Parameters

There are also times when the frontend does not use grouping and passes parameters normally, but the backend needs to add additional conditions that are complex and require the use of logical grouping functionality. In this case, because the backend uses grouping while the frontend parameters are not grouped, if not handled specially, the frontend parameters will be forced to **remain outside the groups** and thus be ignored by the searcher.

Therefore, if the backend determines after consideration that **this search api requires frontend parameters**, it can use the parameter builder's `groupRoot()` method to add the frontend's regular parameters to the root group before adding additional grouped conditions. For example:

```java
Map<String, Object> params = MapUtils.builder(..)
        // Add the frontend parameters to the root group
        .groupRoot()
        // Continue adding additional grouped conditions
        .or(o -> o
            .field(User::getAge, 20, 30).op(Between.class)
            .field(User::getGender, "Male")
        )
        .build()
```

## Configuration Items

When using the `bean-searcher-boot-starter` dependency, we can use the following configuration keys to customize:

Configuration Key Name | Meaning | Optional Values | Default Value
-|-|-|-
`bean-searcher.params.group.enable` | Whether to enable the logical grouping feature | `Boolean` | `true`
`bean-searcher.params.group.expr-name` | Name of the logical expression parameter | `String` | `gexpr`
`bean-searcher.params.group.max-expr-length ` | Maximum length of the group expression | `Integer` | `50`
`bean-searcher.params.group.cache-size` | Cache size for expression parsing (number) | `Integer` | `50`
`bean-searcher.params.group.separator` | Separator for group names | `String` | `.`
`bean-searcher.params.group.mergeable` | Whether the group expressions can be merged | `Boolean` | `true`
