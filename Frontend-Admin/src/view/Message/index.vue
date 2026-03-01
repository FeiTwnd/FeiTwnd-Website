<script setup>
import { ref, onMounted } from 'vue'
import { useMessageStore } from '@/stores'
import dayjs from 'dayjs'

const messageStore = useMessageStore()

const filterStatus = ref('')
const page = ref(1)
const size = ref(15)
const selected = ref([])

/** 拉取列表 */
const load = () => {
  messageStore.fetchList({
    page: page.value,
    pageSize: size.value,
    isApproved: filterStatus.value === '' ? undefined : filterStatus.value
  })
}

const handleSelectionChange = (rows) => {
  selected.value = rows
}

const handlePageChange = (p) => {
  page.value = p
  load()
}

const handleSizeChange = (s) => {
  size.value = s
  page.value = 1
  load()
}

const handleFilterChange = () => {
  page.value = 1
  load()
}

/* ---- 审核 ---- */
const approveOne = async (row) => {
  await messageStore.approve([row.id])
  ElMessage.success('审核通过')
  load()
}

const batchApprove = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择留言')
  await messageStore.approve(selected.value.map((r) => r.id))
  ElMessage.success('批量审核通过')
  load()
}

/* ---- 删除 ---- */
const deleteOne = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该条留言？', '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await messageStore.remove([row.id])
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '删除失败')
  }
}

const batchDelete = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择留言')
  try {
    await ElMessageBox.confirm(
      `确认删除选中的 ${selected.value.length} 条留言？`,
      '警告',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }
  try {
    await messageStore.remove(selected.value.map((r) => r.id))
    ElMessage.success('批量删除成功')
    load()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '批量删除失败')
  }
}

/* ---- 详情弹窗 ---- */
const detailVisible = ref(false)
const detailRow = ref(null)

const openDetail = (row) => {
  detailRow.value = row
  detailVisible.value = true
}

/* ---- 回复弹窗 ---- */
const replyVisible = ref(false)
/** @type {import('vue').Ref<Object|null>} */
const replyTarget = ref(null)
const replyContent = ref('')

const openReply = (row) => {
  replyTarget.value = row
  replyContent.value = ''
  replyVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) return ElMessage.warning('回复内容不能为空')
  const t = replyTarget.value
  await messageStore.reply({
    parentId: t.id,
    rootId: t.rootId ?? t.id,
    parentNickname: t.nickname ?? '',
    content: replyContent.value,
    isMarkdown: 1
  })
  ElMessage.success('回复成功')
  replyVisible.value = false
  load()
}

/** 格式化时间 */
const fmtDate = (d) => (d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '-')

onMounted(load)
</script>

<template>
  <div class="message-page">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-radio-group v-model="filterStatus" @change="handleFilterChange">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button :label="0">待审核</el-radio-button>
          <el-radio-button :label="1">已通过</el-radio-button>
        </el-radio-group>
      </div>
      <div class="toolbar-right">
        <el-button plain :disabled="!selected.length" @click="batchApprove">
          批量通过
        </el-button>
        <el-button plain :disabled="!selected.length" @click="batchDelete">
          <span class="iconfont icon-delete" />
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div v-loading="messageStore.loading" class="table-wrap">
      <el-table
        :data="messageStore.list"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column label="留言内容" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="msg-content" v-html="row.contentHtml || row.content" />
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="110" />
        <el-table-column
          prop="parentNickname"
          label="回复对象"
          width="110"
          show-overflow-tooltip
        />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <span>{{ row.isApproved ? '已通过' : '待审核' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="158" align="center">
          <template #default="{ row }">{{ fmtDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="210" align="center" fixed="right">
          <template #default="{ row }">
            <div class="row-actions">
              <el-button link size="small" @click="openDetail(row)"
                >查看</el-button
              >
              <el-divider direction="vertical" />
              <el-button
                v-if="!row.isApproved"
                link
                size="small"
                @click="approveOne(row)"
                >通过</el-button
              >
              <el-button link size="small" @click="openReply(row)"
                >回复</el-button
              >
              <el-divider direction="vertical" />
              <el-button link size="small" @click="deleteOne(row)"
                >删除</el-button
              >
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :page-sizes="[10, 15, 20, 50]"
        :total="messageStore.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="留言详情"
      width="540px"
      :close-on-click-modal="false"
    >
      <el-descriptions v-if="detailRow" :column="2" border size="small">
        <el-descriptions-item label="昵称">{{
          detailRow.nickname ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="邮箱/QQ">{{
          detailRow.emailOrQq ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{
          detailRow.isApproved ? '已通过' : '待审核'
        }}</el-descriptions-item>
        <el-descriptions-item label="地区">{{
          detailRow.location ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="操作系统">{{
          detailRow.userAgentOs ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="浏览器">{{
          detailRow.userAgentBrowser ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="时间" :span="2">{{
          fmtDate(detailRow.createTime)
        }}</el-descriptions-item>
        <el-descriptions-item label="留言内容" :span="2">
          <div v-html="detailRow.contentHtml || detailRow.content || '-'" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 回复弹窗 -->
    <el-dialog
      v-model="replyVisible"
      title="回复留言"
      width="500px"
      :close-on-click-modal="false"
    >
      <div v-if="replyTarget" class="reply-quote">
        <span class="reply-author">{{ replyTarget.nickname }}：</span>
        {{ replyTarget.content }}
      </div>
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="4"
        placeholder="输入回复内容…"
        style="margin-top: 14px"
      />
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">发送回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.message-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.toolbar-right .iconfont {
  font-size: 14px;
  margin-right: 4px;
}

.table-wrap {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.msg-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
}

.row-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
}

/* 引用原留言 */
.reply-quote {
  background: #f5f7fa;
  border-left: 3px solid #d3d6db;
  border-radius: 4px;
  padding: 10px 14px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.reply-author {
  font-weight: 600;
  color: #303133;
}
</style>
