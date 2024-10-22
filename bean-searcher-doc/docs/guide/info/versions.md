# What's New?

详细的版本信息请查阅 [Github](https://github.com/troyzhxu/bean-searcher/releases) 与 [Gitee](https://gitee.com/troyzhxu/bean-searcher/releases) , 本节主要介绍每个里程碑版本引入的新特性。

## V4 版本

### v4.3 的新特性（v4.3.4）

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
  * 增强 `DateTimeParamConvertor`: 使支持整型的时间戳参数值（since v4.3.2）
  * 优化 `BeanMeta` 可保持检索实体类中的字段声明顺序（即条件生成顺序）（since v4.3.3）
  * 优化 `DefaultParamResolver` 提升 `extractFieldParams(..)` 方法的权限，可供子类重写，便于用户自定义（since v4.3.3）
  * 优化 `DefaultMetaResolver`: 抽取 `createBeanMeta(..)` 方法，便于用户自定义 `BeanMeta` 子类（since v4.3.3）
  * 修复 `DefaultGroupResolver` 的默认 `LRUCache` 缓存没有遵循访问顺序规则的问题（since v4.3.3）
  * 升级 Junit -> 5.10.2 并完善单元测试
* Bean Searcher Boot Starter
  * 新增配置项 `bean-searcher.params.group.mergeable` 指定组表达式是否可合并，默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-size-limit` 是否启用 `SizeLimitParamFilter`，默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-array-value` 是否启用 `ArrayValueParamFilter`, 默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-suffix-op` 是否启用 `SuffixOpParamFilter`, 默认 `false`
  * 新增配置项 `bean-searcher.params.filter.use-json-array` 是否启用 `JsonArrayParamFilter`, 默认 `false`
  * 支持以 Bean 的形式自定义 `JoinParaSerializer` 组件
  * 新增 `SpringSqlExecutor`: 支持 Spring 事务的 Sql 执行器，且默认使用（since v4.3.2）
  * 新增配置项：`bean-searcher.params.convertor.zone-id`: 可配置 `DateTimeParamConvertor` 使用的时区（since v4.3.2）
* Bean Searcher Solon Plugin
  * 新增配置项 `bean-searcher.params.group.mergeable` 指定组表达式是否可合并，默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-size-limit` 是否启用 `SizeLimitParamFilter`，默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-array-value` 是否启用 `ArrayValueParamFilter`, 默认 `true`
  * 新增配置项 `bean-searcher.params.filter.use-suffix-op` 是否启用 `SuffixOpParamFilter`, 默认 `false`
  * 新增配置项 `bean-searcher.params.filter.use-json-array` 是否启用 `JsonArrayParamFilter`, 默认 `false`
  * 支持以 Bean 的形式自定义 `JoinParaSerializer` 组件
  * 新增 `SolonSqlExecutor`: 支持 Solon 事务的 Sql 执行器，且默认使用（since v4.3.2）
  * 新增配置项：`bean-searcher.params.convertor.zone-id`: 可配置 `DateTimeParamConvertor` 使用的时区（since v4.3.2）

> 参考章节：[字段参数](/guide/param/field)、[逻辑分组](/guide/param/group)、[参数过滤器](/guide/advance/filter)、[拼接参数](/guide/param/embed#拼接参数)、[请求第三方 BS 服务](/guide/usage/rpc)

### v4.2 的新特性（v4.2.9）

* Bean Searcher
  * 新增 `DynamicDialect` 与 `DynamicDialectSupport` 类，用于支持动态方言（v4.2.0）
  * 重构 `FieldParam.Value.isEmptyValue()` 方法重命名为 `FieldParam.Value.isEmpty()`（v4.2.0）
  * 重构 `AbstractSearcher` 重命名为 `BaseSearcher`（v4.2.0）
  * 优化 `DateTimeParamConvertor`, 使其支持解析 `yyyy-MM-dd HH:mm:ss.SSS`、`yyyy-MM` 与 `yyyy` 格式的参数（v4.2.0）
  * 增强 `DateFieldConvertor` 支持 `Instant` 类型的转换：https://gitee.com/troyzhxu/bean-searcher/pulls/9（v4.2.1）
  * 新增 `EnumParamConvertor` 对于枚举字段，可将 `String/Emun` 类型的参数自动转换为 `枚举序号`（默认）或 `枚举名`（由 `@DbField.type` 决定）（v4.2.1）
  * 优化 `PreparedStatement.setObject(..)` 方法报错时，仍然打印 SQL 日志（v4.2.1）
  * 增强 `JsonFieldConvertor`，支持将 `非 String` 类型（例如：`PGobject`）的 JSON 值转换为对象（v4.2.2）
  * 增强 `BaseSearcher`，新增 `failOnParamError` 属性，可配置当参数错误时是否向外抛出异常，默认 `false`（v4.2.3）
  * 增强 `DateParamConvertor`，支持 `java.util.Date` 子类的转换，并新增 `target` 属性，支持配置转换目标类型（v4.2.3）
  * 增强 `DateTimeParamConvertor`，支持 `java.util.Date` 子类的转换，并新增 `target` 属性，支持配置转换目标类型（v4.2.3）
  * 增强 `TimeParamConvertor`，新增 `target` 属性，支持配置转换目标类型（v4.2.3）
  * 增强 `JsonFieldConvertor`：使支持复杂 `JSON` 数组到泛型 `List<T>` 的字段转换（v4.2.6）
* Bean Searcher Boot Starter
  * 新增配置项 `bean-searcher.sql.dialect-dynamic`, 表示是否启用动态方言，默认 `false`（v4.2.0）
  * 当启用动态数据源时，支持以 `DataSourceDialect` 注入 Bean 的方式添加数据源与方言的映射关系（v4.2.0）
  * 自动配置 `EnumParamConvertor`（v4.2.1）
  * 新增配置项 `bean-searcher.params.fail-on-error` 指定参数错误时，是否抛出异常，默认 `false`（v4.2.3）
  * 新增配置项 `bean-searcher.params.convertor.date-target`（v4.2.3）
  * 新增配置项 `bean-searcher.params.convertor.date-time-target`（v4.2.3）
  * 新增配置项 `bean-searcher.params.convertor.time-target`（v4.2.3）
* Bean Searcher Solon Plugin
  * 新增配置项 `bean-searcher.sql.dialect-dynamic`, 表示是否启用动态方言，默认 `false`（v4.2.0）
  * 当启用动态数据源时，支持以 `DataSourceDialect` 注入 Bean 的方式添加数据源与方言的映射关系（v4.2.0）
  * 自动配置 `EnumParamConvertor`（v4.2.1）
  * 新增配置项 `bean-searcher.params.fail-on-error` 指定参数错误时，是否抛出异常，默认 `false`（v4.2.3）
  * 新增配置项 `bean-searcher.params.convertor.date-target`（v4.2.3）
  * 新增配置项 `bean-searcher.params.convertor.date-time-target`（v4.2.3）
  * 新增配置项 `bean-searcher.params.convertor.time-target`（v4.2.3）

### v4.1 的新特性（v4.1.2）

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
  * 支持 `GroupPairResolver` 以注入 Bean 的方式自定义
* Bean Searcher Solon Plugin
  * 首发 Solon 插件（功能同 `bean-searcher-boot-starter`）

### v4.0 的新特性（v4.0.2）

* Bean Searcher
  * Maven 坐标 groupId 变更 -> `cn.zhxu`
  * 包名变更：`com.ejlchina.searcher` -> `cn.zhxu.bs`
  * 移除过时 API: `@SearchBean` 注解的 `joinCond` 属性
  * 优化嵌入参数：提高兼容性，支持嵌入参数后紧跟了 `.` 符号
  * 当字段是数字，但传参不是数字时，直接返回空数据
  * 注解 `@SearchBean` 添加 `timeout` 属性，用于控制慢 SQL 最大执行时长
  * 当使用 groupBy 与 逻辑分组时，如果所传参数都在 groupBy 内，也使用 where 形式的条件: https://gitee.com/troyzhxu/bean-searcher/issues/I5V4ON
  * 移除 Searcher 接口内的 search()、searchFirst()、searchList() 与 searchAll() 方法
  * 新增方法：`search(Class<T> beanClass) -> SearchResult`
  * 新增方法：`search(Class<T> beanClass, String summaryField) -> SearchResult`
  * 新增方法：`search(Class<T> beanClass, String[] summaryFields) -> SearchResult`
  * 新增方法：`search(Class<T> beanClass, FieldFns.FieldFn<T, ?> summaryField) -> SearchResult`
  * 新增方法：`search(Class<T> beanClass, Map<String, Object> paraMap, String summaryField) -> SearchResult`
  * 新增方法：`search(Class<T> beanClass, Map<String, Object> paraMap, FieldFns.FieldFn<T, ?> summaryField) -> SearchResult`
  * 新增方法：`searchFirst(Class<T> beanClass) -> T / Map<String, Object>`
  * 新增方法：`searchList(Class<T> beanClass) -> List<T> / List<Map<String, Object>>`
  * 新增方法：`searchAll(Class<T> beanClass) -> List<T> / List<Map<String, Object>>`
  * 新增方法：`searchCount(Class<T> beanClass) -> Number`
  * 新增方法：`searchSum(Class<T> beanClass, String field) -> Number`
  * 新增方法：`searchSum(Class<T> beanClass, String[] fields) -> Number[]`
  * 新增方法：`searchSum(Class<T> beanClass, FieldFns.FieldFn<T, ?> field) -> Number`
  * 新增方法：`searchSum(Class<T> beanClass, Map<String, Object> paraMap, FieldFns.FieldFn<T, ?> field) -> Number`
  * 新增 `JsonFieldConvertor` 字段转换器，配合 `@DbField(type = DbType.JSON)` 可支持 JSON 字段自动转对象，需要添加 JSON 依赖（以下依赖任选一个即可）：
    * `cn.zhxu:xjsonkit-fastjson:1.5.0`
    * `cn.zhxu:xjsonkit-fastjson2:1.5.0`
    * `cn.zhxu:xjsonkit-gson:1.5.0`
    * `cn.zhxu:xjsonkit-jackson:1.5.0`
    * `cn.zhxu:xjsonkit-snack3:1.5.0`
    * 参考：https://gitee.com/troyzhxu/xjsonkit
  * 增强 `JsonFieldConvertor` 字段转换器，新增 `failOnError` 字段，可配置遇到某些值 JSON 解析错误时，是否抛出异常
  * 新增 `ListFieldConvertor` 字段转换器，可支持将 字符串字段自动转为简单 List 对象
  * 增强 `NumberFieldConvertor`，使支持 `BigDecimal` 与 `Integer Long Float Double Short Byte` 之间的相互转换
  * 增强 `DefaultDbMapping`，新增 `setAroundChar(String)` 方法，支持配置标识符的围绕符，以区分系统保留字（只对自动映射的表与字段起作用）
  * 增强 `MapUtils`，新增 `of(k, v)`, `of(k1, v1, k2, v2)` 等 4 个 便捷 Map 构造方法
  * 重构 `cn.zhxu.bs.param.Operator` -> `cn.zhxu.bs.FieldOps`

* Bean Searcher Boot Starter
  * 支持 Spring Boot 3（仍然支持 SpringBoot 1.4 ~ 2.x）
  * 新增 `bean-searcher.sql.default-mapping.around-char` 配置项，可配置标识符的围绕符（例如 MySQL 的 ` 符）
  * 新增 `bean-searcher.field-convertor.use-json` 配置项，表示是否自动添加 `JsonFieldConvertor`，默认 `true`
  * 新增 `bean-searcher.field-convertor.json-fail-on-error` 配置项，表示 JSON 解析错误时，是否抛出异常，默认 `true`
  * 新增 `bean-searcher.field-convertor.use-list` 配置项，表示是否自动添加 `ListFieldConvertor`，默认 `true`
  * 新增 `bean-searcher.field-convertor.list-item-separator` 配置项，用于配置如何将一个字符串分割成 `List` 字段
  * 优化 `bean-searcher.field-convertor.date-formats` 配置项，支持用 `-` 替代 `:`（因为在 yml 的 key 中 `:` 默认会被过滤掉）

## V3 版本

### v3.8 的新特性（v3.8.2）

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
  * 新增：分页大深度保护，默认最大允许分页偏移 `20000` 条（since v3.8.1）
  * 优化：当检索参数过于庞大（阈值可配置）时，不执行查询，直接返回空数据（since v3.8.1）
  * 优化：当逻辑分组表达式过于复杂（阈值可配置）或非法时，不执行查询，直接返回空数据（since v3.8.1）
  * 优化：当指定的排序参数非法时，也不执行查询（之前是忽略排序），返回空数据（since v3.8.1）
  * 优化：提升参数构建器性能，并将 `Builder.toFieldName` 方法标记为过时，新增 `FieldFns` 工具类（since v3.8.1）
  * 优化：参数构建器新增 `asc(boolean sure)` 与 `desc(boolean sure)` 方法（since v3.8.1）
  * 优化：参数构建器新增 `putAll(Map<String, ?> params)` 方法（since v3.8.1）
  * 优化：当分页尺寸小于等于 `0` 时，不执行列表查询（since v3.8.1）
  * 优化：标准化异常提示信息，全部英文化（since v3.8.1）
* Bean Searcher Boot Starter
  * 支持 用户配置一个 `ParamResolver.Convertor` 的 Spring Bean 扩展参数值转换能力
  * 移除 `bean-searcher.sql.use-date-value-corrector` 配置项
  * 升级 `spring-boot` -> `v2.6.9`
  * 新增 `bean-searcher.params.filter.max-para-map-size` 配置项，默认 `150`（since v3.8.1）
  * 新增 `bean-searcher.params.group.max-expr-length` 配置项，默认 `50`（since v3.8.1）
  * 新增 `bean-searcher.params.pagination.max-allowed-offset` 配置项，默认 `20000`（since v3.8.1）
  * 新增配置项校验：`bean-searcher.params.pagination.default-size` 的值不能比 `bean-searcher.params.pagination.max-allowed-size` 大，且都必须大于 `0`（since v3.8.1）
  * 优化：标准化异常提示信息，全部英文化（since v3.8.1）

### v3.7 的新特性（v3.7.1）

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
  * 优化 `参数构建器`：新增 `orderBy(FieldFn<T, ?> fieldFn)`、`orderBy(String fieldName)`、`asc()` 与 `desc()` 方法（since v3.7.1）
  * 优化 `参数构建器`：使 `onlySelect(..)` 与 `selectExclude(..)` 方法支持传入形如 `age,name` 这样以 `,` 分隔的字符串参数（since v3.7.1）
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
  * 优化：添加自定义参数过滤器时，不覆盖内置的参数过滤器（since v3.7.1）
  * 升级 `spring-boot` -> `v2.6.8`

### v3.6 的新特性（v3.6.3）

* Bean Searcher
  * 实体类 `@SearchBean` [注解内的 SQL 片段支持 `:` 的 转义（`\\:`）语义](/guide/bean/params#前缀符转义-since-v3-6-0)；
  * 实体类 `@SearchBean` [注解新增 `orderBy` 属性，可指定 默认排序字段](/guide/bean/otherform#默认排序-since-v3-6-0)；
  * 实体类 `@SearchBean` [注解新增 `sortType` 属性，可指定 排序约束类型](/guide/bean/otherform#排序约束-since-v3-6-0)；
  * 实体类 `@SearchBean` 注解指定 `groupBy` 属性时，支持 字段求和 查询
  * 检索器 [新增 `ResultFilter` 机制](/guide/advance/filter#结果过滤器-v3-6-0)，可让用户对检索结果统一做进一步的自定义处理
  * 新增 [`PostgreSqlDialect` 方言实现](/guide/advance/dialect#方言实现)，可用于 PostgreSql 数据库
  * 重构 `SqlResult`，与 JDBC 解耦，便于使用其它 ORM 重写 `SqlExecutor`
  * 重构 `SqlInterceptor`，[它的 `intercept(..)` 方法新增 `FetchType` 参数](/guide/advance/interceptor)
  * 新增 `B2MFieldConvertor`，[可让 `BFieldConvertor` 也适用于 `MapSearcher` 检索器](/guide/advance/convertor.html#b2mfieldconvertor)
  * 移除 `DefaultSqlExecutor` 的 `addDataSource(..)` 方法，该方法在 `v3.0.0` 被标记为过时
  * 移除 `DateFormatFieldConvertor` 的 `addFormat(..)` 方法，该方法在 `v3.0.1` 被标记为过时
  * 当使用 `in/Include` 运算符时，输出警告，提示使用 `ct/Contain` 运算符
  * 当使用 `mv/MultiValue` 运算符时，输出警告，提示使用 `il/InList` 运算符
  * 新增 `BoolNumFieldConvertor` 字段转换器：支持 `Boolean -> Number` 方向的转换（since v3.6.1）
  * 优化 `SearcherBuilder` 新增 `addResultFilter(..)` 方法（since v3.6.1）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.default-mapping.inherit-type` 配置键，可在配置文件中指定默认的实体类继承类型，默认为 `ALL`
  * 新增 `bean-searcher.sql.default-mapping.sort-type` 配置键，可在配置文件中指定默认的排序约束类型，默认为 `ALLOW_PARAM`
  * 支持 `Spring Bean` 的方式为 `BeanSearcher` 与 `MapSearcher` 检索器添加 `ResultFilter` 过滤器
  * 配置 `bean-searcher.sql.dialect` 支持指定为 `PostgreSQL` 或 `PgSQL` 来使用 PostgreSql 方言
  * 新增 `bean-searcher.field-convertor.use-b2-m` 配置键，可在配置文件中指定是否启用 `B2MFieldConvertor`, 默认 `false`
  * 新增 `bean-searcher.field-convertor.use-bool-num` 配置键，可自动配置 `BoolNumFieldConvertor`，默认为 `true`（since v3.6.1）

### v3.5 的新特性（v3.5.5）

* Bean Searcher
  * 新增 `GroupResolver`、`ExprParser` 等组件，实现参数分组与逻辑关系的表达、运算、智能化简与解析的能力
  * `DefaultParamResolver` 新增 `gexprName`、`groupSeparator` 属性，用于指定组参数名的形式
  * `MapBuilder`（参数构建器）新增 `group(String group)` 方法，用于构建字段参数组
  * `MapBuilder` 新增 `groupExpr(String expr)` 方法，用于指定参数组间的逻辑关系
  * 新增 `TimeFieldConvertor`，支持 `java.sql.Time` 与 `LocalTime` 之间的转换 
  * 注解 `@DbField` 注解新增 `alias` 属性，支持手动指定字段别名（不指定则自动生成）
  * 参数构建器新增 `field(FieldFn<T, ?> fieldFn, Collection<?> values)` 与 `field(String fieldName, Collection<?> values)` 方法，支持字段值集合参数（since v3.5.2）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.params.group.enable` 配置键，可在配置文件中指定是否使用参数组功能，默认为 `true`
  * 新增 `bean-searcher.params.group.expr-name` 配置键，可在配置文件中指定组表达式参数名，默认为 `gexpr`
  * 新增 `bean-searcher.params.group.expr-cache-size` 配置键，可在配置文件中指定组表达式解析缓存的大小，默认为 `50` 个
  * 新增 `bean-searcher.params.group.separator` 配置键，可在配置文件中指定参数组分隔符，默认为 `.`
  * 新增 `bean-searcher.field-convertor.use-time` 配置项，表示是否自动添加 `TimeFieldConvertor`，默认 `true`

### v3.4 的新特性（v3.4.3）

* Bean Searcher
  * 增强 `MapBuilder.orderBy(..)` 方法，支持多次调用来指定按多个字段进行排序 
  * 新增 `orderBy` 排序参数，可以类似 `orderBy=age:asc,time:desc` 的形式来指定多个排序字段
  * 重构 `DefaultSqlResolver`、`Dialect`、`SqlSnippet` 等相关类
  * 移除 `StringUtils.firstCharToUpperCase(..)` 方法
  * `DefaultDbMapping` 新增 `ignoreFields` 属性，支持配置全局忽略的属性
  * `@SearchBean` 新增 `ignoreFields` 属性，可配置单个实体类应该被忽略的属性（可忽略父类中的属性）
  * 带嵌入参数的字段也能参与 过滤条件 与 字段统计（since v3.4.2）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.default-mapping.ignore-fields` 配置项，可在配置文件中指定全局忽略的属性
  * 新增 `bean-searcher.params.order-by` 配置项，可在配置文件中指定排序参数的参数名

### v3.3 的新特性（v3.3.3）

* Bean Searcher
  * 新增 `FieldOp` 接口，用户可用之扩展自己的字段运算符
  * 新增 `FieldOpPool` 类，用户可用之定制一套全新的字段运算符
  * 内置新增 `NotIn` / `ni` 与 `NotBetween` / `nb` 运算符
  * 内置运算符 `MultiValue` / `mv` 重命名为 `InList` / `il` (原运算符仍可使用)
  * `DefaultDbMapping` 新增 `redundantSuffixes` 属性，可配置 在实体类自动映射表名时 统一去除类名中的冗余后缀（比如 VO、DTO 等）
  * 简化 `Dialect`: 移除 `truncateToDateStr`，`truncateToDateMinuteStr` 与 `truncateToDateSecondStr` 方法
  * `MapBuilder` 新增 `op(Class<? extends FieldOp> op)` 方法（v3.3.1）
  * 优化 `DateValueCorrector`, 可配置支持的运算符（v3.3.1）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.default-mapping.redundant-suffixes` 配置项，可配置多个冗余后缀
  * 支持直接声明一个 `FieldOp` 类型的 Spring Bean 来扩展一个新的字段运算符
  * 支持直接声明一个 `FieldOpPool` 类型的 Spring Bean 来定制一套全新的字符运算符
  * 新增 `bean-searcher.sql.use-date-value-corrector` 配置项，可配置 是否使用 `DateValueCorrector`（v3.3.1）

### v3.2 的新特性（v3.2.4）

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
  * 使用 Searcher 类型注入检索器时，默认注入 MapSearcher，不再报错
  * 提高兼容性，SpringBoot 最低版本支持到 v1.4+

### v3.1 的新特性（v3.1.4）

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

### v3.0 的新特性（v3.0.5）

1. 支持 热加载
2. 支持 无注解
3. 支持 Select 指定字段
4. 支持 条件与运算符的约束
5. 支持 参数过滤器
6. 支持 字段转换器
7. 支持 Sql 拦截器
8. 支持 多数据源
9. Spring Boot 自动配置功能 独立成 Bean Searcher Boot Starter` 项目中，Bean Searcher 核心包不再依赖 Spring
10. 简化使用，不再需要启动操作，不再需要配置 SearchBean 包名路径（移除了 SearcherStarter 类）

## V2 版本

该版本过于古老，具体已不可考，只余坊间仍流传着上古年间女娲补天前所留的只言片语。

## V1 版本

该版本过于古老，具体已不可考，只余坊间仍流传着盘古开天辟地的故事。
