import { defineConfig } from 'vitepress'
import { projects } from './projects'


// https://vitepress.dev/reference/site-config
export default defineConfig({

  lang: 'zh-CN',

  title: "Bean Searcher",
  description: "专注高级查询的只读 ORM，天生支持联表，免 DTO/VO 转换，使一行代码实现复杂列表检索成为可能！",

  head: [['link', { rel: 'icon', href: '/logo.png' }]],

  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    logo: '/logo.png',

    search: {
      provider: 'local'
    },

    nav: [
      { text: '指南', link: '/guide/info/bean-searcher', activeMatch: '/guide/' },
      {
        text: 'v4.3.0',
        items: [
          {
            text: '更新日志',
            link: 'https://gitee.com/troyzhxu/bean-searcher/blob/dev/CHANGELOG.md'
          },
          {
            text: '参与贡献',
            link: 'https://gitee.com/troyzhxu/bean-searcher/blob/dev/CONTRIBUTING.md'
          }
        ]
      },
      { text: '💖赞助', link: '/support' },
      {
        text: '推荐',
        items: projects
      },
      { text: '博客', link: 'https://juejin.cn/column/7028509095564935199' },
    ],

    sidebar: {
      '/guide/': { 
        base: '/guide', 
        items: [
          {
            text: '介绍',
            base: '/guide/info',
            collapsed: false,
            items: [
              { text: '什么为用', link: '/why' },
              { text: 'Bean Searcher', link: '/bean-searcher' },
              { text: 'What\'s New?', link: '/versions' },
            ]
          },
          {
            text: '起步',
            base: '/guide/start',
            collapsed: false,
            items: [
              { text: '安装', link: '/install' },
              { text: '集成', link: '/integration' },
              { text: '使用', link: '/use' },
            ]
          },
          {
            text: '实体类',
            base: '/guide/bean',
            collapsed: false,
            items: [
              { text: '概念', link: '/info' },
              { text: '多表关联', link: '/multitable' },
              { text: '其它形式', link: '/otherform' },
              { text: '条件属性', link: '/fields' },
              { text: '嵌入参数', link: '/params' },
              { text: '注解缺省', link: '/aignore' },
              { text: '实体类继承', link: '/inherit' },
              { text: '属性忽略', link: '/fignore' },
              { text: '可选接口', link: '/optional' },
            ]
          },
          {
            text: '检索参数',
            base: '/guide/param',
            collapsed: false,
            items: [
              { text: '概念', link: '/info' },
              { text: '分页参数', link: '/page' },
              { text: '排序参数', link: '/sort' },
              { text: '字段参数', link: '/field' },
              { text: '自定义 SQL 条件', link: '/sql' },
              { text: '逻辑分组', link: '/group' },
              { text: '指定 Select 字段', link: '/select' },
              { text: '内嵌参数', link: '/embed' },
            ]
          },
          {
            text: '高级',
            base: '/guide/advance',
            collapsed: false,
            items: [
              { text: '约束与风控', link: '/safe' },
              { text: '参数与结果过滤器', link: '/filter' },
              { text: '字段与参数转换器', link: '/convertor' },
              { text: '玩转运算符', link: '/fieldop' },
              { text: 'SQL 拦截器', link: '/interceptor' },
              { text: 'SQL 方言（Dialect）', link: '/dialect' },
              { text: '慢 SQL 日志与监听', link: '/slowsql' },
              { text: '多数据源', link: '/datasource' },
            ]
          },
          {
            text: '场景',
            base: '/guide/usage',
            collapsed: false,
            items: [
              { text: '请求第三方 BS 服务', link: '/rpc' },
              { text: '大表滚动', link: '/tables' },
              { text: '数据权限', link: '/datascope' },
            ]
          }
        ]
      }
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
      label: '页面导航',
      level: [2, 3]
    },

    lastUpdated: {
      text: '最后更新于',
      formatOptions: {
        dateStyle: 'short',
        timeStyle: 'medium'
      }
    },

    editLink: {
      pattern: 'https://gitee.com/troyzhxu/bean-searcher/edit/dev/bean-searcher-doc/docs/:path',
      text: '在 Gitee 上编辑此页面'
    },

    docFooter: {
      prev: '上一页',
      next: '下一页'
    },

    footer: {
      message: '基于 Apache 许可发布',
      copyright: '版权所有 © 2017-2024 周旭'
    }
  }
})
