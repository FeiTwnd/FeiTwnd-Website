<script setup>
import { onMounted, ref } from 'vue'

const startYear = 2025
const currentYear = ref(2025)

onMounted(() => {
  currentYear.value = new Date().getFullYear()

  document.querySelectorAll('.link').forEach((link, index) => {
    link.style.setProperty('--link-index', index + 1)
  })
})
</script>

<template>
  <div class="home-container">
    <main class="card" role="main">
      <div class="avatar-container">
        <img src="@/assets/avatar.png" alt="FeiTwnd的头像" />
      </div>
      <h1 data-name>FeiTwnd</h1>
      <p class="tagline">初出茅庐 | 科班码农 | 拾枝者 · 欢迎来到我的主页</p>

      <nav class="links" aria-label="站点导航">
        <a
          class="link"
          href="https://blog.feitwnd.cc"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="iconfont icon-boke"></span>
          博客
        </a>
        <a
          class="link"
          href="https://cv.feitwnd.cc"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="iconfont icon-jianli"></span>
          简历
        </a>
        <a
          class="link"
          href="mailto:kiwoba@qq.com"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="iconfont icon-youxiang"></span>
          邮箱
        </a>
        <a
          class="link"
          href="https://github.com/FeiTwnd"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="iconfont icon-github"></span>
          GitHub
        </a>
        <a
          class="link"
          href="https://space.bilibili.com/502786603"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="iconfont icon-bilibili-line"></span>
          Bilibili
        </a>
        <a
          class="link"
          href="https://blog.csdn.net/FeiTwnd"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="iconfont icon-csdn1"></span>
          CSDN
        </a>
        <a
          class="link"
          href="https://leetcode.cn/u/competent-maxwellmll/"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="iconfont icon-code"></span>
          LeetCode
        </a>
      </nav>

      <footer>
        <div class="beian-info">
          <span>豫公网安备41132702000130号</span>
          <span class="beian-divider">|</span>
          <a
            href="https://beian.miit.gov.cn/"
            target="_blank"
            rel="noopener noreferrer"
          >
            豫ICP备2025138023号-1
          </a>
          <span class="beian-divider">|</span>
          <span>© {{ startYear }}-{{ currentYear }} FeiTwnd.</span>
        </div>
      </footer>
    </main>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

:root {
  --bg-light: #fafafa;
  --bg-dark: #1a1a1a;
  --card-light: #ffffff;
  --card-dark: #242424;
  --border-light: #e0e0e0;
  --border-dark: #333333;
  --text-light: #121212;
  --text-dark: #f0f0f0;
  --muted-light: #666666;
  --muted-dark: #9e9e9e;
  --hover-light: #f0f0f0;
  --hover-dark: #333333;
}

html,
body {
  height: 100%;
  width: 100%;
}

body {
  background-color: var(--bg-light);
  color: var(--text-light);
  font:
    16px/1.6 system-ui,
    -apple-system,
    Segoe UI,
    Roboto,
    Helvetica,
    Arial,
    sans-serif;
  transition: background-color 0.3s ease;
}

@media (prefers-color-scheme: dark) {
  body {
    background-color: var(--bg-dark);
    color: var(--text-dark);
  }
}

.home-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 卡片容器样式 */
.card {
  width: 100%;
  max-width: 1200px;
  margin-inline: auto;
  padding: clamp(2.5rem, 7vw, 4.5rem);
  border-radius: 20px;
  background-color: var(--card-light);
  border: 1px solid var(--border-light);
  box-shadow: 0 4px 25px rgba(0, 0, 0, 0.06);
  text-align: center;
  transition: all 0.3s ease;
  max-height: 95vh;
  overflow-y: auto;
  animation: fadeIn 0.6s ease-out forwards;
}

@media (prefers-color-scheme: dark) {
  .card {
    background-color: var(--card-dark);
    border-color: var(--border-dark);
    box-shadow: 0 4px 25px rgba(0, 0, 0, 0.25);
  }
}

/* 头像容器 */
.avatar-container {
  width: 150px;
  height: 150px;
  margin: 0 auto 2rem;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid var(--border-light);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.12);
  transition:
    transform 0.3s ease,
    border-color 0.3s ease;
  animation: fadeIn 0.5s ease-out 0.1s forwards;
  opacity: 0;
}

.avatar-container:hover {
  transform: scale(1.05);
}

.avatar-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  filter: contrast(1.1) brightness(1.05);
}

@media (prefers-color-scheme: dark) {
  .avatar-container {
    border-color: var(--border-dark);
  }
  .avatar-container img {
    filter: contrast(1.15) brightness(0.95);
  }
}

/* 标题 */
h1 {
  margin: 0 0 0.8rem;
  font-size: clamp(2.2rem, 6vw, 3.2rem);
  letter-spacing: -0.02em;
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0;
}

/* 标语 */
.tagline {
  margin: 0 0 2.5rem;
  color: var(--muted-light);
  font-size: clamp(1.2rem, 3vw, 1.4rem);
  font-weight: 400;
  animation: fadeIn 0.5s ease-out 0.4s forwards;
  opacity: 0;
}

@media (prefers-color-scheme: dark) {
  .tagline {
    color: var(--muted-dark);
  }
}

/* 图标字体 */
.iconfont {
  font-size: 22px;
  width: 26px;
  text-align: center;
}

/* 链接区域 */
.links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
  margin-top: 1.5rem;
}

/* 链接样式 */
.link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 12px;
  border-radius: 12px;
  border: 1px solid var(--border-light);
  background-color: transparent;
  color: inherit;
  text-decoration: none;
  transition: all 0.2s ease;
  font-weight: 500;
  font-size: 15px;
  animation: fadeIn 0.6s ease-out forwards;
  animation-delay: calc(var(--link-index) * 0.05s + 0.5s);
  opacity: 0;
}

.link:hover {
  background-color: var(--hover-light);
  border-color: currentColor;
  transform: translateY(-2px);
}

@media (prefers-color-scheme: dark) {
  .link {
    border-color: var(--border-dark);
  }
  .link:hover {
    background-color: var(--hover-dark);
  }
}

/* 页脚 */
footer {
  margin-top: 3.2rem;
  color: var(--muted-light);
  font-size: 0.9rem;
  line-height: 1.6;
  animation: fadeIn 0.6s ease-out 0.8s forwards;
  opacity: 0;
}

@media (prefers-color-scheme: dark) {
  footer {
    color: var(--muted-dark);
  }
}

.beian-info {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 0.5rem 1rem;
  margin-top: 0.5rem;
}

.beian-info a {
  color: inherit;
  text-decoration: none;
  border-bottom: 1px dotted currentColor;
  transition: border-color 0.2s ease;
}

.beian-info a:hover {
  border-bottom-style: solid;
}

.beian-divider {
  opacity: 0.5;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 900px) {
  .links {
    grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
  }
}

@media (max-width: 600px) {
  .card {
    padding: clamp(2rem, 5vw, 3rem);
  }
  .links {
    grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
  }
}

@media (max-width: 600px), (max-height: 700px) {
  .card {
    max-height: none;
    overflow: visible;
    margin-block: 12px;
  }
}
</style>
