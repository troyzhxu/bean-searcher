# Bean Searcher SpringBoot 3 后端服务

### 介绍

本项目是 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 的 SpringBoot 3 后端示例服务，演示在 Web 工程中如何使用 Bean Searcher 提升列表检索的开发效率。

本项目为纯后端 API 服务，前端已分离至 `../frontend-vue` 目录。

### 软件架构

- Web 框架：SpringBoot 3
- 数据库：H2（无需安装配置）
- 数据库访问：spring-jdbc、bean-searcher

### API 接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/employee/index` | 员工列表检索接口（组合检索、排序、分页、统计） |
| GET | `/employee/file.cvs` | 员工数据导出接口 |
| GET | `/employee/index1` | 员工检索接口（显式参数写法示例） |

### 运行

环境要求：JDK 17+

```bash
> git clone https://github.com/troyzhxu/bean-searcher.git
> cd bean-searcher/bean-searcher-demos/backend-springboot3
> ./gradlew bootRun
```

启动后，后端服务运行在 `http://localhost:8080`。

### 前端

前端项目位于 `../frontend-vue`，运行方式：

```bash
> cd ../frontend-vue
> npm run dev
```

前端开发服务运行在端口 `7300`，通过 `.env` 中的 `VITE_API_BASE` 配置后端 API 地址（默认 `http://localhost:8080`）。

### 代码分析

##### 控制层代码

Bean Searcher 可以用一句代码实现 **组合检索**、**排序**、**分页** 和 **统计** 功能：

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

检索条件、检索方式、排序、分页都交给 Bean Searcher 自动处理。

##### 检索实体类

Employee 类用来告诉 bean-searcher 如何与数据库字段映射：

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
- 本例只是 Bean Searcher 在联表检索中的一个简单演示，更多用法请参阅：[https://bs.zhxu.cn](https://bs.zhxu.cn)

### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
