
# 集成

集成 v3.x 的 Bean Searcher 比 v2.x 更加简单（不再需要配置 SearchBean 所在包名路径）。

通常情况下，我们都是在一个后端的 Java Web 项目中使用 Bean Searcher，它可以在任意的 Web 框架中使用，以下介绍在常见的几种 Web 框架的集成方法：

## Spring Boot / Grails

只需添加 `bean-searcher-boot-starter` 依赖，就已集成完毕！

::: tip 提示
Grails 项目使用 `bean-searcher-boot-starter` 依赖时必须使用大于等于 `v3.1.4`、`v3.2.3` 或 `v3.3.1` 的较新版本。
:::

另外，在 Grails 框架下，如果你想 Bean Searcher 复用它的 domain（域类），则需要在 `application.yml` 中添加以下配置（这是因为 Grails 会在运行时为 domain 添加一个非 DB 映射属性，我们需要将它忽略）：

```yml
bean-searcher:
    sql:
        default-mapping:
            ignore-fields: org_grails_datastore_gorm_GormValidateable__errors
```

* [SpringBoot 集成案例](https://gitee.com/troyzhxu/bean-searcher/tree/dev/bean-searcher-demos/bs-demo-springboot)
* [Grails 集成案例](https://gitee.com/troyzhxu/bean-searcher/tree/dev/bean-searcher-demos/bs-demo-grails)

## 非 Boot 的 Spring 项目

在传统的 Spring MVC 项目中需要添加 `bean-searcher` 核心依赖，然后在项目的 xml 文件内配置如下：

```xml
<bean id="sqlExecutor" 
        class="cn.zhxu.bs.implement.DefaultSqlExecutor" 
        p:dataSource-ref="dataSource" />
<!-- 声明 BeanSearcher 检索器，它查询的结果是 SearchBean 泛型对象 -->
<bean id="beanSearcher" 
        class="cn.zhxu.bs.implement.DefaultBeanSearcher"
        p:sqlExecutor-ref="sqlExecutor" />
<!-- 声明 MapSearcher 检索器，它查询的结果是 Map 对象 -->
<bean id="mapSearcher" 
        class="cn.zhxu.bs.implement.DefaultMapSearcher"
        p:sqlExecutor-ref="sqlExecutor" />
```

## Grails (只使用 bean-searcher 依赖)

在 Grails 项目中集成 Bean Searcher，只需要在 `grails-app/conf/spring/resources.groovy` 文件内配置三个 Bean 即可：

```groovy
import cn.zhxu.bs.implement.DefaultSqlExecutor
import cn.zhxu.bs.implement.DefaultBeanSearcher
import cn.zhxu.bs.implement.DefaultMapSearcher

sqlExecutor(DefaultSqlExecutor) {
    dataSource = ref('dataSource')
}
// 声明 BeanSearcher 检索器，它查询的结果是 SearchBean 泛型对象
beanSearcher(DefaultBeanSearcher) {
    sqlExecutor = ref('sqlExecutor')
}
// 声明 MapSearcher 检索器，它查询的结果是 Map 对象
mapSearcher(DefaultMapSearcher) {
    sqlExecutor = ref('sqlExecutor')
}
```

## Solon 项目

只需添加 `bean-searcher-solon-plugin` 依赖，就已集成完毕！支持 `solon` 的 `v2.2.1` 及以上版本。

::: tip 提示
`bean-searcher-solon-plugin` 也提供了与 `bean-searcher-boot-starter` 一样的配置项。
:::

* [Solon 集成案例](https://gitee.com/troyzhxu/bean-searcher/tree/dev/bean-searcher-demos/bs-demo-solon)

> Solon 文档地址：https://solon.noear.org/

## Others

在其它任意的 Java 项目中，都可以在项目启动时，使用 `SearcherBuilder ` 快速构建一个 `MapSearcher` 和 `BeanSearcher` 检索器: 

```java
// 使用默认数据源
DefaultSqlExecutor sqlExecutor = new DefaultSqlExecutor(getDefaultDataSource());
// sqlExecutor.addDataSource("slave1", getSlave1DataSource());  // 添加具名数据源 slave1
// sqlExecutor.addDataSource("slave2", getSlave2DataSource());  // 添加具名数据源 slave2

// 构建 Map 检索器
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .sqlExecutor(sqlExecutor)
        .build();
// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        .sqlExecutor(sqlExecutor)
        .build();
```
