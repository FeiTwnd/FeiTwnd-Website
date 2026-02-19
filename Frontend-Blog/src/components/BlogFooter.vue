<script setup>
import { ref } from 'vue'
import { useBlogStore } from '@/stores'
import { addSubscription } from '@/api/rss'

const blogStore = useBlogStore()
const rssEmail = ref('')
const subscribing = ref(false)

const handleSubscribe = async () => {
  const mail = rssEmail.value.trim()
  if (!mail) return
  subscribing.value = true
  try {
    await addSubscription({ visitorId: 0, nickname: 'subscriber', email: mail })
    ElMessage.success('订阅成功')
    rssEmail.value = ''
  } catch {
    ElMessage.error('订阅失败')
  } finally {
    subscribing.value = false
  }
}
</script>

<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <div class="footer-col">
        <p class="footer-brand">FeiTwnd's Blog</p>
        <p class="footer-desc">
          {{ blogStore.personalInfo.description || '' }}
        </p>
      </div>

      <div class="footer-col">
        <p class="footer-label">RSS 订阅</p>
        <div class="rss-row">
          <input
            v-model="rssEmail"
            type="email"
            placeholder="输入邮箱订阅"
            class="rss-input"
            @keyup.enter="handleSubscribe"
          />
          <button
            class="rss-btn"
            :disabled="subscribing"
            @click="handleSubscribe"
          >
            <i class="iconfont icon-rss" />
          </button>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <span
        >&copy; {{ new Date().getFullYear() }} FeiTwnd. All rights
        reserved.</span
      >
    </div>
  </footer>
</template>

<style scoped>
.site-footer {
  border-top: 2px solid #1a1a1a;
  background: var(--blog-bg);
  margin-top: auto;
}
.footer-inner {
  max-width: 1060px;
  margin: 0 auto;
  padding: 28px 24px 18px;
  display: flex;
  justify-content: space-between;
  gap: 40px;
  flex-wrap: wrap;
}
.footer-col {
  min-width: 200px;
}
.footer-brand {
  font-family: var(--blog-serif);
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 1px;
  margin: 0 0 6px;
}
.footer-desc {
  font-size: 13px;
  color: #777;
  margin: 0 0 10px;
  max-width: 320px;
  line-height: 1.6;
}

.footer-label {
  font-size: 13px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #1a1a1a;
}
.rss-row {
  display: flex;
  gap: 6px;
}
.rss-input {
  border: 1px solid #d4d4d0;
  border-radius: 2px;
  padding: 5px 10px;
  font-size: 13px;
  width: 180px;
  outline: none;
  font-family: inherit;
}
.rss-input:focus {
  border-color: #1a1a1a;
}
.rss-btn {
  background: #1a1a1a;
  color: #fff;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 2px;
}
.rss-btn:disabled {
  opacity: 0.5;
}
.footer-bottom {
  border-top: 1px solid #e8e8e4;
  text-align: center;
  padding: 12px 24px;
  font-size: 12px;
  color: #999;
}
</style>
