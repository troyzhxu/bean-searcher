<p align="center">
  <a href="https://bs.zhxu.cn/" target="_blank">
    <img width="128" src="./assets/logo.png" alt="logo">
  </a>
</p>
<p align="center">
  <a href="https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/"><img src="https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg" alt="Maven Central"></a>
  <a href="https://gitee.com/troyzhxu/bean-searcher/blob/master/LICENSE"><img src="https://img.shields.io/hexpm/l/plug.svg" alt="License"></a>
  <a href="https://github.com/troyzhxu"><img src="https://img.shields.io/badge/%E4%BD%9C%E8%80%85-troyzhxu-orange.svg" alt="Troy.Zhou"></a>
</p>

English | [中文](./README.zh-CN.md)

* Documentation：https://bs.zhxu.cn
* 🚀 **Online Demo**：https://demo-bs.zhxu.cn/
* JueJin blogs：
  - [Writing code like this is 100 times more efficient than using MyBatis directly!](https://juejin.cn/post/7027733039299952676)
  - [What's the difference between Bean Searcher and MyBatis Plus?](https://juejin.cn/post/7092411551507808264)
  
* Only one line of code to achieve:
  - Retrieval from multi tables
  - Pagination by any field
  - Combined filter by any field 
  - Sorting by any field 
  - Summaries with multi field
  - Return VO directly

* Design thinking: [Bean Searcher's thinking](https://bs.zhxu.cn/guide/latest/introduction.html#%E8%AE%BE%E8%AE%A1%E5%93%B2%E5%AD%A6)

* Architecture:

![](./assets/architecture.jpg)

* Change log：[CHANGELOG](./CHANGELOG.md)
* Performance：[see the report](./performance/README.md)
* Gitee 企业版：https://gitee.com/enterprises?invite_code=Z2l0ZWUtMTM5MzQxMg%3D%3D

### ✨ Features

* Support **one entity mapping to multi tables**
* Support **dynamic field operator**
* Support **group and aggregation query**
* Support **Select | Where | From subquery**
* Support **embedded params in entity**
* Support **field converters**
* Support **sql interceptors**
* Support **sql dialect extension**
* Support **multi datasource and dynamic datasource**
* Support **annotation omitting and customizing**
* Support **field operator extension**
* and so on

### ⁉️WHY

#### This is not a repeating wheel

Although CREATE/UPDATE/DELETE are the strengths of Hibernate, MyBatis, DataJDBC and other ORM, queries, especially complex list queries with **multi conditions**, **multi tables**, **paging**, **sorting**, have always been their weaknesses.

Traditional ORM is difficult to realize a complex list retrieval with less code, but **Bean Searcher** has made great efforts in this regard. These complex queries can be solved in almost one line of code.

* For example, such a typical requirement：

![](./assets/case.png)

The back-end needs to write a retrieval API, and if it is written with traditional ORM, the complexity of the code is very high

But Bean Searcher can：

### 💥 Achieved with one line of code

First, you have an Entity class:

```java
@SearchBean(tables="user u, role r", joinCond="u.role_id = r.id", autoMapTo="u")
public class User {
  private long id;
  private String username;
  private int status;
  private int age;
  private String gender;
  private Date joinDate;
  private int roleId;
  @DbField("r.name")
  private String roleName;
  // Getters and setters...
}
```

Then you can complete the API with one line of code :

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanSearcher beanSearcher;              // Inject BeanSearcher

    @GetMapping("/index")
    public SearchResult<User> index(HttpServletRequest request) {
        // Only one line of code written here
        return beanSearcher.search(User.class, MapUtils.flat(request.getParameterMap()), new String[]{ "age" });
    }

}
```

This line of code can achieve：

* **Retrieval from multi tables**
* **Pagination by any field**
* **Combined filter by any field**
* **Sorting by any field**
* **Summary with `age` field**

For example, this API can be requested as follows:

* `GET: /user/index`
  
  Retrieving by default pagination:
  ```json
  {
    "dataList": [
      {
        "id": 1,
        "username": "Jack",
        "status": 1,
        "age": 25,
        "gender": "Male",
        "joinDate": "2021-10-01",
        "roleId": 1,
        "roleName": "User"
      },
      ...     // 15 records default
    ],
    "totalCount": 100,
    "summaries": [
      2500    // age statistics
    ]
  }
  ```
  
* `GET: /user/index? page=1 & size=10`
  
  Retrieval by specified pagination

* `GET: /user/index? status=1`
  
  Retrieval with `status = 1` by default pagination

* `GET: /user/index? name=Jac & name-op=sw`
  
  Retrieval with `name` starting with `Jac` by default pagination

* `GET: /user/index? name=Jack & name-ic=true`
  
  Retrieval with `name = Jack`(case ignored) by default pagination

* `GET: /user/index? sort=age & order=desc`
   
  Retrieval sorting by `age` descending and by default pagination

* `GET: /user/index? onlySelect=username,age`

  Retrieval `username,age` only by default pagination:
  ```json
  {
    "dataList": [
      {
        "username": "Jack",
        "age": 25,
      },
      ...     // 15 records default
    ],
    "totalCount": 100,
    "summaries": [
      2500    // age statistics
    ]
  }
  ```
* `GET: /user/index? selectExclude=joinDate`

  Retrieving `joinDate` excluded default pagination

### ✨ Parameter builder

```java
Map<String, Object> params = MapUtils.builder()
        .selectExclude(User::getJoinDate)                 // Exclude joinDate field
        .field(User::getStatus, 1)                        // Filter：status = 1
        .field(User::getName, "Jack").ic()                // Filter：name = 'Jack' (case ignored)
        .field(User::getAge, 20, 30).op(Opetator.Between) // Filter：age between 20 and 30
        .orderBy(User::getAge, "asc")                     // Sorting by age ascending 
        .page(0, 15)                                      // Pagination: page=0 and size=15
        .build();
List<User> users = beanSearcher.searchList(User.class, params);
```

**Demos**：

* [v4.x - demos](./bean-searcher-demos)
* [v3.x - demos](https://gitee.com/troyzhxu/bean-searcher/tree/v3.8/bean-searcher-demos)

### 🚀 Rapid development

Using Bean Searcher can greatly save the development time of the complex list retrieval apis!

* An ordinary complex list query requires only one line of code
* Retrieval from single table can reuse the original `domain`, without defining new `Entity`

### 🌱 Easy integration

Bean Searcher can work with any JavaWeb frameworks, such as: SpringBoot, SpringMVC, Grails, Jfinal and so on.

#### SpringBoot / Grails

All you need is to add a dependence:

```groovy
implementation "cn.zhxu:bean-searcher-boot-stater:${latestVersion}"
```

and then you can inject Searcher into a `Controller` or `Service`:

```groovy
/**
 * Inject a MapSearcher, which retrieved data is Map objects
 */
@Autowired
private MapSearcher mapSearcher;

/**
 * Inject a BeanSearcher, which retrieved data is generic objects
 */
@Autowired
private BeanSearcher beanSearcher;
```

#### Solon Project

All you need is to add a dependence:

```groovy
implementation "cn.zhxu:bean-searcher-solon-plugin:${latestVersion}"
```

and then you can inject Searcher into a `Controller` or `Service`:

```groovy
/**
 * Inject a MapSearcher, which retrieved data is Map objects
 */
@Inject
private MapSearcher mapSearcher;

/**
 * Inject a BeanSearcher, which retrieved data is generic objects
 */
@Inject
private BeanSearcher beanSearcher;
```

#### Other frameworks

Adding this dependence:

```groovy
implementation "cn.zhxu:bean-searcher:${latestVersion}"
```

then you can build a `Searcher` with `SearcherBuilder`:

```java
DataSource dataSource = ...     // Get the dataSource of the application

// DefaultSqlExecutor suports multi datasources
SqlExecutor sqlExecutor = new DefaultSqlExecutor(dataSource);

// build a MapSearcher
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .sqlExecutor(sqlExecutor)
        .build();

// build a BeanSearcher
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        .sqlExecutor(sqlExecutor)
        .build();
```

### 🔨 Easy extended

You can customize and extend any component in Bean Searcher .

For example:
* Customizing [`FieldOp`](/bean-searcher/src/main/java/cn/zhxu/bs/FieldOp.java) to support other field operator
* Customizing [`DbMapping`](/bean-searcher/src/main/java/cn/zhxu/bs/DbMapping.java) to support other ORM‘s annotations
* Customizing [`ParamResolver`](/bean-searcher/src/main/java/cn/zhxu/bs/ParamResolver.java) to support JSON query params
* Customizing [`FieldConvertor`](/bean-searcher/src/main/java/cn/zhxu/bs/FieldConvertor.java) to support any type of field
* Customizing [`Dialect`](/bean-searcher/src/main/java/cn/zhxu/bs/dialect/Dialect.java) to support more database
* and so and

### 📚 Detailed documentation

Reference ：https://bs.zhxu.cn

### 🤝 Friendship links

[**[ Sa-Token ]** 一个轻量级 Java 权限认证框架，让鉴权变得简单、优雅！](https://github.com/dromara/Sa-Token)

[**[ Fluent MyBatis ]** MyBatis 语法增强框架, 综合了 MyBatisPlus, DynamicSql,Jpa 等框架的特性和优点，利用注解处理器生成代码](https://gitee.com/fluent-mybatis/fluent-mybatis)

[**[ OkHttps ]** 轻量却强大的 HTTP 客户端，前后端通用，支持 WebSocket 与 Stomp 协议](https://gitee.com/troyzhxu/okhttps)

[**[ hrun4j ]** 接口自动化测试解决方案 --工具选得好，下班回家早；测试用得对，半夜安心睡 ](https://github.com/lematechvip/hrun4j)

[**[ JsonKit ]** 超轻量级 JSON 门面工具，用法简单，不依赖具体实现，让业务代码与 Jackson、Gson、Fastjson 等解耦！](https://gitee.com/troyzhxu/xjsonkit)

[**[ Free UI ]** 基于 Vue3 + TypeScript，一个非常轻量炫酷的 UI 组件库 ！](https://gitee.com/phoeon/free-ui)


### ❤️ How to contribute

1. Fork code!
2. Create your own branch: `git checkout -b feat/xxxx`
3. Submit your changes: `git commit -am 'feat(function): add xxxxx'`
4. Push your branch: `git push origin feat/xxxx`
5. submit `pull request`
