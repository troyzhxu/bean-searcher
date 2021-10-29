# Bean Searcher Grails Demo

### 介绍

本项目主要用于演示 [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 在 Web 工程中的使用方式，以及展示在列表检索场景中 [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 是如何提升我们的开发效率。

### 软件架构
软件架构说明

- 控制层：grails 5
- 数据库：h2 （无需安装配置）
- 数据库访问：gorm、bean-searcher

### 四步运行 DEMO

```bash
> git clone https://gitee.com/ejlchina-zhxu/bean-searcher.git
> cd bean-searcher/bean-searcher-demos/grails-demo
> grails
> run-app
```

### 运行效果

##### 1. 访问接口 http://localhost:8080/employee?size=3
查询 3 条员工，输出：

```json
{
  "dataList":[
    {
      "age":22,
      "department":"Finance",
      "entryDate":"2019-06-23T04:01:01Z",
      "id":1,
      "name":"Jack"
    },
    {
      "age":21,
      "department":"Technical",
      "entryDate":"2019-06-24T04:01:01Z",
      "id":2,
      "name":"Tom"
    },
    {
      "age":23,
      "department":"Market",
      "entryDate":"2019-06-21T04:01:01Z",
      "id":3,
      "name":"Alice"
    }
  ],
  "summaries":[],
  "totalCount":20,
}
```

##### 2. 访问接口 http://localhost:8080/employee?name=Jack

查询 name=Jack 的员工，输出：

```json
{
    "dataList":[
        {
            "age":22,
            "department":"Finance",
            "entryDate":"2019-06-23T04:01:01Z",
            "id":1,
            "name":"Jack"
        }
    ],
    "summaries":[],
    "totalCount":1,
}
```

本例还支持非常多的检索条件，例如：

* http://localhost:8080/employee?size=5&page=1
  - 分页，每页 5 条，第 2 页（第一页 page = 0）
* http://localhost:8080/employee?name=s&name-op=in&name-ic=true
  - 查询姓名包含字符串 "s" 的员工，并且忽略大小写
* http://localhost:8080/employee?department=Finance&age=24
  - 查询 Finance 部门 年龄为 24 的员工
* http://localhost:8080/employee?age-0=20&age-1=25&age-op=bt
  - 查询年龄在 20 - 25 之间的员工
* 等等

以上不同条件之间可以相互组合

##### 3. 本示例展示了一个简单的员工列表页面，实现了如下功能：

* 各种复杂条件组合过滤

* 年龄统计（支持多字段统计）

* 任意字段后端排序（点击表头）

* 分页查询功能

* 总条数统计

OK，页面做的虽然粗糙，但是一个列表检索的功能基本上展示了，下面主要看下在后端, Bean Searcher 是如何简化我们的代码。

### 代码分析

##### 控制层代码

有同学看到这会想，若要实现以上演示的的可以按照各种条件 **组合检索**、**排序**、**分页** 和 **统计** 的功能，那后端的代码量至少也得上百行吧。Bean Searcher 告诉你，不用，关键代码，就一句！啥？我怎么不信？请看代码：

```groovy
class EmployeeController {

    Searcher searcher

    def index() {
        render searcher.search(EmployeeBean, params) as JSON
    }

}
```

检索条件呢？检索方式呢？排序呢？分页呢？通通都交给 Bean Sarcher 去实现啦，世界突然如此美好！

##### 检索实体类

细心的同学会发现在上述代码里用到一个 Employee 这个类。没错，它就是用来告诉 bean-searcher 如何与数据库字段映射的一个实体类：

```java
@SearchBean(tables = "employee e, department d", joinCond = "e.department_id = d.id")
class EmployeeBean {

    @DbField("e.id")
    Long id;

    @DbField("e.name")
    String name;

    @DbField("e.age")
    Integer age;

    @DbField("d.name")
    String department;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DbField("e.entry_date")
    Date entryDate;

}
```

### 总结

- [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 设计的目标并不是替代某个ORM框架，它只是为了弥补现有ORM框架在复杂列表检索中的不便，实际项目中，配合使用它们，效果或会更好。
- 本例只是 [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 在联表检索中的一个简单的演示，更多用法，请参阅: [https://searcher.ejlchina.com](https://searcher.ejlchina.com)
- 看完这些，大家有没有觉得 [Bean Searcher](https://gitee.com/ejlchina-zhxu/bean-searcher) 正好可以帮到你呢？如果是，就点个 Star 吧 ^_^

### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
