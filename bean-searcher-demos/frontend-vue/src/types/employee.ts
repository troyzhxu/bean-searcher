/** 员工实体 */
export interface Employee {
  id: number
  name: string
  age: number
  gender: string
  department: string
  entryDate: string
}

/** Bean Searcher 检索参数 */
export interface EmployeeSearchParams {
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
  dataList: Employee[]
  totalCount: number
  summaries: number[]
}

/** 操作符选项 */
export interface OperatorOption {
  key: string
  label: string
}

/** 默认检索参数 */
export function defaultSearchParams(): EmployeeSearchParams {
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

/** 字符串操作符 */
export const strOps: OperatorOption[] = [
  { key: 'eq', label: '等于' },
  { key: 'in', label: '包含' },
  { key: 'sw', label: '以...开始' },
  { key: 'ew', label: '以...结束' },
]

/** 姓名操作符 */
export const nameOps: OperatorOption[] = [
  { key: 'eq', label: '等于' },
  { key: 'in', label: '包含' },
  { key: 'sw', label: '以...开始' },
  { key: 'ew', label: '以...结束' },
]

/** 数字操作符 */
export const numOps: OperatorOption[] = [
  { key: 'eq', label: '等于' },
  { key: 'gt', label: '大于' },
  { key: 'lt', label: '小于' },
  { key: 'ge', label: '大于等于' },
  { key: 'le', label: '小于等于' },
  { key: 'bt', label: '区间' },
]

/** 时间操作符 */
export const timeOps: OperatorOption[] = [
  { key: 'bt', label: '区间' },
  { key: 'gt', label: '大于' },
  { key: 'lt', label: '小于' },
  { key: 'ge', label: '大于等于' },
  { key: 'le', label: '小于等于' },
]

/** 表格列定义 */
export const tableColumns = [
  { key: 'id', title: 'ID' },
  { key: 'name', title: '姓名' },
  { key: 'age', title: '年龄' },
  { key: 'gender', title: '性别' },
  { key: 'department', title: '部门' },
  { key: 'entryDate', title: '入职时间' },
] as const
