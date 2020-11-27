module.exports = {
  title: 'IOTCP-2.0 SDK',
  locales: {
    '/': {
      lang: 'zh-CN',
      description: '新一代物联网协议',
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
          { 
            text: '下载',
            ariaLabel: '资料下载',
            items: [
              { text: 'SDK 下载', link: '/v2/#sdk-下载' },
              { text: '协议文档', link: '/v2/#协议文档' }
            ]
          },
          { text: 'OkHttps', link: 'http://okhttps.ejlchina.com' },
          { text: 'Grails 中文', link: 'http://grails.ejlchina.com' }
        ],
        sidebar: {
          '/v2/': [
            '',
            'integration',
            'initialization',
            'sdk-apis',
            'up-reboot',
            'gateway'
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
