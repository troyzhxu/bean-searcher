# 检索实体类

检索实体类，即是被 `@SearchBean` 注解的类，又称 **SearchBean**，[上一节](/guide/latest/start.html#编写实体类)，我们体验了 Bean Searcher 的单表检索功能，但相比于传统的 ORM，其实它更擅长处理复杂的联表检索 与 一些奇奇怪怪的 子查询，此时 SearchBean 的定义也非常容易。

另外，Bean Searcher 也支持 [省略注解](/guide/latest/bean.html#注解缺省)。一个没有任何注解的实体类也可以自动进行数据库映射。

::: tip 重要提示
这里所说的 实体类（**SearchBean**）是一个与数据库有跨表映射关系的 **VO（View Ojbect）**，它与传统 ORM 中的实体类（**Entity**）或 域类（**Domain**）在概念上有着本质的区别。 
可参阅：[介绍 > 为什么用 > 设计思想（出发点）](/guide/latest/introduction.html#设计思想（出发点）) 章节。
:::

## 多表关联

注解 `@SearchBean` 的 `tables` 属性，可以很容易的指定多张表的关联关系。

### 内连接

```java
@SearchBean(
    tables = "user u, role r",          // 两表关联
    where = "u.role_id = r.id"          // Where 条件（v3.8.0 及之后的写法）
    joinCond = "u.role_id = r.id"       // Where 条件（v3.8.0 之前的写法）
) 
public class User {

    @DbField("u.name")                  // 字段映射
    private Long username;

    @DbField("r.name")                  // 字段映射
    private String rolename;

    // Getter and Setter ...
}
```

或者：

```java
@SearchBean(tables = "user u inner join role r on u.role_id = r.id") 
public class User {
    // ...
}
```

### 左连接

```java
@SearchBean(tables = "user u left join user_detail d on u.id = d.user_id") 
public class User {

    @DbField("u.name")
    private Long username;

    @DbField("d.address")
    private String address;

    // Getter and Setter ...
}
```

### 右连接

```java
@SearchBean(tables = "user_detail d right join user u on u.id = d.user_id")
public class User {
    // ...
}
```

### From 子查询

```java
@SearchBean(
    tables = "(select id, name from user) t"
) 
public class User {
    // ...
}
```

### 关联 From 子查询

```java
@SearchBean(
    tables = "user u, (select user_id, ... from ...) t", 
    where = "u.id = t.user_id"
) 
public class User {
    // ...
}
```

## 其它形式

除了上述的多表关联外，Bean Searcher 还支持很多复杂的 SQL 形式：

### Select 子查询

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

### Where 子查询

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

### Distinct 去重

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

### Group By 分组 与 聚合函数

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

### 字段别名（since v3.5.0）

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

### 默认排序（since v3.6.0）

自 v2.6.0 起，可以在实体类中声明 **默认** 的排序规则，例如：

```java
@SearchBean(orderBy = "age desc, height asc")
public class User {
    // ...
}
```

当检索参数中未指定任何排序信息时，Bean Searcher 将使用 `@SearchBean` 注解中指定的排序信息进行排序。

::: tip 注意
当检索参数中也包含排序信息时，注解 `@SearchBean` 中指定的排序信息可能会被覆盖，参考：[排序约束](/guide/latest/bean.html#排序约束（since-v3-6-0）)。
:::

参见：[参数 > 排序参数](/guide/latest/params.html#排序参数) 章节。

### 排序约束（since v3.6.0）

有时候，我们希望某个检索实体类的生成的 SQL 只能以某个字段进行排序，当我们指定了 [默认排序](/guide/latest/bean.html#默认排序（since-v3-6-0）) 后，我们 **不希望** 检索参数 可以再次修改它。这时，我们可以在实体类中指定排序的约束类型：

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
* `DEFAULT` -（默认值）取决于检索器的 [默认排序约束](/guide/latest/bean.html#配置默认排序约束)

#### 配置默认排序约束

默认排序约束为：`ALLOW_PARAM`，你也可以修改它。

#### SpringBoot / Grails

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.sort-type` | 默认排序约束 | `ALLOW_PARAM`、`ONLY_ENTITY` | `ALLOW_PARAM`

#### 非 Boot 的 Spring 项目

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

#### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setDefaultSortType(SortType.ONLY_ENTITY);               // 这里配置需要默认继承类型
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))       // BeanSearcher 检索器也同此配置
        .build();
```

## 嵌入参数

检索实体类 除了可以实现上述的各种形式的 SQL 以外，还可以在注解 `@SearchBean` 与 `@DbField` 的 SQL 片段内嵌入 **动态** 参数。 

#### 使用场景

* 动态指定查询的表字段 或 动态指定查询的数据库表名
* 想按某个表字段检索，但又不想把该表字段做成实体类的字段属性

#### 参数类型

实体类的注解内可以嵌入两种形式的参数：

* 形如 `:name` 的可作为 JDBC 参数的 [普通内嵌参数](/guide/latest/params.html#普通内嵌参数)（该参数无 SQL 注入风险，应首选使用）
* 形如 `:name:` 的 [拼接参数](/guide/latest/params.html#拼接参数)（该参数会拼接在 SQL 内，开发者在检索时应 **先检查该参数值的合法性，以免 SQL 注入漏洞产生**）

### 嵌入到 @SearchBean.tables

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

### 嵌入到 @SearchBean.where

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

### 嵌入到 @SearchBean.groupBy

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

### 嵌入到 @DbField

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

### 前缀符转义（since v3.6.0）

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

## 注解缺省

Bean Searcher 自 v3.0 起开始支持注解省略。

### 省略 @SearchBean

当 Bean Searcher 找不到 `@SearchBean` 注解（v3.2 开始会自动寻找父类的 `@SearchBean` 注解），或 `@SearchBean` 注解内没有指定 `tables` 属性时，会认为该实体类是一个 **单表映射** 实体类。此时的表名将服从自动映射规则：

* `表名` =  `前缀` + `根据配置是否转为大写（驼峰转小写下划线（去掉冗余后缀的类名））`

其中的 `前缀` 与 `根据配置是否转为大写` 是一个可配置项，可使用以下方式配置。

#### SpringBoot / Grails

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.table-prefix` | 表名前缀 | `字符串` | `null`
`bean-searcher.sql.default-mapping.underline-case` | 表名和字段名是否驼峰转小写下划线（since v3.7.0） | `布尔值` | `true`
`bean-searcher.sql.default-mapping.upper-case` | 表名和字段名是否大写 | `布尔值` | `false`
`bean-searcher.sql.default-mapping.redundant-suffixes` | 类名的冗余后缀（可配多个）（since v3.3.0） | `冗余后缀` | `null`

::: tip 冗余后缀
例如冗余后缀配置为 VO,DTO 时，则对于名为 `UserVO`, `UserDTO` 的实体类, 在自动映射表名是，会自动将 VO，DTO 后缀给去掉。
:::

#### 非 Boot 的 Spring 项目

```xml
<bean id="dbMapping" class="cn.zhxu.bs.implement.DefaultDbMapping">
    <property name="tablePrefix" value="t_" />      <!-- 表名前缀 -->
    <property name="underlineCase" value="true" />  <!-- 是否驼峰转小写下划线 -->
    <property name="upperCase" value="false" />     <!-- 是否大写 -->
</bean>
<bean id="metaResolver" class="cn.zhxu.bs.implement.DefaultMetaResolver">
    <property name="dbMapping" ref="dbMapping" />
</bean>
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="metaResolver" ref="metaResolver" />
</bean>
```

#### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setTablePrefix("t_");     // 表名前缀
dbMapping.setUpperCase(false);      // 是否大写
dbMapping.setUnderlineCase(true);   // 是否驼峰转小写下划线

MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))   // BeanSearcher 检索器也同此配置
        .build();
```

### 省略 @DbField

当检索实体类满足以下四个条件之一时（满足一个即可）：

* 实体类省略了 `@SearchBean` 注解
* 实体类的 `@SearchBean` 没有指定 `tables` 属性
* 实体类的 `@SearchBean.tables` 只含一张表（since v3.8.0）
* 实体类的 `@SearchBean` 指定了 `autoMapTo` 属性

则实体类中省略 `@DbField` 注解 且没被 `@DbIgnore` 注解的字段将会自动映射到数据库，自动映射规则为：

* `数据库字段名` = `根据配置是否转为大写（驼峰转小写下划线（实体类字段名））`
* 如果实体类指定了 `autoMapTo` 属性，则该字段映射到 `autoMapTo` 指定的表中

其中的 `根据配置是否转为大写` 是一个可配置项，配置方法 [同上文](/guide/latest/bean.html#省略-searchbean)。

::: tip 提示
如果想忽略某个字段，可使用 `@DbIgnore` 注解，它不可与 `@DbField` 在同一个字段上使用。
:::

### 识别其它 ORM 注解

例如你已经在项目中使用了 Jpa，那么你可能希望 Bean Searcher 自动识别 Jpa 的注解。这很简单，如果你使用的是 `bean-searcher-boot-starter` 或 `bean-searcher-solon-plugin` 依赖，则只需声明一个 Bean 即可：

```java
@Bean
public DbMapping bsJpaDbMapping(BeanSearcherProperties config) {
    var mapping = new DefaultDbMapping() {

        @Override
        public String toTableName(Class<?> beanClass) {
            // 识别 JPA 的 @Table 注解
            var table = beanClass.getAnnotation(javax.persistence.Table.class);
            if (table != null && StringUtils.notBlank(table.name())) {
                return table.name();
            }
            // 识别 JPA 的 @Entity 注解
            var entity = beanClass.getAnnotation(javax.persistence.Entity.class);
            if (entity != null && StringUtils.notBlank(entity.name())) {
                return entity.name();
            }
            return super.toTableName(beanClass);
        }

        @Override
        public String toColumnName(BeanField field) {
            // 识别 JPA 的 @Column 注解
            var column = field.getAnnotation(javax.persistence.Column.class);
            if (column != null && StringUtils.notBlank(column.name())) {
                return column.name();
            }
            return super.toColumnName(field);
        }

    };
    BeanSearcherProperties.Sql.DefaultMapping conf = config.getSql().getDefaultMapping();
    mapping.setTablePrefix(conf.getTablePrefix());
    mapping.setUpperCase(conf.isUpperCase());
    mapping.setUnderlineCase(conf.isUnderlineCase());
    mapping.setRedundantSuffixes(conf.getRedundantSuffixes());
    mapping.setIgnoreFields(conf.getIgnoreFields());
    mapping.setDefaultInheritType(conf.getInheritType());
    mapping.setDefaultSortType(conf.getSortType());
    return mapping;
}
```

如果你用的是其它 ORM，则只需要简单修改 `String toTableName(Class<?> beanClass)` 与 `String toColumnName(BeanField field)` 方法里的代码即可。

## 实体类继承

Bean Searcher 自 v3.2.0 开始支持实体类继承。一个实体类中可被继承的内容有：

* 多表关联信息 
* 字段映射信息

### 字段继承

例如有一个基类，里面有一些公共属性：

```java
public class BaseEntity {
    private long id;
    private long version;
    private Date createAt;
    private Date updateAt;
}
```

然后我们可以定义一个新的实体类来继承它：

```java
public class User extends BaseEntity {
    // 父类与子类的字段映射到同一张表
    private long id;
    private String username;
    private int roleId;
}
```

再如：

```java
@SearchBean(tables="user u, role r", where="u.role_id = r.id", autoMapTo="u")
public class User extends BaseEntity {
    // 父类与子类中的未被注解的字段都映射到 user 表
    private long id;
    private String username;
    private int roleId;
    @DbField("r.name")
    private String roleName;
}
```

### 表继承

有时候 `@SearchBean` 注解内写入的内容太多，子类能否复用呢？也是可以的，例如：

```java
@SearchBean(tables="user u, role r", where="u.role_id = r.id", autoMapTo="u")
public class User {
    private long id;
    private String username;
    private int roleId;
    @DbField("r.name")
    private String roleName;
}
```

现在我们需要一个新的实体类，它同样映射到 `user` 和 `role` 表，只是字段多了许多，希望复用它，又不想改动原有的实体类，可以这么做：

```java
// 将复用父类的 @SearchBean 注解
public class UserDetail extends User {
    private int age;
    private int status;
    @DbField("r.role_type")
    private int roleType;
}
```

::: tip 注意
一个实体类只会有一个 `@SearchBean` 注解生效，如果子类和父类都添加了该注解，则子类的注解生效，父类的注解将被覆盖。
:::

### 继承方式

默认的继承方式是 字段 与 表 都继承，但我们可以指定使用其它方式。

#### 指定单个实体类的继承方式

```java
@SearchBean(
    // 指定只继承字段
    inheritType = InheritType.FIELD
)
public class UserDetail extends User {
    private int age;
    private int status;
    private int roleType;
}
```

其中 `InheritType` 是一个枚举类型，共有一下一些值：

* `DEFAULT` - 使用默认配置
* `NONE` - 不继承
* `TABLE` - 只继承表（@SearchBean 注解）
* `FIELD` - 只继承类属性
* `ALL` - 都继承

### 配置默认值

你也可以使用全局配置来修改默认的继承类型。

#### SpringBoot / Grails（since v3.6.0）

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.inherit-type` | 默认继承类型 | `ALL`、`TABLE`、`FIELD`、`NONE` | `ALL`

#### 非 Boot 的 Spring 项目

```xml
<bean id="dbMapping" class="cn.zhxu.bs.implement.DefaultDbMapping">
    <property name="defaultInheritType" /> 
        <util:constant static-field="cn.zhxu.bs.bean.InheritType.ALL"/>
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

#### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setDefaultInheritType(InheritType.ALL);               // 这里配置需要默认继承类型
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))       // BeanSearcher 检索器也同此配置
        .build();
```

## 属性忽略

Bean Searcher 中共有四种方法可以忽略实体类中的某个属性。

### 修饰符 static 与 transient

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

### @DbIgnore 忽略单个字段

Bean Searcher 自 v3.0.0 新增了 `@DbIgnore` 注解，我们可以直接用它来标记实体类中的某个属性，从而忽略它参与数据库映射。

::: warning 注意
该注解不可以与  `@DbField` 注解使用在同一个属性上。
:::

### @SearchBean.ignoreFields 忽略多个字段

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

### 全局属性忽略

Bean Searcher 自 v3.4.0 开始支持全局属性忽略某些未被 `@DbField` 注解的属性。

#### SpringBoot / Grails

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.ignore-fields` | 需要全局忽略的属性名（可指定多个） | `字符串数组` | `null`

#### 非 Boot 的 Spring 项目

```xml
<bean id="dbMapping" class="cn.zhxu.bs.implement.DefaultDbMapping">
    <property name="ignoreFields"> 
        <!-- 这里配置需要全局忽略的属性名 -->
        <array>
            <value>field1</value>
            <value>field2</value>
        </array>
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

#### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setIgnoreFields(new String[] { "field1", "field2" }); // 这里配置需要全局忽略的属性名
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))       // BeanSearcher 检索器也同此配置
        .build();
```

## 可选接口

接口 `BeanAware` 与 `ParamAware` 是 Search Bean 的可选实现接口。实现这个接口的 SearchBean，可以在 afterAssembly 方法里添加 Bean 装配完之后的自定义逻辑。

### BeanAware

实现  `BeanAware` 接口，可在 Bean 装配完之后做一些自定义逻辑逻辑处理：

```java
public class User implements BeanAware {

    // 省略其它代码

	@Override
	public void afterAssembly() {
        // 该方法会在 User 的字段值装配完后自动执行 
        // 代码走到这里说明，说明被 @DbField 注解的字段都已经被赋过值
		System.out.println("id = " + id + ", name = " + name);
	}

}
```

### ParamAware

实现  `ParamAware` 接口，可在 Bean 装配完之后监听到当前的检索参数：

```java
public class User implements ParamAware {

    // 省略其它代码

	@Override
	public void afterAssembly(Map<String, Object> paraMap) {
        // 该方法可接收到当前的检索参数
		System.out.println(paraMap);
	}

}
```
