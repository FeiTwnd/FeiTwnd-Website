<script setup>
import { inject, onMounted, computed } from 'vue'
import { useBlogStore } from '@/stores'
import SidebarCard from '@/components/SidebarCard.vue'

const blogStore = useBlogStore()
const { articleTitle, articleMeta } = inject('setHero')

const info = computed(() => blogStore.personalInfo)

onMounted(() => {
  articleTitle.value = '关于'
  articleMeta.value = '关于我和这个博客'
})
</script>

<template>
  <div class="about-page">
    <div class="about-layout">
      <div class="about-inner">
        <div class="about-card">
          <!-- 关于我 -->
          <section class="about-section">
            <h3 class="section-title">
              <svg
                viewBox="0 0 24 24"
                width="17"
                height="17"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              关于我
            </h3>
            <div class="section-body">
              <p>
                你好，我是
                <strong>FeiTwnd</strong>，一名喜欢折腾代码的普通计科学生。
              </p>
              <p>
                江西理工大学计算机科学与技术 24
                级在读，主要兴趣和精力都投在了探索 Web 开发的方方面面。
              </p>
            </div>
          </section>

          <!-- 关于博客 -->
          <section class="about-section">
            <h3 class="section-title">
              <svg
                viewBox="0 0 24 24"
                width="17"
                height="17"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M2 3h6a4 4 0 014 4v14a3 3 0 00-3-3H2z" />
                <path d="M22 3h-6a4 4 0 00-4 4v14a3 3 0 013-3h7z" />
              </svg>
              关于博客
            </h3>
            <div class="section-body">
              <p>这个博客对我而言，远不止一个技术笔记本。</p>
              <p>
                如果你仔细看看，会发现这里除了技术分享，还有不少我的日常琐碎和内心所想，记录这些才是我最初建立这里的根本原因。
              </p>
              <p>
                我过去没有写日记的习惯，以至于许多重要的时刻和感受，都在时间的流逝中变得模糊不清。这让我觉得有些遗憾。
              </p>
              <p>
                于是，我想在这里诚实地留下一些痕迹。记录下解决问题时的思路，也记录下某个黄昏的触动；分享学到的知识，也分享那些不成熟但真实的思考。
              </p>
              <p>
                我相信，当未来的某一天回看这些文字，无论是为某个技术难点焦头烂额，还是为生活中一件小事感慨，这些记录都会让那个时刻的我，与现在的我重逢，并会心一笑。
              </p>
              <p>
                感谢你的到访。这里没什么特别，只是一个学生记录成长、思考和生活的地方。欢迎常来坐坐。
              </p>
            </div>
          </section>

          <!-- 联系我 -->
          <section class="about-section">
            <h3 class="section-title">
              <svg
                viewBox="0 0 24 24"
                width="17"
                height="17"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"
                />
                <polyline points="22,6 12,13 2,6" />
              </svg>
              联系我
            </h3>
            <div class="contact-list">
              <div class="contact-item">
                <span class="contact-label">QQ</span>
                <span class="contact-value">822824739</span>
              </div>
              <div v-if="info.email" class="contact-item">
                <span class="contact-label">邮箱</span>
                <a :href="`mailto:${info.email}`" class="contact-link">{{
                  info.email
                }}</a>
              </div>
              <div class="contact-item">
                <span class="contact-label">留言板</span>
                <router-link to="/message" class="contact-link"
                  >去留言板看看 →</router-link
                >
              </div>
            </div>
          </section>

          <!-- 关于这个网站 -->
          <section class="about-section">
            <h3 class="section-title">
              <svg
                viewBox="0 0 24 24"
                width="17"
                height="17"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <rect x="2" y="3" width="20" height="14" rx="2" ry="2" />
                <line x1="8" y1="21" x2="16" y2="21" />
                <line x1="12" y1="17" x2="12" y2="21" />
              </svg>
              关于这个网站
            </h3>
            <div class="section-body">
              <p>
                本网站含有<strong>主页 Home</strong>、<strong>博客 Blog</strong
                >、<strong>简历 CV</strong>、<strong>管理 Admin</strong>
                四个端，采用黑白简约风格。
              </p>
              <div class="tech-stack">
                <h4 class="tech-label">前端 Vue3</h4>
                <h4 class="tech-label">后端 Springboot3</h4>
              </div>
              <p v-if="info.github" class="github-hint">
                如果你喜欢这样的风格或对部分功能感兴趣，欢迎去
                <a
                  :href="info.github"
                  target="_blank"
                  rel="noopener"
                  class="inline-link"
                >
                  <svg
                    viewBox="0 0 24 24"
                    width="14"
                    height="14"
                    fill="currentColor"
                    style="vertical-align: -2px; margin-right: 2px"
                  >
                    <path
                      d="M12 .3a12 12 0 00-3.8 23.38c.6.12.83-.26.83-.57L9 21.07c-3.34.72-4.04-1.61-4.04-1.61-.55-1.39-1.34-1.76-1.34-1.76-1.08-.74.08-.73.08-.73 1.2.08 1.84 1.24 1.84 1.24 1.07 1.83 2.81 1.3 3.5 1 .1-.78.42-1.3.76-1.6-2.67-.3-5.47-1.33-5.47-5.93 0-1.31.47-2.38 1.24-3.22-.13-.3-.54-1.52.12-3.18 0 0 1-.33 3.3 1.23a11.5 11.5 0 016.02 0c2.28-1.56 3.29-1.23 3.29-1.23.66 1.66.25 2.88.12 3.18a4.65 4.65 0 011.23 3.22c0 4.61-2.81 5.63-5.48 5.93.43.37.81 1.1.81 2.22l-.01 3.29c0 .31.22.69.83.57A12 12 0 0012 .3"
                    /></svg
                  >GitHub
                </a>
                拿代码。
              </p>
            </div>
          </section>

          <!-- 版权 -->
          <section class="about-section about-section--last">
            <h4 class="copyright-title">版权声明</h4>
            <p class="copyright-text">
              本博客所有原创文章采用
              <strong>CC BY-NC-SA 4.0</strong>
              协议进行许可。转载请注明出处，禁止商业使用，演绎作品需以相同方式共享。
            </p>
          </section>
        </div>
      </div>

      <SidebarCard />
    </div>
  </div>
</template>

<style scoped>
.about-page {
  width: 100%;
}
.about-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.about-inner {
  flex: 1;
  min-width: 0;
}

.about-card {
  background: var(--blog-card, #fff);
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--blog-border-light, #ebeef5);
  overflow: hidden;
}

.about-section {
  padding: 24px 28px;
  border-bottom: 1px solid var(--blog-border-light, #ebeef5);
}
.about-section--last {
  border-bottom: none;
  background: var(--blog-hover, #fafafa);
}

/* 标题 */
.section-title {
  font-size: 15px;
  font-weight: 700;
  margin: 0 0 14px;
  color: var(--blog-text, #303133);
  display: flex;
  align-items: center;
  gap: 7px;
}

/* 正文 */
.section-body {
  font-size: 14px;
  color: var(--blog-text2, #606266);
  line-height: 1.85;
}
.section-body p {
  margin: 0 0 10px;
}
.section-body p:last-child {
  margin-bottom: 0;
}
.section-body strong {
  color: var(--blog-text, #303133);
  font-weight: 600;
}

/* 联系 */
.contact-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}
.contact-label {
  flex-shrink: 0;
  width: 48px;
  font-size: 12px;
  color: var(--blog-text3, #909399);
  font-weight: 500;
}
.contact-value {
  color: var(--blog-text, #303133);
  font-family: var(--blog-serif);
}
.contact-link {
  color: var(--blog-text, #303133);
  text-decoration: none;
  font-weight: 500;
  border-bottom: 1px dashed var(--blog-border, #e4e7ed);
  transition:
    border-color 0.2s,
    color 0.2s;
}
.contact-link:hover {
  border-bottom-style: solid;
}

/* 技术栈 */
.tech-stack {
  margin-top: 14px;
}
.tech-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--blog-text3, #909399);
  margin: 0 0 8px;
  letter-spacing: 0.5px;
}

.github-hint {
  margin-top: 14px;
  margin-bottom: 0;
}
.inline-link {
  color: var(--blog-text, #303133);
  font-weight: 600;
  text-decoration: none;
  border-bottom: 1px solid var(--blog-border, #e4e7ed);
  transition: border-color 0.2s;
}
.inline-link:hover {
  border-bottom-color: var(--blog-text, #303133);
}

/* 版权 */
.copyright-title {
  font-size: 13px;
  font-weight: 600;
  margin: 0 0 6px;
  color: var(--blog-text3, #909399);
}
.copyright-text {
  font-size: 13px;
  color: var(--blog-text3, #909399);
  margin: 0;
  line-height: 1.7;
}
.copyright-text strong {
  color: var(--blog-text2, #606266);
  font-weight: 600;
}

@media (max-width: 960px) {
  .about-layout {
    flex-direction: column;
  }
}
@media (max-width: 600px) {
  .about-section {
    padding: 16px;
  }
}
</style>
