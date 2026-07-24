# Bean Searcher Solon 后端服务

### 介绍

本项目是 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 在 Solon Web 工程中的后端服务案例，演示在列表检索场景中 Bean Searcher 是如何提升开发效率的。

前端页面已分离到独立的工程 [`../frontend-vue`](../frontend-vue)。

### 软件架构

- Web 框架：Solon 4
- 数据库：H2（无需安装配置）
- 数据库访问：wood、bean-searcher

### 运行后端

```bash
> git clone https://github.com/troyzhxu/bean-searcher.git
> cd bean-searcher/bean-searcher-demos/backend-solon3
> IDEA 打开运行
```

后端启动后，默认监听 `http://localhost:8080`。

### 运行前端

```bash
> cd bean-searcher/bean-searcher-demos/frontend-vue
> npm install
> npm run dev
```

前端开发服务器启动在 `http://localhost:7300`，通过 `.env` 中的 `VITE_API_BASE` 配置后端 API 地址（默认 `http://localhost:8080`）。

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/employees` | 分页检索员工数据（含年龄统计） |
| GET | `/employees.cvs` | 导出 CSV 文件 |

### 代码分析

有同学看到这会想，若要实现可以按照各种条件 **组合检索**、**排序**、**分页** 和 **统计** 的功能，那后端的代码量至少也得上百行吧。Bean Searcher 告诉你，不用，关键代码，就一句：

```java
@Controller
public class DemoController {

    @Inject
    private BeanSearcher beanSearcher;

    @Mapping("/employees")
    public SearchResult<Employee> employees() {
        // 分页查询员工信息，并对年龄进行统计
        return beanSearcher.search(Employee.class, Employee::getAge);
    }

}
```

### 总结

- [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 设计的目标并不是替代某个 ORM 框架，它只是为了弥补现有 ORM 框架在复杂列表检索中的不便，实际项目中，配合使用它们，效果或会更好。
- 本例只是 Bean Searcher 在联表检索中的一个简单的演示，更多用法，请参阅：[https://bs.zhxu.cn](https://bs.zhxu.cn)
- 看完这些，大家有没有觉得 Bean Searcher 正好可以帮到你呢？如果是，就点个 Star 吧 ^_^
