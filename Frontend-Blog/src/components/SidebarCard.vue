<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBlogStore, useVisitorStore } from '@/stores'
import { addSubscription, checkSubscription, unsubscribe } from '@/api/rss'

const router = useRouter()
const blogStore = useBlogStore()
const visitorStore = useVisitorStore()

/* 分类/标签弹窗 */
const showCatModal = ref(false)
const showTagModal = ref(false)

/* RSS 订阅 */
const showRssModal = ref(false)
const rssForm = ref({ nickname: '', email: '' })
const rssSubmitting = ref(false)
const isSubscribed = ref(false)
const rssEditMode = ref(false) // 编辑模式

const checkRssStatus = async () => {
  if (!visitorStore.visitorId) return
  try {
    const res = await checkSubscription(visitorStore.visitorId)
    const data = res.data.data
    if (data && data.subscribed) {
      isSubscribed.value = true
      rssForm.value.nickname = data.nickname || ''
      rssForm.value.email = data.email || ''
    } else {
      isSubscribed.value = false
    }
  } catch {
    /* ignore */
  }
}

const openRssModal = () => {
  rssEditMode.value = false
  showRssModal.value = true
}

const handleRssSubscribe = async () => {
  if (!rssForm.value.email.trim()) {
    ElMessage.warning('请输入邮箱')
    return
  }
  rssSubmitting.value = true
  try {
    await addSubscription({
      visitorId: visitorStore.visitorId,
      nickname: rssForm.value.nickname.trim() || '',
      email: rssForm.value.email.trim()
    })
    ElMessage.success(rssEditMode.value ? '订阅信息已更新' : '订阅成功')
    isSubscribed.value = true
    rssEditMode.value = false
    showRssModal.value = false
  } catch {
    ElMessage.error('操作失败')
  } finally {
    rssSubmitting.value = false
  }
}

const handleRssUnsubscribe = async () => {
  if (!rssForm.value.email) return
  rssSubmitting.value = true
  try {
    await unsubscribe(rssForm.value.email)
    ElMessage.success('已取消订阅')
    isSubscribed.value = false
    rssForm.value = { nickname: '', email: '' }
    rssEditMode.value = false
    showRssModal.value = false
  } catch {
    ElMessage.error('取消失败')
  } finally {
    rssSubmitting.value = false
  }
}

/* WebSocket 在线访客 */
const onlineCount = ref(0)
let ws = null

const connectWs = () => {
  const protocol = location.protocol === 'https:' ? 'wss' : 'ws'
  const host = import.meta.env.DEV ? 'localhost:5922' : location.host
  try {
    ws = new WebSocket(`${protocol}://${host}/api/ws/online`)
    ws.onmessage = (e) => {
      const msg = e.data
      if (msg === 'pong') return
      const count = parseInt(msg, 10)
      if (!isNaN(count)) onlineCount.value = count
    }
    ws.onclose = () => {
      setTimeout(connectWs, 5000)
    }
    ws.onerror = () => {
      ws?.close()
    }
  } catch {
    /* ignore */
  }
}

onMounted(() => {
  connectWs()
  checkRssStatus()
})
onUnmounted(() => {
  ws?.close()
  ws = null
})

const report = computed(() => blogStore.report)
const info = computed(() => blogStore.personalInfo)

const goArchive = () => router.push('/archive')
const goCategory = (slug) => {
  showCatModal.value = false
  router.push(`/category/${slug}`)
}
const goTag = (slug) => {
  showTagModal.value = false
  router.push(`/tag/${slug}`)
}
</script>

<template>
  <aside class="sidebar">
    <!-- 个人信息卡片 -->
    <div class="side-card info-card">
      <div class="info-avatar-wrap">
        <img
          v-if="info.avatar"
          :src="info.avatar"
          class="info-avatar"
          loading="lazy"
        />
      </div>
      <h3 class="info-name">{{ info.nickname || 'FeiTwnd' }}</h3>
      <p v-if="info.tag" class="info-tag">{{ info.tag }}</p>
      <p v-if="info.location" class="info-location">
        <i class="iconfont icon-position" />
        {{ info.location }}
      </p>

      <!-- 数据统计行 -->
      <div class="info-stats">
        <div class="info-stat" @click="goArchive">
          <span class="stat-num">{{ report.articleTotalCount ?? 0 }}</span>
          <span class="stat-label">文章</span>
        </div>
        <div class="info-stat" @click="showCatModal = true">
          <span class="stat-num">{{ report.categoryTotalCount ?? 0 }}</span>
          <span class="stat-label">分类</span>
        </div>
        <div class="info-stat" @click="showTagModal = true">
          <span class="stat-num">{{ report.tagTotalCount ?? 0 }}</span>
          <span class="stat-label">标签</span>
        </div>
      </div>

      <!-- 社交链接 -->
      <div class="info-social">
        <a
          v-if="info.github"
          :href="info.github"
          target="_blank"
          rel="noopener"
          class="social-link"
          title="GitHub"
        >
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path
              d="M12 .3a12 12 0 00-3.8 23.38c.6.12.83-.26.83-.57L9 21.07c-3.34.72-4.04-1.61-4.04-1.61-.55-1.39-1.34-1.76-1.34-1.76-1.08-.74.08-.73.08-.73 1.2.08 1.84 1.24 1.84 1.24 1.07 1.83 2.81 1.3 3.5 1 .1-.78.42-1.3.76-1.6-2.67-.3-5.47-1.33-5.47-5.93 0-1.31.47-2.38 1.24-3.22-.13-.3-.54-1.52.12-3.18 0 0 1-.33 3.3 1.23a11.5 11.5 0 016.02 0c2.28-1.56 3.29-1.23 3.29-1.23.66 1.66.25 2.88.12 3.18a4.65 4.65 0 011.23 3.22c0 4.61-2.81 5.63-5.48 5.93.43.37.81 1.1.81 2.22l-.01 3.29c0 .31.22.69.83.57A12 12 0 0012 .3"
            />
          </svg>
        </a>
        <a
          v-if="info.email"
          :href="`mailto:${info.email}`"
          class="social-link"
          title="Email"
        >
          <svg
            viewBox="0 0 24 24"
            width="18"
            height="18"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <rect width="20" height="16" x="2" y="4" rx="2" />
            <path d="m22 7-8.97 5.7a1.94 1.94 0 01-2.06 0L2 7" />
          </svg>
        </a>
        <span
          class="social-link"
          :class="{ subscribed: isSubscribed }"
          :title="isSubscribed ? '已订阅 RSS' : 'RSS 订阅'"
          @click="openRssModal"
        >
          <i class="iconfont icon-rssdingyue" style="font-size: 18px" />
        </span>
      </div>
    </div>

    <!-- 站点统计卡片 -->
    <div class="side-card stats-card">
      <h4 class="card-title"><i class="iconfont icon-eye" /> 站点统计</h4>
      <div class="stats-grid">
        <div class="sg-item">
          <span class="sg-label">在线访客</span>
          <span class="sg-val">{{ onlineCount }}</span>
        </div>
        <div class="sg-item">
          <span class="sg-label">今日浏览</span>
          <span class="sg-val">{{ report.viewTodayCount ?? 0 }}</span>
        </div>
        <div class="sg-item">
          <span class="sg-label">总浏览量</span>
          <span class="sg-val">{{ report.viewTotalCount ?? 0 }}</span>
        </div>
        <div class="sg-item">
          <span class="sg-label">总访客量</span>
          <span class="sg-val">{{ report.visitorTotalCount ?? 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 默认插槽（文章页可插入目录等） -->
    <slot />

    <!-- ===== 分类弹窗 ===== -->
    <el-dialog
      v-model="showCatModal"
      title="所有分类"
      width="420px"
      :append-to-body="true"
    >
      <div class="modal-list">
        <div
          v-for="cat in blogStore.categories"
          :key="cat.id"
          class="modal-item"
          @click="goCategory(cat.slug)"
        >
          <i class="iconfont icon-folder" />
          <span class="modal-name">{{ cat.name }}</span>
          <span class="modal-count">{{ cat.articleCount ?? 0 }} 篇</span>
        </div>
        <div v-if="!blogStore.categories.length" class="modal-empty">
          暂无分类
        </div>
      </div>
    </el-dialog>

    <!-- ===== 标签弹窗 ===== -->
    <el-dialog
      v-model="showTagModal"
      title="所有标签"
      width="420px"
      :append-to-body="true"
    >
      <div class="modal-tag-cloud">
        <span
          v-for="tag in blogStore.tags"
          :key="tag.id"
          class="modal-tag"
          @click="goTag(tag.slug)"
        >
          <i class="iconfont icon-biaoqian" /> {{ tag.name }}
          <span class="tag-count">{{ tag.articleCount ?? 0 }}</span>
        </span>
        <div v-if="!blogStore.tags.length" class="modal-empty">暂无标签</div>
      </div>
    </el-dialog>

    <!-- ===== RSS 订阅弹窗 ===== -->
    <el-dialog
      v-model="showRssModal"
      :title="isSubscribed && !rssEditMode ? '已订阅 RSS' : 'RSS 订阅'"
      width="380px"
      :append-to-body="true"
    >
      <!-- 已订阅 - 查看状态 -->
      <div v-if="isSubscribed && !rssEditMode" class="rss-status">
        <div class="rss-status-row">
          <span class="rss-label">昵称</span>
          <span class="rss-value">{{ rssForm.nickname || '未设置' }}</span>
        </div>
        <div class="rss-status-row">
          <span class="rss-label">邮箱</span>
          <span class="rss-value">{{ rssForm.email }}</span>
        </div>
        <div class="rss-actions">
          <button class="rss-edit-btn" @click="rssEditMode = true">编辑</button>
          <button
            class="rss-unsub-btn"
            :disabled="rssSubmitting"
            @click="handleRssUnsubscribe"
          >
            {{ rssSubmitting ? '处理中...' : '取消订阅' }}
          </button>
        </div>
      </div>
      <!-- 未订阅 / 编辑模式 -->
      <div v-else class="rss-form">
        <input
          v-model="rssForm.nickname"
          placeholder="昵称"
          class="rss-form-input"
        />
        <input
          v-model="rssForm.email"
          type="email"
          placeholder="邮箱 *"
          class="rss-form-input"
        />
        <div class="rss-form-btns">
          <button
            v-if="rssEditMode"
            class="rss-cancel-btn"
            @click="rssEditMode = false"
          >
            取消
          </button>
          <button
            class="rss-submit-btn"
            :disabled="rssSubmitting"
            @click="handleRssSubscribe"
          >
            {{ rssSubmitting ? '提交中...' : rssEditMode ? '保存' : '订阅' }}
          </button>
        </div>
      </div>
    </el-dialog>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 280px;
  flex-shrink: 0;
  position: sticky;
  top: 74px;
}
.side-card {
  background: var(--blog-card);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--blog-border-light);
  padding: 20px;
  margin-bottom: 16px;
}

/* 个人信息卡片 */
.info-card {
  text-align: center;
}
.info-avatar-wrap {
  margin-bottom: 12px;
}
.info-avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #ebeef5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.info-name {
  font-size: 17px;
  font-weight: 700;
  margin: 0 0 4px;
  color: #303133;
}
.info-tag {
  font-size: 12px;
  color: #909399;
  margin: 0 0 6px;
}
.info-location {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3px;
  font-size: 12px;
  color: #b0b0b0;
  margin: 0 0 14px;
}
.info-location .iconfont {
  font-size: 13px;
}

.info-stats {
  display: flex;
  justify-content: center;
  gap: 0;
  padding: 12px 0;
  margin: 0 -20px;
}
.info-stat {
  flex: 1;
  text-align: center;
  cursor: pointer;
  transition: background 0.15s;
  padding: 4px 0;
  border-radius: 4px;
}
.info-stat:hover {
  background: #f5f7fa;
}
.info-stat + .info-stat {
  border-left: 1px solid #ebeef5;
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

.info-social {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 14px;
}
.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  color: #606266;
  cursor: pointer;
  background: none;
  transition:
    color 0.15s,
    background 0.15s;
}
.social-link:hover {
  color: #303133;
  background: #f5f7fa;
}
.social-link.subscribed {
  color: #303133;
}

/* 站点统计 */
.card-title {
  font-size: 14px;
  font-weight: 700;
  margin: 0 0 12px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
}
.card-title .iconfont {
  font-size: 15px;
}
.stats-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.sg-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #606266;
}
.sg-icon {
  margin-right: 4px;
  font-size: 10px;
}
.sg-label {
  flex: 1;
}
.sg-val {
  font-weight: 600;
  color: #303133;
  font-family: var(--blog-serif);
}

/* 弹窗内容 */
.modal-list {
  max-height: 400px;
  overflow-y: auto;
}
.modal-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.12s;
  font-size: 14px;
  color: #303133;
}
.modal-item:hover {
  background: #f5f7fa;
}
.modal-item .iconfont {
  font-size: 15px;
  color: #909399;
}
.modal-name {
  flex: 1;
}
.modal-count {
  font-size: 12px;
  color: #909399;
}
.modal-empty {
  text-align: center;
  color: #909399;
  padding: 20px;
  font-size: 14px;
}

.modal-tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 400px;
  overflow-y: auto;
  padding: 4px 0;
}
.modal-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 16px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  transition: all 0.15s;
}
.modal-tag:hover {
  color: #303133;
  border-color: #303133;
  background: #f5f7fa;
}
.modal-tag .iconfont {
  font-size: 12px;
}
.tag-count {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 1px 6px;
  font-size: 11px;
  color: #909399;
  margin-left: 2px;
}

@media (max-width: 960px) {
  .sidebar {
    width: 100%;
    position: static;
  }
}

/* RSS 订阅弹窗 */
.rss-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.rss-form-input {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  font-size: 13px;
  outline: none;
  font-family: inherit;
  box-sizing: border-box;
}
.rss-form-input:focus {
  border-color: #303133;
}
.rss-form-btns {
  display: flex;
  gap: 8px;
}
.rss-submit-btn {
  flex: 1;
  padding: 8px 0;
  font-size: 13px;
  border: none;
  background: #303133;
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
  font-family: inherit;
  transition: background 0.15s;
}
.rss-submit-btn:hover {
  background: #000;
}
.rss-submit-btn:disabled {
  opacity: 0.5;
}
.rss-cancel-btn {
  padding: 8px 20px;
  font-size: 13px;
  border: 1px solid #e4e7ed;
  background: #fff;
  color: #606266;
  border-radius: 6px;
  cursor: pointer;
  font-family: inherit;
  transition: border-color 0.15s;
}
.rss-cancel-btn:hover {
  border-color: #303133;
}

/* RSS 已订阅状态 */
.rss-status {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.rss-status-row {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}
.rss-label {
  color: #909399;
  min-width: 36px;
}
.rss-value {
  color: #303133;
  font-weight: 500;
}
.rss-actions {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}
.rss-edit-btn {
  flex: 1;
  padding: 8px 0;
  font-size: 13px;
  border: 1px solid #e4e7ed;
  background: #fff;
  color: #303133;
  border-radius: 6px;
  cursor: pointer;
  font-family: inherit;
  transition: border-color 0.15s;
}
.rss-edit-btn:hover {
  border-color: #303133;
}
.rss-unsub-btn {
  flex: 1;
  padding: 8px 0;
  font-size: 13px;
  border: none;
  background: #f5f7fa;
  color: #909399;
  border-radius: 6px;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.15s;
}
.rss-unsub-btn:hover {
  background: #fee;
  color: #c00;
}
.rss-unsub-btn:disabled {
  opacity: 0.5;
}
</style>
