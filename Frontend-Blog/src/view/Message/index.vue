<script setup>
import { ref, onMounted, computed } from 'vue'
import {
  getMessageTree,
  submitMessage,
  editMessage,
  deleteMessage
} from '@/api/message'
import { useVisitorStore } from '@/stores'

const visitorStore = useVisitorStore()

const messages = ref([])
const loading = ref(false)

/* 表单 */
const form = ref({ nickname: '', email: '', content: '' })
const replyTarget = ref(null)
const editTarget = ref(null)
const submitting = ref(false)

/* 加载 */
const load = async () => {
  loading.value = true
  try {
    const res = await getMessageTree()
    messages.value = res.data.data ?? []
  } catch {
    messages.value = []
  } finally {
    loading.value = false
  }
}

/* 提交 */
const handleSubmit = async () => {
  if (!form.value.nickname.trim()) return ElMessage.warning('请输入昵称')
  if (!form.value.content.trim()) return ElMessage.warning('请输入内容')
  submitting.value = true
  try {
    if (editTarget.value) {
      await editMessage({
        id: editTarget.value.id,
        contentHtml: `<p>${form.value.content}</p>`,
        visitorId: visitorStore.visitorId
      })
      ElMessage.success('修改成功')
    } else {
      await submitMessage({
        contentHtml: `<p>${form.value.content}</p>`,
        rootId: replyTarget.value?.rootId || replyTarget.value?.id || null,
        parentId: replyTarget.value?.id || null,
        nickname: form.value.nickname,
        email: form.value.email,
        visitorId: visitorStore.visitorId
      })
      ElMessage.success('留言成功')
    }
    /* 保存昵称/邮箱 */
    visitorStore.nickname = form.value.nickname
    visitorStore.email = form.value.email
    resetForm()
    await load()
  } catch {
    /* handled by interceptor */
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (msg) => {
  try {
    await ElMessageBox.confirm('确定删除这条留言?', '确认', { type: 'warning' })
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
  form.value.email = visitorStore.email || ''
  load()
})
</script>

<template>
  <div class="message-page">
    <header class="page-header">
      <i class="iconfont icon-liuyan" />
      <h1 class="page-title">留言板</h1>
      <p class="page-count">共 {{ totalCount }} 条留言</p>
    </header>

    <!-- 留言表单 -->
    <div class="msg-form">
      <div v-if="replyTarget" class="reply-tip">
        回复 <strong>{{ replyTarget.nickname }}</strong>
        <span class="cancel" @click="resetForm">&times;</span>
      </div>
      <div v-if="editTarget" class="reply-tip">
        修改留言
        <span class="cancel" @click="resetForm">&times;</span>
      </div>

      <div class="form-row">
        <input
          v-model="form.nickname"
          type="text"
          placeholder="昵称 *"
          class="form-input"
          :disabled="!!editTarget"
        />
        <input
          v-model="form.email"
          type="email"
          placeholder="邮箱（选填）"
          class="form-input"
          :disabled="!!editTarget"
        />
      </div>
      <textarea
        v-model="form.content"
        class="form-textarea"
        placeholder="写点什么..."
        rows="3"
      />
      <div class="form-actions">
        <button class="btn-submit" :disabled="submitting" @click="handleSubmit">
          {{ editTarget ? '修改' : '留言' }}
        </button>
      </div>
    </div>

    <!-- 留言列表 -->
    <div v-if="loading" class="placeholder">
      <div v-for="i in 3" :key="i" class="sk-line" />
    </div>

    <div v-else class="msg-list">
      <template v-for="msg in messages" :key="msg.id">
        <div class="msg-item root">
          <div class="msg-head">
            <span class="msg-nick">{{ msg.nickname }}</span>
            <span v-if="msg.isAdmin" class="badge-admin">博主</span>
            <span class="msg-date">{{ fmtDate(msg.createTime) }}</span>
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
              class="msg-item child"
            >
              <div class="msg-head">
                <span class="msg-nick">{{ child.nickname }}</span>
                <span v-if="child.isAdmin" class="badge-admin">博主</span>
                <span v-if="child.parentNickname" class="reply-to">
                  回复 {{ child.parentNickname }}
                </span>
                <span class="msg-date">{{ fmtDate(child.createTime) }}</span>
              </div>
              <div class="msg-body" v-html="child.contentHtml" />
              <div class="msg-actions">
                <span class="act" @click="startReply(child)">回复</span>
                <template v-if="isMine(child)">
                  <span class="act" @click="startEdit(child)">编辑</span>
                  <span class="act del" @click="handleDelete(child)">删除</span>
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
</template>

<style scoped>
.page-header {
  text-align: center;
  padding: 28px 0 20px;
  border-bottom: 2px solid #303133;
  margin-bottom: 24px;
}
.page-header .iconfont {
  font-size: 22px;
  color: #303133;
}
.page-title {
  font-family: var(--blog-serif);
  font-size: 26px;
  font-weight: 700;
  margin: 6px 0 4px;
  color: #303133;
  letter-spacing: 1px;
}
.page-count {
  font-size: 13px;
  color: #888;
  margin: 0;
}

/* 表单 */
.msg-form {
  border: 1px solid #e4e7ed;
  border-radius: 3px;
  padding: 16px;
  margin-bottom: 28px;
  background: #fff;
}
.reply-tip {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.cancel {
  cursor: pointer;
  font-size: 16px;
  margin-left: 4px;
  color: #909399;
}
.form-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
.form-input {
  flex: 1;
  padding: 8px 10px;
  border: 1px solid #e4e7ed;
  border-radius: 2px;
  font-size: 13px;
  outline: none;
  background: #f5f7fa;
}
.form-input:focus {
  border-color: #303133;
}
.form-textarea {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #e4e7ed;
  border-radius: 2px;
  font-size: 13px;
  outline: none;
  resize: vertical;
  font-family: inherit;
  background: #f5f7fa;
  box-sizing: border-box;
}
.form-textarea:focus {
  border-color: #303133;
}
.form-actions {
  margin-top: 8px;
  text-align: right;
}
.btn-submit {
  padding: 6px 20px;
  font-size: 13px;
  border: 1px solid #303133;
  background: #303133;
  color: #fff;
  border-radius: 2px;
  cursor: pointer;
  transition: opacity 0.15s;
}
.btn-submit:hover {
  opacity: 0.85;
}
.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 留言列表 */
.msg-list {
  padding-bottom: 40px;
}
.msg-item.root {
  padding: 16px 0;
  border-bottom: 1px solid #e4e7ed;
}
.msg-item.root:last-child {
  border-bottom: none;
}
.msg-item.child {
  padding: 10px 0 0 20px;
  border-left: 2px solid #ebeef5;
  margin-top: 6px;
}
.msg-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}
.msg-nick {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.badge-admin {
  font-size: 10px;
  padding: 1px 5px;
  border: 1px solid #303133;
  border-radius: 2px;
  color: #303133;
  letter-spacing: 0.5px;
}
.reply-to {
  font-size: 12px;
  color: #909399;
}
.msg-date {
  font-size: 12px;
  color: #bbb;
  margin-left: auto;
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
  margin-top: 4px;
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
  color: #c0392b;
}

.placeholder {
  padding: 30px 0;
}
.sk-line {
  height: 14px;
  background: #ebeef5;
  border-radius: 2px;
  margin-bottom: 12px;
  width: 60%;
}
.empty {
  text-align: center;
  color: #909399;
  padding: 60px 0;
  font-size: 14px;
}
</style>
