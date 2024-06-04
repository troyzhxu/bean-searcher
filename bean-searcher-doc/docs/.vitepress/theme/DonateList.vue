<template>
  <table :class="props.home ? 'table-list' : 'table-list'">
    <thead>
      <tr>
        <th>赞助人</th>
        <!-- <th style="width: 100px">金额</th> -->
        <th>他的分享 / 留言</th>
        <!-- <th style="width: 120px">赞助时间</th> -->
      </tr>
    </thead>
    <tbody>
      <tr v-for="d in donateList">
        <td>
          <a v-if="d.home" :href="d.home" target="_blank">{{d.name}}</a>
          <span v-else>{{d.name}}</span>
        </td>
        <!-- <td><span class="money">￥{{d.totalAmount}}</span></td> -->
        <td>
          <template v-if="d.share && d.share.msg">
            <a v-if="d.share.url" :href="d.share.url" target="_blank">{{d.share.msg}}</a>
            <span v-else>{{d.share.msg}}</span>
          </template>
          <span v-else>请作者喝杯咖啡</span>
        </td>
        <!-- <td>{{d.lastDate}}</td> -->
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'

const props = defineProps({
  home: {
    type: Boolean,
    default: false,
  }
})

const donateList = ref([]);

onMounted(() => {
  axios.get('https://sponsors.oss-cn-hangzhou.aliyuncs.com/donate-list.json')
    .then(res => {
      const list = res.data;
      list.sort((a, b) => {
        return weight(b) - weight(a);   // 按权重降序排列
      });
      donateList.value = list;
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
.table-list {
  display: block;
  margin: 10px auto;
}
.money {
  color: #fa6007;
  font-weight: 600;
}
</style>
