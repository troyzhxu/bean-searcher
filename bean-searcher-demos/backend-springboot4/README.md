# Bean Searcher SpringBoot 4 后端服务

### 介绍

本项目是 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 演示项目的后端 API 服务，提供员工列表的检索与导出能力。前端工程已拆分至 `../frontend-vue`，本服务仅负责提供数据接口。

### 软件架构

- Web 框架：SpringBoot 4
- 数据库：H2（无需安装配置）
- 数据库访问：bean-searcher

### 运行方式

```bash
> git clone https://github.com/troyzhxu/bean-searcher.git
> cd bean-searcher/bean-searcher-demos/backend-springboot4
> ./gradlew bootRun
```

服务启动后默认监听 `8080` 端口。

### API 接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/employee/index` | 员工列表检索（组合检索、排序、分页、统计） |
| GET | `/employee/file.cvs` | 导出 CSV 文件 |
| GET | `/employee/index1` | `/employee/index` 的等效写法（显式接收参数） |

### 前端工程

前端项目位于 `../frontend-vue`，启动方式：

```bash
> cd ../frontend-vue
> npm run dev
```

前端默认运行在 `7300` 端口，通过 `.env` 中的 `VITE_API_BASE` 配置后端 API 地址（默认 `http://localhost:8080`）。

### 代码分析

##### 控制层代码

Bean Searcher 的核心检索能力，关键代码只需一句：

```java
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final BeanSearcher beanSearcher;

    public EmployeeController(BeanSearcher beanSearcher) {
        this.beanSearcher = beanSearcher;
    }

    /**
     * 员工列表检索接口
     */
    @GetMapping("/index")
    public Object index(@RequestParam Map<String, Object> params) {
        // 组合检索、排序、分页 和 统计 都在这一句代码中实现了
        return beanSearcher.search(Employee.class, params, Employee::getAge);
    }

}
```

检索条件、检索方式、排序、分页统统交给 Bean Searcher 处理。该方法返回值为 `Object`，接收参数为 `Map<String, Object>`，也可以等效地写成显式接收参数的形式（见 `/employee/index1` 接口）：

```java
@GetMapping("/index1")
public SearchResult<Employee> index1(String name, String department, Integer page, Integer size, String sort, String order,
                                        @RequestParam(value = "name-op", required = false) String name_op,
                                        @RequestParam(value = "name-ic", required = false) boolean name_ic,
                                        @RequestParam(value = "age-0", required = false) Integer age_0,
                                        @RequestParam(value = "age-1", required = false) Integer age_1,
                                        @RequestParam(value = "age-op", required = false) String age_op,
                                        @RequestParam(value = "department-op", required = false) String department_op,
                                        @RequestParam(value = "department-ic", required = false) boolean department_ic,
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
            .page(page != null ? page : 0, size != null ? size : 15)
            .build();
    // 组合检索、排序、分页 和 统计 都在这一句代码中实现了
    return beanSearcher.search(Employee.class, params, Employee::getAge);
}
```

因为该例支持的参数比较多，所以这种写法看起来稍微臃肿一点，但 **实际检索的地方仍只是最后一行代码**。

至于为什么可以支持这么多的参数，请参阅 [Bean Searcher 文档的参数章节](https://bs.zhxu.cn/guide/params.html)。

##### 检索实体类

Employee 是用来告诉 bean-searcher 如何与数据库字段映射的实体类：

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

### 总结

- [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 设计的目标并不是替代某个 ORM 框架，它只是为了弥补现有 ORM 框架在复杂列表检索中的不便，实际项目中配合使用效果更好。
- 本例只是 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 在联表检索中的一个简单演示，更多用法请参阅 [https://bs.zhxu.cn](https://bs.zhxu.cn)。
