# Parameter and Result Filters

## Parameter Filters

### SizeLimitParamFilter

> since v3.8.1, enabled by default

A risk control filter used to control the number of retrieval parameters, enabled by default. It has the following configuration items:

```properties
# Whether to enable this filter, default is true
bean-searcher.params.filter.use-size-limit = true
# The maximum allowed number of retrieval parameters, default is 150
bean-searcher.params.filter.max-para-map-size = 150
```

### ArrayValueParamFilter

> since v4.3.0, enabled by default

Used in conjunction with the `MapUtils.flat(..)` and `MapUtils.flatBuilder(..)` methods to be compatible with the usage of array parameter values. For example, when the front - end passes parameters, **parameters with the same name** can pass **multiple values**:

```
GET /user/list ? age=20 & age=30 & age-op=bt
```

It has the following configuration items:

```properties
# Whether to enable this filter, default is true
bean-searcher.params.filter.use-array-value = true
```

> The original parameter syntax is still supported after enabling.

### SuffixOpParamFilter

> since v4.3.0, disabled by default

A suffix operator parameter filter used to simplify front - end parameter passing. When enabled, the original field parameter name and the operator can be combined into a new parameter. For example:

* Combine the parameters `age = 30` and `age-op = eq`: `age-eq = 25`.
* Combine the parameters `age = 25` and `age-op = gt`: `age-gt = 25`.
* Combine the parameters `name = J` and `name-op = sw`: `name-sw = J`.

The `-` in `age-eq`, `age-gt`, `name-sw` is the same hyphen as the `-` in `age-op`, `name-op`, which can be customized through the configuration item [`bean-searcher.params.separator`](/en/guide/param/field).

If you want to enable this filter, you need to add the following configuration:

```properties
# Whether to enable this filter, default is false
bean-searcher.params.filter.use-suffix-op = true
```

> The original parameter syntax is still supported after enabling.

### JsonArrayParamFilter

> since v4.3.0, disabled by default

A JSON array parameter value filter used to simplify front - end parameter passing. After enabling, when the front - end needs to add multiple values for the same field parameter, it can be passed in a JSON array. For example:

* `age=[20,30] & age-op=bt` replaces the original `age-0 = 20 & age-1 = 30 & age-op = bt`
* `id=[1,2,3] & id-op=il` replaces the original `id-0 = 1 & id-1 = 2 & id-2 = 3 & id-op = il`

If you also enable the [SuffixOpParamFilter](#suffixopparamfilter), the above parameters can be further simplified:

* `age-bt=[20,30]` replaces the original `age-0 = 20 & age-1 = 30 & age-op = bt`
* `id-il=[1,2,3]` replaces the original `id-0 = 1 & id-1 = 2 & id-2 = 3 & id-op = il`

If you want to enable this filter, you need to add the following configuration:

```properties
# Whether to enable this filter, default is false
bean-searcher.params.filter.use-json-array = true
```

> The original parameter syntax is still supported after enabling.

Since JSON conversion is involved, it is inevitable to use JSON parsing related frameworks. However, different developers may prefer different JSON frameworks. So this filter is not bound to a specific JSON framework, but supports users to choose their own (currently there are 5 frameworks available by default). You only need to add the following specific dependencies (if not added, this filter will not work): 

* Use the Jackson framework

```groovy
implementation 'cn.zhxu:xjsonkit-jackson:1.5.1'
```

* Use the Gson framework

```groovy
implementation 'cn.zhxu:xjsonkit-gson:1.5.1'
```

* Use the Fastjson framework

```groovy
implementation 'cn.zhxu:xjsonkit-fastjson:1.5.1'
```

* Use the Fastjson2 framework

```groovy
implementation 'cn.zhxu:xjsonkit-fastjson2:1.5.1'
```

* Use the Snack3 framework

```groovy
implementation 'cn.zhxu:xjsonkit-snack3:1.5.1'
```

If your favorite JSON parsing framework is not included, you can also customize the underlying implementation. Refer to: https://gitee.com/troyzhxu/xjsonkit

### IndexArrayParamFilter

> since v4.4.0, disabled by default

The index array parameter filter is used to be compatible with array parameters in the following form (some HTTP clients will default to passing array parameters in this way):

```
GET /user/list ? age[0]=20 & age[1]=30 & age-op=bt
```

If you want to enable this filter, you need to add the following configuration:

```properties
# Whether to enable this filter, default is false
bean-searcher.params.filter.use-index-value = true
```

> The original parameter syntax is still supported after enabling.

### Parameter Filter Priority

If you are using the `bean-searcher-boot-starter` or `bean-searcher-solon-plugin` dependency, the priority of the above built - in filters in the framework is as follows:

Filter | Priority (the smaller, the earlier to execute)
-|-
`SizeLimitParamFilter` | `-100`
`ArrayValueParamFilter` | `100`
`SuffixOpParamFilter` | `200`
`JsonArrayParamFilter` | `300`
`IndexArrayParamFilter` | `400`

The priority of newly injected parameter filters is determined by the injection method of the IOC framework. For example, in the SpringBoot framework, you can use the `@Order` annotation to control the priority, and in the Solon framework, you can use the `index` attribute of the `@Bean` annotation to control it.

### Custom Parameter Filter

You can also customize any parameter filtering rules. In SpringBoot / Solon / Grails projects, you only need to configure a Bean (taking SpringBoot as an example):

```java
@Bean
public ParamFilter myParamFilter() {
    return new ParamFilter() {
        @Override
        public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
            // beanMeta is the meta - information of the entity class being retrieved, paraMap is the current retrieval parameter
            // TODO: You can write some custom parameter filtering rules here
            return paraMap;      // Return the filtered retrieval parameters
        }
    };
}
```

For other projects, the configuration method is as follows:

```java
DefaultParamResolver paramResolver = new DefaultParamResolver();
// Add parameter filters
paramResolver.addParamFilter(new MyParamFilter1());
paramResolver.addParamFilter(new MyParamFilter2());
// Build a Map searcher
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Omit other configurations
        .paramResolver(paramResolver)   // The BeanSearcher searcher is also configured in the same way
        .build();
```

## Result Filter (v3.6.0)

Sometimes, you may want to further process the retrieval results of Bean Searcher. In this case, you can use the `ResultFilter` to handle it.

### ResultFilter

It is an interface that defines two methods, which act on the `BeanSearcher` and `MapSearcher` searchers respectively:

```java
public interface ResultFilter {

    /**
     * ResultFilter
     * Further convert and process the retrieval results of {@link BeanSearcher }
     * @param result The retrieval result
     * @param beanMeta The meta - information of the retrieved entity class
     * @param paraMap The retrieval parameters
     * @param fetchType The retrieval type
     * @param <T> Generic type
     * @return The converted retrieval result
     */
    default <T> SearchResult<T> doBeanFilter(SearchResult<T> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return result;
    }

    /**
     * Further convert and process the retrieval results of {@link MapSearcher }
     * @param result The retrieval result
     * @param beanMeta The meta - information of the retrieved entity class
     * @param paraMap The retrieval parameters
     * @param fetchType The retrieval type
     * @param <T> Generic type
     * @return The converted retrieval result
     */
    default <T> SearchResult<Map<String, Object>> doMapFilter(SearchResult<Map<String, Object>> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return result;
    }

}
```

### Configuration (SpringBoot / Grails)

In SpringBoot / Grails projects, when using the `bean-searcher-boot-starter` dependency, you only need to declare the defined `ResultFilter` as a Bean:

```java
@Bean
public ResultFilter myFitstResultFilter() {
    return new MyFirstResultFilter();
}

@Bean
public ResultFilter mySecondResultFilter() {
    return new MySecondResultFilter();
}
```

### Configuration (Non - Boot Spring Projects)

```xml
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- Omit other attribute configurations, the BeanSearcher searcher is also configured in the same way -->
    <property name="resultFilters">
        <list>
            <bean class="com.example.FitstResultFilter" />
            <bean class="com.example.SecondResultFilter" />
        </list>
    </property>
</bean>
```

### Configuration (Others)

```java
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .addResultFilter(new FitstResultFilter());      // since v3.6.1
        .addResultFilter(new SecondResultFilter());     // since v3.6.1
        // Omit other attribute configurations, the BeanSearcher searcher is also configured in the same way
        .build();
```
