---
title: 为什么选择 Bean Searcher？ | Bean Searcher 文档
description: 了解声明式检索如何解决传统 Java 列表查询痛点：消灭 if-else 条件拼接、一行代码搞定分页筛选排序统计、内置安全风控机制。
head:
  - - meta
    - property: og:title
      content: 为什么选择 Bean Searcher？
  - - meta
    - property: og:description
      content: 消灭模板代码，一行代码实现复杂列表检索。
---
# 为什么用

![需求图](/requirement.png)

产品给你画了一张图，还附带了一些要求：

* 检索结果分页展示
* 可以按任意字段排序
* 按检索条件统计某些字段值

这时候，后台接口该怎么写？用 Mybatis 或 Hibernate 来实现，100 行代码恐怕都不够用。而使用 Bean Searcher，仅需 **一行代码** 便可实现上述所有要求。

::: tip
**Bean Searcher 之于列表检索，就像 GraphQL 之于 API 查询。** 不同的是：GraphQL 需要专用协议和 schema，而 Bean Searcher 工作在标准 HTTP 参数上——不改协议、不加中间层、一个依赖即用。
:::

## 从 VO 到 SearchBean（声明式检索的诞生）

任何一个系统都有列表检索需求（订单管理、用户管理等），而列表页的数据往往横跨多张数据库表。比如订单管理页：订单号来自订单表，用户名来自用户表——后端的 **域类**（与单表映射的实体类）无法直接对应用户看到的页面。

为了解决这个错位，**VO（View Object）** 应运而生。VO 不再和数据库表绑定，而是直接面向页面展示。听起来很美，但代价是：你需要额外写一层代码，把域类的查询结果转换到 VO——条件越复杂，转换逻辑越臃肿。

Bean Searcher 的答案是：**为什么 VO 不能直接映射到数据库？** 既然 VO 就是为了给页面用的，那让它直接"声明"它能关联哪些表、能接受哪些筛选条件，不是更合理吗？

这种升级版的 VO，就是 **SearchBean**——它既面向页面展示，又能直接映射到多张数据库表，是"声明"与"检索"的统一体：

> **SearchBean = VO（面向页面） + 跨表映射能力（面向数据库）**

这带来了一个根本性的变化：SearchBean 的每个字段不仅是页面要展示的数据，也是前端可以检索的条件。**SearchBean 本身就是检索接口的"声明"**——前端传什么参数，框架就生成对应的 SQL。你不再需要手写任何转换代码。

因此，一个 SearchBean 对应一个检索接口：
* 订单列表 → 一个 SearchBean
* 用户列表 → 一个 SearchBean
* 数据导出 → 同一个 SearchBean（复用检索逻辑）

## 效率极大提高的原因

**传统方式**：写 SQL / QueryWrapper → 查域类 → 转换到 VO → 收集筛选条件时还要写一堆 if-else 拼接。需求和 VO 每变一次，改三四层代码。

**声明式检索**：定义 SearchBean（一步到位的 VO）→ 一行 `search()` 结束。前端加筛选项、换排序、改分页大小 —— 全部直接支持，**后端零改动**。除非重新定义可检索边界，否则连 SearchBean 都不用改。

因为 SearchBean 声明了检索边界，`search()` 方法驱动了查询——你不需要在"声明"和"执行"之间再插入任何转换代码。返回的 SearchBean 直接就是页面需要的数据，前端拿到就能用。

这就是 **一行代码实现复杂列表检索** 的根本原因。

## 前端需要多传参数吗

很多人第一次接触 Bean Searcher，都会先入为主地**误以为**：使用 Bean Searcher 会给前端带来压力，需要前端多传许多原本不必传的参数。

其实完全不是这样：前端需要传的**参数多少**，只与**产品需求的复杂度**有关，与后端用的什么框架没有关系。

同学可能又问了：我看到很多关于 Bean Searcher 的文章里，都有 **xxx-op** 与 **xxx-ic** 这类参数，我们系统里从来没这东西，用了 Bean Searcher 也必须传它们吗？

放心，那些文章讲的是**高级查询**场景——产品本就要求前端可以自己控制某个字段是模糊查还是精确查，如下图：

![](/requirement_1.png)

那如果前端没这个需求呢，比如 `username` 字段，前端只需要模糊查询，也不需要忽略大小写。那后端还需要传 `username-op` 与 `username-ic` 参数吗？

**当然不需要**，只传 `username` 一个参数即可。那后端如何表达"模糊查询"这个约束呢？很简单，只需在 SearchBean 中给 `username` 字段加一个注解：

```java
@DbField(onlyOn = Contain.class)
private String username;
```

可参考：[高级 > 约束与风控](/guide/advance/safe) 章节。
