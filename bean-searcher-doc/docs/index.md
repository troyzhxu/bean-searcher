---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: Bean Searcher
  text: 专注高级查询的只读 ORM
  tagline: 为应用赋能高级查询，你只需一行代码！
  image: /logo.png
  actions:
    - theme: brand
      text: WHY BEAN-SEARCHER
      link: /guide/info/why
    - theme: alt
      text: 起步 →
      link: /guide/start/install

features:
  - icon: 🚀
    title: 开发效率惊人
    details: 天生支持联表，免 DTO/VO 转换，一行代码轻松实现高级查询，极大提高研发效率。
  - icon: 🎁
    title: 运行性能绝尘
    details: 相较于 MyBatis、Hibernate 等传统 ORM，拥有数倍的运行时性能提升。
  - icon: 🛡︎
    title: 构建安全服务
    details: 内置 防注入、防大页 等安全机制，降低安全隐患，开发者可放心编码，构建安全服务。
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
    public SearchResult<User> index(HttpServletRequest request) {
        // 只一行代码，实现包含 分页、组合过滤、任意字段排序、甚至统计、多表联查的 复杂检索功能
        return beanSearcher.search(User.class, MapUtils.flat(request.getParameterMap())); // [!code focus]
    }

}
```

<br>

<img src="/wx_discuss.png" width = "700" style="margin: 1rem auto" />

<div style="text-align: center; margin-top:1rem;"> 广告推销者谢绝进群 </div>
