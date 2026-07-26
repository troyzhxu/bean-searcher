<template>
  <div class="container">
    <div v-show="showTitle" class="title">赞助者</div>
    <div class="logos">
      <template v-for="s in sponsors">
        <a :href="s.link" target="_blank" :title="s.name + ' - ' + s.description">
          <img :src="s.logo" :alt="s.description">
        </a>
      </template>
    </div>
    <a href="https://www.aliyun.com/minisite/goods?userCode=zugtbi5w" target="_blank" style="color: red;">🧧 阿里云低至 1 折 🧧</a>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

defineProps({
  showTitle: { type: Boolean, default: true },
})

const sponsors = ref([]);

onMounted(() => {
  fetch('https://sponsors.oss-cn-hangzhou.aliyuncs.com/sponsors.json')
    .then(res => res.json())
    .then(list => {
      list.sort((a, b) => {
        return weight(b) - weight(a);   // 按权重降序排列
      });
      sponsors.value = list;
    })
})

const NOW = Date.now();
const DAY = 24 * 3600 * 1000;

/**
 * 计算赞助者的权重
 */
function weight(sponsor) {
  const fistDays = (NOW - Date.parse(sponsor.firstDate)) / DAY + 1;
  const lastDays = (NOW - Date.parse(sponsor.lastDate)) / DAY + 1;
  const totalWeight = sponsor.totalAmount / Math.max(fistDays, 1);
  const lastWeight = sponsor.lastAmount / Math.max(lastDays, 1);
  return Math.max(totalWeight, lastWeight);
}

</script>

<style lang="css" scoped>
.container {
  margin-bottom: 30px;
}
.title {
  font-weight: 600;
  font-size: 20px;
}
.logos {
  margin: 20px auto 10px auto;
  max-width: 1000px;
}
.logos img {
  max-width: 200px;
  max-height: 45px;
  margin: 15px;
  display: inline-block;
}
</style>
