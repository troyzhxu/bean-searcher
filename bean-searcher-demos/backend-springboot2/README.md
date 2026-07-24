# Bean Searcher SpringBoot 2 后端服务

### 介绍

本项目是 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 的 Spring Boot 2 后端示例服务，演示在 Web 工程中如何使用 Bean Searcher 简化列表检索的开发。

本工程为纯后端 API 服务，前端代码已分离至 `../frontend-vue` 目录，前后端独立部署。

### 软件架构

- Web 框架：Spring Boot 2
- 数据库：H2（无需安装配置）
- 数据库访问：spring-jdbc、bean-searcher

### API 接口

##### 员工检索接口

```
GET /employee/index
```

组合检索、排序、分页与统计均在同一个接口内完成。检索参数由 `config` 包下的 `AutoLoadParamFilter` 自动加载，无需在控制器中逐个声明。

### 配置说明

`config` 包下还提供了：

- `AutoLoadParamFilter` —— 自动加载检索参数过滤器
- `SlowSqlListener` —— 慢 SQL 监听器

### 运行方式

##### 环境要求

- JDK 8+

##### 启动后端

```bash
mvn spring-boot:run
```

##### 启动前端

前端项目位于 `../frontend-vue`，进入该目录后执行：

```bash
npm run dev
```

前端默认运行在 7300 端口，通过 `.env` 中的 `VITE_API_BASE` 配置后端 API 地址（默认 `http://localhost:8080`）。

### 总结

- [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 的目标不是替代某个 ORM 框架，而是弥补现有 ORM 框架在复杂列表检索中的不便，实际项目中配合使用效果更佳。
- 本例只是 Bean Searcher 在联表检索中的一个简单演示，更多用法请参阅：[https://bs.zhxu.cn](https://bs.zhxu.cn)
