# Bean Searcher

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Troy.Zhou](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-ejlchina-orange.svg)](https://github.com/ejlchina)

English | [中文](./README.zh-CN.md)

* Document：https://searcher.ejlchina.com/

* Getting start：https://juejin.cn/post/7027733039299952676

* Change log：[CHANGELOG](./CHANGELOG.md)

* Only one line of code to achieve:
  - Retrieving with multi tables joined
  - Pagination by any field
  - Combined filtering by any field 
  - Sorting by any field 
  - Summaries with multi field

* Architecture:

![](./assets/architecture.jpg)

### ✨ Features

* Support **one entity mapping to multi tables**
* Support **dynamic field operator**
* Support **group aggregation query**
* Support **Select | Where | From subquery**
* Support **embedded params in entity**
* Support **field converter**
* Support **sql interceptor**
* Support **sql dialect extension**
* Support **Multi data source and dynamic data source**
* Support **annotations omitting and customizing**
* and so on

### ⁉️WHY

#### This is not a repeating wheel

Although CREATE/UPDATE/DELETE are the strengths of Hibernate, MyBatis, DataJDBC and other ORM, queries, especially complex list queries with **multi conditions**, **multi tables**, **paging**, **sorting**, have always been their weaknesses.

Traditional ORM is difficult to realize a complex list retrieval with less code, but **Bean Searcher** has made great efforts in this regard. These complex queries can be solved in almost one line of code.

* For example, such a typical requirement：

![](./assets/case.png)

The back-end needs to write a retrieval api, and if it is written with traditional ORM, the complexity of the code is very high

But Bean Searcher can：

### 💥 Achieved with one line of code

Whether simple or complex of requirements, Bean Searcher requires only one line of code

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

* **Retrieving with multi tables joined**
* **Pagination by any field**
* **Combined filtering by any field**
* **Sorting by any field**
* **Summary with `age` field**

For example, this api can be accessed like:

* `/user/index`
  - Retrieving by default pagination:
  ```json
  {
    "dataList": [
      {
        "id": 1,
        "username": "Jack",
        "status": 1,
        "level": 1,
        "age": 25,
        "gender": "Male",
        "joinDate": "2021-10-01"
      },
      ...     // 15 records default
    ],
    "totalCount": 100,
    "summaries": [
      2500    // age statistics
    ]
  }
  ```
* `/user/index? page=1 & size=10`
  - Retrieving by specified pagination
* `/user/index? status=1`
  - Retrieving with `status = 1` by default pagination
* `/user/index? name=Jac & name-op=sw`
  - Retrieving with `name` starting with `Jac` by default pagination
* `/user/index? name=Jack & name-ic=true`
  - Retrieving with `name = Jack`(case ignored) by default pagination
* `/user/index? sort=age & order=desc`
  - Retrieving sorting by `age` descending and by default pagination
* `/user/index? onlySelect=username,age`
  - Retrieving `username,age` only by default pagination:
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
* `/user/index? selectExclude=joinDate`
  - Retrieving `joinDate` excluded default pagination

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

* [v3.x - demos](./bean-searcher-demos)
* [v2.x - demo](https://gitee.com/ejlchina-zhxu/bean-searcher-demo)

### 🚀 Rapid development

使用 Bean Searcher 可以极大地节省后端的复杂列表检索接口的开发时间！

* 普通的复杂列表查询只需一行代码
* 单表检索可复用原有 `Domain`，无需定义 `SearchBean`

### 🌱 Easy integration

可以和任意 Java Web 框架集成，如：SpringBoot、Spring MVC、Grails、Jfinal 等等。

#### Spring Boot 项目，添加依赖即集成完毕：

```groovy
implementation 'com.ejlchina:bean-searcher-boot-stater:3.1.2'
```

接着便可在 `Controller` 或 `Service` 里注入检索器：

```groovy
/**
 * 注入 Map 检索器，它检索出来的数据以 Map 对象呈现
 */
@Autowired
private MapSearcher mapSearcher;

/**
 * 注入 Bean 检索器，它检索出来的数据以 泛型 对象呈现
 */
@Autowired
private BeanSearcher beanSearcher;
```

#### 其它框架，使用如下依赖：

```groovy
implementation 'com.ejlchina:bean-searcher:3.1.2'
```

然后可以使用 `SearcherBuilder` 构建一个检索器：

```java
DataSource dataSource = ...     // 拿到应用的数据源

// DefaultSqlExecutor 也支持多数据源
SqlExecutor sqlExecutor = new DefaultSqlExecutor(dataSource);

// 构建 Map 检索器
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .sqlExecutor(sqlExecutor)
        .build();

// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        .sqlExecutor(sqlExecutor)
        .build();
```

### 🔨 Easy extended

面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件！

比如你可以：
* 自定义数据库映射（[`DbMapping`](/bean-searcher/src/main/java/com/ejlchina/searcher/DbMapping.java)）来实现自定义注解，或让 Bean Searcher 识别其它 ORM 的注解
* 自定义参数解析器（[`ParamResolver`](/bean-searcher/src/main/java/com/ejlchina/searcher/ParamResolver.java)）来支持 JSON 形式的检索参数
* 自定义字段转换器（[`FieldConvertor`](/bean-searcher/src/main/java/com/ejlchina/searcher/FieldConvertor.java)）来支持任意的 字段类型
* 自定义数据库方言（[`Dialect`](/bean-searcher/src/main/java/com/ejlchina/searcher/Dialect.java)）来支持更多的数据库
* 等等..

### 📚 Detailed documentation

Reference ：https://searcher.ejlchina.com/

### 🤝 Friendship links

[**[ Sa-Token ]** 一个轻量级 Java 权限认证框架，让鉴权变得简单、优雅！](https://github.com/dromara/Sa-Token)

[**[ OkHttps ]** 轻量却强大的 HTTP 客户端，前后端通用，支持 WebSocket 与 Stomp 协议](https://gitee.com/ejlchina-zhxu/okhttps)

[**[ hrun4j ]** 接口自动化测试解决方案 --工具选得好，下班回家早；测试用得对，半夜安心睡 ](https://github.com/lematechvip/hrun4j)

[**[ JsonKit ]** 超轻量级 JSON 门面工具，用法简单，不依赖具体实现，让业务代码与 Jackson、Gson、Fastjson 等解耦！](https://gitee.com/ejlchina-zhxu/jsonkit)

[**[ Free UI ]** 基于 Vue3 + TypeScript，一个非常轻量炫酷的 UI 组件库 ！](https://gitee.com/phoeon/free-ui)


### ❤️ How to contribute

1. Fork code!
2. Create your own branch: `git checkout -b feat/xxxx`
3. Submit your changes: `git commit -am 'feat(function): add xxxxx'`
4. Push your branch: `git push origin feat/xxxx`
5. submit `pull request`
