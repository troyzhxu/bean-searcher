<template>
  <div class="locale-switcher">
    <a-button size="small" type="text" @click="toggle">
      <span class="locale-text">{{ current === 'zh-CN' ? 'English' : '中文' }}</span>
    </a-button>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { switchLocale } from '@/i18n'

const { locale } = useI18n()
const current = ref(locale.value)

watch(locale, (val) => {
  current.value = val
})

function toggle() {
  const next = switchLocale()
  current.value = next
}
</script>

<style scoped>
.locale-switcher {
  position: absolute;
  top: 16px;
  right: 24px;
  z-index: 10;
}

.locale-text {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
}

.locale-switcher :deep(.ant-btn) {
  border-radius: 8px;
  transition: all 0.3s;
}

.locale-switcher :deep(.ant-btn:hover) {
  color: var(--primary);
  background: var(--primary-bg);
}

@media (max-width: 480px) {
  .locale-switcher {
    top: 8px;
    right: 12px;
  }
}
</style>
