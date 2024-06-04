# 其它形式

除了上述的多表关联外，Bean Searcher 还支持很多复杂的 SQL 形式：

## Select 子查询

```java
@SearchBean(tables = "student s") 
public class Student {

    @DbField("s.name")
    private String name;

    // 该学生的总分数（Select 子查询）
    @DbField("select sum(sc.score) from student_course sc where sc.student_id = s.id")
    private int totalScore;

    // ...
}
```

## Where 子查询

```java
@SearchBean(
    tables = "student s", 
    // 只查询考试均分及格的学生（Where 子查询）
    where = "(select avg(sc.score) from student_course sc where sc.student_id = s.id) >= 60"
)
public class GoodStudent {

    @DbField("s.name")
    private String name;

    // ...
}
```

## Distinct 去重

```java
// 参与考试的课程
@SearchBean(
    tables = "student_course sc, course c", 
    where = "sc.course_id = c.id", 
    distinct = true                     // 去重
) 
public class ExamCourse {

    @DbField("c.id")
    private String courseId;

    @DbField("c.name")
    private String courseName;

    // ...
}
```

## Group By 分组 与 聚合函数

```java
@SearchBean(
    tables = "student_course sc", 
    groupBy = "sc.course_id"            // 按课程 ID 分组
) 
public class CourseScore {

    @DbField("sc.course_id")
    private long courseId;

    @DbField("sum(sc.score)")           // 该课程的总分（聚合函数：sum）
    private long totalScore;
    // ...
}
```

如果有固定的 `having` 条件，可以写在这里：

```java
@SearchBean(
    tables = "student_course sc", 
    groupBy = "sc.course_id",           // 按课程 ID 分组
    having = "avg(sc.score) > 50"       // having 条件 （since v3.8.0）
) 
```

## 字段别名（since v3.5.0）

默认情况下，Bean Searcher 会为每个字段都生成一个不重复的别名。自 `v3.5.0` 起，可以使用 `@DbField` 注解的 `alias` 属性手动指定别名，例如：
 
```java
@SearchBean(
    tables = "user u", 
    groupBy = "date"           // 使用别名分组
) 
public class UseData {

    @DbField("count(u.id)")
    private long count;        // 每一天的用户注册数

    // 注册日期格式化精确到天，并指定别名：date
    @DbField(value = "DATE_FORMAT(u.date_created, '%Y-%m-%d')", alias="date") 
    private String dateCreated;
    // ...
}
```

::: tip 注意
别名是否能在 `groupBy` 子句中使用得看使用的是什么数据库，MySQL 是支持的，Oracle 可能不支持这么用。
:::

## 默认排序（since v3.6.0）

自 v2.6.0 起，可以在实体类中声明 **默认** 的排序规则，例如：

```java
@SearchBean(orderBy = "age desc, height asc")
public class User {
    // ...
}
```

当检索参数中未指定任何排序信息时，Bean Searcher 将使用 `@SearchBean` 注解中指定的排序信息进行排序。

::: tip 注意
当检索参数中也包含排序信息时，注解 `@SearchBean` 中指定的排序信息可能会被覆盖，参考：[排序约束](#排序约束-since-v3-6-0)。
:::

参见：[参数 > 排序参数](/guide/param/sort) 章节。

## 排序约束（since v3.6.0）

有时候，我们希望某个检索实体类的生成的 SQL 只能以某个固定的字段进行排序，即当我们指定了 [默认排序](#默认排序-since-v3-6-0) 后，我们 **不希望** 检索参数 可以再次修改它。这时，我们可以在实体类中指定排序的约束类型：

```java
@SearchBean(
    orderBy = "age desc",               // 如果该字段未指定，则表示：禁用排序
    sortType = SortType.ONLY_ENTITY     // 指定只有实体类中的排序信息才会生效，检索参数中的排序信息将会忽略
)
public class User {
    // ...
}
```

其中 `SortType` 是一个枚举，他共有三个值：

* `ONLY_ENTITY` - 只使用实体类中的排序信息，检索参数中的排序信息将会忽略
* `ALLOW_PARAM` - 允许检索参数中的排序信息覆盖实体类中的排序信息
* `DEFAULT` -（默认值）取决于检索器的 [默认排序约束](#配置默认排序约束)

### 配置默认排序约束

默认排序约束为：`ALLOW_PARAM`，你也可以修改它。

### SpringBoot / Grails

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.sort-type` | 默认排序约束 | `ALLOW_PARAM`、`ONLY_ENTITY` | `ALLOW_PARAM`

### 非 Boot 的 Spring 项目

```xml
<bean id="dbMapping" class="cn.zhxu.bs.implement.DefaultDbMapping">
    <property name="defaultSortType" /> 
        <util:constant static-field="cn.zhxu.bs.bean.SortType.ONLY_ENTITY"/>
    </property>
</bean>
<bean id="metaResolver" class="cn.zhxu.bs.implement.DefaultMetaResolver">
    <property name="dbMapping" ref="dbMapping" />
</bean>
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="metaResolver" ref="metaResolver" />
</bean>
```

### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setDefaultSortType(SortType.ONLY_ENTITY);               // 这里配置需要默认继承类型
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))       // BeanSearcher 检索器也同此配置
        .build();
```
