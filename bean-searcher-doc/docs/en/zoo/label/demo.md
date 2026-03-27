# Usage Examples

This chapter walks through several complete examples that demonstrate the label system in typical scenarios.

## Example 1: Cross-Database Lookup in Microservices

**Scenario**: The `order` and `user` tables are in different databases. A JOIN is not possible, so buyer names must be fetched via RPC.

### SearchBean

```java
@SearchBean(tables = "order", autoMapTo = "o")
public class OrderVO {

    private Long id;
    private Long buyerId;

    @LabelFor("buyerId")            // key defaults to "buyerName"
    private String buyerName;       // auto-filled by the label system

    private Long sellerId;

    @LabelFor("sellerId")           // key defaults to "sellerName"
    private String sellerName;

    // getters / setters omitted
}
```

### LabelLoader

```java
@Component
public class UserLabelLoader implements LabelLoader<Long> {

    @Autowired
    private UserRpcService userRpcService;

    @Override
    public boolean supports(String key) {
        return key != null && key.endsWith("Name");
    }

    @Override
    public List<Label<Long>> load(String key, List<Long> ids) {
        List<UserDTO> users = userRpcService.batchGetUsers(ids);
        return users.stream()
                .map(u -> new Label<>(u.getId(), u.getName()))
                .collect(Collectors.toList());
    }
}
```

### Query

```java
// No extra steps — buyerName and sellerName are automatically populated
SearchResult<OrderVO> result = beanSearcher.search(OrderVO.class, paraMap);
```

---

## Example 2: Dictionary Table (In-Memory Cache)

**Scenario**: A `sys_dict` table is small enough to be cached in memory. We want to avoid JOINs on every query.

### SearchBean

```java
@SearchBean(tables = "product")
public class ProductVO {

    private Long id;
    private String name;

    private Integer categoryCode;

    @LabelFor(value = "categoryCode", key = "category")
    private String categoryName;

    private Integer brandCode;

    @LabelFor(value = "brandCode", key = "brand")
    private String brandName;

    // getters / setters omitted
}
```

### LabelLoader

```java
@Component
public class DictLabelLoader implements LabelLoader<Integer> {

    @Autowired
    private DictService dictService;

    @Override
    public boolean supports(String key) {
        return "category".equals(key) || "brand".equals(key);
    }

    @Override
    public List<Label<Integer>> load(String key, List<Integer> codes) {
        return dictService.findByGroupAndCodes(key, codes).stream()
                .map(d -> new Label<>(d.getCode(), d.getName()))
                .collect(Collectors.toList());
    }
}
```

---

## Example 3: Enum Field Translation

**Scenario**: A SearchBean contains an enum field. The front end needs the display text.

### Enum Definition

```java
public enum OrderStatus {
    PENDING(0, "Pending Payment"),
    PAID(1, "Paid"),
    SHIPPED(2, "Shipped"),
    DONE(3, "Completed"),
    CANCELLED(4, "Cancelled");

    private final int code;
    private final String label;

    OrderStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getLabel() { return label; }
}
```

### SearchBean

```java
@SearchBean(tables = "order")
public class OrderVO {

    private Long id;

    private OrderStatus status;     // enum field — Bean Searcher converts DB value to enum

    @LabelFor("status")             // key defaults to "statusLabel"
    private String statusLabel;     // auto-filled by EnumLabelLoader

    // getters / setters omitted
}
```

### Register EnumLabelLoader

```java
@Bean
public EnumLabelLoader enumLabelLoader() {
    return new EnumLabelLoader()
            .with(OrderStatus.class, OrderStatus::getLabel);
}
```

### Result

| id | status   | statusLabel |
|----|----------|-------------|
| 1  | PAID     | Paid        |
| 2  | SHIPPED  | Shipped     |
| 3  | DONE     | Completed   |

---

## Example 4: MapSearcher with the Label System

The label system also works with `MapSearcher`. Label fields are written into each `Map` using the field name as the key.

```java
SearchResult<Map<String, Object>> result = mapSearcher.search(OrderVO.class, paraMap);

for (Map<String, Object> row : result.getDataList()) {
    System.out.println(row.get("buyerName"));   // auto-populated
    System.out.println(row.get("statusLabel")); // auto-populated
}
```

---

## Example 5: Multiple LabelLoaders Working Together

Multiple `LabelLoader` instances can coexist. The framework matches the correct loader by both **key** and **ID type**.

```java
@Bean
public LabelLoader<Long> userLabelLoader() {
    return new UserLabelLoader();       // handles Long-typed IDs
}

@Bean
public LabelLoader<Integer> dictLabelLoader() {
    return new DictLabelLoader();       // handles Integer-typed dictionary codes
}

@Bean
public EnumLabelLoader enumLabelLoader() {
    return new EnumLabelLoader()        // handles enum fields
            .with(OrderStatus.class, OrderStatus::getLabel)
            .with(GenderType.class, GenderType::getLabel);
}
```

::: tip Matching Rules
The framework matches a `LabelLoader` by two criteria simultaneously:
1. **Key match**: `labelLoader.supports(key)` returns `true`;
2. **ID type match**: the generic parameter `ID` of `LabelLoader<ID>` is assignable from the referenced field's type.

If no matching loader is found, the framework throws a `SearchException` prompting you to add one.
:::
