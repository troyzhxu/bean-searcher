# Mastering Operators

Since `v3.3.0`, the [Field Operators](/en/guide/param/field#Field Operators) of Bean Searcher support high-level extension and customization.

## Adding New Field Operators

In SpringBoot / Grails projects, if you use the `bean-searcher-boot-starter` dependency, you only need to implement the [`FieldOp`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/FieldOp.java) interface and declare it as a Spring Bean.

For example, define a field operator named `IsOne`:

```java
public class IsOne implements FieldOp {
    @Override
    public String name() { return "IsOne"; }
    @Override
    public boolean isNamed(String name) {
        return "io".equals(name) || "IsOne".equals(name);
    }
    @Override
    public boolean lonely() { return true; } // Return true indicates that the operator does not require a parameter value
    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        sqlBuilder.append(fieldSql.getSql()).append(" = 1");
        return fieldSql.getParas();
    }
}
```

Then declare it as a Spring Bean:

```java
@Bean
public FieldOp myOp() { return new IsOne(); }
```

Then you can use it:

* /user/index ? **age-op=io**  (Applying the example in the [Getting Started > Usage](/en/guide/start/use#Start Retrieving) section)
* /user/index ? **age-op=IsOne**  (Equivalent request)
* Or use it in the parameter builder:

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getAge).op(IsOne.class)    // Recommended way, since v3.3.1
        .field(User::getAge).op(new IsOne())    // Equivalent way, since v3.3.0
        .field(User::getAge).op("io")           // Equivalent way
        .field(User::getAge).op("IsOne")        // Equivalent way
        .build();
List<User> list = beanSearcher.searchList(User.class, params);
```

The final executed SQL will have a condition like this:

```sql
... where (age = 1) ...
```

::: tip You can refer to the source code implementation of the system's built-in operators:
https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher/src/main/java/cn/zhxu/bs/operator
:::

## Defining a Brand-New Operator System

If you **don't like** the [built-in set of field operators](/en/guide/param/field#Field Operators) in Bean Searcher, you can easily replace them **all**.

### SpringBoot / Grails Projects (Using the bean-searcher-boot-starter Dependency)

Just declare a Bean of type [`FieldOpPool`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/FieldOpPool.java):

```java
@Bean
public FieldOpPool myFieldOpPool() { 
    List<FieldOp> ops = new ArrayList<>();
    // Add all the field operators you like into it
    // Operators not added here will not be available
    ops.add(new MyOp1());
    ops.add(new MyOp2());
    ops.add(new MyOp3());
    // ...
    return new FieldOpPool(ops); 
}
```

> If you just want to add your own operator and also want to use the system's built-in operators, refer to the previous section.

### Non-Boot Spring Projects

```xml
<bean id="fieldOpPool" class="cn.zhxu.bs.FieldOpPool">
    <property name="fieldOps">
        <list>
            <bean class="com.demo.MyOp1">
            <bean class="com.demo.MyOp2">
            <bean class="com.demo.MyOp3">
            <!-- All the custom operators you need to use are placed here. You can also add the operators provided by Bean Searcher -->
            <bean class="cn.zhxu.bs.operator.Equal">
        </list>
    </property>
</bean>
<bean id="paramResolver" class="cn.zhxu.bs.implement.DefaultParamResolver">
    <property name="fieldOpPool" ref="fieldOpPool" />
</bean>
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- Other property configurations are omitted. The BeanSearcher retriever has the same configuration -->
    <property name="paramResolver" ref="paramResolver" />
</bean>
```

### Other Projects

```java
List<FieldOp> ops = new ArrayList<>();
// Add all the field operators you like into it
ops.add(new MyOp1());   
ops.add(new MyOp2());
ops.add(new MyOp3());
FieldOpPool fieldOpPool = new FieldOpPool(ops);
DefaultParamResolver paramResolver = new DefaultParamResolver();
paramResolver.setFieldOpPool(fieldOpPool);
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Other property configurations are omitted. The BeanSearcher retriever has the same configuration
        .paramResolver(paramResolver)
        .build();
```
