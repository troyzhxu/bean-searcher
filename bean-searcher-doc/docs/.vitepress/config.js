// @ts-check
/**
 * @type {import('vitepress').UserConfig}
 */
module.exports = {
  title: 'Bean Searcher',
  description: 'Bean Searcher - 比 MyBatis 效率快 100 倍的条件检索引擎，使一行代码实现复杂列表检索成为可能!',
  lang: 'zh-CN',
  head: [
    ['link', { rel: 'icon', href: '/logo.png' }]
  ],
  themeConfig: {
    logo: '/logo.png',
    nav: [
      {
        text: '教程',
        ariaLabel: '版本',
        items: [
          { text: 'v4.x', link: '/guide/latest/start.html' },
          { text: 'v3.x', link: '/guide/v3/start.html' },
        ]
      },
      { text: '💖支持', link: '/guide/latest/help.html' },
      {
        text: '推荐',
        items: [
          { text: 'OkHttps 非常好用的 HTTP 客户端', link: 'http://ok.zhxu.cn' },
          { text: 'Sa-Token 一个轻量级 java 权限认证框架，让鉴权变得简单、优雅！', link: 'https://sa-token.dev33.cn/' },
          { text: 'Jsonkit 一个超轻盈-优雅-简单的 JSON 门面工具！', link: 'https://gitee.com/troyzhxu/xjsonkit' },
          { text: 'Easy-Trans 一个注解搞定数据翻译，减少30%SQL代码量！', link: 'http://easy-trans.fhs-opensource.top/' },
          { text: 'Grails 中文文档', link: 'https://grails.zhxu.cn/' },
        ]
      },
      { text: '博客', link: 'https://juejin.cn/column/7028509095564935199' },
      { text: 'Gitee', link: 'https://gitee.com/troyzhxu/bean-searcher' },
      { text: 'Github', link: 'https://github.com/troyzhxu/bean-searcher' }
    ],
    sidebar: {
      '/guide/latest/': [
        {
          text: '介绍',
          link: '/guide/latest/introduction.html'
        },
        {
          text: '起步',
          link: '/guide/latest/start.html'
        },
        {
          text: '实体类',
          link: '/guide/latest/bean.html'
        },
        {
          text: '参数',
          link: '/guide/latest/params.html'
        },
        {
          text: '高级',
          link: '/guide/latest/advance.html'
        },
        {
          text: '示例',
          link: '/guide/latest/simples.html'
        },
        {
          text: '支持',
          link: '/guide/latest/help.html'
        }
      ],
      '/guide/v3/': [
        {
          text: '介绍',
          link: '/guide/v3/introduction.html'
        },
        {
          text: '起步',
          link: '/guide/v3/start.html'
        },
        {
          text: '实体类',
          link: '/guide/v3/bean.html'
        },
        {
          text: '参数',
          link: '/guide/v3/params.html'
        },
        {
          text: '高级',
          link: '/guide/v3/advance.html'
        },
        {
          text: '示例',
          link: '/guide/v3/simples.html'
        },
        {
          text: '支持',
          link: '/guide/v3/help.html'
        }
      ],
    }
  },
  markdown: {
    lineNumbers: true
  }
}
