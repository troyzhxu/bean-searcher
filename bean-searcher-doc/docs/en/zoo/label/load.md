# Label Loader

## LabelLoader

> since v4.4.0

`LabelLoader<ID>` is the core interface of the label system. It is responsible for **batch-loading** label texts given a list of IDs. The generic type `ID` represents the type of the referenced field (i.e., the field named in `@LabelFor.value`).

```java
public interface LabelLoader<ID> {

    /**
     * Indicates whether this loader handles the given label key.
     * @param key the label key (from @LabelFor.key, or the field name if unspecified)
     */
    boolean supports(String key);

    /**
     * Loads labels for the given IDs in batch.
     * @param key  the label key
     * @param ids  deduplicated list of IDs collected from the current result set
     * @return a list of Label objects (each containing an id and a label text)
     */
    List<Label<ID>> load(String key, List<ID> ids);
}
```

::: tip Batch Loading for Performance
The framework collects and deduplicates all ID values from the current result set first, then calls `load()` once per label key — it never queries label data record by record.
:::

## Custom LabelLoader

### Basic Implementation

```java
@Component
public class BuyerLabelLoader implements LabelLoader<Long> {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(String key) {
        return "buyerName".equals(key);
    }

    @Override
    public List<Label<Long>> load(String key, List<Long> ids) {
        List<User> users = userService.findAllById(ids);
        return users.stream()
                .map(u -> new Label<>(u.getId(), u.getName()))
                .collect(Collectors.toList());
    }
}
```

In SpringBoot / Solon projects, simply declare the `LabelLoader` as a Bean — the framework will **auto-discover and inject** it.

### Supporting Multiple Keys

```java
@Component
public class DictLabelLoader implements LabelLoader<Integer> {

    private static final Set<String> SUPPORTED = Set.of("statusName", "typeName");

    @Override
    public boolean supports(String key) {
        return SUPPORTED.contains(key);
    }

    @Override
    public List<Label<Integer>> load(String key, List<Integer> codes) {
        return dictService.findLabels(key, codes).stream()
                .map(d -> new Label<>(d.getCode(), d.getName()))
                .collect(Collectors.toList());
    }
}
```

### Key Prefix Matching

```java
@Component
public class RemoteLabelLoader implements LabelLoader<Long> {

    @Override
    public boolean supports(String key) {
        return key != null && key.startsWith("remote.");
    }

    @Override
    public List<Label<Long>> load(String key, List<Long> ids) {
        String serviceName = key.substring("remote.".length());
        return remoteService.loadLabels(serviceName, ids).stream()
                .map(r -> new Label<>(r.getId(), r.getName()))
                .collect(Collectors.toList());
    }
}
```

## EnumLabelLoader

> since v4.4.0

`EnumLabelLoader` is a built-in label loader that translates **enum field values** into human-readable strings.

### Creating and Registering

```java
@Bean
public EnumLabelLoader enumLabelLoader() {
    return new EnumLabelLoader()
            .with(OrderStatus.class, OrderStatus::getLabel)
            .with(GenderType.class, g -> g.name().toLowerCase());
}
```

The second argument of `with(...)` is a `Function<T, String>` that extracts the display text from an enum instance.

### keyPrefix Filtering

The constructor accepts an optional `keyPrefix` to restrict which keys this loader handles:

```java
// only handle keys starting with "enum."
new EnumLabelLoader("enum.")
```

When no prefix is given, the default is `""` (matches all keys).

### Usage with @LabelFor

```java
@SearchBean(tables = "order")
public class OrderVO {

    private long id;

    private OrderStatus status;     // enum field from the database

    @LabelFor("status")             // key defaults to "statusLabel"
    private String statusLabel;     // filled automatically by EnumLabelLoader

    // getters / setters omitted
}
```

---

## Configuration

### SpringBoot / Grails Projects

After adding the `bean-searcher-label` dependency, the framework automatically collects all `LabelLoader<?>` Beans and injects them into `LabelResultFilter`. No additional configuration is needed:

```java
@Bean
public LabelLoader<Long> userLabelLoader() {
    return new UserLabelLoader();
}
```

To customize the `LabelResultFilter` itself, declare a Bean of that type:

```java
@Bean
public LabelResultFilter labelResultFilter() {
    LabelResultFilter filter = new LabelResultFilter();
    filter.addLabelLoader(new BuyerLabelLoader());
    filter.addLabelLoader(new DictLabelLoader());
    return filter;
}
```

### Solon Projects

Same as SpringBoot — just declare the `LabelLoader` as a Bean.

### Plain Spring (non-Boot)

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

### Others (Manual Configuration)

```java
LabelResultFilter labelFilter = new LabelResultFilter();
labelFilter.addLabelLoader(new BuyerLabelLoader());
labelFilter.addLabelLoader(new DictLabelLoader());

MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .addResultFilter(labelFilter)
        .build();
```

---

## LabelResultFilter

`LabelResultFilter` is the core implementation class of the label system. It implements the `ResultFilter` interface and follows these steps for each query result:

1. Collect all ID values for fields referenced by `@LabelFor` (deduplication applied);
2. Group by label key + ID type;
3. Find the matching `LabelLoader` and call `load()` in batch;
4. Write the returned label texts back into the corresponding label fields.

::: tip Caching
`LabelResultFilter` caches the field-resolution results for each `beanClass` using `ConcurrentHashMap` to avoid repeated reflection. If you modify a SearchBean's field definition at runtime, call `clearCache()` to reset the cache.
:::

### Managing LabelLoaders

```java
filter.addLabelLoader(loader);      // add a loader
filter.removeLabelLoader(loader);   // remove a loader
filter.clearLabelLoaders();         // remove all loaders
filter.clearCache();                // clear the field-resolution cache
```
