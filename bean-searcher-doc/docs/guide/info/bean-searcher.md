
# Bean Searcher

Bean Searcher 是一个轻量级 数据库 条件检索引擎，它的作用是从已有的数据库表中检索数据，它的目的是为了减少后端模板代码的开发，极大提高开发效率，节省开发时间，使得一行代码完成一个列表查询接口成为可能！

* 不依赖具体的 Web 框架（即可以在任意的 Java Web 框架内使用）

* 不依赖具体的 ORM 框架（即可以与任意的 ORM 框架配合使用，没有 ORM 也可单独使用）

## 框架介绍

### 架构设计图

![](/architecture.jpg)

### 与 Hibernate MyBatis 的区别

首先，Bean Searcher 并不是一个完全的 `ORM` 框架，它存在的目的不是为了替换他们，而是为了弥补他们在 `列表检索领域` 的不足。

下表列举它们之间的具体区别：

区别点 | Bean Searcher | Hibernate | MyBatis
-|-|-|-
ORM | 只读 ORM | 全自动 ORM | 半自动 ORM
实体类可多表映射 | 支持 | 不支持 | 不支持
字段运算符 | **动态** | 静态 | 静态
CRUD | Only R | CRUD | CRUD

从上表可以看出，Bean Searcher 只能做数据库查询，不支持 增删改。但它的 **多表映射机制** 与 **动态字段运算符**，可以让我们在做复杂列表检索时代码 **以一当十**，甚至 **以一当百**。

更关键的是，它无第三方依赖，在项目中可以和 **任意 ORM 配合** 使用。

### 哪些项目可以使用

* Java 项目（当然 Kotlin、Groovy 也是可以的）；

* 使用了 关系数据库的项目（如：MySQL、Oracle 等）；

* 可与任意框架集成：Spring Boot、Grails、Jfinal 等等。

### 什么场景下需要用

任何框架都有其使用场景，当然 Bean Searcher 也不例外，它的诞生并不是为了替换 MyBatis / Hibernate 等传统 ORM，因此，理解哪些场景适合使用它非常重要。

* **推荐** 在 **非事务性** 的 **动态** 检索场景中使用，例如：

  管理后台的 [订单管理]、[用户管理] 等页面 的检索场景，该检索是 **非事务性** 的，不会向数据库中插入数据，且检索条件是 **动态** 的，用户检索方式不同，执行的 SQL 也不同（如按 `订单号` 检索 与 按 `状态` 检索 需要不同的 SQL），此时推荐使用 Bean Searcher 来执行检索；

* **不建议** 在 **事务性** 的 **静态** 查询场景中使用，例如：

  用户注册接口中需要先查询账号是否已存在的场景，该接口是 **事务性** 的，它需要向数据库中插入数据，且此时的查询条件是 **静态** 的，无论哪个账号，都执行一样的 SQL（都按 `账号名` 查询），此时不建议使用 Bean Searcher 来执行。

### 支持哪些数据库

只要支持正常的 SQL 语法，都是支持的，另外 Bean Searcher 内置了四个方言实现：

* 分页语法和 MySQL 一样的数据库，默认支持
* 分页语法和 PostgreSql 一样的数据库，选用 PostgreSql 方言 即可
* 分页语法和 Oracle 一样的数据库，选用 Oracle 方言 即可
* 分页语法和 SqlServer (v2012+) 一样的数据库，选用 SqlServer 方言 即可

如果分页语法独创的，则只需自定义一个方言，只需实现两个方法，参考：[高级 > SQL 方言](/guide/advance/dialect) 章节。

## DEMO 快速体验

仓库地址: [https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher-demos/bs-demo-springboot](https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher-demos/bs-demo-springboot)

### 第一步：克隆

```bash
> git clone https://github.com/troyzhxu/bean-searcher.git
```

### 第二步：运行

```bash
> cd bean-searcher/bean-searcher-demos/bs-demo-springboot
> mvn spring-boot:run
```

### 第三步：效果

访问 `http://localhost:8080/` 既可查看运行效果。

此例的更多信息，可参阅：[DEMO 详细介绍](https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher-demos/bs-demo-springboot)。

[更多 DEMO](https://github.com/troyzhxu/bean-searcher/tree/master/bean-searcher-demos)
