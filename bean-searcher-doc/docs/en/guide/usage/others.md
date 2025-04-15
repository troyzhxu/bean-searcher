# Other Usages

The parameter filter `ParamFilter` of Bean Searcher is very easy to customize. With just a few simple lines of code, we can achieve things that are difficult to implement with other ORMs.

## Automatically Receive Request Parameters

```java
@Component
public class MyParamFilter implements ParamFilter {

    // Define a constant as a switch. When enabled, the automatic loading function is cancelled.
    public static final String IGNORE_REQUEST_PARAMS = "IGNORE_REQUEST_PARAMS";

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        MapBuilder builder;
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes &&
                !paraMap.containsKey(IGNORE_REQUEST_PARAMS)) {
            // In a Web request context and IGNORE_REQUEST_PARAMS is not enabled, retrieve all parameters sent from the front end.
            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
            builder = MapUtils.flatBuilder(request.getParameterMap());
        } else {
            builder = MapUtils.builder();
        }
        // Custom query parameters have the highest priority and can override the above parameters.
        builder.putAll(paraMap);
        return builder.build();
    }
}
```

* Then, when writing queries in the `Controller`, we no longer need to manually receive the front-end request parameters, which can further simplify the business code. For example:

```java
@GetMapping("/users")
public List<UserVO> users() {
    // The front-end request parameters have been automatically received in MyParamFilter.
    return beanSearcher.searchList(UserVO.class);
}
```

* Sometimes, in addition to the front-end request parameters, the back-end also needs to construct some additional parameters based on the business. In this case, we only need to handle these additional parameters:

```java
@GetMapping("/users")
public List<UserVO> users() {
    var params = MapUtils.builder()
          .field(UserVO::getAge, 10, 20).op(Between.class)  // Additional parameters
          .build();
    return beanSearcher.searchList(UserVO.class, params);
}
```

* There are also times when we don't want the framework to automatically load the front-end request parameters. In this case, we can use the switch constant `IGNORE_REQUEST_PARAMS` defined above:

```java
@GetMapping("/users")
public List<UserVO> users() {
    var params = MapUtils.builder()
          .put(MyParamFilter.IGNORE_REQUEST_PARAMS, true)   // Cancel the automatic receiving function
          .build();
    return beanSearcher.searchList(UserVO.class, params);
}
```

## Multi-Tenant Isolation

```java
@Component
public class MyParamFilter implements ParamFilter {
    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        // Tenant ID field: tenantId
        var tenantId = getTenantIdFromCurrentRequest();  // Get the tenant ID from the current request
        paraMap.put("tenantId", tenantId);
        return paraMap;
    }
}
```

The premise for this configuration to take effect is that the retrieval entity class has a `tenantId` field. Of course, you can define it in the base class.

## Logical Deletion

```java
@Component
public class MyParamFilter implements ParamFilter {
    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        // Specify the parameter value for the logical deletion field.
        paraMap.put("deleted", false);
        return paraMap;
    }
}
```

The premise for this configuration to take effect is that the retrieval entity class has a `deleted` field. Of course, you can define it in the base class.
