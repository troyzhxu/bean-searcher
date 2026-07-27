<template>
  <div>
    <Layout>
      <!-- <template #navbar-search>
        <div class="discuss">
          <span>交流群</span>
          <span class="button-arrow down" />
          <img src="/wx_discuss.png" />
        </div>
      </template> -->
      <template #sidebar-nav-before>
       <!--  <div class="aliyun-ad">
          <a href="https://www.aliyun.com/minisite/goods?userCode=zugtbi5w" target="_blank" >
          🧧 阿里云低至 1 折 🧧
          </a>
        </div> -->
      </template>
      <template #home-hero-after>
        <HomeSponsors style="text-align: center" />
      </template>
    </Layout>
    <div class="tb-widget" data-10bWorkId="1" data-theme="light"></div>
  </div>
</template>

<script setup lang="ts">

import { onMounted, ref, watch } from "vue"
import DefaultTheme from 'vitepress/theme'
import { useRoute } from 'vitepress'
import HomeSponsors from './HomeSponsors.vue'
const { Layout } = DefaultTheme
const route = useRoute()

const LOCALE_STORAGE_KEY = 'bs-locale'

// 记录上一次 SPA 导航时的语言，用于检测"用户是否手动点了语言切换按钮"
let prevLocale: string | null = null

// 仅在跨语言 SPA 跳转时更新 localStorage（点击导航栏语言切换按钮）
watch(() => route.path, (path) => {
  try {
    const newLocale = path.startsWith('/en/') ? 'en' : 'zh'
    if (prevLocale && prevLocale !== newLocale) {
      localStorage.setItem(LOCALE_STORAGE_KEY, newLocale)
    }
    prevLocale = newLocale
  } catch (_) {}
})

onMounted(() => {
  if (checkEnvAndSSL()) {
    baiduTongji();
    loadXsWidget();
  }
  // 初始化 prevLocale（用于 watch 的跨语言检测）
  prevLocale = window.location.pathname.startsWith('/en/') ? 'en' : 'zh'
  autoRedirectLocale();
  // JSON-LD 结构化数据
  injectJsonLd();
  console.log("\n%c Bean Searcher %c 你点 STAR 了没有 😎 ? \n", "color: #fff; background: #f1404b; padding:5px 0;", "background: #111; padding:5px 0; color: #fff");
  console.log('👉 https://github.com/troyzhxu/bean-searcher')
  console.log('👉 https://gitee.com/troyzhxu/bean-searcher')
})

function injectJsonLd() {
  const websiteSchema = {
    "@context": "https://schema.org",
    "@type": "WebSite",
    "name": "Bean Searcher 文档",
    "url": "https://bs.zhxu.cn",
    "description": "专注高级查询的只读 ORM，天生支持联表，免 DTO/VO 转换",
    "potentialAction": {
      "@type": "SearchAction",
      "target": "https://bs.zhxu.cn/?q={search_term_string}",
      "query-input": "required name=search_term_string"
    }
  }

  const softwareSchema = {
    "@context": "https://schema.org",
    "@type": "SoftwareApplication",
    "name": "Bean Searcher",
    "applicationCategory": "DeveloperApplication",
    "operatingSystem": "Cross-platform",
    "description": "专注高级查询的只读 ORM，天生支持联表，免 DTO/VO 转换，使一行代码实现复杂列表检索成为可能",
    "url": "https://github.com/troyzhxu/bean-searcher",
    "author": {
      "@type": "Person",
      "name": "周旭"
    }
  }

  const orgSchema = {
    "@context": "https://schema.org",
    "@type": "Organization",
    "name": "Bean Searcher",
    "url": "https://bs.zhxu.cn",
    "logo": "https://bs.zhxu.cn/logo.png"
  }

  const schemas = [websiteSchema, softwareSchema, orgSchema]
  schemas.forEach(schema => {
    const script = document.createElement('script')
    script.type = 'application/ld+json'
    script.textContent = JSON.stringify(schema)
    document.head.appendChild(script)
  })
}

function checkEnvAndSSL() {
  const path = location.href;
  const isSsl = path.indexOf('https') == 0;
  if (!isSsl && import.meta.env.PROD) {
    // 自动重定向
    location.href = 'https' + path.substr(4);
  }
  return isSsl || import.meta.env.DEV;
}

/**
 * 页面首次加载时执行的语言检测与重定向：
 *   1. localStorage 为空 → 浏览器语言检测，不匹配时跳转，匹配时记录偏好
 *   2. localStorage 有值且与当前页面语言不同 → 跳转到偏好语言版本
 *   3. localStorage 有值且匹配 → 不操作
 *
 * 注意：localStorage 仅在用户点击导航栏语言切换按钮时被修改（见上方 watch），
 * 手动修改地址栏不会更新 localStorage。
 */
function autoRedirectLocale() {
  const rawPath = window.location.pathname
  const path = rawPath.replace(/\/index\.html$/, '')

  try {
    const saved = localStorage.getItem(LOCALE_STORAGE_KEY)
    const isEnglishPage = path.startsWith('/en/') || path === '/en'
    const currentLocale = isEnglishPage ? 'en' : 'zh'

    if (!saved) {
      // 首次访问 — 浏览器语言检测
      const navLang = navigator.language || ''
      const isEnglishBrowser = navLang.startsWith('en')

      if (isEnglishBrowser && !isEnglishPage) {
        const enPath = path === '/' || path === '' ? '/en/' : `/en${path}`
        window.location.replace(enPath)
        return
      }
      if (!isEnglishBrowser && isEnglishPage) {
        const zhPath = path.replace(/^\/en/, '') || '/'
        window.location.replace(zhPath)
        return
      }
      // 语言匹配 → 记录偏好
      localStorage.setItem(LOCALE_STORAGE_KEY, currentLocale)
    } else if (saved !== currentLocale) {
      // 有历史偏好，但当前页面语言不匹配 → 跳转
      if (saved === 'en') {
        const enPath = path === '/' || path === '' ? '/en/' : `/en${path}`
        window.location.replace(enPath)
      } else {
        const zhPath = path.replace(/^\/en/, '') || '/'
        window.location.replace(zhPath)
      }
    }
    // saved === currentLocale → 不跳，不写 localStorage（偏好未变）
  } catch (_) {
    // localStorage 不可用时静默忽略
  }
}

function baiduTongji() {
  // 集成百度统计
  const hm = document.createElement("script");
  hm.src = "https://hm.baidu.com/hm.js?33338f09bb5e0f93efcf6b15508209e6";
  const s = document.getElementsByTagName("script")[0];
  s.parentNode?.insertBefore(hm, s);
}

function loadXsWidget() {
  // 加载时伴作品嵌入卡片
  const hm = document.createElement("script");
  hm.src = "https://10b.zhxu.cn/widget/index.js";
  const s = document.getElementsByTagName("script")[0];
  s.parentNode?.insertBefore(hm, s);
}

</script>

<style lang="css" scoped>
.discuss {
  font-size: 0.9rem;
  margin-left: 1rem;
  cursor: pointer;
  padding-top: 6px;
}

.button-arrow {
    display: inline-block;
    margin-top: -1px;
    margin-left: 8px;
    border-top: 6px solid #ccc;
    border-right: 4px solid transparent;
    border-bottom: 0;
    border-left: 4px solid transparent;
    vertical-align: middle;
}

.discuss img {
  position: fixed;
  top: 3rem;
  right: 0;
  width: 600px;
  display: none;
}

.discuss:hover img {
  display: block;
}

.aliyun-ad {
  background: rgb(250, 117, 83);
  color: rgb(250, 220, 86);
  text-align: center;
  margin-top: 8px;
  margin-right: -18px;
  padding: 3px 0;
  position: sticky;
  top: 0;
  z-index: 10;
}
</style>
