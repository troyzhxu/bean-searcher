# 检索示例

> 如果您还没有阅读 [介绍 > 为什么用](/guide/v3/introduction.html#为什么用) 章节，建议先阅读它们。

## 查询方法

为便于下文示例讲解，我们首先需要一个 SearchBean，并假设 Bean Searcher 的可配置项都是默认值。

```java
public class User {         // 对应数据库中的 user 表
    private Long id;        // ID 
    private String name;    // 姓名
    private int age;        // 年零
    private int point;      // 积分
    // Getter and Setter ...
}
```

### 分页查询

不带参数时，默认查询 15 条数据（因为 [默认分页大小为 15 ，可配置](/guide/v3/params.html#可配置项)）:

```java
// 执行一条 SQL，默认查询 15 条数据
List<User> users = searcher.searchList(User.class, null);
```

查询第 2 页，每页 20 条（[page 和 size 的参数名可配置](/guide/v3/params.html#可配置项)）：

```java
Map<String, Object> params = MapUtils.builder()
        .page(1, 20)
        .build();
// 执行 1 条 SQL
List<User> users = searcher.searchList(User.class, params);
```

分页检索时，返回分页信息：

```java
// 执行 2 条 SQL
SearchResult<User> result = searcher.search(User.class, params);
// 用户列表
List<User> users = result.getDataList();
// 数据总条数
Number totalCount = result.getTotalCount();
```

### 全量查询

Sercher 实例的 `searchAll` 方法可检索满足条件的所有数据：

```java
Map<String, Object> params = MapUtils.builder()
        // 构造条件...
        .build();
// 向 params 添加检索条件, 比如按某个字段检索..
// 执行 1 条 SQL，不分页
List<User> result = searcher.searchAll(User.class, params);
```

### 对象查询

Sercher 实例的 `searchFirst` 方法可检索满足条件的第一个数据：

```java
Map<String, Object> params = MapUtils.builder()
        // 构造条件...
        .build();
// 向 params 添加检索条件, 比如按某个字段检索..
// 执行 1 条 SQL，limit 1
User user = searcher.searchFirst(User.class, params);
```

### 统计查询

单字段统计：

```java
Map<String, Object> params = MapUtils.builder()
        // 构造条件...
        .build();
// 执行 1 条 SQL，返回满足条件的用户的总年龄
Number totalAge = searcher.searchSum(User.class, params, "age");
```

多字段统计：

```java
Map<String, Object> params = MapUtils.builder()
        // 构造条件...
        .build();
// 执行 1 条 SQL
Number[] summaries = searcher.searchSum(User.class, params, new String[] { "age", "point" });
Number totalAge = summaries[0];                 // 满足条件的用户总年龄
Number totalPoint = summaries[1];               // 满足条件的用户总积分
```

分页列表检索时顺带统计：

```java
Map<String, Object> params = MapUtils.builder()
        // 构造条件...
        .build();
// 执行 2 条 SQL
SearchResult<User> result = searcher.search(User.class, params, new String[] { "age" });
List<User> users = result.getDataList();        // 用户列表
Number[] summaries = result.getSummaries();     // 统计信息
```

## 检索方式

不同的检索方式，只是调用 Sercher 实例的检索方法时传递的第二个 `Map<String, Object>` 类型的参数不同而已，所以为了简化文档，下文示例代码中只列出检索参数的组装过程，不再赘述 Sercher 实例的方法的调用。

参考：[参数 > 字段参数 > 字段运算符](/guide/v3/params.html#字段运算符) 章节。

### 精确查询

以例说明，查询 `age` 等于 18 的用户：

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getAge, 18).op(Equal.class)
        .build();
// ...
```

### 范围查询

以例说明，查询 `age` 大于 18 的用户：

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getAge, 18).op(GreaterThan.class)      // age >  18
        .field(User::getAge, 18).op(GreaterEqual.class)     // age >= 18
        .field(User::getAge, 18).op(LessThan.class)         // age <  18
        .field(User::getAge, 18).op(LessEqual.class)        // age <= 18
        .field(User::getAge, 18).op(NotEqual.class)         // age != 18
        .field(User::getAge, 10, 20).op(Between.class)      // 10 <= age <= 20
        .build();  
// ...
```

### 模糊查询

以例说明，查询 `name` 包含字符串 `'A'` 的用户：

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getName, "A").op(Contain.class)    // 包含 A
        .field(User::getName, "A").op(StartWith.class)  // 以 A 开头
        .field(User::getName, "A").op(EndWith.class)    // 以 A 结尾
        .field(User::getName, "A%", "%B", "%C%").op(OrLike.class)  // 以 A 开头 或 以 B 结尾 或 包含 C
        .build();
```

### 多值查询

以例说明，查询 `id` 等于 1 和 2 和 3 的用户：

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getId, 1, 2, 3).op(InList.class)
        .build();
// ...
```

### 非空查询

以例说明，查询 `name` 不为空的用户：

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getName).op(NotEmpty.class)
        .build();
// ...
```

## 动态检索

参考：[参数 > 内嵌参数](/guide/v3/params.html#内嵌参数) 章节。

### 分表检索

某订单系统的数据量非常巨大，如果把所有数据都放在一张表内，查询时经常会产生一些慢 SQL，检索时间长用户体验非常差。为了缓解这个问题，我们可以按年度把订单数据放在不同的表内，例如：

* 表 `order_2018` 存放 2018 年的订单
* 表 `order_2019` 存放 2019 年的订单
* 表 `order_2020` 存放 2020 年的订单
* 表 `order_2021` 存放 2021 年的订单

此时，我们的 SearchBean 可以这样定义：

```java
@SearchBean(
    tables = ":table: o, user u",      // 参数 table 由检索时动态指定
    where = "o.user_id = u.id", autoMapTo = "o"
)
public class Order {
    private Long id;
    private String orderNo;
    @DbField("u.username")
    private String username;
    // 省略其它 ...
}
```

然后，后台订单管理系统加载数据时，同样只需几行代码便可轻松搞定：

```java
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private BeanSearcher beanSearcher;

    /**
     * 订单检索接口
     * @param year 查询的订单年份，如：2018，2019
     **/
    @GetMapping("/index")
    public SearchResult<Order> index(HttpServletRequest request, int year) {
        Map<String, Object> params = MapUtils.flatBuilder(request.getParameterMap())
                .put("table", "order_" + year)              // 根据年份指定查询订单的表名
                .build();
        return beanSearcher.search(Order.class, params);    // 开始检索，并返回数据
    }
	
}
```

### 动态字段

参见 [实体类 > 嵌入参数 > 嵌入到 @DbField](/guide/v3/bean.html#嵌入到-dbfield) 章节，字段参数 `field` 同样在检索参数中指定即可。
