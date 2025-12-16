# 字段与参数转换器

Bean Searcher 在搜索管道的两个不同阶段执行类型转换：

1. **参数转换** - 在执行 SQL 之前，将传入的搜索参数值（通常是字符串）转换为数据库兼容的类型
1. **字段转换** - 在查询执行之后，将数据库 ResultSet 的值转换为 Java bean 属性类型

这些转换之所以必要，是因为：

- HTTP 参数以字符串形式到达，但可能需要查询数字、日期或布尔类型的数据库列
- 不同的数据库对相同逻辑数据返回不同的类型（例如，JSON 可能作为 `String`、`byte[]` 或 `Clob` 返回）
- Java bean 可能使用方便的、不能直接映射到数据库列类型的类型（例如 `List<Integer>`）

## 字段转换器

字段转换器用于解决数据库列类型与 Java Bean 字段类型之间的阻抗不匹配问题。虽然 JDBC 提供了基本的类型映射（例如，`INT` → `Integer`，`VARCHAR` → `String`），但许多场景需要自定义的转换逻辑：

- 存储为字符串/字节的 JSON 列，需要反序列化为 Java 对象
- 需要转换为字符串或集合的 CLOB/TEXT 字段
- 数据库特定的类型表示（例如，字节数组、专有类型）
- 编码了结构化数据的字符串表示（例如，逗号分隔的值）

字段转换器在搜索管道的 `SqlExecutor` 阶段运行，即在数据库返回结果之后，但在用值填充 Bean 之前。

Bean Searcher 提供了多个内置的字段转换器来处理常见的转换场景。

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
implementation 'cn.zhxu:xjsonkit-jackson:1.5.1'
```

* 使用 Gson 框架

```groovy
implementation 'cn.zhxu:xjsonkit-gson:1.5.1'
```

* 使用 Fastjson 框架

```groovy
implementation 'cn.zhxu:xjsonkit-fastjson:1.5.1'
```

* 使用 Fastjson2 框架

```groovy
implementation 'cn.zhxu:xjsonkit-fastjson2:1.5.1'
```

* 使用 Snack3 框架

```groovy
implementation 'cn.zhxu:xjsonkit-snack3:1.5.1'
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

### StringFieldConvertor

> since v4.6.0

> 只对 `BeanSearcher` 检索器有效

该转换器支持 `java.sql.Clob`、`Number`、`Boolean`、`Date`、`LocalDate`、`LocalDateTime` 向 `String` 转换：

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v4.6.0+** 的依赖，**无需任何配置**，自动生效。

如需 **关闭** 该转换器，可在 `application.properties` 中配置：

```properties
bean-searcher.field-convertor.use-string = false
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new StringFieldConvertor());           // 添加转换器
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### OracleTimestampFieldConvertor

> since v4.4.0

> 只对 `BeanSearcher` 检索器有效（用于兼容 Oracle 驱动返回的 TIMESTAMP 类型）

该转换器支持 `oracle.sql.TIMESTAMP` 向 `Instant`、`java.util.Date`、`java.sql.Timestamp`、`LocalDate`、`LocalDateTime` 转换。

#### 配置方法

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` **v4.4.0+** 的依赖，**无需任何配置**，自动生效。

如需 **关闭** 该转换器，可在 `application.properties` 中配置：

```properties
# 若未使用 Oracle 驱动，将之关闭可获得更好的性能
bean-searcher.field-convertor.use-oracle-timestamp = false
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new OracleTimestampFieldConvertor());    // 添加转换器
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

未启用时，`MapSearcher` 检索器的检索结果的 值类型 与 实体类 中声明的字段类型 **可能不一致**。比如实体类中声明的是 `Long` 类型，而检索结果的 `Map` 对象里的值可能是 `Integer`（由 数据库列类型 与 JDBC 驱动决定）类型。

启用该转换器后，可以让 `MapSearcher` 检索器的检索结果的 值类型 与 实体类 中声明的字段类型 **保持一致**。

::: tip 注意
当启用 [DateFormatFieldConvertor](/guide/advance/convertor#dateformatfieldconvertor)，并且 某 日期/时间 类型的字段 在它指定的格式化范围内 时，则该字段仍会被格式化为字符串，从而与实体类种声明的 日期/时间 类型 不再保持一致。
:::

为了性能考虑，**默认未启用** 该转换器，用户可以根据自己的业务需求决定是否启用它。

#### 配置方法

* SpringBoot / Grails 项目

使用 `bean-searcher-boot-starter`（v3.6.0+）依赖时，可在 `application.properties` 中添加一下配置即可启用：

```properties
bean-searcher.field-convertor.use-b2-m = true
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

参数转换器将传入的搜索参数值从输入格式（通常来自HTTP请求的字符串）在执行SQL之前转换为数据库兼容的Java类型。它们是参数处理管道中的关键组件，确保了用户输入和数据库查询之间的类型安全。

参数转换器在参数解析阶段运行，发生在参数过滤器标准化输入之后、SQL生成之前。它们弥合了弱类型的HTTP参数和强类型的数据库列之间的差距。

Bean Searcher提供了六个内置的参数转换器，每个都处理特定的类型转换：

### BoolParamConvertor

> 自 v3.8.0 起

为 `DbType.BOOL` 类型的字段转换布尔值。

#### 支持的输入类型

- `String` → 解释为 true/false
- `Number` → `0` 为 false，非零为 true

#### 字符串到布尔值的映射

转换器使用可配置的 false 值数组：

```java
private String[] falseValues = new String[] { "0", "OFF", "FALSE", "N", "NO", "F" };
```

任何**不**匹配这些值（不区分大小写）的字符串都被视为 `true`。空白字符串返回 `null`。

### NumberParamConvertor

> 自 v3.8.0 起

为具有数字 `DbType`（BYTE、SHORT、INT、LONG、FLOAT、DOUBLE、DECIMAL）的字段转换数字值。

#### 转换逻辑

对于**字符串**输入：

- 空白字符串返回 `null`
- 使用 `Byte.parseByte()`, `Integer.parseInt()`, `Long.parseLong()` 等方法
- 解析失败时抛出 `IllegalParamException`

对于**数字**输入：

- 使用 `Number.byteValue()`, `intValue()`, `longValue()` 等方法
- 处理来自整数和浮点类型的 `BigDecimal` 转换

### DateParamConvertor

> 自 v3.8.0 起

为 `DbType.DATE` 类型的字段转换日期值。

#### 支持的转换

| 输入类型             | 示例                          | 输出                            |
| ---------------- | --------------------------- | ----------------------------- |
| `String`         | `"2023-01-15"`              | `java.sql.Date` 或 `LocalDate` |
| `java.util.Date` | 任何 Date 实例                  | `java.sql.Date` 或 `LocalDate` |
| `LocalDate`      | `LocalDate.of(2023, 1, 15)` | `java.sql.Date` 或 `LocalDate` |
| `LocalDateTime`  | `LocalDateTime.now()`       | `java.sql.Date` 或 `LocalDate` |

#### 主要特性

- 接受 `/` 和 `-` 作为日期分隔符
- 使用正则表达式模式 `[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}` 从字符串中提取日期
- 可配置目标类型：`SQL_DATE`（默认）或 `LOCAL_DATE`
- 通过提取日期部分来转换 `LocalDateTime`

### DateTimeParamConvertor

> 自 v3.8.0 起

为 `DbType.DATETIME` 类型的字段转换日期时间值。

#### 主要特性：

- 解析多种日期时间字符串格式：`yyyy-MM-dd HH:mm:ss.SSS`, `yyyy-MM-dd HH:mm:ss`, `yyyy-MM-dd HH:mm`, `yyyy-MM-dd`
- 接受 `/` 和 `-` 作为日期分隔符
- 将数字字符串作为纪元毫秒数处理
- 可配置目标类型：`SQL_TIMESTAMP`（默认）或 `LOCAL_DATE_TIME`
- 通过可配置的 `TimeZone`/`ZoneId` 进行时区感知的转换

### TimeParamConvertor

> 自 v3.8.0 起

为 `DbType.TIME` 类型的字段转换时间值。

#### 支持的字符串格式：

- `HH:mm:ss`（例如，`"14:30:00"`）
- `HH:mm`（例如，`"14:30"` → 秒数默认为 `00`）

#### 支持的类型：

- `String` → `java.sql.Time` 或 `LocalTime`
- `LocalTime` → `java.sql.Time` 或 `LocalTime`
- `java.sql.Time` → `LocalTime`（当目标为 `LOCAL_TIME` 时）

### EnumParamConvertor

> 自 v4.2.1 起

为 Java 类型是 Enum 子类的字段转换枚举值。

#### 转换规则：

| 输入                    | DbType   | 输出                 | 逻辑           |
| --------------------- | -------- | ------------------ | ------------ |
| `String: "ACTIVE"`    | `INT`    | `Integer: 0`       | 匹配枚举名称，返回其序数 |
| `String: "0"`         | `INT`    | `Integer: 0`       | 将数字字符串解析为序数  |
| `Enum: Status.ACTIVE` | `INT`    | `Integer: 0`       | 返回枚举的序数      |
| `Enum: Status.ACTIVE` | `STRING` | `String: "ACTIVE"` | 返回枚举的名称      |

#### 关键实现细节：

`supports()` 方法检查：

1. 目标类型可赋值给 `Enum.class`
1. DbType 是 `INT` 或 `STRING`
1. 值的类型是 `String` 或目标枚举类型

`convert()` 方法通过以下方式处理字符串输入：

1. 尝试进行不区分大小写的枚举名称匹配
1. 回退到将其作为数字序数解析
1. 如果两者都失败，则抛出 `IllegalParamException`

## 自定义转换器

若以上自带的转换器都无法满足您的需求，您可以通过自定义转换器来实现您的特殊需求。自定义转换器只需要实现以下接口即可：

* `BFieldConvertor`（支持 `BeanSearcher` 检索器）
* `MFieldConvertor`（支持 `MapSearcher` 检索器）
* `ParamConvertor`（参数转换器）

这仨接口都只需实现两个方法：

* `boolean supports(FieldMeta meta, Class<?> valueType)` - 判断该转换器支持的实体类属性类型与数据库值的类型
* `Object convert(FieldMeta meta, Object value)` - 转换操作，将 value 值转换为 meta 指定的字段类型值

具体编码可参考框架源码中的转换器：

* Github: https://github.com/troyzhxu/bean-searcher/tree/main/bean-searcher/src/main/java/cn/zhxu/bs/convertor
* Gitee: https://github.com/troyzhxu/bean-searcher/tree/main/bean-searcher/src/main/java/cn/zhxu/bs/convertor

### BFieldConverter 

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` 依赖，自定义好转换器后，只需将之声明为 Spring 的 Bean 即可：

```java
@Bean
public MyFieldConvertor myFieldConvertor() {
    return new MyFieldConvertor();
}
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new MyFieldConvertor());    // 添加转换器
// 构建 BeanSearcher
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .beanReflector(beanReflector)
        .build();
```

### MFieldConverter

* SpringBoot / Grails projects

建议使用 `bean-searcher-boot-starter` 依赖，自定义好转换器后，只需将之声明为 Spring 的 Bean 即可：

```java
@Bean
public MyMFieldConvertor myMFieldConvertor() {
    return new MyMFieldConvertor();
}
```

* Others

```java    
// 构建 MapSearcher
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性的配置
        .addFieldConvertor(new MyMFieldConvertor())    // 添加转换器
        .build();
```

### ParamConverter 

* SpringBoot / Grails 项目

建议使用 `bean-searcher-boot-starter` 依赖，自定义好转换器后，只需将之声明为 Spring 的 Bean 即可：

```java
@Bean
public MyParamConvertor myParamConvertor() {
    return new MyParamConvertor();
}
```

* Others

```java
DefaultParamResolver paramResolver = new DefaultParamResolver();
beanReflector.addConvertor(new MyParamConvertor());    // 添加转换器
// 构建 BeanSearcher 或 MapSearcher
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // 省略其它属性的配置
        .paramResolver(paramResolver)
        .build();
```
