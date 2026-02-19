<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticleArchive } from '@/api/article'

const router = useRouter()
const yearGroups = ref([])
const loading = ref(false)
const totalCount = ref(0)

const load = async () => {
  loading.value = true
  try {
    const res = await getArticleArchive()
    const list = res.data.data ?? []
    /* 后端返回按年月分组: [{ year, month, articles: [{ id, title, slug, publishDay, publishTime }] }] */
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
    <header class="page-header">
      <i class="iconfont icon-time" />
      <h1 class="page-title">归档</h1>
      <p class="page-count">共 {{ totalCount }} 篇文章</p>
    </header>

    <div v-if="loading" class="placeholder">
      <div v-for="i in 4" :key="i" class="sk-line" />
    </div>

    <div v-else class="timeline">
      <div v-for="g in yearGroups" :key="g.year" class="year-group">
        <h2 class="year-label">{{ g.year }}</h2>
        <ul class="year-list">
          <li
            v-for="a in g.items"
            :key="a.id"
            class="archive-item"
            @click="goArticle(a.slug)"
          >
            <span class="item-date">{{ a.displayDate }}</span>
            <span class="item-title">{{ a.title }}</span>
          </li>
        </ul>
      </div>
    </div>

    <p v-if="!loading && !yearGroups.length" class="empty">暂无归档</p>
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
  padding: 30px 0;
}
.sk-line {
  height: 14px;
  background: #eee;
  border-radius: 2px;
  margin-bottom: 12px;
  width: 60%;
}

.timeline {
  padding-bottom: 40px;
}
.year-group {
  margin-bottom: 28px;
}
.year-label {
  font-family: var(--blog-serif);
  font-size: 22px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e8e8e4;
}
.year-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.archive-item {
  display: flex;
  align-items: baseline;
  gap: 14px;
  padding: 7px 0;
  border-bottom: 1px dashed #f0f0ec;
  cursor: pointer;
  transition: color 0.15s;
}
.archive-item:last-child {
  border-bottom: none;
}
.archive-item:hover .item-title {
  color: #1a1a1a;
}
.item-date {
  flex-shrink: 0;
  font-size: 13px;
  color: #999;
  font-family: var(--blog-serif);
  font-variant-numeric: tabular-nums;
}
.item-title {
  font-size: 15px;
  color: #555;
}

.empty {
  text-align: center;
  color: #999;
  padding: 60px 0;
  font-size: 14px;
}
</style>
