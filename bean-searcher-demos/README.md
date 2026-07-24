# Bean Searcher 演示项目

前后端分离架构，一个前端工程 + 五个后端服务。所有后端共享相同的表结构与 API 接口，前端可无缝切换对接。

## 项目列表

| 项目 | 类型 | 框架 | JDK | 构建 |
|------|------|------|-----|------|
| [frontend-vue](./frontend-vue) | 前端 | Vue 3 + Vite + TypeScript + Ant Design Vue 4 | — | `npm run dev` |
| [backend-springboot4](./backend-springboot4) | 后端 | SpringBoot 4.x | 21+ | `./gradlew bootRun` |
| [backend-springboot3](./backend-springboot3) | 后端 | SpringBoot 3.x | 17+ | `./gradlew bootRun` |
| [backend-springboot2](./backend-springboot2) | 后端 | SpringBoot 2.x | 8+ | `mvn spring-boot:run` |
| [backend-solon4](./backend-solon4) | 后端 | Solon 4.x | 17+ | IDEA 运行 |
| [backend-solon3](./backend-solon3) | 后端 | Solon 3.x | 8+ | IDEA 运行 |

## 快速开始

### 1. 启动后端（任选一个）

```bash
# 以 SpringBoot 4 为例
cd backend-springboot4
./gradlew bootRun
```

后端启动后监听 `http://localhost:8080`，使用 H2 内存数据库，无需任何额外配置。

### 2. 启动前端

```bash
cd frontend-vue
npm install
npm run dev
```

前端运行在 `http://localhost:7300`，默认通过 `.env` 中的 `VITE_API_BASE=http://localhost:8080` 对接后端��

> **提示**：启动前端前，请确保至少有一个后端服务正在运行。

## API 接口

所有后端提供完全一致的两个接口：

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/index` | 分页检索员工（含多条件过滤、排序、年龄统计） |
| GET | `/user/export` | 导出 CSV 文件 |

## 核心亮点

每个后端的检索接口仅用了 **一行代码**：

```java
return beanSearcher.search(User.class, User::getAge);
```

就能同时完成：
- 🔍 复杂条件组合过滤
- 📊 多字段统计（年龄汇总）
- ⬆⬇ 后端排序（点击表头）
- 📄 分页查询
- 📥 CSV 流式导出
- 📋 总条数统计

前端不需要关心后端是什么框架、哪个 JDK 版本——接口完全一致，改一行 `.env` 即可切换。
