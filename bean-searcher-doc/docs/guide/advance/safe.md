# 约束与风控

## 检索条件约束

从前面的 [字段参数](/guide/param/field) 章节，我们知道，Bean Searcher 对实体类中的每一个字段，都直接支持了很多的检索方式。但有时候我们可能并不需要这么多，甚至某些时候我们需要禁止一些方式。Bean Searcher 使用 [运算符约束](#运算符约束) 和 [条件约束](#条件约束) 来实现此需求。

### 运算符约束

例如：字段 `name` 只允许 **精确匹配** 与 **后模糊匹配**，则，可以在 SearchBean 上使用如下注解：

```java
public class User {

    @DbField(onlyOn = {Equal.class, StartWith.class})           // v3.3.0+ 的写法
    @DbField(onlyOn = {FieldOps.Equal, FieldOps.StartWith})     // v3.3.0 以前的写法
    private String name;

    // 为减少篇幅，省略其它字段...
}
```

如上，通过 `@DbField` 注解的 `onlyOn` 属性，指定 `name` 字段只能适用与 **精确匹配** 和 **后模糊匹配** 方式，其它检索方式它将直接忽略。

::: tip 默认运算符
* 若 `@DbField.onlyOn` 为空，则该字段的 默认运算符 为 **Equal**。
* 若 `@DbField.onlyOn` 不空，则其 **第一个值** 就是该字段的 默认运算符。
:::

上面的代码是限制了 `name` 只能有两种检索方式，如果再严格一点，**只允许 精确匹配**，那其实有两种写法。

#### （1）还是使用运算符约束：

```java
@SearchBean(tables="user u, role r", where="u.role_id = r.id", autoMapTo="u") 
public class User {

    @DbField(onlyOn = Equal.class)
    private String name;

    // 为减少篇幅，省略其它字段...
}
```

#### （2）在 Controller 的接口方法里把运算符参数覆盖：

```java
@GetMapping("/index")
public SearchResult<Map<String, Object>> index(HttpServletRequest request) {
    Map<String, Object> params = MapUtils.flatBuilder(request.getParameterMap())
        .field(User::getName).op(Equal.class)   // 把 name 字段的运算符直接覆盖为 Equal
        .build()
    return mapSearcher.search(User.class, params);
}
```

### 条件约束

有时候我们不想让某个字段参与 where 条件，可以这样：

```java
public class User {

    @DbField(conditional = false)
    private int age;

    // 为减少篇幅，省略其它字段...
}
```

如上，通过 `@DbField` 注解的 `conditional` 属性， 就直接不允许 `age` 字段参与条件了，无论前端怎么传参，Bean Searcher 都不搭理。

#### 其它项目，配置方法：

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

## 风控配置项

Bean Searcher 默认提供了一些风险控制项，并支持配置。

在 SpringBoot / Grails/ Solon 项目中，若使用了 `bean-searcher-boot-starter` 或 `bean-searcher-solon-plugin` 依赖，则可在项目配置文件（例如：`application.properties`）中使用如下配置项：

配置键名 | 含义 | 可选值 | 默认值 | 开始版本
-|-|-|-|-
`bean-searcher.params.pagination.max-allowed-size` | 每页最大查询条数（分页保护） | `正整数` | `100` | v2.0.0
`bean-searcher.params.pagination.max-allowed-offset` | 最大分页深度 | `正整数` | `20000` | v3.8.1
`bean-searcher.params.filter.max-para-map-size` | 检索参数最大允许的键值对数 | `正整数` | `150` | v3.8.1
`bean-searcher.params.group.max-expr-length` | 逻辑分组表达式的最大长度（字符数） | `正整数` | `50` | v3.8.1
