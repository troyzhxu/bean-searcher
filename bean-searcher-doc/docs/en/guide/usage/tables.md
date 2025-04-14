# 大表滚动

## 需求场景

某张表的数据量非常巨大（例如：订单表、日志表、事件表），需要对表进行（按年/按月）滚动，例如：

* 表 `order_2018` 存放 2018 年的订单
* 表 `order_2019` 存放 2019 年的订单
* 表 `order_2020` 存放 2020 年的订单
* 表 `order_2021` 存放 2021 年的订单

#### 需求目的

* 便于删除旧数据，释放空间
* 便于旧数据归档

## 实现方案

> 具体如何实现表名滚动，不是本文讨论的重点。这里只说大表滚动后业务端还如何统一查询历史数据。

此时，我们的 SearchBean 可以这样定义（使用 [拼接参数](/en/guide/param/embed#%E6%8B%BC%E6%8E%A5%E5%8F%82%E6%95%B0)）：

```java
@SearchBean(
    tables = "order_:year: o, user u",      // 参数 year 由检索时动态指定
    where = "o.user_id = u.id",
    autoMapTo = "o"
)
public class Order {

    public static final String TABLE_SUFFIX = "year";

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
                .put(Order.TABLE_SUFFIX, year)              // 指定表名后缀
                .build();
        return beanSearcher.search(Order.class, params);    // 检索并返回数据
    }
	
}
```
