# Bean Searcher 对比 demo - JPA 后端实现

本项目是从 [`../backend-springboot4`](../backend-springboot4)（Bean Searcher 版）拷贝而来，目标是用 **Spring Data JPA** 重新实现相同的后端效果，从而直观对比「声明式检索框架」与「标准 ORM 手写」的差异。前端已分离至 [`../frontend-vue`](../frontend-vue)，三个后端（Bean Searcher / MyBatis / JPA）对前端**契约完全一致**，可无缝互换。

### 技术栈

- Web 框架：SpringBoot 4.1.0
- 数据库：H2（文件库，无需安装配置）
- ORM：Spring Data JPA + Hibernate
- JDK：17+

### 快速开始

```bash
cd backend-vs-jpa
./gradlew bootRun
```

启动后监听 `http://localhost:8080`，由 `db/schema.sql` + `db/data.sql` 自动建表并初始化种子数据（与 MyBatis 版同一份数据）。

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/index` | 分页检索员工（多条件过滤、排序、年龄统计） |
| GET | `/user/export` | 导出 CSV 文件 |

### 前端对接

```bash
cd ../frontend-vue
npm run dev
```

前端运行在 `http://localhost:7300`，通过 `.env` 中的 `VITE_API_BASE=http://localhost:8080` 连接本服务。

### 代码分析

检索接口在 Bean Searcher 版只需 **一行**：

```java
@GetMapping("/index")
public SearchResult<User> index() {
    // 组合检索、排序、分页 和 统计 都在这一句代码中实现
    return beanSearcher.search(User.class, User::getAge);
}
```

而 JPA 版需要手写「参数解析 → 动态条件 → 分页 → 排序 → 统计」：

```java
@GetMapping("/index")
public SearchResult index(@RequestParam Map<String, String> params) {
    Map<String, Object> query = buildQuery(params);          // 1) 解析 Bean Searcher 风格参数

    Specification<User> spec = (root, q, cb) ->              // 2) 动态条件
            cb.and(toPredicates(cb, root, query).toArray(new Predicate[0]));

    int page = (Integer) query.getOrDefault("page", 0);
    int size = (Integer) query.getOrDefault("size", 5);

    List<User> list = userRepository.findAll(spec,           // 3) 分页 + 排序
            PageRequest.of(page, size, buildSort(query))).getContent();
    long total = userRepository.count(spec);                 // 4) 总条数
    int sumAge = sumAge(query);                              // 5) 年龄求和（EntityManager Criteria 查询）

    return new SearchResult(list, total, List.of(sumAge));
}
```

要点：

- **`buildQuery`**：把前端传来的 `{field}-{suffix}` 风格参数（如 `name-op` / `age-0` / `department-ic`）解析成查询条件 Map。这一步在 Bean Searcher 中由框架（`ReqParamFilter` + 内建参数解析）自动完成。
- **`toPredicates`**：把解析后的条件翻译成 JPA `Predicate`，复刻 Bean Searcher 的 `eq / in / sw / ew / bt / gt / lt / ge / le` 等语义，含 `-ic` 忽略大小写。
- **`sumAge`**：用 `EntityManager` 的 Criteria 查询算 `SUM(age)`，等价于 Bean Searcher `search(User.class, User::getAge)` 的 `summaries[0]`。
- **导出**：`userRepository.findAll(spec)` 取全部匹配数据，分批写 CSV 并间隔刷新，模拟 Bean Searcher 流式导出的「边查边下」效果。

### 实体映射（对比 Bean Searcher 注解）

Bean Searcher 用注解声明多表映射与枚举标签，JPA 版用标准映射等价实现：

| Bean Searcher | JPA 版 | 说明 |
|---|---|---|
| `@SearchBean(tables="users u, dept d", where="u.dept_id=d.id")` | `User` 实体 + `@Formula("(SELECT d.name FROM dept d WHERE d.id = dept_id)")` | department 派生为只读列 |
| `@DbField("d.name")` | `@Formula(...)` 上的 `department` 字段 | 部门名 |
| `@LabelFor("gender")` | `getGenderName()` 取 `gender.getLabel()` | 性别中文名 |
| 枚举自动序列化 | `@Enumerated(EnumType.STRING) Gender gender` | 存储/输出 `Male`/`Female` |
| `@Export(format="yyyy-MM-dd HH:mm")` | `@JsonFormat(pattern="yyyy-MM-dd HH:mm")` | 入职时间格式 |

> 说明：`department` 用 `@Formula` 派生为只读列，而非 `@ManyToOne` 关联，这样既与 MyBatis 版的扁平 `User`（department 即字符串列）保持一致，又避免了实体关联带来的懒加载 / JOIN 复杂度。

### 与另外两个后端的对比

| 实现 | 检索写法 | 统计写法 | 动态条件 | 样板代码量 |
|---|---|---|---|---|
| Bean Searcher (`backend-springboot4`) | `beanSearcher.search(User.class, User::getAge)` 一行 | 同一行内置 | 框架自动 | 极少 |
| MyBatis (`backend-vs-mybatis`) | `UserMapper.search` + 手写 XML 动态 SQL | 三条 SQL | XML `<if>` | 多 |
| **JPA（本工程）** | `userRepository.findAll(spec, pageable)` | `count(spec)` + `EntityManager` Criteria 求和 | `Specification` / `Predicate` | 多 |

三个后端的响应结构均为 `{ dataList, totalCount, summaries: [年龄求和] }`，前端无需任何改动即可切换。

### 更多信息

- [Bean Searcher 文档](https://bs.zhxu.cn)
- [更多 Demo](../)
