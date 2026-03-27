# SQL 拦截器

Bean Searcher 支持配置多个 SQL 拦截器，用于在 SQL 执行前对其进行自定义修改。

## SqlInterceptor

```java
/**
 * Sql 拦截器
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.0.0
 */
public interface SqlInterceptor {

    /**
     * Sql 拦截
     * @param <T> 泛型
     * @param searchSql 检索 SQL 信息（非空）
     * @param paraMap 原始检索参数（非空）
     * @param fetchType 检索类型（v3.6.0 新增参数）
     * @return 新的检索 SQL（非空）
     */
    <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType);

}
```

在 SQL 拦截器中，可以对 `SearchSql` 对象进行任意修改，从而实现自定义的 SQL 逻辑。

* 案例：[使用 SQL 拦截器 实现 多字段排序](https://github.com/troyzhxu/bean-searcher/issues/9)（自 `v3.4.0` 起，框架已内置 [多字段排序](/guide/param/sort#多字段排序-since-v3-4) 功能）。

## 配置（SpringBoot / Grails）

在 SpringBoot / Grails 项目中，使用 `bean-searcher-boot-starter` 依赖时，只需要把定义好的 `SqlInterceptor` 声明为一个 Bean 即可：

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

## 配置（Others）

```java
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .addInterceptor(new MyFirstSqlInterceptor())
        .addInterceptor(new MySecondSqlInterceptor())
        .build();
```
