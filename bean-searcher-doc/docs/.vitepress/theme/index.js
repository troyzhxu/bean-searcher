import { Carousel } from 'ant-design-vue';
import DefaultTheme from 'vitepress/theme'
import MyLayout from './MyLayout.vue'
import DonateList from './DonateList.vue'

import 'ant-design-vue/lib/carousel/style/index.css';
import './theme.css'

// @ts-check
/**
 * @type {import('vitepress').Theme}
 */
export default {
  ...DefaultTheme,
  Layout: MyLayout,
  enhanceApp({ app }) {
    // register global components
    app.component('DonateList', DonateList)
    app.component('Carousel', Carousel)
    // console.log('app = ', app)
  }
}