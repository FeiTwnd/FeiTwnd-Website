<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { getPersonalInfo } from '@/api/personalInfo'
import { getExperiences } from '@/api/experience'
import { getSkills } from '@/api/skill'
import { recordVisitor } from '@/api/visitor'

/* ============ 数据 ============ */
const info = ref({})
const experiences = ref([])
const skills = ref([])
const loaded = ref(false)

/* 按 type 拆分经历 */
const education = computed(() => experiences.value.filter((e) => e.type === 0))
const work = computed(() => experiences.value.filter((e) => e.type === 1))
const projects = computed(() => experiences.value.filter((e) => e.type === 2))

/* ============ 暗黑模式 ============ */
const isDark = ref(false)
const initTheme = () => {
  const mq = window.matchMedia('(prefers-color-scheme: dark)')
  isDark.value = mq.matches
  applyTheme()
  mq.addEventListener('change', (e) => {
    isDark.value = e.matches
    applyTheme()
  })
}
const applyTheme = () => {
  document.documentElement.setAttribute(
    'data-theme',
    isDark.value ? 'dark' : 'light'
  )
}

/* ============ 日期格式 ============ */
const fmtDate = (d) => {
  if (!d) return '至今'
  const [y, m] = d.split('-')
  return `${y}.${m}`
}
const dateRange = (e) => `${fmtDate(e.startDate)} ~ ${fmtDate(e.endDate)}`

/* ============ 打印 ============ */
const printPage = () => window.print()

/* ============ 滚入动画 ============ */
const observeSections = () => {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible')
          observer.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.12 }
  )
  document
    .querySelectorAll('.fade-section')
    .forEach((el) => observer.observe(el))
}

/* ============ 初始化 ============ */
onMounted(async () => {
  initTheme()
  try {
    const [infoRes, expRes, skillRes] = await Promise.all([
      getPersonalInfo(),
      getExperiences(),
      getSkills()
    ])
    info.value = infoRes.data.data ?? {}
    experiences.value = expRes.data.data ?? []
    skills.value = skillRes.data.data ?? []
  } catch (e) {
    console.error('数据加载失败', e)
  }
  loaded.value = true
  await nextTick()
  observeSections()

  // 异步记录访客
  recordVisitor().catch(() => {})
})
</script>

<template>
  <div v-if="loaded" class="cv-page">
    <!-- 打印按钮 -->
    <button
      class="print-btn no-print"
      title="打印 / 导出 PDF"
      @click="printPage"
    >
      <svg
        viewBox="0 0 24 24"
        width="20"
        height="20"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <polyline points="6 9 6 2 18 2 18 9" />
        <path
          d="M6 18H4a2 2 0 01-2-2v-5a2 2 0 012-2h16a2 2 0 012 2v5a2 2 0 01-2 2h-2"
        />
        <rect x="6" y="14" width="12" height="8" />
      </svg>
    </button>

    <!-- ========== 头部个人信息 ========== -->
    <header class="hero fade-section">
      <h1 class="hero-name">{{ info.nickname }}</h1>
      <ul class="hero-meta">
        <li v-if="info.email">
          <svg
            viewBox="0 0 24 24"
            width="16"
            height="16"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <rect width="20" height="16" x="2" y="4" rx="2" />
            <path d="m22 7-8.97 5.7a1.94 1.94 0 01-2.06 0L2 7" />
          </svg>
          <a :href="'mailto:' + info.email">{{ info.email }}</a>
        </li>
        <li v-if="info.github">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
            <path
              d="M12 .3a12 12 0 00-3.8 23.38c.6.12.83-.26.83-.57L9 21.07c-3.34.72-4.04-1.61-4.04-1.61-.55-1.39-1.34-1.76-1.34-1.76-1.08-.74.08-.73.08-.73 1.2.08 1.84 1.24 1.84 1.24 1.07 1.83 2.81 1.3 3.5 1 .1-.78.42-1.3.76-1.6-2.67-.3-5.47-1.33-5.47-5.93 0-1.31.47-2.38 1.24-3.22-.13-.3-.54-1.52.12-3.18 0 0 1-.33 3.3 1.23a11.5 11.5 0 016.02 0c2.28-1.56 3.29-1.23 3.29-1.23.66 1.66.25 2.88.12 3.18a4.65 4.65 0 011.23 3.22c0 4.61-2.81 5.63-5.48 5.93.43.37.81 1.1.81 2.22l-.01 3.29c0 .31.22.69.83.57A12 12 0 0012 .3"
            />
          </svg>
          <a :href="info.github" target="_blank" rel="noopener">{{
            info.github.replace(/^https?:\/\/(www\.)?github\.com\//, '')
          }}</a>
        </li>
        <li v-if="info.website">
          <svg
            viewBox="0 0 24 24"
            width="16"
            height="16"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <circle cx="12" cy="12" r="10" />
            <path
              d="M2 12h20M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10A15.3 15.3 0 0112 2z"
            />
          </svg>
          <a
            :href="
              info.website.match(/^https?:\/\//)
                ? info.website
                : 'https://' + info.website
            "
            target="_blank"
            rel="noopener"
            >{{ info.website.replace(/^https?:\/\//, '') }}</a
          >
        </li>
      </ul>
    </header>

    <!-- ========== 教育经历 ========== -->
    <section v-if="education.length" class="section fade-section">
      <h2 class="section-title">
        <svg
          viewBox="0 0 24 24"
          width="22"
          height="22"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M22 10v6M2 10l10-5 10 5-10 5z" />
          <path d="M6 12v5c0 1.1 2.7 3 6 3s6-1.9 6-3v-5" />
        </svg>
        教育经历
      </h2>
      <div class="timeline">
        <div v-for="item in education" :key="item.id" class="timeline-item">
          <div class="tl-dot" />
          <div class="tl-card">
            <div class="tl-head">
              <img
                v-if="item.logoUrl"
                :src="item.logoUrl"
                class="tl-logo"
                :alt="item.title"
              />
              <div class="tl-info">
                <h3>{{ item.title }}</h3>
                <p v-if="item.subtitle" class="tl-sub">{{ item.subtitle }}</p>
              </div>
              <span class="tl-date">{{ dateRange(item) }}</span>
            </div>
            <p v-if="item.content" class="tl-content">{{ item.content }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 工作 / 实习经历 ========== -->
    <section v-if="work.length" class="section fade-section">
      <h2 class="section-title">
        <svg
          viewBox="0 0 24 24"
          width="22"
          height="22"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <rect width="20" height="14" x="2" y="7" rx="2" />
          <path d="M16 7V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v2" />
        </svg>
        实习 & 工作经历
      </h2>
      <div class="timeline">
        <div v-for="item in work" :key="item.id" class="timeline-item">
          <div class="tl-dot" />
          <div class="tl-card">
            <div class="tl-head">
              <img
                v-if="item.logoUrl"
                :src="item.logoUrl"
                class="tl-logo"
                :alt="item.title"
              />
              <div class="tl-info">
                <h3>{{ item.title }}</h3>
                <p v-if="item.subtitle" class="tl-sub">{{ item.subtitle }}</p>
              </div>
              <span class="tl-date">{{ dateRange(item) }}</span>
            </div>
            <p v-if="item.content" class="tl-content">{{ item.content }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 项目经历 ========== -->
    <section v-if="projects.length" class="section fade-section">
      <h2 class="section-title">
        <svg
          viewBox="0 0 24 24"
          width="22"
          height="22"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M12 2L2 7l10 5 10-5-10-5z" />
          <path d="M2 17l10 5 10-5M2 12l10 5 10-5" />
        </svg>
        项目经历
      </h2>
      <div class="timeline">
        <div v-for="item in projects" :key="item.id" class="timeline-item">
          <div class="tl-dot" />
          <div class="tl-card">
            <div class="tl-head">
              <img
                v-if="item.logoUrl"
                :src="item.logoUrl"
                class="tl-logo"
                :alt="item.title"
              />
              <div class="tl-info">
                <h3>{{ item.title }}</h3>
                <p v-if="item.subtitle" class="tl-sub">{{ item.subtitle }}</p>
              </div>
              <span class="tl-date">{{ dateRange(item) }}</span>
            </div>
            <p v-if="item.content" class="tl-content">{{ item.content }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 技术栈 ========== -->
    <section v-if="skills.length" class="section fade-section">
      <h2 class="section-title">
        <svg
          viewBox="0 0 24 24"
          width="22"
          height="22"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path
            d="M14.7 6.3a1 1 0 000 1.4l1.6 1.6a1 1 0 001.4 0l3.77-3.77a6 6 0 01-7.94 7.94l-6.91 6.91a2.12 2.12 0 01-3-3l6.91-6.91a6 6 0 017.94-7.94l-3.76 3.76z"
          />
        </svg>
        技术栈
      </h2>
      <div class="skill-list">
        <div v-for="s in skills" :key="s.id" class="skill-row">
          <img v-if="s.icon" :src="s.icon" :alt="s.name" class="skill-logo" />
          <div v-else class="skill-logo skill-logo--text">
            {{ s.name?.[0] }}
          </div>
          <div class="skill-info">
            <h4>{{ s.name }}</h4>
            <p v-if="s.description">{{ s.description }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>

  <!-- 加载动画 -->
  <div v-else class="cv-loading">
    <div class="spinner" />
  </div>
</template>

<style scoped>
/* ====== 页面 ====== */
.cv-page {
  position: relative;
  max-width: 760px;
  margin: 0 auto;
  padding: 52px 28px 40px;
  color: var(--cv-text);
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, 'Noto Sans SC', sans-serif;
  line-height: 1.7;
}

/* ====== 打印按钮 ====== */
.print-btn {
  position: fixed;
  top: 20px;
  right: 24px;
  z-index: 100;
  width: 40px;
  height: 40px;
  border: 1px solid var(--cv-border);
  border-radius: 8px;
  background: var(--cv-card);
  color: var(--cv-text);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--cv-shadow);
  transition:
    background 0.2s,
    transform 0.15s,
    box-shadow 0.2s;
}
.print-btn:hover {
  background: var(--cv-hover);
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
}
.print-btn:active {
  transform: scale(0.95);
}

/* ====== 加载 ====== */
.cv-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--cv-border);
  border-top-color: var(--cv-text);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ====== 滚入动画 ====== */
.fade-section {
  opacity: 0;
  transform: translateY(28px);
  transition:
    opacity 0.6s ease,
    transform 0.6s ease;
}
.fade-section.visible {
  opacity: 1;
  transform: translateY(0);
}

/* ====== Hero ====== */
.hero {
  text-align: center;
  padding-bottom: 36px;
  border-bottom: 1px solid var(--cv-border);
  margin-bottom: 40px;
}
.hero-name {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 14px;
  letter-spacing: -0.5px;
}
.hero-meta {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px 28px;
}
.hero-meta li {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--cv-muted);
  background: var(--cv-hover);
  border: 1px solid var(--cv-border);
  border-radius: 20px;
  padding: 5px 14px;
  transition: background 0.2s;
}
.hero-meta li:hover {
  background: var(--cv-border);
}
.hero-meta a {
  color: var(--cv-text);
  text-decoration: none;
}
.hero-meta svg {
  flex-shrink: 0;
  color: var(--cv-muted);
}

/* ====== Section ====== */
.section {
  margin-bottom: 40px;
}
.section-title {
  font-size: 22px;
  font-weight: 800;
  margin: 0 0 22px;
  letter-spacing: -0.3px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.section-title svg {
  color: var(--cv-muted);
  flex-shrink: 0;
}

/* ====== Timeline ====== */
.timeline {
  position: relative;
  padding-left: 28px;
}
.timeline::before {
  content: '';
  position: absolute;
  left: 5px;
  top: 8px;
  bottom: 8px;
  width: 1.5px;
  background: var(--cv-line);
}
.timeline-item {
  position: relative;
  margin-bottom: 28px;
}
.timeline-item:last-child {
  margin-bottom: 0;
}
.tl-dot {
  position: absolute;
  left: -28px;
  top: 10px;
  width: 11px;
  height: 11px;
  border-radius: 50%;
  background: var(--cv-card);
  border: 2.5px solid var(--cv-dot);
  z-index: 1;
  transition:
    border-color 0.2s,
    transform 0.2s;
}
.timeline-item:hover .tl-dot {
  border-color: var(--cv-text);
  transform: scale(1.3);
}
.tl-card {
  background: var(--cv-card);
  border: 1px solid var(--cv-border);
  border-radius: 10px;
  padding: 18px 22px;
  box-shadow: var(--cv-shadow);
  transition:
    box-shadow 0.25s,
    transform 0.25s;
}
.tl-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}
.tl-head {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}
.tl-logo {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: contain;
  flex-shrink: 0;
  background: var(--cv-hover);
  padding: 3px;
}
.tl-info {
  flex: 1;
  min-width: 0;
}
.tl-info h3 {
  font-size: 17px;
  font-weight: 700;
  margin: 0;
  line-height: 1.35;
}
.tl-date {
  font-size: 13px;
  color: var(--cv-muted);
  white-space: nowrap;
  flex-shrink: 0;
  margin-left: auto;
  padding-left: 12px;
}
.tl-sub {
  font-size: 14.5px;
  color: var(--cv-muted);
  margin: 6px 0 0;
  font-weight: 500;
}
.tl-content {
  font-size: 14.5px;
  color: var(--cv-muted);
  margin: 10px 0 0;
  line-height: 1.7;
  white-space: pre-line;
}

/* ====== 技能列表 ====== */
.skill-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.skill-row {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  background: var(--cv-card);
  border: 1px solid var(--cv-border);
  border-radius: 10px;
  padding: 14px 18px;
  box-shadow: var(--cv-shadow);
  transition:
    box-shadow 0.25s,
    transform 0.25s;
}
.skill-row:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}
.skill-logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: contain;
  flex-shrink: 0;
  margin-top: 2px;
}
.skill-logo--text {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cv-border);
  font-weight: 700;
  font-size: 18px;
  color: var(--cv-text);
}
.skill-info {
  flex: 1;
  min-width: 0;
}
.skill-info h4 {
  font-size: 15px;
  font-weight: 700;
  margin: 0;
  line-height: 1.4;
}
.skill-info p {
  font-size: 13.5px;
  color: var(--cv-muted);
  margin: 4px 0 0;
  line-height: 1.6;
}

/* ====== 响应式 ====== */
@media (max-width: 600px) {
  .cv-page {
    padding: 32px 16px 28px;
  }
  .hero-name {
    font-size: 26px;
  }
  .hero-meta {
    gap: 8px 12px;
  }
  .tl-head {
    flex-wrap: wrap;
  }
  .print-btn {
    top: 12px;
    right: 12px;
  }
}

/* ====== 打印样式 ====== */
@media print {
  /* 隐藏打印按钮 */
  .no-print {
    display: none !important;
  }
  /* 取消动画 */
  .fade-section {
    opacity: 1 !important;
    transform: none !important;
    transition: none !important;
  }
  /* 页面布局 */
  .cv-page {
    max-width: 100%;
    padding: 0;
    margin: 0;
  }
  /* 取消卡片悬停效果 */
  .tl-card,
  .skill-row {
    box-shadow: none;
    border-color: #ddd;
  }
  .tl-card:hover,
  .skill-row:hover {
    transform: none;
    box-shadow: none;
  }
  /* 页头页脚由浏览器自动生成（日期+URL） */
  @page {
    size: A4;
    margin: 15mm 12mm;
  }
}
</style>
