<script setup>
import { ref, inject, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useVisitorStore } from '@/stores'
import { getArticleBySlug } from '@/api/article'
import {
  getCommentTree,
  submitComment,
  editComment,
  deleteComment
} from '@/api/comment'
import { likeArticle, unlikeArticle, hasLiked } from '@/api/like'
import { generateCaptcha } from '@/api/captcha'
import TableOfContents from '@/components/TableOfContents.vue'

const route = useRoute()
const visitorStore = useVisitorStore()
const { articleCover, articleTitle, articleMeta } = inject('setHero')

const article = ref(null)
const loading = ref(true)
const liked = ref(false)
const liking = ref(false)

/* 评论 */
const comments = ref([])
const commentForm = ref({
  nickname: '',
  emailOrQq: '',
  content: '',
  captchaAnswer: '',
  isSecret: false,
  isNotice: true,
  isMarkdown: true
})
const replyTarget = ref(null)
const submitting = ref(false)
const editingId = ref(null)
const editContent = ref('')

/* 验证码 */
const captcha = ref({ question: '', result: null })
const captchaHover = ref(false)
const captchaFocus = ref(false)
const loadCaptcha = async () => {
  try {
    const res = await generateCaptcha()
    captcha.value = res.data.data
  } catch {
    /* ignore */
  }
}

const loadArticle = async (slug) => {
  loading.value = true
  try {
    const res = await getArticleBySlug(slug)
    article.value = res.data.data
    document.title = `${article.value.title} - FeiTwnd`
    // 更新 Hero
    articleTitle.value = article.value.title
    articleCover.value = article.value.coverImage || ''
    const parts = []
    if (article.value.publishTime)
      parts.push(article.value.publishTime.slice(0, 10))
    if (article.value.categoryName) parts.push(article.value.categoryName)
    if (article.value.wordCount) parts.push(`${article.value.wordCount} 字`)
    if (article.value.readingTime)
      parts.push(`${article.value.readingTime} 分钟阅读`)
    articleMeta.value = parts.join(' · ')
    loadComments()
    checkLike()
    loadCaptcha()
  } catch {
    article.value = null
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  if (!article.value) return
  try {
    const res = await getCommentTree(article.value.id, visitorStore.visitorId)
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

/* 评论操作 */
const handleSubmitComment = async () => {
  const nick = commentForm.value.nickname.trim() || visitorStore.nickname
  const content = commentForm.value.content.trim()
  if (!nick || !content) {
    ElMessage.warning('请填写昵称和内容')
    return
  }
  // 验证码校验
  const answer = parseInt(commentForm.value.captchaAnswer, 10)
  if (isNaN(answer) || answer !== captcha.value.result) {
    ElMessage.warning('验证码错误，请重新计算')
    loadCaptcha()
    return
  }
  submitting.value = true
  try {
    const payload = {
      articleId: article.value.id,
      rootId: replyTarget.value?.rootId ?? replyTarget.value?.id ?? null,
      parentId: replyTarget.value?.id ?? null,
      parentNickname: replyTarget.value?.nickname ?? null,
      content: content,
      nickname: nick,
      visitorId: visitorStore.visitorId,
      emailOrQq: commentForm.value.emailOrQq || visitorStore.email || '',
      isMarkdown: commentForm.value.isMarkdown ? 1 : 0,
      isSecret: commentForm.value.isSecret ? 1 : 0,
      isNotice: commentForm.value.isNotice ? 1 : 0
    }
    await submitComment(payload)
    visitorStore.nickname = nick
    if (commentForm.value.emailOrQq)
      visitorStore.email = commentForm.value.emailOrQq
    commentForm.value = {
      nickname: nick,
      emailOrQq: commentForm.value.emailOrQq,
      content: '',
      captchaAnswer: '',
      isSecret: false,
      isNotice: true,
      isMarkdown: true
    }
    replyTarget.value = null
    ElMessage.success('评论成功，审核通过后将展示')
    loadComments()
    loadCaptcha()
  } finally {
    submitting.value = false
  }
}

const startReply = (c) => {
  replyTarget.value = c
  commentForm.value.nickname = visitorStore.nickname
  commentForm.value.emailOrQq = visitorStore.email
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
      content: editContent.value.trim(),
      isMarkdown: commentForm.value.isMarkdown ? 1 : 0
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

/* 头像 */
const getAvatarUrl = (c) => {
  const eq = c.emailOrQq
  if (!eq) return ''
  // 纯数字 → QQ号
  if (/^\d{5,11}$/.test(eq)) return `https://q1.qlogo.cn/g?b=qq&nk=${eq}&s=640`
  // @qq.com → 提取QQ号
  const m = eq.match(/^(\d{5,11})@qq\.com$/i)
  if (m) return `https://q1.qlogo.cn/g?b=qq&nk=${m[1]}&s=640`
  return ''
}
const getInitial = (name) => (name ? name.charAt(0).toUpperCase() : '?')

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}

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
  commentForm.value.emailOrQq = visitorStore.email
  loadArticle(route.params.slug)
  loadCaptcha()
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
      <div class="article-layout">
        <!-- 左侧: 文章内容 -->
        <div class="article-main">
          <div class="article-card">
            <!-- 文章概要 -->
            <div v-if="article.summary" class="article-summary-block">
              <i class="iconfont icon-guidang" />
              <p>{{ article.summary }}</p>
            </div>

            <!-- 正文 -->
            <div class="article-content" v-html="article.contentHtml" />

            <!-- 点赞 + 转发 -->
            <div class="article-actions-inline">
              <button
                class="action-icon"
                :class="{ liked }"
                @click="toggleLike"
                title="点赞"
              >
                <i class="iconfont icon-dianzan" />
                <span>{{ article.likeCount ?? 0 }}</span>
              </button>
              <button class="action-icon" @click="copyLink" title="复制链接">
                <i class="iconfont icon-zhuanfa" />
              </button>
            </div>

            <!-- 上下篇 -->
            <div
              v-if="article.prevArticle || article.nextArticle"
              class="article-nav"
            >
              <router-link
                v-if="article.prevArticle"
                :to="`/article/${article.prevArticle.slug}`"
                class="nav-prev"
              >
                <i class="iconfont icon-arrow-left" />
                <span>{{ article.prevArticle.title }}</span>
              </router-link>
              <div v-else />
              <router-link
                v-if="article.nextArticle"
                :to="`/article/${article.nextArticle.slug}`"
                class="nav-next"
              >
                <span>{{ article.nextArticle.title }}</span>
                <i class="iconfont icon-arrow-right" />
              </router-link>
            </div>
          </div>

          <!-- 相关文章 -->
          <div v-if="article.relatedArticles?.length" class="related-card">
            <h3 class="section-title">相关文章</h3>
            <ul class="related-list">
              <li v-for="r in article.relatedArticles" :key="r.id">
                <router-link :to="`/article/${r.slug}`">{{
                  r.title
                }}</router-link>
              </li>
            </ul>
          </div>

          <!-- 评论区 -->
          <div class="comment-card">
            <h3 class="section-title">
              <i class="iconfont icon-pinglun" /> 评论 ({{ flatCommentCount }})
            </h3>

            <div class="comment-form">
              <div v-if="replyTarget" class="reply-hint">
                回复 <strong>{{ replyTarget.nickname }}</strong>
                <a class="cancel-reply" @click="cancelReply">&times;</a>
              </div>
              <textarea
                v-model="commentForm.content"
                placeholder="写下你的想法..."
                class="form-textarea"
                rows="4"
              />
              <div class="form-row">
                <div class="input-with-icon">
                  <i class="iconfont icon-user input-icon" />
                  <input
                    v-model="commentForm.nickname"
                    placeholder="昵称 *"
                    class="form-input"
                  />
                </div>
                <div class="input-with-icon">
                  <i class="iconfont icon-youxiang input-icon" />
                  <input
                    v-model="commentForm.emailOrQq"
                    placeholder="邮箱/QQ号"
                    class="form-input"
                  />
                </div>
                <div
                  class="input-with-icon captcha-wrap"
                  @mouseenter="captchaHover = true"
                  @mouseleave="captchaHover = false"
                >
                  <i class="iconfont icon-lock input-icon" />
                  <input
                    v-model="commentForm.captchaAnswer"
                    placeholder="验证码"
                    class="form-input"
                    @focus="captchaFocus = true"
                    @blur="captchaFocus = false"
                  />
                  <span
                    v-show="(captchaHover || captchaFocus) && captcha.question"
                    class="captcha-tip"
                  >
                    {{ captcha.question }}
                  </span>
                  <span
                    class="captcha-refresh"
                    @click="loadCaptcha"
                    title="换一题"
                    >↻</span
                  >
                </div>
              </div>
              <div class="form-options">
                <label class="option-check">
                  <input type="checkbox" v-model="commentForm.isSecret" />
                  悄悄话
                </label>
                <label class="option-check">
                  <input type="checkbox" v-model="commentForm.isNotice" />
                  邮件提醒
                </label>
                <label class="option-check">
                  <input type="checkbox" v-model="commentForm.isMarkdown" />
                  Markdown
                </label>
                <button
                  class="form-submit"
                  :disabled="submitting"
                  @click="handleSubmitComment"
                >
                  {{ submitting ? '提交中...' : '发表评论' }}
                </button>
              </div>
            </div>

            <!-- 评论树 -->
            <div class="comment-tree">
              <template v-for="c in comments" :key="c.id">
                <div class="comment-item">
                  <div class="c-avatar">
                    <img
                      v-if="getAvatarUrl(c)"
                      :src="getAvatarUrl(c)"
                      class="c-avatar-img"
                    />
                    <span v-else class="c-avatar-letter">{{
                      getInitial(c.nickname)
                    }}</span>
                  </div>
                  <div class="c-main">
                    <div class="c-head">
                      <span class="c-nick">{{ c.nickname }}</span>
                      <span v-if="c.isAdminReply" class="c-badge">博主</span>
                      <span v-if="c.isApproved === 0" class="c-pending"
                        >未审核</span
                      >
                      <span class="c-meta-item"
                        ><svg
                          class="c-meta-icon"
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          stroke-width="2"
                        >
                          <path
                            d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"
                          />
                          <circle cx="12" cy="10" r="3" /></svg
                        >{{ c.location || '未知' }}</span
                      >
                      <span class="c-meta-item"
                        ><svg
                          class="c-meta-icon"
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          stroke-width="2"
                        >
                          <rect width="20" height="14" x="2" y="3" rx="2" />
                          <path d="M8 21h8M12 17v4" /></svg
                        >{{
                          c.userAgentOs && c.userAgentOs !== 'Unknown'
                            ? c.userAgentOs
                            : '未知'
                        }}</span
                      >
                      <span class="c-meta-item"
                        ><svg
                          class="c-meta-icon"
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          stroke-width="2"
                        >
                          <circle cx="12" cy="12" r="10" />
                          <circle cx="12" cy="12" r="4" />
                          <path
                            d="M21.17 8H2.83M21.17 16H2.83M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10 15.3 15.3 0 014-10z"
                          /></svg
                        >{{
                          c.userAgentBrowser && c.userAgentBrowser !== 'Unknown'
                            ? c.userAgentBrowser
                            : '未知'
                        }}</span
                      >
                      <span class="c-date">{{ fmtDate(c.createTime) }}</span>
                    </div>
                    <div v-if="editingId === c.id" class="c-edit-wrap">
                      <input v-model="editContent" class="form-input" />
                      <button class="inline-btn" @click="doEdit(c)">
                        保存
                      </button>
                      <button class="inline-btn" @click="editingId = null">
                        取消
                      </button>
                    </div>
                    <div v-else class="c-body" v-html="c.contentHtml" />
                    <div class="c-footer">
                      <div class="c-actions">
                        <a @click="startReply(c)">回复</a>
                        <template v-if="isOwn(c)">
                          <a @click="startEdit(c)">编辑</a>
                          <a class="danger" @click="doDelete(c)">删除</a>
                        </template>
                      </div>
                    </div>

                    <!-- 子评论 -->
                    <div v-if="c.children?.length" class="comment-children">
                      <div
                        v-for="child in c.children"
                        :key="child.id"
                        class="comment-item child"
                      >
                        <div class="c-avatar c-avatar-sm">
                          <img
                            v-if="getAvatarUrl(child)"
                            :src="getAvatarUrl(child)"
                            class="c-avatar-img"
                          />
                          <span v-else class="c-avatar-letter">{{
                            getInitial(child.nickname)
                          }}</span>
                        </div>
                        <div class="c-main">
                          <div class="c-head">
                            <span class="c-nick">{{ child.nickname }}</span>
                            <span v-if="child.isAdminReply" class="c-badge"
                              >博主</span
                            >
                            <span v-if="child.parentNickname" class="c-reply-to"
                              ><i class="iconfont icon-zhuanfa reply-icon" />
                              {{ child.parentNickname }}</span
                            >
                            <span
                              v-if="child.isApproved === 0"
                              class="c-pending"
                              >未审核</span
                            >
                            <span class="c-meta-item"
                              ><svg
                                class="c-meta-icon"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2"
                              >
                                <path
                                  d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"
                                />
                                <circle cx="12" cy="10" r="3" /></svg
                              >{{ child.location || '未知' }}</span
                            >
                            <span class="c-meta-item"
                              ><svg
                                class="c-meta-icon"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2"
                              >
                                <rect
                                  width="20"
                                  height="14"
                                  x="2"
                                  y="3"
                                  rx="2"
                                />
                                <path d="M8 21h8M12 17v4" /></svg
                              >{{
                                child.userAgentOs &&
                                child.userAgentOs !== 'Unknown'
                                  ? child.userAgentOs
                                  : '未知'
                              }}</span
                            >
                            <span class="c-meta-item"
                              ><svg
                                class="c-meta-icon"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2"
                              >
                                <circle cx="12" cy="12" r="10" />
                                <circle cx="12" cy="12" r="4" />
                                <path
                                  d="M21.17 8H2.83M21.17 16H2.83M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10 15.3 15.3 0 014-10z"
                                /></svg
                              >{{
                                child.userAgentBrowser &&
                                child.userAgentBrowser !== 'Unknown'
                                  ? child.userAgentBrowser
                                  : '未知'
                              }}</span
                            >
                            <span class="c-date">{{
                              fmtDate(child.createTime)
                            }}</span>
                          </div>
                          <div
                            v-if="editingId === child.id"
                            class="c-edit-wrap"
                          >
                            <input v-model="editContent" class="form-input" />
                            <button class="inline-btn" @click="doEdit(child)">
                              保存
                            </button>
                            <button
                              class="inline-btn"
                              @click="editingId = null"
                            >
                              取消
                            </button>
                          </div>
                          <div
                            v-else
                            class="c-body"
                            v-html="child.contentHtml"
                          />
                          <div class="c-footer">
                            <div class="c-actions">
                              <a @click="startReply(child)">回复</a>
                              <template v-if="isOwn(child)">
                                <a @click="startEdit(child)">编辑</a>
                                <a class="danger" @click="doDelete(child)"
                                  >删除</a
                                >
                              </template>
                            </div>
                          </div>
                        </div>
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
        </div>

        <!-- 右侧: 目录 -->
        <aside class="article-sidebar">
          <TableOfContents :content-html="article.contentHtml" />
        </aside>
      </div>
    </template>

    <div v-else class="not-found">文章不存在</div>
  </div>
</template>

<style scoped>
.article-page {
  width: 100%;
}

.loading-wrap {
  padding: 20px 0;
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
.w40 {
  width: 40%;
}
.w90 {
  width: 90%;
}

/* 双栏布局 */
.article-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.article-main {
  flex: 1;
  min-width: 0;
}
.article-sidebar {
  width: 260px;
  flex-shrink: 0;
  position: sticky;
  top: 74px;
}

/* 卡片 */
.article-card,
.related-card,
.comment-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #ebeef5;
  padding: 28px;
  margin-bottom: 16px;
}

/* 文章概要 */
.article-summary-block {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  padding: 16px 20px;
  background: #f5f7fa;
  border-radius: 6px;
  border-left: 3px solid #303133;
  margin-bottom: 24px;
  font-size: 14px;
  color: #606266;
  line-height: 1.7;
}
.article-summary-block .iconfont {
  font-size: 16px;
  color: #909399;
  margin-top: 2px;
  flex-shrink: 0;
}
.article-summary-block p {
  margin: 0;
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
  color: #303133;
  margin: 28px 0 12px;
  font-weight: 700;
}
.article-content :deep(h2) {
  font-size: 22px;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 6px;
}
.article-content :deep(h3) {
  font-size: 18px;
}
.article-content :deep(p) {
  margin: 0 0 14px;
}
.article-content :deep(a) {
  color: #303133;
  text-decoration: underline;
}
.article-content :deep(img) {
  max-width: 100%;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin: 8px 0;
}
.article-content :deep(blockquote) {
  margin: 14px 0;
  padding: 10px 16px;
  border-left: 3px solid #303133;
  background: #f5f7fa;
  color: #606266;
  font-style: italic;
}
.article-content :deep(code) {
  background: #f5f7fa;
  padding: 2px 5px;
  border-radius: 3px;
  font-size: 14px;
}
.article-content :deep(pre) {
  background: #303133;
  color: #e4e7ed;
  padding: 16px;
  border-radius: 6px;
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
  border: 1px solid #e4e7ed;
  padding: 8px 12px;
  text-align: left;
}
.article-content :deep(th) {
  background: #f5f7fa;
  font-weight: 600;
}

/* 点赞+转发 */
.article-actions-inline {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
.action-icon {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 13px;
  color: #909399;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: inherit;
  transition:
    color 0.15s,
    background 0.15s;
}
.action-icon:hover {
  color: #303133;
  background: #f5f7fa;
}
.action-icon.liked {
  color: #303133;
}
.action-icon .iconfont {
  font-size: 16px;
}

/* 上下篇 */
.article-nav {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  font-size: 14px;
}
.nav-prev,
.nav-next {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  text-decoration: none;
  max-width: 48%;
  transition: color 0.15s;
}
.nav-prev:hover,
.nav-next:hover {
  color: #303133;
}
.nav-next {
  margin-left: auto;
  text-align: right;
}

/* 相关文章 */
.section-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 14px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
}
.section-title .iconfont {
  font-size: 16px;
}
.related-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.related-list li {
  padding: 6px 0;
  border-bottom: 1px dashed #ebeef5;
  font-size: 14px;
}
.related-list a {
  color: #606266;
  text-decoration: none;
}
.related-list a:hover {
  color: #303133;
}

/* 评论表单 */
.comment-form {
  margin-bottom: 24px;
}
.reply-hint {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}
.cancel-reply {
  cursor: pointer;
  margin-left: 6px;
  color: #909399;
  font-size: 16px;
}
.form-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}
.input-with-icon {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
}
.input-icon {
  position: absolute;
  left: 10px;
  font-size: 14px;
  color: #c0c4cc;
  pointer-events: none;
  z-index: 1;
}
.input-with-icon .form-input {
  padding-left: 32px;
  width: 100%;
}
.captcha-wrap {
  min-width: 0;
}
.captcha-refresh {
  position: absolute;
  right: 8px;
  font-size: 16px;
  color: #909399;
  cursor: pointer;
  user-select: none;
  transition: color 0.15s;
}
.captcha-refresh:hover {
  color: #303133;
}
.captcha-tip {
  position: absolute;
  top: -28px;
  left: 32px;
  font-size: 12px;
  color: #303133;
  background: #fff;
  padding: 2px 10px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  border: 1px solid #ebeef5;
  white-space: nowrap;
  z-index: 10;
  pointer-events: none;
  font-weight: 600;
}
.form-input {
  flex: 1;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 8px 12px;
  font-size: 13px;
  outline: none;
  font-family: inherit;
  background: #fff;
  box-sizing: border-box;
}
.form-input:focus {
  border-color: #303133;
}
.form-textarea {
  width: 100%;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 13px;
  resize: vertical;
  outline: none;
  font-family: inherit;
  margin-bottom: 8px;
  box-sizing: border-box;
  background: #fff;
}
.form-textarea:focus {
  border-color: #303133;
}
.form-options {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.option-check {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
}
.option-check input[type='checkbox'] {
  accent-color: #303133;
}
.form-submit {
  background: #303133;
  color: #fff;
  border: none;
  padding: 8px 24px;
  font-size: 13px;
  cursor: pointer;
  border-radius: 6px;
  font-family: inherit;
  margin-left: auto;
  transition: background 0.15s;
}
.form-submit:hover {
  background: #000;
}
.form-submit:disabled {
  opacity: 0.5;
}

/* 评论树 */
.comment-tree {
  margin-top: 4px;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
}
.comment-item.child {
  padding: 12px 0;
  border-bottom: 1px dashed #ebeef5;
}
.comment-children {
  margin-top: 8px;
  padding-left: 4px;
  border-left: 2px solid #ebeef5;
}

/* 头像 */
.c-avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
}
.c-avatar-sm {
  width: 32px;
  height: 32px;
}
.c-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ebeef5;
}
.c-avatar-letter {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #e4e7ed;
  color: #606266;
  font-size: 16px;
  font-weight: 700;
  font-family: var(--blog-serif);
  user-select: none;
}
.c-avatar-sm .c-avatar-letter {
  font-size: 13px;
}

.c-main {
  flex: 1;
  min-width: 0;
}
.c-head {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}
.c-nick {
  font-size: 14px;
  font-weight: 700;
  color: #303133;
}
.c-badge {
  font-size: 10px;
  background: #303133;
  color: #fff;
  padding: 1px 5px;
  border-radius: 3px;
  font-weight: 600;
}
.c-reply-to {
  font-size: 12px;
  color: #909399;
}
.c-pending {
  font-size: 10px;
  color: #e6a23c;
  background: #fdf6ec;
  padding: 1px 6px;
  border-radius: 3px;
  font-weight: 500;
}
.reply-icon {
  display: inline-block;
  transform: scaleX(-1);
  font-size: 12px;
}

/* 元信息（位置、系统、浏览器）— 与昵称同行 */
.c-meta-item {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: #b0b4bb;
}
.c-meta-icon {
  width: 13px;
  height: 13px;
  flex-shrink: 0;
}

.c-body {
  font-size: 14px;
  color: #444;
  line-height: 1.65;
}
.c-body :deep(p) {
  margin: 0;
}

.c-date {
  font-size: 12px;
  color: #c0c4cc;
  margin-left: auto;
  flex-shrink: 0;
}
/* 底部：hover操作在右下角 */
.c-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 4px;
}
.c-actions {
  display: flex;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.15s;
}
.comment-item:hover .c-actions {
  opacity: 1;
}
.c-actions a {
  font-size: 12px;
  color: #909399;
  cursor: pointer;
  text-decoration: none;
}
.c-actions a:hover {
  color: #303133;
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
  border: 1px solid #e4e7ed;
  padding: 3px 10px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 4px;
  font-family: inherit;
}
.inline-btn:hover {
  border-color: #303133;
}
.empty-comment {
  padding: 32px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
}
.not-found {
  padding: 80px 0;
  text-align: center;
  color: #909399;
  font-size: 16px;
}

@media (max-width: 960px) {
  .article-layout {
    flex-direction: column;
  }
  .article-sidebar {
    width: 100%;
    position: static;
  }
}
@media (max-width: 600px) {
  .article-card,
  .related-card,
  .comment-card {
    padding: 16px;
  }
  .article-nav {
    flex-direction: column;
  }
  .form-row {
    flex-direction: column;
  }
}
</style>
