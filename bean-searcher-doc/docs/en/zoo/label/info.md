# 介绍（since v4.4）

## 概述

### 版本说明

从 v4.4 起，Bean Searcher 提供了 Bean Searcher Label 组件。它可以将 SearchBean 中的某个字段标记为另一个字段的标签，并自动为这个标签字段赋值（另一种说法成为 字典翻译）。

### 标签系统

标签系统是一个结果增强组件，它可以根据 ID 值或其他字段数据，自动为 SearchBean 中的指定字段填充易于理解的标签。该系统无需复杂的 SQL 连接或手动后处理，即可自动解析外键关系、枚举转换和其他 ID 到标签的映射关系。

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

但是，仍然存在某些直接用联表查询无法解决，或可以优化的场景，例如：

### 微服务场景下的跨库关联

微服务场景下，所需关联的数据表不在在此应用（服务）中（例如上例中的 order 与 user 表在两个不同的库中），就无法直接像上例那样直接联表了。此时，如果使用 Bean Searcher Label，则可以轻松解决。

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

所需关联的表是一种字典表，数据量不大，完全可以直接加载到内存中，无需每次查询都联表

### 枚举字段

SearchBean 中有一个枚举字段，但如果前端还需要后端将这个枚举转换为字符串，也可以使用此功能。
