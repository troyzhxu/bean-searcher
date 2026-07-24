# Bean Searcher Solon 4 后端服务

[Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 演示项目的后端 API 服务，提供员工列表的检索与导出能力。前端已分离至 [`../frontend-vue`](../frontend-vue)。

### 技术栈

- Web 框架：Solon 4.0
- 数据库：H2（内存数据库，无需安装配置）
- ORM：bean-searcher 4.8.12 + wood
- JDK：17+

### 快速开始

在 IDEA 中打开 `backend-solon4` 目录，运行 `App.java` 主类即可。

启动后监听 `http://localhost:8080`，数据库自动建表并初始化种子数据。

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/index` | 分页检索员工（多条件过滤、排序、年龄统计） |
| GET | `/user/export` | 导出 CSV 文件 |

检索参数由 `ReqParamFilter` 自动从请求中加载，无需在 Controller 里逐个声明参数。

### 前端对接

```bash
cd ../frontend-vue
npm run dev
```

前端运行在 `http://localhost:7300`，通过 `.env` 中的 `VITE_API_BASE=http://localhost:8080` 连接本服务。

### 代码分析

检索接口的核心代码仅需 **一行**：

```java
@Controller
@Mapping("/user")
public class UserController {

    @Inject
    private BeanSearcher beanSearcher;

    @Inject
    private BeanExporter beanExporter;

    @Mapping("/index")
    public SearchResult<User> index() {
        // 组合检索、排序、分页 和 统计 都在这一句代码中实现
        return beanSearcher.search(User.class, User::getAge);
    }

    @Mapping("/export")
    public void export() throws IOException {
        beanExporter.export("员工资料", User.class);
    }
}
```

检索实体类 `User` 通过注解声明多表映射：

```java
@SearchBean(tables = "users u, dept d", where = "u.dept_id = d.id", autoMapTo = "u")
public class User {
    private long id;
    private String name;
    private int age;
    @DbField("d.name")
    @Export(name = "部门")
    private String department;
    @Export(name = "入职时间", format = "yyyy-MM-dd HH:mm")
    private LocalDateTime entryDate;
    // ...
}
```

### 更多信息

- [Bean Searcher 文档](https://bs.zhxu.cn)
- [更多 Demo](../)
