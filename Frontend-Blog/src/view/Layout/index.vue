<script setup>
import BlogHeader from '@/components/BlogHeader.vue'
import BlogFooter from '@/components/BlogFooter.vue'
import { useBlogStore, useVisitorStore } from '@/stores'
import { onMounted } from 'vue'

const blogStore = useBlogStore()
const visitorStore = useVisitorStore()

onMounted(() => {
  blogStore.init()
  visitorStore.record()
})
</script>

<template>
  <div class="blog-layout">
    <BlogHeader />
    <main class="blog-main">
      <router-view />
    </main>
    <BlogFooter />
  </div>
</template>

<style scoped>
.blog-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding-top: 56px;
}
.blog-main {
  flex: 1;
  width: 100%;
  max-width: 1060px;
  margin: 0 auto;
  padding: 32px 24px;
}
@media (max-width: 768px) {
  .blog-main {
    padding: 20px 16px;
  }
}
</style>
