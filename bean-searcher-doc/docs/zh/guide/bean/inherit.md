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

其中 `InheritType` 是一个枚举类型，共有一下一些值：

* `DEFAULT` - 使用默认配置
* `NONE` - 不继承
* `TABLE` - 只继承表（@SearchBean 注解）
* `FIELD` - 只继承类属性
* `ALL` - 都继承

## 配置默认值

你也可以使用全局配置来修改默认的继承类型。

### SpringBoot / Grails（since v3.6.0）

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.inherit-type` | 默认继承类型 | `ALL`、`TABLE`、`FIELD`、`NONE` | `ALL`

### 非 Boot 的 Spring 项目

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

### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setDefaultInheritType(InheritType.ALL);               // 这里配置需要默认继承类型
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))       // BeanSearcher 检索器也同此配置
        .build();
```
