<template>
  <div class="repo-banner">
    <p class="repo-text">开源不易，点个 ⭐ Star 支持一下</p>
    <div class="repo-links">
      <a href="https://github.com/troyzhxu/bean-searcher" target="_blank" rel="noopener noreferrer" class="repo-icon-link" title="GitHub">
        <svg class="repo-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path fill="currentColor" d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/>
        </svg>
      </a>
      <a href="https://gitee.com/troyzhxu/bean-searcher" target="_blank" rel="noopener noreferrer" class="repo-icon-link gitee" title="Gitee">
        <svg class="repo-icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
          <path fill="currentColor" d="M512 1024C229.222 1024 0 794.778 0 512S229.222 0 512 0s512 229.222 512 512-229.222 512-512 512z m259.149-568.883h-290.74a25.293 25.293 0 0 0-25.292 25.293l-0.026 63.206c0 13.952 11.315 25.293 25.267 25.293h177.024c13.978 0 25.293 11.315 25.293 25.267v12.646a75.853 75.853 0 0 1-75.853 75.853h-240.23a25.293 25.293 0 0 1-25.267-25.293V417.203a75.853 75.853 0 0 1 75.827-75.853h353.946a25.293 25.293 0 0 0 25.267-25.292l0.077-63.207a25.293 25.293 0 0 0-25.268-25.293H417.152a189.62 189.62 0 0 0-189.62 189.645V771.15c0 13.977 11.316 25.293 25.294 25.293h372.94a170.65 170.65 0 0 0 170.65-170.65V480.384a25.293 25.293 0 0 0-25.293-25.267z"/>
        </svg>
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.repo-banner {
  text-align: center;
  padding: 36px 24px 44px;
}

.repo-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 18px;
}

.repo-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.repo-icon-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--surface);
  box-shadow: var(--shadow-sm);
  color: #333;
  transition: box-shadow .3s, transform .3s, color .3s;
}

.repo-icon-link:hover {
  box-shadow: 0 4px 20px rgba(91,90,247,.2);
  transform: translateY(-2px);
  color: #5b5af7;
}

.repo-icon-link.gitee {
  color: #c71d23ce;
}

.repo-icon-link.gitee:hover {
  box-shadow: 0 4px 20px rgba(199,29,35,.25);
  color: #c71d23;
}

.repo-icon {
  width: 26px;
  height: 26px;
}
</style>
