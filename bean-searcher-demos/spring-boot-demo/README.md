# Bean Searcher Spring Boot Demo

### 介绍

本项目主要用于演示 [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 在 Web 工程中的使用方式，以及展示在列表检索场景中 [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 是如何提升我们的开发效率。

### 软件架构
软件架构说明

- 控制层：spring-boot-web 2
- 数据库：h2 （无需安装配置）
- 数据库访问：spring-jdbc、bean-searcher
- 模板引擎：thymeleaf
- 前端框架：vue、element-ui

### 三步运行 DEMO

```bash
> git clone https://gitee.com/ejlchina-zhxu/bean-searcher-demo.git
> cd bean-searcher-demo
> mvn spring-boot:run
```

### 运行效果

##### 1. 打开浏览器访问：[http://localhost:8080/](http://localhost:8080/) 效果如下：

![输入图片说明](https://images.gitee.com/uploads/images/2020/1231/163659_08cb49b0_1393412.png "屏幕截图.png")

##### 2. 如上图，本示例展示了一个简单的员工列表页面，实现了如下功能：

* 各种复杂条件组合过滤

* 年龄统计（支持多字段统计）

* 任意字段后端排序（点击表头）

* 分页查询功能

* 总条数统计

OK，页面做的虽然粗糙，但是一个列表检索的功能基本上展示了，下面主要看下在后端, Bean Searcher 是如何简化我们的代码。

### 代码分析

##### 控制层代码

有同学看到这会想，若要实现以上演示的的可以按照各种条件 **组合检索**、**排序**、**分页** 和 **统计** 的功能，那后端的代码量至少也得上百行吧。Bean Searcher 告诉你，不用，关键代码，就一句！啥？我怎么不信？请看代码：

```java
@RestController
public class DemoController {

    @Autowired
    private Searcher searcher;
    
    /**
     * 列表检索接口
     */
    @GetMapping("/employee/index")
    public Object index(HttpServletRequest request) {
        // 组合检索、排序、分页 和 统计 都在这一句代码中实现了
        return searcher.search(Employee.class,              // 指定实体类
                MapUtils.flat(request.getParameterMap()),   // 收集页面请求参数
                new String[] { "age" });                    // 统计字段：年龄
    }

}
```

检索条件呢？检索方式呢？排序呢？分页呢？通通都交给 Bean Sarcher 去实现啦，世界突然如此美好！

**咦！**,这方法的 **返回值怎么是 Object**，**接收参数怎么是 HttpServletRequest**，这让我的 **文档工具 Swagger 怎么用**?

可能看到此处很多人都由此疑问，实际上这些与 Bean Searcher 都没有关系，它只是需要一个 `Map<String, Object>` 类型的参数，其它的你爱咋写就咋写，比如你可以把它等效的写成这样：

```java
@GetMapping("/employee/index")
public SearchResult<Employee> index1(String name, String department, Integer page, Integer size, String sort, String order,
            @RequestParam(value = "name-op", required = false) String name_op,
            @RequestParam(value = "name-ic", required = false) String name_ic,
            @RequestParam(value = "age-0", required = false) Integer age_0,
            @RequestParam(value = "age-1", required = false) Integer age_1,
            @RequestParam(value = "age-op", required = false) String age_op,
            @RequestParam(value = "department-op", required = false) String department_op,
            @RequestParam(value = "department-ic", required = false) String department_ic,
            @RequestParam(value = "entryDate-0", required = false) String entryDate_0,
            @RequestParam(value = "entryDate-1", required = false) String entryDate_1,
            @RequestParam(value = "entryDate-op", required = false) String entryDate_op) {
    // 使用 MapUtils 构建检索参数
    Map<String, Object> params = MapUtils.builder()
        .field(Employee::getName, name).op(name_op).ic(name_ic)
        .field(Employee::getAge, age_0, age_1).op(age_op)
        .field(Employee::getDepartment, department).op(department_op).ic(department_ic)
        .field(Employee::getEntryDate, entryDate_0, entryDate_1).op(entryDate_op)
        .orderBy(sort, order)
        .page(page, size)
        .build();
    // 组合检索、排序、分页 和 统计 都在这一句代码中实现了
    return searcher.search(Employee.class, params, new String[] { "age" });
}
```

因为该例支持的参数比较多，所以这种写法看起来就稍微臃肿一点，但 **实际检索的地方仍只是最后一行代码**！

至于为什么可以支持这么多的参数，请参阅 [Bean Searcher 的文档的参数章节](https://searcher.ejlchina.com/guide/params.html)，本例重在体验，具体细节不做讨论。

##### 检索实体类

细心的同学会发现在上述代码里用到一个 Employee 这个类。没错，它就是用来告诉 bean-searcher 如何与数据库字段映射的一个实体类：

```java
@SearchBean(
    tables = "employee e, department d",  // 员工表 与 部门表
    joinCond = "e.department_id = d.id"   // 连接条件
)
public class Employee {

    @DbField("e.id")
    private Long id;

    @DbField("e.name")
    private String name;

    @DbField("e.age")
    private Integer age;

    @DbField("d.name")
    private String department;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DbField("e.entry_date")
    private Date entryDate;

    // Getter and Setter ...
}
```

##### 检索器配置

Bean Searcher 2.x 版本，已经实现了 spring-boot-starter 化，所在，在spring-boot 项目里，它只需要在`application.properties`里配几个必须的信息即可：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/012140_aa87c91d_1393412.png "微信图片_20190707012120.png")

另外，在 IDEA 里，bean-searcher的配置是有提示的哦：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/012532_e81dee9c_1393412.png "微信图片_20190707012518.png")

### 总结

- [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 设计的目标并不是替代某个ORM框架，它只是为了弥补现有ORM框架在复杂列表检索中的不便，实际项目中，配合使用它们，效果或会更好。
- 本例只是 [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 在联表检索中的一个简单的演示，更多用法，请参阅: [https://searcher.ejlchina.com](https://searcher.ejlchina.com)
- 看完这些，大家有没有觉得 [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 正好可以帮到你呢？如果是，就点个 Star 吧 ^_^

### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
