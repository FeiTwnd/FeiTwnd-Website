<script setup>
import { ref, provide, watch } from 'vue'
import { useRoute } from 'vue-router'
import { onMounted } from 'vue'
import BlogHeader from '@/components/BlogHeader.vue'
import BlogFooter from '@/components/BlogFooter.vue'
import HeroBanner from '@/components/HeroBanner.vue'
import { useBlogStore, useVisitorStore } from '@/stores'

const route = useRoute()
const blogStore = useBlogStore()
const visitorStore = useVisitorStore()

/* 文章详情页会通过 provide/inject 传递封面和标题 */
const articleCover = ref('')
const articleTitle = ref('')
const articleMeta = ref('')

provide('setHero', { articleCover, articleTitle, articleMeta })

/* 路由切换时重置 hero 数据，子页面的 onMounted 会重新设置 */
watch(
  () => route.fullPath,
  () => {
    articleCover.value = ''
    articleTitle.value = ''
    articleMeta.value = ''
  }
)

onMounted(() => {
  blogStore.init()
  visitorStore.record()
})
</script>

<template>
  <div class="blog-layout">
    <BlogHeader />
    <HeroBanner
      :cover-image="articleCover"
      :title="articleTitle"
      :meta="articleMeta"
    />
    <main class="blog-main">
      <div class="main-inner">
        <router-view />
      </div>
    </main>
    <BlogFooter />
  </div>
</template>

<style scoped>
.blog-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.blog-main {
  flex: 1;
  width: 100%;
  background: var(--blog-bg);
}
.main-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 28px;
}
@media (max-width: 768px) {
  .main-inner {
    padding: 20px 16px;
  }
}
</style>
