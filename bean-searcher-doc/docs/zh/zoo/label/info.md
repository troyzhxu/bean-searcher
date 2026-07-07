# 介绍（since v4.4）

## 概述

### 版本说明

从 v4.4 起，Bean Searcher 提供了 Bean Searcher Label 组件。它可以将 SearchBean 中的某个字段标记为另一个字段的标签，并自动为这个标签字段赋值（另一种说法称为 字典翻译）。

### 标签系统

标签系统是一个**结果增强组件**，它可以根据 ID 值或其他字段数据，自动为 SearchBean 中的指定字段填充易于理解的标签。该系统无需复杂的 SQL 连接或手动后处理，即可自动解析外键关系、枚举转换和其他 ID 到标签的映射关系。

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

微服务场景下，所需关联的数据表不在此应用（服务）中（例如上例中的 order 与 user 表在两个不同的库中），就无法直接像上例那样直接联表了。此时，如果使用 Bean Searcher Label，则可以轻松解决。

```java{5}
@SearchBean(tables = "order")
public class OrderVO {
    private long id;
    private long buyerId;       // 买家ID
    @LabelFor("buyerId")        // 标记为 buyerId 字段的 Label
    private String buyerName;   // 买家名
    // 省略 Getter Setter
}
```

当然，这还需要全局配置一个 `LabelLoader`，详见[标签加载器](/zoo/label/load)章节。

### 字典表优化

所需关联的表是一种字典表，数据量不大，完全可以加载到内存中缓存，无需每次查询都联表。

### 枚举字段

SearchBean 中有一个枚举字段，需要将枚举值转换为可读文本展示给前端，也可以使用此功能。

## 快速上手

### 第一步：引入依赖

在原有 `bean-searcher-boot-starter`（或 `bean-searcher-solon-plugin`）基础上，追加 `bean-searcher-label` 依赖：

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

### 第二步：在 SearchBean 中添加 @LabelFor 字段

```java
@SearchBean(tables = "order")
public class OrderVO {
    private long id;
    private long buyerId;

    @LabelFor("buyerId")        // 声明为 buyerId 的标签
    private String buyerName;   // 自动填充买家名称

    // 省略 Getter / Setter
}
```

### 第三步：实现并注册 LabelLoader

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

在 SpringBoot / Solon 项目中，将 `LabelLoader` 声明为 Bean 后，框架会**自动收集注入**，无需任何额外配置。

### 第四步：正常调用检索

```java
// 无需任何额外操作，检索结果中的 buyerName 会被自动填充
SearchResult<OrderVO> result = beanSearcher.search(OrderVO.class, paraMap);
```

## 组件说明

| 组件 | 说明 |
|------|------|
| [`@LabelFor`](/zoo/label/anno) | 标签注解，标记一个字段为另一个字段的标签 |
| [`LabelLoader`](/zoo/label/load) | 标签加载器接口，负责批量加载标签文本 |
| [`EnumLabelLoader`](/zoo/label/load#enumlabelloader) | 内置枚举标签加载器 |
| [`LabelResultFilter`](/zoo/label/load#labelresultfilter) | 标签结果过滤器，标签系统的核心驱动组件 |
| [`Label`](/zoo/label/load) | 标签数据传输对象，包含 `id` 和 `label` 文本 |
