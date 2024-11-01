# 多数据源

Bean Searcher 支持多数据源。

## 静态数据源

我们可在 `@SearchBean` 注解中为每个实体类指定不同的数据源，例如，指定 User 实体类来自 userDs 数据源：

```java
@SearchBean(dataSource="userDs")
public class User {
    // 省略其它代码
}
```

指定 Order 实体类来自 orderDs 数据源：

```java
@SearchBean(dataSource="orderDs")
public class Order {
    // 省略其它代码
}
```

## 配置（SpringBoot 为例）

首先在配置文件 `application.properties` 中配置数据源信息:

```properties
# 默认数据源
spring.datasource.url = jdbc:h2:~/test
spring.datasource.driverClassName = org.h2.Driver
spring.datasource.username = sa
spring.datasource.password = 123456
# user 数据源
spring.datasource.user.url = jdbc:h2:~/user
spring.datasource.user.driverClassName = org.h2.Driver
spring.datasource.user.username = sa
spring.datasource.user.password = 123456
# order 数据源
spring.datasource.order.url = jdbc:h2:~/order
spring.datasource.order.driverClassName = org.h2.Driver
spring.datasource.order.username = sa
spring.datasource.order.password = 123456
```

然后配置以下一些 Bean 即可:

```java
// 收集 user 数据源的配置信息
@Bean(name = "userDsProps")
@ConfigurationProperties(prefix = "spring.datasource.user")
public DataSourceProperties userDsProps() {
    return new DataSourceProperties();
}

// 收集 order 数据源的配置信息
@Bean(name = "orderDsProps")
@ConfigurationProperties(prefix = "spring.datasource.order")
public DataSourceProperties orderDsProps() {
    return new DataSourceProperties();
}

@Bean
public NamedDataSource userNamedDataSource(@Qualifier("userDsProps") DataSourceProperties dataSourceProperties) {
    // 根据配置信息构建一个数据源对象
    DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
    // 具名数据源：cn.zhxu.bs.boot.NamedDataSource（前面只是铺垫，这才是关键步骤）
    return new NamedDataSource("userDs", dataSource);
}

@Bean
public NamedDataSource orderNamedDataSource(@Qualifier("orderDsProps") DataSourceProperties dataSourceProperties) {
    // 根据配置信息构建一个数据源对象
    DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
    // 具名数据源：cn.zhxu.bs.boot.NamedDataSource （前面只是铺垫，这才是关键步骤）
    return new NamedDataSource("orderDs", dataSource);
}
```

特别的，如果你的项目中的其它 ORM 已经配置了多数据源，例如你的 Spring 容器中已经存在了 `org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource` 类型的 Bean，那么你只需要如下配置即可：

```java
@Bean
public NamedDataSource userNamedDataSource(AbstractRoutingDataSource routingDataSource) {
    // 直接从 DynamicRoutingDataSource 中取出目标数据源
    DataSource dataSource = routingDataSource.getResolvedDataSources().get("userDs");
    // 具名数据源：cn.zhxu.bs.boot.NamedDataSource（关键步骤：套一个具名数据源的壳）
    return new NamedDataSource("userDs", dataSource);
}

@Bean
public NamedDataSource orderNamedDataSource(AbstractRoutingDataSource routingDataSource) {
    // 直接从 DynamicRoutingDataSource 中取出目标数据源
    DataSource dataSource = routingDataSource.getResolvedDataSources().getDataSource("orderDs");
    // 具名数据源：cn.zhxu.bs.boot.NamedDataSource （关键步骤：套一个具名数据源的壳）
    return new NamedDataSource("orderDs", dataSource);
}
```

## 动态数据源

上述配置的多数据源对单个 SearchBean 而言都是静态的，即某个实体类与某个数据源之间的关系是注解里指定死的。如果你开发的项目是 SAAS 模式，要求同一个实体类对不同的 Tenant（租户）使用不同数据源。则可以使用本节所讲的动态数据源。

要使用动态数据源，首先定义个 `DynamicDatasource`:

```java
public class DynamicDatasource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // 可以在拦截器中使用 ThreadLocal 记录当前租户信息
        // 然后在这里从 ThreadLocal 中取出
        return "当前租户编号";      // 返回当前租户编号
    }

}
```

然后，配置一个动态数据源（以 SpringBoot 项目为例）：

```java
// 把 DynamicDatasource 注册为一个 Bean
@Bean
public DataSource dynamicDatasource() {
    DynamicDatasource dynamicDatasource = new DynamicDatasource();
    dynamicDatasource.setTargetDataSources(getAllDataSources());
    return dynamicDatasource;
}

private Map<Object, Object> getAllDataSources() {
    Map<Object, Object> dataSources = new HashMap<>();
    dataSources.put("租户1编号", new DataSource1());
    dataSources.put("租户2编号", new DataSource2());
    // 把所有数据源都放进 dataSources 里
    return dataSources;
}
```
