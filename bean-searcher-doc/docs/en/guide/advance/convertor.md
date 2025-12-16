# Field and Parameter Converter

## Field Converter

The field converter is used to convert the values queried from the database into the values we need. For example, the value queried from the database is an `Integer` type number, while we need a `Long` type value.

Bean Searcher provides **7** field converters:

### NumberFieldConvertor

> since v3.0.0

> Only valid for the `BeanSearcher` retriever

This converter provides mutual conversion between `Integer`, `int`, `Long`, `long`, `Float`, `float`, `Double`, `double`, `Short`, `short`, `Byte`, `byte`, and `BigDecimal` (support for `BigDecimal` starts from v4.0).

#### Configuration Method

* SpringBoot / Grails projects

It is recommended to use the dependency of `bean-searcher-boot-starter` **v3.1.0 +**. **No configuration is required**, and it takes effect automatically. If you need to **disable** this converter, you need to configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-number = false
```

If you use the `bean-searcher-boot-starter` dependency of version **v3.0.x**, you need to configure a Bean to enable it:

```java
@Bean
public NumberFieldConvertor numberFieldConvertor() {
    return new NumberFieldConvertor();
}
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new NumberFieldConvertor());         // Add the converter
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes
        .beanReflector(beanReflector)
        .build();
```

### StrNumFieldConvertor

> since v3.0.0

> Only valid for the `BeanSearcher` retriever

This converter provides type conversion from `String` to `Integer | int | Long | long | Float | float | Double | double | Short | short | Byte | byte`.

#### Configuration Method

* SpringBoot / Grails projects

It is recommended to use the dependency of `bean-searcher-boot-starter` **v3.1.0 +**. **No configuration is required**, and it takes effect automatically. If you need to **disable** this converter, you need to configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-str-num = false
```

If you use the `bean-searcher-boot-starter` dependency of version **v3.0.x**, you need to configure a Bean to enable it:

```java
@Bean
public StrNumFieldConvertor strNumFieldConvertor() {
    return new StrNumFieldConvertor();
}
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new StrNumFieldConvertor());         // Add the converter
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes
        .beanReflector(beanReflector)
        .build();
```

### BoolNumFieldConvertor

> since v3.6.1

> Only valid for the `BeanSearcher` retriever

This converter can convert from `Boolean` type to `Integer | int | Long | long | Short | short | Byte | byte`.

> See: https://github.com/troyzhxu/bean-searcher/issues/33

#### Configuration Method

* SpringBoot / Grails projects

It is recommended to use the dependency of `bean-searcher-boot-starter` **v3.6.1 +**. **No configuration is required**, and it takes effect automatically.

If you need to **disable** this converter, you can configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-bool-num = false
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new BoolNumFieldConvertor());           // Add the converter
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes
        .beanReflector(beanReflector)
        .build();
```

### BoolFieldConvertor

> since v3.0.0

> Only valid for the `BeanSearcher` retriever

This converter provides type conversion from `String | Number` to `Boolean | boolean`. Since `v3.1.4`, `v3.2.3`, and `v3.3.2`, it also supports the conversion from `Boolean -> boolean`.

For **numeric types**, it defaults to converting `0` to `false` and non-`0` to `true`. For **String types**, it defaults to converting `"0" | "OFF" | "FALSE" | "N" | "NO" | "F"` (case-insensitive) to `false` and others to `true`. Additionally, it provides a method to add which string values should be converted to `false`, for example:

```java
BoolFieldConvertor convertor = new BoolFieldConvertor();
// Add the string value "Nothing" in the database to be converted to false, also case-insensitive
convertor.addFalseValues(new String[] { "Nothing" });
```

#### Configuration Method

* SpringBoot / Grails projects

It is recommended to use the dependency of `bean-searcher-boot-starter` **v3.1.0 +**. **No configuration is required**, and it takes effect automatically. If you need to **disable** this converter, you need to configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-bool = false
```

If you use the `bean-searcher-boot-starter` dependency of version **v3.0.x**, you need to configure a Bean to enable it:

```java
@Bean
public BoolFieldConvertor boolFieldConvertor() {
    return new BoolFieldConvertor();
}
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new BoolFieldConvertor());           // Add the converter
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes
        .beanReflector(beanReflector)
        .build();
```

### DateFieldConvertor

> since v3.1.0

> Only valid for the `BeanSearcher` retriever

This converter provides mutual conversion between `Date`, `java.sql.Date`, `java.sql.Timestamp`, `LocalDateTime`, and `LocalDate`. It also supports setting the time zone (if not set, the system default time zone is used). For example:

```java
DateFieldConvertor convertor = new DateFieldConvertor();
convertor.setZoneId(ZoneId.of("Asia/Shanghai"));
```

#### Configuration Method

* SpringBoot / Grails projects

It is recommended to use the dependency of `bean-searcher-boot-starter` **v3.1.0 +**. **No configuration is required**, and it takes effect automatically. If you need to modify the time zone, you only need to add it to `application.properties`:

```properties
# This configuration is shared with DateFormatFieldConvertor
bean-searcher.field-convertor.zone-id = Asia/Shanghai
```

If you need to **disable** this converter, you need to configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-date = false
```

If you use the `bean-searcher-boot-starter` dependency of version **v3.0.x**, you need to configure a Bean to enable it:

```java
@Bean
public DateFieldConvertor dateFieldConvertor() {
    return new DateFieldConvertor();
}
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new DateFieldConvertor());           // Add the converter
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes
        .beanReflector(beanReflector)
        .build();
```

### DateFormatFieldConvertor

> since v3.1.0

> Only valid for the `MapSearcher` retriever.

This converter can **format field values of types `Date`, `java.sql.Date`, `java.sql.Timestamp`, `LocalDateTime`, `LocalDate`, `LocalTime`, `java.sql.Time` into strings**. It provides a **very powerful** `setFormat(String scope, String format)` method, which supports setting **multiple date formats by scope** (the more precise the scope, the higher the priority of using the format). For example:

```java
DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
// Set all date fields in the com.example.sbean package to use the yyyy-MM-dd HH:mm format.
convertor.setFormat("com.example.sbean", "yyyy-MM-dd HH:mm");
// Set date fields of type LocalTime in the com.example.sbean package to use the HH:mm format.
convertor.setFormat("com.example.sbean:LocalTime", "HH:mm");
// Set date fields of type LocalDate in the com.example.sbean package to use the yyyy-MM-dd format.
convertor.setFormat("com.example.sbean:LocalDate", "yyyy-MM-dd");
// Set all date fields in the com.example.sbean.User entity class to use the yyyy-MM-dd HH:mm:ss format.
convertor.setFormat("com.example.sbean.User", "yyyy-MM-dd HH:mm:ss");
// Set fields of type Date in the com.example.sbean.User entity class to use the yyyy-MM-dd HH format.
convertor.setFormat("com.example.sbean.User:Date", "yyyy-MM-dd HH");
// Set the createDate field in the com.example.sbean.User entity class to use the yyyy-MM-dd format.
convertor.setFormat("com.example.sbean.User.createDate", "yyyy-MM-dd");
```

::: tip Note
In the scopes like `com.example.sbean:LocalTime` and `com.example.sbean.User:Date` above, the type qualifier after `:` does not refer to the field type declared in the entity class, but to the data type retrieved by `SqlExecutor` (i.e., the native field type returned by Jdbc by default).
:::

In addition, it also supports setting the time zone (if not set, the system default time zone will be used). For example:

```java
DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
convertor.setZoneId(ZoneId.of("Asia/Shanghai"));
```

#### Enabling the Effect

When a field of date/time type is formatted, the field in the `Map` object of the retrieval result of the `MapSearcher` retriever will no longer be of date/time type, but a formatted string. The value type has changed.

#### Configuration Methods

* SpringBoot / Grails Projects

It is recommended to use the `bean-searcher-boot-starter` dependency of version **v3.1.0 +**. You only need to configure the relevant date formats in `application.properties`:

```properties
bean-searcher.field-convertor.date-formats[com.example] = yyyy-MM-dd HH:mm      # The content in the square brackets is the scope of the format.
bean-searcher.field-convertor.date-formats[com.example.sbean] = yyyy-MM-dd HH
bean-searcher.field-convertor.date-formats[com.example.sbean\:Date] = yyyy-MM-dd
bean-searcher.field-convertor.date-formats[com.example.sbean.Employee] = yyyy-MM
bean-searcher.field-convertor.date-formats[com.example.sbean.Employee\:Date] = yyyy/MM/dd
bean-searcher.field-convertor.date-formats[com.example.sbean.Employee.entryDate] = yyyy-MM-dd HH:mm
# If you need to modify the time zone, you can add this configuration (this configuration is shared with DateFieldConvertor).
bean-searcher.field-convertor.zone-id = Asia/Shanghai
```

If you are using `application.yml`, the configuration is as follows:

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

If you are using the `bean-searcher-boot-starter` dependency of version **v4.0.0 +**, you can also configure it as follows in `application.yml`:

```yaml
bean-searcher:
  field-convertor:
    date-formats:
      com.example: yyyy-MM-dd HH:mm
      com.example.sbean: yyyy-MM-dd HH
      # A hyphen is used instead of a colon in the key.
      com.example.sbean-Date: yyyy-MM-dd
      com.example.sbean.Employee: yyyy-MM
      com.example.sbean.Employee-Date: yyyy/MM/dd
      com.example.sbean.Employee.entryDate: yyyy-MM-dd HH:mm
```

If you need to **disable** this converter, you need to configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-date-format = false
```

If you are using the `bean-searcher-boot-starter` dependency of version **v3.0.x**, you need to configure a Bean to enable it:

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
// Build the MapSearcher retriever.
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Omit the configuration of other attributes.
        .addFieldConvertor(convertor)
        .build();
```

### EnumFieldConvertor

> since v3.2.0 

> Only valid for the `BeanSearcher` retriever.

This converter provides type conversion in the direction of **`String | Integer | int -> Enum`** (since v3.7.0, it supports converting `Integer | int` to an enumeration, which is converted according to the enumeration sequence number).

For example, when there is a `VARCHAR` type field `gender` (gender) in the database, and the value `Male` represents male and `Female` represents female, you can define an enumeration class:

```java
public enum Gender {
    Male, Female
}
```

Then declare it as the type of the corresponding attribute in the entity class:

```java
public class User {
    private Gender gender;
    // Omit other...
}
```

#### Configuration Methods

* SpringBoot / Grails Projects

It is recommended to use the `bean-searcher-boot-starter` dependency of version **v3.2.0 +**. **No configuration is required**, and it will take effect automatically. If you need to **disable** this converter, you need to configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-enum = false          # Whether to enable this converter, default is true.
bean-searcher.field-convertor.enum-fail-on-error = true # Whether to report an error when an illegal value cannot be converted, default is true (since v3.7.0).
bean-searcher.field-convertor.enum-ignore-case = false  # Whether to ignore case when converting a string value to an enumeration, default is false (since v3.7.0).
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new EnumFieldConvertor());           // Add the converter.
// Build the Bean retriever.
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes.
        .beanReflector(beanReflector)
        .build();
```

### TimeFieldConvertor

> since v3.5.0

> Only valid for the `BeanSearcher` retriever.

This converter supports the mutual conversion between `java.sql.Time` and `LocalTime`:

#### Configuration Methods

* SpringBoot / Grails Projects

It is recommended to use the `bean-searcher-boot-starter` dependency of version **v3.5.0 +**. **No configuration is required**, and it will take effect automatically.

If you need to **disable** this converter, you can configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-time = false
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new TimeFieldConvertor());           // Add the converter.
// Build the Bean retriever.
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes.
        .beanReflector(beanReflector)
        .build();
```

### JsonFieldConvertor

> since v4.0.0

> Only valid for the `BeanSearcher` retriever

This converter supports the conversion from `JSON string` to `POJO` and is used in conjunction with the annotation `@DbField(type = DbType.JSON)` on the field of the SearchBean.

#### Prerequisites

Since JSON conversion is involved, it is inevitable to use a JSON parsing framework. However, different developers may prefer different JSON frameworks. Therefore, this converter is not bound to a specific JSON framework but allows users to choose their own (currently, there are 5 default frameworks to choose from). You only need to add the following specific dependencies:

* Use the Jackson framework

```groovy
implementation 'cn.zhxu:xjsonkit-jackson:1.5.1'
```

* Use the Gson framework

```groovy
implementation 'cn.zhxu:xjsonkit-gson:1.5.1'
```

* Use the Fastjson framework

```groovy
implementation 'cn.zhxu:xjsonkit-fastjson:1.5.1'
```

* Use the Fastjson2 framework

```groovy
implementation 'cn.zhxu:xjsonkit-fastjson2:1.5.1'
```

* Use the Snack3 framework

```groovy
implementation 'cn.zhxu:xjsonkit-snack3:1.5.1'
```

If your preferred JSON parsing framework is not included, you can also customize the underlying implementation. Refer to: https://gitee.com/troyzhxu/xjsonkit

#### Configuration Methods

* SpringBoot / Grails projects

It is recommended to use the `bean-searcher-boot-starter` dependency of **v4.0.0+**. **No configuration is required**, and it takes effect automatically.

If you need to **disable** this converter, you can configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-json = false
```

Other configuration items:

```properties
# Whether to throw an exception when JSON conversion fails. The default is false, and only warning logs are printed.
bean-searcher.field-convertor.json-fail-on-error = false
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new JsonFieldConvertor());           // Add the converter
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes
        .beanReflector(beanReflector)
        .build();
```

#### Usage Examples

* Example 1: **Convert a JSON array to a simple object**

```java
public class User {
    // Database value: {id: 1, name: "Administrator"}
    @DbField(type = DbType.JSON)
    private Role role;
    // Omit other fields...
}
```

* Example 2: **Convert a JSON array to a simple List**

```java
public class User {
    // Database value: ["New student", "Excellent"]
    @DbField(type = DbType.JSON)
    private List<String> tags;
    // Omit other fields...
}
```

* Example 3: **Convert a JSON array to a complex List** (since v4.2.6)

```java
public class User {
    // Database value: [{id: 1, name: "Administrator"},{id: 2, name: "Finance"}]
    @DbField(type = DbType.JSON)
    private List<Role> roles;
    // Omit other fields...
}
```

### ListFieldConvertor

> since v4.0.0

> Only valid for the `BeanSearcher` retriever

This converter supports the conversion from `string value` to `List` and can be used to handle lightweight one-to-many relationships.

#### Configuration Methods

* SpringBoot / Grails projects

It is recommended to use the `bean-searcher-boot-starter` dependency of **v4.0.0+**. **No configuration is required**, and it takes effect automatically.

If you need to **disable** this converter, you can configure it in `application.properties`:

```properties
bean-searcher.field-convertor.use-list = false
```

Other configuration items:

```properties
# Separator for each item in the List string. The default is a single English comma.
bean-searcher.field-convertor.list-item-separator = ,
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new ListFieldConvertor());           // Add the converter
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes
        .beanReflector(beanReflector)
        .build();
```

#### Usage Examples

* Example 1: **Convert a single-table field value to a simple List**

The `user` table in the database has a `tags` field, and its value is a comma-separated string, for example: `New student,Excellent`. Then the SearchBean can be designed as follows:

```java
public class User {
    // You can directly use a List to receive user tags.
    private List<String> tags;
    // Omit other fields...
}
```

* Example 2: **Convert a one-to-many joined table to a simple List**

The database has a `user(id,..)` table and its child table `user_tag(user_id, tag)` for storing user tags. Then the SearchBean can be designed as follows:

```java
@SearchBean(tables="user u")
public class User {
    // Use a List to receive user tags.
    @DbField("select group_concat(t.tag) from user_tag t where u.id = t.user_id")
    private List<String> tags;
    // Omit other fields...
}
```

* Example 3: **Convert a many-to-many joined table to a simple List**

The database has `user(id,..)`, `tag(id,name)` tables and their association table `user_tag(user_id, tag_id)` for storing user tags. Then the SearchBean can be designed as follows:

```java
@SearchBean(tables="user u")
public class User {
    @DbField("select group_concat(t.id) from tag t, user_tag ut where u.id = ut.user_id and ut.tag_id = t.id")
    private List<Integer> tagIds;  // Collection of tag IDs
    @DbField("select group_concat(t.name) from tag t, user_tag ut where u.id = ut.user_id and ut.tag_id = t.id")
    private List<String> tagNames; // Collection of tag names
    // Omit other fields...
}
```

* Example 4: **Convert a many-to-many joined table to a complex List**

In Example 3, we got two simple List fields, `tagIds` and `tagNames`. Now we combine them into a more complex List. First, define a simple tag class containing `id` and `name` fields:

```java
public class Tag {
    private int id;
    private String name;
    // Omit Getter and Setter
}
```

Then the SearchBean is designed as follows:

```java
@SearchBean(tables="user u")
public class User {
    // Query all the tag IDs and names and separate them with a colon.
    @DbField("select group_concat(concat(t.id,':',t.name)) from tag t, user_tag ut where u.id = ut.user_id and ut.tag_id = t.id")
    private List<Tag> tags; // Collection of tag objects
    // Omit other fields...
}
```

Finally, you need to define a list item converter and declare it as a Bean:

```java
@Component
public class TagConvertor implements ListFieldConvertor.Convertor<Tag> {
    public Tag convert(String value) {
        String[] vs = value.split(":"); // Split according to the colon
        int tagId = Integer.parseInt(vs[0]);
        String tagName = vs[1];
        return new Tag(tagId, tagName);
    }
}
```

### B2MFieldConvertor

> since v3.6.0

> Only valid for the `MapSearcher` retriever

This converter can combine the `BFieldConvertor`, which is only valid for the `BeanSearcher` retriever, into an `MFieldConvertor`, so that it also works for the `MapSearcher`.

#### Enable Effect

When not enabled, the value type of the retrieval result of the `MapSearcher` retriever **may not be consistent** with the field type declared in the entity class. For example, the field is declared as `Long` type in the entity class, but the value in the `Map` object of the retrieval result may be of `Integer` type (determined by the database column type and the JDBC driver).

After enabling this converter, the value type of the retrieval result of the `MapSearcher` retriever can be **consistent** with the field type declared in the entity class.

::: tip Note
When the [DateFormatFieldConvertor](/en/guide/advance/convertor#dateformatfieldconvertor) is enabled, and a field of the date/time type is within the formatting range specified by it, then this field will still be formatted as a string, so it will no longer be consistent with the date/time type declared in the entity class.
:::

For performance reasons, this converter is **not enabled by default**. Users can decide whether to enable it according to their own business needs.

#### Configuration Methods

* SpringBoot / Grails projects

When using the `bean-searcher-boot-starter` (v3.6.0+) dependency, you can add the following configuration to `application.properties` to enable it:

```properties
bean-searcher.field-convertor.use-b2-m = true
```

## Parameter Converter

To be improved...

## Custom Converter

If none of the built-in converters above can meet your requirements, you can implement your special needs through a custom converter. A custom converter only needs to implement the following interfaces:

* `BFieldConvertor` (Implementing this interface supports the `BeanSearcher` retriever)
* `MFieldConvertor` (Implementing this interface supports the `MapSearcher` retriever)

Both of these interfaces only require implementing two methods:

* `boolean supports(FieldMeta meta, Class<?> valueType)` - Determine the types of entity class attributes and database values supported by this converter.
* `Object convert(FieldMeta meta, Object value)` - Conversion operation to convert the `value` to the value of the field type specified by `meta`.

For specific coding, you can refer to the source code implementations of the built-in converters:

* [Source code of `BoolFieldConvertor`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/BoolFieldConvertor.java)
* [Source code of `DateFieldConvertor`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/DateFieldConvertor.java)
* [Source code of `DateFormatFieldConvertor`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/DateFormatFieldConvertor.java)
* [Source code of `EnumFieldConvertor`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/EnumFieldConvertor.java)
* [Source code of `NumberFieldConvertor`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/NumberFieldConvertor.java)
* [Source code of `StrNumFieldConvertor`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/StrNumFieldConvertor.java)
* [Source code of `TimeFieldConvertor`](https://github.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/implement/TimeFieldConvertor.java)

### Configuration Methods

* SpringBoot / Grails Projects

It is recommended to use the `bean-searcher-boot-starter` dependency. After customizing the converter, you only need to declare it as a Spring Bean:

```java
@Bean
public MyFieldConvertor myFieldConvertor() {
    return new MyFieldConvertor();
}
```

* Others

```java
DefaultBeanReflector beanReflector = new DefaultBeanReflector();
beanReflector.addConvertor(new MyFieldConvertor());           // Add the converter
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        // Omit the configuration of other attributes
        .beanReflector(beanReflector)
        .build();
```
