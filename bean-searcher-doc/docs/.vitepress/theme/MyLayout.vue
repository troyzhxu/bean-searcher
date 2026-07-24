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
        <div class="aliyun-ad">
          <a href="https://www.aliyun.com/minisite/goods?userCode=zugtbi5w" target="_blank" >
          🧧 阿里云低至 1 折 🧧
          </a>
        </div>
      </template>
      <template #home-hero-after>
        <HomeSponsors style="text-align: center" />
      </template>
    </Layout>
    <div class="tb-widget" data-10bWorkId="1" data-theme="light"></div>
  </div>
</template>

<script setup>

import { onMounted, ref } from "vue"
import DefaultTheme from 'vitepress/theme'
import HomeSponsors from './HomeSponsors.vue'
const { Layout } = DefaultTheme

onMounted(() => {
  if (checkEnvAndSSL()) {
    baiduTongji();
    loadXsWidget();
  }
  console.log("\n%c Bean Searcher %c 你点 STAR 了没有 😎 ? \n", "color: #fff; background: #f1404b; padding:5px 0;", "background: #111; padding:5px 0; color: #fff");
  console.log('👉 https://github.com/troyzhxu/bean-searcher')
  console.log('👉 https://gitee.com/troyzhxu/bean-searcher')
})

function checkEnvAndSSL() {
  const path = location.href;
  const isSsl = path.indexOf('https') == 0;
  if (!isSsl && import.meta.env.PROD) {
    // 自动重定向
    location.href = 'https' + path.substr(4);
  }
  return isSsl || import.meta.env.DEV;
}

function baiduTongji() {
  // 集成百度统计
  const hm = document.createElement("script");
  hm.src = "https://hm.baidu.com/hm.js?33338f09bb5e0f93efcf6b15508209e6";
  const s = document.getElementsByTagName("script")[0];
  s.parentNode.insertBefore(hm, s);
}

function loadXsWidget() {
  // 加载西市商品嵌入卡片
  const hm = document.createElement("script");
  hm.src = "https://10b.zhxu.cn/widget/index.js";
  const s = document.getElementsByTagName("script")[0];
  s.parentNode.insertBefore(hm, s);
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
