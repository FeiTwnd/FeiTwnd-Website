<script setup>
import { ref, provide, watch, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
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

/* 回到顶部 */
const showBackTop = ref(false)
const onScroll = () => {
  showBackTop.value = window.scrollY > 400
}
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  blogStore.init()
  visitorStore.record()
  window.addEventListener('scroll', onScroll, { passive: true })
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
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

    <!-- 回到顶部 -->
    <transition name="backtop-fade">
      <button
        v-show="showBackTop"
        class="back-to-top"
        title="回到顶部"
        @click="scrollToTop"
      >
        <svg
          viewBox="0 0 24 24"
          width="20"
          height="20"
          fill="none"
          stroke="currentColor"
          stroke-width="2.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <polyline points="18 15 12 9 6 15" />
        </svg>
      </button>
    </transition>
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

/* 回到顶部 */
.back-to-top {
  position: fixed;
  right: 28px;
  bottom: 36px;
  z-index: 99;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 1px solid var(--blog-border);
  background: var(--blog-card);
  color: var(--blog-text2);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    color 0.2s,
    box-shadow 0.2s,
    border-color 0.2s;
}
.back-to-top:hover {
  color: var(--blog-text);
  border-color: var(--blog-text3);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}
.backtop-fade-enter-active,
.backtop-fade-leave-active {
  transition:
    opacity 0.25s,
    transform 0.25s;
}
.backtop-fade-enter-from,
.backtop-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 768px) {
  .main-inner {
    padding: 20px 16px;
  }
  .back-to-top {
    right: 16px;
    bottom: 20px;
    width: 38px;
    height: 38px;
  }
}
</style>
