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
          🧧 阿里云 7 折优惠 🧧
          </a>
        </div>
      </template>
      <template #home-hero-after>
        <HomeSponsors style="text-align: center" />
      </template>
    </Layout>
    <div class="wwads-cn" :class="adClass" data-id="117"></div>
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
    advertisement();
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

const adClass = ref('wwads-vertical');

function advertisement() {
  if (window.innerWidth <= 1032) {
    adClass.value = 'wwads-horizontal';
  }
  // 集成万维广告
  const hm = document.createElement("script");
  hm.src = "https://cdn.wwads.cn/js/makemoney.js";
  const s = document.getElementsByTagName("script")[0];
  s.parentNode.insertBefore(hm, s);
  //check if wwads' fire function was blocked after document is ready with 3s timeout (waiting the ad loading)
  docReady(function () {
    setTimeout(function () {
      if(window._AdBlockInit === undefined ){
          ABDetected();
      }
    }, 3000);
  });
}

// function called if wwads is blocked
function ABDetected() {
  document.getElementsByClassName("wwads-cn")[0].insertAdjacentHTML("beforeend", "<style>" + 
  ".wwads-horizontal,.wwads-vertical{background-color:#f4f8fa;padding:5px;min-height:120px;margin-top:20px;box-sizing:border-box;border-radius:3px;font-family:sans-serif;display:flex;min-width:150px;position:relative;overflow:hidden;}" + 
  ".wwads-horizontal{flex-wrap:wrap;justify-content:center}" + 
  ".wwads-vertical{flex-direction:column;align-items:center;padding-bottom:32px}" + 
  ".wwads-horizontal a,.wwads-vertical a{text-decoration:none}" + 
  ".wwads-horizontal .wwads-img,.wwads-vertical .wwads-img{margin:5px}" + 
  ".wwads-horizontal .wwads-content,.wwads-vertical .wwads-content{margin:5px}" + 
  ".wwads-horizontal .wwads-content{flex:130px}.wwads-vertical .wwads-content{margin-top:10px}" +
  ".wwads-horizontal .wwads-text,.wwads-content .wwads-text{font-size:14px;line-height:1.4;color:#0e1011;-webkit-font-smoothing:antialiased}" +
  ".wwads-horizontal .wwads-poweredby,.wwads-vertical .wwads-poweredby{display:block;font-size:11px;color:#a6b7bf;margin-top:1em}" + 
  ".wwads-vertical .wwads-poweredby{position:absolute;left:10px;bottom:10px}" + 
  ".wwads-horizontal .wwads-poweredby span,.wwads-vertical .wwads-poweredby span{transition:all 0.2s ease-in-out;margin-left:-1em}" + 
  ".wwads-horizontal .wwads-poweredby span:first-child,.wwads-vertical .wwads-poweredby span:first-child{opacity:0}" +
  ".wwads-horizontal:hover .wwads-poweredby span,.wwads-vertical:hover .wwads-poweredby span{opacity:1;margin-left:0}" +
  ".wwads-horizontal .wwads-hide,.wwads-vertical .wwads-hide{position:absolute;right:-23px;bottom:-23px;width:46px;height:46px;border-radius:23px;transition:all 0.3s ease-in-out;cursor:pointer;}" +
  ".wwads-horizontal .wwads-hide:hover,.wwads-vertical .wwads-hide:hover{background:rgb(0 0 0 /0.05)}" +
  ".wwads-horizontal .wwads-hide svg,.wwads-vertical .wwads-hide svg{position:absolute;left:10px;top:10px;fill:#a6b7bf}" +
  ".wwads-horizontal .wwads-hide:hover svg,.wwads-vertical .wwads-hide:hover svg{fill:#3E4546}</style>" +
  "<a href='https://wwads.cn/page/whitelist-wwads' class='wwads-img' target='_blank' rel='nofollow'><img src='/wwads_please.png' width='130'></a>" +
  "<div class='wwads-content'><a href='https://wwads.cn/page/whitelist-wwads' class='wwads-text' target='_blank' rel='nofollow'>为了本站的长期运营，请将我们的网站加入广告拦截器的白名单，感谢您的支持！</a>" +
  "<a href='https://wwads.cn/page/end-user-privacy' class='wwads-poweredby' title='万维广告 ～ 让广告更优雅，且有用' target='_blank'><span>万维</span><span>广告</span></a></div><a class='wwads-hide' onclick='parentNode.remove()' title='隐藏广告'>" +
  "<svg xmlns='http://www.w3.org/2000/svg' width='6' height='7'><path d='M.879.672L3 2.793 5.121.672a.5.5 0 11.707.707L3.708 3.5l2.12 2.121a.5.5 0 11-.707.707l-2.12-2.12-2.122 2.12a.5.5 0 11-.707-.707l2.121-2.12L.172 1.378A.5.5 0 01.879.672z'></path></svg></a>");
};

//check document ready
function docReady(t) {
    "complete" === document.readyState ||
    "interactive" === document.readyState
      ? setTimeout(t, 1)
      : document.addEventListener("DOMContentLoaded", t);
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

.wwads-cn {
  position: fixed !important;
}

.wwads-vertical {
  max-width: 150px;
  right: 10px;
  bottom: 10px;
}

.wwads-horizontal {
  max-height: 100px;
  margin: 2rem auto 0 auto;
}

.myshop {
  width: 150px;
  position: fixed;
  top: 100px;
  right: 10px;
  z-index: 1;
  border: 5px solid #f4f8fa;
}
.myshop .text {
  font-size: 12px;
  padding: 0 5px;
  text-align: center;
  background: #f4f8fa;
  padding-top: 3px;
  color: rgb(255, 0, 0);
}
.myshop .qrcode {
  display: block;
  margin-top: -5px;
}
.myshop .tips {
  position: absolute;
  top: -5px;
  bottom: -5px;
  right: 145px;
  width: 250px;
  background: #f4f8fa;
  color: rgb(87, 97, 93);
  font-size: small;
  padding: 8px 0 8px 8px;
  display: none;
}
.myshop:hover .tips {
  display: block;
}
@media screen and (max-width: 1400px) {
  .wwads-vertical {
    max-width: 120px;
  }
}

@media screen and (max-width: 1032px) {
  .wwads-cn {
    position: static !important;
  }
}
.aliyun-ad {
  background: rgba(219, 33, 0);
  color: white;
  text-align: center;
  margin-top: 8px;
  margin-right: -18px;
  padding: 3px 0;
  position: sticky;
  top: 0;
  z-index: 10;
}
</style>
