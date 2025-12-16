# Integration

Integrating Bean Searcher v3.x is simpler than v2.x (there's no need to configure the package path of SearchBean anymore).

Normally, we use Bean Searcher in a backend Java Web project, and it can be used in any Web framework. The following introduces the integration methods in several common Web frameworks:

Integration examples：

* Gitee: https://gitee.com/troyzhxu/bean-searcher/tree/main/bean-searcher-demos
* Github: https://github.com/troyzhxu/bean-searcher/tree/main/bean-searcher-demos

## Spring Boot / Grails

Just add the `bean-searcher-boot-starter` dependency, and the integration is complete!

::: tip Tip
When using the `bean-searcher-boot-starter` dependency in a Grails project, you must use a newer version greater than or equal to `v3.1.4`, `v3.2.3`, or `v3.3.1`.
:::

In addition, in the Grails framework, if you want Bean Searcher to reuse its domain (domain classes), you need to add the following configuration to `application.yml` (this is because Grails adds a non-DB mapping property to the domain at runtime, and we need to ignore it):

```yml
bean-searcher:
    sql:
        default-mapping:
            ignore-fields: org_grails_datastore_gorm_GormValidateable__errors
```

## Non-Boot Spring Projects

In a traditional Spring MVC project, you need to add the core `bean-searcher` dependency, and then configure the following in the project's XML file:

```xml
<bean id="sqlExecutor" 
        class="cn.zhxu.bs.implement.DefaultSqlExecutor" 
        p:dataSource-ref="dataSource" />
<!-- Declare the BeanSearcher retriever, whose query result is a generic object of SearchBean -->
<bean id="beanSearcher" 
        class="cn.zhxu.bs.implement.DefaultBeanSearcher"
        p:sqlExecutor-ref="sqlExecutor" />
<!-- Declare the MapSearcher retriever, whose query result is a Map object -->
<bean id="mapSearcher" 
        class="cn.zhxu.bs.implement.DefaultMapSearcher"
        p:sqlExecutor-ref="sqlExecutor" />
```

## Grails (Using Only the `bean-searcher` Dependency)

To integrate Bean Searcher in a Grails project, you only need to configure three Beans in the `grails-app/conf/spring/resources.groovy` file:

```groovy
import cn.zhxu.bs.implement.DefaultSqlExecutor
import cn.zhxu.bs.implement.DefaultBeanSearcher
import cn.zhxu.bs.implement.DefaultMapSearcher

sqlExecutor(DefaultSqlExecutor) {
    dataSource = ref('dataSource')
}
// Declare the BeanSearcher retriever, whose query result is a generic object of SearchBean
beanSearcher(DefaultBeanSearcher) {
    sqlExecutor = ref('sqlExecutor')
}
// Declare the MapSearcher retriever, whose query result is a Map object
mapSearcher(DefaultMapSearcher) {
    sqlExecutor = ref('sqlExecutor')
}
```

## Solon Projects

Just add the `bean-searcher-solon-plugin` dependency, and the integration is complete! It supports `solon` version `v2.2.1` and above.

::: tip Tip
The `bean-searcher-solon-plugin` also provides the same configuration items as the `bean-searcher-boot-starter`.
:::

## Others

In any other Java project, you can quickly build a `MapSearcher` and a `BeanSearcher` retriever using `SearcherBuilder` when the project starts:

```java
// Use the default data source
DefaultSqlExecutor sqlExecutor = new DefaultSqlExecutor(getDefaultDataSource());
// sqlExecutor.addDataSource("slave1", getSlave1DataSource());  // Add a named data source slave1
// sqlExecutor.addDataSource("slave2", getSlave2DataSource());  // Add a named data source slave2

// Build the Map retriever
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .sqlExecutor(sqlExecutor)
        .build();
// Build the Bean retriever
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        .sqlExecutor(sqlExecutor)
        .build();
```