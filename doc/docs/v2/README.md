---
description: Bean Searcher
---

# 介绍

## 为什么用

![需求图](/requirement.png)

你的产品给你画了以上一张图，还附带了一些要求：

* 检索结果分页展示
* 可以按任意字段排序
* 按检索条件统计某些字段值

这时候，后台接口该怎么写？？？使用 Mybatis 或 Hibernate 写 100 行代码是不是还打不住？而使用 Bean Searcher，只需 **一行代码** 便可实现上述要求！！！

## Bean Searcher

Bean Searcher 是一个轻量级 WEB 条件检索引擎，它的作用是从已有的数据库表中检索数据，它的目的是为了减少后端模板代码的开发，极大提高开发效率，节省开发时间，使得一行代码完成一个列表查询接口成为可能！

* 不依赖具体的 Web 框架（即可以在任意的 Java Web 框架内使用）

* 不依赖具体的 ORM 框架（即可以与任意的 ORM 框架配合使用，没有 ORM 也可单独使用）

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

从上表可以看出，似乎 Bean Searcher 能做的事，Hibernate、MyBatis 都可以做，而且还可以做的更多，确是如此，所以 Bean Searcher 可以为我们做的就是我们日常在用传统 ROM 再做的一部分事，只不过在它所擅长的方面，比起使用传统 ORM， 它可以让我们的代码 **以一当十** 甚至 **以一当百**，这便是它的作用 ！

### 哪些项目可以使用

* Java 项目（当然 Kotlin、Groovy 也是可以的）

* 使用了 关系数据库的项目（如：MySQL）

* 可与任意框架集成：Spring Boot、Grails、Jfinal 等等

### 小 DEMO 快速体验

#### 仓库地址

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

访问 [http://localhost:8080/](http://localhost:8080/) 既可查看运行效果。

此例的更多信息，可参阅：[DEMO 详细介绍](https://gitee.com/ejlchina-zhxu/bean-searcher-demo)。

## 版本迭代

### v2.0 的新特性

1. 实现 Spring Boot Starter 化