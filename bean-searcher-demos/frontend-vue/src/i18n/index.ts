import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN'
import enUS from './locales/en-US'

export type Locale = 'zh-CN' | 'en-US'

const STORAGE_KEY = 'app-locale'

function getDefaultLocale(): Locale {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (stored === 'zh-CN' || stored === 'en-US') {
    return stored
  }
  // 根据浏览器语言推测
  return navigator.language.startsWith('zh')
    ? 'zh-CN'
    : 'en-US'
}

const i18n = createI18n({
  legacy: false,
  locale: getDefaultLocale(),
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
  },
  missingWarn: false,
  fallbackWarn: false,
})

/** 切换语言，返回切换后的 locale */
export function switchLocale(locale?: Locale): string {
  const current = i18n.global.locale.value as Locale
  const next: Locale = locale || (current === 'zh-CN' ? 'en-US' : 'zh-CN')
  i18n.global.locale.value = next
  localStorage.setItem(STORAGE_KEY, next)
  document.documentElement.lang = next
  return next
}

/** 获取当前 locale */
export function getCurrentLocale(): string {
  return i18n.global.locale.value
}

export default i18n
