# Bean Searcher 前端工程

[Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 演示项目的统一前端，基于 Vue 3 + Vite + TypeScript + Ant Design Vue 4 构建。

### 技术栈

- Vue 3.5（Composition API + `<script setup>`）
- Vite 6
- TypeScript 5.7
- Ant Design Vue 4.2（按需自动导入）
- 原生 `fetch`（无 axios）

### 快速开始

```bash
cd frontend-vue
npm install
npm run dev
```

开发服务器启动在 `http://localhost:7300`。

### 环境配置

后端 API 地址通过 `.env` 文件配置：

```env
VITE_APP_TITLE=Bean Searcher 演示程序
VITE_API_BASE=http://localhost:8080
```

切换后端服务时，只需修改 `VITE_API_BASE` 指向对应的后端地址即可。

> **注意**：由于前后端分离，前端（7300）与后端（8080）属于跨域请求，所有后端均已配置 CORS 允许跨域访问。

### 构建

```bash
npm run build
```

构建产物输出到 `dist/` 目录，可部署到任意静态文件服务器。

### 项目结构

```
src/
├── api/
│   └── employee.ts            # API 请求（fetch）+ 查询参数构建
├── components/
│   ├── DataTable.vue          # 数据表格（a-table）
│   ├── FilterCard.vue         # 检索条件表单
│   ├── FooterBanner.vue       # 页脚横幅
│   ├── HeroBanner.vue         # 顶部标题
│   └── StatsCards.vue         # 统计卡片
├── types/
│   └── employee.ts            # 类型定义与常量（操作符、列定义）
├── styles/
│   └── global.css             # 全局样式
├── App.vue                    # 主页面（组装各组件）
└── main.ts                    # 入口
```
