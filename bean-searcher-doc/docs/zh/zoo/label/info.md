# 介绍（since v4.4）

## 版本说明

从 v4.4 起，Bean Searcher 提供了 Bean Searcher Label 组件。它可以将 SearchBean 中的某个字段标记为另一个字段的标签，并自动为这个标签字段赋值（另一种说法成为 字典翻译）。

## 使用场景

虽然 Bean Searcher 支持联表查询，并且对于 `id` 取 `name` 这件事非常熟练，例如：

```java{10}
@SearchBean(
    tables = "order o, user u",
    where = "o.buyer_id = u.id",  // 关联关系
    autoMapTo = "o"
)
public class OrderVO {
    private long id;            // 订单ID
    private long buyerId;       // 买家ID
    @DbField("u.name")
    private String buyerName;   // 买家名   u.name
    // 省略 Getter Setter
}
```

但是，仍然存在某些直接持联表查询无法解决，或可以优化的场景，例如：

### 微服务场景下的跨库关联


微服务场景下，所需关联的表不再在此应用（服务）中（例如上例中的 order 与 user 表在两个不同的库中），就无法直接像上例那样直接联表了。此时，如果使用 Bean Searcher Label，则可以轻松解决。

```java{5}
@SearchBean(tables = "order")
public class OrderVO {
    private long id;
    private long buyerId;       // 买家ID
    @LaberFor("buyerId")        // 标记为 buyerId 字段的 Label
    private String buyerName;   // 买家名
    // 省略 Getter Setter
}
```

当然，这还需要全局配置一个 `LabelLoader`, 下文介绍。

### 字典表优化


* 所需关联的表是一种字典表，数据量不大，可以直接加载到内存中，无需每次查询都联表


### 枚举字段


