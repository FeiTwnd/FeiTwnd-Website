<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticleArchive } from '@/api/article'
import SidebarCard from '@/components/SidebarCard.vue'

const router = useRouter()
const yearGroups = ref([])
const loading = ref(false)
const totalCount = ref(0)

const load = async () => {
  loading.value = true
  try {
    const res = await getArticleArchive()
    const list = res.data.data ?? []
    const map = new Map()
    let count = 0
    list.forEach((group) => {
      const y = group.year
      if (!map.has(y)) map.set(y, [])
      const items = (group.articles ?? []).map((a) => ({
        ...a,
        month: group.month,
        displayDate: `${String(group.month).padStart(2, '0')}-${String(a.publishDay).padStart(2, '0')}`
      }))
      map.get(y).push(...items)
      count += items.length
    })
    yearGroups.value = [...map.entries()]
      .sort((a, b) => b[0] - a[0])
      .map(([year, items]) => ({
        year,
        items: items.sort((a, b) => {
          const da = new Date(a.publishTime)
          const db = new Date(b.publishTime)
          return db - da
        })
      }))
    totalCount.value = count
  } catch {
    yearGroups.value = []
  } finally {
    loading.value = false
  }
}

const goArticle = (slug) => router.push(`/article/${slug}`)

onMounted(load)
</script>

<template>
  <div class="archive-page">
    <div class="archive-layout">
      <div class="archive-main">
        <div class="content-card">
          <div class="card-header">
            <i class="iconfont icon-guidang" />
            <span>共 {{ totalCount }} 篇文章</span>
          </div>

          <div v-if="loading" class="placeholder">
            <div v-for="i in 4" :key="i" class="sk-line" />
          </div>

          <div v-else class="timeline">
            <div v-for="g in yearGroups" :key="g.year" class="year-group">
              <h2 class="year-label">
                <span class="year-dot" />
                {{ g.year }}
              </h2>
              <ul class="year-list">
                <li
                  v-for="a in g.items"
                  :key="a.id"
                  class="archive-item"
                  @click="goArticle(a.slug)"
                >
                  <span class="item-dot" />
                  <span class="item-date">{{ a.displayDate }}</span>
                  <span class="item-title">{{ a.title }}</span>
                </li>
              </ul>
            </div>
          </div>

          <p v-if="!loading && !yearGroups.length" class="empty">暂无归档</p>
        </div>
      </div>

      <SidebarCard />
    </div>
  </div>
</template>

<style scoped>
.archive-page {
  width: 100%;
}
.archive-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.archive-main {
  flex: 1;
  min-width: 0;
}

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

.placeholder {
  padding: 20px 0;
}
.sk-line {
  height: 14px;
  background: #ebeef5;
  border-radius: 4px;
  margin-bottom: 12px;
  width: 60%;
}

.timeline {
  position: relative;
  padding-left: 20px;
  border-left: 2px solid #e4e7ed;
}
.year-group {
  margin-bottom: 24px;
}
.year-group:last-child {
  margin-bottom: 0;
}
.year-label {
  font-family: var(--blog-serif);
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
}
.year-dot {
  position: absolute;
  left: -27px;
  width: 12px;
  height: 12px;
  background: #303133;
  border-radius: 50%;
  border: 2px solid #fff;
}
.year-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.archive-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  cursor: pointer;
  position: relative;
  transition: background 0.15s;
  border-radius: 6px;
  padding-left: 4px;
}
.archive-item:hover {
  background: #f5f7fa;
}
.archive-item:hover .item-title {
  color: #303133;
}
.item-dot {
  position: absolute;
  left: -24px;
  width: 6px;
  height: 6px;
  background: #c0c4cc;
  border-radius: 50%;
}
.archive-item:hover .item-dot {
  background: #303133;
}
.item-date {
  flex-shrink: 0;
  font-size: 13px;
  color: #909399;
  font-family: var(--blog-serif);
  font-variant-numeric: tabular-nums;
  min-width: 48px;
}
.item-title {
  font-size: 14px;
  color: #606266;
  transition: color 0.15s;
}

.empty {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

@media (max-width: 960px) {
  .archive-layout {
    flex-direction: column;
  }
}
@media (max-width: 600px) {
  .content-card {
    padding: 16px;
  }
}
</style>
