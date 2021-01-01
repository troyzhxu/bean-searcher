# Bean Searcher

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Troy.Zhou](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-ejlchina-orange.svg)](https://github.com/ejlchina)

* 文档：https://searcher.ejlchina.com/
* 案例：https://gitee.com/ejlchina-zhxu/bean-searcher-demo

### ❓ 为什么用

* 产品画了一个图，后台接口怎么破？

![输入图片说明](https://images.gitee.com/uploads/images/2021/0101/172143_62355c4e_1393412.png "屏幕截图.png")

**嗯？嗯。这个简单！**

* 产品又来了个图，后台接口又该怎么破？

![输入图片说明](https://images.gitee.com/uploads/images/2021/0101/172608_d622bcd3_1393412.png "屏幕截图.png")

**什么？.... 好吧! 代码凌乱都是被产品逼的！**

* 但是，你的产品放大招了：

![输入图片说明](https://gitee.com/saodiyang/layui-soul-table/raw/master/img/tableFilter.gif "屏幕截图.png")

**我去，这后台代码该怎么写 ！！！**

### 💥 只一行代码实现以上功能

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

更多请参见文档：https://searcher.ejlchina.com/

### 🚀 快速开发

使用 Bean Searcher 可以极大节省后端的复杂列表检索接口的开发时间

### 🌱 集成简单

可以和任意 Java Web 框架集成，如：SpringBoot、Grails、Jfinal 等

### 🔨 扩展性强

面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件

### 友情链接

[**[ OkHttps ]** 前后端通用轻量却强大的 HTTP 客户端（同时支持 WebSocket 与 Stomp）](https://gitee.com/ejlchina-zhxu/okhttps)

### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request



