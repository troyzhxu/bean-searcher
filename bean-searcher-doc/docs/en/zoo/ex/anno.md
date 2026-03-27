# Export Annotation

## @Export

> since v4.5.0

`@Export` is the core annotation of the export system. It marks a field in a SearchBean as **eligible for export** and configures how that field appears in the output file. **Only fields annotated with `@Export`** will be included in the export.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Export {

    /** Column header text in the output file (required) */
    String name();

    /** Column order — smaller values appear first; default 0 */
    int idx() default 0;

    /** Value transformation expression (supports SpEL / SnEL) */
    String expr() default "";

    /** Format pattern (date / number / string) */
    String format() default "";

    /** Export condition expression (since v4.8.0, supports SpEL / SnEL) */
    String onlyIf() default "";
}
```

### name — Column Header

`name` is **required**. It specifies the column header text displayed in the export file (e.g., the first row of a CSV):

```java
@Export(name = "Order ID")
private Long id;

@Export(name = "Buyer Name")
private String buyerName;
```

### idx — Column Order

`idx` controls the column order in the export. **Smaller values appear first**; the default is `0`. When multiple fields share the same `idx`, they are ordered by their declaration order in the class (subclass fields before superclass fields):

```java
@Export(name = "Order ID", idx = 1)
private Long id;

@Export(name = "Created At", idx = 10)
private LocalDateTime createTime;

@Export(name = "Buyer Name", idx = 5)
private String buyerName;
// Export order: Order ID(1) → Buyer Name(5) → Created At(10)
```

### expr — Value Transformation Expression

`expr` allows you to compute or transform a field's value before export. Supported references:

- **`@`** — refers to the current field's value;
- **field name** — refers to another field of the same object;
- **Spring environment** — full SpEL (Spring Expression Language) syntax;
- **Solon environment** — SnEL syntax.

Examples:

```java
// Append a unit to the current field value
@Export(name = "Amount", expr = "@ + ' USD'")
private BigDecimal amount;

// Reference another field (compute subtotal from price × quantity)
@Export(name = "Subtotal", expr = "price * quantity")
private transient String subTotal;   // virtual field, transient to skip DB mapping

// SpEL ternary expression
@Export(name = "Completed", expr = "status == 'DONE' ? 'Yes' : 'No'")
private String status;
```

::: tip Virtual Fields
For a computed column that has no corresponding database column, declare the field as `transient` (so Bean Searcher ignores it for SQL) and use `expr` to calculate its value at export time.
:::

### format — Format Pattern

`format` formats the field value (or the result of `expr`) as a string. The built-in `Formatter.DEFAULT` supports:

| Value Type | Format Example | Output |
|-----------|----------------|--------|
| `Date` / `LocalDateTime` etc. | `yyyy-MM-dd HH:mm:ss` | `2024-06-01 12:00:00` |
| `Number` and subclasses | `#,##0.00` | `1,234.56` |
| Other | `%s (USD)` | `value (USD)` |

```java
@Export(name = "Order Date", format = "yyyy-MM-dd")
private LocalDate orderDate;

@Export(name = "Amount (USD)", format = "#,##0.00")
private BigDecimal amount;
```

::: tip Using expr and format Together
When both are set, `expr` is evaluated first, then `format` is applied to the result.
:::

### onlyIf — Export Condition (since v4.8.0)

`onlyIf` dynamically controls whether a field is included in a specific export request, based on the search parameters (`paraMap`):

- Use **parameter names** to reference values in `paraMap` directly;
- **Spring environment**: SpEL syntax;
- **Solon environment**: SnEL syntax.

```java
// Include the phone field only when the caller passes showPhone=true
@Export(name = "Phone", onlyIf = "showPhone")
private String phone;

// SpEL: include only when exportType equals 'full'
@Export(name = "Remarks", onlyIf = "exportType == 'full'")
private String remark;
```

When `onlyIf` is empty (the default), the field is always exported.

### Rate-Limit Configuration

Bean Searcher has a built-in **pagination guard** that defaults to:

- Maximum records per query (`maxAllowedSize`): **100**
- Maximum allowed offset (`maxAllowedOffset`): **20000**

Because `BeanExporter` queries data in batches of `batchSize` (default **1000**) and the offset grows as pagination advances, **a SearchBean used for export must relax these limits via `@SearchBean`'s `maxSize` and `maxOffset` attributes**, otherwise Bean Searcher will throw an `IllegalParamException`.

```java{1}
@SearchBean(
    tables = "order",
    maxSize = 2000,             // must be >= batchSize
    maxOffset = Long.MAX_VALUE  // allow full-table export
)
public class OrderExportVO {
    // ...
}
```

::: tip Why is maxOffset needed?
`BeanExporter` pages through data by incrementing the page number (`page(0, size)` → `page(1, size)` → ...).  
Without a higher `maxOffset`, once the accumulated offset exceeds 20,000 rows Bean Searcher will reject the request.
:::

::: warning Use a Dedicated Export SearchBean
Define a separate SearchBean for export (e.g. `OrderExportVO`) rather than sharing one with your normal search API. The relaxed limits are appropriate for controlled server-side exports but should not be exposed to ordinary user-driven searches.
:::

### Complete Example

```java
@SearchBean(
    tables = "order o, user u",
    where = "o.buyer_id = u.id",
    autoMapTo = "o",
    maxSize = 2000,             // relax per-batch query limit
    maxOffset = Long.MAX_VALUE  // relax pagination-depth limit
)
public class OrderExportVO {

    @Export(name = "Order No.", idx = 1)
    private String orderNo;

    @Export(name = "Buyer Name", idx = 2)
    @DbField("u.name")
    private String buyerName;

    @Export(name = "Order Time", idx = 3, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Export(name = "Amount (USD)", idx = 4, format = "#,##0.00")
    private BigDecimal amount;

    @Export(name = "Status", idx = 5, expr = "status == 1 ? 'Paid' : status == 2 ? 'Shipped' : 'Other'")
    private Integer status;

    @Export(name = "Phone", idx = 6, onlyIf = "showPhone")
    private String buyerPhone;

    // getters / setters omitted
}
```
