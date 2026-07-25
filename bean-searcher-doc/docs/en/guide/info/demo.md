---
title: Bean Searcher Quick Demo | Try It Online & Locally
description: "Experience Bean Searcher's declarative search without installing anything. Also supports local setup in three steps: clone → start backend → start frontend."
head:
  - - meta
    - property: og:title
      content: Bean Searcher Quick Demo - Try It Now
  - - meta
    - property: og:description
      content: No installation, no configuration. Try declarative search — pagination, filtering, sorting, stats — all in one line of code.
---
# Quick Demo

## Live Demo

> 🚀 **Try it online**: [https://demo-bs.zhxu.cn/](https://demo-bs.zhxu.cn/) — no deployment needed!

## Local Demo

Want to run it locally? Just three steps:

### Step 1: Clone

::: code-group
```bash [Gitee]
git clone https://gitee.com/troyzhxu/bean-searcher.git
```
```bash [Github]
git clone https://github.com/troyzhxu/bean-searcher.git
```
:::

### Step 2: Start a Backend (choose one)

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
# Open and run with IDEA
```
```bash [Solon3]
# JDK 8+
cd bean-searcher/bean-searcher-demos/backend-solon3
# Open and run with IDEA
```
:::

### Step 3: Start Frontend & See Result

```bash
cd bean-searcher/bean-searcher-demos/frontend-vue
npm install && npm run dev
```

Open `http://localhost:7300` to view the demo.

> This demo follows a frontend-backend separated architecture: one unified frontend + multiple backend services with fully consistent APIs. All backends use H2 in-memory database, zero configuration required.

[More DEMOs](https://github.com/troyzhxu/bean-searcher/tree/main/bean-searcher-demos)
