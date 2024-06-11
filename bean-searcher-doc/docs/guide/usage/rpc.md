# 请求第三方 BS 服务

## 场景案例

A 服务中有一个用户列表接口（GET /user/list），该接口是用 Bean Searcher 实现的，并且遵从了 Bean Searcher 的参数约定。

假设现在你正在开发一个 B 服务，在 B 服务中你需要调用 A 服务中的这个用户列表接口，那么你该如何组织参数呢？能够使用上参数构建器呢？

## 不使用参数构建器

例如，你需要按照多个用户状态去检索用户，你的代码可能会这样写：

```java
List<String> userStatusList = getUserStatusList();  // 用户状态
// 用户状态参数
Map<String, Object> params = new HashMap<>();
for (int i = 0; i < userStatusList.size(); i++) {
    params.put("status-" + i, userStatusList.get(i));
}
params.put("status-op", "il");
// 分页参数
params.put("page", 0);
params.put("size", 20);
// 调用 A 服务中的接口
List<User> users = romoteApi.getUserList(params);
```

## 使用参数构建器

自 `v4.3.0` 起，你也可能使用参数构建器提供的 `buildForRpc()` 方法，来生成远程调用的请求参数了：

```java
List<Long> userIds = getUserIds();  // 用户ID
// 组织检索参数
Map<String, Object> params = MapUtils.builder()
        .field(User::getId, userIds).op(InList.class)
        .page(0, 20)
        .buildForRpc();
// 调用 A 服务中的接口
List<User> users = romoteApi.getUserList(params);
```

## 自定义参数名

如果 A 服务中有一个用户列表接口，自定义了一些参数名（比如：分页参数名使用 `pageNo` 与 `pageSize`，字段参数名分隔符使用了下划线 `_`），那么也可以使用参数构建器提供的 `buildForRpc(RpcNames)` 方法：

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getId, userIds).op(InList.class)
        .page(0, 20)
        .buildForRpc(
            RpcNames.separator("_")   // 字段参数名分隔符使用了下划线
                .page("pageNo")       // 分页页码参数使用 pageNo
                .size("pageSize")     // 分页大小参数使用 pageSize
        );
// 调用 A 服务中的接口
List<User> users = romoteApi.getUserList(params);
```

或者，你也可以服务启动时修改全局 RPC 参数名默认配置：

```java
RpcNames.DEFAULT          // 默认配置对象
    .separator("_")       // 字段参数名分隔符使用了下划线
    .page("pageNo")       // 分页页码参数使用 pageNo
    .size("pageSize");    // 分页大小参数使用 pageSize
```

该配置只影响 `buildForRpc(..)` 方法，对本服务的 `BeanSearcher` 与 `MapSearcher` 检索器没有影响。
