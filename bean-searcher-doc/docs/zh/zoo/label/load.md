# 标签加载器

## LabelLoader

> since v4.4.0

`LabelLoader<ID>` 是标签系统的核心接口，负责根据 ID 列表**批量加载**标签文本。泛型参数 `ID` 表示被翻译字段（即 `@LabelFor.value` 所引用字段）的类型。

```java
public interface LabelLoader<ID> {

    /**
     * 判断该加载器是否支持处理指定的标签 KEY
     * @param key 标签 KEY（来自 @LabelFor.key，未指定时为字段名）
     * @return 是否支持
     */
    boolean supports(String key);

    /**
     * 批量加载标签
     * @param key  标签 KEY
     * @param ids  ID 列表（当前批次数据中该字段的所有去重值）
     * @return 标签列表，每个 Label 包含 id 和 label 文本
     */
    List<Label<ID>> load(String key, List<ID> ids);

}
```

::: tip 批量加载，性能优先
框架会先收集当前结果集中所有记录的 ID（自动去重），然后一次性调用 `load()` 方法批量获取标签，不会对每条记录单独查询，从而大幅降低外部调用次数。
:::

## 自定义 LabelLoader

### 基本实现

假设有一个买家 ID → 名称的翻译需求，可以实现一个 `LabelLoader<Long>`：

```java
@Component
public class BuyerLabelLoader implements LabelLoader<Long> {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(String key) {
        // 仅处理 key 为 "buyerName" 的标签字段（即字段名未指定 key 时，默认 key = 字段名）
        return "buyerName".equals(key);
    }

    @Override
    public List<Label<Long>> load(String key, List<Long> ids) {
        // 批量查询用户名
        List<User> users = userService.findAllById(ids);
        return users.stream()
                .map(u -> new Label<>(u.getId(), u.getName()))
                .collect(Collectors.toList());
    }

}
```

在 SpringBoot / Solon 项目中，将 `LabelLoader` 声明为 Bean 即可，**框架会自动收集并注入**。

### 支持多个 key

如果一个加载器需要处理多种 key，可以使用 `Set` 匹配：

```java
@Component
public class DictLabelLoader implements LabelLoader<Integer> {

    private static final Set<String> SUPPORTED_KEYS = Set.of("statusName", "typeName");

    @Override
    public boolean supports(String key) {
        return SUPPORTED_KEYS.contains(key);
    }

    @Override
    public List<Label<Integer>> load(String key, List<Integer> codes) {
        // 从字典表批量加载
        return dictService.findLabels(key, codes).stream()
                .map(d -> new Label<>(d.getCode(), d.getName()))
                .collect(Collectors.toList());
    }

}
```

### 使用 key 前缀匹配

如果希望一个加载器处理某一类前缀的 key，可以用 `startsWith` 做判断：

```java
@Component
public class RemoteLabelLoader implements LabelLoader<Long> {

    @Override
    public boolean supports(String key) {
        // 处理所有以 "remote." 开头的标签
        return key != null && key.startsWith("remote.");
    }

    @Override
    public List<Label<Long>> load(String key, List<Long> ids) {
        // 调用远程服务加载标签
        String serviceName = key.substring("remote.".length());
        return remoteService.loadLabels(serviceName, ids).stream()
                .map(r -> new Label<>(r.getId(), r.getName()))
                .collect(Collectors.toList());
    }

}
```

## EnumLabelLoader

> since v4.4.0

`EnumLabelLoader` 是框架内置的**枚举类型标签加载器**，专门用于将枚举字段翻译为字符串。

### 创建与注册

```java
@Bean
public EnumLabelLoader enumLabelLoader() {
    return new EnumLabelLoader()
            .with(OrderStatus.class, OrderStatus::getLabel)   // 注册订单状态枚举的标签函数
            .with(GenderType.class, g -> g.name().toLowerCase()); // 注册性别枚举的标签函数
}
```

其中 `OrderStatus::getLabel` 是一个 `Function<OrderStatus, String>`，用于从枚举实例中提取标签文本。

### keyPrefix 前缀过滤

`EnumLabelLoader` 支持通过构造函数传入 `keyPrefix`，使其只处理特定前缀的标签 key：

```java
// 只处理 key 以 "enum." 开头的标签字段
new EnumLabelLoader("enum.")
```

不传参时默认 `keyPrefix = ""`，即匹配所有 key。

### 配合 @LabelFor 使用

假设 SearchBean 中有一个枚举字段 `status`：

```java
@SearchBean(tables = "order", autoMapTo = "o")
public class OrderVO {

    private long id;

    private OrderStatus status;       // 枚举字段，来自数据库

    @LabelFor("status")               // key 默认为 "statusLabel"
    private String statusLabel;       // 由 EnumLabelLoader 自动填充

    // 省略 Getter / Setter
}
```

枚举定义：

```java
public enum OrderStatus {
    PENDING("待支付"),
    PAID("已支付"),
    SHIPPED("已发货"),
    DONE("已完成");

    private final String label;
    OrderStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
```

注册 `EnumLabelLoader`：

```java
@Bean
public EnumLabelLoader enumLabelLoader() {
    return new EnumLabelLoader().with(OrderStatus.class, OrderStatus::getLabel);
}
```

这样，每次检索 `OrderVO` 时，`statusLabel` 会自动被填充为如 `"已支付"` 这样的文本。

## 配置方式

### SpringBoot / Grails 项目

引入 `bean-searcher-label` 依赖后，框架会自动扫描容器中所有 `LabelLoader<?>` 类型的 Bean，并注册到 `LabelResultFilter` 中，无需额外配置：

```java
@Bean
public LabelLoader<Long> buyerLabelLoader() {
    return new BuyerLabelLoader();
}
```

若需要自定义 `LabelResultFilter` 本身（例如需要手动控制加载器列表），只需声明一个 `LabelResultFilter` 类型的 Bean 即可：

```java
@Bean
public LabelResultFilter labelResultFilter() {
    LabelResultFilter filter = new LabelResultFilter();
    filter.addLabelLoader(new BuyerLabelLoader());
    filter.addLabelLoader(new DictLabelLoader());
    return filter;
}
```

### Solon 项目

与 SpringBoot 相同，将 `LabelLoader` 声明为 Bean 即可自动注入。

### 非 Boot 的 Spring 项目

```xml
<bean id="labelResultFilter" class="cn.zhxu.bs.label.LabelResultFilter">
    <constructor-arg>
        <list>
            <bean class="com.example.BuyerLabelLoader" />
            <bean class="com.example.DictLabelLoader" />
        </list>
    </constructor-arg>
</bean>
```

### Others（手动配置）

```java
LabelResultFilter labelFilter = new LabelResultFilter();
labelFilter.addLabelLoader(new BuyerLabelLoader());
labelFilter.addLabelLoader(new DictLabelLoader());

MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .addResultFilter(labelFilter)
        .build();
```

## LabelResultFilter

`LabelResultFilter` 是标签系统的核心实现类，它实现了 `ResultFilter` 接口。其工作流程如下：

1. 对结果集中每条记录，读取所有被 `@LabelFor` 标注字段所引用的 ID 字段的值；
2. 按标签 key + ID 类型分组，批量收集不重复的 ID；
3. 根据 key 找到匹配的 `LabelLoader` 并批量加载标签；
4. 将加载到的标签文本回写到对应的标签字段中。

::: tip 缓存机制
`LabelResultFilter` 内部使用 `ConcurrentHashMap` 缓存每个 `beanClass` 对应的标签字段解析结果，避免每次检索都重复反射解析。如果在应用运行期间动态修改了 SearchBean 的字段定义，可调用 `clearCache()` 方法手动清除缓存。
:::

### 管理 LabelLoader

```java
LabelResultFilter filter = ...;

filter.addLabelLoader(loader);      // 追加加载器
filter.removeLabelLoader(loader);   // 移除加载器
filter.clearLabelLoaders();         // 清除所有加载器
filter.clearCache();                // 清除字段解析缓存
```
