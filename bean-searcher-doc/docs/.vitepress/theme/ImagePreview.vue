<template>
  <div class="image-preview" @click="openPreview">
    <!-- 这里渲染原始的图片，插槽内容就是markdown解析后的<img>标签 -->
    <slot></slot>
    <!-- 全屏预览层 -->
    <div v-if="isOpen" class="mask" @click.stop="closePreview">
      <img :src="imgSrc" :alt="imgAlt" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, useSlots } from 'vue'

const isOpen = ref(false)
const imgSrc = ref('')
const imgAlt = ref('')

// 从插槽内的<img>标签中提取src和alt属性
const slots = useSlots()
onMounted(() => {
  const { alt, src } = slots.default?.()[0].props
  imgSrc.value = src || ''
  imgAlt.value = alt || ''
})

function openPreview() {
  isOpen.value = true 
}
function closePreview() {
  isOpen.value = false 
}
</script>

<style scoped>
.image-preview {
  cursor: pointer;
  display: inline-block; /* 保持和原图一样的显示方式 */
}
.image-preview .mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.image-preview img {
  max-width: 90vw;
  max-height: 90vh;
}
</style>