# 参数与结果过滤器

## 参数过滤器

### SizeLimitParamFilter

> since v3.8.1，默认启用

风控过滤器，用于控制检索参数的数量，默认启用。它有以下配置项：

```properties
# 是否启用该过滤器，默认为 true
bean-searcher.params.filter.use-size-limit = true
# 检索参数的最大允许数量，默认 150
bean-searcher.params.filter.max-para-map-size = 150
```

### ArrayValueParamFilter

> since v4.3.0，默认启用

用于配合 `MapUtils.flat(..)` 与 `MapUtils.flatBuilder(..)` 方法，兼容数组参数值的用法，例如前端传参时 **同名参数** 传 **多个值** 的情况：

```
GET /user/list ? age=20 & age=30 & age-op=bt
```

它有以下配置项：

```properties
# 是否启用该过滤器，默认为 true
bean-searcher.params.filter.use-array-value = true
```

> 启用之后原来的参数语法仍然支持。

### SuffixOpParamFilter

> since v4.3.0，默认禁用

后缀运算符参数过滤器，用于简化前端传参。当启用时，原始字段参数名 与 运算符 可以结合为一个新的参数，例如：

* 参数 `age=30` 与 `age-op=eq` 结合在一起： `age-eq=25`。
* 参数 `age=25` 与 `age-op=gt` 结合在一起： `age-gt=25`。
* 参数 `name=J` 与 `name-op=sw` 结合在一起： `name-sw=J`。

其中 `age-eq`、`age-gt`、`name-sw` 中的 `-` 与 `age-op`、`name-op` 中的 `-` 是同一个连字符，可以通过配置项 [`bean-searcher.params.separator`](/guide/param/field) 进行自定义。

如果要启用该过滤器，需要添加以下配置：

```properties
# 是否启用该过滤器，默认为 false
bean-searcher.params.filter.use-suffix-op = true
```

> 启用之后原来的参数语法仍然支持。

### JsonArrayParamFilter

> since v4.3.0，默认禁用

JSON 数组参数值过滤器，用于简化前端传参。启用后，当前端需要为同一个字段参数添加多个值时，可以放在一个 JSON 数组进行传递，例如：

* `age=[20,30] & age-op=bt` 替代原来的 `age-0=20 & age-1=30 & age-op=bt`
* `id=[1,2,3] & id-op=il` 替代原来的 `id-0=1 & id-1=2 & id-2=3 & id-op=il`

如果你同时启用了 [SuffixOpParamFilter](#suffixopparamfilter)，则上例中的参数还可以进一步简化：

* `age-bt=[20,30]` 替代原来的 `age-0=20 & age-1=30 & age-op=bt`
* `id-il=[1,2,3]` 替代原来的 `id-0=1 & id-1=2 & id-2=3 & id-op=il`

如果要启用该过滤器，需要添加以下配置：

```properties
# 是否启用该过滤器，默认为 false
bean-searcher.params.filter.use-json-array = true
```

> 启用之后原来的参数语法仍然支持。

由于涉及到 JSON 转换，不可避免的需要使用 JSON 解析相关的框架，但不同的开发者可能偏好不同的 JSON 框架，所以该过滤器并没有与特定的 JSON 框架绑定，而是支持用户自己选择（目前默认有 5 种框架可选），只需要添加如下特定依赖即可（若不添加则该过滤器不生效）： 

* 使用 Jackson 框架

```gradle
implementation 'cn.zhxu:xjsonkit-jackson:1.5.0'
```

* 使用 Gson 框架

```gradle
implementation 'cn.zhxu:xjsonkit-gson:1.5.0'
```

* 使用 Fastjson 框架

```gradle
implementation 'cn.zhxu:xjsonkit-fastjson:1.5.0'
```

* 使用 Fastjson2 框架

```gradle
implementation 'cn.zhxu:xjsonkit-fastjson2:1.5.0'
```

* 使用 Snack3 框架

```gradle
implementation 'cn.zhxu:xjsonkit-snack3:1.5.0'
```

如果你喜欢的 JSON 解析框架不在其内，也支持自定义底层实现，参考：https://gitee.com/troyzhxu/xjsonkit

### 自定义参数过滤器

你还可自定义任何参数过滤规则，在 SpringBoot / SpringMVC / Grails 项目中，只需要配置一个 Bean（以 SpringBoot 为例）：

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

其它项目，配置方法：

```java
DefaultParamResolver paramResolver = new DefaultParamResolver();
// 添加参数过滤器
paramResolver.addParamFilter(new MyParamFilter1());
paramResolver.addParamFilter(new MyParamFilter2());
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
