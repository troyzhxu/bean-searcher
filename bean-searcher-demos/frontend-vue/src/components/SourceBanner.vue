<template>
  <div class="source-banner">
    <p class="source-heading">📂 后端源码一览（同一接口，五种框架实现）</p>
    <div class="source-links">
      <a href="https://gitee.com/troyzhxu/bean-searcher/blob/main/bean-searcher-demos/backend-springboot4/src/main/java/com/example/UserController.java" target="_blank" class="source-link">
        <span class="source-badge sb">SB4</span>
        SpringBoot 4
      </a>
      <a href="https://gitee.com/troyzhxu/bean-searcher/blob/main/bean-searcher-demos/backend-springboot3/src/main/java/com/example/UserController.java" target="_blank" class="source-link">
        <span class="source-badge sb">SB3</span>
        SpringBoot 3
      </a>
      <a href="https://gitee.com/troyzhxu/bean-searcher/blob/main/bean-searcher-demos/backend-springboot2/src/main/java/com/example/UserController.java" target="_blank" class="source-link">
        <span class="source-badge sb">SB2</span>
        SpringBoot 2
      </a>
      <a href="https://gitee.com/troyzhxu/bean-searcher/blob/main/bean-searcher-demos/backend-solon4/src/main/java/com/example/demo/UserController.java" target="_blank" class="source-link">
        <span class="source-badge so">SO4</span>
        Solon 4
      </a>
      <a href="https://gitee.com/troyzhxu/bean-searcher/blob/main/bean-searcher-demos/backend-solon3/src/main/java/com/example/demo/UserController.java" target="_blank" class="source-link">
        <span class="source-badge so">SO3</span>
        Solon 3
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.source-banner {
  text-align: center;
  padding: 28px 24px 20px;
}

.source-heading {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.source-links {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
}

.source-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text);
  background: var(--surface);
  border: 1px solid var(--border);
  text-decoration: none;
  transition: border-color .2s, box-shadow .2s, transform .2s;
}

.source-link:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow);
  transform: translateY(-1px);
}

.source-badge {
  font-size: 11px;
  font-weight: 700;
  padding: 1px 5px;
  border-radius: 4px;
  letter-spacing: .3px;
}

.source-badge.sb {
  background: rgba(91,90,247,.12);
  color: var(--primary);
}

.source-badge.so {
  background: rgba(16,185,129,.12);
  color: #10b981;
}
</style>
