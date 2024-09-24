# 其它玩法

Bean Searcher 的参数过滤器 ParamFilter，非常易于自定义，可以让我们用简单几行代码，就玩出其它 ORM 难以实现的花样。

## 自动接收请求参数

```java
@Component
public class MyParamFilter implements ParamFilter {

    // 定义一个常量，作为一个开关，当启用时，则取消自动加载功能
    public static final String IGNORE_REQUEST_PARAMS = "IGNORE_REQUEST_PARAMS";

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        MapBuilder builder;
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes &&
                !paraMap.containsKey(IGNORE_REQUEST_PARAMS)) {
            // 在一个 Web 请求上下文中，并且没有开启 IGNORE_REQUEST_PARAMS，则取出前端传来的所有参数
            HttpServletRequest request ((ServletRequestAttributes) attributes).getRequest();
            builder = MapUtils.flatBuilder(request.getParameterMap());
        } else {
            builder = MapUtils.builder();
        }
        // 自定义查询参数，优先级最高，可以覆盖上面的参数
        builder.putAll(paraMap);
        return builder.build();
    }
}
```

* 然后，我们在 `Controller` 中写查询的时候，就可以不用再手动接收前端的请求参数了，可以使业务代码进一步的简化，例如：

```java
@GetMapping("/users")
public List<UserVO> users() {
    // 前端的请求参数，都已经在 MyParamFilter 中自动接收了
    return beanSearcher.searchList(UserVO.class);
}
```

* 有时候除了前端的请求参数，也需要后端根据业务构造一些额外添的参数，此时也只需处理这些额外参数既可以：

```java
@GetMapping("/users")
public List<UserVO> users() {
    var params = MapUtils.builder()
          .field(UserVO::getAge, 10, 20).op(Between.class)  // 额外参数
          .build();
    return beanSearcher.searchList(UserVO.class);
}
```

* 还有时候，我们不希望框架自动加载前端的请求参数，这时候，就可以使用上文定义的开关常量 `IGNORE_REQUEST_PARAMS`:

```java
@GetMapping("/users")
public List<UserVO> users() {
    var params = MapUtils.builder()
          .put(MyParamFilter.IGNORE_REQUEST_PARAMS, true)   // 取消自动接收功能
          .build();
    return beanSearcher.searchList(UserVO.class);
}
```

## 多租户隔离

```java
@Component
public class MyParamFilter implements ParamFilter {
    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        // 租户ID字段：tenantId
        var tenantId = getTenantIdFromCurrentRequest();  // 从当前请求中获取租户ID
        paraMap.put("tenantId", tenantId);
        return paraMap;
    }
}
```

该配置生效的前提是：检索实体类中存在 `tenantId` 字段，当然你可以把它定义在基类中。

## 逻辑删除

```java
@Component
public class MyParamFilter implements ParamFilter {
    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        // 为逻辑删除字段，指定参数值
        paraMap.put("deleted", false);
        return paraMap;
    }
}
```

该配置生效的前提是：检索实体类中存在 `deleted` 字段，当然你可以把它定义在基类中。
