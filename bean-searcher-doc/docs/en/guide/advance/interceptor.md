# SQL Interceptor

Bean Searcher supports configuring multiple SQL interceptors to customize the SQL generation rules.

## SqlInterceptor

```java
/**
 * Sql interceptor
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.0.0
 */
public interface SqlInterceptor {

    /**
     * Sql interception
     * @param <T> Generic type
     * @param searchSql Retrieval SQL information (not null)
     * @param paraMap Original retrieval parameters (not null)
     * @param fetchType Retrieval type (new parameter since v3.6.0)
     * @return New retrieval SQL (not null)
     */
    <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType);

}
```

In the SQL interceptor, we can modify `SearchSql` to implement our custom logic.

* Case: [Using SQL Interceptor to Implement Multi-Field Sorting](https://github.com/troyzhxu/bean-searcher/issues/9) (Since `v3.4.0`, the framework has built-in [Multi-Field Sorting](/en/guide/param/sort#Multi-Field Sorting - Since v3.4) functionality).

## Configuration (SpringBoot / Grails)

In SpringBoot / Grails projects, when using the `bean-searcher-boot-starter` dependency, you only need to declare the defined `SqlInterceptor` as a Bean:

```java
@Bean
public SqlInterceptor myFirstSqlInterceptor() {
    return new MyFirstSqlInterceptor();
}

@Bean
public SqlInterceptor mySecondSqlInterceptor() {
    return new MySecondSqlInterceptor();
}
```

## Configuration (Non-Boot Spring Projects)

```xml
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- Other attribute configurations are omitted. The BeanSearcher retriever is configured in the same way -->
    <property name="interceptors">
        <list>
            <bean class="com.example.MyFirstSqlInterceptor" />
            <bean class="com.example.MySecondSqlInterceptor" />
        </list>
    </property>
</bean>
```

## Configuration (Others)

```java
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Other attribute configurations are omitted. The BeanSearcher retriever is configured in the same way
        .addInterceptor(new MyFirstSqlInterceptor())
        .addInterceptor(new MySecondSqlInterceptor())
        .build();
```
