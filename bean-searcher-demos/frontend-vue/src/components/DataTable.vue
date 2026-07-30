<template>
  <section class="card table-card">
    <div class="card-header">
      <span class="card-icon">📄</span>
      <h2>{{ $t('table.title') }}</h2>
      <span class="card-badge" v-if="total > 0">{{ $t('table.totalCount', { total }) }}</span>
    </div>

    <div class="table-body">
      <a-table
        :columns="columns"
        :data-source="data"
        :loading="loading"
        :pagination="paginationConfig"
        :row-key="(r: User) => r.id"
        size="middle"
        @change="handleChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'id'">
            <a-tag color="purple">{{ record.id }}</a-tag>
          </template>
          <template v-else-if="column.key === 'entryDate'">
            <span class="date-cell">{{ record.entryDate }}</span>
          </template>
        </template>
      </a-table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import type { User } from '@/types/user'

const { t } = useI18n()

const props = defineProps<{
  data: User[]
  loading: boolean
  total: number
  page: number
  pageSize: number
  sort: string | null
  order: string | null
}>()

const emit = defineEmits<{
  change: [payload: { page?: number; pageSize?: number; sort?: string | null; order?: string | null }]
}>()

const columns = computed(() => [
  { title: t('table.colId'), dataIndex: 'id', key: 'id', sorter: true, width: 80, sortOrder: sortOrderFor('id') },
  { title: t('table.colName'), dataIndex: 'name', key: 'name', sorter: true, sortOrder: sortOrderFor('name') },
  { title: t('table.colAge'), dataIndex: 'age', key: 'age', sorter: true, width: 80, sortOrder: sortOrderFor('age') },
  { title: t('table.colGender'), dataIndex: 'gender', key: 'gender', sorter: true, width: 80, sortOrder: sortOrderFor('gender') },
  { title: t('table.colDepartment'), dataIndex: 'department', key: 'department', sorter: true, sortOrder: sortOrderFor('department') },
  { title: t('table.colEntryDate'), dataIndex: 'entryDate', key: 'entryDate', sorter: true, width: 120, sortOrder: sortOrderFor('entryDate') },
])

function sortOrderFor(field: string): 'ascend' | 'descend' | null {
  if (props.sort !== field) return null
  return props.order === 'asc' ? 'ascend' : 'descend'
}

const paginationConfig = computed(() => ({
  current: props.page + 1,
  pageSize: props.pageSize,
  total: props.total,
  showSizeChanger: true,
  pageSizeOptions: ['5', '10', '15'],
  showTotal: (total: number) => t('table.totalCount', { total }),
  size: 'small' as const,
}))

function handleChange(pag: any, _filters: any, sorter: any) {
  const payload: { page?: number; pageSize?: number; sort?: string | null; order?: string | null } = {}
  const newPage = pag.current - 1
  const newSize = pag.pageSize

  if (newPage !== props.page) payload.page = newPage
  if (newSize !== props.pageSize) payload.pageSize = newSize

  const newSort = sorter.order ? (sorter.field || sorter.columnKey) : null
  const newOrder = sorter.order === 'ascend' ? 'asc' : sorter.order === 'descend' ? 'desc' : null
  if (newSort !== props.sort || newOrder !== props.order) {
    payload.sort = newSort
    payload.order = newOrder
  }

  if (Object.keys(payload).length > 0) emit('change', payload)
}
</script>

<style scoped>
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
.card-badge {
  font-size: 12px; color: var(--text-secondary);
  background: var(--bg); padding: 3px 10px; border-radius: 12px;
}
.table-card { padding-bottom: 0; }

.table-body {
  padding: 0 24px 16px;
}

.date-cell { color: var(--text-secondary); font-size: 12px; }
</style>
