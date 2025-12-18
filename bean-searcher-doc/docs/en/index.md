---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: Bean Searcher
  text: Read-only ORM focused on advanced queries
  tagline: Enable "advanced" queries for your application with one line of code!
  image: /logo.png
  actions:
    - theme: brand
      text: WHY BEAN-SEARCHER
      link: /en/guide/info/why
    - theme: alt
      text: Start →
      link: /en/guide/start/install

features:
  - icon: 😱
    title: Amazing development efficiency
    details: Natively supports joint tables, free of DTO/VO conversion, and a line of code can easily achieve advanced queries, greatly improving research and development efficiency.
  - icon: 🚀
    title: Superb runtime performance
    details: Compared with traditional ORMs such as MyBatis and Hibernate, it has several times the Java layer runtime performance improvement.
  - icon: 🛡︎
    title: Building security services
    details: Built-in anti-injection, anti-large page, anti-deep retriving and other security mechanisms, enabled by default, to eliminate unconscious security risks.
---

<div style="text-align: center; font-size: 20px; margin-top:60px">It's not a repetitive wheel</div>
<div style="text-align: center; font-size: 20px; margin-top:10px">because there has never been a complex retrieval interface that simply has a single line of code.</div>

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
