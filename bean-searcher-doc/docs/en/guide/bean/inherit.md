# Entity Class Inheritance

Since v3.2.0, Bean Searcher supports entity class inheritance. The content that can be inherited in an entity class includes:

* Multi-table association information
* Field mapping information

## Field Inheritance

For example, there is a base class with some common attributes:

```java
public class BaseEntity {
    private long id;
    private long version;
    private Date createAt;
    private Date updateAt;
}
```

Then we can define a new entity class to inherit from it:

```java
public class User extends BaseEntity {
    // The fields of the parent class and the child class are mapped to the same table
    private long id;
    private String username;
    private int roleId;
}
```

Another example:

```java
@SearchBean(tables="user u, role r", where="u.role_id = r.id", autoMapTo="u")
public class User extends BaseEntity {
    // The unannotated fields in the parent class and the child class are all mapped to the user table
    private long id;
    private String username;
    private int roleId;
    @DbField("r.name")
    private String roleName;
}
```

## Table Inheritance

Sometimes, there is too much content written in the `@SearchBean` annotation. Can the child class reuse it? Yes, it can. For example:

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

Now we need a new entity class that is also mapped to the `user` and `role` tables, but with many more fields. We want to reuse it without modifying the original entity class. We can do it like this:

```java
// The @SearchBean annotation of the parent class will be reused
public class UserDetail extends User {
    private int age;
    private int status;
    @DbField("r.role_type")
    private int roleType;
}
```

::: tip Note
Only one `@SearchBean` annotation will take effect for an entity class. If both the child class and the parent class have this annotation, the annotation of the child class will take effect, and the annotation of the parent class will be overwritten.
:::

## Inheritance Modes

The default inheritance mode is to inherit both fields and tables, but we can specify other modes.

### Specify the Inheritance Mode for a Single Entity Class

```java
@SearchBean(
    // Specify to only inherit fields
    inheritType = InheritType.FIELD
)
public class UserDetail extends User {
    private int age;
    private int status;
    private int roleType;
}
```

Among them, `InheritType` is an enumeration type with the following values:

* `DEFAULT` - Use the default configuration
* `NONE` - Do not inherit
* `TABLE` - Only inherit tables (`@SearchBean` annotation)
* `FIELD` - Only inherit class attributes
* `ALL` - Inherit both

## Configure Default Values

You can also use global configuration to modify the default inheritance type.

### SpringBoot / Grails (since v3.6.0)

When using the `bean-searcher-boot-starter` dependency, you can configure it through the following key names:

Configuration Key Name | Meaning | Optional Values | Default Value
-|-|-|-
`bean-searcher.sql.default-mapping.inherit-type` | Default inheritance type | `ALL`, `TABLE`, `FIELD`, `NONE` | `ALL`

### Non-Boot Spring Projects

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
    <!-- Other attribute configurations are omitted. The BeanSearcher retriever has the same configuration -->
    <property name="metaResolver" ref="metaResolver" />
</bean>
```

### Other Frameworks

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setDefaultInheritType(InheritType.ALL);               // Configure the default inheritance type here
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Other configurations are omitted
        .metaResolver(new DefaultMetaResolver(dbMapping))       // The BeanSearcher retriever has the same configuration
        .build();
```
