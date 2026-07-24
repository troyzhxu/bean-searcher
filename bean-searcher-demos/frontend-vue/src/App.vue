<template>
  <div class="employee-search">
    <HeroBanner />

    <div class="container">
      <FilterCard
        :params="params"
        @search="handleFilter"
        @export="handleExport"
      />

      <StatsCards
        :sum-age="sumAge"
        :total="total"
      />

      <DataTable
        :data="list"
        :loading="loading"
        :total="total"
        :page="params.page"
        :page-size="params.size"
        :sort="params.sort"
        :order="params.order"
        @change="handleTableChange"
      />
    </div>

    <FooterBanner />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { Employee } from '@/types/employee'
import { defaultSearchParams } from '@/types/employee'
import { searchEmployees, getExportUrl } from '@/api/employee'
import HeroBanner from '@/components/HeroBanner.vue'
import FilterCard from '@/components/FilterCard.vue'
import StatsCards from '@/components/StatsCards.vue'
import DataTable from '@/components/DataTable.vue'
import FooterBanner from '@/components/FooterBanner.vue'

const list = ref<Employee[]>([])
const total = ref(0)
const sumAge = ref(0)
const loading = ref(false)

const params = reactive(defaultSearchParams())

async function loadData() {
  loading.value = true
  try {
    const data = await searchEmployees(params)
    list.value = data.dataList
    total.value = data.totalCount
    sumAge.value = data.summaries?.[0] ?? 0
  } catch (err) {
    console.error('检索失败:', err)
    list.value = []
    total.value = 0
    sumAge.value = 0
  } finally {
    loading.value = false
  }
}

function handleFilter() {
  params.page = 0
  loadData()
}

function handleExport() {
  window.open(getExportUrl(params), '_blank')
}

function handleTableChange(payload: { page?: number; pageSize?: number; sort?: string | null; order?: string | null }) {
  if (payload.page !== undefined) params.page = payload.page
  if (payload.pageSize !== undefined) {
    params.page = 0
    params.size = payload.pageSize
  }
  if (payload.sort !== undefined) params.sort = payload.sort
  if (payload.order !== undefined) params.order = payload.order
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.employee-search {
  min-height: 100vh;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

@media (max-width: 768px) {
  .container {
    padding: 0 12px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 8px;
  }
}
</style>
