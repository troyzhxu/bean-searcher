# Bean Searcher 对比 Demo —— MyBatis 后端实现

本项目是从 [`../backend-springboot4`](../backend-springboot4)（Bean Searcher 实现）**复制而来**，用 **MyBatis** 重写了相同功能的后端，目的是对比两种方案在实现成本上的差异。

前端统一使用 [`../frontend-vue`](../frontend-vue)，两者共用同一套接口契约，可随时切换后端观察效果。

### 技术栈

- Web 框架：SpringBoot 4.1.0
- 数据库：H2（文件型，无需安装配置）
- ORM：**MyBatis**（`mybatis-spring-boot-starter:4.0.1`）
- JDK：17+

### 快速开始

```bash
cd backend-vs-mybatis
./gradlew bootRun
```

启动后监听 `http://localhost:8080`，数据库自动建表并初始化种子数据（见 `src/main/resources/db/`）。

### API 接口（与 Bean Searcher 版完全一致）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/index` | 分页检索用户（多条件过滤、排序、年龄统计） |
| GET | `/user/export` | 导出 CSV 文件（分批流式输出） |

请求参数沿用 Bean Searcher 的命名约定（前端 `frontend-vue` 即按此发送）：

| 参数 | 含义 |
|------|------|
| `name` / `name-op` / `name-ic` | 姓名 + 操作符(eq/in/sw/ew) + 忽略大小写 |
| `age-0` / `age-1` / `age-op` | 年龄区间(eq/gt/lt/ge/le/bt) |
| `department` / `department-op` / `department-ic` | 部门 + 操作符 + 忽略大小写 |
| `entryDate-0` / `entryDate-1` / `entryDate-op` | 入职日期区间(bt/gt/lt/ge/le) |
| `sort` / `order` | 排序字段 / 方向(asc/desc) |
| `page` / `size` | 页码(从 0 开始) / 每页条数 |

响应结构（前端消费契约）：

```json
{
  "dataList": [ { "id": 1, "name": "Jack", "age": 22, "gender": "Male", "genderName": "男", "department": "Finance", "entryDate": "2019-06-23 12:01" } ],
  "totalCount": 20,
  "summaries": [ 532 ]
}
```

> `summaries[0]` 为年龄求和，对应 Bean Searcher `search(User.class, User::getAge)` 的 summaries。

### 前端对接

```bash
cd ../frontend-vue
npm run dev
```

前端运行在 `http://localhost:7300`，通过 `.env` 中的 `VITE_API_BASE=http://localhost:8080` 连接本服务。

### 与 Bean Searcher 的对比

**Bean Searcher 版（`backend-springboot4`）的核心代码只需一行：**

```java
@GetMapping("/index")
public SearchResult<User> index() {
    // 组合检索、排序、分页、统计 全在这一句
    return beanSearcher.search(User.class, User::getAge);
}
```

实体类用注解声明多表映射与枚举标签：

```java
@SearchBean(tables = "users u, dept d", where = "u.dept_id = d.id", autoMapTo = "u")
public class User {
    @DbField("d.name")  private String department;
    @LabelFor("gender") private String genderName;   // 枚举 → 中文
    @Export(name = "入职时间", format = "yyyy-MM-dd HH:mm")
    private LocalDateTime entryDate;
}
```

**MyBatis 版需要手写的部分：**

| 关注点 | Bean Searcher | MyBatis 版 |
|--------|--------------|-----------|
| 参数解析 | 框架自动（ReqParamFilter + 内建解析） | `UserController.buildQuery()` 手动解析 `name-op`/`age-0` 等 |
| 检索 SQL | 框架自动生成 | `UserMapper.xml` 手写动态 SQL（`<if>`/`<choose>`/联表/排序/分页） |
| 分页 | 框架自动 | `LIMIT #{size} OFFSET #{offset}` 手动拼 |
| 计数 | 框架自动 | 单独的 `count` 查询 |
| 统计(求和) | `User::getAge` 一句话 | 单独的 `sumAge` 查询（`COALESCE(SUM(age),0)`） |
| 枚举标签 | `@LabelFor` 注解 | `Gender` 枚举 + `GenderTypeHandler`（数据库 `Male/Female` ↔ 枚举）+ `getGenderName()` 派生中文 |
| 多表字段 | `@DbField("d.name")` | SQL `d.name AS department` |
| CSV 导出 | `beanExporter.export(...)` 自带分批流式 | `UserController.export()` 手写流式 + 分批刷新 |

直观对比，同样是「多条件过滤 + 排序 + 分页 + 统计 + 导出」，`backend-springboot4` 的 `UserController` 仅约 15 行，而本项目的 `UserController` + `UserMapper` + `UserMapper.xml` 合计约 200+ 行——这正是 Bean Searcher 为开发者省掉的样板代码。

### 代码分析（MyBatis 版关键文件）

- `src/main/java/com/example/UserController.java`：参数解析 + 三查组装 + CSV 流式导出
- `src/main/java/com/example/mapper/UserMapper.java`：Mapper 接口（search / count / sumAge）
- `src/main/resources/mapper/UserMapper.xml`：动态 SQL（复刻 Bean Searcher 参数语义）
- `src/main/java/com/example/User.java`：普通 POJO 实体（去掉了 `@SearchBean` 等注解），`gender` 为 `Gender` 枚举
- `src/main/java/com/example/Gender.java`：性别枚举（`Male("男")` / `Female("女")`）
- `src/main/java/com/example/GenderTypeHandler.java`：枚举 <-> 数据库 varchar 的类型处理器（等价 `@LabelFor` 的自动转换）
- `src/main/resources/application.properties`：MyBatis 与数据源配置

### 注意事项

- 日期比较使用 H2 的 `TIMESTAMP` 转型（如 `u.entry_date >= TIMESTAMP #{entryDate0}`），若切换数据库需相应调整。
- 导出时的分批延迟（每 5 条休眠 500ms）是为了模拟大数据量导出、让前端立即开始下载，与 Bean Searcher 版的 `batch-delay` 行为一致。
- 本机需 **JDK 17+** 才能构建运行（Spring Boot 4 要求）。

### 更多信息

- [Bean Searcher 文档](https://bs.zhxu.cn)
- [MyBatis 文档](https://mybatis.org/mybatis-3/zh/index.html)
- [更多 Demo](../)
