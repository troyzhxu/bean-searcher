# 条件属性

实体类中，可以根据 [字段参数](/guide/param/field) 动态生成条件的属性，被称为条件属性。具体点，它还可以细分为 [字段属性](#字段属性) 与 [附加属性](#附加属性) 两种。

## 字段属性

所谓字段属性，即是检索实体类中声明与数据库表字段有关联的 Java 字段，例如下例中 `OrderVO` 中声明的所有字段：

```java
@SearchBean(
    tables = "order o, shop s, user u",  // 三表关联
    where = "o.shop_id = s.id and o.buyer_id = u.id",  // 关联关系
    autoMapTo = "o"  // 未被 @DbField 注解的字段都映射到 order 表
)
public class OrderVO {
    private long id;         // 订单ID   o.id
    private String orderNo;  // 订单号   o.order_no
    private long amount;     // 订单金额 o.amount
    @DbField("s.name")
    private String shop;     // 店铺名   s.name
    @DbField("u.name")
    private String buyer;    // 买家名   u.name
    // 省略 Getter Setter
}
```

一般字段属性都是被 `@DbField` 注解的字段，但这个注解在某些场景下也可以被省略，参考 [注解缺省](/guide/bean/aignore) 章节。当然某些 Java 字段也可以被忽略，参考 [属性忽略](/guide/bean/fignore) 章节。

### 作用与特点

* 在列表查询时，作为 Select 列表中的查询字段，当然也可以在 Select 列表中指定或排除某个特定字段，参考 [指定 Select 字段](/guide/param/select.html) 章节。
* 用 BeanSearcher 检索器列表查询时，字段属性用于承载查询结果。
* 根据 [字段参数](/guide/param/field) 生成 where 或者 having 条件


## 附加属性（since v4.1.0）

字段属性虽然可以根据参数动态生成条件，但是必须要在 Java 类中声明一个字段。如果你不想写这个字段，则可以使用 `@SearchBean.fields` 来定义附加属性，例如：

```java
@SearchBean(
    tables = "user u, role r",  // 二表关联
    where = "u.role_id = r.id"
    fields = {                  // 定义附加属性
        @DbField(name = "name"),                     // 属性名为 name, 自动映射到 u.name
        @DbField(name = "rType", value = "r.type")   // 属性名为 rType, 映射到 r.type
    },
    autoMapTo="u"  // 自动映射到 u
)
public class UserVO { ... }
```

上面的代码为 `UserVO` 定义了 `name` 与 `rType` 两个附加属性。它们必须用注解 `@DbField` 来定义，并且 `@DbField.name` 不能省略，它的作用等同于字段属性中的 Java 字段名。

### 作用与特点

* 它们和字段属性一样，都可以根据检索参数动态生成 where 或者 having 条件；
* 在列表查询时，它们不会出现在 Select 列表中。

## Where 或 Having

条件属性在生成条件时，是生成 where 条件还是 having 条件呢？这要分以下两种情况来说。

### 没有使用分组（groupBy）时

即在没有指定 `@SearchBean.groupBy` 的值时，所有条件属性生成的条件都是 where 条件。

### 使用分组（groupBy）时

当指定了 `@SearchBean.groupBy` 时，具体是生成 where 条件还是 having 条件，这又取决于每个条件属性的 `@DbField.cluster` 的值，它有以下三种取值：

* `Cluster.TRUE` - 表明该属性是聚合字段，它只会生成 having 条件；
* `Cluster.FALSE` - 表明该属性是非聚合字段，它只会生成 where 条件；
* `Cluster.AUTO` - **默认值**，自动推断该字段是否是聚合字段：**当条件属性未在 groupBy 列表中时，并且该属性同时是 Java 类中的字段时，将自动推断为 `TRUE`，其它情况都推断为 `FALSE`**。

#### 字段推断举例

* [附加属性](#附加属性-since-v4-1-0) 全部自动推断为 `Cluster.FALSE`，即默认都生成 where 条件；
* 下例中，`courseId` 自动推断为 `Cluster.FALSE`，`totalScore` 自动推断为 `Cluster.TRUE`

```java
@SearchBean(tables = "student_course sc", groupBy = "sc.course_id") 
public class CourseScore {
    @DbField("sc.course_id")    // 在 groupBy 列表中
    private long courseId;
    @DbField("sum(sc.score)")   // 不在 groupBy 列表中
    private long totalScore;
    // ...
}
```
