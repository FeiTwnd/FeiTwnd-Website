<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useVisitorStore } from '@/stores'
import { getArticleBySlug } from '@/api/article'
import {
  getCommentTree,
  submitComment,
  editComment,
  deleteComment
} from '@/api/comment'
import { likeArticle, unlikeArticle, hasLiked } from '@/api/like'

const route = useRoute()
const router = useRouter()
const visitorStore = useVisitorStore()

const article = ref(null)
const loading = ref(true)
const liked = ref(false)
const liking = ref(false)

/* ---- 评论 ---- */
const comments = ref([])
const commentForm = ref({ nickname: '', email: '', content: '' })
const replyTarget = ref(null)
const submitting = ref(false)
const editingId = ref(null)
const editContent = ref('')

/* ---- 加载文章 ---- */
const loadArticle = async (slug) => {
  loading.value = true
  try {
    const res = await getArticleBySlug(slug)
    article.value = res.data.data
    document.title = `${article.value.title} - FeiTwnd`
    loadComments()
    checkLike()
  } catch {
    article.value = null
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  if (!article.value) return
  try {
    const res = await getCommentTree(article.value.id)
    comments.value = res.data.data ?? []
  } catch {
    comments.value = []
  }
}

const checkLike = async () => {
  if (!article.value || !visitorStore.visitorId) return
  try {
    const res = await hasLiked(article.value.id, visitorStore.visitorId)
    liked.value = res.data.data === true
  } catch {
    liked.value = false
  }
}

const toggleLike = async () => {
  if (!visitorStore.visitorId || liking.value) return
  liking.value = true
  try {
    if (liked.value) {
      await unlikeArticle(article.value.id, visitorStore.visitorId)
      liked.value = false
      article.value.likeCount = Math.max(0, (article.value.likeCount ?? 1) - 1)
    } else {
      await likeArticle(article.value.id, visitorStore.visitorId)
      liked.value = true
      article.value.likeCount = (article.value.likeCount ?? 0) + 1
    }
  } finally {
    liking.value = false
  }
}

/* ---- 评论操作 ---- */
const handleSubmitComment = async () => {
  const nick = commentForm.value.nickname.trim() || visitorStore.nickname
  const content = commentForm.value.content.trim()
  if (!nick || !content) {
    ElMessage.warning('请填写昵称和内容')
    return
  }
  submitting.value = true
  try {
    const payload = {
      articleId: article.value.id,
      rootId: replyTarget.value?.rootId ?? replyTarget.value?.id ?? null,
      parentId: replyTarget.value?.id ?? null,
      contentHtml: `<p>${content}</p>`,
      nickname: nick,
      visitorId: visitorStore.visitorId,
      email: commentForm.value.email || visitorStore.email || '',
      deviceMemory: navigator.deviceMemory || null,
      hardwareConcurrency: navigator.hardwareConcurrency || null
    }
    await submitComment(payload)
    // 保存昵称和邮箱
    visitorStore.nickname = nick
    if (commentForm.value.email) visitorStore.email = commentForm.value.email
    commentForm.value = {
      nickname: nick,
      email: commentForm.value.email,
      content: ''
    }
    replyTarget.value = null
    ElMessage.success('评论成功')
    loadComments()
  } finally {
    submitting.value = false
  }
}

const startReply = (c) => {
  replyTarget.value = c
  commentForm.value.nickname = visitorStore.nickname
  commentForm.value.email = visitorStore.email
}

const cancelReply = () => {
  replyTarget.value = null
}

const startEdit = (c) => {
  editingId.value = c.id
  editContent.value = c.contentHtml?.replace(/<[^>]+>/g, '') ?? ''
}

const doEdit = async (c) => {
  if (!editContent.value.trim()) return
  try {
    await editComment({
      id: c.id,
      visitorId: visitorStore.visitorId,
      contentHtml: `<p>${editContent.value.trim()}</p>`
    })
    editingId.value = null
    ElMessage.success('修改成功')
    loadComments()
  } catch {
    ElMessage.error('修改失败')
  }
}

const doDelete = async (c) => {
  try {
    await ElMessageBox.confirm('确定删除这条评论？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteComment(c.id, visitorStore.visitorId)
    ElMessage.success('删除成功')
    loadComments()
  } catch {
    /* cancel */
  }
}

const fmtDate = (d) => (d ? d.slice(0, 16).replace('T', ' ') : '')
const isOwn = (c) => c.visitorId && c.visitorId === visitorStore.visitorId

const flatCommentCount = computed(() => {
  let count = 0
  const walk = (list) => {
    list.forEach((c) => {
      count++
      if (c.children?.length) walk(c.children)
    })
  }
  walk(comments.value)
  return count
})

watch(
  () => route.params.slug,
  (slug) => {
    if (slug) loadArticle(slug)
  }
)

onMounted(() => {
  commentForm.value.nickname = visitorStore.nickname
  commentForm.value.email = visitorStore.email
  loadArticle(route.params.slug)
})
</script>

<template>
  <div class="article-page">
    <div v-if="loading" class="loading-wrap">
      <div
        class="skeleton-line w60"
        style="height: 28px; margin-bottom: 16px"
      />
      <div class="skeleton-line w40" style="margin-bottom: 24px" />
      <div v-for="i in 6" :key="i" class="skeleton-line w90" />
    </div>

    <template v-else-if="article">
      <!-- 文章头 -->
      <header class="article-header">
        <span v-if="article.categoryName" class="article-category">{{
          article.categoryName
        }}</span>
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span
            ><i class="iconfont icon-time" />
            {{ fmtDate(article.publishTime) }}</span
          >
          <span><i class="iconfont icon-eye" /> {{ article.viewCount }}</span>
          <span v-if="article.wordCount"
            ><i class="iconfont icon-bianjiwenzhang_huaban" />
            {{ article.wordCount }} 字</span
          >
          <span v-if="article.readingTime"
            >{{ article.readingTime }} 分钟阅读</span
          >
        </div>
        <div v-if="article.tagNames?.length" class="article-tags">
          <span v-for="t in article.tagNames" :key="t" class="atag"
            ># {{ t }}</span
          >
        </div>
      </header>

      <!-- 封面图 -->
      <div v-if="article.coverImage" class="article-cover">
        <img :src="article.coverImage" :alt="article.title" />
      </div>

      <!-- 正文 -->
      <div class="article-content" v-html="article.contentHtml" />

      <!-- 点赞 + 上下篇 -->
      <div class="article-actions">
        <button class="like-btn" :class="{ liked }" @click="toggleLike">
          <i class="iconfont icon-shield" />
          {{ liked ? '已赞' : '赞' }} {{ article.likeCount ?? 0 }}
        </button>
      </div>

      <div
        v-if="article.prevArticle || article.nextArticle"
        class="article-nav"
      >
        <router-link
          v-if="article.prevArticle"
          :to="`/article/${article.prevArticle.slug}`"
          class="nav-prev"
        >
          <i class="iconfont icon-arrow-left-bold" />
          <span>{{ article.prevArticle.title }}</span>
        </router-link>
        <div v-else />
        <router-link
          v-if="article.nextArticle"
          :to="`/article/${article.nextArticle.slug}`"
          class="nav-next"
        >
          <span>{{ article.nextArticle.title }}</span>
          <i class="iconfont icon-arrow-right-bold" />
        </router-link>
      </div>

      <!-- 相关文章 -->
      <div v-if="article.relatedArticles?.length" class="related-section">
        <h3 class="related-title">相关文章</h3>
        <ul class="related-list">
          <li v-for="r in article.relatedArticles" :key="r.id">
            <router-link :to="`/article/${r.slug}`">{{ r.title }}</router-link>
          </li>
        </ul>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3 class="comment-title">
          <i class="iconfont icon-comment" /> 评论 ({{ flatCommentCount }})
        </h3>

        <!-- 评论表单 -->
        <div class="comment-form">
          <div v-if="replyTarget" class="reply-hint">
            回复 <strong>{{ replyTarget.nickname }}</strong>
            <a class="cancel-reply" @click="cancelReply">&times;</a>
          </div>
          <div class="form-row">
            <input
              v-model="commentForm.nickname"
              placeholder="昵称 *"
              class="form-input"
            />
            <input
              v-model="commentForm.email"
              placeholder="邮箱（可选）"
              class="form-input"
            />
          </div>
          <textarea
            v-model="commentForm.content"
            placeholder="写下你的想法..."
            class="form-textarea"
            rows="3"
          />
          <button
            class="form-submit"
            :disabled="submitting"
            @click="handleSubmitComment"
          >
            {{ submitting ? '提交中...' : '发表评论' }}
          </button>
        </div>

        <!-- 评论树 -->
        <div class="comment-tree">
          <template v-for="c in comments" :key="c.id">
            <div class="comment-item">
              <div class="c-head">
                <span class="c-nick">{{ c.nickname }}</span>
                <span v-if="c.isAdminReply" class="c-badge">博主</span>
                <span class="c-date">{{ fmtDate(c.createTime) }}</span>
              </div>
              <div v-if="editingId === c.id" class="c-edit-wrap">
                <input v-model="editContent" class="form-input" />
                <button class="inline-btn" @click="doEdit(c)">保存</button>
                <button class="inline-btn" @click="editingId = null">
                  取消
                </button>
              </div>
              <div v-else class="c-body" v-html="c.contentHtml" />
              <div class="c-actions">
                <a @click="startReply(c)">回复</a>
                <template v-if="isOwn(c)">
                  <a @click="startEdit(c)">编辑</a>
                  <a class="danger" @click="doDelete(c)">删除</a>
                </template>
              </div>
              <!-- 子评论 -->
              <div v-if="c.children?.length" class="comment-children">
                <div
                  v-for="child in c.children"
                  :key="child.id"
                  class="comment-item child"
                >
                  <div class="c-head">
                    <span class="c-nick">{{ child.nickname }}</span>
                    <span v-if="child.isAdminReply" class="c-badge">博主</span>
                    <span v-if="child.parentNickname" class="c-reply-to">
                      回复 {{ child.parentNickname }}
                    </span>
                    <span class="c-date">{{ fmtDate(child.createTime) }}</span>
                  </div>
                  <div v-if="editingId === child.id" class="c-edit-wrap">
                    <input v-model="editContent" class="form-input" />
                    <button class="inline-btn" @click="doEdit(child)">
                      保存
                    </button>
                    <button class="inline-btn" @click="editingId = null">
                      取消
                    </button>
                  </div>
                  <div v-else class="c-body" v-html="child.contentHtml" />
                  <div class="c-actions">
                    <a @click="startReply(child)">回复</a>
                    <template v-if="isOwn(child)">
                      <a @click="startEdit(child)">编辑</a>
                      <a class="danger" @click="doDelete(child)">删除</a>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </template>
          <div v-if="!comments.length" class="empty-comment">
            暂无评论，来抢沙发
          </div>
        </div>
      </div>
    </template>

    <div v-else class="not-found">文章不存在</div>
  </div>
</template>

<style scoped>
.article-page {
  max-width: 740px;
  margin: 0 auto;
}

/* 骨架 */
.loading-wrap {
  padding: 20px 0;
}
.skeleton-line {
  height: 14px;
  background: #eee;
  border-radius: 2px;
  margin-bottom: 10px;
}
.w60 {
  width: 60%;
}
.w40 {
  width: 40%;
}
.w90 {
  width: 90%;
}

/* 头部 */
.article-header {
  margin-bottom: 24px;
}
.article-category {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  color: #888;
  font-weight: 600;
  display: inline-block;
  margin-bottom: 6px;
}
.article-title {
  font-family: var(--blog-serif);
  font-size: 30px;
  font-weight: 800;
  margin: 0 0 12px;
  line-height: 1.35;
  color: #1a1a1a;
}
.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: #888;
}
.article-meta .iconfont {
  font-size: 13px;
  margin-right: 3px;
}
.article-tags {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.atag {
  font-size: 12px;
  color: #666;
  padding: 2px 8px;
  border: 1px solid #e8e8e4;
  border-radius: 2px;
}

/* 封面 */
.article-cover {
  margin-bottom: 24px;
  border: 1px solid #e8e8e4;
  border-radius: 3px;
  overflow: hidden;
}
.article-cover img {
  width: 100%;
  display: block;
}

/* 正文 */
.article-content {
  font-size: 15.5px;
  line-height: 1.85;
  color: #333;
  word-break: break-word;
}
.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3),
.article-content :deep(h4) {
  font-family: var(--blog-serif);
  color: #1a1a1a;
  margin: 28px 0 12px;
  font-weight: 700;
}
.article-content :deep(h2) {
  font-size: 22px;
  border-bottom: 1px solid #e8e8e4;
  padding-bottom: 6px;
}
.article-content :deep(h3) {
  font-size: 18px;
}
.article-content :deep(p) {
  margin: 0 0 14px;
}
.article-content :deep(a) {
  color: #1a1a1a;
  text-decoration: underline;
}
.article-content :deep(img) {
  max-width: 100%;
  border: 1px solid #e8e8e4;
  border-radius: 3px;
  margin: 8px 0;
}
.article-content :deep(blockquote) {
  margin: 14px 0;
  padding: 10px 16px;
  border-left: 3px solid #1a1a1a;
  background: #fafaf7;
  color: #555;
  font-style: italic;
}
.article-content :deep(code) {
  background: #f4f4f0;
  padding: 2px 5px;
  border-radius: 2px;
  font-size: 14px;
}
.article-content :deep(pre) {
  background: #1a1a1a;
  color: #e8e8e4;
  padding: 16px;
  border-radius: 3px;
  overflow-x: auto;
  margin: 14px 0;
  font-size: 13.5px;
  line-height: 1.6;
}
.article-content :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
}
.article-content :deep(ul),
.article-content :deep(ol) {
  padding-left: 24px;
  margin: 0 0 14px;
}
.article-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 14px 0;
  font-size: 14px;
}
.article-content :deep(th),
.article-content :deep(td) {
  border: 1px solid #d4d4d0;
  padding: 8px 12px;
  text-align: left;
}
.article-content :deep(th) {
  background: #f4f4f0;
  font-weight: 600;
}

/* 操作 */
.article-actions {
  margin: 32px 0;
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid #e8e8e4;
  border-bottom: 1px solid #e8e8e4;
}
.like-btn {
  background: none;
  border: 1px solid #d4d4d0;
  border-radius: 2px;
  padding: 8px 24px;
  font-size: 14px;
  color: #555;
  cursor: pointer;
  transition: all 0.15s;
  font-family: inherit;
}
.like-btn:hover,
.like-btn.liked {
  color: #1a1a1a;
  border-color: #1a1a1a;
}
.like-btn .iconfont {
  margin-right: 4px;
}

/* 上下篇 */
.article-nav {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin: 24px 0;
  font-size: 14px;
}
.nav-prev,
.nav-next {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #555;
  text-decoration: none;
  max-width: 48%;
  transition: color 0.15s;
}
.nav-prev:hover,
.nav-next:hover {
  color: #1a1a1a;
}
.nav-next {
  margin-left: auto;
  text-align: right;
}

/* 相关文章 */
.related-section {
  margin: 24px 0;
  padding: 16px 0;
  border-top: 1px solid #e8e8e4;
}
.related-title {
  font-size: 14px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin: 0 0 10px;
  color: #1a1a1a;
}
.related-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.related-list li {
  padding: 5px 0;
  border-bottom: 1px dashed #f0f0ec;
  font-size: 14px;
}
.related-list a {
  color: #555;
  text-decoration: none;
}
.related-list a:hover {
  color: #1a1a1a;
}

/* ===== 评论区 ===== */
.comment-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid #1a1a1a;
}
.comment-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 18px;
  color: #1a1a1a;
}
.comment-title .iconfont {
  font-size: 16px;
  margin-right: 4px;
}

/* 表单 */
.comment-form {
  margin-bottom: 24px;
}
.reply-hint {
  font-size: 13px;
  color: #555;
  margin-bottom: 8px;
  padding: 6px 10px;
  background: #f7f7f4;
  border-radius: 2px;
}
.cancel-reply {
  cursor: pointer;
  margin-left: 6px;
  color: #999;
  font-size: 16px;
}
.form-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}
.form-input {
  flex: 1;
  border: 1px solid #d4d4d0;
  border-radius: 2px;
  padding: 7px 10px;
  font-size: 13px;
  outline: none;
  font-family: inherit;
  background: #fff;
}
.form-input:focus {
  border-color: #1a1a1a;
}
.form-textarea {
  width: 100%;
  border: 1px solid #d4d4d0;
  border-radius: 2px;
  padding: 8px 10px;
  font-size: 13px;
  resize: vertical;
  outline: none;
  font-family: inherit;
  margin-bottom: 8px;
  box-sizing: border-box;
  background: #fff;
}
.form-textarea:focus {
  border-color: #1a1a1a;
}
.form-submit {
  background: #1a1a1a;
  color: #fff;
  border: none;
  padding: 8px 20px;
  font-size: 13px;
  cursor: pointer;
  border-radius: 2px;
  font-family: inherit;
}
.form-submit:disabled {
  opacity: 0.5;
}

/* 评论树 */
.comment-tree {
  margin-top: 4px;
}
.comment-item {
  padding: 14px 0;
  border-bottom: 1px solid #f0f0ec;
}
.comment-item.child {
  padding: 10px 0 10px 16px;
  border-bottom: 1px dashed #f0f0ec;
  border-left: 2px solid #e8e8e4;
}
.comment-children {
  margin-top: 4px;
}
.c-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}
.c-nick {
  font-size: 13px;
  font-weight: 700;
  color: #1a1a1a;
}
.c-badge {
  font-size: 10px;
  background: #1a1a1a;
  color: #fff;
  padding: 1px 5px;
  border-radius: 2px;
  font-weight: 600;
}
.c-reply-to {
  font-size: 12px;
  color: #888;
}
.c-date {
  font-size: 12px;
  color: #aaa;
  margin-left: auto;
}
.c-body {
  font-size: 14px;
  color: #444;
  line-height: 1.65;
}
.c-body :deep(p) {
  margin: 0;
}
.c-actions {
  display: flex;
  gap: 10px;
  margin-top: 4px;
}
.c-actions a {
  font-size: 12px;
  color: #888;
  cursor: pointer;
  text-decoration: none;
}
.c-actions a:hover {
  color: #1a1a1a;
}
.c-actions a.danger:hover {
  color: #c00;
}
.c-edit-wrap {
  display: flex;
  gap: 6px;
  align-items: center;
  margin: 4px 0;
}
.inline-btn {
  background: none;
  border: 1px solid #d4d4d0;
  padding: 3px 10px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 2px;
  font-family: inherit;
}
.inline-btn:hover {
  border-color: #1a1a1a;
}
.empty-comment {
  padding: 32px 0;
  text-align: center;
  color: #999;
  font-size: 14px;
}
.not-found {
  padding: 80px 0;
  text-align: center;
  color: #999;
  font-size: 16px;
}

@media (max-width: 600px) {
  .article-title {
    font-size: 24px;
  }
  .article-nav {
    flex-direction: column;
  }
  .form-row {
    flex-direction: column;
  }
}
</style>
