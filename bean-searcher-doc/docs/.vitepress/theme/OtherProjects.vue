<template>
  <div class="container">
    <div class="title">开源推荐</div>
    <div class="logos">
      <template v-for="p in imgProjects">
        <a :href="p.link" target="_blank" :title="p.text">
          <img :src="p.image" :alt="p.text">
        </a>
      </template>
    </div>
    <a href="https://www.aliyun.com/minisite/goods?userCode=zugtbi5w" target="_blank">🧧 阿里云低至 1 折 🧧</a>
  </div>
</template>

<script setup>
import { projects } from '../projects'
const imgProjects = projects.filter(p => !!p.image)
</script>

<style lang="css" scoped>
.container {
  margin-top: 60px;
  text-align: center;
}
.container a {
  text-decoration: none;
  color: red;
}
.title {
  font-weight: 600;
  font-size: 20px;
}
.logos {
  margin: 20px auto;
  max-width: 1000px;
}
.logos img {
  max-width: 120px;
  max-height: 40px;
  margin: 15px;
  display: inline-block;
}
</style>
