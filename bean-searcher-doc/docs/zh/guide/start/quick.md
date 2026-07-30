---
title: Bean Searcher 快速入门 | 5 分钟上手高级查询
description: 5 分钟从零写出第一个检索接口。添加依赖、创建实体类、注入 BeanSearcher、一行代码实现分页+过滤+排序+统计的复杂列表检索。
head:
  - - meta
    - property: og:title
      content: Bean Searcher 快速入门 - 5 分钟上手
  - - meta
    - property: og:description
      content: 从零开始，5 分钟写出第一个复杂列表检索接口。
---
# 快速入门

这篇文章带你 **5 分钟**从零写出第一个检索接口。不用读完所有文档，跟着做就行。

## 1. 添加依赖

以 SpringBoot + Maven 为例，在 `pom.xml` 中加入：

```xml
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.8.12</version>
</dependency>
```

::: tip 其它构建方式
如果你用的是 Gradle、Solon 或其它 JDK 版本，请参考 [安装](/guide/start/install) 页面。
:::

## 2. 写一个 SearchBean

SearchBean 就是普通的 Java 类，用来映射数据库表。不用写任何注解，框架自动识别字段：

```java
public class User {

    private Long id;
    private String name;
    private int age;

    // 记得写 Getter 和 Setter，或者使用 Lombok 注解
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ...
}
```

## 3. 写一个 Controller

注入 `BeanSearcher`，一行代码搞定检索：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanSearcher beanSearcher;

    @GetMapping("/index")
    public SearchResult<Map<String, Object>> index(HttpServletRequest request) {
        // 就这一行
        return beanSearcher.search(User.class, MapUtils.flat(request.getParameterMap()));
    }
}
```

## 4. 跑一下

启动项目，试试这些请求：

```bash
# 无参查询（默认分页）
GET /user/index

# 按姓名搜
GET /user/index?name=Jack

# 按年龄搜
GET /user/index?age=20

# 年龄范围
GET /user/index?age-op=bt&age=20&age=30

# 分页 + 排序
GET /user/index?page=1&size=10&sort=age&order=desc
```

返回结果像这样：

```json
{
    "dataList": [
        { "id": 1, "name": "Jack", "age": 25 },
        { "id": 2, "name": "Tom",  "age": 20 }
    ],
    "totalCount": 100
}
```

## 接下来

到这里你已经跑通了！接下来可以看看：

- [使用](/guide/start/use) — 了解完整的检索 API 和运算符
- [安装](/guide/start/install) — 其它框架或 JDK 版本的安装方式
- [为什么用](/guide/info/why) — 了解 Bean Searcher 解决了什么问题
