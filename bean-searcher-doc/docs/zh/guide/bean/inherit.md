# 实体类继承

Bean Searcher 自 v3.2.0 开始支持实体类继承。一个实体类中可被继承的内容有：

* 多表关联信息 
* 字段映射信息

## 字段继承

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

## 表继承

如果 `@SearchBean` 注解里的内容比较多，子类想复用该怎么做？也很简单，例如：

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

## 继承方式

默认的继承方式是 字段 与 表 都继承，但我们可以指定使用其它方式。

### 指定单个实体类的继承方式

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

其中 `InheritType` 是一个枚举类型，共有以下一些值：

* `DEFAULT` - 使用默认配置
* `NONE` - 不继承
* `TABLE` - 只继承表（@SearchBean 注解）
* `FIELD` - 只继承类属性
* `ALL` - 都继承

## 配置默认值

如果项目中大多数实体类都需要某种特定的继承方式，可以通过全局配置来修改默认值，避免逐一声明。

### SpringBoot / Grails（since v3.6.0）

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.inherit-type` | 默认继承类型 | `ALL`、`TABLE`、`FIELD`、`NONE` | `ALL`

### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setDefaultInheritType(InheritType.ALL);               // 这里配置默认继承类型
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))       // BeanSearcher 检索器也同此配置
        .build();
```

## 泛型字段转换（since v4.8.7）

当父类中含有**泛型字段**时，Bean Searcher 可以正确识别子类中该字段的实际类型，并使用合适的 `FieldConvertor` 对其进行转换。

例如：

```java
// 父类定义泛型 ID 字段
public class BaseEntity<ID> {
    private ID id;
}

// 子类指定泛型为 Long
public class User extends BaseEntity<Long> {
    private String username;
}
```

在 v4.8.7 之前，如果数据库返回的 `id` 值类型与 `Long` 不符（例如返回 `BigDecimal`），Bean Searcher 不会对该字段做类型转换——因为泛型擦除后父类中 `id` 的类型为 `Object`，无法匹配到对应的 `FieldConvertor`。

自 v4.8.7 起，Bean Searcher 能够正确解析子类对泛型的实际绑定类型，使 `FieldConvertor` 可以正常介入并完成类型转换，避免后续 JSON 序列化等场景出现类型兼容性问题。
