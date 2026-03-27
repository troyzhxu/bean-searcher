# 属性忽略

Bean Searcher 中共有四种方法可以忽略实体类中的某个属性。

## 修饰符 static 与 transient

被关键字 `static` 或 `transient` 修饰的属性会被自动忽略，例如：

```java
public class Address {
    public static String SUZHOU = "苏州市"; // 自动忽略
    private String city;    // 不会忽略
    private String street;  // 不会忽略
    private transient fullAddress;          // 自动忽略
    // Getter Setter ...
}
```

## @DbIgnore 忽略单个字段

Bean Searcher 自 v3.0.0 新增了 `@DbIgnore` 注解，我们可以直接用它来标记实体类中的某个属性，从而忽略它参与数据库映射。

::: warning 注意
该注解不可以与  `@DbField` 注解使用在同一个属性上。
:::

### 组合注解（since v4.4.0）

自 `v4.4.0` 起，`@DbIgnore` 支持被标注到其它自定义注解上，使该自定义注解也具备 `@DbIgnore` 的功能。

**典型场景：**项目中已使用了 Jackson 的 `@JsonIgnore` 注解来忽略 JSON 序列化字段，同时也希望这些字段在 Bean Searcher 中被自动忽略，而无需同时标注两个注解。可这样定义自定义 `@JsonIgnore`：

```java
@DbIgnore
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JsonIgnore {
}
```

之后，任何标注了 `@JsonIgnore` 的字段，Bean Searcher 也会自动将其忽略：

```java
public class User {
    private Long id;
    private String name;
    @JsonIgnore         // 同时具有 @DbIgnore 的效果，Bean Searcher 会忽略此字段
    private String password;
}
```

## @SearchBean.ignoreFields 忽略多个字段

Bean Searcher 自 v3.4.0 为注解 `@SearchBean` 新增了 `ignoreFields` 参数，我们可以设定它的值来忽略这个实体类中的多个属性。

```java
@SearchBean(
    ignoreFields = {"field1", "field2"}
)
public class User extends BaseEntity {
    // ...
}
```

::: tip 既然可以用 `@DbIgnore` 直接忽略指定字段，为什么还需要 `@SearchBean.ignoreFields` 呢？
* 原因一：在某些框架中，可能会在运行时对实体类动态添加某些字段，对于这些在运行时动态添加上去的字段，我们无法给它标记 `@DbIgnore` 注解
* 原因二：有时候要忽略的属性在父类中，但这个属性在其它的子实体类中又不能被忽略
:::

## 全局属性忽略

Bean Searcher 自 v3.4.0 开始支持全局属性忽略某些未被 `@DbField` 注解的属性。

### SpringBoot / Grails

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.ignore-fields` | 需要全局忽略的属性名（可指定多个） | `字符串数组` | `null`

### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setIgnoreFields(new String[] { "field1", "field2" }); // 配置全局忽略的属性名
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))       // BeanSearcher 检索器也同此配置
        .build();
```
