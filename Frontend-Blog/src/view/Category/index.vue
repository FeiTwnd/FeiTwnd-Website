<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticlesByCategory } from '@/api/article'
import { useBlogStore } from '@/stores'
import ArticleCard from '@/components/ArticleCard.vue'

const route = useRoute()
const blogStore = useBlogStore()
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
  categoryName.value = blogStore.getCategoryNameBySlug(route.params.slug)
}

const handlePage = (p) => {
  page.value = p
  load()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(
  () => route.params.slug,
  () => {
    page.value = 1
    resolveName()
    load()
  }
)
onMounted(() => {
  resolveName()
  load()
})
</script>

<template>
  <div class="category-page">
    <header class="page-header">
      <i class="iconfont icon-folder" />
      <h1 class="page-title">{{ categoryName || '分类' }}</h1>
      <p class="page-count">共 {{ total }} 篇文章</p>
    </header>

    <div v-if="loading" class="placeholder">
      <div v-for="i in 3" :key="i" class="sk-line" />
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
    <p v-else class="empty">该分类下暂无文章</p>
  </div>
</template>

<style scoped>
.page-header {
  text-align: center;
  padding: 28px 0 20px;
  border-bottom: 2px solid #1a1a1a;
  margin-bottom: 4px;
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
  width: 70%;
}
.pager {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
.empty {
  text-align: center;
  color: #999;
  padding: 60px 0;
  font-size: 14px;
}
</style>
