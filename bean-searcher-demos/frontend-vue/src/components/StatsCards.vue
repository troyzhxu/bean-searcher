<template>
  <div class="stats-row">
    <div class="stat-card">
      <div class="stat-icon">📊</div>
      <div class="stat-body">
        <span class="stat-label">总年龄</span>
        <span class="stat-value">{{ sumAge }}</span>
      </div>
    </div>
    <div class="stat-card">
      <div class="stat-icon">📈</div>
      <div class="stat-body">
        <span class="stat-label">平均年龄</span>
        <span class="stat-value">{{ avgAge }}</span>
      </div>
    </div>
    <div class="stat-card">
      <div class="stat-icon">📋</div>
      <div class="stat-body">
        <span class="stat-label">总条数</span>
        <span class="stat-value">{{ total }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  sumAge: number
  total: number
}>()

const avgAge = computed(() => {
  if (props.sumAge && props.total) {
    return (props.sumAge / props.total).toFixed(1)
  }
  return '—'
})
</script>

<style scoped>
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: var(--surface);
  border-radius: var(--radius);
  padding: 20px 22px;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s;
}

.stat-card:hover {
  box-shadow: var(--shadow);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 28px;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: var(--primary-bg);
  flex-shrink: 0;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
  letter-spacing: 0.3px;
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  color: var(--primary);
  letter-spacing: -0.5px;
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  .stat-card {
    padding: 14px 16px;
  }
  .stat-value {
    font-size: 22px;
  }
  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 22px;
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  .stat-card {
    padding: 12px 14px;
  }
}
</style>
