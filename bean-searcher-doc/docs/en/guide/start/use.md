# Usage

If you're not sure where Bean Searcher is suitable for use, please first read the [Introduction > Bean Searcher](/en/guide/info/bean-searcher) section.

## Searcher

After successfully integrating it into your project, you can then obtain the searcher instance in your business code (Controller or Service).

In Spring or Grails projects, you can directly inject it (in Grails projects, you don't need to use the `@Autowired` annotation):

```java
/**
 * Inject the Map searcher, which retrieves data presented as Map objects.
 */
@Autowired
private MapSearcher mapSearcher;
/**
 * Inject the Bean searcher, which retrieves data presented as generic objects.
 */
@Autowired
private BeanSearcher beanSearcher;
```

For other projects, you can directly pass in the searcher constructed during project startup or use your own injection method.

::: warning Note
If you have both `MapSearcher` and `BeanSearcher` instances in your Spring container (when using the `bean-searcher-boot-starter` dependency, two searcher instances are added to the Spring container by default), you cannot use the following injection method:
```java
@Autowired
private Searcher Searcher;
```
Because both `MapSearcher` and `BeanSearcher` are instances of `Searcher`, the Spring container can't tell which one you want.

When using the `bean-searcher-boot-starter` version `v3.0.5` or `v3.1.3+`, this issue doesn't exist, and it will inject the `MapSearcher` instance by default.
:::

After obtaining the searcher, let's take a look at the methods provided by both the `MapSearcher` and `BeanSearcher` searchers:

### Common methods

* `searchCount(Class<T> beanClass, Map<String, Object> params): Number` Query the **total number** of data under the specified conditions.
* `searchSum(Class<T> beanClass, Map<String, Object> params, String field): Number` Query the **statistical value** of a **specific field** under the specified conditions.
* `searchSum(Class<T> beanClass, Map<String, Object> params, String[] fields): Number[]` Query the **statistical values** of **multiple fields** under the specified conditions.

> The retrieval parameter `params` is a set of conditions, including field filtering, pagination, sorting, and other parameters.

### Methods only available in MapSearcher

* `search(Class<T> beanClass, Map<String, Object> params): SearchResult<Map<String, Object>>` **Paginated** query of the data **list** and **total number** under the specified conditions.
* `search(Class<T> beanClass, Map<String, Object> params, String[] summaryFields): SearchResult<Map<String, Object>>` **Same as above** + **statistics** of multiple fields.
* `searchFirst(Class<T> beanClass, Map<String, Object> params): Map<String, Object>` Query the **first** piece of data under the specified conditions.
* `searchList(Class<T> beanClass, Map<String, Object> params): List<Map<String, Object>>` **Paginated** query of the data **list** under the specified conditions.
* `searchAll(Class<T> beanClass, Map<String, Object> params): List<Map<String, Object>>` Query the **entire** data **list** under the specified conditions.

> The single pieces of data retrieved by the above methods are all presented as `Map` objects.

### Methods only available in BeanSearcher

* `search(Class<T> beanClass, Map<String, Object> params): SearchResult<T>` **Paginated** query of the data **list** and **total number** under the specified conditions.
* `search(Class<T> beanClass, Map<String, Object> params, String[] summaryFields): SearchResult<T>` **Same as above** + **statistics** of multiple fields.
* `searchFirst(Class<T> beanClass, Map<String, Object> params): T` Query the **first** piece of data under the specified conditions.
* `searchList(Class<T> beanClass, Map<String, Object> params): List<T>` **Paginated** query of the data **list** under the specified conditions.
* `searchAll(Class<T> beanClass, Map<String, Object> params): List<T>` Query the **entire** data **list** under the specified conditions.

> The single pieces of data retrieved by the above methods are all presented as generic `T` objects.

For the complete interface definitions, please refer to: [Searcher](https://gitee.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/Searcher.java), [MapSearcher](https://gitee.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/MapSearcher.java), and [BeanSearcher](https://gitee.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/BeanSearcher.java).

## Search Entity Classes

In the world of Bean Searcher, entity classes that have a mapping relationship with the database are called SearchBeans (a SearchBean can map to one table or multiple tables). For example, you may already have an entity class like this in your project:

```java
public class User {             // By default, it maps to the user table.

    private Long id;            // By default, it maps to the id field.
    private String name;        // By default, it maps to the name field.
    private int age;            // By default, it maps to the age field.

    // Getter and Setter ...
}
```

Compared to v2.x, the entity classes in Bean Searcher v3.x can omit annotations and can also be configured to recognize annotations from other frameworks.

In the absence of annotations, Bean Searcher considers it a single-table entity class (i.e., it only maps to one table in the database. For an example of a multi-table entity class, please refer to the [Entity Classes > Multi-table Association](/en/guide/bean/multitable) section).

## Start Searching

After having the entity class, let's use the `search(Class<T> beanClass, Map<String, Object> params): SearchResult<Map<String, Object>>` method of `MapSearcher` to experience how to implement a search interface with **just one line of code**. The code is as follows:

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MapSearcher mapSearcher;              // Inject the BeanSearcher searcher.

    @GetMapping("/index")
    public SearchResult<Map<String, Object>> index(HttpServletRequest request) {
        // One line of code to implement a user search interface (MapUtils.flat just collects the request parameters from the front end).
        return mapSearcher.search(User.class, MapUtils.flat(request.getParameterMap()));
    }

}
```

::: tip Code Explanation
The `MapUtils` mentioned above is a utility class provided by Bean Searcher. `MapUtils.flat(request.getParameterMap())` is just used to collect all the request parameters sent from the front end. Then, the rest is all handed over to the `MapSearcher` searcher. <br>
Of course, you don't have to get the parameters directly from the `request`. It's just that the code looks more concise this way.
:::

You can also configure [Automatic Request Parameter Reception](/en/guide/usage/others#Automatic Request Parameter Reception), and then your code can be further simplified:

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MapSearcher mapSearcher;              // Inject the BeanSearcher searcher.

    @GetMapping("/index")
    public SearchResult<Map<String, Object>> index() {
        // One line of code to implement a user search interface.
        return mapSearcher.search(User.class);
    }

}
```

The above code implements a `/user/index` interface. There's really only one line of code in its method body. But what request parameters can this interface support? And what results can different request parameters produce? Let's briefly list them below:

### (1) Request without parameters

* GET /user/index
* Return result:

```json
{
    "dataList": [           // User list, by default, it returns the first page, and the default page size is 15 (configurable).
        { "id": 1, "name": "Jack", "age": 25 },,,
    ],
    "totalCount": 100       // Total number of users.
}
```

### (2) Pagination request (page | size)

* GET /user/index? page=2 & size=10 
* Return result: The structure is the same as **(1)** (but with 10 items per page, returning the second page).

::: tip
The parameter names `size` and `page` can be customized. By default, `page` starts from `0`, which can also be customized and can be combined with other parameters.
:::

### (3) Data sorting (sort | order)

* GET /user/index? sort=age & order=desc 
* Return result: The structure is the same as **(1)** (but the dataList is sorted in descending order by the age field).

::: tip
The parameter names `sort` and `order` can be customized and can be combined with other parameters.
:::

### (4) Specify (exclude) fields (onlySelect | selectExclude)

* GET /user/index? onlySelect=id,name
* GET /user/index? selectExclude=age
* Return result: (The dataList only contains the id and name fields)

```json
{
    "dataList": [           // User list, by default, it returns the first page (only containing the id and name fields).
        { "id": 1, "name": "Jack" },,,
    ],
    "totalCount": 100       // Total number of users.
}
```

::: tip
The parameter names `onlySelect` and `selectExclude` can be customized and can be combined with other parameters.
:::

### (5) Field filtering ([field]-op=eq)

* GET /user/index? age=20
* GET /user/index? age=20 & age-op=eq
* Return result: The structure is the same as **(1)** (but only returns data where age=20).

::: tip
The parameter `age-op=eq` means that the **field operator** of `age` is `eq` (an abbreviation for `Equal`), indicating that the relationship between the parameter `age` and the parameter value `20` is `Equal`. Since `Equal` is the default relationship, `age-op=eq` can also be omitted.

The suffix `-op` of the parameter name `age-op` can be customized and can be combined with other field parameters and the parameters listed above (pagination, sorting, specifying fields). The same applies to the field parameters listed below, and it won't be repeated.
:::

### (6) Field filtering ([field]-op=ne)

* GET /user/index? age=20 & age-op=ne
* Return result: The structure is the same as **(1)** (but only returns data where age != 20, `ne` is an abbreviation for `NotEqual`).

### (7) Field filtering ([field]-op=ge)

* GET /user/index? age=20 & age-op=ge
* Return result: The structure is the same as **(1)** (but only returns data where age >= 20, `ge` is an abbreviation for `GreateEqual`).

### (8) Field filtering ([field]-op=le)

* GET /user/index? age=20 & age-op=le
* Return result: The structure is the same as **(1)** (but only returns data where age <= 20, `le` is an abbreviation for `LessEqual`).

### (9) Field filtering ([field]-op=gt)

* GET /user/index? age=20 & age-op=gt
* Return result: The structure is the same as **(1)** (but only returns data where age > 20, `gt` is an abbreviation for `GreateThan`).

### (10) Field Filtering ([field]-op=lt)

* GET /user/index? age=20 & age-op=lt
* Return result: The structure is the same as **(1)** (but only return data where age < 20. `lt` is the abbreviation of `LessThan`).

### (11) Field Filtering ([field]-op=bt)

* GET /user/index? age-0=20 & age-1=30 & age-op=bt
* Return result: The structure is the same as **(1)** (but only return data where 20 <= age <= 30. `bt` is the abbreviation of `Between`).

::: tip
The parameter `age-0=20` means that the 0th parameter value of `age` is `20`. The `age=20` mentioned above is actually a shorthand for `age-0=20`.
The hyphen `-` in the parameter names `age-0` and `age-1` can be customized.

**Optimization**: Think that `age-0=20 & age-1=30` is a bit complicated and want to use **age=[20,30]** instead? It's okay! Please refer to the [Advanced > Parameter Filter](/en/guide/advance/filter) section.
:::

### (12) Field Filtering ([field]-op=il)

* GET /user/index? age-0=20 & age-1=30 & age-2=40 & age-op=il
* Return result: The structure is the same as **(1)** (but only return data where age in (20, 30, 40). `il` is the abbreviation of `InList`).

::: tip Optimization
Similarly, `age-0=20 & age-1=30 & age-2=40` can be optimized to **age=[20,30,40]**. Refer to the [Advanced > Parameter Filter](/en/guide/advance/filter) section.
:::

### (13) Field Filtering ([field]-op=ct)

* GET /user/index? name=Jack & name-op=ct
* Return result: The structure is the same as **(1)** (but only return data where the name contains Jack. `ct` is the abbreviation of `Contain`).

### (14) Field Filtering ([field]-op=sw)

* GET /user/index? name=Jack & name-op=sw
* Return result: The structure is the same as **(1)** (but only return data where the name starts with Jack. `sw` is the abbreviation of `StartWith`).

### (15) Field Filtering ([field]-op=ew)

* GET /user/index? name=Jack & name-op=ew
* Return result: The structure is the same as **(1)** (but only return data where the name ends with Jack. `ew` is the abbreviation of `EndWith`).

### (16) Field Filtering ([field]-op=ey)

* GET /user/index? name-op=ey
* Return result: The structure is the same as **(1)** (but only return data where the name is empty or null. `ey` is the abbreviation of `Empty`).

### (17) Field Filtering ([field]-op=ny)

* GET /user/index? name-op=ny
* Return result: The structure is the same as **(1)** (but only return data where the name is not empty. `ny` is the abbreviation of `NotEmpty`).

### (18) Ignore Case ([field]-ic=true)

* GET /user/index? name=Jack & name-ic=true
* Return result: The structure is the same as **(1)** (but only return data where the name is equal to Jack (ignoring case). `ic` is the abbreviation of `IgnoreCase`).

::: tip
The suffix `-ic` in the parameter name `name-ic` can be customized. This parameter can be combined with other parameters. For example, here the search is for the name equal to Jack while ignoring case, but it also applies to the search when the name starts or ends with Jack while ignoring case.
:::

Bean Searcher also supports **more** retrieval methods (and even custom ones. Refer to the [Parameters > Field Parameters > Field Operators](/en/guide/param/field#Field Operators) section). They are not listed here.

In the `/user/index` interface of this example, we only wrote one line of code, and it can support so many retrieval methods. Do you now understand the meaning of "one line of code can implement complex list retrieval"? Do you feel that one line of code you write now can do the work of a hundred lines of others?

::: tip
This example is a simple single-table query. In fact, whether it is a single table or multiple tables, as long as they are mapped to the same entity class, all the retrieval methods listed above can be supported.

As for how to map multiple database tables to the same entity class, please refer to the next section: [Entity Class](/en/guide/bean/info).
:::

## SQL Log

If you need to view the SQL execution log of Bean Searcher, you only need to adjust the log level of `cn.zhxu.bs.implement.DefaultSqlExecutor` to `DEBUG` in your log configuration file.

> If the log level is configured to `INFO` or `WARN`, only slow SQL logs will be printed. If it is configured to `DEBUG`, both slow SQL and ordinary SQL logs will be printed.

* Example 1: SpringBoot project `application.yml`

```yml
logging:
  level:
    cn.zhxu.bs: DEBUG
```

* Example 2: SpringBoot project `application.properties`

```yml
logging.level.cn.zhxu.bs: DEBUG
```

* Example 3: The log configuration of the SpringBoot project can refer to [logback-spring.xml](https://gitee.com/troyzhxu/bean-searcher/blob/master/bean-searcher-demos/bs-demo-springboot/src/main/resources/logback-spring.xml).

* Example 4: The log configuration of the Grails project can refer to [logback.groovy](https://gitee.com/troyzhxu/bean-searcher/blob/master/bean-searcher-demos/grails-demo/grails-app/conf/logback.groovy).

After the output level is configured, the effect of the SQL log is as follows:

![](/sql_log.png)

> Bean Searcher uses the logging facade `slf4j-api` to print logs (this is also the only dependency of Bean Searcher). It does not depend on `logback`, so other logging frameworks such as `log4j` are also supported.

For the configuration related to slow SQL, please refer to the [Advanced > Slow SQL Log and Monitoring](/en/guide/advance/slowsql) section.
