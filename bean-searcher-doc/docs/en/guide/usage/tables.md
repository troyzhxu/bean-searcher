# Large Table Rolling

## Requirement Scenario

The data volume of a certain table is extremely large (e.g., order table, log table, event table). The table needs to be rolled (by year/month). For example:

* Table `order_2018` stores orders from 2018.
* Table `order_2019` stores orders from 2019.
* Table `order_2020` stores orders from 2020.
* Table `order_2021` stores orders from 2021.

#### Requirement Objectives

* Facilitate the deletion of old data to free up space.
* Facilitate the archiving of old data.

## Implementation Solution

> How to specifically implement table name rolling is not the focus of this article. Here, we only discuss how the business side can still perform unified queries on historical data after large table rolling.

At this time, our SearchBean can be defined as follows (using [Concatenating Parameters](/en/guide/param/embed#Concatenating_Parameters)):

```java
@SearchBean(
    tables = "order_:year: o, user u",      // The parameter 'year' is dynamically specified during retrieval.
    where = "o.user_id = u.id",
    autoMapTo = "o"
)
public class Order {

    public static final String TABLE_SUFFIX = "year";

    private Long id;
    private String orderNo;
    @DbField("u.username")
    private String username;
    // Other fields are omitted...
}
```

Then, when the background order management system loads data, it can be easily accomplished with just a few lines of code:

```java
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private BeanSearcher beanSearcher;

    /**
     * Order retrieval interface
     * @param year The year of the orders to be queried, e.g., 2018, 2019
     **/
    @GetMapping("/index")
    public SearchResult<Order> index(HttpServletRequest request, int year) {
        Map<String, Object> params = MapUtils.flatBuilder(request.getParameterMap())
                .put(Order.TABLE_SUFFIX, year)              // Specify the table name suffix
                .build();
        return beanSearcher.search(Order.class, params);    // Retrieve and return data
    }
	
}
```
