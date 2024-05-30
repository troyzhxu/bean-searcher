# 高级

> 如果您还没有阅读 [介绍 > 为什么用](/guide/v3/introduction.html#为什么用) 章节，建议先阅读它们。

## 检索约束

从前面的 [字段参数](/guide/v3/params.html#字段参数) 章节，我们知道，Bean Searcher 对实体类中的每一个字段，都直接支持了很多的检索方式。但有时候我们可能并不需要这么多，甚至某些时候我们需要禁止一些方式。Bean Searcher 使用 [运算符约束](#运算符约束) 和 [条件约束](#条件约束) 来实现此需求。

### 运算符约束

例如：字段 `name` 只允许 **精确匹配** 与 **后模糊匹配**，则，可以在 SearchBean 上使用如下注解：

```java
public class User {

    @DbField(onlyOn = {Equal.class, StartWith.class})           // v3.3.0+ 的写法
    @DbField(onlyOn = {Operator.Equal, Operator.StartWith})     // v3.3.0 以前的写法
    private String name;

    // 为减少篇幅，省略其它字段...
}
```

如上，通过 `@DbField` 注解的 `onlyOn` 属性，指定 `name` 字段只能适用与 **精确匹配** 和 **后模糊匹配** 方式，其它检索方式它将直接忽略。

上面的代码是限制了 `name` 只能有两种检索方式，如果再严格一点，**只允许 精确匹配**，那其实有两种写法。

#### （1）还是使用运算符约束：

```java
@SearchBean(tables="user u, role r", where="u.role_id = r.id", autoMapTo="u") 
public class User {

    @DbField(onlyOn = Equal.class)
    private String name;

    // 为减少篇幅，省略其它字段...
}
```

#### （2）在 Controller 的接口方法里把运算符参数覆盖：

```java
@GetMapping("/index")
public SearchResult<Map<String, Object>> index(HttpServletRequest request) {
    Map<String, Object> params = MapUtils.flatBuilder(request.getParameterMap())
        .field(User::getName).op(Equal.class)   // 把 name 字段的运算符直接覆盖为 Equal
        .build()
    return mapSearcher.search(User.class, params);
}
```

### 条件约束

有时候我们不想让某个字段参与 where 条件，可以这样：

```java
public class User {

    @DbField(conditional = false)
    private int age;

    // 为减少篇幅，省略其它字段...
}
```

如上，通过 `@DbField` 注解的 `conditional` 属性， 就直接不允许 `age` 字段参与条件了，无论前端怎么传参，Bean Searcher 都不搭理。

### 参数过滤器

Bean Searcher 还支持配置全局参数过滤器，可自定义任何参数过滤规则。

* 案例：[使用 参数过滤器器 简化 op=mv/bt 时的多值传参，例如：用 age=[20,30] 替代 age-0=20 & age-1=30 参数](https://github.com/troyzhxu/bean-searcher/issues/10)。

#### 在 SpringBoot / SpringMVC / Grails 项目中，只需要配置一个 Bean（以 SpringBoot 为例）：

```java
@Bean
public ParamFilter myParamFilter() {
    return new ParamFilter() {
        @Override
        public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
            // beanMeta 是正在检索的实体类的元信息, paraMap 是当前的检索参数
            // TODO: 这里可以写一些自定义的参数过滤规则
            return paraMap;      // 返回过滤后的检索参数
        }
    };
}
```

#### 其它项目，配置方法：

```java
DefaultParamResolver paramResolver = new DefaultParamResolver();
// 添加参数过滤器
paramResolver.setParamFilters(new ParamFilter[] { 
    new MyParamFilter1(),
    new MyParamFilter2(),
});
// 构建 Map 检索器
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .paramResolver(paramResolver)   // BeanSearcher 检索器也同此配置
        .build();
```

## 字段转换器

字段转换器用于将从数据库查询出来的值转换成我们需要值。例如：数据库查询出来的值时一个 `Integer` 类型的数，而我们需要的是一个 `Long` 类型的值。

Bean Searcher 提供了 **7** 个字段转换器：

### NumberFieldConvertor

> since v3.0.0

> 只对 `BeanSearcher` 检索器有效

该转换器提供 `Integer`、`int`、`Long`、`long`、`Float`、`float`、`Double`、`double`、`Short`、`short`、`Byte`、`byte` 之间的相互转换。

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v3.1.0 +** 的依赖，**无需任何配置**，自动生效。如需 **关闭** 该转换器，则需 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-number = false
```

若使用 **v3.0.x** 版本的 `bean-searcher-boot-starter` 依赖，开启则需要配置一个 Bean:

```java
@Bean
public NumberFieldConvertor numberFieldConvertor() {
    return new NumberFieldConvertor();
}
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="com.ejlchina.searcher.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.NumberFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="com.ejlchina.searcher.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new NumberFieldConvertor());         // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### StrNumFieldConvertor

> since v3.0.0

> 只对 `BeanSearcher` 检索器有效

该转换器提供 `String` 到 `Integer | int | Long | long | Float | float | Double | double | Short | short | Byte | byte` 方向的类型转换。

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v3.1.0 +** 的依赖，**无需任何配置**，自动生效。如需 **关闭** 该转换器，则需 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-str-num = false
```

若使用 **v3.0.x** 版本的 `bean-searcher-boot-starter` 依赖，开启则需要配置一个 Bean:

```java
@Bean
public StrNumFieldConvertor strNumFieldConvertor() {
    return new StrNumFieldConvertor();
}
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="com.ejlchina.searcher.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.StrNumFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="com.ejlchina.searcher.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new StrNumFieldConvertor());         // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### BoolNumFieldConvertor

> since v3.6.1

> 只对 `BeanSearcher` 检索器有效

该转换器可将 `Boolean` 类型到 `Integer | int | Long | long | Short | short | Byte | byte` 方向的转换。

> See: https://github.com/troyzhxu/bean-searcher/issues/33

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v3.6.1 +** 的依赖，**无需任何配置**，自动生效。

如需 **关闭** 该转换器，可在 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-bool-num = false
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="com.ejlchina.searcher.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.BoolNumFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="com.ejlchina.searcher.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new BoolNumFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### BoolFieldConvertor

> since v3.0.0

> 只对 `BeanSearcher` 检索器有效

该转换器提供 `String | Number` 到 `Boolean | boolean` 方向的类型转换，自 `v3.1.4`、`v3.2.3`、`v3.3.2` 起，也开始支持  `Boolean -> boolean` 方向的转换。

对于 **数值类型**，它默认将 `0` 转换为 `false`，非 `0` 转换为 `true`。对于 **String 类型**，它默认将 `"0" | "OFF" | "FALSE" | "N" | "NO" | "F"`（不区分大小写）转换为 `false`，其它转换为 `true`。另外，它还提供了一个添加将哪些字符串值转换为 `false` 的方法，如：

```java
BoolFieldConvertor convertor = new BoolFieldConvertor();
// 添加将数据库中的字符串值 "Nothing" 转换为 false，同样不区分大小写
convertor.addFalseValues(new String[] { "Nothing" });
```

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v3.1.0 +** 的依赖，**无需任何配置**，自动生效。如需 **关闭** 该转换器，则需 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-bool = false
```

若使用 **v3.0.x** 版本的 `bean-searcher-boot-starter` 依赖，开启则需要配置一个 Bean:

```java
@Bean
public BoolFieldConvertor boolFieldConvertor() {
    return new BoolFieldConvertor();
}
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="com.ejlchina.searcher.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.BoolFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="com.ejlchina.searcher.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new BoolFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### DateFieldConvertor

> since v3.1.0

> 只对 `BeanSearcher` 检索器有效

该转换器提供 `Date`、`java.sql.Date`、`java.sql.Timestamp`、`LocalDateTime`、`LocalDate` 之间的相互转换。它还支持设置时区（不设置使用系统默认时区），例如：

```java
DateFieldConvertor convertor = new DateFieldConvertor();
convertor.setZoneId(ZoneId.of("Asia/Shanghai"));
```

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v3.1.0 +** 的依赖，**无需任何配置**，自动生效。如需修改时区，只需要在 `application.properties` 中添加：

```properties
# 该配置与 DateFormatFieldConvertor 共用
bean-searcher.field-convertor.zone-id = Asia/Shanghai
```

如需 **关闭** 该转换器，则需 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-date = false
```

若使用 **v3.0.x** 版本的 `bean-searcher-boot-starter` 依赖，开启则需要配置一个 Bean:

```java
@Bean
public DateFieldConvertor dateFieldConvertor() {
    return new DateFieldConvertor();
}
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="com.ejlchina.searcher.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.DateFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="com.ejlchina.searcher.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new DateFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### DateFormatFieldConvertor

> since v3.1.0

> 只对 `MapSearcher` 检索器有效

该转换器可以将 `Date`、`java.sql.Date`、`java.sql.Timestamp`、`LocalDateTime`、`LocalDate`、`LocalTime`、`java.sql.Time` 类型的字段值 **格式化为字符串**。它供了一个 **非常强大** 的 `setFormat(String scope, String format)` 方法，它支持 **按范围** 设置 **多个日期格式**（范围越精确，其格式的使用优先级就越高），例如：

```java
DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
// 设置 com.example.sbean 包下的所有日期字段使用 yyyy-MM-dd HH:mm 格式
convertor.setFormat("com.example.sbean", "yyyy-MM-dd HH:mm");
// 设置 com.example.sbean 包下的 LocalTime 类型的字段日期字段使用 HH:mm 格式
convertor.setFormat("com.example.sbean:LocalTime", "HH:mm");
// 设置 com.example.sbean 包下的 LocalDate 类型的字段日期字段使用 yyyy-MM-dd 格式
convertor.setFormat("com.example.sbean:LocalDate", "yyyy-MM-dd");
// 设置 com.example.sbean.User 实体类的所有日期字段使用 yyyy-MM-dd HH:mm:ss 格式
convertor.setFormat("com.example.sbean.User", "yyyy-MM-dd HH:mm:ss");
// 设置 com.example.sbean.User 实体类的 Date 类型的字段使用 yyyy-MM-dd HH 格式
convertor.setFormat("com.example.sbean.User:Date", "yyyy-MM-dd HH");
// 设置 com.example.sbean.User 实体类的 createDate 字段使用 yyyy-MM-dd 格式
convertor.setFormat("com.example.sbean.User.createDate", "yyyy-MM-dd");
```

::: tip 注意
上文形如 `com.example.sbean:LocalTime` 与 `com.example.sbean.User:Date` 的范围中 `:` 后的类型限定符，限定的 **并不是指实体类中声明的字段类型**，而是 **`SqlExecutor` 查出来的数据类型**（即：默认是 Jdbc 返回的原生字段类型）。
:::

另外，它也支持设置时区（不设置使用系统默认时区），如：

```java
DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
convertor.setZoneId(ZoneId.of("Asia/Shanghai"));
```

#### 启用效果

当某个 日期/时间 类型的字段给格式化后，`MapSearcher` 检索器检索结果 `Map` 对象中的该字段就不再是 日期/时间 类型，而是已经格式化后的字符串。值类型发生了改变。

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v3.1.0 +** 的依赖，只需要在 `application.properties` 配置相关的日期格式即可：

```properties
bean-searcher.field-convertor.date-formats[com.example] = yyyy-MM-dd HH:mm      # 中括号内的是该格式的生效范围
bean-searcher.field-convertor.date-formats[com.example.sbean] = yyyy-MM-dd HH
bean-searcher.field-convertor.date-formats[com.example.sbean\:Date] = yyyy-MM-dd
bean-searcher.field-convertor.date-formats[com.example.sbean.Employee] = yyyy-MM
bean-searcher.field-convertor.date-formats[com.example.sbean.Employee\:Date] = yyyy/MM/dd
bean-searcher.field-convertor.date-formats[com.example.sbean.Employee.entryDate] = yyyy-MM-dd HH:mm
# 如果需要修改时区，可以在添加这个配置（该配置与 DateFieldConvertor 共用）
bean-searcher.field-convertor.zone-id = Asia/Shanghai
```

如果使的是 `application.yml`，则如下配置：

```yaml
bean-searcher:
  field-convertor:
    date-formats[com.example]: yyyy-MM-dd HH:mm
    date-formats[com.example.sbean]: yyyy-MM-dd HH
    date-formats[com.example.sbean:Date]: yyyy-MM-dd
    date-formats[com.example.sbean.Employee]: yyyy-MM
    date-formats[com.example.sbean.Employee:Date]: yyyy/MM/dd
    date-formats[com.example.sbean.Employee.entryDate]: yyyy-MM-dd HH:mm
```

如果您用的是 使用 `bean-searcher-boot-starter` **v4.0.0 +** 的依赖，在 `application.yml` 中也可以如下配置：

```yaml
bean-searcher:
  field-convertor:
    date-formats:
      com.example: yyyy-MM-dd HH:mm
      com.example.sbean: yyyy-MM-dd HH
      # 键里使用了 减号 代替了 冒号
      com.example.sbean-Date: yyyy-MM-dd
      com.example.sbean.Employee: yyyy-MM
      com.example.sbean.Employee-Date: yyyy/MM/dd
      com.example.sbean.Employee.entryDate: yyyy-MM-dd HH:mm
```

如需 **关闭** 该转换器，则需 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-date-format = false
```

若使用 **v3.0.x** 版本的 `bean-searcher-boot-starter` 依赖，开启则需要配置一个 Bean:

```java
@Bean
public DateFormatFieldConvertor dateFormatFieldConvertor() {
    DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
    convertor.setFormat("com.example.sbean", "yyyy-MM-dd HH:mm");
    return convertor;
}
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.DateFormatFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
```

使用 xml 注册的方式，不太方便在 Bean 初始化时调用其 `setFormat(String scope, String format)` 方法，我们可以在项目启动监听里拿到 `DateFormatFieldConvertor` 类型的 Bean 再调用它的 `setFormat` 方法设置格式。

* Others

```java
DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
convertor.setFormat("com.example.sbean", "yyyy-MM-dd HH:mm");
// 构建 MapSearcher 检索器
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性的配置
        .addFieldConvertor(convertor)
        .build();
```

### EnumFieldConvertor

> since v3.2.0 

> 只对 `BeanSearcher` 检索器有效

该转换器提供 **`String | Integer | int -> Enum`** 方向的类型转换（`v3.7.0` 后支持 `Integer | int` 转换为枚举，根据枚举序号转换）。

例如，当数据库有一个 `VARCHAR` 类型的字段 `gender`（性别），存储 `Male` 值表示男性，`Female` 值表示女性时，则可以定义一个枚举类：

```java
public enum Gender {
    Male, Female
}
```

然后再实体类中将之声明为对应属性的类型：

```java
public class User {
    private Gender gender;
    // 省略其它...
}
```

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v3.2.0 +** 的依赖，**无需任何配置**，自动生效。如需 **关闭** 该转换器，则需 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-enum = false          # 是否启用该转换器，默认 true
bean-searcher.field-convertor.enum-fail-on-error = true # 遇到非法值无法转换时是否报错，默认 true（since v3.7.0）
bean-searcher.field-convertor.enum-ignore-case = false  # 字符串值转换为枚举时是否忽略大小写，默认 false（since v3.7.0）
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="com.ejlchina.searcher.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.EnumFieldConvertor">
                <property name="failOnError" value="true">  <!-- 遇到非法值无法转换时是否报错（since v3.7.0） -->
                <property name="ignoreCase" value="false">  <!-- 字符串值转换为枚举时是否忽略大小写（since v3.7.0） -->
            </bean>
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="com.ejlchina.searcher.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new EnumFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### TimeFieldConvertor

> since v3.5.0

> 只对 `BeanSearcher` 检索器有效

该转换器支持 `java.sql.Time`、`LocalTime` 之间的相互转换：

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v3.5.0 +** 的依赖，**无需任何配置**，自动生效。

如需 **关闭** 该转换器，可在 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-time = false
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="com.ejlchina.searcher.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.TimeFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="com.ejlchina.searcher.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new TimeFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### B2MFieldConvertor

> since v3.6.0

> 只对 `MapSearcher` 检索器有效

该转换器可以把只对 `BeanSearcher` 检索器有效的 `BFieldConvertor` 组合成一个 `MFieldConvertor`，从而对 `MapSearcher` 也起作用。

#### 启用效果

未启用时，可以让 `MapSearcher` 检索器的检索结果的 值类型 与 实体类 中声明的字段类型 **可能不一致**。比如实体类中声明的是 `Long` 类型，而检索结果的 `Map` 对象里的值可能是 `Integer`（由 数据库列类型 与 JDBC 驱动决定）类型。

启用该转换器后，可以让 `MapSearcher` 检索器的检索结果的 值类型 与 实体类 中声明的字段类型 **保持一致**。

::: tip 注意
当启用 [DateFormatFieldConvertor](/guide/v3/advance.html#dateformatfieldconvertor)，并且 某 日期/时间 类型的字段 在它指定的格式化范围内 时，则该字段仍会被格式化为字符串，从而与实体类种声明的 日期/时间 类型 不再保持一致。
:::

为了性能考虑，**默认未启用** 该转换器，用户可以根据自己的业务需求决定是否启用它。

#### 配置方法

* SpringBoot / Grails 项目

使用 `bean-searcher-boot-starter`（v3.6.0+）依赖时，可在 `application.properties` 中添加一下配置即可启用：

```properties
bean-searcher.field-convertor.use-b2-m = true
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.B2MFieldConvertor">
                <constructor-arg>
                    <list>
                        <bean class="com.ejlchina.searcher.implement.NumberFieldConvertor" />
                        <bean class="com.ejlchina.searcher.implement.StrNumFieldConvertor" />
                        <bean class="com.ejlchina.searcher.implement.BoolNumFieldConvertor" />
                        <bean class="com.ejlchina.searcher.implement.BoolFieldConvertor" />
                        <bean class="com.ejlchina.searcher.implement.DateFieldConvertor" />
                        <bean class="com.ejlchina.searcher.implement.EnumFieldConvertor" />
                        <bean class="com.ejlchina.searcher.implement.TimeFieldConvertor" />
                    <list>
                </constructor-arg>
            </bean>
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
```

* Others

```java
List<BFieldConvertor> convertors = new ArrayList<>();
convertors.add(new NumberFieldConvertor());
convertors.add(new StrNumFieldConvertor());
convertors.add(new BoolNumFieldConvertor());
convertors.add(new BoolFieldConvertor());
convertors.add(new DateFieldConvertor());
convertors.add(new EnumFieldConvertor());
convertors.add(new TimeFieldConvertor());
// 构建 MapSearcher 检索器
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性的配置
        .addFieldConvertor(new B2MFieldConvertor(convertors))
        .build();
```

### 自定义转换器

若以上自带的转换器都无法满足您的需求，您可以通过自定义转换器来实现您的特殊需求。自定义转换器只需要实现以下接口即可：

* `BFieldConvertor`（实现则支持 `BeanSearcher` 检索器）
* `MFieldConvertor`（实现则支持 `MapSearcher` 检索器）

这俩接口都只需实现两个方法：

* `boolean supports(FieldMeta meta, Class<?> valueType)` - 判断该转换器支持的实体类属性类型与数据库值的类型
* `Object convert(FieldMeta meta, Object value)` - 转换操作，将 value 值转换为 meta 指定的字段类型值

具体编码可参考自带的转换器的源码实现：

* [`BoolFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/implement/BoolFieldConvertor.java)
* [`DateFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/implement/DateFieldConvertor.java)
* [`DateFormatFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/implement/DateFormatFieldConvertor.java)
* [`EnumFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/implement/EnumFieldConvertor.java)
* [`NumberFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/implement/NumberFieldConvertor.java)
* [`StrNumFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/implement/StrNumFieldConvertor.java)
* [`TimeFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/implement/TimeFieldConvertor.java)

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` 依赖，自定义好转换器后，只需将之声明为 Spring 的 Bean 即可：

```java
@Bean
public MyFieldConvertor myFieldConvertor() {
    return new MyFieldConvertor();
}
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="com.ejlchina.searcher.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.ejlchina.searcher.implement.MyFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="com.ejlchina.searcher.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new MyFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

## 玩转运算符（since v3.3.0）

自 `v3.3.0` 起，Bean Searcher 的 [字段运算符](/guide/v3/params.html#%E5%AD%97%E6%AE%B5%E8%BF%90%E7%AE%97%E7%AC%A6) 支持高度扩展与自定义。

### 添加新的字段运算符

在 SpringBoot / Grails 项目中，若使用 `bean-searcher-boot-starter` 依赖，则只需实现 [`FieldOp`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/FieldOp.java) 接口，并将之声明为一个 Spring Bean 即可。

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

* /user/index ? **age-op=io**  （套用 [起步](/guide/v3/start.html#开始检索) 章节中的例子）
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
https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher/src/main/java/com/troyzhxu/searcher/operator
:::

### 定义全新运算符体系

如果你 **不喜欢** Bean Searcher [内置的一套字段运算符](/guide/v3/params.html#%E5%AD%97%E6%AE%B5%E8%BF%90%E7%AE%97%E7%AC%A6)，你可以轻松的将它们 **都换掉**。

#### SpringBoot / Grails 项目（使用 bean-searcher-boot-starter 依赖）

只需声明一个 [`FieldOpPool`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/FieldOpPool.java) 类型的 Bean 即可：

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

#### 非 Boot 的 Spring 项目

```xml
<bean id="fieldOpPool" class="com.ejlchina.searcher.FieldOpPool">
    <property name="fieldOps">
        <list>
            <bean class="com.demo.MyOp1">
            <bean class="com.demo.MyOp2">
            <bean class="com.demo.MyOp3">
            <!-- 需要使用的自定义运算符都放在这里，也可以添加 Bean Searcher 自带的运算符 -->
            <bean class="com.ejlchina.searcher.operator.Equal">
        </list>
    </property>
</bean>
<bean id="paramResolver" class="com.ejlchina.searcher.implement.DefaultParamResolver">
    <property name="fieldOpPool" ref="fieldOpPool" />
</bean>
<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="paramResolver" ref="paramResolver" />
</bean>
```

#### 其它项目

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

## SQL 拦截器

Bean Searcher 支持配置 多个 SQL 拦截器 来自定义修改 SQL 的生成规则。

### SqlInterceptor

```java
/**
 * Sql 拦截器
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.0.0
 */
public interface SqlInterceptor {

    /**
     * Sql 拦截
     * @param <T> 泛型
     * @param searchSql 检索 SQL 信息（非空）
     * @param paraMap 原始检索参数（非空）
     * @param fetchType 检索类型（v3.6.0 新增参数）
     * @return 新的检索 SQL（非空）
     */
    <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType);

}
```

在 SQL 拦截器中，我们可以对 `SearchSql` 进行修改，来实现我们自定义的逻辑。

* 案例：[使用 SQL 拦截器 实现 多字段排序](https://github.com/troyzhxu/bean-searcher/issues/9)（自 `v3.4.0` 起，框架已内置 [多字段排序](/guide/v3/params.html#多字段排序（since-v3-4）) 功能）。

### 配置（SpringBoot / Grails）

在 SpringBoot / Grails 项目中，使用 `bean-searcher-boot-starter` 依赖时，只需要把定义好的 `SqlInterceptor` 声明为一个 Bean 即可：

```java
@Bean
public SqlInterceptor myFitstSqlInterceptor() {
    return new MyFitstSqlInterceptor();
}

@Bean
public SqlInterceptor mySecondSqlInterceptor() {
    return new MySecondSqlInterceptor();
}
```

### 配置（非 Boot 的 Spring 项目）

```xml
<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="interceptors">
        <list>
            <bean class="com.example.MyFitstSqlInterceptor" />
            <bean class="com.example.MySecondSqlInterceptor" />
        </list>
    </property>
</bean>
```

### 配置（Others）

```java
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .addInterceptor(new MyFitstSqlInterceptor())
        .addInterceptor(new MySecondSqlInterceptor())
        .build();
```

## SQL 方言（Dialect）

Bean Searcher 可以为我们自动生成完整的 SQL 语句，但对应不同的数据库，SQL 语法可能略有不同。为此，Bean Searcher 使用方言（Dialect）来扩展支持这些不同的数据库。

### 方言实现

Bean Searcher 自带四种 Dialect 实现：

* [`MySqlDialect`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/dialect/MySqlDialect.java) - **默认方言**，可用于 类 MySql 的数据库
* [`OracleDialect`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/dialect/OracleDialect.java) - 可用于 类 Oracle 的数据库
* [`PostgreSqlDialect`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/dialect/PostgreSqlDialect.java) - 可用于 类 PostgreSqlDialect 的数据库（**since v3.6.0**）
* [`SqlServerDialect`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/dialect/SqlServerDialect.java) - 可用于 类 SqlServer (v2012+) 的数据库（**since v3.7.0**）
* 其它数据库可自定义 Dialect，可 [参考 MySqlDialect 的实现](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/dialect/MySqlDialect.java)

::: tip Bean Searcher 中的方言很简单
* 自 **v3.3.0** 起，它被简化，只需实现 **两个** 方法即可；
* 自 **v3.7.0** 起，再被简化，只需实现 **一个** 方法即可。
:::

### 配置方法

以下介绍各框架下的方言配置。

#### SpringBoot / Grails

使用 `bean-searcher-boot-starter` 依赖时，如需切换 Bean Searcher 自带的方言，则可通过以下配置项来指定：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.dialect` | SQL 方言 | `MySQL`、`Oracle`、`PostgreSQL`、`SqlServer` | `MySQL`

自定义的方言，只需将之注册为 Bean 即可：

```java
@Bean
public Dialect myDialect() {
    return new MyDialect();
}
```

#### 非 Boot 的 Spring 项目

```xml
<!-- 定义 Oracle 方言 -->
<bean id="dialect" class="com.ejlchina.searcher.dialect.MyDialect" />

<!-- v3.3 起需要配置运算符池 -->
<bean id="fieldOpPool" class="com.ejlchina.searcher.FieldOpPool" 
    p:dialect-ref="dialect" />

<bean id="paramResolver" class="com.ejlchina.searcher.implement.DefaultParamResolver" 
    p:fieldOpPool-ref="fieldOpPool" />

<bean id="sqlResolver" class="com.ejlchina.searcher.implement.DefaultSqlResolver" 
    p:dialect-ref="dialect" />

<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="paramResolver" ref="paramResolver" />
    <property name="sqlResolver" ref="sqlResolver" />
</bean>
```

#### Others

```java
Dialect dialect = new MyDialect();
// v3.3 起需要配置运算符池
FieldOpPool fieldOpPool = new FieldOpPool();
fieldOpPool.setDialect(dialect);    // 配置使用 Oracle 方言
DefaultParamResolver paramResolver = new DefaultParamResolver();
paramResolver.setFieldOpPool(fieldOpPool);
DefaultSqlResolver sqlResolver = new DefaultSqlResolver();
sqlResolver.setDialect(dialect);    // 配置使用 Oracle 方言
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .paramResolver(paramResolver)
        .sqlResolver(sqlResolver)
        .build();
```

## 慢 SQL 日志与监听（since v3.7.0）

自 `v3.7.0` 起 Bean Searcher 提供了慢 SQL 日志与 监听功能。

### 慢 SQL 阈值

慢 SQL 阈值指的是慢 SQL 的最小执行耗时，它是判断一个 SQL 是否为慢 SQL 的标准，单位 `ms`，默认值为 `500`。当然也可以通过配置修改。

#### SpringBoot / Grails 配置项（使用 `bean-searcher-boot-starter` 依赖）

配置键名 | 含义 | 类型 | 默认值
-|-|-|-
`bean-searcher.sql.slow-sql-threshol` | 慢 SQL 阈值（单位：毫秒） | `int` | `500`

#### 非 Boot 的 Spring 配置方法（使用 `bean-searcher` 依赖）

```xml
<bean id="sqlExecutor" class="com.ejlchina.searcher.implement.DefaultSqlExecutor">
    <property name="dataSource" ref="dataSource" />
    <!-- 配置慢 SQL 阈值 -->
    <property name="slowSqlThreshold" value="500" />
</bean>
<!-- 声明 MapSearcher 检索器，它查询的结果是 Map 对象 -->
<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="sqlExecutor" ref="sqlExecutor" />
</bean>
```

#### Others

```java
DefaultSqlExecutor sqlExecutor = new DefaultSqlExecutor(getDefaultDataSource());
// 配置慢 SQL 阈值
sqlExecutor.setSlowSqlThreshold(500);
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .sqlExecutor(sqlExecutor)
        .build();
```

### 开启慢 SQL 日志

慢 SQL 的日志级别为 `WARN`，所以，只需将 `com.ejlchina.searcher.implement.DefaultSqlExecutor` 的日志级别调整为 `WARN | INFO | DEBUG` 即可开启。

日志效果（`执行耗时`、`SQL`、`执行参数`、`实体类`）：

```log
14:55:02.151 WARN - bean-searcher [600ms] slow-sql: [select count(*) s_count from employee e where (e.type = ?)] params: [1] on [com.example.sbean.Employee]
```

参考：[起步 > 使用 > SQL 日志](/guide/v3/start.html#sql-日志) 章节。

### 监听慢 SQL 事件

有时候我们需要在代码中监听慢 SQL 事件，以便做进一步的自定义处理（比如：发送警告通知）。

#### SpringBoot / Grails（使用 `bean-searcher-boot-starter` 依赖）只需配置一个 Bean 即可

```java
@Bean
public SqlExecutor.SlowListener slowSqlListener() {
    return (
        Class<?> beanClass,     // 发生慢 SQL 的实体类 
        String slowSql,         // 慢 SQL 字符串
        List<Object> params,    // SQL 执行参数
        long timeCost           // 执行耗时（单位：ms）
    ) -> {
        // TODO: 监听处理
    }
}
```

#### 非 Boot 的 Spring 项目

```xml
<bean id="sqlExecutor" class="com.ejlchina.searcher.implement.DefaultSqlExecutor">
    <property name="dataSource" ref="dataSource" />
    <!-- 配置 慢 SQL 监听器 -->
    <property name="slowListener">
        <!-- 自定义 MySlowSqlListener 实现 SqlExecutor.SlowListener 接口 -->
        <bean class="com.example.MySlowSqlListener" />
    </property>
</bean>
<!-- 声明 MapSearcher 检索器，它查询的结果是 Map 对象 -->
<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="sqlExecutor" ref="sqlExecutor" />
</bean>
```

#### Others

```java
DefaultSqlExecutor sqlExecutor = new DefaultSqlExecutor(getDefaultDataSource());
// 配置慢 SQL 监听器
sqlExecutor.setSlowListener((
    Class<?> beanClass,     // 发生慢 SQL 的实体类 
    String slowSql,         // 慢 SQL 字符串
    List<Object> params,    // SQL 执行参数
    long timeCost           // 执行耗时（单位：ms）
) -> {
    // TODO: 监听处理
});
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .sqlExecutor(sqlExecutor)
        .build();
```

## 多数据源

Bean Searcher 支持多数据源。

### 静态数据源

我们可在 `@SearchBean` 注解中为每个实体类指定不同的数据源，例如，指定 User 实体类来自 userDs 数据源：

```java
@SearchBean(dataSource="userDs")
public class User {
    // 省略其它代码
}
```

指定 Order 实体类来自 orderDs 数据源：

```java
@SearchBean(dataSource="orderDs")
public class Order {
    // 省略其它代码
}
```

### 配置（SpringBoot 为例）

首先在配置文件 `application.properties` 中配置数据源信息:

```properties
# 默认数据源
spring.datasource.url = jdbc:h2:~/test
spring.datasource.driverClassName = org.h2.Driver
spring.datasource.username = sa
spring.datasource.password = 123456
# user 数据源
spring.datasource.user.url = jdbc:h2:~/user
spring.datasource.user.driverClassName = org.h2.Driver
spring.datasource.user.username = sa
spring.datasource.user.password = 123456
# order 数据源
spring.datasource.order.url = jdbc:h2:~/order
spring.datasource.order.driverClassName = org.h2.Driver
spring.datasource.order.username = sa
spring.datasource.order.password = 123456
```

然后配置以下一些 Bean 即可:

```java
// user 数据源配置信息
@Bean(name = "userDsProps")
@ConfigurationProperties(prefix = "spring.datasource.user")
public DataSourceProperties userDsProps() {
    return new DataSourceProperties();
}

// order 数据源配置信息
@Bean(name = "orderDsProps")
@ConfigurationProperties(prefix = "spring.datasource.order")
public DataSourceProperties orderDsProps() {
    return new DataSourceProperties();
}

@Bean
public NamedDataSource userNamedDataSource(@Qualifier("userDsProps") DataSourceProperties dataSourceProperties) {
    DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
    // 具名数据源：com.ejlchina.searcher.boot.NamedDataSource
    return new NamedDataSource("userDs", dataSource);
}

@Bean
public NamedDataSource orderNamedDataSource(@Qualifier("orderDsProps") DataSourceProperties dataSourceProperties) {    
    DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
    // 具名数据源：com.ejlchina.searcher.boot.NamedDataSource
    return new NamedDataSource("orderDs", dataSource);
}
```

### 动态数据源

上述配置的多数据源对单个 SearchBean 而言都是静态的，即某个实体类与某个数据源之间的关系是注解里指定死的。如果你开发的项目是 SAAS 模式，要求同一个实体类对不同的 Tenant（租户）使用不同数据源。则可以使用本节所讲的动态数据源。

要使用动态数据源，首先定义个 `DynamicDatasource`:

```java
public class DynamicDatasource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // 可以在拦截器中使用 ThreadLocal 记录当前租户信息
        // 然后在这里从 ThreadLocal 中取出
        return "当前租户编号";      // 返回当前租户编号
    }

}
```

然后，配置一个动态数据源（以 SpringBoot 项目为例）：

```java
// 把 DynamicDatasource 注册为一个 Bean
@Bean
public DataSource dynamicDatasource() {
    DynamicDatasource dynamicDatasource = new DynamicDatasource();
    dynamicDatasource.setTargetDataSources(getAllDataSources());
    return dynamicDatasource;
}

private Map<Object, Object> getAllDataSources() {
    Map<Object, Object> dataSources = new HashMap<>();
    dataSources.put("租户1编号", new DataSource1());
    dataSources.put("租户2编号", new DataSource2());
    // 把所有数据源都放进 dataSources 里
    return dataSources;
}
```

## 结果过滤器（v3.6.0）

有时可能想对 Bean Searcher 的检索结果做进一步的处理，这时候可以使用 `ResultFilter` 来处理。

### ResultFilter

它是一个接口，共定义了两个方法，它分别作用于 `BeanSearcher` 与 `MapSearcher` 检索器：

```java
public interface ResultFilter {

    /**
     * ResultFilter
     * 对 {@link BeanSearcher } 的检索结果做进一步转换处理
     * @param result 检索结果
     * @param beanMeta 检索实体类的元信息
     * @param paraMap 检索参数
     * @param fetchType 检索类型
     * @param <T> 泛型
     * @return 转换后的检索结果
     */
    default <T> SearchResult<T> doBeanFilter(SearchResult<T> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return result;
    }

    /**
     * 对 {@link MapSearcher } 的检索结果做进一步转换处理
     * @param result 检索结果
     * @param beanMeta 检索实体类的元信息
     * @param paraMap 检索参数
     * @param fetchType 检索类型
     * @param <T> 泛型
     * @return 转换后的检索结果
     */
    default <T> SearchResult<Map<String, Object>> doMapFilter(SearchResult<Map<String, Object>> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return result;
    }

}
```

### 配置（SpringBoot / Grails）

在 SpringBoot / Grails 项目中，使用 `bean-searcher-boot-starter` 依赖时，只需要把定义好的 `ResultFilter` 声明为一个 Bean 即可：

```java
@Bean
public ResultFilter myFitstResultFilter() {
    return new MyFirstResultFilter();
}

@Bean
public ResultFilter mySecondResultFilter() {
    return new MySecondResultFilter();
}
```

### 配置（非 Boot 的 Spring 项目）

```xml
<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="resultFilters">
        <list>
            <bean class="com.example.FitstResultFilter" />
            <bean class="com.example.SecondResultFilter" />
        </list>
    </property>

</bean>
```

### 配置（Others）

```java
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .addResultFilter(new FitstResultFilter());      // since v3.6.1
        .addResultFilter(new SecondResultFilter());     // since v3.6.1
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .build();
```
