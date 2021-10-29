# Bean Searcher

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Troy.Zhou](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-ejlchina-orange.svg)](https://github.com/ejlchina)

* 文档：https://searcher.ejlchina.com/
 
 若以上地址不可用，请访问：http://searcher.ejlchina-app.com/

* 案例：https://gitee.com/ejlchina-zhxu/bean-searcher-demo

### ⁉️为什么用

* 产品如下一个需求，后端接口怎写多少行？

![输入图片说明](https://images.gitee.com/uploads/images/2021/0101/172608_d622bcd3_1393412.png "屏幕截图.png")

### 💥 只一行代码实现以上功能

无论简单还是复杂，Bean Searcher 只需一行代码：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private Searcher searcher;                      // 注入 Bean Searcher 的检索器

    @GetMapping("/index")
    public SearchResult<User> index(HttpServletRequest request) {
        // 只一行代码，实现包含 分页、组合过滤、任意字段排序、甚至统计、多表联查的 复杂检索功能
        return searcher.search(User.class, MapUtils.flat(request.getParameterMap()));
    }
	
}
```

这一行代码可实现：

* **多表联查**
* **分页搜索**
* **组合过滤**
* **任意字段排序**
* **字段统计**

### ✨ 独创动态字段运算符，检索方式随心所欲

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getName, "张").op("sw")        // 条件：姓名以"张"开头
        .field(User::getAge, 20, 30).op("bt")       // 条件：年龄在 20 与 30 之间
        .field(User::getNickname, "Jack").ic()      // 条件：昵称等于 Jack, 忽略大小写
        .orderBy(User::getAge, "asc")               // 排序：年龄，从小到大
        .page(0, 15)                                // 分页：第 0 页, 每页 15 条
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

小 **DEMO 快速体验** 一下：

https://gitee.com/ejlchina-zhxu/bean-searcher-demo


### 🚀 快速开发

使用 Bean Searcher 可以极大地节省后端的复杂列表检索接口的开发时间！

### 🌱 集成简单

可以和任意 Java Web 框架集成，如：SpringBoot、Spring MVC、Grails、Jfinal 等等。

### 🔨 扩展性强

面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件！

### 📚 详细文档

参阅：https://searcher.ejlchina.com/

文档已完善！

### 🤝 友情接链

[**[ Sa-Token ]** 一个 JavaWeb 轻量级权限认证框架，功能全面，上手简单](https://github.com/dromara/Sa-Token)

[**[ OkHttps ]** 轻量却强大的 HTTP 客户端，前后端通用，支持 WebSocket 与 Stomp 协议](https://gitee.com/ejlchina-zhxu/okhttps)

[**[ JsonKit ]** 超轻量级 JSON 门面工具，用法简单，不依赖具体实现，让业务代码与 Jackson、Gson、Fastjson 等解耦！](https://gitee.com/ejlchina-zhxu/jsonkit)

### ❤️ 参与贡献

1.  Star and Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request



