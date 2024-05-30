---
home: true
heroImage: /logo.png
heroText: Bean Searcher
tagline: 专注高级查询的只读 ORM，天生支持联表，免 DTO/VO 转换，使一行代码实现复杂列表检索成为可能！
actionText: WHY BEAN-SEARCHER
actionLink: /guide/latest/introduction.html
altActionText: 起步 →
altActionLink: /guide/latest/start.html
features:
- title: 极速开发 ⚡
  details: 轻松实现高级查询，且无需 DTO/VO 转换, 极大提高后端研发效率
- title: 集成简单 🌱
  details: 可以和任意 Java Web 框架集成，如：SpringBoot、Grails、Solon、Jfinal 等 
- title: 扩展性强 🔨
  details: 面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件
- title: 支持 注解缺省
  details: 约定优于配置，可省略注解，可复用原有域类，同时支持自定义注解
- title: 支持 多数据源
  details: 分库分表？在这里特别简单，告别分库分表带来的代码熵值增高问题
- title: 支持 Select 指定字段
  details: 同一个实体类，可指定只 Select 其中的某些字段，或排除某些字段
- title: 支持 参数过滤器
  details: 支持添加多个参数过滤器，可自定义参数过滤规则
- title: 支持 字段转换器
  details: 支持添加多个字段转换器，可自定义数据库字段到实体类字段的转换规则
- title: 支持 SQL 拦截器
  details: 支持添加多个 SQL 拦截器，可自定义 SQL 生成规则


footer: Apache Licensed | Copyright © 2020-present zhouxu
---

### <div style="text-align: center"> 这绝不是一个重复的轮子<br><br> 因为从未有过一个功能复杂的接口可以简单的只剩一行代码 </div>

<br>

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanSearcher searcher;               // 注入 Bean Searcher 的检索器

    @GetMapping("/index")
    public SearchResult<User> index(HttpServletRequest request) {
        // 只一行代码，实现包含 分页、组合过滤、任意字段排序、甚至统计、多表联查的 复杂检索功能
        return searcher.search(User.class, MapUtils.flat(request.getParameterMap()));
    }

}
```

<br>

### <div style="text-align: center"> 同时支持编码式构建检索参数 <br><br> 独创动态字段运算符，检索方式随心所欲 </div>

<br>

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getType, 1).op(Equal.class)         // 条件：type 等于 1 （默认可以省略 Equal ）
        .field(User::getName, "张").op(StartWith.class)  // 条件：name 以 "张" 开头
        .field(User::getAge, 20, 30).op(Between.class)   // 条件：age 在 20 与 30 之间
        .field(User::getNickname, "Jack").ic()   // 条件：nickname 等于 Jack, 忽略大小写（ic = IgnoreCase）
        .orderBy(User::getAge).asc()             // 排序：age，从小到大
        .page(0, 15)                             // 分页：第 0 页, 每页 15 条
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

<br>

### <div style="text-align: center"> 若有疑难，立进有问必答交流群 </div>

<br>

<div style="text-align: center; margin-top:1rem;"> <img src="/wx_discuss.png" width = "700" /> </div>

<div style="text-align: center; margin-top:1rem;"> 广告推销者谢绝进群 </div>

<br>

### <div style="text-align: center">特别鸣谢</div>

<br/>

<DonateList :home="true" />

<br/>

<div style="text-align: center">
  <a href="/guide/latest/help.html#我要赞助">成为赞助者</a>
</div>

<br/>

### <div style="text-align: center"> 从此，代码以一当百，你还等什么？</div>

### [<div style="text-align: center"> 马上开始！ </div>](/guide/latest/introduction.html)

<br/>
