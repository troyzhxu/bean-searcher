# 嵌入参数

检索实体类 除了可以实现上述的各种形式的 SQL 以外，还可以在注解 `@SearchBean` 与 `@DbField` 的 SQL 片段内嵌入 **动态** 参数。 

## 使用场景

* 动态指定查询的表字段 或 动态指定查询的数据库表名
* 想按某个表字段检索，但又不想把该表字段做成实体类的字段属性

## 参数类型

实体类的注解内可以嵌入两种形式的参数：

* 形如 `:name` 的可作为 JDBC 参数的 [普通内嵌参数](/guide/latest/params.html#普通内嵌参数)（该参数无 SQL 注入风险，应首选使用）
* 形如 `:name:` 的 [拼接参数](/guide/latest/params.html#拼接参数)（该参数会拼接在 SQL 内，开发者在检索时应 **先检查该参数值的合法性，以免 SQL 注入漏洞产生**）

## 嵌入到 @SearchBean.tables

示例（按某字段动态检索）：

```java
@SearchBean(
    tables = "(select id, name from user where age = :age) t"   // 参数 age 的值由检索时动态指定
) 
public class User {
    
    @DbField("t.id")
    private long id;

    @DbField("t.name")
    private String name;

}
```

示例（动态指定检索表名）：

```java
@SearchBean(
    tables = ":table:"      // 参数 table 由检索时动态指定，这在分表检索时非常有用
) 
public class Order {
    
    @DbField("id")
    private long id;

    @DbField("order_no")
    private String orderNo;

}
```

参考：[示例 > 动态检索 > 分表检索](/guide/latest/simples.html#分表检索) 章节。

## 嵌入到 @SearchBean.where

示例（只查某个年龄的学生）：

```java
@SearchBean(
    tables = "student", 
    where = "age = :age"
) 
public class Student {

    @DbField("s.name")
    private String name;

    // ...
}
```

示例（只查指定某些年龄的学生）：

```java
@SearchBean(
    tables = "student", 
    where = "age in (:ages:)"    // 参数 ages 形如："18,20,25"
) 
public class Student {

    @DbField("s.name")
    private String name;

    // ...
}
```

## 嵌入到 @SearchBean.groupBy

动态指定分组条件：

```java
@SearchBean(
    tables = "student", 
    groupBy = ":groupBy:"           // 动态指定分组条件
) 
public class StuAge {

    @DbField("avg(age)")
    private int avgAge;

}
```

## 嵌入到 @DbField

动态指定检索字段

```java
@SearchBean(tables = "sutdent") 
public class StuAge {

    @DbField(":field:")
    private String value;

}
```

为 Select 子查询动态指定条件

```java
@SearchBean(tables = "student s") 
public class Student {

    @DbField("s.name")
    private String name;

    // 查询某一个科目的成绩（具体哪门科目在检索时有参数 courseId 指定
    @DbField("select sc.score from student_course sc where sc.student_id = s.id and sc.course_id = :courseId")
    private int score;

    // ...
}
```

::: warning 注意
带有嵌入参数的实体类 **属性**，只有 `v3.4.2+` 的版本中才支持参与 过滤条件 与 字段统计。
:::

## 前缀符转义（since v3.6.0）

因为 Bean Searcher 默认使用 `:` 作为嵌入参数的前缀符。所以当 `@SearchBean` 注解的 SQL 片段中用到 `:` 时都会被 Bean Searcher 当做嵌入参数处理。但某些数据库的 SQL 语法确实又包含 `:` 符。比如 PostgreSQL 的 json 语法：

```sql
select '{"name":"Jack"}'::json->'name'  -- 这里的 `:json` 是不应该被当做嵌入参数处理的
```

为了兼容这类情况，Bean Searcher 自 v3.6.0 起新增了转义语义：

* **用 `\\:` 来表示一个原始的 `:` 符（不会被当作嵌入参数前缀符）**

例如：

```java
@DbField("data\\:\\:json->'name'")      // 最终生成的 SQL 片段：data::json->'name'
private String name;
```

参考：https://github.com/troyzhxu/bean-searcher/issues/30
