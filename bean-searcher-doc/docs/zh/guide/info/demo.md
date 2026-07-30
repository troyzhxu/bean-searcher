---
title: Bean Searcher DEMO 极速体验 | 在线 + 本地，即刻感受声明式检索
description: 无需安装任何环境，在线体验 Bean Searcher 的声明式检索能力。也支持三步本地运行：克隆 → 启动后端 → 启动前端。
head:
  - - meta
    - property: og:title
      content: Bean Searcher DEMO 极速体验 - 即刻感受
  - - meta
    - property: og:description
      content: 无需安装、无需配置，在线体验一行代码实现分页、筛选、排序、统计的声明式检索能力。
---
# DEMO 极速体验

## 在线体验

> 🚀 **在线 Demo**：[https://demo-bs.zhxu.cn/](https://demo-bs.zhxu.cn/) — 无需部署，即刻体验！

## 本地体验

如果想在本地跑起来，三步即可：

### 第一步：克隆

::: code-group
```bash [Gitee]
git clone https://gitee.com/troyzhxu/bean-searcher.git
```
```bash [Github]
git clone https://github.com/troyzhxu/bean-searcher.git
```
:::

### 第二步：启动后端（任选一个）

::: code-group
```bash [SpringBoot4]
# JDK 21+
cd bean-searcher/bean-searcher-demos/backend-springboot4
./gradlew bootRun
```
```bash [SpringBoot3]
# JDK 17+
cd bean-searcher/bean-searcher-demos/backend-springboot3
./gradlew bootRun
```
```bash [SpringBoot2]
# JDK 8+
cd bean-searcher/bean-searcher-demos/backend-springboot2
mvn spring-boot:run
```
```bash [Solon4]
# JDK 17+
cd bean-searcher/bean-searcher-demos/backend-solon4
# IDEA 中打开运行
```
```bash [Solon3]
# JDK 8+
cd bean-searcher/bean-searcher-demos/backend-solon3
# IDEA 中打开运行
```
:::

### 第三步：启动前端 & 查看效果

```bash
cd bean-searcher/bean-searcher-demos/frontend-vue
npm install && npm run dev
```

访问 `http://localhost:7300` 即可查看运行效果。

> 此 Demo 采用前后端分离架构，一个统一前端 + 多个后端服务，API 接口完全一致，前端可无缝切换对接。所有后端均使用 H2 内存数据库，无需任何配置，开箱即用。

[更多 DEMO](https://github.com/troyzhxu/bean-searcher/tree/main/bean-searcher-demos)
