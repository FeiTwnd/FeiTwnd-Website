<script setup>
import { onMounted, ref, watch, nextTick } from 'vue'
import { getSocialMediaAPI } from '@/api/socialMedia'
import { getPersonalInfoAPI } from '@/api/personalInfo'
import { recordVisitorAPI } from '@/api/visitor'
import { getSystemConfigAPI } from '@/api/systemConfig'

// 网站创建年份
const startYear = ref(0)
// 当前年份
const currentYear = ref(0)

// 个人信息
const personalInfo = ref({})
// 社交媒体信息
const socialMedia = ref([])
// icp备案信息
const icpBeian = ref('')
// 公安备案信息
const gonganBeian = ref('')
// 数据加载状态
const isDataLoaded = ref(false)

onMounted(async () => {
  // 获取当前年份
  currentYear.value = new Date().getFullYear()

  // 初始化主题检测
  initTheme()

  // 获取数据
  await fetchData()

  // 记录访客（异步，不阻塞页面渲染）
  recordVisitorAPI().catch((err) => {
    console.error('访客记录失败:', err)
  })

  // 数据加载完成后设置链接动画
  await nextTick()
  setupLinkAnimations()
})

// 主题状态
const isDarkMode = ref(false)

// 初始化主题检测
const initTheme = () => {
  // 检测系统主题偏好
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  isDarkMode.value = prefersDark
  // 立即应用主题
  applyTheme()

  // 监听系统主题变化
  window
    .matchMedia('(prefers-color-scheme: dark)')
    .addEventListener('change', (e) => {
      isDarkMode.value = e.matches
      applyTheme()
    })
}

// 应用主题样式
const applyTheme = () => {
  const root = document.documentElement

  if (isDarkMode.value) {
    // 黑暗模式
    root.style.setProperty('--bg-light', '#1a1a1a')
    root.style.setProperty('--card-light', '#242424')
    root.style.setProperty('--border-light', '#333333')
    root.style.setProperty('--text-light', '#f0f0f0')
    root.style.setProperty('--muted-light', '#9e9e9e')
    root.style.setProperty('--hover-light', '#333333')
  } else {
    // 亮色模式
    root.style.setProperty('--bg-light', '#fafafa')
    root.style.setProperty('--card-light', '#ffffff')
    root.style.setProperty('--border-light', '#e0e0e0')
    root.style.setProperty('--text-light', '#121212')
    root.style.setProperty('--muted-light', '#666666')
    root.style.setProperty('--hover-light', '#f0f0f0')
  }
}

// 获取数据
const fetchData = async () => {
  try {
    // 并行请求数据
    const [personalRes, socialRes, icpRes, gonganRes, startTimeRes] =
      await Promise.all([
        getPersonalInfoAPI(),
        getSocialMediaAPI(),
        getSystemConfigAPI('icp-beian'),
        getSystemConfigAPI('gongan-beian'),
        getSystemConfigAPI('start-time')
      ])
    personalInfo.value = personalRes.data.data || {}
    socialMedia.value = socialRes.data.data || []
    icpBeian.value = icpRes?.data?.data?.configValue || ''
    gonganBeian.value = gonganRes?.data?.data?.configValue || ''
    startYear.value = startTimeRes?.data?.data?.configValue.split('-')[0]
      ? parseInt(startTimeRes.data.data.configValue.split('-')[0])
      : startYear.value
    isDataLoaded.value = true
  } catch (error) {
    console.error('数据获取失败:', error)
    socialMedia.value = []
    isDataLoaded.value = true
  }
}

// 设置链接动画延迟
const setupLinkAnimations = () => {
  // 等待DOM更新完成
  nextTick(() => {
    const links = document.querySelectorAll('.link')
    links.forEach((link, index) => {
      link.style.setProperty('--link-index', index + 1)
    })
  })
}

// 监听数据变化，重新设置动画
watch([() => socialMedia.value, isDataLoaded], () => {
  if (isDataLoaded.value && socialMedia.value.length > 0) {
    setupLinkAnimations()
  }
})
</script>

<template>
  <div class="home-container">
    <main class="card" role="main">
      <!-- 头像 -->
      <div class="avatar-container">
        <img :src="personalInfo.avatar" alt="头像" />
      </div>

      <!-- 昵称 -->
      <h1 data-name>{{ personalInfo.nickname }}</h1>

      <!-- 标签 -->
      <p class="tagline">{{ personalInfo.tag }} · 欢迎来到我的主页</p>

      <!-- 社交媒体链接 -->
      <nav class="links" aria-label="站点导航" v-if="socialMedia.length > 0">
        <a
          v-for="item in socialMedia"
          :key="item.id"
          class="link"
          :href="item.link"
          target="_blank"
          :title="item.name"
        >
          <span :class="`iconfont icon-${item.icon}`"></span>
          {{ item.name }}
        </a>
      </nav>

      <!-- 页脚 -->
      <footer>
        <div class="beian-info">
          <span>{{ gonganBeian }}</span>
          <span class="beian-divider">|</span>
          <a
            href="https://beian.miit.gov.cn/"
            target="_blank"
            rel="noopener noreferrer"
          >
            {{ icpBeian }}
          </a>
          <span class="beian-divider">|</span>
          <span
            >© {{ startYear }}-{{ currentYear }}
            {{ personalInfo.nickname }}.</span
          >
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

html,
body {
  height: 100%;
  width: 100%;
}

:root {
  --bg-light: #fafafa;
  --card-light: #ffffff;
  --border-light: #e0e0e0;
  --text-light: #121212;
  --muted-light: #666666;
  --hover-light: #f0f0f0;
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
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
}

.home-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
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
  transition: filter 0.3s ease;
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

/* 页脚 */
footer {
  margin-top: 3.2rem;
  color: var(--muted-light);
  font-size: 0.9rem;
  line-height: 1.6;
  animation: fadeIn 0.6s ease-out 0.8s forwards;
  opacity: 0;
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

@media (max-width: 768px) {
  .card {
    padding: 3rem 2rem;
  }
}

@media (max-width: 600px) {
  .home-container {
    height: auto;
    min-height: 100vh;
    padding: 1rem;
    align-items: flex-start;
  }

  .card {
    padding: 2.5rem 1.5rem;
    max-height: none;
    overflow: visible;
  }

  .links {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .tagline {
    font-size: 1rem;
    margin-bottom: 2rem;
  }

  .avatar-container {
    width: 120px;
    height: 120px;
  }
}

@media (max-height: 720px) {
  .home-container {
    height: auto;
    min-height: 100vh;
    padding: 1rem;
    align-items: flex-start;
  }

  .card {
    max-height: none;
    overflow: visible;
  }
}
</style>
