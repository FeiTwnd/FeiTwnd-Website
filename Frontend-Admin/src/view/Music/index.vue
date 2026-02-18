<script setup>
import { ref, onMounted } from 'vue'
import { useMusicStore } from '@/stores'
import { uploadFile } from '@/api/settings'

const musicStore = useMusicStore()

/* ---- 搜索 + 分页 ---- */
const searchTitle = ref('')
const page = ref(1)
const size = ref(15)
const selected = ref([])

const load = () => {
  musicStore.fetchList({
    page: page.value,
    pageSize: size.value,
    title: searchTitle.value || undefined
  })
}

const handleSearch = () => {
  page.value = 1
  load()
}

const handleReset = () => {
  searchTitle.value = ''
  handleSearch()
}

const handlePageChange = (p) => {
  page.value = p
  load()
}

const handleSelectionChange = (rows) => {
  selected.value = rows
}

/* ---- 弹窗 ---- */
const dialogVisible = ref(false)
const isEditing = ref(false)
const uploadingCover = ref(false)
const uploadingAudio = ref(false)
const uploadingLyric = ref(false)

const form = ref({
  id: null,
  title: '',
  artist: '',
  duration: null,
  coverImage: '',
  musicUrl: '',
  lyricUrl: '',
  hasLyric: 0,
  lyricType: '',
  sort: null,
  isVisible: 1
})
const saving = ref(false)

const openDialog = (row = null) => {
  isEditing.value = !!row
  if (row) {
    form.value = {
      id: row.id,
      title: row.title,
      artist: row.artist ?? '',
      duration: row.duration ?? null,
      coverImage: row.coverImage ?? '',
      musicUrl: row.musicUrl ?? '',
      lyricUrl: row.lyricUrl ?? '',
      hasLyric: row.hasLyric ?? 0,
      lyricType: row.lyricType ?? '',
      sort: row.sort ?? null,
      isVisible: row.isVisible ?? 1
    }
  } else {
    form.value = {
      id: null, title: '', artist: '', duration: null,
      coverImage: '', musicUrl: '', lyricUrl: '',
      hasLyric: 0, lyricType: '', sort: null, isVisible: 1
    }
  }
  dialogVisible.value = true
}

/** 封面上传 */
const handleCoverUpload = async (options) => {
  uploadingCover.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    form.value.coverImage = res.data
    ElMessage.success('封面上传成功')
  } finally { uploadingCover.value = false }
}

/** 音频上传 */
const handleAudioUpload = async (options) => {
  uploadingAudio.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    form.value.musicUrl = res.data
    form.value.hasLyric = form.value.musicUrl ? form.value.hasLyric : 0
    ElMessage.success('音频上传成功')
  } finally { uploadingAudio.value = false }
}

/** 歌词文件上传 */
const handleLyricUpload = async (options) => {
  uploadingLyric.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    form.value.lyricUrl = res.data
    form.value.hasLyric = 1
    ElMessage.success('歌词上传成功')
  } finally { uploadingLyric.value = false }
}

const handleSave = async () => {
  if (!form.value.title.trim()) return ElMessage.warning('歌曲名称不能为空')
  if (!form.value.musicUrl.trim()) return ElMessage.warning('音频文件不能为空')
  saving.value = true
  try {
    await musicStore.saveMusic({ ...form.value })
    ElMessage.success(isEditing.value ? '修改成功' : '创建成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

/* ---- 删除 ---- */
const deleteOne = async (row) => {
  await ElMessageBox.confirm(`确认删除「${row.title}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await musicStore.remove([row.id])
  ElMessage.success('删除成功')
  load()
}

const batchDelete = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择音乐')
  await ElMessageBox.confirm(
    `确认删除选中的 ${selected.value.length} 首音乐？`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  )
  await musicStore.remove(selected.value.map((r) => r.id))
  ElMessage.success('批量删除成功')
  load()
}

onMounted(load)
</script>

<template>
  <div class="music-page">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchTitle"
          placeholder="搜索歌曲名"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <!-- ICON: icon-search -->
            <span class="iconfont icon-search" />
          </template>
        </el-input>
        <el-button @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
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
          新增音乐
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div v-loading="musicStore.loading" class="table-wrap">
      <el-table
        :data="musicStore.list"
        border
        stripe
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column label="封面" width="68" align="center">
          <template #default="{ row }">
            <img
              v-if="row.coverImage"
              :src="row.coverImage"
              :alt="row.title"
              class="cover-thumb"
            />
            <span v-else class="cover-empty">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="歌曲名" min-width="160" />
        <el-table-column prop="artist" label="艺术家" width="130" />
        <el-table-column label="时长" width="80" align="center">
          <template #default="{ row }">
            {{ row.duration ? Math.floor(row.duration / 60) + ':' + String(row.duration % 60).padStart(2,'0') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="音频" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.musicUrl" type="success" size="small">已上传</el-tag>
            <el-tag v-else type="info" size="small">无</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="歌词" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.hasLyric" type="success" size="small">{{ row.lyricType || 'lrc' }}</el-tag>
            <el-tag v-else type="info" size="small">无</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <div class="row-actions">
              <el-button link size="small" @click="openDialog(row)"
                >编辑</el-button
              >
              <el-divider direction="vertical" />
              <el-button link size="small" type="danger" @click="deleteOne(row)"
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
        :page-size="size"
        :total="musicStore.total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑音乐' : '新增音乐'"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="90px">
        <el-form-item label="歌曲名" required>
          <el-input v-model="form.title" placeholder="歌曲名称" clearable />
        </el-form-item>
        <el-form-item label="艺术家">
          <el-input v-model="form.artist" placeholder="艺术家 / 歌手" clearable />
        </el-form-item>
        <el-form-item label="时长(秒)">
          <el-input-number v-model="form.duration" :min="0" :precision="0" controls-position="right" style="width:140px" placeholder="单位秒" />
        </el-form-item>
        <el-form-item label="封面">
          <div class="upload-row">
            <el-upload :show-file-list="false" :http-request="handleCoverUpload" accept="image/*">
              <el-button size="small" :loading="uploadingCover"><!-- ICON --><span class="iconfont icon-upload" />上传封面</el-button>
            </el-upload>
            <el-input v-model="form.coverImage" placeholder="封面图片 URL" clearable class="upload-url-input" />
          </div>
          <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
        </el-form-item>
        <el-form-item label="音频" required>
          <div class="upload-row">
            <el-upload :show-file-list="false" :http-request="handleAudioUpload" accept="audio/*">
              <el-button size="small" :loading="uploadingAudio"><!-- ICON --><span class="iconfont icon-upload" />上传音频</el-button>
            </el-upload>
            <el-input v-model="form.musicUrl" placeholder="音频文件 URL" clearable class="upload-url-input" />
          </div>
        </el-form-item>
        <el-form-item label="歌词文件">
          <div class="upload-row">
            <el-upload :show-file-list="false" :http-request="handleLyricUpload" accept=".lrc,.txt,.json">
              <el-button size="small" :loading="uploadingLyric"><!-- ICON --><span class="iconfont icon-upload" />上传歌词</el-button>
            </el-upload>
            <el-input v-model="form.lyricUrl" placeholder="歌词文件 URL" clearable class="upload-url-input" />
          </div>
        </el-form-item>
        <el-form-item label="歌词类型">
          <el-select v-model="form.lyricType" placeholder="选择歌词类型" clearable style="width:140px">
            <el-option label="LRC" value="lrc" />
            <el-option label="JSON" value="json" />
            <el-option label="TXT" value="txt" />
          </el-select>
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
.music-page {
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

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.toolbar-left .iconfont,
.toolbar-right .iconfont {
  font-size: 14px;
  margin-right: 4px;
}

.search-input {
  width: 200px;
}

.table-wrap {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.cover-thumb {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  object-fit: cover;
  vertical-align: middle;
}

.cover-empty {
  color: #c0c4cc;
  font-size: 13px;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.upload-row .iconfont {
  font-size: 13px;
  margin-right: 3px;
}

.upload-url-input {
  flex: 1;
}

.cover-preview {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  margin-top: 6px;
  display: block;
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
</style>
