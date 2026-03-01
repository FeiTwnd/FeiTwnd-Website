<script setup>
import { ref, onMounted } from 'vue'
import { useCommentStore } from '@/stores'
import dayjs from 'dayjs'

const commentStore = useCommentStore()

const filterStatus = ref('')
const filterArticleId = ref('')
const page = ref(1)
const size = ref(15)
const selected = ref([])

const load = () => {
  commentStore.fetchList({
    page: page.value,
    pageSize: size.value,
    isApproved: filterStatus.value === '' ? undefined : filterStatus.value,
    articleId: filterArticleId.value || undefined
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

/* ---- 通过 ---- */
const approveOne = async (row) => {
  await commentStore.approve([row.id])
  ElMessage.success('已通过')
  load()
}

const batchApprove = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择评论')
  await commentStore.approve(selected.value.map((r) => r.id))
  ElMessage.success('批量通过成功')
  load()
}

/* ---- 删除 ---- */
const deleteOne = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该评论？', '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await commentStore.remove([row.id])
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '删除失败')
  }
}

const batchDelete = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择评论')
  try {
    await ElMessageBox.confirm(
      `确认删除选中的 ${selected.value.length} 条评论？`,
      '警告',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }
  try {
    await commentStore.remove(selected.value.map((r) => r.id))
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
const replyDialogVisible = ref(false)
const replyTarget = ref(null)
const replyContent = ref('')

const openReply = (row) => {
  replyTarget.value = row
  replyContent.value = ''
  replyDialogVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) return ElMessage.warning('回复内容不能为空')
  await commentStore.reply({
    articleId: replyTarget.value.articleId,
    parentId: replyTarget.value.id,
    rootId: replyTarget.value.rootId || replyTarget.value.id,
    parentNickname: replyTarget.value.nickname,
    content: replyContent.value,
    isMarkdown: 1
  })
  ElMessage.success('回复成功')
  replyDialogVisible.value = false
  load()
}

const fmtDate = (d) => (d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '-')

onMounted(load)
</script>

<template>
  <div class="comment-page">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-radio-group
          v-model="filterStatus"
          @change="
            () => {
              page = 1
              load()
            }
          "
        >
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button :label="0">待审核</el-radio-button>
          <el-radio-button :label="1">已通过</el-radio-button>
        </el-radio-group>
        <el-input
          v-model="filterArticleId"
          placeholder="文章 ID"
          clearable
          class="input-article-id"
          @keyup.enter="
            () => {
              page = 1
              load()
            }
          "
          @clear="
            () => {
              page = 1
              load()
            }
          "
        />
      </div>
      <div class="toolbar-right">
        <el-button plain :disabled="!selected.length" @click="batchApprove">
          批量通过
        </el-button>
        <el-button plain :disabled="!selected.length" @click="batchDelete">
          <span class="iconfont icon-delete" /> 批量删除
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-wrap" v-loading="commentStore.loading">
      <el-table
        :data="commentStore.list"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column label="评论内容" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-html="row.contentHtml || '-'" class="comment-content" />
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="110" />
        <el-table-column
          prop="parentNickname"
          label="回复对象"
          width="110"
          show-overflow-tooltip
        />
        <el-table-column
          label="所属文章"
          prop="articleTitle"
          min-width="140"
          show-overflow-tooltip
        />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <span>{{ row.isApproved ? '已通过' : '待审核' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="150" align="center">
          <template #default="{ row }">{{ fmtDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
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
        :total="commentStore.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="评论详情"
      width="580px"
      :close-on-click-modal="false"
    >
      <el-descriptions v-if="detailRow" :column="2" border size="small">
        <el-descriptions-item label="所属文章">{{
          detailRow.articleTitle ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{
          detailRow.isAdminReply ? '管理员' : '访客'
        }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{
          detailRow.nickname ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="邮箱/QQ">{{
          detailRow.emailOrQq ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="回复对象">{{
          detailRow.parentNickname ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{
          detailRow.isApproved ? '已通过' : '待审核'
        }}</el-descriptions-item>
        <el-descriptions-item label="地区">{{
          detailRow.location ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{
          fmtDate(detailRow.createTime)
        }}</el-descriptions-item>
        <el-descriptions-item label="操作系统">{{
          detailRow.userAgentOs ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="浏览器">{{
          detailRow.userAgentBrowser ?? '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="评论内容" :span="2">
          <div v-html="detailRow.contentHtml || detailRow.content || '-'" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 回复弹窗 -->
    <el-dialog v-model="replyDialogVisible" title="回复评论" width="500px">
      <div v-if="replyTarget" class="reply-original">
        <span class="reply-author">{{ replyTarget.nickname }}：</span>
        {{ replyTarget.content }}
      </div>
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="4"
        placeholder="输入回复内容"
        style="margin-top: 12px"
      />
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">发送回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.comment-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  align-items: center;
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

.input-article-id {
  width: 110px;
}

.table-wrap {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
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

.reply-original {
  background: #f5f7fa;
  border-left: 3px solid #d3d6db;
  padding: 10px 14px;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.reply-author {
  font-weight: 600;
  color: #303133;
}
</style>
