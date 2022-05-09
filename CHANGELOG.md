# Next

### ✨ Features

* Bean Searcher
  * 新增 `Bool2NumFieldConvertor` 支持 `Boolean -> Number` 方向的转换
  * 新增 慢 SQL 日志以及 相关实体类 快速定位 功能（TODO）
  * 可配置关闭默认的驼峰转下划线映射规则（TODO）
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.field-convertor.use-bool-num` 配置键，可自动配置 `BoolNumFieldConvertor`，默认为 `true`

# v3.6.1 @ 2022-05-09

* Bean Searcher
  * 新增 `Bool2NumFieldConvertor` 支持 `Boolean -> Number` 方向的转换
  * 
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.field-convertor.use-bool-num` 配置键，可自动配置 `BoolNumFieldConvertor`，默认为 `true`

# v3.6.0 @ 2022-04-21

### ✨ Features

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

### ✨ Better

* Bean Searcher Boot Starter: 升级 spring-boot -> 2.6.6

### 🐛 Bug Fixes

* 修复：对于 `Boolean` 类型的字段，当检索时该字段传入的参数值为 `空串` 时，`BoolValueFilter` 会将其转换为 `true` 的问题：https://github.com/ejlchina/bean-searcher/issues/29

# v3.5.2 @ 2022-03-17

### ✨ Features

* Bean Searcher: 参数构建器新增 `field(FieldFn<T, ?> fieldFn, Collection<?> values)` 与 `field(String fieldName, Collection<?> values)` 方法，支持字段值集合参数

### ✨ Better

* Bean Searcher Boot Starter: 升级 spring-boot -> 2.6.4

### 🐛 Bug Fixes

* 修复当排序字段不在 SELECT 子句中时 ORDER BY 子句仍然会使用该字段的别名的问题

# v3.5.1 @ 2022-02-24

### ✨ Better

* 强化对复杂逻辑表达式的简化能力
* 升级 slf4j-api -> 1.7.36

# v3.5.0 @ 2022-02-23

### ✨ Features

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

### 🐛 Bug Fixes

* 修复 `StartWith` 运算符不后模糊匹配的问题（该 BUG 在 `v3.4.2` 中滋生）

# v3.4.2 @ 2022-02-18

### ✨ Better

* 带嵌入参数的字段也能参与过滤条件
* 带嵌入参数的字段也能参与字段统计

# v3.4.1 @ 2022-02-11

### ✨ Better

* Bean Searcher
  * 优化 SQL 生成逻辑：当 `@SearchBean` 注解的 `joinCond` 属性只有一个拼接参数 且 该参数值为空时，则使其不参与 `where` 子句

### 🐛 Bug Fixes

* 修复 `DateFieldConvertor` 无法将 `java.sql.Date` 转换为 `LocalDate / LocalDateTime` 的问题
* 修复 `DateFieldConvertor` 转换 `LocalDate / LocalDateTime` 时会产生时区偏差的问题
* 修复 `DateFormatFieldConvertor` 无法格式化 `java.sql.Date / java.sql.Time` 的问题

# v3.3.3 @ 2022-02-11

### ✨ Better

* Bean Searcher
  * bump slf4j-api from 1.7.32 to 1.7.35
  * 优化注解声明
  * 优化异常信息
* Bean Searcher Boot Starter
  * bump spring-boot from 2.6.2 to 2.6.3
  * 去掉无用的配置提示

### 🐛 Bug Fixes

* 修复 `DateFieldConvertor` 无法将 `java.sql.Date` 转换为 `LocalDate / LocalDateTime` 的问题
* 修复 `DateFieldConvertor` 转换 `LocalDate / LocalDateTime` 时会产生时区偏差的问题
* 修复 `DateFormatFieldConvertor` 无法格式化 `java.sql.Date / java.sql.Time` 的问题

# v3.2.4 @ 2022-02-11

### ✨ Better

* Bean Searcher Boot Starter
  * 去掉无用的配置提示

### 🐛 Bug Fixes

* 修复 `DateFieldConvertor` 无法将 `java.sql.Date` 转换为 `LocalDate / LocalDateTime` 的问题
* 修复 `DateFieldConvertor` 转换 `LocalDate / LocalDateTime` 时会产生时区偏差的问题
* 修复 `DateFormatFieldConvertor` 无法格式化 `java.sql.Date / java.sql.Time` 的问题

# v3.1.4 @ 2022-02-11

### ✨ Better

* Bean Searcher
  * 增强 `BoolFieldConvertor`，使支持 `Boolean -> boolean` 方向的转换
* Bean Searcher Boot Starter
  * 不再强制依赖 `DataSource`, 支持 Grails 项目
  * 去掉无用的配置提示

### 🐛 Bug Fixes

* 修复 `DateFieldConvertor` 无法将 `java.sql.Date` 转换为 `LocalDate / LocalDateTime` 的问题
* 修复 `DateFieldConvertor` 转换 `LocalDate / LocalDateTime` 时会产生时区偏差的问题
* 修复 `DateFormatFieldConvertor` 无法格式化 `java.sql.Date / java.sql.Time` 的问题

# v3.4.0 @ 2022-02-09

### ✨ Features

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

### 🌻 Better

* Bean Searcher
  * 优化异常信息
  * 优化注解声明
  * bump slf4j-api from 1.7.32 to 1.7.35
* Bean Searcher Boot Starter
  * 优化配置提示信息
  * bump spring-boot from 2.6.2 to 2.6.3

# v3.3.2 @ 2022-02-07

### 🌻 Better

* Bean Searcher
  * 增强 `BoolFieldConvertor`，使支持 `Boolean` -> `boolean` 方向的转换

# v3.2.3 @ 2022-01-30

### 🌻 Better

* Bean Searcher
  * 增强 `BoolFieldConvertor`，使支持 `Boolean` -> `boolean` 方向的转换
* Bean Searcher Boot Starter
  * 优化 `BeanSearcherAutoConfiguration` 不再强制依赖 `DataSource`
  * 支持在 Grails 项目中使用 `bean-searcher-boot-starter` 依赖

# v3.3.1 @ 2022-01-21

### ✨ Features

* Bean Searcher
  * `MapBuilder` 新增 `op(Class<? extends FieldOp> op)` 方法
  * 优化 `DateValueCorrector`, 可配置支持的运算符
* Bean Searcher Boot Starter
  * 新增 `bean-searcher.sql.use-date-value-corrector` 配置项，可配置 是否使用 `DateValueCorrector`
  * 支持在 Grails 项目中使用 `bean-searcher-boot-starter` 依赖

### 🌻 Better

* Bean Searcher
  * 优化字段运算符的匹配逻辑：使用严格模式
  * 优化 `Operator` 常量，使其可以直接作为 `@DbField.onlyOn` 的值（兼容以前版本，便于升级）
* Bean Searcher Boot Starter
  * 优化自动配置机制，支持无 `DataSource` 自动配置

### 🐛 Bug Fixes

* 修复当用户对同一个运算符 new 很多次时会导致 `FieldOpPool` 膨胀的问题


# v3.2.2 @ 2021-12-20

### 🌻 Better

* Bean Searcher
  * 优化 `DateValueCorrector`，使其支持 `LocalDateTime` 类型字段
* Bean Searcher Boot Starter
  * 优化自动配置机制，使其不依赖于 `DataSourceAutoConfiguration`，只要提供了 `DataSource` 就能自动配置
* Change LICENSE to Apache-2.0

# v3.3.0 @ 2022-01-19

### ✨ Features

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

### 🌻 Better

* Bean Searcher
  * 无 `@DbIgnore` 注解也自动忽略实体类中的 `static` 与 `transient` 属性
  * 实体类支持子类重写父类中已存在的属性

### 🐛 Bug Fixes

* Bean Searcher
  * 修复非字符串字段使用 Empty/NotEmpty 运算符时会报错的问题：https://gitee.com/ejlchina-zhxu/bean-searcher/issues/I4N1MG

# v3.1.3 @ 2021-12-14

### 🌻 Better

* Bean Searcher
  * 无 `@DbIgnore` 也自动忽略实体类中的静态字段
* Bean Searcher Boot Starter
  * 使用 Searcher 类型注入检索器时，默认注入 MapSearcher，不再报错
  * 提高兼容性，SpringBoot 最低版本支持到 v1.4+
  
# v3.0.5 @ 2021-12-12

### 🌻 Better

* Bean Searcher
  * 无 `@DbIgnore` 也自动忽略实体类中的静态字段
* Bean Searcher Boot Starter
  * 使用 Searcher 类型注入检索器时，默认注入 MapSearcher，不再报错
  * 提高兼容性，SpringBoot 最低版本支持到 v1.4+

# v3.2.0 @ 2021-12-08

### ✨ Features

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

### 🌻 Better

* Bean Searcher Boot Starter
  * 使用 Searcher 类型注入检索器时，默认注入 MapSearcher，不再报错
  * 提高兼容性，SpringBoot 最低版本支持到 v1.4+

# v3.1.2 & v3.0.4 @ 2021-12-02

### 🌻 Better

* 优化 `JDBC` 调用，兼容 `sharding-jdbc`
* 优化 `Operator.from(Object)` 方法
* 优化 `MapBuilder` 工具类，增加非空校验

# v3.1.1 & v3.0.3 @ 2021-12-01

### ✨ Features

* 优化 `DefaultSqlResolver` 的方法的权限修饰符，便于子类复用

### 🐛 Bug Fixes

* 优化别名生成规则，兼容 Oracle 数据库

# v3.1.0 @ 2021-11-15

### ✨ Features

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

### 🐛 Bug Fixes

* 修复：在 v3.0.0 中，单独使用 `searchCount` 和 `searchSum` 方法时，获取 SQL 结果会出错的问题

# v3.0.1 @ 2021-11-05

### ✨ Features

* DateFormatFieldConvertor 新增 setFormat 方法

### 🐛 Bug Fixes

* 修复：在 v3.0.0 中，再没有指定 @SearchBean 注解的 joinCond 属性时，带条件的 SQL 生成中 where 后少一个 左括号的问题

# v3.0.0 重大更新 @ 2021-11-04

### ✨ Features

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

