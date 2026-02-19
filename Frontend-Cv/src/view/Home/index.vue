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

/* ============ 技能弹窗 ============ */
const skillDialogVisible = ref(false)
const activeSkill = ref(null)
const openSkill = (s) => {
  activeSkill.value = s
  skillDialogVisible.value = true
}

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
const dateRange = (e) => `${fmtDate(e.startDate)} — ${fmtDate(e.endDate)}`

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
    <!-- ========== 头部个人信息 ========== -->
    <header class="hero fade-section">
      <div class="hero-avatar" v-if="info.avatar">
        <img :src="info.avatar" :alt="info.nickname" />
      </div>
      <h1 class="hero-name">{{ info.nickname }}</h1>
      <p v-if="info.tag" class="hero-tag">{{ info.tag }}</p>
      <p v-if="info.description" class="hero-desc">{{ info.description }}</p>

      <ul class="hero-meta">
        <li v-if="info.location">
          <svg
            viewBox="0 0 24 24"
            width="16"
            height="16"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z" />
            <circle cx="12" cy="10" r="3" />
          </svg>
          <span>{{ info.location }}</span>
        </li>
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
          <a :href="info.github" target="_blank" rel="noopener">GitHub</a>
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
          <a :href="info.website" target="_blank" rel="noopener">{{
            info.website.replace(/^https?:\/\//, '')
          }}</a>
        </li>
      </ul>
    </header>

    <!-- ========== 教育经历 ========== -->
    <section v-if="education.length" class="section fade-section">
      <h2 class="section-title">
        <svg
          viewBox="0 0 24 24"
          width="20"
          height="20"
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
          width="20"
          height="20"
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
          width="20"
          height="20"
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

    <!-- ========== 技能 ========== -->
    <section v-if="skills.length" class="section fade-section">
      <h2 class="section-title">
        <svg
          viewBox="0 0 24 24"
          width="20"
          height="20"
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
      <div class="skill-grid">
        <div
          v-for="s in skills"
          :key="s.id"
          class="skill-item"
          :title="s.name"
          @click="openSkill(s)"
        >
          <img v-if="s.icon" :src="s.icon" :alt="s.name" class="skill-icon" />
          <div v-else class="skill-icon skill-icon--text">
            {{ s.name?.[0] }}
          </div>
          <span class="skill-name">{{ s.name }}</span>
        </div>
      </div>
    </section>

    <!-- 技能描述弹窗 -->
    <el-dialog
      v-model="skillDialogVisible"
      :title="activeSkill?.name"
      width="420px"
      class="skill-dialog"
    >
      <div class="skill-dialog-body">
        <img
          v-if="activeSkill?.icon"
          :src="activeSkill.icon"
          :alt="activeSkill.name"
          class="skill-dialog-icon"
        />
        <p>{{ activeSkill?.description || '暂无描述' }}</p>
      </div>
    </el-dialog>

    <!-- ========== 页脚 ========== -->
    <footer class="footer fade-section">
      <p>© {{ new Date().getFullYear() }} {{ info.nickname || 'FeiTwnd' }}</p>
    </footer>
  </div>

  <!-- 加载动画 -->
  <div v-else class="cv-loading">
    <div class="spinner" />
  </div>
</template>

<style scoped>
/* ====== 页面 ====== */
.cv-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 48px 24px 32px;
  color: var(--cv-text);
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, 'Noto Sans SC', sans-serif;
  line-height: 1.6;
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
  padding-bottom: 40px;
  border-bottom: 1px solid var(--cv-border);
  margin-bottom: 36px;
}
.hero-avatar {
  margin-bottom: 16px;
}
.hero-avatar img {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--cv-border);
  transition: transform 0.3s;
}
.hero-avatar img:hover {
  transform: scale(1.06);
}
.hero-name {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 4px;
  letter-spacing: -0.5px;
}
.hero-tag {
  font-size: 15px;
  color: var(--cv-muted);
  margin: 0 0 6px;
}
.hero-desc {
  font-size: 14px;
  color: var(--cv-muted);
  margin: 0 0 18px;
  max-width: 480px;
  margin-left: auto;
  margin-right: auto;
}
.hero-meta {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16px 24px;
}
.hero-meta li {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--cv-muted);
}
.hero-meta a {
  color: var(--cv-text);
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.2s;
}
.hero-meta a:hover {
  border-bottom-color: var(--cv-text);
}
.hero-meta svg {
  flex-shrink: 0;
  color: var(--cv-muted);
}

/* ====== Section ====== */
.section {
  margin-bottom: 36px;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  letter-spacing: -0.3px;
}
.section-title svg {
  color: var(--cv-muted);
  flex-shrink: 0;
}

/* ====== Timeline ====== */
.timeline {
  position: relative;
  padding-left: 24px;
}
.timeline::before {
  content: '';
  position: absolute;
  left: 5px;
  top: 6px;
  bottom: 6px;
  width: 1px;
  background: var(--cv-line);
}
.timeline-item {
  position: relative;
  margin-bottom: 24px;
}
.timeline-item:last-child {
  margin-bottom: 0;
}
.tl-dot {
  position: absolute;
  left: -24px;
  top: 8px;
  width: 11px;
  height: 11px;
  border-radius: 50%;
  background: var(--cv-card);
  border: 2px solid var(--cv-dot);
  z-index: 1;
  transition:
    border-color 0.2s,
    transform 0.2s;
}
.timeline-item:hover .tl-dot {
  border-color: var(--cv-text);
  transform: scale(1.25);
}
.tl-card {
  background: var(--cv-card);
  border: 1px solid var(--cv-border);
  border-radius: 8px;
  padding: 16px 18px;
  box-shadow: var(--cv-shadow);
  transition:
    box-shadow 0.25s,
    transform 0.25s;
}
.tl-card:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}
.tl-head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.tl-logo {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  object-fit: contain;
  flex-shrink: 0;
  background: var(--cv-hover);
  padding: 2px;
}
.tl-info {
  flex: 1;
  min-width: 0;
}
.tl-info h3 {
  font-size: 15px;
  font-weight: 600;
  margin: 0;
  line-height: 1.3;
}
.tl-sub {
  font-size: 13px;
  color: var(--cv-muted);
  margin: 2px 0 0;
}
.tl-date {
  font-size: 12px;
  color: var(--cv-muted);
  white-space: nowrap;
  flex-shrink: 0;
}
.tl-content {
  font-size: 13.5px;
  color: var(--cv-muted);
  margin: 4px 0 0;
  line-height: 1.65;
  white-space: pre-line;
}

/* ====== 技能网格 ====== */
.skill-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(72px, 1fr));
  gap: 14px;
}
.skill-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 12px 4px;
  border-radius: 8px;
  transition:
    background 0.2s,
    transform 0.2s;
}
.skill-item:hover {
  background: var(--cv-hover);
  transform: translateY(-3px);
}
.skill-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: contain;
}
.skill-icon--text {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cv-border);
  font-weight: 700;
  font-size: 18px;
  color: var(--cv-text);
}
.skill-name {
  font-size: 11.5px;
  color: var(--cv-muted);
  text-align: center;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

/* ====== 技能弹窗 ====== */
.skill-dialog-body {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.skill-dialog-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: contain;
  flex-shrink: 0;
}
.skill-dialog-body p {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: var(--cv-text);
}

/* ====== Footer ====== */
.footer {
  text-align: center;
  padding-top: 28px;
  border-top: 1px solid var(--cv-border);
  margin-top: 8px;
}
.footer p {
  font-size: 12px;
  color: var(--cv-muted);
  margin: 0;
}

/* ====== 响应式 ====== */
@media (max-width: 600px) {
  .cv-page {
    padding: 28px 16px 24px;
  }
  .hero-name {
    font-size: 24px;
  }
  .hero-meta {
    gap: 10px 16px;
  }
  .tl-head {
    flex-wrap: wrap;
  }
  .tl-date {
    width: 100%;
    margin-top: 2px;
  }
  .skill-grid {
    grid-template-columns: repeat(auto-fill, minmax(64px, 1fr));
    gap: 10px;
  }
}
</style>
