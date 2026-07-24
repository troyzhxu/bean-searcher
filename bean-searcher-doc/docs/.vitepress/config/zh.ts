import { defineConfig } from 'vitepress'
import { projects } from '../projects'

export default defineConfig({

  lang: 'zh-CN',

  description: "专注高级查询的只读 ORM，天生支持联表，免 DTO/VO 转换，使一行代码实现复杂列表检索成为可能！",

  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config

    nav: [
      { text: '指南', link: '/guide/info/bean-searcher', activeMatch: '/guide/' },
      {
        text: 'v4.8.12',
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
      {
        text: '生态',
        items: [
          {
            text: '标签系统',
            link: '/zoo/label/info',
            activeMatch: '/zoo/label'
          },
          {
            text: '数据导出',
            link: '/zoo/ex/info',
            activeMatch: '/zoo/ex'
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
              { text: '为什么用', link: '/why' },
              { text: 'Bean Searcher', link: '/bean-searcher' },
              { text: 'What\'s New?', link: '/versions' },
            ]
          },
          {
            text: '起步',
            base: '/guide/start',
            collapsed: false,
            items: [
              { text: '快速入门', link: '/quick' },
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
              { text: '核心注解', link: '/annos' },
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
              { text: '其它玩法', link: '/others' },
            ]
          }
        ]
      },
      '/zoo/': {
        base: '/zoo', 
        items: [
          {
            text: '标签系统',
            base: '/zoo/label',
            collapsed: false,
            items: [
              { text: '介绍', link: '/info' },
              { text: '标签注解', link: '/anno' },
              { text: '标签加载器', link: '/load' },
              { text: '用法示例', link: '/demo' },
            ]
          },
          {
            text: '数据导出',
            base: '/zoo/ex',
            collapsed: false,
            items: [
              { text: '介绍', link: '/info' },
              { text: '文件导出器', link: '/exporter' },
              { text: '导出注解', link: '/anno' },
              { text: '文件写出', link: '/fwriter' },
              { text: '并发控制', link: '/control' },
            ]
          },
        ]
      },
    },

    outline: {
      level: [2, 3],
      label: '页面导航',
    },

    lastUpdated: {
      text: '最后更新于',
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
      copyright: '版权所有 © 2017-现在 周旭'
    }
  }

})
