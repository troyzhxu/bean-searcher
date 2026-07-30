<template>
  <section class="card filter-card">
    <div class="card-header" @click="toggleOpen" style="cursor: pointer">
      <span class="card-icon">🎯</span>
      <h2>{{ $t('filter.title') }}</h2>
      <a-button size="small" type="default" @click.stop="toggleOpen">
        {{ open ? $t('filter.collapse') : $t('filter.expand') }}
      </a-button>
    </div>

    <div v-show="open" class="filter-body">
      <div class="filter-grid">
        <!-- 姓名 -->
        <div class="filter-row">
          <label>{{ $t('filter.name') }}</label>
          <a-select v-model:value="localParams['name-op']" class="ctl">
            <a-select-option v-for="o in nameOps" :key="o.key" :value="o.key">{{ o.label }}</a-select-option>
          </a-select>
          <a-input v-model:value="localParams.name" :placeholder="$t('filter.namePlaceholder')" class="ctl" />
          <a-checkbox v-model:checked="localParams['name-ic']" class="ctl-check">{{ $t('filter.ignoreCase') }}</a-checkbox>
        </div>

        <!-- 年龄 -->
        <div class="filter-row">
          <label>{{ $t('filter.age') }}</label>
          <a-select v-model:value="localParams['age-op']" class="ctl">
            <a-select-option v-for="o in numOps" :key="o.key" :value="o.key">{{ o.label }}</a-select-option>
          </a-select>
          <a-input-number v-model:value="localParams['age-0']" :placeholder="$t('filter.agePlaceholder')" class="ctl" />
          <a-input-number
            v-if="localParams['age-op'] === 'bt'"
            v-model:value="localParams['age-1']"
            :placeholder="$t('filter.ageEndPlaceholder')"
            class="ctl"
          />
        </div>

        <!-- 部门 -->
        <div class="filter-row">
          <label>{{ $t('filter.department') }}</label>
          <a-select v-model:value="localParams['department-op']" class="ctl">
            <a-select-option v-for="o in strOps" :key="o.key" :value="o.key">{{ o.label }}</a-select-option>
          </a-select>
          <a-input v-model:value="localParams.department" :placeholder="$t('filter.departmentPlaceholder')" class="ctl" />
          <a-checkbox v-model:checked="localParams['department-ic']" class="ctl-check">{{ $t('filter.ignoreCase') }}</a-checkbox>
        </div>

        <!-- 入职日期 -->
        <div class="filter-row">
          <label>{{ $t('filter.entryDate') }}</label>
          <a-select v-model:value="localParams['entryDate-op']" class="ctl">
            <a-select-option v-for="o in timeOps" :key="o.key" :value="o.key">{{ o.label }}</a-select-option>
          </a-select>
          <a-date-picker v-model:value="localParams['entryDate-0']" value-format="YYYY-MM-DD" class="ctl" :placeholder="$t('filter.startDate')" />
          <a-date-picker
            v-if="localParams['entryDate-op'] === 'bt'"
            v-model:value="localParams['entryDate-1']"
            value-format="YYYY-MM-DD"
            class="ctl"
            :placeholder="$t('filter.endDate')"
          />
        </div>
      </div>

      <div class="filter-actions">
        <a-button type="primary" @click="$emit('search')">{{ $t('filter.search') }}</a-button>
        <a-button @click="$emit('export')">{{ $t('filter.export') }}</a-button>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import type { UserSearchParams } from '@/types/user'
import { nameOpKeys, strOpKeys, numOpKeys, timeOpKeys } from '@/types/user'

const { t } = useI18n()

const props = defineProps<{
  params: UserSearchParams
}>()

defineEmits<{
  search: []
  export: []
}>()

const open = ref(true)
const localParams = reactive<UserSearchParams>({ ...props.params })

watch(() => props.params, (val) => Object.assign(localParams, val), { deep: true })
watch(localParams, (val) => Object.assign(props.params, val), { deep: true })

function toggleOpen() {
  open.value = !open.value
}

const nameOps = computed(() => nameOpKeys.map(key => ({ key, label: t(`op.${key}`) })))
const strOps = computed(() => strOpKeys.map(key => ({ key, label: t(`op.${key}`) })))
const numOps = computed(() => numOpKeys.map(key => ({ key, label: t(`op.${key}`) })))
const timeOps = computed(() => timeOpKeys.map(key => ({ key, label: t(`op.${key}`) })))
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

/* 第二行年龄：与默认行保持同样的自适应宽度 */
.filter-row:has(.ant-input-number) {
  grid-template-columns: 72px 130px 1fr 1fr;
}

/* 第四行入职日期：两个 1fr 让日期自适应填满右侧 */
.filter-row:has(.ant-picker) {
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

/* 让 a-select / a-input-number 占满网格单元 */
.ctl { width: 100%; }
.ctl :deep(.ant-input-number) { width: 100%; }

/* checkbox 在网格中垂直居中 */
.ctl-check {
  display: inline-flex;
  align-items: center;
  padding-left: 4px;
  font-size: 13px;
  color: var(--text-secondary);
}

/* ===== a-date-picker：与 .ctl 一致的高度和边框 ===== */
.ctl.ant-picker {
  height: 32px;
  padding: 0 11px;
  border-radius: 6px;
}
.ctl.ant-picker :deep(.ant-picker-input > input) {
  font-size: 14px;
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
  .filter-row:has(.ant-picker) {
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
  .filter-row:has(.ant-picker) {
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
