<script setup>
import { ref, onMounted } from 'vue'
import { getFriendLinks } from '@/api/friendLink'

const links = ref([])
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    const res = await getFriendLinks()
    links.value = res.data.data ?? []
  } catch {
    links.value = []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="links-page">
    <header class="page-header">
      <i class="iconfont icon-link" />
      <h1 class="page-title">友链</h1>
      <p class="page-count">共 {{ links.length }} 位朋友</p>
    </header>

    <div v-if="loading" class="placeholder">
      <div v-for="i in 6" :key="i" class="sk-card" />
    </div>

    <div v-else class="link-grid">
      <a
        v-for="link in links"
        :key="link.id"
        :href="link.url"
        target="_blank"
        rel="noopener noreferrer"
        class="link-card"
      >
        <img v-if="link.avatar" :src="link.avatar" class="link-avatar" />
        <div class="link-body">
          <p class="link-name">{{ link.name }}</p>
          <p class="link-desc">{{ link.description }}</p>
        </div>
      </a>
    </div>

    <p v-if="!loading && !links.length" class="empty">暂无友链</p>
  </div>
</template>

<style scoped>
.page-header {
  text-align: center;
  padding: 28px 0 20px;
  border-bottom: 2px solid #1a1a1a;
  margin-bottom: 28px;
}
.page-header .iconfont {
  font-size: 22px;
  color: #1a1a1a;
}
.page-title {
  font-family: var(--blog-serif);
  font-size: 26px;
  font-weight: 700;
  margin: 6px 0 4px;
  color: #1a1a1a;
  letter-spacing: 1px;
}
.page-count {
  font-size: 13px;
  color: #888;
  margin: 0;
}

.placeholder {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}
.sk-card {
  height: 80px;
  background: #eee;
  border-radius: 3px;
}

.link-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
  padding-bottom: 40px;
}
.link-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border: 1px solid #e8e8e4;
  border-radius: 3px;
  text-decoration: none;
  color: inherit;
  transition: border-color 0.15s;
  background: #fff;
}
.link-card:hover {
  border-color: #1a1a1a;
}
.link-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 1px solid #e8e8e4;
}
.link-body {
  min-width: 0;
}
.link-name {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 2px;
  color: #1a1a1a;
}
.link-desc {
  font-size: 12px;
  color: #888;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty {
  text-align: center;
  color: #999;
  padding: 60px 0;
  font-size: 14px;
}
</style>
