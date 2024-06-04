# 参数与结果过滤器

## 参数过滤器

Bean Searcher 还支持配置全局参数过滤器，可自定义任何参数过滤规则。

* 案例：[使用 参数过滤器器 简化 op=mv/bt 时的多值传参，例如：用 age=[20,30] 替代 age-0=20 & age-1=30 参数](https://github.com/troyzhxu/bean-searcher/issues/10)。

### 在 SpringBoot / SpringMVC / Grails 项目中，只需要配置一个 Bean（以 SpringBoot 为例）：

```java
@Bean
public ParamFilter myParamFilter() {
    return new ParamFilter() {
        @Override
        public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
            // beanMeta 是正在检索的实体类的元信息, paraMap 是当前的检索参数
            // TODO: 这里可以写一些自定义的参数过滤规则
            return paraMap;      // 返回过滤后的检索参数
        }
    };
}
```

### 其它项目，配置方法：

```java
DefaultParamResolver paramResolver = new DefaultParamResolver();
// 添加参数过滤器
paramResolver.setParamFilters(new ParamFilter[] { 
    new MyParamFilter1(),
    new MyParamFilter2(),
});
// 构建 Map 检索器
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .paramResolver(paramResolver)   // BeanSearcher 检索器也同此配置
        .build();
```

## 结果过滤器（v3.6.0）

有时可能想对 Bean Searcher 的检索结果做进一步的处理，这时候可以使用 `ResultFilter` 来处理。

### ResultFilter

它是一个接口，共定义了两个方法，它分别作用于 `BeanSearcher` 与 `MapSearcher` 检索器：

```java
public interface ResultFilter {

    /**
     * ResultFilter
     * 对 {@link BeanSearcher } 的检索结果做进一步转换处理
     * @param result 检索结果
     * @param beanMeta 检索实体类的元信息
     * @param paraMap 检索参数
     * @param fetchType 检索类型
     * @param <T> 泛型
     * @return 转换后的检索结果
     */
    default <T> SearchResult<T> doBeanFilter(SearchResult<T> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return result;
    }

    /**
     * 对 {@link MapSearcher } 的检索结果做进一步转换处理
     * @param result 检索结果
     * @param beanMeta 检索实体类的元信息
     * @param paraMap 检索参数
     * @param fetchType 检索类型
     * @param <T> 泛型
     * @return 转换后的检索结果
     */
    default <T> SearchResult<Map<String, Object>> doMapFilter(SearchResult<Map<String, Object>> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return result;
    }

}
```

### 配置（SpringBoot / Grails）

在 SpringBoot / Grails 项目中，使用 `bean-searcher-boot-starter` 依赖时，只需要把定义好的 `ResultFilter` 声明为一个 Bean 即可：

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

### 配置（非 Boot 的 Spring 项目）

```xml
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="resultFilters">
        <list>
            <bean class="com.example.FitstResultFilter" />
            <bean class="com.example.SecondResultFilter" />
        </list>
    </property>

</bean>
```

### 配置（Others）

```java
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .addResultFilter(new FitstResultFilter());      // since v3.6.1
        .addResultFilter(new SecondResultFilter());     // since v3.6.1
        // 省略其它属性配置，BeanSearcher 检索器也同此配置
        .build();
```
