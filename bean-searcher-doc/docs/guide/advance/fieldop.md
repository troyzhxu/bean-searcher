# 玩转运算符

自 `v3.3.0` 起，Bean Searcher 的 [字段运算符](/guide/latest/params.html#%E5%AD%97%E6%AE%B5%E8%BF%90%E7%AE%97%E7%AC%A6) 支持高度扩展与自定义。

## 添加新的字段运算符

在 SpringBoot / Grails 项目中，若使用 `bean-searcher-boot-starter` 依赖，则只需实现 [`FieldOp`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/FieldOp.java) 接口，并将之声明为一个 Spring Bean 即可。

例如，定义一个名为 `IsOne` 的字段运算符：

```java
public class IsOne implements FieldOp {
    @Override
    public String name() { return "IsOne"; }
    @Override
    public boolean isNamed(String name) {
        return "io".equals(name) || "IsOne".equals(name);
    }
    @Override
    public boolean lonely() { return true; } // 返回 true 表示该运算符不需要参数值
    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        sqlBuilder.append(fieldSql.getSql()).append(" = 1");
        return fieldSql.getParas();
    }
}
```

接着将其声明为 Spring 的 Bean:

```java
@Bean
public FieldOp myOp() { return new IsOne(); }
```

然后就可以使用它了：

* /user/index ? **age-op=io**  （套用 [起步](/guide/latest/start.html#开始检索) 章节中的例子）
* /user/index ? **age-op=IsOne** （等效请求）
* 或者在参数构建器里使用：

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getAge).op(IsOne.class)    // 推荐写法，since v3.3.1
        .field(User::getAge).op(new IsOne())    // 等效写法，since v3.3.0
        .field(User::getAge).op("io")           // 等效写法
        .field(User::getAge).op("IsOne")        // 等效写法
        .build();
List<User> list = beanSearcher.searchList(User.class, params);
```

它们最后执行的 SQL 中将会有这样的一个条件：

```sql
... where (age = 1) ...
```

::: tip 可参考系统内置运算符的源码实现：
https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher/src/main/java/cn/zhxu/bs/operator
:::

## 定义全新运算符体系

如果你 **不喜欢** Bean Searcher [内置的一套字段运算符](/guide/latest/params.html#%E5%AD%97%E6%AE%B5%E8%BF%90%E7%AE%97%E7%AC%A6)，你可以轻松的将它们 **都换掉**。

### SpringBoot / Grails 项目（使用 bean-searcher-boot-starter 依赖）

只需声明一个 [`FieldOpPool`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/FieldOpPool.java) 类型的 Bean 即可：

```java
@Bean
public FieldOpPool myFieldOpPool() { 
    List<FieldOp> ops = new ArrayList<>();
    // 添加自己喜欢的字段运算符全部 add 进去即可
    // 这里没添加的运算符将不可用
    ops.add(new MyOp1());
    ops.add(new MyOp2());
    ops.add(new MyOp3());
    // ...
    return new FieldOpPool(ops); 
}
```

> 如果你只是想添加一个自己的运算符，系统内置的运算符也想用，则看上一章节就可以了。

### 非 Boot 的 Spring 项目

```xml
<bean id="fieldOpPool" class="cn.zhxu.bs.FieldOpPool">
    <property name="fieldOps">
        <list>
            <bean class="com.demo.MyOp1">
            <bean class="com.demo.MyOp2">
            <bean class="com.demo.MyOp3">
            <!-- 需要使用的自定义运算符都放在这里，也可以添加 Bean Searcher 自带的运算符 -->
            <bean class="cn.zhxu.bs.operator.Equal">
        </list>
    </property>
</bean>
<bean id="paramResolver" class="cn.zhxu.bs.implement.DefaultParamResolver">
    <property name="fieldOpPool" ref="fieldOpPool" />
</bean>
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="paramResolver" ref="paramResolver" />
</bean>
```

### 其它项目

```java
List<FieldOp> ops = new ArrayList<>();
// 添加自己喜欢的字段运算符全部 add 进去即可
ops.add(new MyOp1());   
ops.add(new MyOp2());
ops.add(new MyOp3());
FieldOpPool fieldOpPool = new FieldOpPool(ops);
DefaultParamResolver paramResolver = new DefaultParamResolver();
paramResolver.setFieldOpPool(fieldOpPool);
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .paramResolver(paramResolver)
        .build();
```
