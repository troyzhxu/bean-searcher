# 用法示例

本章通过几个完整示例，展示标签系统在不同场景下的典型用法。

## 示例一：微服务跨库翻译

**场景**：`order` 表与 `user` 表在不同的数据库（微服务架构），无法直接联表，需要通过 RPC 调用翻译买家名称。

### 定义 SearchBean

```java
@SearchBean(tables = "order", autoMapTo = "o")
public class OrderVO {

    private Long id;

    private Long buyerId;           // 买家 ID

    @LabelFor("buyerId")            // 声明为 buyerId 的标签，key 默认为字段名 "buyerName"
    private String buyerName;       // 买家姓名，由标签系统自动填充

    private Long sellerId;

    @LabelFor("sellerId")           // 声明为 sellerId 的标签，key 默认为 "sellerName"
    private String sellerName;      // 卖家姓名

    // 省略 Getter / Setter
}
```

### 实现 LabelLoader

```java
@Component
public class UserLabelLoader implements LabelLoader<Long> {

    @Autowired
    private UserRpcService userRpcService;   // 远程用户服务

    @Override
    public boolean supports(String key) {
        // 处理所有以 "Name" 结尾的 key，buyerName 和 sellerName 都满足
        return key != null && key.endsWith("Name");
    }

    @Override
    public List<Label<Long>> load(String key, List<Long> ids) {
        // 批量 RPC 查询，一次调用获取多个用户名
        List<UserDTO> users = userRpcService.batchGetUsers(ids);
        return users.stream()
                .map(u -> new Label<>(u.getId(), u.getName()))
                .collect(Collectors.toList());
    }
}
```

### 检索调用

```java
// 和普通检索完全一样，无需任何额外操作
SearchResult<OrderVO> result = beanSearcher.search(OrderVO.class, paraMap);
// result 中的每条 OrderVO，其 buyerName 和 sellerName 已被自动填充
```

---

## 示例二：字典表翻译（内存缓存）

**场景**：系统有一个 `sys_dict` 字典表，数据量小，可以缓存在内存中，不希望每次查询都联表。

### 定义 SearchBean

```java
@SearchBean(tables = "product")
public class ProductVO {

    private Long id;
    private String name;

    private Integer categoryCode;

    @LabelFor(value = "categoryCode", key = "category")
    private String categoryName;    // 商品类目名称

    private Integer brandCode;

    @LabelFor(value = "brandCode", key = "brand")
    private String brandName;       // 品牌名称

    // 省略 Getter / Setter
}
```

### 实现 LabelLoader

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
        // 从缓存中批量获取字典项
        List<DictItem> items = dictService.findByGroupAndCodes(key, codes);
        return items.stream()
                .map(d -> new Label<>(d.getCode(), d.getName()))
                .collect(Collectors.toList());
    }
}
```

---

## 示例三：枚举字段翻译

**场景**：SearchBean 中包含枚举类型字段，需要将枚举值翻译为用户可读的文本。

### 定义枚举

```java
public enum OrderStatus {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    DONE(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String label;

    OrderStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getLabel() { return label; }
}
```

### 定义 SearchBean

```java
@SearchBean(tables = "order")
public class OrderVO {

    private Long id;

    private OrderStatus status;     // 枚举字段，Bean Searcher 会自动将数据库值转换为枚举实例

    @LabelFor("status")             // key 默认为字段名 "statusLabel"
    private String statusLabel;     // 由 EnumLabelLoader 自动填充

    // 省略 Getter / Setter
}
```

### 注册 EnumLabelLoader

```java
@Bean
public EnumLabelLoader enumLabelLoader() {
    return new EnumLabelLoader()
            .with(OrderStatus.class, OrderStatus::getLabel);
}
```

### 检索结果

执行检索时，`statusLabel` 字段会自动被填充：

| id  | status    | statusLabel |
|-----|-----------|-------------|
| 1   | PAID      | 已支付       |
| 2   | SHIPPED   | 已发货       |
| 3   | DONE      | 已完成       |

---

## 示例四：MapSearcher 结合标签系统

标签系统同样支持 `MapSearcher`（返回 `Map<String, Object>` 的检索器）。在这种情况下，标签字段会以字段名作为 key 写入 Map 中。

```java
// 使用 MapSearcher 检索
SearchResult<Map<String, Object>> result = mapSearcher.search(OrderVO.class, paraMap);

// result 中的 Map 同样包含 buyerName、statusLabel 等标签字段
for (Map<String, Object> row : result.getDataList()) {
    System.out.println(row.get("buyerName"));   // 已被自动填充
    System.out.println(row.get("statusLabel")); // 已被自动填充
}
```

---

## 示例五：多个 LabelLoader 协作

一个系统中可以同时注册多个 `LabelLoader`，框架会根据 `key` 和 `ID 类型` 自动匹配合适的加载器。

```java
@Bean
public LabelLoader<Long> userLabelLoader() {
    return new UserLabelLoader();      // 处理 Long 类型 ID 的用户名翻译
}

@Bean
public LabelLoader<Integer> dictLabelLoader() {
    return new DictLabelLoader();      // 处理 Integer 类型的字典码翻译
}

@Bean
public EnumLabelLoader enumLabelLoader() {
    return new EnumLabelLoader()       // 处理枚举字段翻译
            .with(OrderStatus.class, OrderStatus::getLabel)
            .with(GenderType.class, GenderType::getLabel);
}
```

::: tip 匹配规则
框架按以下两个条件同时匹配 `LabelLoader`：
1. **key 匹配**：`labelLoader.supports(key)` 返回 `true`；
2. **ID 类型匹配**：`LabelLoader<ID>` 的泛型参数 `ID` 类型与被引用字段的类型兼容。

如果没有找到匹配的 `LabelLoader`，框架会抛出 `SearchException` 提示用户添加对应的加载器。
:::
