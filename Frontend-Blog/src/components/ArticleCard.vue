<script setup>
defineProps({
  article: { type: Object, required: true }
})

const fmtDate = (d) => {
  if (!d) return ''
  return d.slice(0, 10)
}
</script>

<template>
  <router-link :to="`/article/${article.slug}`" class="article-card">
    <div v-if="article.coverImage" class="card-cover">
      <img :src="article.coverImage" :alt="article.title" loading="lazy" />
    </div>
    <div class="card-body">
      <span v-if="article.categoryName" class="card-category">{{
        article.categoryName
      }}</span>
      <h3 class="card-title">{{ article.title }}</h3>
      <p v-if="article.summary" class="card-summary">{{ article.summary }}</p>
      <div class="card-meta">
        <span
          ><i class="iconfont icon-time" />
          {{ fmtDate(article.publishTime) }}</span
        >
        <span
          ><i class="iconfont icon-eye" /> {{ article.viewCount ?? 0 }}</span
        >
        <span
          ><i class="iconfont icon-comment" />
          {{ article.commentCount ?? 0 }}</span
        >
      </div>
    </div>
  </router-link>
</template>

<style scoped>
.article-card {
  display: flex;
  gap: 20px;
  padding: 22px 0;
  border-bottom: 1px solid #e8e8e4;
  text-decoration: none;
  color: inherit;
  transition: background 0.15s;
}
.article-card:first-child {
  padding-top: 0;
}
.article-card:hover {
  background: var(--blog-hover);
}
.card-cover {
  flex-shrink: 0;
  width: 180px;
  height: 120px;
  overflow: hidden;
  border-radius: 3px;
  border: 1px solid #e8e8e4;
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.card-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.card-category {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #888;
  font-weight: 600;
  margin-bottom: 4px;
}
.card-title {
  font-family: var(--blog-serif);
  font-size: 19px;
  font-weight: 700;
  margin: 0 0 6px;
  line-height: 1.4;
  color: #1a1a1a;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-summary {
  font-size: 13.5px;
  color: #666;
  margin: 0 0 8px;
  line-height: 1.65;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-meta {
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: #999;
}
.card-meta .iconfont {
  font-size: 12px;
  margin-right: 3px;
}

@media (max-width: 600px) {
  .article-card {
    flex-direction: column;
    gap: 10px;
  }
  .card-cover {
    width: 100%;
    height: 160px;
  }
}
</style>
