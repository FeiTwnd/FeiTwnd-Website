<script setup>
import { inject, onMounted } from 'vue'
import { useBlogStore } from '@/stores'
import SidebarCard from '@/components/SidebarCard.vue'

const blogStore = useBlogStore()
const { articleTitle, articleMeta } = inject('setHero')

onMounted(() => {
  articleTitle.value = '关于'
  articleMeta.value = '关于我和这个博客'
})
</script>

<template>
  <div class="about-page">
    <div class="about-layout">
      <div class="about-inner">
        <!-- 个人信息卡片 -->
        <div class="about-card profile-card">
          <div class="profile-top">
            <img
              v-if="blogStore.personalInfo.avatar"
              :src="blogStore.personalInfo.avatar"
              class="profile-avatar"
            />
            <div class="profile-info">
              <h2 class="profile-name">
                {{ blogStore.personalInfo.nickname }}
              </h2>
              <p v-if="blogStore.personalInfo.tag" class="profile-tag">
                {{ blogStore.personalInfo.tag }}
              </p>
            </div>
          </div>
          <p v-if="blogStore.personalInfo.description" class="profile-desc">
            {{ blogStore.personalInfo.description }}
          </p>
          <div class="profile-meta">
            <span v-if="blogStore.personalInfo.location" class="meta-item">
              <i class="iconfont icon-position" />
              {{ blogStore.personalInfo.location }}
            </span>
            <span v-if="blogStore.personalInfo.email" class="meta-item">
              <i class="iconfont icon-youxiang" />
              {{ blogStore.personalInfo.email }}
            </span>
            <a
              v-if="blogStore.personalInfo.github"
              :href="blogStore.personalInfo.github"
              target="_blank"
              rel="noopener"
              class="meta-item meta-link"
            >
              <svg
                viewBox="0 0 24 24"
                width="14"
                height="14"
                fill="currentColor"
              >
                <path
                  d="M12 .3a12 12 0 00-3.8 23.38c.6.12.83-.26.83-.57L9 21.07c-3.34.72-4.04-1.61-4.04-1.61-.55-1.39-1.34-1.76-1.34-1.76-1.08-.74.08-.73.08-.73 1.2.08 1.84 1.24 1.84 1.24 1.07 1.83 2.81 1.3 3.5 1 .1-.78.42-1.3.76-1.6-2.67-.3-5.47-1.33-5.47-5.93 0-1.31.47-2.38 1.24-3.22-.13-.3-.54-1.52.12-3.18 0 0 1-.33 3.3 1.23a11.5 11.5 0 016.02 0c2.28-1.56 3.29-1.23 3.29-1.23.66 1.66.25 2.88.12 3.18a4.65 4.65 0 011.23 3.22c0 4.61-2.81 5.63-5.48 5.93.43.37.81 1.1.81 2.22l-.01 3.29c0 .31.22.69.83.57A12 12 0 0012 .3"
                />
              </svg>
              GitHub
            </a>
          </div>
        </div>

        <!-- 博客统计 -->
        <div class="about-card">
          <h3 class="card-title"><i class="iconfont icon-eye" /> 博客统计</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-val">{{
                blogStore.report.articleTotalCount ?? 0
              }}</span>
              <span class="stat-lbl">文章</span>
            </div>
            <div class="stat-item">
              <span class="stat-val">{{
                blogStore.report.categoryTotalCount ?? 0
              }}</span>
              <span class="stat-lbl">分类</span>
            </div>
            <div class="stat-item">
              <span class="stat-val">{{
                blogStore.report.tagTotalCount ?? 0
              }}</span>
              <span class="stat-lbl">标签</span>
            </div>
            <div class="stat-item">
              <span class="stat-val">{{
                blogStore.report.viewTotalCount ?? 0
              }}</span>
              <span class="stat-lbl">浏览</span>
            </div>
            <div class="stat-item">
              <span class="stat-val">{{
                blogStore.report.visitorTotalCount ?? 0
              }}</span>
              <span class="stat-lbl">访客</span>
            </div>
          </div>
        </div>

        <!-- 音乐 -->
        <div v-if="blogStore.musics.length" class="about-card">
          <h3 class="card-title"><i class="iconfont icon-yinle" /> 音乐</h3>
          <div class="music-grid">
            <div v-for="m in blogStore.musics" :key="m.id" class="music-item">
              <img
                v-if="m.coverImage"
                :src="m.coverImage"
                class="music-cover"
              />
              <div class="music-info">
                <span class="music-name">{{ m.title }}</span>
                <span class="music-artist">{{ m.artist }}</span>
              </div>
              <a v-if="m.url" :href="m.url" target="_blank" class="music-link">
                <i class="iconfont icon-lianjie" />
              </a>
            </div>
          </div>
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
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.about-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #ebeef5;
  padding: 24px 28px;
}
.card-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 16px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}
.card-title .iconfont {
  font-size: 17px;
}

/* 个人卡片 */
.profile-card {
  text-align: left;
}
.profile-top {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}
.profile-avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 3px solid #ebeef5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.profile-info {
  min-width: 0;
}
.profile-name {
  font-family: var(--blog-serif);
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 4px;
  color: #303133;
}
.profile-tag {
  font-size: 13px;
  color: #909399;
  margin: 0;
}
.profile-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin: 0 0 14px;
}
.profile-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}
.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}
.meta-item .iconfont {
  font-size: 14px;
}
.meta-item svg {
  margin-right: 2px;
}
.meta-link {
  text-decoration: none;
  color: #606266;
  transition: color 0.15s;
}
.meta-link:hover {
  color: #303133;
}

/* 统计 */
.stats-grid {
  display: flex;
  gap: 0;
  text-align: center;
}
.stat-item {
  flex: 1;
  padding: 14px 8px;
  border-right: 1px solid #ebeef5;
}
.stat-item:last-child {
  border-right: none;
}
.stat-val {
  display: block;
  font-size: 24px;
  font-weight: 700;
  font-family: var(--blog-serif);
  color: #303133;
  margin-bottom: 2px;
}
.stat-lbl {
  font-size: 12px;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 音乐 */
.music-grid {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.music-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 0;
  border-bottom: 1px dashed #ebeef5;
  font-size: 14px;
}
.music-item:last-child {
  border-bottom: none;
}
.music-cover {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
  border: 1px solid #ebeef5;
}
.music-info {
  flex: 1;
  min-width: 0;
}
.music-name {
  display: block;
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}
.music-artist {
  display: block;
  color: #909399;
  font-size: 12px;
}
.music-link {
  color: #909399;
  font-size: 14px;
  transition: color 0.15s;
  flex-shrink: 0;
}
.music-link:hover {
  color: #303133;
}

@media (max-width: 960px) {
  .about-layout {
    flex-direction: column;
  }
}
@media (max-width: 600px) {
  .about-card {
    padding: 16px;
  }
  .profile-top {
    flex-direction: column;
    text-align: center;
  }
  .profile-meta {
    justify-content: center;
  }
  .stats-grid {
    flex-wrap: wrap;
  }
  .stat-item {
    min-width: 30%;
  }
}
</style>
