---
home: true
heroImage: /logo.png
heroText: Bean Searcher
tagline: 轻量级 WEB 条件检索引擎，使一行代码实现复杂列表检索成为可能！
actionText: 极速上手 →
actionLink: /v2/
features:
- title: 快速开发
  details: 使用 Bean Searcher 可以大大降低后端的复杂列表检索接口的开发时间
- title: 集成简单
  details: 可以和任何 Java Web 框架集成，如：SpringBoot、Grails、Jfinal 等 
- title: 扩展性强
  details: 面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件
footer: Apache Licensed | Copyright © 2020-present ejlchina.com
---

### <center> 如艺术一般优雅，像 1、2、3 一样简单 </center>

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private Searcher searcher;

    @GetMapping("/index")
    public Object index(HttpServletRequest request) {
        // 只需一行代码，完成包含分页、过滤、排序的复杂列表检索功能
        // 调用 bean-searcher 提供的 searcher 接口检索数据并返回
        return searcher.search(User.class, MapUtils.flat(request.getParameterMap()));
    }
	
}
```

### [<center> 了解更多 </center>](/v2/)

<br/>