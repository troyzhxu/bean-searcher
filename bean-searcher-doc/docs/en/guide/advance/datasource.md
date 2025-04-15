# Multiple Data Sources

Bean Searcher supports multiple data sources.

## Static Data Sources

We can specify different data sources for each entity class in the `@SearchBean` annotation. For example, specify that the User entity class comes from the userDs data source:

```java
@SearchBean(dataSource="userDs")
public class User {
    // Omit other code
}
```

Specify that the Order entity class comes from the orderDs data source:

```java
@SearchBean(dataSource="orderDs")
public class Order {
    // Omit other code
}
```

Here, the value of the annotation attribute `@SearchBean.dataSource` points to the name specified by the `NamedDataSource` (named data source) mentioned below.

## Configuration (Taking Spring Boot as an example)

First, configure the data source information in the configuration file `application.properties`:

```properties
# Default data source
spring.datasource.url = jdbc:h2:~/test
spring.datasource.driverClassName = org.h2.Driver
spring.datasource.username = sa
spring.datasource.password = 123456
# User data source
spring.datasource.user.url = jdbc:h2:~/user
spring.datasource.user.driverClassName = org.h2.Driver
spring.datasource.user.username = sa
spring.datasource.user.password = 123456
# Order data source
spring.datasource.order.url = jdbc:h2:~/order
spring.datasource.order.driverClassName = org.h2.Driver
spring.datasource.order.username = sa
spring.datasource.order.password = 123456
```

Then, configure the following Beans:

```java
// Collect the configuration information of the user data source
@Bean(name = "userDsProps")
@ConfigurationProperties(prefix = "spring.datasource.user")
public DataSourceProperties userDsProps() {
    return new DataSourceProperties();
}

// Collect the configuration information of the order data source
@Bean(name = "orderDsProps")
@ConfigurationProperties(prefix = "spring.datasource.order")
public DataSourceProperties orderDsProps() {
    return new DataSourceProperties();
}

// Note: The configuration of the above two Beans is not mandatory!
// They are only used to collect the configuration information of the data sources, and the ultimate goal is to construct the following Beans of the NamedDataSource type.
// If these two Beans affect the normal operation of your system, you can choose to remove them and use other methods to collect the configuration information of the data sources, for example:
// 1. Use a custom DataSourceProperties class to receive the configuration information.
// 2. Use the Environment object to dynamically obtain the configuration.
// 3. Use the @Value annotation to read a single configuration.
// 4. Use the file reading method to read the configuration.
// ...

@Bean
public NamedDataSource userNamedDataSource(@Qualifier("userDsProps") DataSourceProperties dataSourceProperties) {
    // Construct a DataSource object based on the configuration information.
    // Here, for convenience, the DataSourceBuilder tool is borrowed. You can also use other construction methods.
    DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
    // Named data source: cn.zhxu.bs.boot.NamedDataSource
    // All the above are just preparations, and this is the key step.
    return new NamedDataSource("userDs", dataSource);
}

@Bean
public NamedDataSource orderNamedDataSource(@Qualifier("orderDsProps") DataSourceProperties dataSourceProperties) {
    // Construct a DataSource object based on the configuration information.
    // Here, for convenience, the DataSourceBuilder tool is borrowed. You can also use other construction methods.
    DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
    // Named data source: cn.zhxu.bs.boot.NamedDataSource
    // All the above are just preparations, and this is the key step.
    return new NamedDataSource("orderDs", dataSource);
}
```

In particular, if multiple data sources have already been configured in other ORMs in your project, for example, there is already a Bean of the `org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource` type in your Spring container, then you only need the following configuration:

```java
@Bean
public NamedDataSource userNamedDataSource(AbstractRoutingDataSource routingDataSource) {
    // Directly retrieve the target data source from the DynamicRoutingDataSource.
    DataSource dataSource = routingDataSource.getResolvedDataSources().get("userDs");
    // Named data source: cn.zhxu.bs.boot.NamedDataSource (Key step: Wrap it with a named data source shell).
    return new NamedDataSource("userDs", dataSource);
}

@Bean
public NamedDataSource orderNamedDataSource(AbstractRoutingDataSource routingDataSource) {
    // Directly retrieve the target data source from the DynamicRoutingDataSource.
    DataSource dataSource = routingDataSource.getResolvedDataSources().getDataSource("orderDs");
    // Named data source: cn.zhxu.bs.boot.NamedDataSource (Key step: Wrap it with a named data source shell).
    return new NamedDataSource("orderDs", dataSource);
}
```

## Dynamic Data Sources

The multiple data sources configured above are static for a single SearchBean, that is, the relationship between an entity class and a data source is specified in the annotation. If the project you are developing is in the SAAS mode and requires the same entity class to use different data sources for different Tenants, you can use the dynamic data sources described in this section.

To use dynamic data sources, first define a `DynamicDatasource`:

```java
public class DynamicDatasource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // You can use ThreadLocal in the interceptor to record the current tenant information.
        // Then retrieve it from ThreadLocal here.
        return "Current tenant ID";      // Return the current tenant ID.
    }

}
```

Then, configure a dynamic data source (taking a Spring Boot project as an example):

```java
// Register the DynamicDatasource as a Bean.
@Bean
public DataSource dynamicDatasource() {
    DynamicDatasource dynamicDatasource = new DynamicDatasource();
    dynamicDatasource.setTargetDataSources(getAllDataSources());
    return dynamicDatasource;
}

private Map<Object, Object> getAllDataSources() {
    Map<Object, Object> dataSources = new HashMap<>();
    dataSources.put("Tenant 1 ID", new DataSource1());
    dataSources.put("Tenant 2 ID", new DataSource2());
    // Put all data sources into the dataSources map.
    return dataSources;
}
```
