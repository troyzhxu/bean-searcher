module.exports = {
  title: 'Bean Searcher',
  locales: {
    '/': {
      lang: 'zh-CN',
      description: 'Bean Searcher - 轻量级 WEB 条件检索引擎',
    }
  },
  head: [
    ['link', { rel: 'icon', href: '/logo.png' }]
  ],
  themeConfig: {
    logo: '/logo.png',
    locales: {
      '/': {
        nav: [
          { 
            text: '教程',
            ariaLabel: '版本',
            items: [
              { text: 'v2.x', link: '/v2/' }
            ]
          },

          { text: 'OkHttps', link: 'http://okhttps.ejlchina.com' },
          { text: 'Grails 中文', link: 'http://grails.ejlchina.com' },
          { text: '码云', link: 'https://gitee.com/ejlchina-zhxu/bean-searcher' }
        ],
        sidebar: {
          '/v2/': [
            '',
            'starting'
          ]
        },
        lastUpdated: '上次更新',
      }
    },
    sidebarDepth: 2,
    smoothScroll: true
  },
  // 若全局使用 vuepress，back-to-top 就会失效
  plugins: [
    '@vuepress/back-to-top', 
    '@vuepress/medium-zoom', 
    'baidu-autopush', 'seo',
    ['baidu-tongji', {hm: '5b09df56eba9ccf9cba21208e4631ad0'}]
  ],
  markdown: {
    lineNumbers: true
  }
}
