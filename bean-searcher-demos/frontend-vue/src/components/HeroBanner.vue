<template>
  <div class="hero">
    <div class="hero-bg"></div>
    <div class="hero-content">
      <h1>🔍 高级列表检索演示</h1>
      <p>一行代码，实现复杂条件组合过滤、排序、分页与统计</p>
      <a href="https://bs.zhxu.cn/" target="_blank" rel="noopener noreferrer" class="hero-badge">Bean Searcher - ReadOnly ORM</a>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.hero {
  position: relative;
  padding: 30px 0;
  text-align: center;
  overflow: hidden;
  margin-bottom: 30px;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #5b5af7 0%, #a78bfa 50%, #7c3aed 100%);
  opacity: 0.06;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-badge {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 20px;
  background: var(--primary-bg);
  color: var(--primary);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  margin-top: 8px;
  cursor: pointer;
  text-decoration: none;
  transition: box-shadow .3s, transform .3s;
}
.hero-badge:hover {
  box-shadow: 0 4px 16px rgba(91,90,247,.25);
  transform: translateY(-1px);
}

.hero h1 {
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, #5b5af7, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.hero p {
  color: var(--text-secondary);
  font-size: 15px;
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 22px;
  }
  .hero p {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .hero {
    padding: 20px 0;
    margin-bottom: 20px;
  }
  .hero h1 {
    font-size: 18px;
  }
  .hero p {
    font-size: 12px;
  }
  .hero-badge {
    font-size: 10px;
    padding: 3px 10px;
  }
}
</style>
