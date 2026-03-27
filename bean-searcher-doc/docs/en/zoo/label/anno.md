# Label Annotation

## @LabelFor

> since v4.4.0

`@LabelFor` is the core annotation of the label system. It marks a field in a SearchBean as the **label (translation) of another field**, telling the framework to automatically populate this field with a human-readable string.

### Annotation Attributes

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LabelFor {

    /**
     * The name of the referenced field (the field whose value is the ID to translate).
     */
    String value();

    /**
     * The label type KEY used to match a LabelLoader.
     * Defaults to the annotated field's own name when not specified.
     */
    String key() default "";
}
```

::: tip Note
`@LabelFor` implicitly includes `@DbIgnore`, so annotated fields are **not mapped to any database column**. They are purely for holding the translated label text.
:::

### value Attribute

`value` specifies the **name of another field in the same SearchBean** whose value is the ID (or enum, or other key) to be translated.

Example — declare `buyerName` as the label for `buyerId`:

```java
@LabelFor("buyerId")
private String buyerName;
```

This tells the label system: read the value of `buyerId`, pass it to the appropriate `LabelLoader`, and write the returned text back into `buyerName`.

::: tip Cross-level Field References
`value` supports referencing fields declared in parent or subclasses — not only the current class.
:::

### key Attribute

`key` is used to distinguish between multiple `LabelLoader` instances when more than one exists in the application.

- **Not specified** (default `""`): the annotated field's own name is used as the key (e.g., `"buyerName"`).
- **Explicitly specified**: the given value is used as the key for matching.

Example:

```java
@LabelFor(value = "statusCode", key = "orderStatus")
private String statusName;
```

The framework will look for a `LabelLoader` whose `supports("orderStatus")` returns `true`.

### Complete Example

```java
@SearchBean(tables = "order")
public class OrderVO {

    private long id;

    private long buyerId;           // buyer ID from the database

    @LabelFor("buyerId")            // key defaults to "buyerName"
    private String buyerName;       // filled automatically by the label system

    private Integer status;         // status code (dictionary value)

    @LabelFor(value = "status", key = "orderStatus")
    private String statusName;      // filled automatically by the label system

    // getters / setters omitted
}
```
