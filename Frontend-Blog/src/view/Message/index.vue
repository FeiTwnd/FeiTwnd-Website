<script setup>
import { ref, onMounted, computed } from 'vue'
import {
  getMessageTree,
  submitMessage,
  editMessage,
  deleteMessage
} from '@/api/message'
import { generateCaptcha } from '@/api/captcha'
import { useVisitorStore } from '@/stores'

const visitorStore = useVisitorStore()

const messages = ref([])
const loading = ref(false)

const form = ref({
  nickname: '',
  emailOrQq: '',
  content: '',
  captchaAnswer: '',
  isSecret: false,
  isNotice: true,
  isMarkdown: true
})
const replyTarget = ref(null)
const editTarget = ref(null)
const submitting = ref(false)

/* 验证码 */
const captcha = ref({ question: '', result: null })
const loadCaptcha = async () => {
  try {
    const res = await generateCaptcha()
    captcha.value = res.data.data
  } catch {
    /* ignore */
  }
}

const load = async () => {
  loading.value = true
  try {
    const res = await getMessageTree(visitorStore.visitorId)
    messages.value = res.data.data ?? []
  } catch {
    messages.value = []
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!form.value.nickname.trim()) return ElMessage.warning('请输入昵称')
  if (!form.value.content.trim()) return ElMessage.warning('请输入内容')
  // 新留言需要验证码
  if (!editTarget.value) {
    const answer = parseInt(form.value.captchaAnswer, 10)
    if (isNaN(answer) || answer !== captcha.value.result) {
      ElMessage.warning('验证码错误，请重新计算')
      loadCaptcha()
      return
    }
  }
  submitting.value = true
  try {
    if (editTarget.value) {
      await editMessage({
        id: editTarget.value.id,
        content: form.value.content,
        visitorId: visitorStore.visitorId,
        isMarkdown: form.value.isMarkdown ? 1 : 0
      })
      ElMessage.success('修改成功')
    } else {
      await submitMessage({
        content: form.value.content,
        rootId: replyTarget.value?.rootId || replyTarget.value?.id || null,
        parentId: replyTarget.value?.id || null,
        parentNickname: replyTarget.value?.nickname || null,
        nickname: form.value.nickname,
        emailOrQq: form.value.emailOrQq,
        visitorId: visitorStore.visitorId,
        isSecret: form.value.isSecret ? 1 : 0,
        isNotice: form.value.isNotice ? 1 : 0,
        isMarkdown: form.value.isMarkdown ? 1 : 0
      })
      ElMessage.success('留言成功，审核通过后将展示')
    }
    visitorStore.nickname = form.value.nickname
    visitorStore.email = form.value.emailOrQq
    resetForm()
    loadCaptcha()
    await load()
  } catch {
    /* handled by interceptor */
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (msg) => {
  try {
    await ElMessageBox.confirm('确定删除这条留言?', '确认', {
      type: 'warning'
    })
    await deleteMessage(msg.id, visitorStore.visitorId)
    ElMessage.success('已删除')
    await load()
  } catch {
    /* cancelled */
  }
}

const startReply = (msg) => {
  editTarget.value = null
  replyTarget.value = msg
  form.value.content = ''
  document.querySelector('.msg-form')?.scrollIntoView({ behavior: 'smooth' })
}

const startEdit = (msg) => {
  replyTarget.value = null
  editTarget.value = msg
  form.value.content = msg.contentHtml?.replace(/<[^>]+>/g, '') || ''
  document.querySelector('.msg-form')?.scrollIntoView({ behavior: 'smooth' })
}

const resetForm = () => {
  replyTarget.value = null
  editTarget.value = null
  form.value.content = ''
  form.value.captchaAnswer = ''
  form.value.isSecret = false
  form.value.isNotice = true
  form.value.isMarkdown = true
}

const isMine = (msg) =>
  msg.visitorId && msg.visitorId === visitorStore.visitorId

const fmtDate = (d) => {
  if (!d) return ''
  const dt = new Date(d)
  return `${dt.getFullYear()}-${String(dt.getMonth() + 1).padStart(2, '0')}-${String(dt.getDate()).padStart(2, '0')}`
}

const totalCount = computed(() => {
  let n = 0
  const count = (list) => {
    list.forEach((m) => {
      n++
      if (m.children?.length) count(m.children)
    })
  }
  count(messages.value)
  return n
})

onMounted(() => {
  form.value.nickname = visitorStore.nickname || ''
  form.value.emailOrQq = visitorStore.email || ''
  load()
  loadCaptcha()
})
</script>

<template>
  <div class="message-page">
    <div class="message-inner">
      <!-- 留言表单 -->
      <div class="msg-form form-card">
        <h3 class="form-title">
          <i class="iconfont icon-liuyan" />
          {{ editTarget ? '修改留言' : '写留言' }}
        </h3>

        <div v-if="replyTarget" class="reply-tip">
          回复 <strong>{{ replyTarget.nickname }}</strong>
          <span class="cancel" @click="resetForm">&times;</span>
        </div>
        <div v-if="editTarget" class="reply-tip">
          修改留言
          <span class="cancel" @click="resetForm">&times;</span>
        </div>

        <textarea
          v-model="form.content"
          class="form-textarea"
          placeholder="写点什么..."
          rows="4"
        />
        <div class="form-row">
          <div class="input-with-icon">
            <i class="iconfont icon-user input-icon" />
            <input
              v-model="form.nickname"
              type="text"
              placeholder="昵称 *"
              class="form-input"
              :disabled="!!editTarget"
            />
          </div>
          <div class="input-with-icon">
            <i class="iconfont icon-youxiang input-icon" />
            <input
              v-model="form.emailOrQq"
              type="text"
              placeholder="邮箱/QQ号"
              class="form-input"
              :disabled="!!editTarget"
            />
          </div>
          <div v-if="!editTarget" class="input-with-icon captcha-wrap">
            <i class="iconfont icon-lock input-icon" />
            <input
              v-model="form.captchaAnswer"
              :placeholder="captcha.question || '验证码'"
              class="form-input"
            />
            <span class="captcha-refresh" @click="loadCaptcha" title="换一题"
              >↻</span
            >
          </div>
        </div>
        <div class="form-options">
          <label class="option-check">
            <input type="checkbox" v-model="form.isSecret" />
            悄悄话
          </label>
          <label class="option-check">
            <input type="checkbox" v-model="form.isNotice" />
            邮件提醒
          </label>
          <label class="option-check">
            <input type="checkbox" v-model="form.isMarkdown" />
            Markdown
          </label>
          <button
            class="btn-submit"
            :disabled="submitting"
            @click="handleSubmit"
          >
            {{ editTarget ? '修改' : '留言' }}
          </button>
        </div>
      </div>

      <!-- 留言数 -->
      <div class="msg-count">
        <span>共 {{ totalCount }} 条留言</span>
      </div>

      <!-- 留言列表 -->
      <div v-if="loading" class="placeholder">
        <div v-for="i in 3" :key="i" class="sk-line" />
      </div>

      <div v-else class="msg-list">
        <template v-for="msg in messages" :key="msg.id">
          <div class="msg-item-card">
            <div class="msg-head">
              <span class="msg-nick">{{ msg.nickname }}</span>
              <span v-if="msg.isAdmin" class="badge-admin">博主</span>
              <span class="msg-date">
                {{ fmtDate(msg.createTime) }}
                <span v-if="msg.isApproved === 0" class="msg-pending"
                  >未审核</span
                >
              </span>
            </div>
            <div class="msg-body" v-html="msg.contentHtml" />
            <div class="msg-actions">
              <span class="act" @click="startReply(msg)">回复</span>
              <template v-if="isMine(msg)">
                <span class="act" @click="startEdit(msg)">编辑</span>
                <span class="act del" @click="handleDelete(msg)">删除</span>
              </template>
            </div>

            <!-- 子留言 -->
            <div v-if="msg.children?.length" class="msg-children">
              <div
                v-for="child in msg.children"
                :key="child.id"
                class="msg-child"
              >
                <div class="msg-head">
                  <span class="msg-nick">{{ child.nickname }}</span>
                  <span v-if="child.isAdmin" class="badge-admin">博主</span>
                  <span v-if="child.parentNickname" class="reply-to">
                    <i class="iconfont icon-zhuanfa reply-icon" />
                    {{ child.parentNickname }}
                  </span>
                  <span class="msg-date">
                    {{ fmtDate(child.createTime) }}
                    <span v-if="child.isApproved === 0" class="msg-pending"
                      >未审核</span
                    >
                  </span>
                </div>
                <div class="msg-body" v-html="child.contentHtml" />
                <div class="msg-actions">
                  <span class="act" @click="startReply(child)">回复</span>
                  <template v-if="isMine(child)">
                    <span class="act" @click="startEdit(child)">编辑</span>
                    <span class="act del" @click="handleDelete(child)"
                      >删除</span
                    >
                  </template>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <p v-if="!loading && !messages.length" class="empty">
        还没有留言，来写第一条吧
      </p>
    </div>
  </div>
</template>

<style scoped>
.message-page {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}
.message-inner {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 表单卡片 */
.form-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #ebeef5;
  padding: 20px 24px;
}
.form-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 14px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
}
.form-title .iconfont {
  font-size: 17px;
}
.reply-tip {
  font-size: 13px;
  color: #606266;
  margin-bottom: 10px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.cancel {
  cursor: pointer;
  font-size: 16px;
  margin-left: auto;
  color: #909399;
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
.form-input {
  flex: 1;
  padding: 9px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  font-size: 13px;
  outline: none;
  background: #fff;
  font-family: inherit;
  box-sizing: border-box;
}
.form-input:focus {
  border-color: #303133;
}
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  font-size: 13px;
  outline: none;
  resize: vertical;
  font-family: inherit;
  background: #fff;
  box-sizing: border-box;
  margin-bottom: 8px;
}
.form-textarea:focus {
  border-color: #303133;
}
.form-options {
  display: flex;
  align-items: center;
  gap: 16px;
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
.btn-submit {
  padding: 8px 24px;
  font-size: 13px;
  border: none;
  background: #303133;
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
  font-family: inherit;
  margin-left: auto;
  transition: background 0.15s;
}
.btn-submit:hover {
  background: #000;
}
.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 留言数 */
.msg-count {
  font-size: 13px;
  color: #909399;
  padding: 0 4px;
}

/* 留言卡片 */
.msg-item-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #ebeef5;
  padding: 18px 22px;
  margin-bottom: 12px;
}
.msg-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}
.msg-nick {
  font-size: 14px;
  font-weight: 700;
  color: #303133;
}
.badge-admin {
  font-size: 10px;
  padding: 1px 6px;
  background: #303133;
  color: #fff;
  border-radius: 3px;
  font-weight: 600;
}
.reply-to {
  font-size: 12px;
  color: #909399;
}
.msg-date {
  font-size: 12px;
  color: #c0c4cc;
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 6px;
}
.msg-pending {
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
.msg-body {
  font-size: 14px;
  color: #444;
  line-height: 1.7;
}
.msg-body :deep(p) {
  margin: 0;
}
.msg-actions {
  margin-top: 6px;
  display: flex;
  gap: 12px;
}
.act {
  font-size: 12px;
  color: #909399;
  cursor: pointer;
  transition: color 0.15s;
}
.act:hover {
  color: #303133;
}
.act.del:hover {
  color: #c00;
}

/* 子留言 */
.msg-children {
  margin-top: 10px;
  padding-left: 16px;
  border-left: 2px solid #ebeef5;
}
.msg-child {
  padding: 10px 0;
}
.msg-child + .msg-child {
  border-top: 1px dashed #ebeef5;
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
.empty {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

@media (max-width: 600px) {
  .form-card,
  .msg-item-card {
    padding: 14px 16px;
  }
  .form-row {
    flex-direction: column;
  }
}
</style>
