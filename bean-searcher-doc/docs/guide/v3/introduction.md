---
description: Bean Searcher
---

# 介绍

## 为什么用

![需求图](/requirement.png)

产品给你画了一张图，还附带了一些要求：

* 检索结果分页展示
* 可以按任意字段排序
* 按检索条件统计某些字段值

这时候，后台接口该怎么写？？？使用 Mybatis 或 Hibernate 写 100 行代码是不是还打不住？而使用 Bean Searcher，仅需 **一行代码** 便可实现上述要求！！！

### 设计思想（出发点）

写这个章节实属无奈，因为很多刚刚接触 Bean Searcher 的人都很难明白它真正要解决的问题是什么，只有正确使用它一段时间后才能体验到它带来的巨大便利。本章则尝试用文字向你展示它的一些设计思考。

任何一个系统都难逃有列表检索（如：订单管理，用户管理）的这样的需求，而每一个列表页所需展示的数据往往会横跨多张数据库表（比如订单管理页表格里的订单号列来自订单表，用户名列来自用户表），此时我们的后端所建的 **域类**（与数据库表想关联的那个实体类）与页面所需展示的数据并不能形成一一对应关系。

因此，**VO（View Object）** 产生了。它介于页面数据与域类之间，页面展示的数据不再需要与后端的域类一一对应，而只需要与 VO 一一对应就可以了，而 VO 也不再需要与数据表做映射，业务代码里拼装就可以了。

但此时，后端的逻辑又复杂了一点，因为我们还要处理 域类（或者复杂的 SQL 查询语句）与 VO 之间的转换关系。 

而 Bean Searcher 认为，VO 不再需要与域类扯上关系，一个 VO 既可以与页面数据一一对应，又可以直接映射到数据库里的多张数据表（域类不同，它只映射到一张表），而这种新的 VO 称为 **Search Bean**。因此，**Search Bean 是直接与数据库有跨表映射关系的 VO**，它与 域类 有着本质的区别。

正因为 Search Bean 是一种 VO，所以它不应像域类那样在业务代码中被随意引用，它是直接面向前端的页面数据的（深刻理解一下：Search Bean 里定义的 Java 字段都是给前端页面直接使用的）。因此，一个 Search Bean 代表一种业务检索，如：

* 订单列表检索接口，对应一个 SearchBean
* 用户列表检索接口，对应一个 SearchBean

### 效率极大提高的原因

在 Search Bean 出现之前，前端传来的检索条件都是需要业务代码处理的（因为普通的 VO 无法与数据库直接映射），而 Search Bean 出现之后，检索条件可以用 Search Bean 里的字段和参数直接表达，并且直接映射成数据库的查询语句。

所以，后端检索接口里的代码只需要收集页面的检索参数即可，就像文档首页所展示的代码一样，并不需要做太多的处理，并且 Bean Searcher 返回的 SearchBean 就是前端需要的 VO 对象，也不需要再做转换。而这，就是 Bean Searcher 之所以能 **使一行代码实现复杂列表检索成为可能** 的原因。

### 前端需要多传参数吗

很多人首次接触 Bean Searcher，都会先入为主地 **误以为**：使用 Bean Searcher 会给前端带来压力，**需要前端多传许多原本不必传的参数**。

其实不然：前端需要传递的 **参数多少**，只与 **产品需求的复杂度** 有关，与 **后端所用的框架** 是无关的。

同学可能又问了：我看到很多关于 Bean Searcher 的文章，都有写 **xxx-op** 与 **xxx-ic** 的参数呀，我们的系统里就从来都没这种参数，那用 Bean Searcher 后不需要传它们吗？

那同学们可要注意了，那些文章所讲的内容都是 **高级查询**，产品本就要求前端可以自己控制某个字段是 模糊查 还是 精确查，如下图：

![](/requirement_1.png)

那如果前端没这个需求呢，比如 `username` 字段，前端只需要 模糊查询，并且也不需要忽略大小写。那此时后端还需要传 `username-op` 与 `username-ic` 参数吗？

**当然不需要**，只需要传 `username` 一个即可。那后端又是如何表达 **模糊查询** 这个条件的呢？很简单，只需在 SearchBean 中对 `username` 属性加一个注解即可：

```java
@DbField(onlyOn = Contain.class)
private String username;
```

可参考：[高级 > 条件约束](/guide/v3/advance.html#检索约束) 章节。

这时需要注意，上文所提到的：**Search Bean 是直接与数据库有跨表映射关系的 VO**。

## Bean Searcher

Bean Searcher 是一个轻量级 数据库 条件检索引擎，它的作用是从已有的数据库表中检索数据，它的目的是为了减少后端模板代码的开发，极大提高开发效率，节省开发时间，使得一行代码完成一个列表查询接口成为可能！

* 不依赖具体的 Web 框架（即可以在任意的 Java Web 框架内使用）

* 不依赖具体的 ORM 框架（即可以与任意的 ORM 框架配合使用，没有 ORM 也可单独使用）

### 架构设计图

![](/architecture.jpg)

### 与 Hibernate MyBatis 的区别

首先，Bean Searcher 并不是一个完全的 `ORM` 框架，它存在的目的不是为了替换他们，而是为了弥补他们在 `列表检索领域` 的不足。

下表列举它们之间的具体区别：

区别点 | Bean Searcher | Hibernate | MyBatis
-|-|-|-
ORM | 只读 ORM | 全自动 ORM | 半自动 ORM
实体类可多表映射 | 支持 | 不支持 | 不支持
字段运算符 | **动态** | 静态 | 静态
CRUD | Only R | CRUD | CRUD

从上表可以看出，Bean Searcher 只能做数据库查询，不支持 增删改。但它的 **多表映射机制** 与 **动态字段运算符**，可以让我们在做复杂列表检索时代码 **以一当十**，甚至 **以一当百**。

更关键的是，它无第三方依赖，在项目中可以和 **任意 ORM 配合** 使用。


### 哪些项目可以使用

* Java 项目（当然 Kotlin、Groovy 也是可以的）；

* 使用了 关系数据库的项目（如：MySQL、Oracle 等）；

* 可与任意框架集成：Spring Boot、Grails、Jfinal 等等。

### 什么场景下需要用

任何框架都有其使用场景，当然 Bean Searcher 也不例外，它的诞生并不是为了替换 MyBatis / Hibernate 等传统 ORM，因此，理解哪些场景适合使用它非常重要。

* **推荐** 在 **非事务性** 的 **动态** 检索场景中使用，例如：

  管理后台的 [订单管理]、[用户管理] 等页面 的检索场景，该检索是 **非事务性** 的，不会向数据库中插入数据，且检索条件是 **动态** 的，用户检索方式不同，执行的 SQL 也不同（如按 `订单号` 检索 与 按 `状态` 检索 需要不同的 SQL），此时推荐使用 Bean Searcher 来执行检索；

* **不建议** 在 **事务性** 的 **静态** 查询场景中使用，例如：

  用户注册接口中需要先查询账号是否已存在的场景，该接口是 **事务性** 的，它需要向数据库中插入数据，且此时的查询条件是 **静态** 的，无论哪个账号，都执行一样的 SQL（都按 `账号名` 查询），此时不建议使用 Bean Searcher 来执行。

### 支持哪些数据库

只要支持正常的 SQL 语法，都是支持的，另外 Bean Searcher 内置了四个方言实现：

* 分页语法和 MySQL 一样的数据库，默认支持
* 分页语法和 PostgreSql 一样的数据库，选用 PostgreSql 方言 即可
* 分页语法和 Oracle 一样的数据库，选用 Oracle 方言 即可
* 分页语法和 SqlServer (v2012+) 一样的数据库，选用 SqlServer 方言 即可

如果分页语法独创的，则只需自定义一个方言，只需实现两个方法，参考：[高级 > SQL 方言](/guide/v3/advance.html#sql-方言（dialect）) 章节。

### DEMO 快速体验

仓库地址: [https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher-demos/bs-demo-springboot](https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher-demos/bs-demo-springboot)

#### 第一步：克隆

```bash
> git clone https://github.com/troyzhxu/bean-searcher.git
```

#### 第二步：运行

```bash
> cd bean-searcher/bean-searcher-demos/bs-demo-springboot
> mvn spring-boot:run
```

#### 第三步：效果

访问 `http://localhost:8080/` 既可查看运行效果。

此例的更多信息，可参阅：[DEMO 详细介绍](https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher-demos/bs-demo-springboot)。

[更多 DEMO](https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher-demos)

## 版本迭代

详细的版本信息请查阅 [Github](https://github.com/troyzhxu/bean-searcher/releases) 与 [Gitee](https://gitee.com/troyzhxu/bean-searcher/releases) , 本节主要介绍每个里程碑版本引入的新特性。

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
  * 实体类 `@SearchBean` [注解内的 SQL 片段支持 `:` 的 转义（`\\:`）语义](/guide/v3/bean.html#前缀符转义（since-v3-6-0）)；
  * 实体类 `@SearchBean` [注解新增 `orderBy` 属性，可指定 默认排序字段](/guide/v3/bean.html#默认排序（since-v3-6-0）)；
  * 实体类 `@SearchBean` [注解新增 `sortType` 属性，可指定 排序约束类型](/guide/v3/bean.html#排序约束（since-v3-6-0）)；
  * 实体类 `@SearchBean` 注解指定 `groupBy` 属性时，支持 字段求和 查询
  * 检索器 [新增 `ResultFilter` 机制](/guide/v3/advance.html#结果过滤器（v3-6-0）)，可让用户对检索结果统一做进一步的自定义处理
  * 新增 [`PostgreSqlDialect` 方言实现](/guide/v3/advance.html#方言实现)，可用于 PostgreSql 数据库
  * 重构 `SqlResult`，与 JDBC 解耦，便于使用其它 ORM 重写 `SqlExecutor`
  * 重构 `SqlInterceptor`，[它的 `intercept(..)` 方法新增 `FetchType` 参数](/guide/v3/advance.html#sqlinterceptor)
  * 新增 `B2MFieldConvertor`，[可让 `BFieldConvertor` 也适用于 `MapSearcher` 检索器](/guide/v3/advance.html#b2mfieldconvertor)
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

### v2.2 的新特性（v2.2.0）

1. 支持以 lambda 的方式构建检索参数
2. SQL 日志的输出级别调为 DEBUG

### v2.1 的新特性（v2.1.2）

1. 支持形如 `:name:` 的拼接参数

### v2.0 的新特性（v2.0.1）

1. 实现 Spring Boot Starter 化