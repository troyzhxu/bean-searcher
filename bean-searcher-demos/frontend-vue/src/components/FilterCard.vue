<template>
  <section class="card filter-card">
    <div class="card-header" @click="toggleOpen" style="cursor: pointer">
      <span class="card-icon">🎯</span>
      <h2>检索条件</h2>
      <a-button size="small" type="default" @click.stop="toggleOpen">
        {{ open ? '收起 ▲' : '展开 ▼' }}
      </a-button>
    </div>

    <div v-show="open" class="filter-body">
      <div class="filter-grid">
        <!-- 姓名 -->
        <div class="filter-row">
          <label>姓名</label>
          <a-select v-model:value="localParams['name-op']" class="ctl">
            <a-select-option v-for="o in nameOps" :key="o.key" :value="o.key">{{ o.label }}</a-select-option>
          </a-select>
          <a-input v-model:value="localParams.name" placeholder="请输入姓名" class="ctl" />
          <a-checkbox v-model:checked="localParams['name-ic']" class="ctl-check">忽略大小写</a-checkbox>
        </div>

        <!-- 年龄 -->
        <div class="filter-row">
          <label>年龄</label>
          <a-select v-model:value="localParams['age-op']" class="ctl">
            <a-select-option v-for="o in numOps" :key="o.key" :value="o.key">{{ o.label }}</a-select-option>
          </a-select>
          <a-input-number v-model:value="localParams['age-0']" placeholder="值" class="ctl" />
          <a-input-number
            v-if="localParams['age-op'] === 'bt'"
            v-model:value="localParams['age-1']"
            placeholder="结束值"
            class="ctl"
          />
        </div>

        <!-- 部门 -->
        <div class="filter-row">
          <label>部门</label>
          <a-select v-model:value="localParams['department-op']" class="ctl">
            <a-select-option v-for="o in strOps" :key="o.key" :value="o.key">{{ o.label }}</a-select-option>
          </a-select>
          <a-input v-model:value="localParams.department" placeholder="请输入部门" class="ctl" />
          <a-checkbox v-model:checked="localParams['department-ic']" class="ctl-check">忽略大小写</a-checkbox>
        </div>

        <!-- 入职日期 -->
        <div class="filter-row">
          <label>入职日期</label>
          <a-select v-model:value="localParams['entryDate-op']" class="ctl">
            <a-select-option v-for="o in timeOps" :key="o.key" :value="o.key">{{ o.label }}</a-select-option>
          </a-select>
          <input type="date" v-model="localParams['entryDate-0']" class="date-input" />
          <input
            v-if="localParams['entryDate-op'] === 'bt'"
            type="date"
            v-model="localParams['entryDate-1']"
            class="date-input"
          />
        </div>
      </div>

      <div class="filter-actions">
        <a-button type="primary" @click="$emit('search')">⚡ 检索</a-button>
        <a-button @click="$emit('export')">📥 导出 CSV</a-button>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { EmployeeSearchParams } from '@/types/employee'
import { nameOps, strOps, numOps, timeOps } from '@/types/employee'

const props = defineProps<{
  params: EmployeeSearchParams
}>()

defineEmits<{
  search: []
  export: []
}>()

const open = ref(true)
const localParams = reactive<EmployeeSearchParams>({ ...props.params })

watch(() => props.params, (val) => Object.assign(localParams, val), { deep: true })
watch(localParams, (val) => Object.assign(props.params, val), { deep: true })

function toggleOpen() {
  open.value = !open.value
}
</script>

<style scoped>
/* ===== Card ===== */
.card {
  background: var(--surface);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  margin-bottom: 20px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}
.card:hover { box-shadow: var(--shadow-lg); }

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 24px;
  border-bottom: 1px solid var(--border);
}
.card-icon { font-size: 18px; }
.card-header h2 { font-size: 15px; font-weight: 700; flex: 1; }

/* ===== Filter body ===== */
.filter-body { padding: 20px 24px; }

.filter-grid { display: flex; flex-direction: column; gap: 14px; }

/* ===== Filter row：CSS Grid 对齐控件 ===== */
.filter-row {
  display: grid;
  grid-template-columns: 72px 130px 1fr 1fr;
  align-items: center;
  gap: 12px;
  min-height: 32px;
}

/* 第二行年龄：两个固定宽度的 input-number */
.filter-row:has(.ant-input-number) {
  grid-template-columns: 72px 130px 110px 110px;
}

/* 第四行入职日期：两个 1fr 让日期自适应填满右侧 */
.filter-row:has(.date-input) {
  grid-template-columns: 72px 130px 1fr 1fr;
}

.filter-row > label:first-child {
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
  text-align: right;
}

/* ===== 控件统一样式：class="ctl" ===== */
.ctl,
.ctl :deep(.ant-select-selector),
.ctl :deep(.ant-input),
.ctl :deep(.ant-input-number-input) {
  height: 32px;
}

.ctl :deep(.ant-select-selector) {
  padding: 0 11px;
  border-radius: 6px;
}
.ctl :deep(.ant-input),
.ctl :deep(.ant-input-number-input) {
  padding: 0 11px;
  border-radius: 6px;
  font-size: 14px;
}

/* 让 a-select 占满网格单元（虽然外层 .ctl 已经是块级） */
.ctl {
  width: 100%;
}

/* checkbox 在网格中垂直居中 */
.ctl-check {
  display: inline-flex;
  align-items: center;
  padding-left: 4px;
  font-size: 13px;
  color: var(--text-secondary);
}

/* ===== 原生日期输入：与 Ant 控件高度对齐 ===== */
.date-input {
  height: 32px;
  width: 100%;
  padding: 0 11px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  outline: none;
  background: #fff;
  color: var(--text);
  transition: border-color 0.2s, box-shadow 0.2s;
}
.date-input:hover { border-color: #5b5af7; }
.date-input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px var(--primary-bg);
}

/* ===== 底部按钮 ===== */
.filter-actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid var(--border);
}

/* ===== 响应式：平板 ===== */
@media (max-width: 768px) {
  .card-header { padding: 14px 16px; }
  .filter-body { padding: 16px; }
  .filter-row,
  .filter-row:has(.ant-input-number),
  .filter-row:has(.date-input) {
    grid-template-columns: 60px 120px 1fr 1fr;
    gap: 8px;
  }
  .filter-row > label:first-child { font-size: 12px; }
  .filter-actions { padding-top: 14px; margin-top: 14px; }
}

/* ===== 响应式：手机（变成单列） ===== */
@media (max-width: 480px) {
  .card-header { padding: 12px 14px; }
  .card-header h2 { font-size: 14px; }
  .filter-body { padding: 12px 14px; }
  .filter-row,
  .filter-row:has(.ant-input-number),
  .filter-row:has(.date-input) {
    grid-template-columns: 1fr;
    gap: 6px;
  }
  .filter-row > label:first-child {
    text-align: left;
    width: 100%;
    margin-bottom: 2px;
  }
  .filter-actions { padding-top: 12px; margin-top: 12px; flex-wrap: wrap; }
}
</style>
