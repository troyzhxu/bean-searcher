# SQL Dialect

Bean Searcher can automatically generate complete SQL statements for us. However, the SQL syntax may vary slightly for different databases. To address this, Bean Searcher uses dialects to extend support for these different databases.

## Dialect Implementations

Bean Searcher comes with four built-in Dialect implementations:

* [`MySqlDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/MySqlDialect.java) - **Default dialect**, suitable for MySQL-like databases.
* [`OracleDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/OracleDialect.java) - Suitable for databases similar to Oracle 12c (released in June 2013) and above.
* [`PostgreSqlDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/PostgreSqlDialect.java) - Suitable for PostgreSQL-like databases (**since v3.6.0**).
* [`SqlServerDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/SqlServerDialect.java) - Suitable for databases similar to SQL Server (v2012+) (**since v3.7.0**).
* [`DaMengDialect`](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/DaMengDialect.java) - Suitable for databases similar to DaMeng database (**since v4.6.0**).

* For other databases, you can customize a Dialect. You can [refer to the implementation of MySqlDialect](https://github.com/troyzhxu/bean-searcher/blob/dev/bean-searcher/src/main/java/cn/zhxu/bs/dialect/MySqlDialect.java).

::: tip The dialect in Bean Searcher is very simple
* Since **v3.3.0**, it has been simplified, and only **two** methods need to be implemented.
* Since **v3.7.0**, it has been further simplified, and only **one** method needs to be implemented.
:::

## Configuration Methods

The following introduces the dialect configuration in each framework.

### SpringBoot / Grails

When using the `bean-searcher-boot-starter` dependency, if you need to switch the built-in dialect of Bean Searcher, you can specify it through the following configuration item:

Configuration Key | Meaning | Available Values | Default Value
-|-|-|-
`bean-searcher.sql.dialect` | SQL dialect | `MySQL`, `Oracle`, `PostgreSQL`, `SqlServer` | `MySQL`

For a custom dialect, you just need to register it as a Bean:

```java
@Bean
public Dialect myDialect() {
    return new MyDialect();
}
```

### Non-Boot Spring Projects

```xml
<!-- Define the Oracle dialect -->
<bean id="dialect" class="cn.zhxu.bs.dialect.MyDialect" />

<!-- Since v3.3, you need to configure the operator pool -->
<bean id="fieldOpPool" class="cn.zhxu.bs.FieldOpPool" 
    p:dialect-ref="dialect" />

<bean id="paramResolver" class="cn.zhxu.bs.implement.DefaultParamResolver" 
    p:fieldOpPool-ref="fieldOpPool" />

<bean id="sqlResolver" class="cn.zhxu.bs.implement.DefaultSqlResolver" 
    p:dialect-ref="dialect" />

<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- Other property configurations are omitted. The BeanSearcher retriever is configured in the same way -->
    <property name="paramResolver" ref="paramResolver" />
    <property name="sqlResolver" ref="sqlResolver" />
</bean>
```

### Others

```java
Dialect dialect = new MyDialect();
// Since v3.3, you need to configure the operator pool
FieldOpPool fieldOpPool = new FieldOpPool();
fieldOpPool.setDialect(dialect);    // Configure to use the Oracle dialect
DefaultParamResolver paramResolver = new DefaultParamResolver();
paramResolver.setFieldOpPool(fieldOpPool);
DefaultSqlResolver sqlResolver = new DefaultSqlResolver();
sqlResolver.setDialect(dialect);    // Configure to use the Oracle dialect
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Other property configurations are omitted. The BeanSearcher retriever is configured in the same way
        .paramResolver(paramResolver)
        .sqlResolver(sqlResolver)
        .build();
```

## Dynamic Dialect (v4.2.0)

The dynamic dialect is generally used in the scenario of [multiple data sources](/en/guide/advance/datasource). Its implementation principle is very simple, with only two implementation classes:

* `DynamicDialect` (core class)
* `DynamicDialectSupport`

Configuration method (when using the `bean-searcher-boot-starter` and `bean-searcher-solon-plugin` dependencies):

Configuration Key | Meaning | Available Values | Default Value
-|-|-|-
`bean-searcher.sql.dialect-dynamic` | Whether to use the dynamic dialect | `true`, `false` | `false`
`bean-searcher.sql.dialects` | The dialect relationship of different data sources | `Map<String, Dialect>` | Empty

For example:

```yml
bean-searcher:
  sql:
    # Default MySQL dialect
    dialect: MySQL
    # Enable the dynamic dialect
    dialect-dynamic: true
    dialects:
      # Use the Oracle dialect for the user data source
      user: Oracle
      # Use the PostgreSQL dialect for the order data source
      order: PostgreSQL
```

The dialects specified above are all built-in dialects of the framework. You can also customize a dialect and configure a data source to use the custom dialect in the following way:

```java
@Bean
public DataSourceDialect shopDialect() {
    // Use the custom MyDialect for the shop data source
    return new DataSourceDialect("shop", new MyDialect());
}
```
