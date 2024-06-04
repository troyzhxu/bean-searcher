# 慢 SQL 日志与监听（since v3.7.0）

自 `v3.7.0` 起 Bean Searcher 提供了慢 SQL 日志与 监听功能。

## 慢 SQL 阈值

慢 SQL 阈值指的是慢 SQL 的最小执行耗时，它是判断一个 SQL 是否为慢 SQL 的标准，单位 `ms`，默认值为 `500`。当然也可以通过配置修改。

### SpringBoot / Grails 配置项（使用 `bean-searcher-boot-starter` 依赖）

配置键名 | 含义 | 类型 | 默认值
-|-|-|-
`bean-searcher.sql.slow-sql-threshol` | 慢 SQL 阈值（单位：毫秒） | `int` | `500`

### 非 Boot 的 Spring 配置方法（使用 `bean-searcher` 依赖）

```xml
<bean id="sqlExecutor" class="cn.zhxu.bs.implement.DefaultSqlExecutor">
    <property name="dataSource" ref="dataSource" />
    <!-- 配置慢 SQL 阈值 -->
    <property name="slowSqlThreshold" value="500" />
</bean>
<!-- 声明 MapSearcher 检索器，它查询的结果是 Map 对象 -->
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="sqlExecutor" ref="sqlExecutor" />
</bean>
```

### Others

```java
DefaultSqlExecutor sqlExecutor = new DefaultSqlExecutor(getDefaultDataSource());
// 配置慢 SQL 阈值
sqlExecutor.setSlowSqlThreshold(500);
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .sqlExecutor(sqlExecutor)
        .build();
```

## 开启慢 SQL 日志

慢 SQL 的日志级别为 `WARN`，所以，只需将 `cn.zhxu.bs.implement.DefaultSqlExecutor` 的日志级别调整为 `WARN | INFO | DEBUG` 即可开启。

日志效果（`执行耗时`、`SQL`、`执行参数`、`实体类`）：

```log
14:55:02.151 WARN - bean-searcher [600ms] slow-sql: [select count(*) s_count from employee e where (e.type = ?)] params: [1] on [com.example.sbean.Employee]
```

参考：[起步 > 使用 > SQL 日志](/guide/latest/start.html#sql-日志) 章节。

## 监听慢 SQL 事件

有时候我们需要在代码中监听慢 SQL 事件，以便做进一步的自定义处理（比如：发送警告通知）。

### SpringBoot / Grails（使用 `bean-searcher-boot-starter` 依赖）只需配置一个 Bean 即可

```java
@Bean
public SqlExecutor.SlowListener slowSqlListener() {
    return (
        Class<?> beanClass,     // 发生慢 SQL 的实体类 
        String slowSql,         // 慢 SQL 字符串
        List<Object> params,    // SQL 执行参数
        long timeCost           // 执行耗时（单位：ms）
    ) -> {
        // TODO: 监听处理
    }
}
```

### 非 Boot 的 Spring 项目

```xml
<bean id="sqlExecutor" class="cn.zhxu.bs.implement.DefaultSqlExecutor">
    <property name="dataSource" ref="dataSource" />
    <!-- 配置 慢 SQL 监听器 -->
    <property name="slowListener">
        <!-- 自定义 MySlowSqlListener 实现 SqlExecutor.SlowListener 接口 -->
        <bean class="com.example.MySlowSqlListener" />
    </property>
</bean>
<!-- 声明 MapSearcher 检索器，它查询的结果是 Map 对象 -->
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="sqlExecutor" ref="sqlExecutor" />
</bean>
```

### Others

```java
DefaultSqlExecutor sqlExecutor = new DefaultSqlExecutor(getDefaultDataSource());
// 配置慢 SQL 监听器
sqlExecutor.setSlowListener((
    Class<?> beanClass,     // 发生慢 SQL 的实体类 
    String slowSql,         // 慢 SQL 字符串
    List<Object> params,    // SQL 执行参数
    long timeCost           // 执行耗时（单位：ms）
) -> {
    // TODO: 监听处理
});
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .sqlExecutor(sqlExecutor)
        .build();
```
