# What's New?

For detailed version information, please refer to [Github](https://github.com/troyzhxu/bean-searcher/releases) and [Gitee](https://gitee.com/troyzhxu/bean-searcher/releases). This section introduces the new features introduced in each milestone version.

## V4 Version

### New Features in v4.4 (v4.4.1)

* Bean Searcher
  - Added `IndexArrayParamFilter`: Supports parsing parameters in the form `key[0]=v1 & key[1]=v2`.
  - Enhanced `SuffixOpParamFilter`: Supports merging case-insensitive checks into a single parameter, e.g., `name-ct-ic=xxx`.
  - Enhanced `MetaResolver`: Added `clearCache()` method to manually clear the `BeanMeta` cache.
  - Enhanced `@DbIgnore`: Supports annotating other custom annotations to inherit `@DbIgnore` functionality.
  - Added `AnnoUtils`: For parsing composite annotations.
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
