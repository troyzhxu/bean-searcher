# 核心注解

本文档解释了如何使用 Bean Searcher 的注解系统将 Java 类映射到数据库查询。它涵盖了核心注解 `@SearchBean`、`@DbField` 和 `@DbIgnore`，以及它们支持的类型和处理机制。

## 概述

Bean Searcher 使用声明式注解系统将普通的 Java 类转换为可搜索的实体。该框架在运行时分析这些注解，以生成相应的 SQL 查询、处理字段映射并管理数据库交互。

SearchBean 是任何可以与 Bean Searcher 配合使用以执行数据库搜索的 Java 类。类可以通过显式注解成为 SearchBean，也可以 `@SearchBean` 在省略注解时通过自动映射实现。

## @SearchBean

该 `@SearchBean` 注解将类标记为可检索，并定义其数据库映射配置。如果省略此注解，Bean Searcher 将尝试根据类名和字段名进行自动映射。

### 核心属性

属性 | 类型 | 默认值 | 含义 | 版本
-|-|-|-|-
tables | String | `""` | 数据库表和别名 | 1.0
dataSource | String | `""` | 数据源标识符 | 3.0
where | String | `""` | 静态 WHERE 条件 | 3.8
fields | @DbField[] | `""` | [附加属性](/guide/bean/fields.html#附加属性-since-v4-1-0)，用于动态条件 | 4.1
groupBy | String | `""` | GROUP BY 子句 | 1.0
having | String | `""` | 分组查询的 HAVING 子句 | 3.8
orderBy | String | `""` | 默认 ORDER BY 子句 | 3.6
autoMapTo | String | `""` | 字段映射的默认表 | 3.0
distinct | boolean | `false` | 是否对结果集启用 DISTINCT | 1.0
inheritType | InheritType | `DEFAULT` | 领域继承策略 | 3.2
ignoreFields | String[] | `{}` | 要从映射中排除的字段 | 3.4
sortType | SortType | `DEFAULT` | 参数排序约束，[参考](/guide/bean/otherform.html#排序约束-since-v3-6-0) | 3.6
timeout | int | `0` | SQL 执行最大时间（秒），0 表示不限制 | 4.0
maxSize | int | `0` | 单页最大条数，0 表示使用 [全局配置值](/guide/advance/safe.html#风控配置项) | 4.5
maxOffset | long | `0` | 最大分页深度，0 表示使用 [全局配置值](/guide/advance/safe.html#风控配置项) | 4.5

### 数据表映射

属性 `tables` 支持多种格式：

* 单表：`"users"`
* 带别名的表：`"users u"`
* 多张表格：`"users u, roles r, user_roles ur"`
* 复杂连接：`"users u LEFT JOIN roles r ON u.role_id = r.id"`

当 `tables` 为空时，Bean Searcher 会使用配置的命名策略自动将类名映射到表名。

### 多表映射策略

对于多表配置，可用 `autoMapTo` 属性指定未映射字段应指向哪个表，例如：

```java
@SearchBean(
    tables = "users u, roles r", 
    where = "u.role_id = r.id",
    autoMapTo = "u"
)
public class UserWithRole {
    private Long id;         // 自动映射到 u.id
    private String name;     // 自动映射到 u.name
    @DbField("r.name")
    private String roleName; // 显式映射到 r.name
}
```

## @DbField

注解 `@DbField` 可对字段到列的映射和 Bean Searcher 的检索行为进行细粒度控制。它可以应用于类字段，也可以在 `@SearchBean.fields` 动态条件下声明。

### 核心属性

属性 | 类型 | 默认值 | 含义 | 版本
-|-|-|-|-
value | String | `""` | SQL 列表达式 | 1.0
name | String | `""` | 字段参数名称 | 4.1
mapTo | String | `""` | 目标表/别名 | 4.1
conditional | `boolean` | true | 是否允许作为检索参数 | 3.0
onlyOn | Class[] | `{}` | 检索时允许的 [运算符](/guide/param/field.html#字段运算符) | 3.0
alias | String | `""` | SQL 列别名 | 3.5
type | DbType | `UNKNOWN` | 数据库列类型 | 3.8
cluster | Cluster | `AUTO` | 聚合列标记，[参考](/guide/bean/fields.html#where-或-having) | 4.1

### 属性映射策略

该注解支持多种映射模式：

#### 简单列映射

1. 简单列：`@DbField("name")`
2. 指定表：`@DbField("r.name")` 
3. 使用 `mapTo` ：`@DbField(value="name", mapTo="r")` 

如果此属性是 [字段属性](/guide/bean/fields.html#字段属性)，其 Java 字段名与表列名一致，则无需指定 `value` 属性。

4. 列映射：`@DbField(mapTo="r")` 

#### 表达式映射

可以直接映射任何合法的 SQL 表示式：

1. 列连接：`@DbField("CONCAT(u.first_name, ' ', u.last_name)")`
2. 日期格式化：`@DbField("date_format(date_created, '%Y-%m-%d')")`
3. 使用聚合函数：`@DbField("avg(u.score)")`
4. 使用 `case` 表达式：`@DbField("sum(case when status = 0 then 1 else 0 end)")`

#### 子查询映射

还可以直接映射到一个子查询

1. 子查询：`@DbField("(SELECT COUNT(*) FROM orders WHERE user_id = u.id)")`

### 条件字段控制

属性 `conditional` 可控制字段是否可以用作搜索参数：

```java
// 指定不能生成检索条件，只承载检索结果
@DbField(conditional = false)
private Date createdAt;
```

属性 `onlyOn` 可限制了条件字段允许使用的运算符：


```java
// 只允许 > 和 < 运算符（第一个是默认运算符）
@DbField(onlyOn = {GreaterThan.class, LessThan.class})
private Integer age;
```

## @DbIgnore

注解 `@DbIgnore` 会将某些字段从数据库映射中排除。它与 `@DbField` 不能标注在同一字段上。

参考：[属性忽略](/guide/bean/fignore.html) 章节
