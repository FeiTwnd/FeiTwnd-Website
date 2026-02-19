<script setup>
import { useBlogStore } from '@/stores'

const blogStore = useBlogStore()
</script>

<template>
  <div class="about-page">
    <header class="page-header">
      <i class="iconfont icon-user" />
      <h1 class="page-title">关于</h1>
    </header>

    <!-- 基本信息 -->
    <section class="about-section">
      <div class="profile-card">
        <img
          v-if="blogStore.personalInfo.avatar"
          :src="blogStore.personalInfo.avatar"
          class="profile-avatar"
        />
        <div class="profile-body">
          <h2 class="profile-name">{{ blogStore.personalInfo.nickname }}</h2>
          <p class="profile-tag">{{ blogStore.personalInfo.tag }}</p>
          <p class="profile-desc">{{ blogStore.personalInfo.description }}</p>
          <div class="profile-meta">
            <span v-if="blogStore.personalInfo.location">
              <i class="iconfont icon-position" />
              {{ blogStore.personalInfo.location }}
            </span>
            <span v-if="blogStore.personalInfo.email">
              <i class="iconfont icon-youxiang" />
              {{ blogStore.personalInfo.email }}
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- 统计 -->
    <section class="about-section">
      <h3 class="section-title">博客统计</h3>
      <div class="stats-row">
        <div class="stat-box">
          <span class="stat-val">{{
            blogStore.report.articleTotalCount ?? 0
          }}</span>
          <span class="stat-lbl">文章</span>
        </div>
        <div class="stat-box">
          <span class="stat-val">{{
            blogStore.report.categoryTotalCount ?? 0
          }}</span>
          <span class="stat-lbl">分类</span>
        </div>
        <div class="stat-box">
          <span class="stat-val">{{
            blogStore.report.tagTotalCount ?? 0
          }}</span>
          <span class="stat-lbl">标签</span>
        </div>
        <div class="stat-box">
          <span class="stat-val">{{
            blogStore.report.viewTotalCount ?? 0
          }}</span>
          <span class="stat-lbl">浏览</span>
        </div>
        <div class="stat-box">
          <span class="stat-val">{{
            blogStore.report.visitorTotalCount ?? 0
          }}</span>
          <span class="stat-lbl">访客</span>
        </div>
      </div>
    </section>

    <!-- 音乐 -->
    <section v-if="blogStore.musics.length" class="about-section">
      <h3 class="section-title"><i class="iconfont icon-yinle" /> 音乐</h3>
      <ul class="music-list">
        <li v-for="m in blogStore.musics" :key="m.id" class="music-item">
          <img v-if="m.coverImage" :src="m.coverImage" class="music-cover" />
          <span class="music-name">{{ m.title }}</span>
          <span class="music-artist">{{ m.artist }}</span>
          <a v-if="m.url" :href="m.url" target="_blank" class="music-link">
            <i class="iconfont icon-lianjie" />
          </a>
        </li>
      </ul>
    </section>
  </div>
</template>

<style scoped>
.page-header {
  text-align: center;
  padding: 28px 0 20px;
  border-bottom: 2px solid #303133;
  margin-bottom: 28px;
}
.page-header .iconfont {
  font-size: 22px;
  color: #303133;
}
.page-title {
  font-family: var(--blog-serif);
  font-size: 26px;
  font-weight: 700;
  margin: 6px 0 0;
  color: #303133;
  letter-spacing: 1px;
}

.about-section {
  margin-bottom: 32px;
}
.section-title {
  font-family: var(--blog-serif);
  font-size: 17px;
  font-weight: 700;
  margin: 0 0 14px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e4e7ed;
  color: #303133;
}

/* 个人卡片 */
.profile-card {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  border: 1px solid #e4e7ed;
  border-radius: 3px;
  padding: 20px;
  background: #fff;
}
.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 1px solid #e4e7ed;
}
.profile-body {
  min-width: 0;
}
.profile-name {
  font-family: var(--blog-serif);
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 2px;
  color: #303133;
}
.profile-tag {
  font-size: 13px;
  color: #888;
  margin: 0 0 8px;
}
.profile-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.7;
  margin: 0 0 10px;
}
.profile-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #888;
  flex-wrap: wrap;
}
.profile-meta .iconfont {
  font-size: 13px;
  margin-right: 2px;
}

/* 统计 */
.stats-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.stat-box {
  flex: 1;
  min-width: 80px;
  text-align: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 3px;
  background: #fff;
}
.stat-val {
  display: block;
  font-size: 22px;
  font-weight: 700;
  font-family: var(--blog-serif);
  color: #303133;
}
.stat-lbl {
  font-size: 11px;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 音乐列表 */
.music-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.music-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px dashed #ebeef5;
  font-size: 14px;
}
.music-item:last-child {
  border-bottom: none;
}
.music-cover {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  object-fit: cover;
  flex-shrink: 0;
}
.music-name {
  color: #303133;
  font-weight: 500;
}
.music-artist {
  color: #909399;
  font-size: 13px;
}
.music-link {
  color: #909399;
  margin-left: auto;
  font-size: 13px;
  transition: color 0.15s;
}
.music-link:hover {
  color: #303133;
}

@media (max-width: 600px) {
  .profile-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  .profile-meta {
    justify-content: center;
  }
}
</style>
