import { defineConfig } from 'vitepress'
import en from './en'
import zh from './zh'

const SITE_URL = 'https://bs.zhxu.cn'
const OG_IMAGE = `${SITE_URL}/logo.png`

export default defineConfig({

  title: "Bean Searcher",
  description: "Bean Searcher - 专注高级查询的只读 ORM，天生支持联表，免 DTO/VO 转换，一行代码实现复杂列表检索",
  lang: 'zh-CN',

  cleanUrls: true,

  head: [
    ['link', { rel: 'icon', href: '/logo.png' }],
    ['meta', { name: 'robots', content: 'index, follow' }],
    ['meta', { name: 'author', content: '周旭 (troyzhxu)' }],
    ['meta', { name: 'keywords', content: 'Bean Searcher,Java ORM,只读ORM,高级查询,动态查询,声明式检索,Spring Boot,列表查询,分页查询,多表联查,字段参数,Java框架' }],

    // Open Graph
    ['meta', { property: 'og:type', content: 'website' }],
    ['meta', { property: 'og:site_name', content: 'Bean Searcher 文档' }],
    ['meta', { property: 'og:locale', content: 'zh_CN' }],
    ['meta', { property: 'og:image', content: OG_IMAGE }],
    ['meta', { property: 'og:image:width', content: '256' }],
    ['meta', { property: 'og:image:height', content: '256' }],

    // Twitter Card
    ['meta', { name: 'twitter:card', content: 'summary' }],
    ['meta', { name: 'twitter:site', content: '@troyzhxu' }],
    ['meta', { name: 'twitter:image', content: OG_IMAGE }],
  ],

  transformHead: (ctx) => {
    const canonical = `${SITE_URL}/${ctx.pageData.relativePath.replace(/\.md$/, '').replace(/index$/, '')}`
    return [
      ['link', { rel: 'canonical', href: canonical.replace(/\/$/, '') || SITE_URL }],
    ]
  },

  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    logo: '/logo.png',

    search: {
      provider: 'local'
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/troyzhxu/bean-searcher' },
      {
        icon: {
        svg: '<svg t="1717340498057" class="gitee" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2400" width="64" height="64"><path d="M512 1024C229.222 1024 0 794.778 0 512S229.222 0 512 0s512 229.222 512 512-229.222 512-512 512z m259.149-568.883h-290.74a25.293 25.293 0 0 0-25.292 25.293l-0.026 63.206c0 13.952 11.315 25.293 25.267 25.293h177.024c13.978 0 25.293 11.315 25.293 25.267v12.646a75.853 75.853 0 0 1-75.853 75.853h-240.23a25.293 25.293 0 0 1-25.267-25.293V417.203a75.853 75.853 0 0 1 75.827-75.853h353.946a25.293 25.293 0 0 0 25.267-25.292l0.077-63.207a25.293 25.293 0 0 0-25.268-25.293H417.152a189.62 189.62 0 0 0-189.62 189.645V771.15c0 13.977 11.316 25.293 25.294 25.293h372.94a170.65 170.65 0 0 0 170.65-170.65V480.384a25.293 25.293 0 0 0-25.293-25.267z" p-id="2401"></path></svg>' 
        },
        link: 'https://gitee.com/troyzhxu/bean-searcher' 
      }
    ],

    outline: {
      level: [2, 3]
    },

    lastUpdated: {
      formatOptions: {
        dateStyle: 'short',
        timeStyle: 'medium'
      }
    },
  },

  locales: {
    root: { label: '简体中文', ...zh },
    en: { label: 'English', ...en },
  },

  rewrites: {
    'zh/:rest*': ':rest*'
  },

  markdown: {
    config(md) {
      // 保存默认的图片渲染规则
      const defaultRender = md.renderer.rules.image
      // 重写规则
      md.renderer.rules.image = (tokens, idx, options, env, self) => {
        // 调用默认规则，生成原始的<img>标签 HTML 字符串
        const defaultResult = defaultRender?.(tokens, idx, options, env, self)
        // 用自定义组件包裹默认结果
        return `<ImagePreview>${defaultResult}</ImagePreview>`
      }
    },
  },

})
