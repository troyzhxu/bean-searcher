# 标签注解

## @LabelFor

> since v4.4.0

`@LabelFor` 是标签系统的核心注解，用于将 SearchBean 中的某个字段标记为**另一个字段的标签（Label）**，即声明该字段的值需要由标签系统自动填充。

### 注解属性

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LabelFor {

    /**
     * 引用的字段名（即被翻译字段的名称）
     * @return 字段名
     */
    String value();

    /**
     * 标签类型 KEY，用于区分不同类型的标签加载器
     * 若不指定，则默认使用被 @LabelFor 注解的字段名
     * @return 标签 KEY
     */
    String key() default "";

}
```

::: tip 注意
`@LabelFor` 注解自动包含了 `@DbIgnore`，被它标注的字段**不会**被映射到数据库字段，适合用来存放从外部翻译得到的标签文本。
:::

### value 属性

`value` 属性用于**引用 SearchBean 中另一个字段的名称**，指定该字段的值就是要被翻译的 ID（或枚举、或其它主键）。

例如，声明 `buyerName` 字段是 `buyerId` 字段的标签：

```java
@LabelFor("buyerId")
private String buyerName;
```

这表示：标签系统会取出 `buyerId` 的值，通过 `LabelLoader` 加载对应的文本，然后回写到 `buyerName` 字段中。

::: tip 支持跨类层级引用
`value` 属性支持引用父类或子类中的字段，不局限于当前类中声明的字段。
:::

### key 属性

`key` 属性用于区分不同类型的标签加载器，当一个系统中存在多种 `LabelLoader` 时，通过 `key` 决定交给哪个 `LabelLoader` 来处理。

- **未指定 `key`**（`key = ""`）：默认使用被注解的字段名作为 key，例如 `buyerName`。
- **指定了 `key`**：使用指定的 key 进行匹配。

例如：

```java
@LabelFor(value = "statusCode", key = "orderStatus")
private String statusName;
```

此时框架会寻找支持 key 为 `"orderStatus"` 的 `LabelLoader` 来加载标签。

### 完整示例

```java
@SearchBean(tables = "order")
public class OrderVO {

    private long id;

    private long buyerId;           // 买家 ID，来自数据库

    @LabelFor("buyerId")            // 声明 buyerName 是 buyerId 的标签
    private String buyerName;       // 买家姓名，由标签系统自动填充

    private Integer status;         // 订单状态（枚举/字典码）

    @LabelFor(value = "status", key = "orderStatus")
    private String statusName;      // 订单状态文本，由标签系统自动填充

    // 省略 Getter / Setter
}
```
