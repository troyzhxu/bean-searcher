# 字段与参数转换器

## 字段转换器

字段转换器用于将从数据库查询出来的值转换成我们需要值。例如：数据库查询出来的值时一个 `Integer` 类型的数，而我们需要的是一个 `Long` 类型的值。

Bean Searcher 提供了 **7** 个字段转换器：

### NumberFieldConvertor

> since v3.0.0

> 只对 `BeanSearcher` 检索器有效

该转换器提供 `Integer`、`int`、`Long`、`long`、`Float`、`float`、`Double`、`double`、`Short`、`short`、`Byte`、`byte`、`BigDecimal`（v4.0 开始支持 BigDecimal）之间的相互转换。

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
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.NumberFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
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
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.StrNumFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
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
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.BoolNumFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
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
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.BoolFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
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
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.DateFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
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
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.DateFormatFieldConvertor" />
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
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.EnumFieldConvertor">
                <property name="failOnError" value="true">  <!-- 遇到非法值无法转换时是否报错（since v3.7.0） -->
                <property name="ignoreCase" value="false">  <!-- 字符串值转换为枚举时是否忽略大小写（since v3.7.0） -->
            </bean>
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
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
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.TimeFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
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

### JsonFieldConvertor

> since v4.0.0

> 只对 `BeanSearcher` 检索器有效

该转换器支持 `JSON 字符串` 到 `POJO` 方向的转换，与 SeachBean 字段上的注解 `@DbField(type = DbType.JSON)` 来配合使用。

#### 前提条件

由于涉及到 JSON 转换，不可避免的需要使用 JSON 解析相关的框架，但是不同的开发者可能偏好不同的 JSON 框架，所以该转换器并没有与特定的 JSON 框架绑定，而是支持用户自己选择（目前默认有 5 种框架可选），只需要添加如下特定依赖即可： 

* 使用 Jackson 框架

```groovy
implementation 'cn.zhxu:xjsonkit-jackson:1.4.3'
```

* 使用 Gson 框架

```groovy
implementation 'cn.zhxu:xjsonkit-gson:1.4.3'
```

* 使用 Fastjson 框架

```groovy
implementation 'cn.zhxu:xjsonkit-fastjson:1.4.3'
```

* 使用 Fastjson2 框架

```groovy
implementation 'cn.zhxu:xjsonkit-fastjson2:1.4.3'
```

* 使用 Snack3 框架

```groovy
implementation 'cn.zhxu:xjsonkit-snack3:1.4.3'
```

如果你喜欢的 JSON 解析框架不在其内，也支持自定义底层实现，参考：https://gitee.com/troyzhxu/xjsonkit

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v4.0.0+** 的依赖，**无需任何配置**，自动生效。

如需 **关闭** 该转换器，可在 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-json = false
```

其它配置项：

```properties
# JSON 转换失败时，是否抛出异常，默认 fasle，只打印警告日志
bean-searcher.field-convertor.json-fail-on-error = false
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.JsonFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new JsonFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

#### 使用案例

* 案例 1：**JSON 数组 转 简单 对象**

```java
public class User {
    // 数据库值：{id: 1, name: "管理员"}
    @DbField(type = DbType.JSON)
    private Role role;
    // 省略其它字段...
}
```

* 案例 2：**JSON 数组 转 简单 List**

```java
public class User {
    // 数据库值：["新生","优秀"]
    @DbField(type = DbType.JSON)
    private List<String> tags;
    // 省略其它字段...
}
```

* 案例 3：**JSON 数组 转 复杂 List**（since v4.2.6）

```java
public class User {
    // 数据库值：[{id: 1, name: "管理员"},{id: 2, name: "财务"}]
    @DbField(type = DbType.JSON)
    private List<Role> roles;
    // 省略其它字段...
}
```

### ListFieldConvertor

> since v4.0.0

> 只对 `BeanSearcher` 检索器有效

该转换器支持 `字符串值` 到 `List` 方向的转换，可用于处理轻量的一对多关系。

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v4.0.0+** 的依赖，**无需任何配置**，自动生效。

如需 **关闭** 该转换器，可在 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-list = false
```

其它配置项：

```properties
# List 字符串各项分隔符，默认为一个英文逗号
bean-searcher.field-convertor.list-item-separator = ,
```

* SpringMVC 项目

需要在配置 Bean 的 xml 文件中添加如下配置：

```xml
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.ListFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="beanReflector" ref="beanReflector">
</bean>
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new ListFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

#### 使用案例

* 案例 1：**单表字段值 转 简单List**

数据库 `user` 表有标签 `tags` 字段，其值为逗号分隔的字符串，例如： `新生,优秀`，则 SearhBean 可以设计如下：

```java
public class User {
    // 直接用数组 List 接收用户标签即可
    private List<String> tags;
    // 省略其它字段...
}
```

* 案例 2：**一对多联表 转 简单List**

数据库有 `user(id,..)` 表与其子表 `user_tag(user_id, tag)` 用于存放用户标签，则 SearhBean 可以设计如下：

```java
@SearchBean(tables="user u")
public class User {
    // 用数组 List 接收用户标签
    @DbField("select group_concat(t.tag) from user_tag t where u.id = t.user_id")
    private List<String> tags;
    // 省略其它字段...
}
```

* 案例 3：**多对多联表 转 简单List**

数据库有 `user(id,..)`、`tag(id,name)` 表与其关联表 `user_tag(user_id, tag_id)` 用于存放用户标签，则 SearhBean 可以设计如下：

```java
@SearchBean(tables="user u")
public class User {
    @DbField("select group_concat(t.id) from tag t, user_tag ut where u.id = ut.user_id and ut.tag_id = t.id")
    private List<Integer> tagIds;  // 标签 ID 集合
    @DbField("select group_concat(t.name) from tag t, user_tag ut where u.id = ut.user_id and ut.tag_id = t.id")
    private List<String> tagNames; // 标签 名称 集合
    // 省略其它字段...
}
```

* 案例 4：**多对多联表 转 复杂List**

在 案例3 中，我得到了 `tagIds` 与 `tagNames` 两个简单的 List 字段，现在我们把它合成一个复杂点的 List，首先定义一个包含 id 与 name 字段的简单标签类：

```java
public class Tag {
    private int id;
    private String name;
    // 省略 Geter 与 Setter
}
```

然后 SearhBean 设计如下：

```java
@SearchBean(tables="user u")
public class User {
    // 将标签的 id 与 name 全部查出，用冒号分隔
    @DbField("select group_concat(concat(t.id,':',t.name)) from tag t, user_tag ut where u.id = ut.user_id and ut.tag_id = t.id")
    private List<Tag> tags; // 标签对象集合
    // 省略其它字段...
}
```

最后，需要定义一个列表项转换器，并将其声明为一个 Bean 即可：

```java
@Component
public class TagConvertor implements ListFieldConvertor.Convertor<Tag> {
    public Tag convert(String value) {
        String[] vs = value.split(":"); // 根据冒号拆分
        int tagId = Integer.parseInt(vs[0]);
        String tagName = vs[1];
        return new Tag(tagId, tagName);
    }
}
```

### B2MFieldConvertor

> since v3.6.0

> 只对 `MapSearcher` 检索器有效

该转换器可以把只对 `BeanSearcher` 检索器有效的 `BFieldConvertor` 组合成一个 `MFieldConvertor`，从而对 `MapSearcher` 也起作用。

#### 启用效果

未启用时，`MapSearcher` 检索器的检索结果的 值类型 与 实体类 中声明的字段类型 **可能不一致**。比如实体类中声明的是 `Long` 类型，而检索结果的 `Map` 对象里的值可能是 `Integer`（由 数据库列类型 与 JDBC 驱动决定）类型。

启用该转换器后，可以让 `MapSearcher` 检索器的检索结果的 值类型 与 实体类 中声明的字段类型 **保持一致**。

::: tip 注意
当启用 [DateFormatFieldConvertor](/guide/latest/advance.html#dateformatfieldconvertor)，并且 某 日期/时间 类型的字段 在它指定的格式化范围内 时，则该字段仍会被格式化为字符串，从而与实体类种声明的 日期/时间 类型 不再保持一致。
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
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性的配置 -->
    <property name="convertors">
        <list>
            <bean class="cn.zhxu.bs.convertor.B2MFieldConvertor">
                <constructor-arg>
                    <list>
                        <bean class="cn.zhxu.bs.convertor.NumberFieldConvertor" />
                        <bean class="cn.zhxu.bs.convertor.StrNumFieldConvertor" />
                        <bean class="cn.zhxu.bs.convertor.BoolNumFieldConvertor" />
                        <bean class="cn.zhxu.bs.convertor.BoolFieldConvertor" />
                        <bean class="cn.zhxu.bs.convertor.DateFieldConvertor" />
                        <bean class="cn.zhxu.bs.convertor.EnumFieldConvertor" />
                        <bean class="cn.zhxu.bs.convertor.TimeFieldConvertor" />
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

## 参数转换器

待完善...

## 自定义转换器

若以上自带的转换器都无法满足您的需求，您可以通过自定义转换器来实现您的特殊需求。自定义转换器只需要实现以下接口即可：

* `BFieldConvertor`（实现则支持 `BeanSearcher` 检索器）
* `MFieldConvertor`（实现则支持 `MapSearcher` 检索器）

这俩接口都只需实现两个方法：

* `boolean supports(FieldMeta meta, Class<?> valueType)` - 判断该转换器支持的实体类属性类型与数据库值的类型
* `Object convert(FieldMeta meta, Object value)` - 转换操作，将 value 值转换为 meta 指定的字段类型值

具体编码可参考自带的转换器的源码实现：

* [`BoolFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/BoolFieldConvertor.java)
* [`DateFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/DateFieldConvertor.java)
* [`DateFormatFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/DateFormatFieldConvertor.java)
* [`EnumFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/EnumFieldConvertor.java)
* [`NumberFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/NumberFieldConvertor.java)
* [`StrNumFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/StrNumFieldConvertor.java)
* [`TimeFieldConvertor` 的源码](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/TimeFieldConvertor.java)

### 配置方法

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
<bean id="beanReflector" class="cn.zhxu.bs.implement.DefaultBeanReflector">
    <property name="convertors">
        <list>
            <bean class="com.example.MyFieldConvertor" />
            <!-- 省略其它自段转换器的配置 -->
        <list>
    </property>
</bean>
<bean id="beanSearcher" class="cn.zhxu.bs.implement.DefaultBeanSearcher">
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
