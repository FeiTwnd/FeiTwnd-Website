<script setup>
import { ref, inject, onMounted } from 'vue'
import { getFriendLinks } from '@/api/friendLink'
import SidebarCard from '@/components/SidebarCard.vue'

const { articleTitle, articleMeta } = inject('setHero')

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

onMounted(() => {
  articleTitle.value = '友链'
  articleMeta.value = '朋友们的小站'
  load()
})
</script>

<template>
  <div class="links-page">
    <div class="links-layout">
      <div class="links-main">
        <div class="content-card">
      <div class="card-header">
        <i class="iconfont icon-lianjie" />
        <span>共 {{ links.length }} 位朋友</span>
      </div>

      <div v-if="loading" class="placeholder-grid">
        <div v-for="i in 6" :key="i" class="sk-card" />
      </div>

      <div v-else-if="links.length" class="link-grid">
        <a
          v-for="link in links"
          :key="link.id"
          :href="link.url"
          target="_blank"
          rel="noopener noreferrer"
          class="link-card"
        >
          <img v-if="link.avatarUrl" :src="link.avatarUrl" class="link-avatar" loading="lazy" />
          <span v-else class="link-avatar-letter">{{ link.name ? link.name.charAt(0) : '?' }}</span>
          <div class="link-body">
            <p class="link-name">{{ link.name }}</p>
            <p class="link-desc">{{ link.description }}</p>
          </div>
        </a>
      </div>

      <p v-else class="empty">暂无友链</p>
    </div>
      </div>

      <SidebarCard />
    </div>
  </div>
</template>

<style scoped>
.links-page { width: 100%; }
.links-layout { display: flex; gap: 24px; align-items: flex-start; }
.links-main { flex: 1; min-width: 0; }
.content-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #ebeef5;
  padding: 24px 28px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid #ebeef5;
}
.card-header .iconfont {
  font-size: 16px;
  color: #606266;
}

.placeholder-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 14px;
}
.sk-card {
  height: 80px;
  background: #ebeef5;
  border-radius: 8px;
}

.link-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}
.link-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  text-decoration: none;
  color: inherit;
  background: #fafafa;
  transition:
    transform 0.2s,
    box-shadow 0.2s,
    border-color 0.15s;
}
.link-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border-color: #c0c4cc;
}
.link-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid #ebeef5;
}
.link-avatar-letter {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e4e7ed;
  color: #606266;
  font-size: 20px;
  font-weight: 700;
  user-select: none;
}
.link-body {
  min-width: 0;
}
.link-name {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 3px;
  color: #303133;
}
.link-desc {
  font-size: 12px;
  color: #909399;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5;
}

.empty {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
  margin: 0;
}

@media (max-width: 960px) {
  .links-layout { flex-direction: column; }
}
@media (max-width: 600px) {
  .content-card {
    padding: 16px;
  }
  .link-grid {
    grid-template-columns: 1fr;
  }
}
</style>
