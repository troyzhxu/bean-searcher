# 概念

检索参数是 Bean Searcher 的重要检索信息，它们共同组成了 [`Searcher`](https://gitee.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/Searcher.java) 接口的检索方法的 第二个 类型为 `Map<String, Object>` 的参数值。

::: tip 重要提示
Bean Searcher 的检索 **参数** 与 **数据库表字段** 是 **解耦** 的，下文所说的 **字段**，均是指 **实体类** 的字段，即实体类的 **属性**。
:::

> 如果您还没有阅读 [介绍 > 为什么用](/en/guide/info/why) 章节，建议先阅读它们。
