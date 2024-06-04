# 字段参数

字段参数是根据 检索实体类里具有数据库字段映射的 属性 衍生出来的一系列参数，它们起到对查询结果进行 **筛选** 的作用。

## 可配置项

在 SpringBoot / Grails 项目中，若使用了 `bean-searcher-boot-starter` 依赖，可在项目的 `application.properties` 或 `application.yml` 文件中通过如下配置项对字段参数进行定制：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.separator` | 字段参数名分隔符 | `字符串` | `-`
`bean-searcher.params.operator-key` | 字段运算符参数名后缀 | `字符串` | `op`
`bean-searcher.params.ignore-case-key` | 是否忽略大小写字段参数名后缀 | `字符串` | `ic`

## 字段衍生规则

示例，对于如下的一个实体类:

```java
public class User {
    private String name;
    // 省略其它..
}
```

以它的 `name` 字段为例，可衍生出以下一系列的字段参数：

* **`name-{n}`**： name 字段的第 n 个参数值，如：`name-0`、`name-1`、`name-2` 等等（[理解不了？参考这里](/guide/start/use#_11-字段过滤-field-op-bt)）
* **`name`**： 等价于 `name-0`，name 字段的第 0 个参数值
* **`name-op`**： name 的 [字段运算符](#字段运算符)，如: `Equal`、`GreaterEqual`、`GreaterThan` 等等
* **`name-ic`**： name 字段在检索时是否应该忽略大小写

以上在衍生字段参数时，用到了中划线（`-`）作为连接符，如果你喜欢下划线（`_`），可把 `bean-searcher.params.separator` 配置为下划线即可。配置为下划线后，衍生出的参数就是 `name_{n}`、`name`、`name_op`、`name_ic` 了。同理: `op` 与 `ic` 后缀您也可以自定义。

::: tip
字段参数，是根据实体类里的 **JAVA 字段名**（不是表字段）衍生出来的，已经和数据库的表字段解耦了。
:::

## 字段运算符

字段运算符是用来描述某个字段的检索方式，即：SQL 的拼接方法。Bean Searcher 共默认提供了 19 种不同的字段运算符，见下表：

> 下表中的 `忽略空值` 的含义是：如果该字段的参数值为 `null` 或 `空串`，是否忽略该条件。

运算符 | 缩写 | SQL 片段 | 是否忽略空值 | 含义
-|-|-|-|-
`Equal` | `eq` | `x = ?` | 是 | 等于（是缺省默认的运算符）
`NotEqual` | `ne` | `x != ?` | 是 | 不等于
`GreaterThan` | `gt` | `x > ?` | 是 | 大于
`GreaterEqual` | `ge` | `x >= ?` | 是 | 大于等于
`LessThan` | `lt` | `x < ?` | 是 | 小于
`LessEqual` | `le` | `x <= ?` | 是 | 小于等于
`Between` | `bt` | `x between ?1 and ?2` / `x >= ?1` / `x <= ?2` | 是 | 在...之间（范围查询）
`NotBetween` | `nb` | `x not between ?1 and ?2` / `x < ?1` / `x > ?2` | 是 | 不在...之间（范围查询）（**since v3.3**）
`Contain` | `ct` | `x like '%?%'` | 是 | 包含（模糊查询）（**since v3.2**）
`StartWith` | `sw` | `x like '?%'` | 是 | 以...开头（模糊查询）
`EndWith` | `ew` | `x like '%?'` | 是 | 以...结尾（模糊查询）
`OrLike` | `ol` | `x like ?1 or x like ?2 or ...` | 是 | 模糊或匹配（可有多个参数值）（**since v3.7**）
`NotLike` | `nk` | `x not like ?` | 是 | 反模糊匹配（**since v3.8**）
`InList`  | `il` | `x in (?, ?, ...)` | 是 | 多值查询（**自 v3.3 新增**，之前是 `MultiValue` / `mv`）
`NotIn` | `ni` | `x not in (?, ?, ...)` | 是 | 多值查询（**since v3.3**）
`IsNull` | `nl` | `x is null` | 否 | 为空（**since v3.3**）
`NotNull` | `nn` | `x is not null` | 否 | 不为空（**since v3.3**）
`Empty` | `ey` | `x is null or x = ''` | 否 | 为空（仅适用于 **字符串** 类型的字段）
`NotEmpty` | `ny` | `x is not null and x != ''` | 否 | 不为空（仅适用于 **字符串** 类型的字段）

::: tip 除此之外
你还可以自定义运算符，参见 [高级 > 玩转运算符](/guide/advance/fieldop) 章节。
:::

由于 Bean Searcher 为运算符提供了全称与缩写，所以对于每一种运算符，都有几种等效的用法，

例如，查询 `name` 等于 Jack 的用户：

* 后端参数构建：

```java
Map<String, Object> params = MapUtils.builder()
        .field("name", "Jack")          // (1) 字段 name 的值为 Jack
        .field(User::getName, "Jack")   // 等效写法 == (1) 
        .op("eq")                       // (2) 指定 name 字段的运算符为 eq (默认就是 eq, 所以也可以省略)
        .op("Equal")                    // 等效写法 == (2) 
        .op(FieldOps.Equal)             // 等效写法 == (2) 
        .op(Equal.class)                // 等效写法 == (2) 
        .build();
User jack = searcher.searchFirst(User.class, params);           // 执行查询
```

* 前端传参形式：

```js
GET /users ? name=Jack & name-op=eq        // (1) 字段 name 的值为 Jack，运算符为 eq
GET /users ? name=Jack & name-op=Equal     // 等效写法 == (1) 
GET /users ? name-0=Jack & name-op=eq      // 等效写法 == (1) 
GET /users ? name-0=Jack & name-op=Equal   // 等效写法 == (1) 
GET /users ? name=Jack          // (2) 当 name 无运算符约束，或运算符约束中第一个为 Equal 时，与 (1) 等效 
GET /users ? name-0=Jack        // 等效写法 == (2) 
```
