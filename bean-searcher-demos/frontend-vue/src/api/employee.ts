import type { EmployeeSearchParams, SearchResponse } from '@/types/employee'

const API_BASE = import.meta.env.VITE_API_BASE || ''

/**
 * 将检索参数转为 URL 查询字符串
 * 过滤 null 值和 false 布尔值
 */
function paramsToQueryString(params: EmployeeSearchParams): string {
  const sp = new URLSearchParams()
  for (const [key, value] of Object.entries(params) as [string, unknown][]) {
    if (value == null) continue
    if (typeof value === 'boolean' && !value) continue
    sp.append(key, String(value))
  }
  return sp.toString()
}

/** 分页检索员工数据 */
export async function searchEmployees(params: EmployeeSearchParams): Promise<SearchResponse> {
  const qs = paramsToQueryString(params)
  const resp = await fetch(`${API_BASE}/user/index?${qs}`)
  return resp.json()
}

/** 获取 CSV 导出 URL */
export function getExportUrl(params: EmployeeSearchParams): string {
  const qs = paramsToQueryString(params)
  return `${API_BASE}/user/export?${qs}`
}
