<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useBlogStore } from '@/stores'
import bgcImg from '@/assets/images/bgc.jpg'

const route = useRoute()
const blogStore = useBlogStore()

const props = defineProps({
  /** 文章封面 (文章详情页传入) */
  coverImage: { type: String, default: '' },
  /** 文章标题 */
  title: { type: String, default: '' },
  /** 文章 meta 信息 */
  meta: { type: String, default: '' }
})

const isHome = computed(() => route.name === 'home')

const bgImage = computed(() => {
  if (props.coverImage) return props.coverImage
  return bgcImg
})
</script>

<template>
  <div class="hero-banner" :style="{ backgroundImage: `url(${bgImage})` }">
    <div class="hero-overlay" />
    <div class="hero-content">
      <!-- 主页: 显示个人信息 -->
      <template v-if="isHome">
        <h1 class="hero-title">
          {{ blogStore.personalInfo.nickname }}
        </h1>
        <p class="hero-desc">{{ blogStore.personalInfo.description || '' }}</p>
      </template>
      <!-- 自定义标题 (文章/分类/标签等) -->
      <template v-else-if="title">
        <h1 class="hero-article-title">{{ title }}</h1>
        <p v-if="meta" class="hero-article-meta">{{ meta }}</p>
      </template>
      <!-- 其他页面: 显示路由 meta 标题 -->
      <template v-else>
        <h1 class="hero-page-title">{{ $route.meta.title || '' }}</h1>
      </template>
    </div>
  </div>
</template>

<style scoped>
.hero-banner {
  position: relative;
  width: 100%;
  height: 40vh;
  min-height: 280px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.hero-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  z-index: 1;
}
.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #fff;
  padding: 0 24px;
  max-width: 600px;
}
.hero-title {
  font-family: var(--blog-serif);
  font-size: 34px;
  font-weight: 800;
  margin: 0 0 10px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  letter-spacing: 1px;
}
.hero-desc {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  line-height: 1.6;
}
.hero-article-title {
  font-family: var(--blog-serif);
  font-size: 30px;
  font-weight: 800;
  margin: 0 0 10px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  line-height: 1.35;
}
.hero-article-meta {
  font-size: 14px;
  margin: 0;
  opacity: 0.85;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}
.hero-page-title {
  font-family: var(--blog-serif);
  font-size: 32px;
  font-weight: 800;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}
</style>
