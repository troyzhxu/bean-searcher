# SQL 方言（Dialect）

Bean Searcher 可以为我们自动生成完整的 SQL 语句，但对应不同的数据库，SQL 语法可能略有不同。为此，Bean Searcher 使用方言（Dialect）来扩展支持这些不同的数据库。

## 方言实现

Bean Searcher 自带四种 Dialect 实现：

* [`MySqlDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/MySqlDialect.java) - **默认方言**，可用于 类 MySql 的数据库
* [`OracleDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/OracleDialect.java) - 可用于 类 Oracle 12c（2013年6月发布）及以上版本 的数据库
* [`PostgreSqlDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/PostgreSqlDialect.java) - 可用于 类 PostgreSqlDialect 的数据库（**since v3.6.0**）
* [`SqlServerDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/SqlServerDialect.java) - 可用于 类 SqlServer (v2012+) 的数据库（**since v3.7.0**）
* 其它数据库可自定义 Dialect，可 [参考 MySqlDialect 的实现](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/MySqlDialect.java)

::: tip Bean Searcher 中的方言很简单
* 自 **v3.3.0** 起，它被简化，只需实现 **两个** 方法即可；
* 自 **v3.7.0** 起，再被简化，只需实现 **一个** 方法即可。
:::

## 配置方法

以下介绍各框架下的方言配置。

### SpringBoot / Grails

使用 `bean-searcher-boot-starter` 依赖时，如需切换 Bean Searcher 自带的方言，则可通过以下配置项来指定：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.dialect` | SQL 方言 | `MySQL`、`Oracle`、`PostgreSQL`、`SqlServer` | `MySQL`

自定义的方言，只需将之注册为 Bean 即可：

```java
@Bean
public Dialect myDialect() {
    return new MyDialect();
}
```

### 非 Boot 的 Spring 项目

```xml
<!-- 定义 Oracle 方言 -->
<bean id="dialect" class="cn.zhxu.bs.dialect.MyDialect" />

<!-- v3.3 起需要配置运算符池 -->
<bean id="fieldOpPool" class="cn.zhxu.bs.FieldOpPool" 
    p:dialect-ref="dialect" />

<bean id="paramResolver" class="cn.zhxu.bs.implement.DefaultParamResolver" 
    p:fieldOpPool-ref="fieldOpPool" />

<bean id="sqlResolver" class="cn.zhxu.bs.implement.DefaultSqlResolver" 
    p:dialect-ref="dialect" />

<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="paramResolver" ref="paramResolver" />
    <property name="sqlResolver" ref="sqlResolver" />
</bean>
```

### Others

```java
Dialect dialect = new MyDialect();
// v3.3 起需要配置运算符池
FieldOpPool fieldOpPool = new FieldOpPool();
fieldOpPool.setDialect(dialect);    // 配置使用 Oracle 方言
DefaultParamResolver paramResolver = new DefaultParamResolver();
paramResolver.setFieldOpPool(fieldOpPool);
DefaultSqlResolver sqlResolver = new DefaultSqlResolver();
sqlResolver.setDialect(dialect);    // 配置使用 Oracle 方言
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .paramResolver(paramResolver)
        .sqlResolver(sqlResolver)
        .build();
```

## 动态方言（v4.2.0）

动态方言一般在 [多数据源](/guide/advance/datasource) 的场景下才会使用。其实现原理非常简单，仅两个实现类：

* `DynamicDialect`（核心类）
* `DynamicDialectSupport`

配置方法（使用 `bean-searcher-boot-starter` 与  `bean-searcher-solon-plugin` 依赖时）：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.dialect-dynamic` | 是否使用动态方言 | `true`、`false` | `false`
`bean-searcher.sql.dialects` | 不同数据源的方言关系 | `Map<String, Dialect>` | 空

例如：

```yml
bean-searcher:
  sql:
    # 默认 MySQL 方言
    dialect: MySQL
    # 启用动态方言
    dialect-dynamic: true
    dialects:
      # user 数据源使用 Oracle 方言
      user: Oracle
      # order 数据源使用 PostgreSQL 方言
      order: PostgreSQL
```

以上指定的方言都是框架自带的方言，你也可以自定义方言，并用如下方法配置某个数据源使用自定义方言：

```java
@Bean
public DataSourceDialect userDialect() {
    // shop 数据源使用 MyDialect 自定义方言
    return new DataSourceDialect("shop", new MyDialect());
}
```
