# Bean Searcher

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Troy.Zhou](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-ejlchina-orange.svg)](https://github.com/ejlchina)

Introduction
---

文档：https://searcher.ejlchina.com/

#### 超轻量级 Web 条件检索引擎，为弥补传统ORM框架在复杂条件列表检索时的不便而生，使一行代码实现复杂列表检索成为可能！

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private Searcher searcher;

    @GetMapping("/index")
    public Object index(HttpServletRequest request) {
        // 只一行代码，实现包含分页、组合过滤、任意字段排序、甚至统计的复杂检索功能
        // 调用 Bean Searcher 提供的 Searcher 接口检索数据并返回
        return searcher.search(User.class, MapUtils.flat(request.getParameterMap()));
    }
	
}
```

#### 快速开发

使用 Bean Searcher 可以极大节省后端的复杂列表检索接口的开发时间

#### 集成简单

可以和任意 Java Web 框架集成，如：SpringBoot、Grails、Jfinal 等

#### 扩展性强

面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件

Documentation
---

### https://searcher.ejlchina.com/

Examples
---

### https://gitee.com/ejlchina-zhxu/bean-searcher-demo






