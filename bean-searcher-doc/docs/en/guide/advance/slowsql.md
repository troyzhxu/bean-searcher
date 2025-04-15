# Slow SQL Logging and Monitoring (since v3.7.0)

Since `v3.7.0`, Bean Searcher has provided slow SQL logging and monitoring functionality.

## Slow SQL Threshold

The slow SQL threshold refers to the minimum execution time for a SQL statement to be considered a slow SQL. It is the criterion for determining whether a SQL statement is a slow SQL, with the unit being `ms` and the default value being `500`. Of course, it can also be modified through configuration.

### SpringBoot / Grails Configuration Items (using the `bean-searcher-boot-starter` dependency)

Configuration Key | Meaning | Type | Default Value
-|-|-|-
`bean-searcher.sql.slow-sql-threshold` | Slow SQL threshold (unit: milliseconds) | `int` | `500`

### Non-Boot Spring Configuration Method (using the `bean-searcher` dependency)

```xml
<bean id="sqlExecutor" class="cn.zhxu.bs.implement.DefaultSqlExecutor">
    <property name="dataSource" ref="dataSource" />
    <!-- Configure the slow SQL threshold -->
    <property name="slowSqlThreshold" value="500" />
</bean>
<!-- Declare the MapSearcher retriever, which returns query results as Map objects -->
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- Omit other property configurations; the BeanSearcher retriever has the same configuration -->
    <property name="sqlExecutor" ref="sqlExecutor" />
</bean>
```

### Others

```java
DefaultSqlExecutor sqlExecutor = new DefaultSqlExecutor(getDefaultDataSource());
// Configure the slow SQL threshold
sqlExecutor.setSlowSqlThreshold(500);
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Omit other property configurations; the BeanSearcher retriever has the same configuration
        .sqlExecutor(sqlExecutor)
        .build();
```

## Enable Slow SQL Logging

The log level for slow SQL is `WARN`. Therefore, you can enable slow SQL logging by adjusting the log level of `cn.zhxu.bs.implement.DefaultSqlExecutor` to `WARN | INFO | DEBUG`.

Log Effect (`Execution Time`, `SQL`, `Execution Parameters`, `Entity Class`):

```log
14:55:02.151 WARN - bean-searcher [600ms] slow-sql: [select count(*) s_count from employee e where (e.type = ?)] params: [1] on [com.example.sbean.Employee]
```

Reference: [Getting Started > Usage > SQL Logging](/en/guide/start/use#sql-Logging) section.

## Monitor Slow SQL Events

Sometimes, we need to monitor slow SQL events in the code for further custom processing (e.g., sending warning notifications).

### SpringBoot / Grails (using the `bean-searcher-boot-starter` dependency). Just configure a Bean.

```java
@Bean
public SqlExecutor.SlowListener slowSqlListener() {
    return (
        Class<?> beanClass,     // The entity class where the slow SQL occurred 
        String slowSql,         // The slow SQL string
        List<Object> params,    // SQL execution parameters
        long timeCost           // Execution time (unit: ms)
    ) -> {
        // TODO: Monitoring processing
    }
}
```

### Non-Boot Spring Projects

```xml
<bean id="sqlExecutor" class="cn.zhxu.bs.implement.DefaultSqlExecutor">
    <property name="dataSource" ref="dataSource" />
    <!-- Configure the slow SQL listener -->
    <property name="slowListener">
        <!-- Customize MySlowSqlListener to implement the SqlExecutor.SlowListener interface -->
        <bean class="com.example.MySlowSqlListener" />
    </property>
</bean>
<!-- Declare the MapSearcher retriever, which returns query results as Map objects -->
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- Omit other property configurations; the BeanSearcher retriever has the same configuration -->
    <property name="sqlExecutor" ref="sqlExecutor" />
</bean>
```

### Others

```java
DefaultSqlExecutor sqlExecutor = new DefaultSqlExecutor(getDefaultDataSource());
// Configure the slow SQL listener
sqlExecutor.setSlowListener((
    Class<?> beanClass,     // The entity class where the slow SQL occurred 
    String slowSql,         // The slow SQL string
    List<Object> params,    // SQL execution parameters
    long timeCost           // Execution time (unit: ms)
) -> {
    // TODO: Monitoring processing
});
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Omit other property configurations; the BeanSearcher retriever has the same configuration
        .sqlExecutor(sqlExecutor)
        .build();
```
