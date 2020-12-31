---
description: Bean Searcher
---

# 介绍

## Bean Searcher 简介

Bean Searcher 是一个轻量级 WEB 条件检索引擎，它的作用是从已有的数据库表中检索数据，它的目的是为了减少后端模板代码的开发，极大提高开发效率，节省开发时间，使得一行代码完成一个列表查询接口成为可能！

### 与 ORM、全文搜索引擎 的区别

首先，Bean Searcher 并不是一个`ORM`框架，也不是`全文搜索引擎`，它存在的目的不是为了替换他们，而是为了弥补他们在`精确的列表检索领域`的不足。

下表列举它们之间的具体区别：

区别 | Bean Searcher | Hibernate、MyBatis | Elasticsearch
-|-|-|-
是 DataBase? | 不是 | 不是 | NoSQL DataBase
可以连接 SQL DataBase? | 可以 | 可以 | 不可以
是 ORM ? | 不算是 | 是 | 不是
可以执行 SQL insert? | 不可以 | 可以 | 不可以
可以执行 SQL update? | 不可以 | 可以 | 不可以
可以执行 SQL select? | 可以 | 可以 | 不可以


从上表可以看出，似乎 Bean Searcher 能做的事，Hibernate、MyBatis 都可以做，而且还可以做的更多，事实确实是这样。

但是，如果你用了 Bean Searcher，在做列表查询的时候，别人写一百行代码，而你只需要写一行！

### 哪些项目可以使用

* Java 项目（当然 Kotlin、Groovy 也是可以的）

* 使用了 关系数据库的项目（如：MySQL）

* 可与任意框架集成：Spring Boot、Grails、Jfinal 等等

### 小 Demo 快速体验

#### 项目地址

[https://gitee.com/ejlchina-zhxu/bean-searcher-demo](https://gitee.com/ejlchina-zhxu/bean-searcher-demo)

#### 第一步：克隆

```bash
> git clone https://gitee.com/ejlchina-zhxu/bean-searcher-demo.git
```

#### 第二步：运行

```bash
> cd bean-searcher-demo
> mvn spring-boot:run
```

#### 第三步：效果

访问 [http://localhost:8080/](http://localhost:8080/) 既可查看运行效果

