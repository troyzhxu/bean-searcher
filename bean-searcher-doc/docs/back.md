
### 区间查询

以例说明，查询 `age` 在 18 与 30 之间的用户：

```java
Map<String, Object> params = new HashMap<>();
params.put("age-0", 18);
params.put("age-1", 30);
params.put("age-op", "bt");    // 区间查询，age 字段 2 个值
// 查询在 18 与 30 之间的用户（包含边界） 
```

当 `age` 字段的参数值只有一个，或第二个参数值为 null 时，`Between` 运算符查询大于等于第一个参数值的数据：

```java
Map<String, Object> params = new HashMap<>();
params.put("age", 18);
params.put("age-op", "bt");    // 区间查询，name 字段有 1 个值，或第 2 个为 null
// 查询大于等于 18 的用户
```

当 `age` 字段的参数值有两个，但 第一个为 `null` 时，`Between` 运算符查询小于等于第二个参数值的数据：

```java
Map<String, Object> params = new HashMap<>();
params.put("age-0", null);
params.put("age-1", 30);
params.put("age-op", "bt");    // 区间查询，age 字段有 2 个值，但第 1 个为 null
// 查询小于等于 30 的用户
```

当 `age` 的参数值超多 2 个时，`Between` 运算符只使用前两个参数值，会忽略其它参数值。

::: tip
Bean Searcher 判断参数值的顺序是根据 `age-{n}` 里面的序号 `n` 判断的，与代码的顺序无关。
:::