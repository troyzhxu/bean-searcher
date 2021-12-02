# Next

### ✨ Features

* Bean Searcher
  * 新增条件分组 与 组逻辑运算功能（TODO）
  * 优化参数：对于 op = mv / bt 的查询，可用 JSON 数组来传输多个值，例如：age=[20,30] 替代 age-0=20&age-1=30（TODO）
  * 新增 `EnumFieldConvertor`，用来做枚举字段转换（TODO）
* Bean Searcher Boot Starter
  * 默认自动配置添加 `EnumFieldConvertor`（TODO）

# v3.1.2 & v3.0.4

### 🌻 Better

* 优化 `JDBC` 调用，兼容 `sharding-jdbc`
* 优化 `Operator.from(Object)` 方法
* 优化 `MapBuilder`：增加非空校验

# v3.1.1 & v3.0.3

### ✨ Features

* 优化 `DefaultSqlResolver` 的方法的权限修饰符，便于子类复用

### 🐛 Bug Fixes

* 优化别名生成规则，兼容 Oracle 数据库

# v3.1.0

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

# v3.0.2

### 🐛 Bug Fixes

* 修复：在 v3.0.0 中，单独使用 `searchCount` 和 `searchSum` 方法时，获取 SQL 结果会出错的问题

# v3.0.1

### ✨ Features

* DateFormatFieldConvertor 新增 setFormat 方法

### 🐛 Bug Fixes

* 修复：在 v3.0.0 中，再没有指定 @SearchBean 注解的 joinCond 属性时，带条件的 SQL 生成中 where 后少一个 左括号的问题

# v3.0.0 重大更新

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

