<p align="center">
  <a href="https://bs.zhxu.cn/" target="_blank">
    <img width="128" src="./assets/logo.png" alt="logo">
  </a>
</p>
<p align="center">
  <a href="https://gitee.com/troyzhxu/bean-searcher/stargazers"><img src="https://gitee.com/troyzhxu/bean-searcher/badge/star.svg?theme=gvp"></a>
  <a href="https://gitee.com/troyzhxu/bean-searcher/members"><img src="https://gitee.com/troyzhxu/bean-searcher/badge/fork.svg?theme=gvp"></a>
  <a href="https://github.com/troyzhxu/bean-searcher/stargazers"><img src="https://img.shields.io/github/stars/troyzhxu/bean-searcher?style=flat-square&logo=GitHub"></a>
  <a href="https://github.com/troyzhxu/bean-searcher/network/members"><img src="https://img.shields.io/github/forks/troyzhxu/bean-searcher?style=flat-square&logo=GitHub"></a>
  <a href="https://gitee.com/troyzhxu/bean-searcher/blob/master/LICENSE"><img src="https://img.shields.io/hexpm/l/plug.svg" alt="License"></a>
</p>

中文 | [English](./README.md)

* 文档：https://bs.zhxu.cn/
* 🚀 **在线 Demo**：https://demo-bs.zhxu.cn/
* 掘金博客：
  - [这样写代码，比直接使用 MyBatis 效率提高了 100 倍！](https://juejin.cn/post/7027733039299952676)
  - [最近火起的 Bean Searcher 与 MyBatis Plus 倒底有啥区别？](https://juejin.cn/post/7092411551507808264)

> **Bean Searcher 是列表检索领域的 GraphQL** — 实体定义检索边界，参数驱动查询逻辑。不改变 HTTP 协议习惯，一个依赖即用。
> 
> 单表实体零注解即可搜，一行代码搞定多表联查、分页、筛选、排序、统计。

* 架构图：

![](./assets/architecture.jpg)

* 更新日志：[CHANGELOG](./CHANGELOG.md)
* 性能：[看报告](./performance/README.md)

### ✨ 特性

* 支持 **实体多表映射**
* 支持 **动态字段运算符**
* 支持 **客户端驱动查询**（前端控制返回字段、筛选条件、排序规则）
* 支持 **分组聚合 查询**
* 支持 **Select | Where | From 子查询**
* 支持 **实体类嵌入参数**
* 支持 **字段转换器**
* 支持 **Sql 拦截器**
* 支持 **数据库 Dialect 扩展**
* 支持 **多数据源 与 动态数据源**
* 支持 **注解缺省 与 自定义**
* 支持 **字段运算符 扩展**
* 等等

### ⁉️ 为什么用

#### 声明式检索 vs 命令式编码

MyBatis / Hibernate 擅长增删改，但面对**多条件、联表、排序、分页**的列表检索时，往往需要大量 if-else 条件拼接、VO 转换代码。

Bean Searcher 用**声明式检索**解决了这个问题：
* **实体即声明** — SearchBean 定义"能搜什么"，无需注解也可搜
* **参数即查询** — 前端传参控制"搜什么"，后端一个接口应对千变万化
* **零协议负担** — 工作在标准 HTTP 参数上，不需要专用协议

就像 GraphQL 让客户端在一次请求中自由指定要什么字段，Bean Searcher 让客户端自由指定筛选、排序、分页、统计 — 无需专用 schema，一行代码搞定。

### 💥 一行代码实现

首先，你有一个实体类：

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

然后你就可以用一行代码实现这个用户检索接口：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanSearcher beanSearcher;              // 注入 Bean Searcher 的检索器

    @GetMapping("/index")
    public SearchResult<User> index(HttpServletRequest request) {
        // 这里只写一行代码
        return beanSearcher.search(User.class, MapUtils.flat(request.getParameterMap()), User::getAge);
    }
	
}
```

这一行代码实现了以下功能：

* **多表联查**
* **分页搜索**
* **组合过滤**
* **任意字段排序**
* **字段统计**

例如，该接口支持如下请求：

* `GET: /user/index`

  无参请求（默认分页）:
  ```json
  {
    "dataList": [
      { "id": 1, "username": "Jack", "status": 1, "age": 25, "gender": "Male", "joinDate": "2021-10-01" },
      ...     // 默认返回 15 条数据
    ],
    "totalCount": 100,
    "summaries": [ 2500 ]    // age 字段统计
  }
  ```

* `GET: /user/index? page=1 & size=10` — 指定分页参数
* `GET: /user/index? status=1` — 返回 `status = 1` 的用户
* `GET: /user/index? name=Jac & name-op=sw` — 返回 `name` 以 `Jac` 开头的用户
* `GET: /user/index? name=Jack & name-ic=true` — 返回 `name = Jack`（忽略大小写）的用户
* `GET: /user/index? sort=age & order=desc` — 按字段 `age` 降序查询
* `GET: /user/index? onlySelect=username,age` — 只检索 `username` 与 `age` 两个字段
* `GET: /user/index? selectExclude=joinDate` — 检索时排除 `joinDate` 字段

### ✨ 参数构建器

```java
Map<String, Object> params = MapUtils.builder()
        .selectExclude(User::getJoinDate)                 // 排除 joinDate 字段
        .field(User::getStatus, 1)                        // 过滤：status = 1
        .field(User::getName, "Jack").ic()                // 过滤：name = 'Jack' (case ignored)
        .field(User::getAge, 20, 30).op(Opetator.Between) // 过滤：age between 20 and 30
        .orderBy(User::getAge, "asc")                     // 排序：年龄，从小到大
        .page(0, 15)                                      // 分页：第 0 页, 每页 15 条
        .build();
List<User> users = beanSearcher.searchList(User.class, params);
```

**DEMO 快速体验**：🖥 [在线 Demo](https://demo-bs.zhxu.cn/) ｜ 💻 [本地运行](./bean-searcher-demos)

### 🚀 快速开发

使用 Bean Searcher 可以极大地节省后端的复杂列表检索接口的开发时间！

* 普通的复杂列表查询只需一行代码
* 单表检索可复用原有 `Domain`，无需定义 `SearchBean`

### 🌱 集成简单

可以和任意 Java Web 框架集成，如：SpringBoot、Spring MVC、Grails、Jfinal 等等。

#### Spring Boot 项目，添加依赖即集成完毕：

```groovy
implementation "cn.zhxu:bean-searcher-boot-starter:${latestVersion}"
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

#### Solon 项目，添加依赖即集成完毕：

```groovy
implementation "cn.zhxu:bean-searcher-solon-plugin:${latestVersion}"
```

接着便可在 `Controller` 或 `Service` 里注入检索器：

```groovy
@Inject
private MapSearcher mapSearcher;

@Inject
private BeanSearcher beanSearcher;
```

#### 其它框架，使用如下依赖：

```groovy
implementation "cn.zhxu:bean-searcher:${latestVersion}"
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

### 🔨 扩展性强

面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件！

比如你可以：
* 自定义 [`FieldOp`](/bean-searcher/src/main/java/cn/zhxu/bs/FieldOp.java) 来支持更多的字段运算符
* 自定义 [`FieldConvertor`](/bean-searcher/src/main/java/cn/zhxu/bs/FieldConvertor.java) 来支持任意的 特殊字段类型
* 自定义 [`DbMapping`](/bean-searcher/src/main/java/cn/zhxu/bs/DbMapping.java) 来实现自定义注解，或让 Bean Searcher 识别其它 ORM 的注解
* 自定义 [`ParamResolver`](/bean-searcher/src/main/java/cn/zhxu/bs/ParamResolver.java) 来支持其它形式的检索参数
* 自定义 [`Dialect`](/bean-searcher/src/main/java/cn/zhxu/bs/dialect/Dialect.java) 来支持更多的数据库
* 等等..

### 📚 详细文档

参阅：https://bs.zhxu.cn/

### 🤝 友情接链

- [**[ Sa-Token ]**](https://github.com/dromara/Sa-Token)： 一个轻量级 Java 权限认证框架，让鉴权变得简单、优雅！

- [**[ Fluent MyBatis ]**](https://gitee.com/fluent-mybatis/fluent-mybatis)： MyBatis 语法增强框架, 综合了 MyBatisPlus, DynamicSql,Jpa 等框架的特性和优点，利用注解处理器生成代码

- [**[ OkHttps ]**](https://gitee.com/troyzhxu/okhttps)： 轻量却强大的 HTTP 客户端，前后端通用，支持 WebSocket 与 Stomp 协议

- [**[ hrun4j ]**](https://github.com/lematechvip/hrun4j)： 接口自动化测试解决方案 --工具选得好，下班回家早；测试用得对，半夜安心睡 

- [**[ JsonKit ]**](https://gitee.com/troyzhxu/xjsonkit)： 超轻量级 JSON 门面工具，用法简单，不依赖具体实现，让业务代码与 Jackson、Gson、Fastjson 等解耦！

- [**[ Free UI ]**](https://gitee.com/phoeon/free-ui)： 基于 Vue3 + TypeScript，一个非常轻量炫酷的 UI 组件库 ！


### ❤️ 参与贡献

1.  Star and Fork 本仓库
2.  新建 Feat_xxx 分支（新功能基于 dev 分支，bugfix 基于特定版本的分支）
3.  提交代码
4.  新建 Pull Request
