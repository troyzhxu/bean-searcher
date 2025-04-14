# 概念

检索实体类，即是被 `@SearchBean` 注解的类，又称 **SearchBean**，在 [上一节](/guide/start/use)，我们体验了 Bean Searcher 的单表检索功能，但相比于传统的 ORM，其实它更擅长处理复杂的联表检索 与 一些奇奇怪怪的 子查询，此时 SearchBean 的定义也非常容易。

另外，Bean Searcher 也支持 [省略注解](/guide/bean/aignore)。一个没有任何注解的实体类也可以自动进行数据库映射。

::: tip 重要提示
这里所说的 实体类（**SearchBean**）是一个与数据库有跨表映射关系的 **VO（View Ojbect）**，它与传统 ORM 中的实体类（**Entity**）或 域类（**Domain**）在概念上有着本质的区别。 
可参阅：[介绍 > 为什么用 > 设计思想（出发点）](/guide/info/why#设计思想-出发点) 章节。
:::
