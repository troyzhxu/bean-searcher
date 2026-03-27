# 导出注解

## @Export

> since v4.5.0

`@Export` 是数据导出的核心注解，用于标记 SearchBean 中需要导出的字段及其导出配置。**只有被 `@Export` 注解标注的字段**才会出现在导出文件中。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Export {

    /** 列名（导出文件中显示的表头文本）*/
    String name();

    /** 列顺序（数值越小越靠前，默认 0）*/
    int idx() default 0;

    /** 值转换表达式（支持 SpEL / SnEL）*/
    String expr() default "";

    /** 格式化输出（支持日期 / 数字 / 字符串格式）*/
    String format() default "";

    /** 导出条件表达式（since v4.8.0，支持 SpEL / SnEL）*/
    String onlyIf() default "";

}
```

### name — 列名

`name` 是**必填属性**，指定该字段在导出文件（如 CSV 表头行）中显示的列名：

```java
@Export(name = "订单ID")
private Long id;

@Export(name = "买家姓名")
private String buyerName;
```

### idx — 列顺序

`idx` 用于控制导出时的列顺序，**数值越小越靠前**，默认值为 `0`。当多个字段的 `idx` 相同时，顺序按字段在类中的声明顺序决定（子类字段优先于父类字段）：

```java
@Export(name = "订单ID", idx = 1)
private Long id;

@Export(name = "创建时间", idx = 10)
private LocalDateTime createTime;

@Export(name = "买家姓名", idx = 5)
private String buyerName;
// 导出列顺序：订单ID(1) → 买家姓名(5) → 创建时间(10)
```

### expr — 值转换表达式

`expr` 用于在导出前对字段值进行计算或转换，支持以下语法：

- **`@`** ：代表当前字段的值；
- **字段名** ：引用同一对象中其他字段的值；
- **Spring 环境** ：支持完整的 SpEL（Spring Expression Language）语法；
- **Solon 环境** ：支持 SnEL（Solon 表达式语法）。

示例：

```java
// 直接引用当前字段值（此处 @ 就是 amount 的值）
@Export(name = "金额", expr = "@ + ' 元'")
private BigDecimal amount;

// 引用其他字段（将 price 和 quantity 相乘计算小计）
@Export(name = "小计", expr = "price * quantity")
private transient String subTotal;   // 虚拟字段，用 transient 排除数据库映射

// SpEL：使用三元表达式
@Export(name = "是否完成", expr = "status == 'DONE' ? '是' : '否'")
private String status;
```

::: tip 关于虚拟字段
若需要导出一个不对应数据库列的计算字段，可以将字段声明为 `transient`（避免被 Bean Searcher 映射到 SQL），然后配合 `expr` 来计算其值。
:::

### format — 格式化输出

`format` 用于对字段值（或 `expr` 计算后的结果）进行格式化。框架内置的 `Formatter.DEFAULT` 支持三种类型：

| 字段类型 | 格式示例 | 效果 |
|---------|---------|------|
| `Date` / `LocalDateTime` 等日期类型 | `yyyy-MM-dd HH:mm:ss` | `2024-06-01 12:00:00` |
| `Number` 及其子类 | `#,##0.00` | `1,234.56` |
| 其他 | `%s（元）` | `字符串（元）` |

```java
@Export(name = "创建时间", format = "yyyy-MM-dd")
private LocalDate createDate;

@Export(name = "金额（元）", format = "#,##0.00")
private BigDecimal amount;
```

::: tip expr 与 format 同时使用
当 `expr` 和 `format` 同时配置时，框架会**先执行 `expr` 计算**，再对计算结果执行 `format` 格式化。
:::

### onlyIf — 导出条件（since v4.8.0）

`onlyIf` 用于动态控制某个字段**是否参与本次导出**，支持使用检索参数（`paraMap`）中的值进行条件判断：

- 使用**参数名**直接引用 `paraMap` 中的值；
- **Spring 环境**：支持 SpEL 语法；
- **Solon 环境**：支持 SnEL 语法。

```java
// 仅当检索参数中包含 showPhone=true 时，才导出手机号字段
@Export(name = "手机号", onlyIf = "showPhone")
private String phone;

// 使用 SpEL：当 exportType 参数为 'full' 时才导出
@Export(name = "备注", onlyIf = "exportType == 'full'")
private String remark;
```

当 `onlyIf` 不配置（或为空字符串）时，默认始终导出该字段。

### 风控配置

Bean Searcher 内置了**分页风控**机制，默认：

- 单次最大查询条数（`maxAllowedSize`）为 **100**
- 最大偏移量（`maxAllowedOffset`）为 **20000**

而 `BeanExporter` 导出时每批会查询 `batchSize`（默认 **1000**）条数据，随着数据量增大，偏移量也会超过 20000。因此，**导出用的 SearchBean 必须通过 `@SearchBean` 的 `maxSize` 与 `maxOffset` 属性放开风控限制**，否则会抛出 `IllegalParamException`。

```java{1}
@SearchBean(
    tables = "order",
    maxSize = 2000,          // 放开单批查询条数限制（须 >= batchSize）
    maxOffset = Long.MAX_VALUE  // 放开分页深度限制（允许导出全量数据）
)
public class OrderExportVO {
    // ...
}
```

::: tip 为什么需要 maxOffset？
`BeanExporter` 通过不断翻页（`page(0, size)` → `page(1, size)` → ...）拉取全量数据。
若不设置 `maxOffset`，当数据量超过 `maxAllowedOffset`（默认 20000）条时，翻页的 offset 会超出限制而报错。
:::

::: warning 独立 SearchBean
建议为导出场景单独定义一个 SearchBean（如 `OrderExportVO`），而不是与检索接口共用，因为导出 Bean 需要放开风控，不适合暴露给普通检索接口。
:::

### 完整示例

```java
@SearchBean(
    tables = "order o, user u",
    where = "o.buyer_id = u.id",
    autoMapTo = "o",
    maxSize = 2000,             // 放开单批查询条数限制
    maxOffset = Long.MAX_VALUE  // 放开分页深度限制
)
public class OrderExportVO {

    @Export(name = "订单编号", idx = 1)
    private String orderNo;

    @Export(name = "买家姓名", idx = 2)
    @DbField("u.name")
    private String buyerName;

    @Export(name = "下单时间", idx = 3, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Export(name = "订单金额（元）", idx = 4, format = "#,##0.00")
    private BigDecimal amount;

    @Export(name = "订单状态", idx = 5, expr = "status == 1 ? '已支付' : status == 2 ? '已发货' : '其他'")
    private Integer status;

    @Export(name = "手机号", idx = 6, onlyIf = "showPhone")
    private String buyerPhone;

    // 省略 Getter / Setter
}
```
