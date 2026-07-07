# Introduction (since v4.4)

## Overview

### Version Note

Starting from v4.4, Bean Searcher provides the Bean Searcher Label component. It allows you to mark a field in a SearchBean as the **label** (translation) of another field and automatically fills in the label value — also known as **dictionary translation**.

### Label System

The label system is a **result-enhancement component**. It automatically fills specified fields in a SearchBean with human-readable labels based on ID values, enum values, or other field data. This system resolves foreign-key lookups, enum conversions, and other ID-to-label mappings without complex SQL JOINs or manual post-processing.

## Use Cases

Bean Searcher already handles cross-table lookups elegantly via JOIN queries, for example:

```java{10}
@SearchBean(
    tables = "order o, user u",
    where = "o.buyer_id = u.id",
    autoMapTo = "o"
)
public class OrderVO {
    private long id;
    private long buyerId;
    @DbField("u.name")
    private String buyerName;   // fetched from user table via JOIN
    // getters / setters omitted
}
```

However, there are scenarios where direct JOINs are not feasible or can be further optimized:

### Cross-Database Lookup in Microservices

In a microservices architecture, the `order` table and `user` table may reside in different databases, making a direct JOIN impossible. Bean Searcher Label solves this gracefully:

```java{5}
@SearchBean(tables = "order")
public class OrderVO {
    private long id;
    private long buyerId;
    @LabelFor("buyerId")        // mark buyerName as the label of buyerId
    private String buyerName;   // automatically filled by the label system
    // getters / setters omitted
}
```

You also need to register a `LabelLoader` — see the [Label Loader](/zoo/label/load) chapter.

### Dictionary Table Optimization

If the reference table is a small dictionary table, caching it in memory and using the label system avoids repeated JOINs on every query.

### Enum Fields

When a SearchBean contains an enum field and the front end needs a human-readable string representation, the label system provides a clean solution.

## Quick Start

### Step 1: Add the Dependency

Add `bean-searcher-label` alongside your existing `bean-searcher-boot-starter` (or `bean-searcher-solon-plugin`):

::: code-group
```groovy [Gradle]
implementation 'cn.zhxu:bean-searcher-label:4.8.8'
```
```xml [Maven]
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.8</version>
</dependency>
```
:::

### Step 2: Annotate Fields with @LabelFor

```java
@SearchBean(tables = "order")
public class OrderVO {
    private long id;
    private long buyerId;

    @LabelFor("buyerId")        // declare buyerName as the label of buyerId
    private String buyerName;   // filled automatically by the label system

    // getters / setters omitted
}
```

### Step 3: Implement and Register a LabelLoader

```java
@Component
public class UserLabelLoader implements LabelLoader<Long> {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(String key) {
        return "buyerName".equals(key);
    }

    @Override
    public List<Label<Long>> load(String key, List<Long> ids) {
        return userService.findAllById(ids).stream()
                .map(u -> new Label<>(u.getId(), u.getName()))
                .collect(Collectors.toList());
    }
}
```

In SpringBoot / Solon projects, declaring the `LabelLoader` as a Bean is all you need — the framework **collects and injects them automatically**.

### Step 4: Query as Usual

```java
// No extra steps needed — buyerName is automatically populated in the results
SearchResult<OrderVO> result = beanSearcher.search(OrderVO.class, paraMap);
```

## Component Overview

| Component | Description |
|-----------|-------------|
| [`@LabelFor`](/zoo/label/anno) | Annotation to mark a field as the label of another field |
| [`LabelLoader`](/zoo/label/load) | Interface for batch-loading label texts |
| [`EnumLabelLoader`](/zoo/label/load#enumlabelloader) | Built-in loader for enum fields |
| [`LabelResultFilter`](/zoo/label/load#labelresultfilter) | Core filter that drives the label system |
| [`Label`](/zoo/label/load) | DTO containing an `id` and its corresponding `label` text |
