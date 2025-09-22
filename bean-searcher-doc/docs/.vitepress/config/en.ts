import { defineConfig } from 'vitepress'

export default defineConfig({

  lang: 'en-US',

  description: "A read-only ORM focused on advanced queries, inherently supporting join tables and eliminating the need for DTO/VO conversion, making it possible to achieve complex list retrieval with just one line of code!",

  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config

    nav: [
      { text: 'Guide', link: '/en/guide/info/bean-searcher', activeMatch: '/guide/' },
      {
        text: 'v4.6.0.alpha',
        items: [
          {
            text: 'Changelog',
            link: 'https://gitee.com/troyzhxu/bean-searcher/blob/dev/CHANGELOG.md'
          },
          {
            text: 'Contributing',
            link: 'https://gitee.com/troyzhxu/bean-searcher/blob/dev/CONTRIBUTING.md'
          }
        ]
      },
      { text: 'Blog', link: 'https://juejin.cn/column/7028509095564935199' },
    ],

    sidebar: {
      '/en/guide/': {
        base: '/en/guide', 
        items: [
          {
            text: 'Introduction',
            base: '/en/guide/info',
            collapsed: false,
            items: [
              { text: 'Why use it', link: '/why' },
              { text: 'Bean Searcher', link: '/bean-searcher' },
              { text: 'What\'s New?', link: '/versions' },
            ]
          },
          {
            text: 'Get Started',
            base: '/en/guide/start',
            collapsed: false,
            items: [
              { text: 'Install', link: '/install' },
              { text: 'Integration', link: '/integration' },
              { text: 'Searching', link: '/use' },
            ]
          },
          {
            text: 'Entity Class',
            base: '/en/guide/bean',
            collapsed: false,
            items: [
              { text: 'Conception', link: '/info' },
              { text: 'Multi-table Join', link: '/multitable' },
              { text: 'Other Forms', link: '/otherform' },
              { text: 'Conditional Fields', link: '/fields' },
              { text: 'Embedding Parameter', link: '/params' },
              { text: 'Annotation Omission', link: '/aignore' },
              { text: 'Entity Inheritance', link: '/inherit' },
              { text: 'Field Ignore', link: '/fignore' },
              { text: 'Optional Interface', link: '/optional' },
            ]
          },
          {
            text: 'Retrieval Parameter',
            base: '/en/guide/param',
            collapsed: false,
            items: [
              { text: 'Conception', link: '/info' },
              { text: 'Paging Parameter', link: '/page' },
              { text: 'Ordering Parameter', link: '/sort' },
              { text: 'Field Parameter', link: '/field' },
              { text: 'Custom SQL Conditions', link: '/sql' },
              { text: 'Logical Grouping', link: '/group' },
              { text: 'Specify Select Fields', link: '/select' },
              { text: 'Embedded Parameter', link: '/embed' },
            ]
          },
          {
            text: 'Advanced',
            base: '/en/guide/advance',
            collapsed: false,
            items: [
              { text: 'Constraints and Risk Control', link: '/safe' },
              { text: 'Parameter and Result Filter', link: '/filter' },
              { text: 'Field and Parameter Converter', link: '/convertor' },
              { text: 'Field Operator', link: '/fieldop' },
              { text: 'SQL Interceptor', link: '/interceptor' },
              { text: 'SQL Dialect', link: '/dialect' },
              { text: 'Slow SQL Logging and Listening', link: '/slowsql' },
              { text: 'Multiple Data Sources', link: '/datasource' },
            ]
          },
          {
            text: 'Usages',
            base: '/en/guide/usage',
            collapsed: false,
            items: [
              { text: 'Call Remote BS Service', link: '/rpc' },
              { text: 'Large Table Rolling', link: '/tables' },
              { text: 'Data Permission', link: '/datascope' },
              { text: 'Other Gameplay', link: '/others' },
            ]
          }
        ]
      }
    },

    editLink: {
      pattern: 'https://github.com/troyzhxu/bean-searcher/edit/dev/bean-searcher-doc/docs/:path',
      text: 'Edit this page on GitHub'
    },

    footer: {
      message: 'Released under the Apache License',
      copyright: 'Copyright © 2017-present Zhou Xu'
    }
  }

})
