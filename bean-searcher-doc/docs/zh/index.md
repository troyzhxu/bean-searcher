---
title: Bean Searcher - Java 声明式检索框架 | 一行代码搞定复杂列表查询
description: Bean Searcher 是列表检索领域的 GraphQL — 实体定义检索边界，参数驱动查询逻辑。单表零注解即可搜，一行代码即可应对千变万化的检索请求。不改变你现有的 HTTP 协议习惯。
head:
  - - meta
    - name: keywords
      content: Bean Searcher,只读ORM,Java声明式检索,声明式检索框架,参数驱动查询,高级查询,动态查询,Spring Boot,列表查询,分页查询,多表联查,检索框架,GraphQL
  - - meta
    - property: og:type
      content: website
  - - meta
    - property: og:title
      content: Bean Searcher - Java 声明式检索框架
  - - meta
    - property: og:description
      content: 列表检索领域的 GraphQL — 实体定义边界，参数驱动查询。一行代码搞定复杂列表检索。
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: Bean Searcher
  text: Java 声明式检索框架
  tagline: REST 版的 GraphQL — 实体定义边界，参数驱动查询，一行代码搞定！
  image: /logo.png
  actions:
    - theme: brand
      text: 🚀 在线体验
      link: https://demo-bs.zhxu.cn/
    - theme: alt
      text: 为什么？
      link: /guide/info/why
    - theme: alt
      text: 起步 →
      link: /guide/start/quick

features:
  - icon: 😱
    title: 开发效率惊人
    details: 单表实体零注解即可搜，联表只需配置关联。告别繁琐的 if-else 条件拼接，一行代码轻松应对分页、筛选、排序、统计。
  - icon: 🎯
    title: 客户端驱动查询
    details: 后端一个接口，前端控制一切：查哪些字段、用什么运算符筛、按什么排序。就像 GraphQL 之于 API，这是声明式检索之于列表查询。
  - icon: 🧩
    title: 天生联表，告别 JOIN
    details: 实体类声明关联关系，框架自动生成多表联查 SQL。单表、多表同一套 API，无需手写复杂 JOIN 语句。
  - icon: 🔌
    title: 零侵入，共存共生
    details: 不替换现有 ORM，不改变已有架构。与 MyBatis/JPA 各司其职——它们管增删改，Bean Searcher 管列表查，和谐共存。
  - icon: 🚀
    title: 运行性能绝尘
    details: 直接生成 SQL 而非包装 ORM，Java 层额外开销接近于零。绝不拖慢你的服务，任何 ORM 能做到的最快，也最多和 Bean Searcher 一样快。
  - icon: 🛡︎
    title: 安全，默认自带
    details: 防 SQL 注入、防超大分页、防深度偏移——三大风控机制全部默认开启。开发者无需写一行校验代码，非法参数自动拦截，上线更安心，运维更省心。
---

<div style="text-align: center; font-size: 20px; margin-top:60px">REST 风格的 GraphQL</div>
<div style="text-align: center; font-size: 20px; margin-top:10px">无需专用协议，不改变 HTTP 习惯。一行代码，自由组合任何字段的筛选、排序、分页与统计。</div>

<br>

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanSearcher beanSearcher;               // 注入 Bean Searcher 的检索器

    @GetMapping("/index")
    public SearchResult<User> index() {
        // 只一行代码，实现包含 分页、组合过滤、任意字段排序、甚至统计、多表联查的 复杂检索功能  // [!code focus]
        return beanSearcher.search(User.class); // [!code focus]
    }

}
```

<br>

<img src="/wx_discuss.png" width = "700" style="margin: 1rem auto" />

<div style="text-align: center; margin-top:1rem; color: gray"> 广告推销者谢绝进群 </div>

<script setup>
import OtherProjects from '../.vitepress/theme/OtherProjects.vue'
</script>

<OtherProjects />
