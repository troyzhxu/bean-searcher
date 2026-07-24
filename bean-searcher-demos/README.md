# Bean Searcher 演示项目

本项目采用前后端分离架构，包含一个统一的前端工程和多个后端服务案例。

## 前端工程

* [frontend-vue](./frontend-vue)
  - Vue 3 + Vite + TypeScript + Ant Design Vue
  - 统一的前端页面，对接以下任意后端服务
  - 开发端口 7300，通过 `.env` 中的 `VITE_API_BASE` 配置后端 API 地址

## 后端服务

* [backend-springboot4](./backend-springboot4)（SpringBoot 4.x）
  - 使用 H2 数据库，无需任何配置即可启动体验
  - JDK 21+
* [backend-springboot3](./backend-springboot3)（SpringBoot 3.x）
  - 使用 H2 数据库，无需任何配置即可启动体验
  - JDK 17+
* [backend-springboot2](./backend-springboot2)（SpringBoot 2.x）
  - 使用 H2 数据库，无需任何配置即可启动体验
  - JDK 8+
* [backend-solon4](./backend-solon3)（Solon 4.x）
  - 使用 H2 数据库，无需任何配置即可启动体验
  - JDK 17+
* [backend-solon3](./backend-solon3)（Solon 3.x）
  - 使用 H2 数据库，无需任何配置即可启动体验
  - JDK 8+
