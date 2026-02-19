<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBlogStore } from '@/stores'
import { getArticlePage, searchArticles } from '@/api/article'
import ArticleCard from '@/components/ArticleCard.vue'

const route = useRoute()
const router = useRouter()
const blogStore = useBlogStore()

const articles = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const loading = ref(false)
const searchKeyword = ref('')

const loadArticles = async () => {
  loading.value = true
  try {
    let res
    if (searchKeyword.value) {
      res = await searchArticles(searchKeyword.value, page.value, pageSize)
    } else {
      res = await getArticlePage(page.value, pageSize)
    }
    const d = res.data.data
    articles.value = d.records ?? []
    total.value = d.total ?? 0
  } catch {
    articles.value = []
  } finally {
    loading.value = false
  }
}

const handlePageChange = (p) => {
  page.value = p
  loadArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goCategory = (slug) => router.push(`/category/${slug}`)
const goTag = (slug) => router.push(`/tag/${slug}`)

watch(
  () => route.query.search,
  (kw) => {
    searchKeyword.value = kw || ''
    page.value = 1
    loadArticles()
  }
)

onMounted(() => {
  searchKeyword.value = route.query.search || ''
  loadArticles()
})
</script>

<template>
  <div class="home-page">
    <div class="home-content">
      <!-- 文章列表 -->
      <div class="article-list">
        <div v-if="searchKeyword" class="search-result-tip">
          搜索: <strong>{{ searchKeyword }}</strong>
          <span class="search-count">{{ total }} 篇结果</span>
          <a class="clear-search" @click="router.push('/')">&times; 清除</a>
        </div>

        <div v-if="loading" class="loading-placeholder">
          <div v-for="i in 4" :key="i" class="skeleton-card">
            <div class="skeleton-line w60" />
            <div class="skeleton-line w90" />
            <div class="skeleton-line w40" />
          </div>
        </div>

        <template v-else-if="articles.length">
          <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
          <div v-if="total > pageSize" class="pagination-wrap">
            <el-pagination
              :current-page="page"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              background
              @current-change="handlePageChange"
            />
          </div>
        </template>

        <div v-else class="empty-tip">暂无文章</div>
      </div>

      <!-- 侧栏 -->
      <aside class="sidebar">
        <!-- 关于 -->
        <div class="side-block">
          <div class="side-author">
            <img
              v-if="blogStore.personalInfo.avatar"
              :src="blogStore.personalInfo.avatar"
              class="author-avatar"
            />
            <div class="author-info">
              <p class="author-name">{{ blogStore.personalInfo.nickname }}</p>
              <p class="author-tag">{{ blogStore.personalInfo.tag }}</p>
            </div>
          </div>
          <p class="author-desc">{{ blogStore.personalInfo.description }}</p>
        </div>

        <!-- 统计 -->
        <div class="side-block">
          <h4 class="side-title">统计</h4>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-num">{{
                blogStore.report.articleTotalCount ?? 0
              }}</span>
              <span class="stat-label">文章</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{
                blogStore.report.categoryTotalCount ?? 0
              }}</span>
              <span class="stat-label">分类</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{
                blogStore.report.tagTotalCount ?? 0
              }}</span>
              <span class="stat-label">标签</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{
                blogStore.report.viewTotalCount ?? 0
              }}</span>
              <span class="stat-label">浏览</span>
            </div>
          </div>
        </div>

        <!-- 分类 -->
        <div class="side-block">
          <h4 class="side-title">分类</h4>
          <ul class="cat-list">
            <li
              v-for="cat in blogStore.categories"
              :key="cat.id"
              class="cat-item"
              @click="goCategory(cat.slug)"
            >
              <span>{{ cat.name }}</span>
            </li>
          </ul>
        </div>

        <!-- 标签 -->
        <div class="side-block">
          <h4 class="side-title">标签</h4>
          <div class="tag-cloud">
            <span
              v-for="tag in blogStore.tags"
              :key="tag.id"
              class="tag-item"
              @click="goTag(tag.slug)"
              >{{ tag.name }}</span
            >
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  width: 100%;
}
.home-content {
  display: flex;
  gap: 36px;
  align-items: flex-start;
}
.article-list {
  flex: 1;
  min-width: 0;
}

.search-result-tip {
  padding: 10px 0 16px;
  font-size: 14px;
  color: #606266;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 4px;
}
.search-count {
  color: #909399;
  margin-left: 8px;
  font-size: 13px;
}
.clear-search {
  color: #303133;
  cursor: pointer;
  margin-left: 8px;
  font-weight: 600;
}

.skeleton-card {
  padding: 22px 0;
  border-bottom: 1px solid #e4e7ed;
}
.skeleton-line {
  height: 14px;
  background: #ebeef5;
  border-radius: 2px;
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

.pagination-wrap {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
.empty-tip {
  padding: 60px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
}

/* ===== 侧栏 ===== */
.sidebar {
  width: 260px;
  flex-shrink: 0;
  position: sticky;
  top: 72px;
}
.side-block {
  border: 1px solid #e4e7ed;
  border-radius: 3px;
  padding: 16px;
  margin-bottom: 16px;
  background: #fff;
}
.side-author {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e4e7ed;
}
.author-info {
  min-width: 0;
}
.author-name {
  font-size: 15px;
  font-weight: 700;
  margin: 0;
  color: #303133;
}
.author-tag {
  font-size: 12px;
  color: #888;
  margin: 2px 0 0;
}
.author-desc {
  font-size: 13px;
  color: #606266;
  margin: 0;
  line-height: 1.6;
}
.side-title {
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin: 0 0 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e4e7ed;
  color: #303133;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  text-align: center;
}
.stat-item {
  padding: 6px 0;
}
.stat-num {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  font-family: var(--blog-serif);
}
.stat-label {
  font-size: 11px;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.cat-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.cat-item {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  border-bottom: 1px dashed #ebeef5;
  transition: color 0.15s;
}
.cat-item:last-child {
  border-bottom: none;
}
.cat-item:hover {
  color: #303133;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.tag-item {
  font-size: 12px;
  color: #606266;
  padding: 2px 8px;
  border: 1px solid #e4e7ed;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.15s;
}
.tag-item:hover {
  color: #303133;
  border-color: #303133;
}

@media (max-width: 860px) {
  .home-content {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
    position: static;
  }
}
</style>
