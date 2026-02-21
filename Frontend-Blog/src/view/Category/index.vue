<script setup>
import { ref, watch, inject, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticlesByCategory } from '@/api/article'
import { useBlogStore } from '@/stores'
import ArticleCard from '@/components/ArticleCard.vue'
import SidebarCard from '@/components/SidebarCard.vue'

const route = useRoute()
const blogStore = useBlogStore()
const { articleTitle, articleMeta } = inject('setHero')

const articles = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const loading = ref(false)
const categoryName = ref('')

const load = async () => {
  const slug = route.params.slug
  const catId = blogStore.getCategoryIdBySlug(slug)
  if (!catId) {
    articles.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    const res = await getArticlesByCategory(catId, page.value, pageSize)
    const d = res.data.data
    articles.value = d.records ?? []
    total.value = d.total ?? 0
  } catch {
    articles.value = []
  } finally {
    loading.value = false
  }
}

const resolveName = () => {
  const slug = route.params.slug
  const name = blogStore.getCategoryNameBySlug(slug)
  const desc = blogStore.getCategoryDescBySlug(slug)
  categoryName.value = name
  articleTitle.value = name || '分类'
  articleMeta.value = desc
    ? `${desc} · 共 ${total.value} 篇文章`
    : `共 ${total.value} 篇文章`
}

const handlePage = (p) => {
  page.value = p
  load()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(
  () => route.params.slug,
  async () => {
    page.value = 1
    await load()
    resolveName()
  }
)

onMounted(async () => {
  await blogStore.init()
  resolveName()
  await load()
  resolveName()
})
</script>

<template>
  <div class="category-page">
    <div class="category-layout">
      <div class="category-main">
        <div v-if="loading" class="loading-placeholder">
          <div v-for="i in 3" :key="i" class="skeleton-card">
            <div class="skeleton-cover" />
            <div class="skeleton-body">
              <div class="skeleton-line w60" />
              <div class="skeleton-line w90" />
              <div class="skeleton-line w40" />
            </div>
          </div>
        </div>

        <template v-else-if="articles.length">
          <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
          <div v-if="total > pageSize" class="pager">
            <el-pagination
              :current-page="page"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              background
              @current-change="handlePage"
            />
          </div>
        </template>

        <div v-else class="empty-card">
          <p class="empty">该分类下暂无文章</p>
        </div>
      </div>

      <SidebarCard />
    </div>
  </div>
</template>

<style scoped>
.category-page {
  width: 100%;
}
.category-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.category-main {
  flex: 1;
  min-width: 0;
}

/* 骨架屏 */
.skeleton-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  border: 1px solid #ebeef5;
}
.skeleton-cover {
  width: 200px;
  height: 130px;
  background: #ebeef5;
  border-radius: 6px;
  flex-shrink: 0;
}
.skeleton-body {
  flex: 1;
}
.skeleton-line {
  height: 14px;
  background: #ebeef5;
  border-radius: 4px;
  margin-bottom: 10px;
}
.w60 {
  width: 60%;
}
.w90 {
  width: 90%;
}
.w40 {
  width: 40%;
}

.pager {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.empty-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}
.empty {
  text-align: center;
  color: #909399;
  padding: 60px 0;
  font-size: 14px;
  margin: 0;
}

@media (max-width: 960px) {
  .category-layout {
    flex-direction: column;
  }
}
@media (max-width: 600px) {
  .skeleton-card {
    flex-direction: column;
  }
  .skeleton-cover {
    width: 100%;
    height: 160px;
  }
  .empty-card {
    padding: 16px;
  }
}
</style>
