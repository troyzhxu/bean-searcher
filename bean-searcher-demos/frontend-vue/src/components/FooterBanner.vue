<template>
  <footer class="footer-banner">
    <div class="footer-bg"></div>
    <div class="footer-content">
      <span class="footer-kicker">How It Works</span>
      <h3 class="footer-headline">后端仅一句代码，功能全部搞定</h3>
      <div class="footer-showcase">
        <div class="footer-code">
          <div class="code-header">
            <span class="code-dot red"></span>
            <span class="code-dot yellow"></span>
            <span class="code-dot green"></span>
            <span class="code-file">DemoController.java</span>
          </div>
          <span class="code-comment">// 后端只要一行代码</span>
          <pre><code><span class="tk-var">beanSearcher</span><span class="tk-punct">.search(Employee.</span><span class="tk-keyword">class</span><span class="tk-punct">, Employee::getAge);</span></code></pre>
        </div>
        <div class="footer-arrow">→</div>
        <div class="footer-features">
          <div class="feature-header">
            <span class="feature-dot"></span>
            <span class="feature-title">这些功能，全部搞定</span>
          </div>
          <ul class="feature-list">
            <li>🔍 复杂过滤</li>
            <li>📊 字段统计</li>
            <li>⬆⬇ 字段排序</li>
            <li>📄 分页查询</li>
          </ul>
        </div>
      </div>
      <p class="footer-tagline">
        这就是
        <a href="https://bs.zhxu.cn"><strong>Bean Searcher</strong></a>
        — 专注高级查询的 <b>只读 ORM</b>，让列表检索从未如此简单。
      </p>
    </div>
  </footer>
</template>

<script setup lang="ts">
</script>

<style scoped>
.footer-banner {
  position: relative;
  margin-top: 30px;
  padding: 30px 0;
  text-align: center;
  overflow: hidden;
}

.footer-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #7c3aed 0%, #5b5af7 40%, #a78bfa 100%);
  opacity: 0.05;
}

.footer-content {
  position: relative;
  z-index: 1;
  margin: 0 auto;
  max-width: 1000px;
  padding: 0 20px;
}

.footer-kicker {
  display: inline-block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--primary);
  background: var(--primary-bg);
  padding: 3px 12px;
  border-radius: 20px;
  margin-bottom: 12px;
}

.footer-headline {
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 28px;
  background: linear-gradient(135deg, #5b5af7, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-showcase {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.footer-code {
  background: #1a1a2e;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  text-align: left;
  min-width: 340px;
}

.code-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: #16162a;
}

.code-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.code-dot.red {
  background: #ff5f57;
}

.code-dot.yellow {
  background: #febc2e;
}

.code-dot.green {
  background: #28c840;
}

.code-file {
  margin-left: 10px;
  font-size: 12px;
  color: #7c7caa;
  font-weight: 500;
}

.code-comment {
  display: block;
  padding: 14px 18px 0;
  font-size: 13px;
  color: #b2d8fd;
  font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace;
  font-style: italic;
  opacity: 0.7;
}

.footer-code pre {
  margin: 0;
  padding: 0 18px 26px 18px;
  overflow-x: auto;
}

.footer-code code {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
  font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace;
  line-height: 1.8;
  font-weight: 500;
}

/* Java 语法高亮 */
.tk-var     { color: #f48fb1; }   /* 变量名（beanSearcher） */
.tk-class   { color: #5fb3d1; }   /* 类名（Employee） */
.tk-keyword { color: #ffab74; }   /* 关键字（class） */
.tk-punct   { color: rgba(255, 255, 255, 0.8); }  /* 标点符号 */

.footer-arrow {
  font-size: 32px;
  color: var(--primary);
  font-weight: 800;
  flex-shrink: 0;
}

.footer-features {
  background: var(--surface);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  min-width: 340px;
  text-align: left;
  border: 1px solid var(--border);
}

.feature-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: var(--primary-bg);
  border-bottom: 1px solid #e0deff;
}

.feature-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--primary);
}

.feature-title {
  font-size: 12px;
  font-weight: 700;
  color: var(--primary);
  letter-spacing: 0.3px;
}

.feature-list {
  list-style: none;
  margin: 0;
  padding: 16px 18px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 16px;
}

.feature-list li {
  font-size: 13px;
  font-weight: 500;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 6px;
}

.footer-tagline {
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
}

.footer-tagline strong {
  color: var(--primary);
  font-weight: 700;
}

.footer-tagline b {
  color: var(--text);
  font-weight: 600;
}

@media (max-width: 768px) {
  .footer-headline {
    font-size: 18px;
  }
  .footer-showcase {
    gap: 14px;
  }
  .footer-code,
  .footer-features {
    min-width: 280px;
  }
  .footer-arrow {
    font-size: 24px;
  }
  .footer-code pre {
    padding: 0 14px 20px 14px;
  }
  .feature-list {
    padding: 12px 14px;
    gap: 8px 12px;
  }
  .footer-tagline {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .footer-banner {
    padding: 24px 0;
  }
  .footer-headline {
    font-size: 16px;
    margin-bottom: 20px;
  }
  .footer-showcase {
    gap: 12px;
  }
  .footer-code,
  .footer-features {
    min-width: 0;
    flex: 1 1 100%;
  }
  .footer-arrow {
    transform: rotate(90deg);
    font-size: 20px;
  }
  .footer-code pre {
    padding: 0 12px 16px 12px;
  }
  .footer-code code {
    font-size: 13px;
  }
  .code-comment {
    padding: 10px 14px 0;
    font-size: 12px;
  }
  .feature-list {
    padding: 10px 12px;
    gap: 6px 10px;
  }
  .feature-list li {
    font-size: 12px;
  }
  .footer-tagline {
    font-size: 12px;
    margin-top: 18px;
  }
}
</style>
