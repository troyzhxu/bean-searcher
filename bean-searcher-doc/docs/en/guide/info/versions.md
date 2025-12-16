# What's New?

For detailed version information, please refer to [Github](https://github.com/troyzhxu/bean-searcher/releases) and [Gitee](https://gitee.com/troyzhxu/bean-searcher/releases). This section introduces the new features introduced in each milestone version.

## V4 Version

### New Features in v4.8 (v4.8.2)

::: warning Note
Starting from version `v4.5`, default support is for `JDK17+`. To maintain compatibility with `JDK 8 ~ 16`, you can use the compatibility version with the `.jdk8` suffix, for example: `v4.8.2.jdk8`.
:::

* Bean Searcher
  - Optimized exception handling mechanism: when encountering unknown SQL syntax that cannot be processed, an exception is thrown to inform the user which part of the SQL cannot be parsed (since v4.8.1)
  - Compatible with PgSQL's `||` string concatenation operator (since v4.8.1)
* Bean Searcher Exporter
  - Added the `onlyIf` attribute to the `Export` annotation for defining field export conditions, supporting expression syntax.
    - Supports referencing values from the search parameter map (`paraMap`) using parameter names.
    - Supports `SpEL` expression syntax in Spring environments.
    - Supports `SnEL` expression syntax in Solon environments.
  - Added an export field resolution caching mechanism to the `ExportFieldResolver` component.
    - Introduced `ConcurrentHashMap` to cache export field resolution results, improving performance.
    - Added the `clearCache` method for manual cache clearing.
  - Optimized the `ExportField` component: when a field value is `null`, returns an empty string `""` and does not invoke the formatter (since v4.8.2).

### New Features in v4.7 (v4.7.1)

::: warning Note
Starting from version `v4.5`, default support is for `JDK17+`. To maintain compatibility with `JDK 8 ~ 16`, you can use the compatibility version with the `.jdk8` suffix, for example: `v4.7.1.jdk8`.
:::

* Bean Searcher
  - Optimized the `paraMap` parameter values for `ParamAware`, `ResultFilter`, and `SqlInterceptor`: uses non-null values processed by `ParamFilter` (since v4.7.1)
* Bean Searcher Exporter
  - The `BeanExporter` component added support for a series of export methods with the `Function<List<T>, List<T>> mapper` parameter, allowing secondary processing of the overall queried data before writing to the export file:
    - `export(String name, Class<T> beanClass, Function<List<T>, List<T>> mapper)`
    - `export(String name, Class<T> beanClass, int batchSize, Function<List<T>, List<T>> mapper)`
    - `export(String name, Class<T> beanClass, Map<String, Object> paraMap, Function<List<T>, List<T>> mapper)`
    - `export(String name, Class<T> beanClass, Map<String, Object> paraMap, int batchSize, Function<List<T>, List<T>> mapper)`
    - `export(FileWriter writer, Class<T> beanClass, Function<List<T>, List<T>> mapper)`
    - `export(FileWriter writer, Class<T> beanClass, int batchSize, Function<List<T>, List<T>> mapper)`
    - `export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, Function<List<T>, List<T>> mapper)`
    - `export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, int batchSize, Function<List<T>, List<T>> mapper)`

### New Features in v4.6 (v4.6.0)

::: warning Note
Starting from version `v4.5`, default support is for `JDK17+`. To maintain compatibility with `JDK 8 ~ 16`, you can use the compatibility version with the `.jdk8` suffix, for example: `v4.6.0.jdk8`.
:::

* Bean Searcher
  - Added the `allowBoolLiterals(): boolean` method definition to the `Dialect` interface, indicating whether the database supports boolean literals.
  - Added the `DialectSqlInterceptor` component to automatically convert boolean literals in user SQL to `1` and `0` when the database does not support them.
  - Added the `DaMengDialect` dialect implementation for compatibility with the **DaMeng** database.
  - Added `StringFieldConvertor`, which can convert JDBC-returned values of types `Clob`, `Number`, `Boolean`, and `Date` to `String` (compatible with cases where the DaMeng database returns `Clob` for `TEXT` type fields).
* Bean Searcher Boot Starter
  - Added the `bean-searcher.field-convertor.use-string` configuration item to control whether to enable `StringFieldConvertor`. Default is `true`.
* Bean Searcher Solon Plugin
  - Added the `bean-searcher.field-convertor.use-string` configuration item to control whether to enable `StringFieldConvertor`. Default is `true`.

### New Features in v4.5 (v4.5.2)

::: warning Note
Starting from version `v4.5`, default support is for `JDK17+`. To maintain compatibility with `JDK 8 ~ 16`, you can use the compatibility version with the `.jdk8` suffix, for example: `v4.5.2.jdk8`.
:::

* Bean Searcher
  - Added `maxSize` and `maxOffset` attributes to the `@SearchBean` annotation, allowing individual search classes to set their own risk control values, overriding global configurations.
  - Added an `extract` method to the `PageExtractor` interface, supporting the inclusion of a `BeanMeta` parameter.
  - Added the `groupRoot(String groupSeparator)` method to the parameter builder, allowing ordinary parameter groups passed from the frontend to be added to the root group.
  - Added the `groupRoot()` method to the parameter builder, which uses the default group separator to add ordinary parameter groups passed from the frontend to the root group.
  - Optimized `DefaultSqlExecutor`: If SQL execution fails, the SQL log level is elevated from `DEBUG` to `ERROR` (since v4.5.1).
  - Optimized `DefaultSqlExecutor`: If JDBC fails at the `prepareStatement` stage, the problematic SQL is also printed (since v4.5.1).
  - Optimized `EnumFieldConvertor`: to support conversion from `short` and `byte` to enums. Previously, only `String` and `int` types were supported (since v4.5.2).
* Bean Searcher Exporter (**Initial Release**: Data Export Module)
  - Added the `Export` annotation to mark fields for export (supports expression-based value conversion).
  - Added the `BeanExporter` exporter, making data export as simple as querying (supports **real-time pagination** export, **immediate frontend response**, and built-in **concurrency control**).
  - Added the `FileWriter` interface for extending export file types. The default implementation `CsvFileWriter` exports CSV files.
  - Customizable export filename decorator `FileNamer`.
  - Customizable data batch loading delay strategy `DelayPolicy`. The default uses a random inflation delay strategy `DelayPolicy.RandomInflate`.
  - And more...
* Bean Searcher Boot Starter
  - Added the `bean-searcher.exporter.batch-size` configuration item: specifies the default number of records queried per batch during data export. Default is `1000`.
  - Added the `bean-searcher.exporter.batch-delay` configuration item: initial delay time after each batch query, default 100 milliseconds, to reduce database pressure. Default is `100ms`.
  - Added the `bean-searcher.exporter.max-exporting-threads` configuration item: maximum number of concurrent exports. When the number of simultaneous exporters reaches this value (default `10`), new exporters will be in a waiting state.
  - Added the `bean-searcher.exporter.max-threads` configuration item: maximum thread count. When there are too many simultaneous exporters (default `30`), new exports will not be accepted (new exporters will receive a prompt to try later or an exception, with specific behavior determined by `FileWriter`).
  - Added the `bean-searcher.exporter.timestamp-filename` configuration item: whether to automatically append the current timestamp to the exported filename. Default is `true`.
  - Added the `bean-searcher.exporter.too-many-requests-message` configuration item: the prompt message returned when there are too many exporters. Default is: "大人请息怒，当前导出数据的人实在太多了，请稍后再试一下子哈！" 
* Bean Searcher Solon Plugin
  - Added the `bean-searcher.exporter.batch-size` configuration item: specifies the default number of records queried per batch during data export. Default is `1000`.
  - Added the `bean-searcher.exporter.batch-delay` configuration item: initial delay time after each batch query, default 100 milliseconds, to reduce database pressure. Default is `100ms`.
  - Added the `bean-searcher.exporter.max-exporting-threads` configuration item: maximum number of concurrent exports. When the number of simultaneous exporters reaches this value (default `10`), new exporters will be in a waiting state.
  - Added the `bean-searcher.exporter.max-threads` configuration item: maximum thread count. When there are too many simultaneous exporters (default `30`), new exports will not be accepted (new exporters will receive a prompt to try later or an exception, with specific behavior determined by `FileWriter`).
  - Added the `bean-searcher.exporter.timestamp-filename` configuration item: whether to automatically append the current timestamp to the exported filename. Default is `true`.
  - Added the `bean-searcher.exporter.too-many-requests-message` configuration item: the prompt message returned when there are too many exporters. Default is: "大人请息怒，当前导出数据的人实在太多了，请稍后再试一下子哈！"

### New Features in v4.4 (v4.4.3)

* Bean Searcher
  - Added `IndexArrayParamFilter`: Supports parsing parameters in the form `key[0]=v1 & key[1]=v2`.
  - Enhanced `SuffixOpParamFilter`: Supports merging case-insensitive checks into a single parameter, e.g., `name-ct-ic=xxx`.
  - Enhanced `MetaResolver`: Added `clearCache()` method to manually clear the `BeanMeta` cache.
  - Enhanced `@DbIgnore`: Supports annotating other custom annotations to inherit `@DbIgnore` functionality.
  - Added `AnnoUtils`: For parsing composite annotations.
  - Enhanced `MapUtils` utility class (since v4.4.2)
    - Added `flat(Map<String, String[]> map, boolean urlDecode)` method to support specifying whether to perform URL decoding
    - Added `flatBuilder(Map<String, String[]> map, boolean urlDecode)` method to support specifying whether to perform URL decoding
    - The original `flat(Map<String, String[]> map)` method now performs URL decoding by default
    - The original `flatBuilder(Map<String, String[]> map)` method now performs URL decoding by default
  - Optimized `EnumFieldConvertor`: to support conversion from `short` and `byte` to enums. Previously, only `String` and `int` types were supported (since v4.4.3).
  - Optimized `DefaultSqlExecutor`: If SQL execution fails, the SQL log level is elevated from `DEBUG` to `ERROR` (since v4.4.3).
  - Optimized `DefaultSqlExecutor`: If JDBC fails at the `prepareStatement` stage, the problematic SQL is also printed (since v4.4.3).
* Bean Searcher Label (**First Release**)
  - Introduced `@LabelFor` annotation (inherits `@DbIgnore`) to label and resolve Label fields (e.g., marking `statusName` as the Label for `status`).
  - Added `LabelLoader` interface for loading custom Labels.
  - Provided `EnumLabelLoader` implementation for loading enum Labels.
  - Optimized `LabelLoader` interface to ensure the `ids` parameter in `load(key, ids)` is non-empty (since v4.4.1).
  - Enhanced `@LabelFor` to support fields in parent and child classes (since v4.4.1).
* Bean Searcher Boot Starter
  - Added configuration `bean-searcher.params.filter.use-index-array` to enable `IndexArrayParamFilter` (default: `false`).
  - Added automated configuration for Bean Searcher Label.
* Bean Searcher Solon Plugin
  - Added configuration `bean-searcher.params.filter.use-index-array` to enable `IndexArrayParamFilter` (default: `false`).
  - Added automated configuration for Bean Searcher Label.

### New Features in v4.3 (v4.3.6)

* Bean Searcher
  - Optimized `BeanMeta`: Added `getSqlSnippets()` to retrieve all parsed SQL snippets on an entity class.
  - Enhanced `SearchSql`: Added `getSearchParam()` to access resolved search parameters in `SqlInterceptor`.
  - Enhanced `MapBuilder`: Added `or(..)` and `and(..)` for simplifying logical grouping. See [issue](https://gitee.com/troyzhxu/bean-searcher/issues/I9T66B).
  - Enhanced `MapBuilder`: Added `buildForRpc()` and `buildForRpc(RpcNames)` for building parameters for remote API calls.
  - Optimized `MapBuilder`: Methods `field(FieldFn, Collection)` and `field(String, Collection)` now accept `null` as the second parameter.
  - Enhanced `DefaultParamResolver`: Added `gexprMerge` to control merging/overriding group expressions. See [issue](https://gitee.com/troyzhxu/bean-searcher/issues/I9TAV6).
  - Added `JoinParaSerializer`: Serializes collection-type parameters into comma-separated strings.
  - Added `ArrayValueParamFilter`: Compatible with array parameters like `age=20 & age=30 & age-op=bt`.
  - Added `SuffixOpParamFilter`: Simplifies parameters (e.g., `age-gt=25` instead of `age=25 & age-op=gt`).
  - Added `JsonArrayParamFilter`: Simplifies parameters (e.g., `age=[20,30]` instead of `age-0=20 & age-1=30`).
  - Added `AlwaysTrue` (`at`) and `AlwaysFalse` (`af`) operators. See [issue](https://gitee.com/troyzhxu/bean-searcher/issues/I9TMFI).
  - Upgraded `OracleDialect`: Uses `offset ? rows fetch next ? rows only` syntax (requires Oracle 12c+).
  - Optimized `ExprParser`: Logical operators `&` and `|` are no longer customizable.
  - Enhanced `DateTimeParamConvertor`: Supports timestamp values (since v4.3.2).
  - Optimized `BeanMeta` to preserve field declaration order (since v4.3.3).
  - Fixed caching order in `DefaultGroupResolver` (since v4.3.3).
  - Enhanced `DateParamConvertor` to support multiple date formats (since v4.3.5).
  - Enhanced `DateTimeParamConvertor` to support flexible datetime formats (since v4.3.5).
  - Fixed `EnumParamConvertor` compatibility with empty strings.
  - Upgraded Junit to 5.10.2.
* Bean Searcher Boot Starter
  - Added configurations: `bean-searcher.params.group.mergeable`, `bean-searcher.params.filter.use-size-limit`, `bean-searcher.params.filter.use-array-value`, `bean-searcher.params.filter.use-suffix-op`, `bean-searcher.params.filter.use-json-array`.
  - Added `SpringSqlExecutor` for Spring transaction support (since v4.3.2).
  - Added timezone configuration for `DateTimeParamConvertor` (since v4.3.2).
* Bean Searcher Solon Plugin
  - Added configurations similar to Boot Starter.
  - Added `SolonSqlExecutor` for Solon transaction support (since v4.3.2).
  - Supports IDE configuration hints with Solon plugin (since v4.3.5).

> Reference sections: [Field Parameters](/en/guide/param/field), [Logical Grouping](/en/guide/param/group), [Parameter Filters](/en/guide/advance/filter), [Embedded Parameters](/en/guide/param/embed#拼接参数), [Requesting Third-Party BS Services](/en/guide/usage/rpc).



### New Features in v4.2 (v4.2.9)

* Bean Searcher
  - Added `DynamicDialect` and `DynamicDialectSupport` for dynamic dialects (v4.2.0).
  - Renamed `FieldParam.Value.isEmptyValue()` to `isEmpty()` (v4.2.0).
  - Renamed `AbstractSearcher` to `BaseSearcher` (v4.2.0).
  - Enhanced `DateTimeParamConvertor` to parse `yyyy-MM-dd HH:mm:ss.SSS`, `yyyy-MM`, and `yyyy` formats (v4.2.0).
  - Added `EnumParamConvertor` for enum serialization (v4.2.1).
  - Enhanced `JsonFieldConvertor` for complex JSON array conversion (v4.2.6).
* Bean Searcher Boot Starter
  - Added `bean-searcher.sql.dialect-dynamic` to enable dynamic dialects (default: `false`).
  - Added `bean-searcher.params.fail-on-error` to control exception throwing (v4.2.3).
* Bean Searcher Solon Plugin
  - Similar updates as Boot Starter.

### New Features in v4.1 (v4.1.2)

* Bean Searcher
  - Enhanced `@DbField` with `name`, `cluster`, and `mapTo` attributes.
  - Added dynamic condition fields via `@SearchBean.fields`.
  - Optimized logical expression simplification.
* Bean Searcher Boot Starter
  - Customizable `GroupPairResolver` via Bean injection.
* Bean Searcher Solon Plugin
  - Initial Solon plugin release.

### New Features in v4.0 (v4.0.2)

* Bean Searcher
  - GroupId changed to `cn.zhxu`.
  - Package renamed to `cn.zhxu.bs`.
  - Added `timeout` property in `@SearchBean`.
  - Introduced JSON field support via `JsonFieldConvertor`.
  - Enhanced `MapUtils` with convenient Map construction methods.
* Bean Searcher Boot Starter
  - Supports Spring Boot 3.
  - Added configurations for JSON and List field conversion.

## V3 Version

The version is too old, and the author is too lazy to translate it.

## V2 Version

This version is too old; details are unavailable.

## V1 Version

This version is too old; details are unavailable.
