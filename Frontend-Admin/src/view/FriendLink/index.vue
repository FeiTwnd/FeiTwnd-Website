<script setup>
import { ref, onMounted } from 'vue'
import { useFriendLinkStore } from '@/stores'
import { uploadFile } from '@/api/settings'

const friendLinkStore = useFriendLinkStore()

/* ---- 弹窗 ---- */
const dialogVisible = ref(false)
const isEditing = ref(false)
const uploading = ref(false)

const form = ref({ id: null, name: '', url: '', avatarUrl: '', description: '', sort: null, isVisible: 1 })

const openDialog = (row = null) => {
  isEditing.value = !!row
  form.value = row
    ? {
        id: row.id,
        name: row.name,
        url: row.url,
        avatarUrl: row.avatarUrl ?? '',
        description: row.description ?? '',
        sort: row.sort ?? null,
        isVisible: row.isVisible ?? 1
      }
    : { id: null, name: '', url: '', avatarUrl: '', description: '', sort: null, isVisible: 1 }
  dialogVisible.value = true
}

/* ---- 保存 ---- */
const saving = ref(false)

/** 头像上传 */
const handleAvatarUpload = async (options) => {
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    form.value.avatarUrl = res.data
    ElMessage.success('图片上传成功')
  } finally {
    uploading.value = false
  }
}

const handleSave = async () => {
  if (!form.value.name.trim()) return ElMessage.warning('链接名称不能为空')
  if (!form.value.url.trim()) return ElMessage.warning('链接地址不能为空')
  saving.value = true
  try {
    await friendLinkStore.saveFriendLink({ ...form.value })
    ElMessage.success(isEditing.value ? '修改成功' : '创建成功')
    dialogVisible.value = false
  } finally {
    saving.value = false
  }
}

/* ---- 批量选择 ---- */
const selected = ref([])
const handleSelectionChange = (rows) => {
  selected.value = rows
}

/* ---- 删除 ---- */
const deleteOne = async (row) => {
  await ElMessageBox.confirm(`确认删除友链「${row.name}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await friendLinkStore.removeFriendLinks([row.id])
  ElMessage.success('删除成功')
}

const batchDelete = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择友链')
  await ElMessageBox.confirm(
    `确认删除选中的 ${selected.value.length} 条友链？`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  )
  await friendLinkStore.removeFriendLinks(selected.value.map((r) => r.id))
  ElMessage.success('批量删除成功')
}

onMounted(() => friendLinkStore.fetchList())
</script>

<template>
  <div class="friendlink-page">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <span class="page-heading">
          <!-- ICON: icon-link -->
          <span class="iconfont icon-link" />
          友情链接管理
        </span>
      </div>
      <div class="toolbar-right">
        <el-button
          type="danger"
          plain
          :disabled="!selected.length"
          @click="batchDelete"
        >
          <!-- ICON: icon-delete -->
          <span class="iconfont icon-delete" />
          批量删除
        </el-button>
        <el-button type="primary" @click="openDialog()">
          <!-- ICON: icon-add -->
          <span class="iconfont icon-add" />
          新增友链
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div v-loading="friendLinkStore.loading" class="table-wrap">
      <el-table
        :data="friendLinkStore.list"
        border
        stripe
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column label="Logo" width="72" align="center">
          <template #default="{ row }">
            <img
              v-if="row.avatarUrl"
              :src="row.avatarUrl"
              :alt="row.name"
              class="link-logo"
            />
            <span v-else class="logo-empty">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="140" />
        <el-table-column label="链接地址" min-width="240">
          <template #default="{ row }">
            <a :href="row.url" target="_blank" rel="noopener" class="link-href">
              {{ row.url }}
            </a>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="描述"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <div class="row-actions">
              <el-button link size="small" @click="openDialog(row)">
                <!-- ICON: icon-edit -->
                编辑
              </el-button>
              <el-divider direction="vertical" />
              <el-button
                link
                size="small"
                type="danger"
                @click="deleteOne(row)"
              >
                <!-- ICON: icon-delete -->
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑友链' : '新增友链'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="站点名称" clearable />
        </el-form-item>
        <el-form-item label="链接" required>
          <el-input v-model="form.url" placeholder="https://..." clearable />
        </el-form-item>
        <el-form-item label="头像">
          <div class="upload-row">
            <el-upload
              :show-file-list="false"
              :http-request="handleAvatarUpload"
              accept="image/*"
            >
              <el-button size="small" :loading="uploading"
                ><!-- ICON: icon-upload --><span class="iconfont icon-upload" />点击上传</el-button
              >
            </el-upload>
            <el-input
              v-model="form.avatarUrl"
              placeholder="头像图片 URL（可手动输入）"
              clearable
              class="upload-url-input"
            />
          </div>
          <div v-if="form.avatarUrl" class="upload-preview">
            <img :src="form.avatarUrl" class="link-logo" />
          </div>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="一句话描述（可选）"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :precision="0" controls-position="right" style="width:120px" />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch v-model="form.isVisible" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave"
          >确认</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.friendlink-page {
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
  gap: 12px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-right .iconfont,
.toolbar-left .iconfont {
  font-size: 14px;
  margin-right: 4px;
}

.page-heading {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.page-heading .iconfont {
  font-size: 18px;
  color: #606266;
}

.table-wrap {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.link-logo {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  object-fit: cover;
  vertical-align: middle;
}

.logo-empty {
  color: #c0c4cc;
  font-size: 13px;
}

.link-href {
  color: #606266;
  font-size: 13px;
  text-decoration: none;
  transition: color 0.15s;
}

.link-href:hover {
  color: #000000;
  text-decoration: underline;
}

.row-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-form {
  padding: 4px 0;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.upload-row .iconfont {
  font-size: 13px;
  margin-right: 4px;
}

.upload-url-input {
  flex: 1;
}

.upload-preview {
  margin-top: 6px;
}
</style>
