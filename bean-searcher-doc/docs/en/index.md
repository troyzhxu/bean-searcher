---
title: Bean Searcher - Java Declarative Search Framework | One-Line Complex List Queries
description: "Bean Searcher is the GraphQL of REST APIs: entities define search boundaries, parameters drive queries — one line of code for complex list queries."
head:
  - - meta
    - name: keywords
      content: Bean Searcher,read-only ORM,Java declarative search,declarative search framework,parameter-driven query,advanced query,dynamic query,Spring Boot,list query,pagination,join table,GraphQL,Rest GraphQL
  - - meta
    - property: og:type
      content: website
  - - meta
    - property: og:title
      content: Bean Searcher - Java Declarative Search Framework
  - - meta
    - property: og:description
      content: The GraphQL of list retrieval — entity defines boundaries, parameters drive queries. One line of code.
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: Bean Searcher
  text: Java Declarative Search Framework
  tagline: The GraphQL of REST APIs — entities define search boundaries, parameters drive queries, one line of code for endless query combinations
  image: /logo.png
  actions:
    - theme: brand
      text: 🚀 Live Demo
      link: https://demo-bs.zhxu.cn/
    - theme: alt
      text: WHY?
      link: /en/guide/info/why
    - theme: alt
      text: Start →
      link: /en/guide/start/quick

features:
  - icon: 😱
    title: Amazing Development Efficiency
    details: Single-table entities searchable with zero annotations. Multi-table? Just declare relationships. Say goodbye to endless if-else condition stitching — pagination, filtering, sorting, and stats, all in one line.
  - icon: 🎯
    title: Client-Driven Queries
    details: "One endpoint on the backend, full control on the frontend: which fields to return, which operators to filter by, what to sort on. Like GraphQL for APIs, this is declarative search for list queries."
  - icon: 🧩
    title: Native Multi-Table Joins
    details: Entity classes declare relationships; the framework generates join SQL automatically. Single-table and multi-table queries share the same API — no hand-written JOINs needed.
  - icon: 🔌
    title: Zero Intrusion, Peaceful Coexistence
    details: Won't replace your existing ORM, won't change your architecture. Works alongside MyBatis/JPA — they handle CRUD, Bean Searcher handles list queries. Harmony.
  - icon: 🚀
    title: Peerless Runtime Performance
    details: Generates SQL directly — no ORM wrapper overhead in the Java layer. Won't slow down your database. The fastest any ORM can be is just as fast as Bean Searcher.
  - icon: 🛡︎
    title: Secure by Default
    details: SQL injection prevention, oversized pagination blocking, deep-offset throttling — all enabled out of the box. Zero security code needed. Ship with confidence.
---
<div style="text-align: center; font-size: 20px; margin-top:60px">GraphQL for REST APIs</div>
<div style="text-align: center; font-size: 20px; margin-top:10px">No special protocol. No changing your HTTP habits. One line of code — free combination of any field's filtering, sorting, pagination, and statistics.</div>

<br>

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanSearcher beanSearcher;               // Inject the Searcher

    @GetMapping("/index")
    public SearchResult<User> index() {
        // Only one line of code, including paging, combination filtering, arbitrary field sorting, and even statistics, multi-table joint search complex search functions // [!code focus]
        return beanSearcher.search(User.class); // [!code focus]
    }

}
```

<br>

<img src="/wx_discuss.png" width = "700" style="margin: 1rem auto" />

<div style="text-align: center; margin-top:1rem; color: gray">Advertisers refuse to join the group</div>

<script setup>
import OtherProjects from '../.vitepress/theme/OtherProjects.vue'
</script>

<OtherProjects />
