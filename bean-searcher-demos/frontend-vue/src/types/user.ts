/** 用户实体 */
export interface User {
  id: number
  name: string
  age: number
  gender: string
  department: string
  entryDate: string
}

/** Bean Searcher 检索参数 */
export interface UserSearchParams {
  name: string | null
  'name-op': string
  'name-ic': boolean
  'age-0': number | null
  'age-1': number | null
  'age-op': string
  department: string | null
  'department-op': string
  'department-ic': boolean
  'entryDate-0': string | null
  'entryDate-1': string | null
  'entryDate-op': string
  sort: string | null
  order: string | null
  page: number
  size: number
}

/** 检索 API 响应 */
export interface SearchResponse {
  dataList: User[]
  totalCount: number
  summaries: number[]
}

/** 默认检索参数 */
export function defaultSearchParams(): UserSearchParams {
  return {
    name: null,
    'name-op': 'in',
    'name-ic': false,
    'age-0': null,
    'age-1': null,
    'age-op': 'eq',
    department: null,
    'department-op': 'in',
    'department-ic': false,
    'entryDate-0': null,
    'entryDate-1': null,
    'entryDate-op': 'bt',
    sort: null,
    order: null,
    page: 0,
    size: 5,
  }
}

/** 字符串操作符 key 列表 */
export const strOpKeys = ['eq', 'in', 'sw', 'ew'] as const

/** 姓名操作符 key 列表 */
export const nameOpKeys = ['eq', 'in', 'sw', 'ew'] as const

/** 数字操作符 key 列表 */
export const numOpKeys = ['eq', 'gt', 'lt', 'ge', 'le', 'bt'] as const

/** 时间操作符 key 列表 */
export const timeOpKeys = ['bt', 'gt', 'lt', 'ge', 'le'] as const
