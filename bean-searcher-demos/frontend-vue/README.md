# Bean Searcher 前端工程

### 介绍

本项目是 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 演示项目的前端工程，基于 Vue 3 + Vite + TypeScript + Ant Design Vue 构建。

### 技术栈

- Vue 3.5（Composition API + `<script setup>`）
- Vite 6
- TypeScript 5.7
- Ant Design Vue 4.2
- 原生 fetch（无 axios）

### 环境配置

后端 API 地址通过 `.env` 文件配置：

```env
VITE_APP_TITLE=Bean Searcher 演示程序
VITE_API_BASE=http://localhost:8080
```

切换后端服务时，只需修改 `VITE_API_BASE` 指向对应的后端地址即可。

> **注意**：由于采用前后端分离架构，前端（7300）与后端（8080）属于跨域请求，后端需配置 CORS 允许跨域访问。

### 运行

```bash
npm install
npm run dev
```

开发服务器启动在 `http://localhost:7300`。

### 构建

```bash
npm run build
```

构建产物输出到 `dist/` 目录。

### 项目结构

```
src/
├── api/employee.ts          # API 请求（fetch）
├── components/
│   ├── HeroBanner.vue       # 顶部横幅
│   ├── FilterCard.vue       # 检索条件表单
│   ├── StatsCards.vue       # 统计卡片
│   ├── DataTable.vue        # 数据表格（ATable）
│   └── FooterBanner.vue     # 页脚
├── types/employee.ts        # 类型定义
├── views/EmployeeSearch.vue # 主页面
├── styles/global.css        # 全局样式
└── main.ts                  # 入口
```
