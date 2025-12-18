# Field Ignore

There are four ways in Bean Searcher to ignore a certain field in an entity class.

## Modifiers static and transient

Fields modified by the keywords `static` or `transient` will be automatically ignored. For example:

```java
public class Address {
    public static String SUZHOU = "Suzhou City"; // Automatically ignored
    private String city;    // Not ignored
    private String street;  // Not ignored
    private transient fullAddress;          // Automatically ignored
    // Getter Setter ...
}
```

## @DbIgnore to ignore a single field

Since v3.0.0, Bean Searcher has added the `@DbIgnore` annotation. We can directly use it to mark a certain attribute in an entity class, thus ignoring its participation in database mapping.

::: warning Note
This annotation cannot be used on the same attribute as the `@DbField` annotation.
:::

## @SearchBean.ignoreFields to ignore multiple fields

Since v3.4.0, Bean Searcher has added the `ignoreFields` parameter to the `@SearchBean` annotation. We can set its value to ignore multiple attributes in this entity class.

```java
@SearchBean(
    ignoreFields = {"field1", "field2"}
)
public class User extends BaseEntity {
    // ...
}
```

::: tip Since we can directly ignore the specified field with `@DbIgnore`, why do we still need `@SearchBean.ignoreFields`?
* Reason 1: In some frameworks, certain fields may be dynamically added to the entity class at runtime. For these fields dynamically added at runtime, we cannot mark them with the `@DbIgnore` annotation.
* Reason 2: Sometimes the attribute to be ignored is in the parent class, but this attribute cannot be ignored in other child entity classes.
:::

## Global fields ignoring

Since v3.4.0, Bean Searcher has supported globally ignoring certain fields that are not annotated with `@DbField`.

### SpringBoot / Grails

When using the `bean-searcher-boot-starter` dependency, you can configure it through the following key names:

Configuration key name | Meaning | Optional values | Default value
-|-|-|-
`bean-searcher.sql.default-mapping.ignore-fields` | Attribute names that need to be globally ignored (multiple can be specified) | `String array` | `null`

### Non-Boot Spring projects

```xml
<bean id="dbMapping" class="cn.zhxu.bs.implement.DefaultDbMapping">
    <property name="ignoreFields"> 
        <!-- Configure the attribute names that need to be globally ignored here -->
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
    <!-- Omit other attribute configurations, the BeanSearcher retriever is also configured in the same way -->
    <property name="metaResolver" ref="metaResolver" />
</bean>
```

### Other frameworks

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setIgnoreFields(new String[] { "field1", "field2" }); // Configure the attribute names that need to be globally ignored here
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Omit other configurations
        .metaResolver(new DefaultMetaResolver(dbMapping))       // The BeanSearcher retriever is also configured in the same way
        .build();
```
