# v4.8.12 @ 2026-07-24

## 🐛 Bug Fixes
* 优化对 Solon 框架的兼容性，使 `bean-searcher-solon-plugin` 支持 `bean-searcher-exporter` 的自动装配

## 同时发布 v4.8.12.jdk8 版本

# v4.8.9 @ 2026-07-07

## 🐛 Bug Fixes

* 修复 `MapUtils.flat()` 配合 **同键多值** 参数使用时，`ARRAY_KEYS` 的 UUID 末尾段若为纯数字，会导致 `DefaultParamResolver.extractFieldParams()` 中 `Integer.parseInt` 抛出 `NumberFormatException` 的问题
  - 根因：`INDEX_PATTERN` 原为 `\d+`（无长度限制），UUID 末尾 12 位纯数字被匹配后传入 `Integer.parseInt`，超出 `Integer.MAX_VALUE` 导致异常
  - 修复 1：将 `INDEX_PATTERN` 改为 `\d{1,4}`，限制索引后缀最多 4 位数字（最大 9999）
  - 修复 2：移除 `ARRAY_KEYS` 与 `FIELD_PARAM` 中 UUID 的分隔符 `-`，从根源上避免 UUID 与检索参数分隔符的意外交互

> 该 BUG 只在前端使用 **同键多值** 参数的情况下放生（例如：GET /query?status=1&status=2），且触发概率为 0.36%，即项目重启 1000次，平均有 3 ~ 4 次会触发该 BUG，如果不幸触发该 BUG 又来不及升级，再次重启即可规避。

## 同时发布 v4.8.9.jdk8 版本

# v4.8.7 @ 2026-04-26

## 🌻 Better

* 字段转换器 `FieldConvertor` 支持对父类中 **泛型字段** 进行转换（修复泛型擦除问题），例如：

```java
// 父类
public class Base<ID> {
    // 泛型字段
    private ID id;
}
// 子类
public class Entity extends Base<Long> {
    // ...
}
```
> 以前版本的 BeanSearcher 虽然也可以对这样的 `Entity` 进行检索，但如果数据库返回的 `id` 字段不是子类期望的 `Long` 类型，则 BeanSearcher 也不会对它进行转换。
这虽然在检索阶段不会报错（JDK 泛型擦除），但如果再用某些第三方库（例如 JSON 库）对这种数据进行序列化，就可能会有兼容性问题。

## 同时发布 v4.8.7.jdk8 版本

# v4.8.6 @ 2026-04-08

## 🌻 Better

* 优化 `NumberParamConvertor` 中的异常信息，使用完整字段名提供更准确的错误提示

## 🐛 Bug Fixes

* 修复检索参数不合法时会抛出 `NPE` 的问题：

```
java.lang.NullPointerException: Cannot invoke "cn.zhxu.bs.SearchParam.getParaMap()" because the return value of "cn.zhxu.bs.SearchSql.getSearchParam()" is null
	at cn.zhxu.bs.implement.DefaultBeanSearcher.search(DefaultBeanSearcher.java:127)
```

## 同时发布 v4.8.6.jdk8 版本

# v4.8.5 @ 2026-03-27

## 🌻 Better

* 优化 `FieldParam`，使其支持多线程只读共享使用
  - `values` 字段改为 `final`，在构造时完成排序（由原来的 `getValues()` 调用时排序改为构造时排序）
  - 返回的 `values` 列表为只读列表，可安全地在多线程环境中共享同一 `FieldParam` 实例
* 新增 `FieldParam.valueList()` 方法，替代 `getValueList()`（后者标记为 `@Deprecated`）

## 🌻 Deps

* 将 Spring Boot 版本从 `3.5.8` 升级到 `3.5.9`
* 将 Solon 版本从 `3.7.3` 升级到 `3.8.3`

## 同时发布 v4.8.5.jdk8 版本

# v4.8.4 @ 2026-01-14

## 🐛 Bug Fixes

* 修复 `MapBuilder` 的 `groupRoot()` 方法会报 `ConcurrentModificationException` 的问题 

## 同时发布 v4.8.4.jdk8 版本

# v4.8.3 @ 2025-12-18

## ✨ Features

* 嵌入参数结束标志支持方括号检测

## 🌻 Better

- 将 Spring Boot 从 `3.3.13` 升级到 `3.5.8`
- 将 Spring Framework 从 `6.1.21` 升级到 `6.2.15`
- 将 Solon 从 `3.5.1` 升级到 `3.7.3`

## 🐛 Bug Fixes

* 修复 `SqlServerDialect` 在用户未指定排序字段时的分页查询的生成的 SQL 不符合 SQL Server 语法要求的问题：
  - 第一页分页查询使用 `SELECT TOP` 语法
  - 其它页查询自动添加 `ORDER BY (SELECT NULL)` 使 SQL 符合 `SQL Server` 语法

## 同时发布 v4.8.3.jdk8 版本

# v4.8.2 @ 2025-11-18

## 🐛 Bug Fixes（Bean Searcher Exporter）

* 优化 `ExportField` 组件：当字段值为 `null` 时，返回空字符串 `""`，不调用格式化器 

## 同时发布 v4.8.2.jdk8 版本

# v4.8.1 @ 2025-10-28

## 🌻 Better（Bean Searcher）

* 优化 `DefaultSnippetResolver` 组件
  - 优化异常处理机制：当遇到不能处理的未知 SQL 语法时，抛出异常告诉用户哪段 SQL 不能解析
  - 兼容 PgSQL 的 `||` 字符串连接符

# v4.8.0 @ 2025-10-13

## ✨ Features（Bean Searcher Exporter）

* 注解 `Export` 新增 `onlyIf` 属性，用于定义字段导出条件，支持表达式语法
  - 支持使用参数名引用检索参数（paraMap）中的值
  - 在 Spring 环境下支持 SpEL 表达式语法
  - 在 Solon 环境下支持 SnEL 表达式语法
* 组件 `ExportFieldResolver` 添加导出字段解析缓存机制
  - 引入 `ConcurrentHashMap` 缓存导出字段解析结果，提高性能
  - 新增 `clearCache` 方法可用于手动清理缓存
* JDK 版本要求：JDK 17+

# v4.7.1 @ 2025-10-11

## ✨ Features

* 优化 `ParamAware`、`ResultFilter` 与 `SqlInterceptor` 的 `paraMap` 参数值：使用经过 `ParamFilter` 处理后的非空值
* JDK 版本要求：JDK 17+

## 同时发布 v4.7.1.jdk8 版本

# v4.7.0 @ 2025-10-11

## ✨ Features

* Bean Searcher Exporter 的 `BeanExporter` 组件新增支持 `Function<List<T>, List<T>> mapper` 参数的系列导出方法，支持在写入导出文件之前，对查询出的整体数据做二次加工
  - `export(String name, Class<T> beanClass, Function<List<T>, List<T>> mapper)`
  - `export(String name, Class<T> beanClass, int batchSize, Function<List<T>, List<T>> mapper)`
  - `export(String name, Class<T> beanClass, Map<String, Object> paraMap, Function<List<T>, List<T>> mapper)`
  - `export(String name, Class<T> beanClass, Map<String, Object> paraMap, int batchSize, Function<List<T>, List<T>> mapper)`
  - `export(FileWriter writer, Class<T> beanClass, Function<List<T>, List<T>> mapper)`
  - `export(FileWriter writer, Class<T> beanClass, int batchSize, Function<List<T>, List<T>> mapper)`
  - `export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, Function<List<T>, List<T>> mapper)`
  - `export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, int batchSize, Function<List<T>, List<T>> mapper)`
* JDK 版本要求：JDK 17+

# v4.6.0 @ 2025-09-25

## ✨ Features

* Bean Searcher
  - 方言 `Dialect` 接口新增 `allowBoolLiterals(): boolean` 方法定义，用于指示数据库是否支持布尔字面量
  - 新增 `DialectSqlInterceptor` 组件，用于当数据库不支持布尔字面量时，将用户 SQL 中的布尔字面量自动转换为 `1` 和 `0`
  - 新增 `DaMengDialect` 方言实现，用于兼容 **达梦** 数据库
  - 新增 `StringFieldConvertor`, 可将 JDBC 返回的 `Clob`、`Number`、`Boolean` 与 `Date` 类型的值转换为 `String`（可兼容达梦数据库对 `TEXT` 类型字段返回 `Clob` 的情况）

* Bean Searcher Boot Starter
  - 新增 `bean-searcher.field-convertor.use-string` 配置项，可是否启动 `StringFieldConvertor`, 默认为 `true`

* Bean Searcher Solon Plugin
  - 新增 `bean-searcher.field-convertor.use-string` 配置项，可是否启动 `StringFieldConvertor`, 默认为 `true`

* JDK 版本要求：JDK 17+

## 同时发布 v4.6.0.jdk8 版本

# v4.5.2 @ 2025-09-20

## 🌻 Better

* 优化 `EnumFieldConvertor`：以支持 `short` 与 `byte` 向枚举转换。此前只支持 `String` 与 `int` 类型。
* JDK 版本要求：JDK 17+

## 同时发布 v4.5.2.jdk8 版本

# v4.5.1 @ 2025-09-20

## 🌻 Better

* 优化 `DefaultSqlExecutor`：如果 SQL 执行报错，则 SQL 日志级别从 `DEBUG` 提升为 `ERROR`
* 优化 `DefaultSqlExecutor`：如果 JDBC 在 `prepareStatement` 阶段报错，也打印出报错的 SQL
* JDK 版本要求：JDK 17+

# v4.4.3 @ 2025-09-20

## 🌻 Better

* 优化 `EnumFieldConvertor`：以支持 `short` 与 `byte` 向枚举转换。此前只支持 `String` 与 `int` 类型。
* 优化 `DefaultSqlExecutor`：如果 SQL 执行报错，则 SQL 日志级别从 `DEBUG` 提升为 `ERROR`
* 优化 `DefaultSqlExecutor`：如果 JDBC 在 `prepareStatement` 阶段报错，也打印出报错的 SQL

# v4.5.0 @ 2025-06-26

## ✨ Features

* Bean Searcher
  - 注解 `@SearchBean` 新增 `maxSize` 与 `maxOffset` 属性，可以为单个检索类设置独风控值，覆盖全局配置
  - 接口 `PageExtractor` 接口中新增 `extract` 方法，支持传入 `BeanMeta` 参数
  - 参数构建器新增 `groupRoot(String groupSeparator)` 方法，可将前端传来的普通参数组添加到根组内
  - 参数构建器新增 `groupRoot()` 方法，使用默认的组分割符，将前端传来的普通参数组添加到根组内
* Bean Searcher Exporter（**首发**：数据文件导出模块）
  - 新增 `Export` 注解，用于标记需要导出的字段（支持表达式转换值）
  - 新增 `BeanExporter` 导出器，让数据导出与查询同样简单（支持**分页实时**导出，前端**立即响应**，且内置**并发控制**）
  - 新增 `FileWriter` 接口，用于扩展导出文件类型，默认实现 `CsvFileWriter` 可导出 CSV 文件
  - 可自定义导出文件名装饰器 `FileNamer`
  - 可自定义数据批次加载延迟策略 `DelayPolicy`，默认采用随机放大延时策略 `DelayPolicy.RandomInflate`
  - 等等..
* Bean Searcher Boot Starter
  - 新增 `bean-searcher.exporter.batch-size` 配置项：指定数据导出时默认每批次查询的条数, 默认为 `1000`
  - 新增 `bean-searcher.exporter.batch-delay` 配置项：每批次查询后的初始延迟时间，默认 100毫秒，用于降低数据库压力, 默认为 `100ms`
  - 新增 `bean-searcher.exporter.max-exporting-threads` 配置项：最大同时导出的并发数，当同时导出操作的人达到这个值（默认 `10`）后，新导出的人会处于等待状态
  - 新增 `bean-searcher.exporter.max-threads` 配置项：最大线程数，当同时导出操作的人太多（默认 `30`），将不再接受新的导出（新导出的人会收到稍后操作的提示，或抛出异常，具体行为可由 `FileWriter` 决定）
  - 新增 `bean-searcher.exporter.timestamp-filename` 配置项：导出的文件名是否自动拼上当前时间戳，默认 `true`
  - 新增 `bean-searcher.exporter.too-many-requests-message` 配置项：导出人数太多时返回的提示信息，默认是 "大人请息怒，当前导出数据的人实在太多了，请稍后再试一下子哈！"
* Bean Searcher Solon Plugin
  - 新增 `bean-searcher.exporter.batch-size` 配置项：指定数据导出时默认每批次查询的条数, 默认为 `1000`
  - 新增 `bean-searcher.exporter.batch-delay` 配置项：每批次查询后的初始延迟时间，默认 100毫秒，用于降低数据库压力, 默认为 `100ms`
  - 新增 `bean-searcher.exporter.max-exporting-threads` 配置项：最大同时导出的并发数，当同时导出操作的人达到这个值（默认 `10`）后，新导出的人会处于等待状态
  - 新增 `bean-searcher.exporter.max-threads` 配置项：最大线程数，当同时导出操作的人太多（默认 `30`），将不再接受新的导出（新导出的人会收到稍后操作的提示，或抛出异常，具体行为可由 `FileWriter` 决定）
  - 新增 `bean-searcher.exporter.timestamp-filename` 配置项：导出的文件名是否自动拼上当前时间戳，默认 `true`
  - 新增 `bean-searcher.exporter.too-many-requests-message` 配置项：导出人数太多时返回的提示信息，默认是 "大人请息怒，当前导出数据的人实在太多了，请稍后再试一下子哈！"
* JDK 版本要求：JDK 17+

## 同时发布 v4.5.0.jdk8 版本

# v4.4.2 @ 2025-04-28

## ✨ Features

* 增强 `MapUtils` 工具类
  - 添加 `flat(Map<String, String[]> map, boolean urlDecode)` 方法，支持 指定知否进行 URL 解码
  - 添加 `flatBuilder(Map<String, String[]> map, boolean urlDecode)` 支持 指定知否进行 URL 解码
  - 原 `flat(Map<String, String[]> map)` 方法，默认进行 URL 解码
  - 原 `flatBuilder(Map<String, String[]> map)` 方法，默认进行 URL 解码

# v4.4.1 @ 2025-03-25

## 🌻 Better

* Bean Searcher Label
  - 优化 `LabelLoader` 接口的调用，确保 `load(key, ids)` 方法中的 `ids` 参数有值
  - 优化 `@LabelFor` 注解，支持指向父类和子类中的字段

# v4.4.0 @ 2025-03-19

## ✨ Features

* Bean Searcher
  - 新增 `IndexArrayParamFilter`: 支持解析形如 `key[0]=v1 & key[1]=v2` 的参数
  - 增强 `SuffixOpParamFilter`: 使支持将 是否忽略大小写 也合并到一个参数中，例如：`name-ct-ic=xxx`
  - 增强 `MetaResolver`: 新增 `clearCache()` 方法，用于手动清除 `BeanMeta` 缓存
  - 增强 `@DbIgnore`: 支持标注其它自定义注解，使其具有 `@DbIgnore` 的功能
  - 新增 `AnnoUtils`: 可用于解析组合注解
* Bean Searcher Label（**首发**）
  - 提供 `@LabelFor` 注解，该注解继承自 `@DbIgnore`，可用于标注并解析 Label 字段（例如：将 `statusName` 字段标记为 `status` 的 Label）
  - 提供 `LabelLoader` 接口，可用于加载自定义的 Label
  - 提供 `EnumLabelLoader` 实现，用于加载枚举的 Label
* Bean Searcher Boot Starter
  - 新增配置项 `bean-searcher.params.filter.use-index-array` 用于控制是否启用 `IndexArrayParamFilter`，默认 `false`
  - 新增 Bean Searcher Label 的自动化配置
* Bean Searcher Solon Plugin
  - 新增配置项 `bean-searcher.params.filter.use-index-array` 用于控制是否启用 `IndexArrayParamFilter`，默认 `false`
  - 新增 Bean Searcher Label 的自动化配置

# v4.3.6 @ 2025-01-23

## 🌻 Better

* 增强 `JsonFieldConvertor`: 使支持所有复合泛型的转换

## 🐛 Bug Fixes

* 修复：`EnumParamConvertor`: 不兼容空字符串参数的问题

# v4.3.5 @ 2024-11-06

## ✨ Features

* Bean Searcher
  * 增强 `DateParamConvertor`，使同时支持以下 8 种格式的 `Date` 参数值：
    * `yyyy-MM-dd` - 例如：`2024-01-01`
    * `yyyy-M-dd` -- 例如：`2024-1-01` （新增）
    * `yyyy-MM-d` -- 例如：`2024-01-1` （新增）
    * `yyyy-M-d` --- 例如：`2024-1-1` - （新增）
    * `yyyy/MM/dd` - 例如：`2024/01/01`
    * `yyyy/M/dd` -- 例如：`2024/1/01` （新增）
    * `yyyy/MM/d` -- 例如：`2024/01/1` （新增）
    * `yyyy/M/d` --- 例如：`2024/1/1` - （新增）
  * 增强 `DateTimeParamConvertor`，使同时支持以下的 312 种格式的 `DateTime` 参数值：
    * `yyyy-MM-dd` ---------- 自动补全为 `yyyy-MM-dd 00:00:00.000`
    * `yyyy-MM-dd H` -------- 自动补全为 `yyyy-MM-dd H:00:00.000`（新增）
    * `yyyy-MM-dd HH` ------- 自动补全为 `yyyy-MM-dd HH:00:00.000`
    * `yyyy-MM-dd H:m` ------ 自动补全为 `yyyy-MM-dd H:m:00.000`（新增）
    * `yyyy-MM-dd HH:m` ----- 自动补全为 `yyyy-MM-dd HH:m:00.000`（新增）
    * `yyyy-MM-dd H:mm` ----- 自动补全为 `yyyy-MM-dd H:mm:00.000`（新增）
    * `yyyy-MM-dd HH:mm` ---- 自动补全为 `yyyy-MM-dd HH:mm:00.000`
    * `yyyy-MM-dd H:m:s` ---- 自动补全为 `yyyy-MM-dd H:m:s.000`（新增）
    * `yyyy-MM-dd HH:m:s` --- 自动补全为 `yyyy-MM-dd HH:m:s.000`（新增）
    * `yyyy-MM-dd H:mm:s` --- 自动补全为 `yyyy-MM-dd H:mm:s.000`（新增）
    * `yyyy-MM-dd H:m:ss` --- 自动补全为 `yyyy-MM-dd H:m:ss.000`（新增）
    * `yyyy-MM-dd HH:mm:s` -- 自动补全为 `yyyy-MM-dd HH:mm:s.000`（新增）
    * `yyyy-MM-dd HH:m:ss` -- 自动补全为 `yyyy-MM-dd HH:m:ss.000`（新增）
    * `yyyy-MM-dd H:mm:ss` -- 自动补全为 `yyyy-MM-dd H:mm:ss.000`（新增）
    * `yyyy-MM-dd HH:mm:ss` - 自动补全为 `yyyy-MM-dd HH:mm:ss.000`
    * `yyyy-MM-dd H:m:s.S` ------ 例如：`2024-01-01 1:1:1.9`（新增）
    * `yyyy-MM-dd H:m:s.SS` ----- 例如：`2024-01-01 1:1:1.09`（新增）
    * `yyyy-MM-dd H:m:s.SSS` ---- 例如：`2024-01-01 1:1:1.009`（新增）
    * `yyyy-MM-dd HH:m:s.S` ----- 例如：`2024-01-01 01:1:1.9`（新增）
    * `yyyy-MM-dd HH:m:s.SS` ---- 例如：`2024-01-01 01:1:1.09`（新增）
    * `yyyy-MM-dd HH:m:s.SSS` --- 例如：`2024-01-01 01:1:1.009`（新增）
    * `yyyy-MM-dd H:mm:s.S` ----- 例如：`2024-01-01 1:01:1.9`（新增）
    * `yyyy-MM-dd H:mm:s.SS` ---- 例如：`2024-01-01 1:01:1.09`（新增）
    * `yyyy-MM-dd H:mm:s.SSS` --- 例如：`2024-01-01 1:01:1.009`（新增）
    * `yyyy-MM-dd H:m:ss.S` ----- 例如：`2024-01-01 1:1:01.9`（新增）
    * `yyyy-MM-dd H:m:ss.SS` ---- 例如：`2024-01-01 1:1:01.09`（新增）
    * `yyyy-MM-dd H:m:ss.SSS` --- 例如：`2024-01-01 1:1:01.009`（新增）
    * `yyyy-MM-dd HH:mm:s.S` ---- 例如：`2024-01-01 01:01:1.9`（新增）
    * `yyyy-MM-dd HH:mm:s.SS` --- 例如：`2024-01-01 01:01:1.09`（新增）
    * `yyyy-MM-dd HH:mm:s.SSS` -- 例如：`2024-01-01 01:01:1.009`（新增）
    * `yyyy-MM-dd HH:m:ss.S` ---- 例如：`2024-01-01 01:1:01.9`（新增）
    * `yyyy-MM-dd HH:m:ss.SS` --- 例如：`2024-01-01 01:1:01.09`（新增）
    * `yyyy-MM-dd HH:m:ss.SSS` -- 例如：`2024-01-01 01:1:01.009`（新增）
    * `yyyy-MM-dd H:mm:ss.S` ---- 例如：`2024-01-01 1:01:01.9`（新增）
    * `yyyy-MM-dd H:mm:ss.SS` --- 例如：`2024-01-01 1:01:01.09`（新增）
    * `yyyy-MM-dd H:mm:ss.SSS` -- 例如：`2024-01-01 1:01:01.009`（新增）
    * `yyyy-MM-dd HH:mm:ss.S` --- 例如：`2024-01-01 01:01:01.9`（新增）
    * `yyyy-MM-dd HH:mm:ss.SS` -- 例如：`2024-01-01 01:01:01.09`（新增）
    * `yyyy-MM-dd HH:mm:ss.SSS` - 例如：`2024-01-01 01:01:01.009`
    > 以上是 `yyyy-MM-dd [Time]` 系列的格式，还支持以下 7 个系列的，每个系列各 39 种格式，不再详细列举：
    > * `yyyy-M-dd [Time]` -- 系列格式（新增）
    > * `yyyy-MM-d [Time]` -- 系列格式（新增）
    > * `yyyy-M-d [Time]` --- 系列格式（新增）
    > * `yyyy/MM/dd [Time]` - 系列格式（新增）
    > * `yyyy/M/dd [Time]` -- 系列格式（新增）
    > * `yyyy/MM/d [Time]` -- 系列格式（新增）
    > * `yyyy/M/d [Time]` --- 系列格式（新增）

* Bean Searcher Solon Plugin
  * 优化配置项，支持在 IDEA 内配置提示项功能（前提是安装 Solon 插件）

## 🐛 Bug Fixes

* Bean Searcher Boot Starter
  * 修复在 `v4.3.4` 版本后出现的在 IDEA 内配置项提示功能失效的问题：https://gitee.com/troyzhxu/bean-searcher/issues/IB1Y6M

# v4.3.4 @ 2024-10-22

## ✨ Features

* Bean Searcher
  * 新增：`OracleTimestampFieldConvertor`, 用于兼容 Oracle 的 `TIMESTAMP` 字段。
  * 优化：`参数构建器` 的 `field(..)` 方法，兼容直接使用 **实际类型不确定（集合/单值）** 的参数值。

  ```java
  var params = MapUtils.builder()
          // ifTrue 若真，返回 List, 否则返回 单值，类型一致，v4.3.4 开始兼容这种写法
          .field(User::getId, ifTrue ? List.of(1,2,3) : 4)
          .build();
  ```

  ```java
  var params = MapUtils.builder()
          // ifTrue 若真，返回 原生数组, 否则返回 单值，类型一致，v4.3.4 开始兼容这种写法
          .field(User::getId, ifTrue ? new int[] {1,2,3} : 4)
          .build();
  ```
  
  ```java
  var params = MapUtils.builder()
          // ifTrue 若真，返回 对象数组, 否则返回 单值，类型一致，v4.3.4 开始兼容这种写法
          .field(User::getId, ifTrue ? new Integer[] {1,2,3} : 4)
          .build();
  ```

* Bean Searcher Boot Starter
  * 新增配置项 `bean-searcher.field-convertor.use-oracle-timestamp` 用于控制是否启用 `OracleTimestampFieldConvertor`，默认 `true`
  * 简化 `BeanSearcherProperties` 类，将内部子类定义成外部类
* Bean Searcher Solon Plugin
  * 新增配置项 `bean-searcher.field-convertor.use-oracle-timestamp` 用于控制是否启用 `OracleTimestampFieldConvertor`，默认 `true`
  * 简化 `BeanSearcherProperties` 类，将内部子类定义成外部类

# v4.3.3 @ 2024-10-08

## ✨ Features

* 优化：`BeanMeta` 可保持检索实体类中的字段声明顺序（即条件生成顺序）
* 优化：`DefaultParamResolver` 提升 `extractFieldParams(..)` 方法的权限，可供子类重写，便于用户自定义
* 优化：`DefaultMetaResolver`: 抽取 `createBeanMeta(..)` 方法，便于用户自定义 `BeanMeta` 子类

## 🐛 Bug Fixes

* 修复：`DefaultGroupResolver` 的默认 `LRUCache` 缓存没有遵循访问顺序规则的问题 

## 🌻 Better

* 升级 Solon -> 2.9.4

# v4.3.2 @ 2024-09-09

## ✨ Features

* Bean Searcher
  * 增强 `DateTimeParamConvertor`: 使支持整型的时间戳参数值
* Bean Searcher Boot Starter
  * 新增 `SpringSqlExecutor`: 支持 Spring 事务的 Sql 执行器，且默认使用
  * 新增配置项：`bean-searcher.params.convertor.zone-id`: 可配置 `DateTimeParamConvertor` 使用的时区
* Bean Searcher Solon Plugin
  * 新增 `SolonSqlExecutor`: 支持 Solon 事务的 Sql 执行器，且默认使用
  * 新增配置项：`bean-searcher.params.convertor.zone-id`: 可配置 `DateTimeParamConvertor` 使用的时区

# v4.3.1 @ 2024-08-24

## 🌻 Better

* 类 `SearchResult` 新增 `empty()` 方法
* 类 `RpcNames` 新增 `newNames()` 替代原来的 `newConfig()` 方法
* 参数构建器的 `buildForRpc()` 方法，提升一点性能
* 依赖升级：`junit -> 5.10.3`, `slf4j -> 2.0.16`, `solon -> 2.9.0`

## 🐛 Bug Fixes

* 修复 `JsonFieldConvertor` 在转换形如 `List<A<B>>` 的复杂 List 泛型时会报错的问题: https://github.com/troyzhxu/bean-searcher/issues/99

# v4.3.0 @ 2024-06-10

## ✨ Features

* Bean Searcher
  * 优化 `BeanMeta`: 新增 `getSqlSnippets()` 方法，用户可以使用该方法获取该实体类上所有已解析的 SQL 片段
  * 优化 `SearchSql`: 新增 `getSearchParam()` 方法，用户可以在 `SqlInterceptor` 中使用该方法获取到解析后的检索参数
  * 增强 `MapBuilder`：新增 `or(..)` 与 `and(..)` 方法，用于简化逻辑分组在后端的使用：https://gitee.com/troyzhxu/bean-searcher/issues/I9T66B
  * 增强 `MapBuilder`：新增 `buildForRpc()` 与 `buildForRpc(RpcNames)` 方法，用于构建适用于请求远程 API 服务的参数
  * 优化 `MapBuilder`：方法 `field(FieldFn, Collection)` 与 `field(String, Collection)` 的第二个参数兼容传入 `null` 的用法
  * 增强 `DefaultParamResolver`：新增 `gexprMerge` 属性，可用于控制参数构建器中使用 `groupExpr(..)` 方法指定的组表达式是否合并或覆盖前端参数传来的组表达式：https://gitee.com/troyzhxu/bean-searcher/issues/I9TAV6
  * 新增 `JoinParaSerializer`：拼接参数序列化器，可处理集合类型的参数值，自动将其连接为用英文逗号分隔的字符串
  * 新增 `ArrayValueParamFilter`：用于配合 `MapUtils.flat(..)` 与 `MapUtils.flatBuilder(..)` 方法，兼容数组参数值的用法，例如前端传参：age=20 & age=30 & age-op=bt
  * 新增 `SuffixOpParamFilter`：用于简化前端传参，例如 age-gt=25 替代 age=25 & age-op=gt
  * 新增 `JsonArrayParamFilter`：用于简化前端传参，例如 age=[20,30] 替代 age-0=20 & age-1=30
  * 新增 `AlwaysTrue`（恒真：`at`）与 `AlwaysFalse`（恒假：`af`）运算符：https://gitee.com/troyzhxu/bean-searcher/issues/I9TMFI
  * 升级 `OracleDialect`：使用新的 `offset ? rows fetch next ? rows only` 分页语法，仅支持 Oracle 12c（2013年6月发布）及以上版本
  * 优化 `ExprParser`：新增逻辑关系符常量，逻辑表达式中的且关系符 `&` 与 或关系符 `|` 不再支持自定义。
  * 优化 `FieldParam`：其内部类 `Value` 新增 `getIndex()` 方法
* Bean Searcher Boot Starter
  * 新增配置项 `bean-searcher.params.group.mergeable` 指定组表达式是否可合并，默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-size-limit` 是否启用 `SizeLimitParamFilter`，默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-array-value` 是否启用 `ArrayValueParamFilter`, 默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-suffix-op` 是否启用 `SuffixOpParamFilter`, 默认 `false`
  * 新增配置项 `bean-searcher.params.filter.use-json-array` 是否启用 `JsonArrayParamFilter`, 默认 `false`
  * 支持以 Bean 的形式自定义 `JoinParaSerializer` 组件
* Bean Searcher Solon Plugin
  * 新增配置项 `bean-searcher.params.group.mergeable` 指定组表达式是否可合并，默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-size-limit` 是否启用 `SizeLimitParamFilter`，默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-array-value` 是否启用 `ArrayValueParamFilter`, 默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-suffix-op` 是否启用 `SuffixOpParamFilter`, 默认 `false`
  * 新增配置项 `bean-searcher.params.filter.use-json-array` 是否启用 `JsonArrayParamFilter`, 默认 `false`
  * 支持以 Bean 的形式自定义 `JoinParaSerializer` 组件

## 🌻 Better

* 升级 Junit -> 5.10.2
* 升级 Solon -> 2.8.3

# v4.2.9 @ 2024-04-25

## 🌻 Better

* 增强 `JsonFieldConvertor`，兼容 DB 返回的 `byte[]` 类型的字段（例如 H2 的 `JSON` 字段）

# v4.2.8 @ 2024-04-24

## 🐛 Bug Fixes

* 修复在 PgSQL 数据库上，使用字段参数动态生成 `having` 条件并且该条件字段在 `select` 列表中时，生成的 `having` 条件含有别名的问题（这个语法特性在 MySQL 上支持，PgSQL 上不支持）。

## 🌻 Better

* 优化 `ListFieldConvertor` 的字段匹配条件：未指定 `dbType` 的 `List` 字段
* 优化 `bean-searcher-solon-plugin` 组件支持零配置使用
* 升级 solon -> 2.7.5
* 升级 spring-boot -> 2.7.18
* 升级 slf4j-api -> 2.0.13
* 兼容 JDK 8

# v4.2.7 @ 2024-01-16

## 🐛 Bug Fixes

* 修复：使用 `@SearchBean.fields` 时，某些情况下会出现 `NullPointerException` 的问题

## 🌻 Better

* 升级 solon -> 2.6.5
* 升级 slf4j-api -> 2.0.11

# v4.2.6 @ 2024-01-08

## ✨ Features

* 增强 `JsonFieldConvertor`：使支持复杂 `JSON` 数组到泛型 `List<T>` 的字段转换

## 🌻 Better

* 升级 solon -> 2.6.4
* 升级 spring-boot -> 3.2.1
* 升级 slf4j-api -> 2.0.10

# v4.2.5 @ 2023-12-13

## 🐛 Bug Fixes

* 修复当默认排序字段 `@SearchBean.orderBy` 仅为一个拼接参数，且查询时未传任何参数时，仍然生成 `order by` 子句的问题。
  https://gitee.com/troyzhxu/bean-searcher/issues/I8NT9X

## 🌻 Better

* 升级 spring-boot -> 3.1.6
* 升级 solon -> 2.6.2
* 升级 slf4j -> 2.0.9
* 升级 xjsonkit -> 1.4.3

# v4.2.4 @ 2023-08-02

* 为配置项 `bean-searcher.params.convertor.date-target` 添加默认值 `SQL_DATE`
* 为配置项 `bean-searcher.params.convertor.date-time-target` 添加默认值 `SQL_TIMESTAMP`
* 为配置项 `bean-searcher.params.convertor.time-target` 添加默认值 `SQL_TIME`

# v4.2.3 @ 2023-08-02

## ✨ Features

* Bean Searcher
  * 增强 `BaseSearcher`，新增 `failOnParamError` 属性，可配置当参数错误时是否向外抛出异常，默认 `false`
  * 增强 `DateParamConvertor`，支持 `java.util.Date` 子类的转换，并新增 `target` 属性，支持配置转换目标类型
  * 增强 `DateTimeParamConvertor`，支持 `java.util.Date` 子类的转换，并新增 `target` 属性，支持配置转换目标类型
  * 增强 `TimeParamConvertor`，新增 `target` 属性，支持配置转换目标类型
* Bean Searcher Boot Starter
  * 新增配置项 `bean-searcher.params.fail-on-error` 指定参数错误时，是否抛出异常，默认 `false`
  * 新增配置项 `bean-searcher.params.convertor.date-target`
  * 新增配置项 `bean-searcher.params.convertor.date-time-target`
  * 新增配置项 `bean-searcher.params.convertor.time-target`
* Bean Searcher Solon Plugin
  * 新增配置项 `bean-searcher.params.fail-on-error` 指定参数错误时，是否抛出异常，默认 `false`
  * 新增配置项 `bean-searcher.params.convertor.date-target`
  * 新增配置项 `bean-searcher.params.convertor.date-time-target`
  * 新增配置项 `bean-searcher.params.convertor.time-target`

## 🐛 Bug Fixes

* Bean Searcher
  * 修复：实体类字段类型是整形，参数传字母时会报 `IllegalArqumentException` 的问题
  * 修复：逻辑分组在某些情况下会丢失部分条件的问题：https://gitee.com/troyzhxu/bean-searcher/issues/I7PZQ1

## 🌻 Better

* 升级 Solon -> v2.4.1
* 升级 SpringBoot -> v3.1.2

# v4.2.2 @ 2023-07-18

* 修复 `NumberParamConvertor` 与 `EnumParamConvertor` 冲突问题，`NumberParamConvertor` 不再处理枚举字段的转换
* 增强 `JsonFieldConvertor`，支持将 `非 String` 类型（例如：`PGobject`）的 JSON 值转换为对象

# v4.2.1 @ 2023-07-13

## ✨ Features

* Bean Searcher
  * 增强：`DateFieldConvertor` 支持 `Instant` 类型的转换：https://gitee.com/troyzhxu/bean-searcher/pulls/9
  * 新增：`EnumParamConvertor` 对于枚举字段，可将 `String/Emun` 类型的参数自动转换为 `枚举序号`（默认）或 `枚举名`（由 `@DbField.type` 决定）
  * 优化：`PreparedStatement.setObject(..)` 方法报错时，仍然打印 SQL 日志
* Bean Searcher Boot Starter
  * 自动配置 `EnumParamConvertor`
* Bean Searcher Solon Plugin
  * 自动配置 `EnumParamConvertor`

## 🐛 Bug Fixes

* Bean Searcher
  * 修复使用动态方言时，默认方言不能用的问题
* Bean Searcher Boot Starter
  * 修复 动态方言 不能自动化配置的问题
* Bean Searcher Solon Plugin
  * 修复 动态方言 不能自动化配置的问题

## 🌻 Better

* 升级 Solon -> v2.3.8
* 升级 SpringBoot -> v3.1.1

# v4.2.0 @ 2023-05-08

## ✨ Features

* Bean Searcher
  * 新增 `DynamicDialect` 与 `DynamicDialectSupport` 类，用于支持动态方言
  * 重构 `FieldParam.Value.isEmptyValue()` 方法重命名为 `FieldParam.Value.isEmpty()`
  * 重构 `AbstractSearcher` 重命名为 `BaseSearcher`
  * 优化 `DateTimeParamConvertor`, 使其支持解析 `yyyy-MM-dd HH:mm:ss.SSS`、`yyyy-MM` 与 `yyyy` 格式的参数
  * 升级 `slf4j` -> `2.0.7`
* Bean Searcher Boot Starter
  * 新增配置项 `bean-searcher.sql.dialect-dynamic`, 表示是否启用动态方言，默认 `false`
  * 当启用动态数据源时，支持以 `DataSourceDialect` 注入 Bean 的方式添加数据源与方言的映射关系
  * 升级 `springboot` -> `3.0.6`
* Bean Searcher Solon Plugin
  * 新增配置项 `bean-searcher.sql.dialect-dynamic`, 表示是否启用动态方言，默认 `false`
  * 当启用动态数据源时，支持以 `DataSourceDialect` 注入 Bean 的方式添加数据源与方言的映射关系
  * 升级 `solon` -> `2.2.17`

## 🐛 Bug Fixes

* 自定义 Equal 运算符不默认的问题：https://github.com/troyzhxu/bean-searcher/issues/73

# v4.1.2 & v4.0.2 @ 2023-03-09

## 🌻 Better

* 提升 `DefaultSqlExecutor` 的执行性能

# v4.1.1 @ 2023-03-06

## 🌻 Better

* 优化 `SearchBean` 校验
* 优化 `DefaultDbMapping.BeanField` 的访问权限

# v4.1.0 @ 2023-03-03

## ✨ Features

* Bean Searcher
  * 优化：`@DbField.type` 为 `UNKNOWN` 时，也支持使用 `ParamConvertor` 进行参数值转换
  * 重构：`ParamResolver.Convertor` -> `FieldConvertor.ParamConvertor`，且入参 `DbType` 类型修改为 `FieldMeta` 类型
  * 功能：注解 `@DbField` 新增 `name` 属性，可显式指定字段的参数名
  * 功能：注解 `@DbField` 新增 `cluster` 属性，可显式指定是否是聚合字段
  * 功能：注解 `@DbField` 新增 `mapTo` 属性，可显式指定映射到哪张表
  * 功能：注解 `@SearchBean` 新增 `fields` 属性，用于指定额外的动态条件字段
  * 优化：注解 `@SearchBean.groupBy` 使用拼接参数时，条件字段的生成逻辑
  * 优化：完善 `groupBy` 与 `groupExpr` 同时使用时 `where` 与 `having` 的条件拆分逻辑
  * 优化：增强逻辑表达式的自动化简能力
* Bean Searcher Boot Starter
  * 支持 `GroupPairResolver` 注入 Bean 的方式自定义
* Bean Searcher Solon Plugin
  * 首发 Solon 插件（功能同 `bean-searcher-boot-starter`）

### 👨🏻‍💻 Contributors

Thank you to all the contributors who worked on this release:

  * [@VampireAchao](https://gitee.com/VampireAchao)
  * [@noear_admin](https://gitee.com/noear_admin)
  * [@troyzhxu](https://gitee.com/troyzhxu)

# v4.0.1 @ 2023-02-25

## ✨ Features

* Bean Searcher
  * 增强 `JsonFieldConvertor` 字段转换器，新增 `failOnError` 字段，可配置遇到某些值 JSON 解析异常时，是否自动捕获（即忽略）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.field-convertor.json-fail-on-error` 配置项，表示是当 JSON 解析错误时，是否抛出异常，默认 `true`

## 🐛 Bug Fixes

* Bean Searcher Boot Starter
  * 修复 未添加 [`xjsonkit`](https://gitee.com/troyzhxu/xjsonkit) 相关依赖时，会启动报错的问题：https://gitee.com/troyzhxu/bean-searcher/issues/I6F4LS

# v4.0.0 @ 2023-01-31

## ✨ Features

* 重构 `cn.zhxu.bs.param.Operator` -> `cn.zhxu.bs.FieldOps`

## 🐛 Bug Fixes

* 修正 `MapUtils.of(..)` 为 `static` 方法。

# v4.0.0.alpha3 @ 2023-01-29

## ✨ Features

* Bean Searcher
  * 新增 `JsonFieldConvertor` 字段转换器，配合 `@DbField(type = DbType.JSON)` 可支持 JSON 字段自动转对象，需要添加 JSON 依赖（以下依赖任性一个即可）：
    * `cn.zhxu:xjsonkit-fastjson:1.4.2`
    * `cn.zhxu:xjsonkit-fastjson2:1.4.2`
    * `cn.zhxu:xjsonkit-gson:1.4.2`
    * `cn.zhxu:xjsonkit-jackson:1.4.2`
    * `cn.zhxu:xjsonkit-snack3:1.4.2`
    * 参考：https://gitee.com/troyzhxu/xjsonkit
  * 新增 `ListFieldConvertor` 字段转换器，可支持将 字符串字段自动转为简单 List 对象。
  * 增强 `NumberFieldConvertor`，使支持 `BigDecimal` 与 `Integer Long Float Double Short Byte` 之间的相互转换
  * 增强 `DefaultDbMapping`，新增 `setAroundChar(String)` 方法，支持配置标识符的围绕符，以区分系统保留字（只对自动映射的表与字段起作用）
  * 增强 `MapUtils`，新增 `of(k, v)`, `of(k1, v1, k2, v2)` 等 4 个 便捷 Map 构造方法
  * 升级 `slf4j-api` -> `2.0.6`
  * 升级 `springboot` -> `3.0.2`

* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.default-mapping.around-char` 配置项，可配置标识符的围绕符（例如 MySQL 的 ` 符）
  * 新增 `bean-searcher.field-convertor.use-json` 配置项，表示是否自动添加 `JsonFieldConvertor`，默认 `true`
  * 新增 `bean-searcher.field-convertor.use-list` 配置项，表示是否自动添加 `ListFieldConvertor`，默认 `true`
  * 新增 `bean-searcher.field-convertor.list-item-separator` 配置项，用于配置如何将一个字符串分割成 `List` 字段
  * 优化 `bean-searcher.field-convertor.date-formats` 配置项，支持用 `-` 替代 `:`（因为在 yml 的 key 中 `:` 默认会被过滤掉）

# v4.0.0.alpha2 @ 2022-12-02

## ✨ Features

* Bean Searcher
  * 移除 Searcher 接口内的 search()、searchFirst()、searchList() 与 searchAll() 方法
  * 新增方法：search(Class<T> beanClass) -> SearchResult
  * 新增方法：search(Class<T> beanClass, String summaryField) -> SearchResult
  * 新增方法：search(Class<T> beanClass, String[] summaryFields) -> SearchResult
  * 新增方法：search(Class<T> beanClass, FieldFns.FieldFn<T, ?> summaryField) -> SearchResult
  * 新增方法：search(Class<T> beanClass, Map<String, Object> paraMap, String summaryField) -> SearchResult
  * 新增方法：search(Class<T> beanClass, Map<String, Object> paraMap, FieldFns.FieldFn<T, ?> summaryField) -> SearchResult
  * 新增方法：searchFirst(Class<T> beanClass) -> T / Map<String, Object>
  * 新增方法：searchList(Class<T> beanClass) -> List<T> / List<Map<String, Object>>
  * 新增方法：searchAll(Class<T> beanClass) -> List<T> / List<Map<String, Object>>
  * 新增方法：searchCount(Class<T> beanClass) -> Number
  * 新增方法：searchSum(Class<T> beanClass, String field) -> Number
  * 新增方法：searchSum(Class<T> beanClass, String[] fields) -> Number[]
  * 新增方法：searchSum(Class<T> beanClass, FieldFns.FieldFn<T, ?> field) -> Number
  * 新增方法：searchSum(Class<T> beanClass, Map<String, Object> paraMap, FieldFns.FieldFn<T, ?> field) -> Number

# v4.0.0.alpha1 @ 2022-12-01

## ✨ Features

* Bean Searcher
  * Maven 坐标 groupId 变更 -> `cn.zhxu`
  * 包名变更：`com.ejlchina.searcher` -> `cn.zhxu.bs`
  * 移除过时 API: `@SearchBean` 注解的 `joinCond` 属性
  * 优化嵌入参数：提高兼容性，支持嵌入参数后紧跟了 `.` 符号
  * 当字段是数字，但传参不是数字时，直接返回空数据
  * 注解 `@SearchBean` 添加 `timeout` 属性，用于控制慢 SQL 最大执行时长
  * 当使用 groupBy 与 逻辑分组时，如果所传参数都在 groupBy 内，也使用 where 形式的条件: https://gitee.com/troyzhxu/bean-searcher/issues/I5V4ON
* Bean Searcher Boot Starter
  * 支持 Spring Boot 3

# v3.8.2 @ 2022-09-16

* 修复 `v3.8.1` 产生的 BUG：https://github.com/troyzhxu/bean-searcher/issues/62

# v3.8.1 @ 2022-08-23

## ✨ Features

* Bean Searcher
  * 新增：分页大深度保护，默认最大允许分页偏移 `20000` 条
  * 优化：当检索参数过于庞大（阈值可配置）时，不执行查询，直接返回空数据
  * 优化：当逻辑分组表达式过于复杂（阈值可配置）或非法时，不执行查询，直接返回空数据
  * 优化：当指定的排序参数非法时，也不执行查询（之前是忽略排序），返回空数据
  * 优化：提升参数构建器性能，并将 `Builder.toFieldName` 方法标记为过时，新增 `FieldFns` 工具类
  * 优化：参数构建器新增 `asc(boolean sure)` 与 `desc(boolean sure)` 方法
  * 优化：参数构建器新增 `putAll(Map<String, ?> params)` 方法
  * 优化：当分页尺寸小于等于 `0` 时，不执行列表查询
  * 优化：标准化异常提示信息，全部英文化
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.params.filter.max-para-map-size` 配置项，默认 `150`
  * 新增 `bean-searcher.params.group.max-expr-length` 配置项，默认 `50`
  * 新增 `bean-searcher.params.pagination.max-allowed-offset` 配置项，默认 `20000`
  * 新增配置项校验：`bean-searcher.params.pagination.default-size` 的值不能比 `bean-searcher.params.pagination.max-allowed-size` 大，且都必须大于 `0`
  * 优化：标准化异常提示信息，全部英文化

# v3.8.0 @ 2022-07-23

## ✨ Features

* Bean Searcher
  * 增强 `@SearchBean` 注解：新增 `having` 属性，用于指定分组条件
  * 增强 `@DbField` 注解：新增 `type` 属性，允许用户手动指定该属性对应的数据库字段类型
  * 增强 `ParamResolver`: 新增 `Convertor` 参数转换器，可对检索参数的值类型转换为数据库字段匹配的类型（更好的兼容 Oracle、PgSQL 等数据库）
  * 新增 `BoolParamConvertor`、`NumberParamConvertor`、`DateParamConvertor`、`TimeParamConvertor` 与 `DateTimeParamConvertor` 参数转换器
  * 新增 `NotLike` 运算符（`nk`）：https://github.com/troyzhxu/bean-searcher/issues/50
  * 增强 `FieldOp.OpPara` ：新增 `getFieldSql(String field)` 方法，可以自定义运算符内获得其它字段的信息
  * 增强 `DefaultDbMapping`，当 `@SearchBean.tables` 的值是单表时，则省略 `@DbField` 的属性也自动映射
  * 增强 `参数构建器`：新增 `sql(..)` 方法，可为用于自定义 SQL 条件：https://github.com/troyzhxu/bean-searcher/issues/51
  * 新增 `根参数` 机制（用 `$` 表示根组，用户构造的组表达式不可以包含 `$`），参数构建器默认使用 根参数
  * 重构 `FieldConvertor`: 字段转换器的 9 个实现类 从 `com.ejlchina.searcher.implement` 包迁移到 `com.ejlchina.searcher.convertor` 包下
  * 重构 `DialectWrapper`：从 `com.ejlchina.searcher.implement` 包迁移到 `com.ejlchina.searcher.dialect` 包下
  * 重构 `@SearchBean` 注解：新增 `where` 替换原来的 `joinCond` 属性，并将 `joinCond` 标记为过时
  * 移除 `DateValueCorrector`，已被 `DateParamConvertor` 与 `DateTimeParamConvertor` 替代
  * 移除 `NullValueFilter` 与 `BoolValueFilter`（已被 `BoolParamConvertor` 替代）
  * 优化 分组动态查询条件生成机制：https://github.com/troyzhxu/bean-searcher/issues/56
  * 优化 当 `@SearchBean.tables` 的值是单表时，则省略注解的字段属性也自动映射
  * 优化 异常提示信息
* Bean Searcher Boot Starter
  * 支持 用户配置一个 `ParamResolver.Convertor` 的 Spring Bean 扩展参数值转换能力
  * 移除 `bean-searcher.sql.use-date-value-corrector` 配置项
  * 升级 `spring-boot` -> `v2.6.9`

# v3.7.1 @ 2022-06-22

## 🌻 Better

* Bean Searcher
  * 优化 `参数构建器`：新增 `orderBy(FieldFn<T, ?> fieldFn)`、`orderBy(String fieldName)`、`asc()` 与 `desc()` 方法
  * 优化 `参数构建器`：使 `onlySelect(..)` 与 `selectExclude(..)` 方法支持传入形如 `age,name` 这样以 `,` 分隔的字符串参数
* Bean Searcher Boot Starter
  * 优化：添加自定义参数过滤器时，不覆盖内置的参数过滤器

# v3.7.0 @ 2022-06-04

## ✨ Features

* Bean Searcher
  * 新增 `SqlServerDialect` 方言实现，支持 SqlServer 2012+
  * 新增 `OrLike` 运算符，参见：https://github.com/troyzhxu/bean-searcher/issues/38
  * 增强 `SqlExecutor`：新增 `SlowListener` 接口，可让用户在代码中监听慢 SQL
  * 增强 `DefaultDbMapping`：新增 `underlineCase` 属性，可配置自动映射时是否开启 `驼峰->下划线` 的风格转换
  * 增强 `Dialect`：新增 `hasILike()` 方法，当忽略大小写查询时，可利用数据库的 `ilike` 关键字提升查询性能
  * 增强 `EnumFieldConvertor`：支持 `整型` 转换为枚举（按枚举序号转换）
  * 增强 `EnumFieldConvertor`：新增 `failOnError` 属性, 可配置在遇到非法值无法转换时是否报错，默认 `true`
  * 增强 `EnumFieldConvertor`：新增 `ignoreCase` 属性, 可配置字符串值匹配枚举时是否忽略大小写，默认 `false`
  * 优化 `SQL 日志`：普通 SQL 显示执行耗时，慢 SQL 日志级别调整为 `WARN` 并输出关联的实体类
  * 优化 `DefaultSqlExecutor`，当执行 count sql 且查询结果为 `0` 时，则不再执行 list sql
  * 优化 `参数构建器` 的 `page(..)` 与 `limit(..)` 方法，它们起始页码也受页码配置约束（**破坏性更新**）
  * 优化 `Dialect`：为 `toUpperCase(..)` 添加默认实现，用户自定义方言时，只有一个 `forPaginate(..)` 方法必须实现
  * 优化 `DefaultParamResolver`：默认使用 `page` 分页参数提取器
  * 优化 `检索器` 的 count 与 sum 检索, 当无记录统计时，返回 `0` 而非 `null`, 并再次优化检索性能
  * 重构 `FetchType#ALL` 重命名为 `FetchType#DEFAULT`
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.slow-sql-threshold` 配置键，可配置慢 SQL 阈值（单位毫秒），默认为 `500`
  * 新增 `bean-searcher.sql.default-mapping.underline-case` 配置键，可配置自动映射时是否开始 驼峰->下划线 的风格转换，默认为 `true`
  * 新增 `bean-searcher.field-convertor.enum-fail-on-error` 配置键，可配置在遇到非法值无法转换时是否报错，默认 `true`
  * 新增 `bean-searcher.field-convertor.enum-ignore-case` 配置键，可配置字符串值匹配枚举时是否忽略大小写，默认 `false`
  * 支持 用户配置一个 `SqlExecutor.SlowListener` 的 Spring Bean 来监听慢 SQL
  * 支持 用户配置 `bean-searcher.sql.dialect` 为 `SqlServer` 来使用 Sql Server 方言 
  * 升级 `spring-boot` -> `v2.6.8`

# v3.6.3 v3.5.5 @ 2022-05-11

## 🐛 Bug Fixes

* 修复 JDK8 上当实体类有 `LocalDate` 类型的字段时会报 `NoSuchMethodError` 的问题: https://github.com/troyzhxu/bean-searcher/issues/43

# v3.6.2 @ 2022-05-11

## 🌻 Better

* Bean Searcher Boot Starter: 升级 `spring-boot -> 2.6.7`

# v3.5.4 @ 2022-05-11

## 🌻 Better

* Bean Searcher Boot Starter: 升级 `spring-boot -> 2.6.7`

## 🐛 Bug Fixes

* 修复：当实体类 `@SearchBean` 注解内指定 `groupBy` 属性时，不支持 字段求和 的问题
* 修复 `MapUtils.builder(..)` 的 `page(..)` 与 `limit(..)` 方法不受 `max-allowed-size` 配置约束的问题

# v3.6.1 @ 2022-05-09

## ✨ Features

* Bean Searcher
  * 新增 `BoolNumFieldConvertor` 字段转换器：支持 `Boolean -> Number` 方向的转换
  * 优化 `SearcherBuilder` 新增 `addResultFilter(..)` 方法
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.field-convertor.use-bool-num` 配置键，可自动配置 `BoolNumFieldConvertor`，默认为 `true`

## 🐛 Bug Fixes

* 修复 `MapUtils.builder(..)` 的 `page(..)` 与 `limit(..)` 方法不受 `max-allowed-size` 配置约束的问题

# v3.6.0 @ 2022-04-21

## ✨ Features

* Bean Searcher
  * 实体类 `@SearchBean` 注解内的 SQL 片段支持 `:` 的转义处理：`\\:`
  * 实体类 `@SearchBean` 注解新增 `orderBy` 属性，可指定默认的排序字段
  * 实体类 `@SearchBean` 注解新增 `sortType` 属性，可指定排序约束类型
  * 实体类 `@SearchBean` 注解指定 `groupBy` 属性时，支持 字段求和 查询
  * 检索器 新增 `ResultFilter` 机制，可让用户对检索结果统一做进一步的自定义处理
  * 新增 `PostgreSqlDialect` 方言实现，可用于 PostgreSql 数据库
  * 重构 `SqlResult`，与 JDBC 解耦，便于使用其它 ORM 重写 `SqlExecutor`
  * 重构 `SqlInterceptor`，它的 `intercept(..)` 方法新增 `FetchType` 参数
  * 新增 `B2MFieldConvertor`，可让 `BFieldConvertor` 也适用于 `MapSearcher` 检索器
  * 移除 `DefaultSqlExecutor` 的 `addDataSource(..)` 方法，该方法在 `v3.0.0` 被标记为过时
  * 移除 `DateFormatFieldConvertor` 的 `addFormat(..)` 方法，该方法在 `v3.0.1` 被标记为过时
  * 当使用 `in/Include` 运算符时，输出警告，提示使用 `ct/Contain` 运算符
  * 当使用 `mv/MultiValue` 运算符时，输出警告，提示使用 `il/InList` 运算符
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.default-mapping.inherit-type` 配置键，可在配置文件中指定默认的实体类继承类型，默认为 `ALL`
  * 新增 `bean-searcher.sql.default-mapping.sort-type` 配置键，可在配置文件中指定默认的排序约束类型，默认为 `ALLOW_PARAM`
  * 支持 `Spring Bean` 的方式为 `BeanSearcher` 与 `MapSearcher` 检索器添加 `ResultFilter` 过滤器
  * 配置 `bean-searcher.sql.dialect` 支持指定为 `PostgreSQL` 或 `PgSQL` 来使用 PostgreSql 方言
  * 新增 `bean-searcher.field-convertor.use-b2-m` 配置键，可在配置文件中指定是否启用 `B2MFieldConvertor`, 默认 `false`

# v3.5.3 @ 2022-04-06

## 🌻 Better

* Bean Searcher Boot Starter: 升级 spring-boot -> 2.6.6

## 🐛 Bug Fixes

* 修复：对于 `Boolean` 类型的字段，当检索时该字段传入的参数值为 `空串` 时，`BoolValueFilter` 会将其转换为 `true` 的问题：https://github.com/troyzhxu/bean-searcher/issues/29

# v3.5.2 @ 2022-03-17

## ✨ Features

* Bean Searcher: 参数构建器新增 `field(FieldFn<T, ?> fieldFn, Collection<?> values)` 与 `field(String fieldName, Collection<?> values)` 方法，支持字段值集合参数

## 🌻 Better

* Bean Searcher Boot Starter: 升级 spring-boot -> 2.6.4

## 🐛 Bug Fixes

* 修复当排序字段不在 SELECT 子句中时 ORDER BY 子句仍然会使用该字段的别名的问题

# v3.5.1 @ 2022-02-24

## 🌻 Better

* 强化对复杂逻辑表达式的简化能力
* 升级 slf4j-api -> 1.7.36

# v3.5.0 @ 2022-02-23

## ✨ Features

* Bean Searcher
  * 新增 `GroupResolver`、`ExprParser` 等组件，实现参数分组与逻辑关系的表达、运算、智能化简与解析的能力
  * `DefaultParamResolver` 新增 `gexprName`、`groupSeparator` 属性，用于指定组参数名的形式
  * `MapBuilder`（参数构建器）新增 `group(String group)` 方法，用于构建字段参数组
  * `MapBuilder` 新增 `groupExpr(String expr)` 方法，用于指定参数组间的逻辑关系
  * 新增 `TimeFieldConvertor`，支持 `java.sql.Time` 与 `LocalTime` 之间的转换 
  * 注解 `@DbField` 注解新增 `alias` 属性，支持手动指定字段别名（不指定则自动生成）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.params.group.enable` 配置键，可在配置文件中指定是否使用参数组功能，默认为 `true`
  * 新增 `bean-searcher.params.group.expr-name` 配置键，可在配置文件中指定组表达式参数名，默认为 `gexpr`
  * 新增 `bean-searcher.params.group.expr-cache-size` 配置键，可在配置文件中指定组表达式解析缓存的大小，默认为 `50` 个
  * 新增 `bean-searcher.params.group.separator` 配置键，可在配置文件中指定参数组分隔符，默认为 `.`
  * 新增 `bean-searcher.field-convertor.use-time` 配置项，表示是否自动添加 `TimeFieldConvertor`，默认 `true`

# v3.4.3 @ 2022-02-21

## 🐛 Bug Fixes

* 修复 `StartWith` 运算符不后模糊匹配的问题（该 BUG 在 `v3.4.2` 中滋生）

# v3.4.2 @ 2022-02-18

## 🌻 Better

* 带嵌入参数的字段也能参与过滤条件
* 带嵌入参数的字段也能参与字段统计

# v3.4.1 @ 2022-02-11

## 🌻 Better

* Bean Searcher
  * 优化 SQL 生成逻辑：当 `@SearchBean` 注解的 `joinCond` 属性只有一个拼接参数 且 该参数值为空时，则使其不参与 `where` 子句

## 🐛 Bug Fixes

* 修复 `DateFieldConvertor` 无法将 `java.sql.Date` 转换为 `LocalDate / LocalDateTime` 的问题
* 修复 `DateFieldConvertor` 转换 `LocalDate / LocalDateTime` 时会产生时区偏差的问题
* 修复 `DateFormatFieldConvertor` 无法格式化 `java.sql.Date / java.sql.Time` 的问题

# v3.3.3 @ 2022-02-11

## 🌻 Better

* Bean Searcher
  * bump slf4j-api from 1.7.32 to 1.7.35
  * 优化注解声明
  * 优化异常信息
* Bean Searcher Boot Starter
  * bump spring-boot from 2.6.2 to 2.6.3
  * 去掉无用的配置提示

## 🐛 Bug Fixes

* 修复 `DateFieldConvertor` 无法将 `java.sql.Date` 转换为 `LocalDate / LocalDateTime` 的问题
* 修复 `DateFieldConvertor` 转换 `LocalDate / LocalDateTime` 时会产生时区偏差的问题
* 修复 `DateFormatFieldConvertor` 无法格式化 `java.sql.Date / java.sql.Time` 的问题

# v3.2.4 @ 2022-02-11

## 🌻 Better

* Bean Searcher Boot Starter
  * 去掉无用的配置提示

## 🐛 Bug Fixes

* 修复 `DateFieldConvertor` 无法将 `java.sql.Date` 转换为 `LocalDate / LocalDateTime` 的问题
* 修复 `DateFieldConvertor` 转换 `LocalDate / LocalDateTime` 时会产生时区偏差的问题
* 修复 `DateFormatFieldConvertor` 无法格式化 `java.sql.Date / java.sql.Time` 的问题

# v3.1.4 @ 2022-02-11

## 🌻 Better

* Bean Searcher
  * 增强 `BoolFieldConvertor`，使支持 `Boolean -> boolean` 方向的转换
* Bean Searcher Boot Starter
  * 不再强制依赖 `DataSource`, 支持 Grails 项目
  * 去掉无用的配置提示

## 🐛 Bug Fixes

* 修复 `DateFieldConvertor` 无法将 `java.sql.Date` 转换为 `LocalDate / LocalDateTime` 的问题
* 修复 `DateFieldConvertor` 转换 `LocalDate / LocalDateTime` 时会产生时区偏差的问题
* 修复 `DateFormatFieldConvertor` 无法格式化 `java.sql.Date / java.sql.Time` 的问题

# v3.4.0 @ 2022-02-09

## ✨ Features

* Bean Searcher
  * 增强 `MapBuilder.orderBy(..)` 方法，支持多次调用来指定按多个字段进行排序 
  * 新增 `orderBy` 排序参数，可以类似 `orderBy=age:asc,time:desc` 的形式来指定多个排序字段
  * 重构 `DefaultSqlResolver`、`Dialect`、`SqlSnippet` 等相关类
  * 移除 `StringUtils.firstCharToUpperCase(..)` 方法
  * `DefaultDbMapping` 新增 `ignoreFields` 属性，支持配置全局忽略的属性
  * `@SearchBean` 新增 `ignoreFields` 属性，可配置单个实体类应该被忽略的属性（可忽略父类中的属性）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.default-mapping.ignore-fields` 配置项，可在配置文件中指定全局忽略的属性
  * 新增 `bean-searcher.params.order-by` 配置项，可在配置文件中指定排序参数的参数名

## 🌻 Better

* Bean Searcher
  * 优化异常信息
  * 优化注解声明
  * bump slf4j-api from 1.7.32 to 1.7.35
* Bean Searcher Boot Starter
  * 优化配置提示信息
  * bump spring-boot from 2.6.2 to 2.6.3

# v3.3.2 @ 2022-02-07

## 🌻 Better

* Bean Searcher
  * 增强 `BoolFieldConvertor`，使支持 `Boolean` -> `boolean` 方向的转换

# v3.2.3 @ 2022-01-30

## 🌻 Better

* Bean Searcher
  * 增强 `BoolFieldConvertor`，使支持 `Boolean` -> `boolean` 方向的转换
* Bean Searcher Boot Starter
  * 优化 `BeanSearcherAutoConfiguration` 不再强制依赖 `DataSource`
  * 支持在 Grails 项目中使用 `bean-searcher-boot-starter` 依赖

# v3.3.1 @ 2022-01-21

## ✨ Features

* Bean Searcher
  * `MapBuilder` 新增 `op(Class<? extends FieldOp> op)` 方法
  * 优化 `DateValueCorrector`, 可配置支持的运算符
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.use-date-value-corrector` 配置项，可配置 是否使用 `DateValueCorrector`
  * 支持在 Grails 项目中使用 `bean-searcher-boot-starter` 依赖

## 🌻 Better

* Bean Searcher
  * 优化字段运算符的匹配逻辑：使用严格模式
  * 优化 `Operator` 常量，使其可以直接作为 `@DbField.onlyOn` 的值（兼容以前版本，便于升级）
* Bean Searcher Boot Starter
  * 优化自动配置机制，支持无 `DataSource` 自动配置

## 🐛 Bug Fixes

* 修复当用户对同一个运算符 new 很多次时会导致 `FieldOpPool` 膨胀的问题


# v3.2.2 @ 2021-12-20

## 🌻 Better

* Bean Searcher
  * 优化 `DateValueCorrector`，使其支持 `LocalDateTime` 类型字段
* Bean Searcher Boot Starter
  * 优化自动配置机制，使其不依赖于 `DataSourceAutoConfiguration`，只要提供了 `DataSource` 就能自动配置
* Change LICENSE to Apache-2.0

# v3.3.0 @ 2022-01-19

## ✨ Features

* Bean Searcher
  * 新增 `FieldOp` 接口，用户可用之扩展自己的字段运算符
  * 新增 `FieldOpPool` 类，用户可用之定制一套全新的字段运算符
  * 内置新增 `NotIn` / `ni` 与 `NotBetween` / `nb` 运算符
  * 内置运算符 `MultiValue` / `mv` 重命名为 `InList` / `il` (原运算符仍可使用)
  * `DefaultDbMapping` 新增 `redundantSuffixes` 属性，可配置 在实体类自动映射表名时 统一去除类名中的冗余后缀（比如 VO、DTO 等）
  * 简化 `Dialect`: 移除 `truncateToDateStr`，`truncateToDateMinuteStr` 与 `truncateToDateSecondStr` 方法
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.default-mapping.redundant-suffixes` 配置项，可配置多个冗余后缀
  * 支持直接声明一个 `FieldOp` 类型的 Spring Bean 来扩展一个新的字段运算符
  * 支持直接声明一个 `FieldOpPool` 类型的 Spring Bean 来定制一套全新的字符运算符
* Change LICENSE to Apache-2.0

# v3.2.1 @ 2021-12-18

## 🌻 Better

* Bean Searcher
  * 无 `@DbIgnore` 注解也自动忽略实体类中的 `static` 与 `transient` 属性
  * 实体类支持子类重写父类中已存在的属性

## 🐛 Bug Fixes

* Bean Searcher
  * 修复非字符串字段使用 Empty/NotEmpty 运算符时会报错的问题：https://gitee.com/troyzhxu/bean-searcher/issues/I4N1MG

# v3.1.3 @ 2021-12-14

## 🌻 Better

* Bean Searcher
  * 无 `@DbIgnore` 也自动忽略实体类中的静态字段
* Bean Searcher Boot Starter
  * 使用 Searcher 类型注入检索器时，默认注入 MapSearcher，不再报错
  * 提高兼容性，SpringBoot 最低版本支持到 v1.4+
  
# v3.0.5 @ 2021-12-12

## 🌻 Better

* Bean Searcher
  * 无 `@DbIgnore` 也自动忽略实体类中的静态字段
* Bean Searcher Boot Starter
  * 使用 Searcher 类型注入检索器时，默认注入 MapSearcher，不再报错
  * 提高兼容性，SpringBoot 最低版本支持到 v1.4+

# v3.2.0 @ 2021-12-08

## ✨ Features

* Bean Searcher
  * 重构 `FieldConvertor`：移除冗余参数 `targetType`
  * 新增 `EnumFieldConvertor`：用来做枚举字段转换
  * 实体类 SearchBean 支持继承（可继承 @SearchBean 注解与映射字段）
  * 注解 `@SearchBean` 新增 `inheritType` 属性，可控制继承规则
  * 类 `DefaultDbMapping` 新增 `defaultInheritType` 属性，可配置实体类的默认继承规则
  * 实体类 SearchBean 的映射字段支持省略 Setter 方法
  * 新增 `ct`（`Contain`）运算符，用于取代 `in`（`Include`）运算符（使用 `in` 将输出警告）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.field-convertor.use-enum` 配置项，表示是否自动添加 `EnumFieldConvertor`，默认 `true`
  * 新增 `bean-searcher.use-map-searcher` 配置项，表示是否自动创建 `MapSearcher` 检索器，默认 `true`
  * 新增 `bean-searcher.use-bean-searcher` 配置项，表示是否自动创建 `BeanSearcher` 检索器，默认 `true`

## 🌻 Better

* Bean Searcher Boot Starter
  * 使用 Searcher 类型注入检索器时，默认注入 MapSearcher，不再报错
  * 提高兼容性，SpringBoot 最低版本支持到 v1.4+

# v3.1.2 & v3.0.4 @ 2021-12-02

## 🌻 Better

* 优化 `JDBC` 调用，兼容 `sharding-jdbc`
* 优化 `Operator.from(Object)` 方法
* 优化 `MapBuilder` 工具类，增加非空校验

# v3.1.1 & v3.0.3 @ 2021-12-01

## ✨ Features

* 优化 `DefaultSqlResolver` 的方法的权限修饰符，便于子类复用

## 🐛 Bug Fixes

* 优化别名生成规则，兼容 Oracle 数据库

# v3.1.0 @ 2021-11-15

## ✨ Features

* Bean Searcher
  * 新增 `BFieldConvertor` 与 `MFieldConvertor` 字段转换器子接口，提高字段转换效能
  * 新增 `DateFieldConvertor`，支持 `Date` 与 `LocalDateTime` 类型之间的转换
  * 增强 `DateFormatFieldConvertor`，使支持 `Temporal` 及其子类的对象的格式化
  * 增强 `DateFormatFieldConvertor`，新增：`setZoneId(ZoneId)` 方法，可配置时区
  * 增强 `DefaultSqlExecutor`，新增 `setTransactionIsolation(int level)` 方法，可配置隔离级别
  * 增强 `DbMapping`，使其完全接管数据映射的解析工作，并将 `DefaultDbMapping` 从 `DefaultMetaResolver` 的内部独立出来
  * 增强 `DefaultDbMapping`，使支持配置注解缺省时的表名前缀与是否开启大写映射，即支持默认映射大写的表名与列名
* Bean Searcher Boot Starter
  * 支持配置 `NamedDataSource` 类型的 Bean 来添加多个具名数据源
  * 自动添加 `spring-boot-starter-jdbc` 依赖
  * 默认自动配置添加 `NumberFieldConvertor`
  * 默认自动配置添加 `StrNumFieldConvertor`
  * 默认自动配置添加 `BoolFieldConvertor`
  * 默认自动配置添加 `DateFieldConvertor`
  * 默认自动配置添加 `DateFormatFieldConvertor`
  * 支持配置文件指定表名与字段默认小写映射 或 大写映射
  * 支持配置文件指定表名默认映射的前缀

# v3.0.2 @ 2021-11-10

## 🐛 Bug Fixes

* 修复：在 v3.0.0 中，单独使用 `searchCount` 和 `searchSum` 方法时，获取 SQL 结果会出错的问题

# v3.0.1 @ 2021-11-05

## ✨ Features

* DateFormatFieldConvertor 新增 setFormat 方法

## 🐛 Bug Fixes

* 修复：在 v3.0.0 中，再没有指定 @SearchBean 注解的 joinCond 属性时，带条件的 SQL 生成中 where 后少一个 左括号的问题

# v3.0.0 重大更新 @ 2021-11-04

## ✨ Features

* 支持 热加载
* 支持 无注解
* 支持 Select 指定字段
* 支持 条件与运算符的约束
* 支持 参数过滤器
* 支持 字段转换器
* 支持 Sql 拦截器
* 支持 多数据源
* 支持 JDK 9+

#### Bean Searcher

* 精简 Searcher 接口，移除一些无用的方法（最后一个形参为 `prefix` 的检索方法被移除）
* 架构优化：SearchBean 支持热加载，在配置了热加载的应用开发中，SearchBean 修改后，无需重启即可生效
* 移除 `SearchPlugin` 与 `SpringSearcher` 辅助类，因为 v3.0 的 Bean Searcher 的使用比借助辅助类更加容易
* 精简 `SearchResult` 类，移除没有必要的字段，只保留 `totalCount`、`dataList` 与 `summaries` 字段
* 新增 `Searcher` 的子接口：`MapSearcher` 与 `BeanSearcher` 与其相关实现，`MapSearcher` 中的检索方法返回的数据类型为 `Map`, `BeanSearcher` 中的检索方法返回的数据类型为泛型的 Search Bean
* 重构 `SearcherBuilder` 构建器, 使其更容易构建出一个 `MapSearcher` 或 `BeanSearcher` 实例
* 注解 `@SearchBean` 的 `groupBy` 属性，支持嵌入参数，嵌入参数未传入时，使用空字符串（以前使用 `"null"` 字符串）
* 抽象 `BeanReflector` 与 `FieldConvertor` 接口，使得 SearchBean 对象的反射机制更加解耦，更容易扩展与自定义
* 新增 `NumberFieldConvertor`、`StrNumFieldConvertor`、`BoolFieldConvertor` 与 `DateFormatFieldConvertor` 四个字段转换器实现，用户可以选择使用
* 新增 `DbMapping` 数据库映射接口，并提供基于下划线风格的映射实现，使得简单应用场景下，用户可以省略 `@SearchBean` 与 `@DbField` 注解
* 注解 `@SearchBean` 新增 `dataSource` 属性，用于指定该 SearchBean 从哪个数据源检索
* 注解 `@SearchBean` 新增 `autoMapTo` 属性，用于指定缺省 `@DbField` 注解的字段自动映射到那张表
* 新增 `@DbIgnore` 注解，用于指定 忽略某些字段，即添加该注解的字段不会被映射到数据库
* 注解 `@DbField` 新增 `conditional` 与 `onlyOn` 属性，使得用户可以控制该字段是否可以用作检索条件，以及当可作检索条件时支持哪些字段运算符
* 新增 `ParamAware` 接口，SearchBean 实现该接口时，可在 `afterAssembly(Map<String, Object> paraMap)` 方法里拿到原始检索参数
* 新增 onlySelect 与 selectExclude 参数（参数名可自定义），可用于指定只 Select 哪些字段，或者排除哪些字段
* 新增 `SqlInterceptor` 接口，实现 SQL 拦截器功能

#### Bean Searcher Boot Starter

* 简化使用，不再需要启动操作，不再需要配置 SearchBean 包名路径（移除了 `SearcherStarter` 类）
* Spring Boot 自动配置功能 独立到 Bean Searcher Boot Starter` 项目中，Bean Searcher 项目不再依赖 Spring

#### JDK

* 支持 JDK8+ 
* 兼容 JDK9+ 的模块引入机制
