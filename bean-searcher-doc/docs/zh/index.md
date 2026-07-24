---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: Bean Searcher
  text: 专注高级查询的只读 ORM
  tagline: 为应用赋能『高级』查询，你只需一行代码！
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
    details: 天生支持联表，免 DTO/VO 转换，一行代码轻松实现高级查询，极大提高研发效率。
  - icon: 🚀
    title: 运行性能绝尘
    details: 相较于 MyBatis、Hibernate 等传统 ORM，拥有数倍的 Java 层运行时性能提升。
  - icon: 🛡︎
    title: 构建安全服务
    details: 内置 防注入、防大页、防深拉 等安全机制，默认启用，杜绝无意识的安全隐患。
---

<div style="text-align: center; font-size: 20px; margin-top:60px">这不是一个重复的轮子</div>
<div style="text-align: center; font-size: 20px; margin-top:10px">因为从未有过一个功能复杂的检索接口可以简单的只剩一行代码 </div>

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
