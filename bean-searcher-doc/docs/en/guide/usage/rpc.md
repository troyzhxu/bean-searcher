# Call Remote BS Services

## Scenario Example

In Service A, there is a user list interface (GET /user/list). This interface is driven by Bean Searcher and follows the parameter conventions of Bean Searcher.

Suppose you are currently developing Service B, and in Service B, you need to remotely call this user list interface in Service A. So, how should you organize the parameters? Can you use the parameter builder?

## Without Using the Parameter Builder

For example, if you need to retrieve the 20 users with the maximum age from a list of user IDs, your code might look like this:

```java
List<Long> userIds = getUserIds();  // User IDs
// User ID parameters
Map<String, Object> params = new HashMap<>();
for (int i = 0; i < userIds.size(); i++) {
    params.put("id-" + i, userIds.get(i));
}
params.put("id-op", "il");
// Sort by age in descending order
params.put("sort", "age");
params.put("order", "desc");
// Pagination parameters
params.put("page", 0);
params.put("size", 20);
// Call the interface in the remote service
List<User> users = romoteApi.getUserList(params);
```

## Using the Parameter Builder

Since `v4.3.0`, you can also use the `buildForRpc()` method provided by the parameter builder to generate request parameters for remote calls:

```java
List<Long> userIds = getUserIds();  // User IDs
// Organize retrieval parameters
Map<String, Object> params = MapUtils.builder()
        .field(User::getId, userIds).op(InList.class)
        .orderBy(User::getAge).desc()
        .page(0, 20)
        .buildForRpc();
// Call the interface in the remote service
List<User> users = romoteApi.getUserList(params);
```

## Customizing Parameter Names

If the Bean Searcher interface in the remote service has customized some parameter names (for example, the pagination parameter names use `pageNo` and `pageSize`, and the field parameter name separator uses an underscore `_`), you can also use the `buildForRpc(RpcNames)` method provided by the parameter builder:

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getId, userIds).op(InList.class)
        .page(0, 20)
        .buildForRpc(
            RpcNames.newNames()
                .separator("_")   // Use an underscore as the field parameter name separator
                .page("pageNo")   // Use pageNo as the pagination page number parameter
                .size("pageSize") // Use pageSize as the pagination size parameter
        );
// Call the interface in Service A
List<User> users = romoteApi.getUserList(params);
```

Alternatively, you can modify the default configuration of global RPC parameter names when the service starts:

```java
RpcNames.DEFAULT          // Default configuration object
    .separator("_")       // Use an underscore as the field parameter name separator
    .page("pageNo")       // Use pageNo as the pagination page number parameter
    .size("pageSize");    // Use pageSize as the pagination size parameter
```

This configuration only affects the `buildForRpc(..)` method and has no impact on the `BeanSearcher` and `MapSearcher` retrievers in this service.
