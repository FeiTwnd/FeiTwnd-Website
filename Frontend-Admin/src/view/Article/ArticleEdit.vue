<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router'
import { useArticleStore } from '@/stores'
import { uploadFile } from '@/api/settings'
import { importMd } from '@/api/article'
import { MdEditor } from 'md-editor-v3'
import EmojiPicker from '@/components/EmojiPicker.vue'
import 'md-editor-v3/lib/style.css'

const route = useRoute()
const router = useRouter()
const articleStore = useArticleStore()

const isEdit = computed(() => !!route.params.id)

const form = ref({
  id: null,
  title: '',
  slug: '',
  summary: '',
  coverImage: '',
  categoryId: null,
  tagIds: [],
  contentMarkdown: '',
  contentHtml: '',
  isPublished: 0
})

/* ---- 同步编辑器渲染后的 HTML ---- */
const onHtmlChanged = (html) => {
  form.value.contentHtml = html
}

const saving = ref(false)
const uploadingCover = ref(false)
const importing = ref(false)
const importFileInput = ref(null)
const editorPanelRef = ref(null)

/* ---- 图片上传（md-editor-v3 回调格式） ---- */
const onUploadImg = async (files, callback) => {
  try {
    const urls = await Promise.all(
      files.map(async (file) => {
        const fd = new FormData()
        fd.append('file', file)
        const res = await uploadFile(fd)
        return res.data
      })
    )
    callback(urls)
    ElMessage.success('图片上传成功')
  } catch {
    ElMessage.error('图片上传失败')
    callback([])
  }
}

/* ---- 封面上传 ---- */
const handleCoverUpload = async (options) => {
  uploadingCover.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    form.value.coverImage = res.data
    ElMessage.success('封面上传成功')
  } finally {
    uploadingCover.value = false
  }
}

/* ---- MD 文件导入 ---- */
const triggerImport = () => {
  importFileInput.value?.click()
}

const handleImportMd = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return

  // 检查文件类型
  if (!file.name.endsWith('.md') && !file.name.endsWith('.markdown')) {
    ElMessage.warning('仅支持 .md 或 .markdown 文件')
    return
  }

  importing.value = true
  try {
    const res = await importMd(file)
    const data = res.data
    // 自动填入表单
    if (data.title && !form.value.title) form.value.title = data.title
    if (data.slug && !form.value.slug) form.value.slug = data.slug
    if (data.summary && !form.value.summary) form.value.summary = data.summary
    if (data.coverImage && !form.value.coverImage) form.value.coverImage = data.coverImage
    if (data.contentMarkdown) form.value.contentMarkdown = data.contentMarkdown

    // 分类和标签需要手动映射
    if (data.category) {
      const matched = articleStore.categories.find(
        c => c.name === data.category || c.slug === data.category
      )
      if (matched) form.value.categoryId = matched.id
    }
    if (data.tags?.length) {
      const matchedIds = data.tags
        .map(tagName => articleStore.tags.find(t => t.name === tagName))
        .filter(Boolean)
        .map(t => t.id)
      if (matchedIds.length) form.value.tagIds = matchedIds
    }

    ElMessage.success(`已导入：${data.title}`)
  } catch {
    // 错误已由拦截器统一处理
  } finally {
    importing.value = false
    // 清空 input，允许重复导入同一个文件
    if (importFileInput.value) importFileInput.value.value = ''
  }
}

/* ---- 标题失焦自动生成 slug ---- */
const autoSlug = () => {
  if (form.value.title && !form.value.slug) {
    form.value.slug =
      form.value.title
        .toLowerCase()
        .replace(/[\u4e00-\u9fff]+/g, '-')
        .replace(/[^a-z0-9-]/g, '-')
        .replace(/-+/g, '-')
        .replace(/^-|-$/g, '')
        .substring(0, 50) || `article-${Date.now()}`
  }
}

/* ---- 快捷插入 emoji ---- */
const insertEditorEmoji = (char) => {
  const textarea = editorPanelRef.value?.querySelector('textarea')
  if (!textarea) {
    form.value.contentMarkdown += char
    return
  }

  const start = textarea.selectionStart ?? form.value.contentMarkdown.length
  const end = textarea.selectionEnd ?? form.value.contentMarkdown.length
  const val = form.value.contentMarkdown
  form.value.contentMarkdown = val.slice(0, start) + char + val.slice(end)

  nextTick(() => {
    const pos = start + char.length
    textarea.setSelectionRange(pos, pos)
    textarea.focus()
  })
}

/* ---- 保存 / 发布 ---- */
const isSaved = ref(false)
const handleSave = async (
  isPublished,
  { redirectAfterSave = isPublished === 1 } = {}
) => {
  if (!form.value.title.trim()) return ElMessage.warning('请输入文章标题')
  if (!form.value.slug.trim())
    return ElMessage.warning('请输入 URL 标识 (Slug)')
  if (!form.value.contentMarkdown.trim())
    return ElMessage.warning('请输入文章内容')
  if (!form.value.categoryId) return ElMessage.warning('请选择文章分类')
  if (saving.value) return
  saving.value = true
  try {
    form.value.isPublished = isPublished
    await articleStore.saveArticle({ ...form.value })
    isSaved.value = isPublished === 1 && redirectAfterSave
    takeSnapshot()
    ElMessage.success(isPublished ? '发布成功' : '保存草稿成功')
    if (redirectAfterSave) {
      router.push('/article/list')
    }
  } catch (error) {
    if (error?.response?.status === 401) {
      ElMessage.warning('登录已过期，请重新登录')
      const redirect = route.fullPath || '/article/edit'
      router.push({ path: '/login', query: { redirect } })
    }
    throw error
  } finally {
    saving.value = false
  }
}

/* ---- Ctrl/Cmd + S 保存草稿 ---- */
const onKeydownSave = (e) => {
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 's') {
    e.preventDefault()
    handleSave(0, { redirectAfterSave: false })
  }
}

/* ---- 内容变更检测 & 离开提示 ---- */
const initialSnapshot = ref('')
const hasUnsavedChanges = () => {
  if (isSaved.value) return false
  const current = JSON.stringify({
    title: form.value.title,
    slug: form.value.slug,
    summary: form.value.summary,
    coverImage: form.value.coverImage,
    categoryId: form.value.categoryId,
    tagIds: form.value.tagIds,
    contentMarkdown: form.value.contentMarkdown
  })
  return current !== initialSnapshot.value
}

const takeSnapshot = () => {
  initialSnapshot.value = JSON.stringify({
    title: form.value.title,
    slug: form.value.slug,
    summary: form.value.summary,
    coverImage: form.value.coverImage,
    categoryId: form.value.categoryId,
    tagIds: form.value.tagIds,
    contentMarkdown: form.value.contentMarkdown
  })
}

onBeforeRouteLeave(async () => {
  if (!hasUnsavedChanges()) return true
  try {
    await ElMessageBox.confirm('你有未保存的内容，是否保存为草稿？', '提示', {
      confirmButtonText: '保存草稿',
      cancelButtonText: '不保存',
      distinguishCancelAndClose: true,
      type: 'warning'
    })
    // 用户点击保存草稿
    await handleSave(0, { redirectAfterSave: false })
    return true
  } catch (action) {
    if (action === 'cancel') {
      // 用户点击不保存，直接离开
      return true
    }
    // 用户点击关闭按钮，留在当前页面
    return false
  }
})

onMounted(async () => {
  window.addEventListener('keydown', onKeydownSave)

  takeSnapshot()

  await Promise.all([articleStore.fetchCategories(), articleStore.fetchTags()])
  if (isEdit.value) {
    const res = await articleStore.fetchDetail(route.params.id)
    if (res) {
      Object.assign(form.value, {
        id: res.id,
        title: res.title ?? '',
        slug: res.slug ?? '',
        summary: res.summary ?? '',
        coverImage: res.coverImage ?? '',
        categoryId: res.categoryId,
        tagIds: res.tagIds ?? [],
        contentMarkdown: res.contentMarkdown || res.contentHtml || '',
        isPublished: res.isPublished ?? 0
      })
    }
  }
  takeSnapshot()
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydownSave)
})
</script>

<template>
  <div class="article-edit">
    <!-- 顶部操作栏 -->
    <div class="edit-topbar">
      <span class="edit-title">{{ isEdit ? '编辑文章' : '新建文章' }}</span>

      <div class="edit-actions">
        <el-button size="small" @click="router.push('/article/list')"
          >返回</el-button
        >
        <input
          ref="importFileInput"
          type="file"
          accept=".md,.markdown"
          style="display: none"
          @change="handleImportMd"
        />
        <el-button
          size="small"
          :loading="importing"
          @click="triggerImport"
          >导入 MD</el-button
        >
        <el-button size="small" :loading="saving" @click="handleSave(0)"
          >保存草稿</el-button
        >
        <el-button
          size="small"
          type="primary"
          :loading="saving"
          @click="handleSave(1)"
          >发布</el-button
        >
      </div>
    </div>

    <!-- 标题行 -->
    <div class="title-row">
      <el-input
        v-model="form.title"
        placeholder="请输入文章标题…"
        class="title-input"
        size="large"
        @blur="autoSlug"
      />
    </div>

    <!-- 主体区域 -->
    <div class="edit-body">
      <!-- Markdown 编辑器 -->
      <div ref="editorPanelRef" class="editor-panel">
        <div class="editor-toolbar-emoji">
          <EmojiPicker @select="insertEditorEmoji" />
        </div>
        <MdEditor
          v-model="form.contentMarkdown"
          preview-theme="github"
          :toolbars-exclude="['mermaid', 'katex', 'github']"
          class="md-editor-fill"
          @on-upload-img="onUploadImg"
          @on-html-changed="onHtmlChanged"
        />
      </div>

      <!-- 元数据侧边栏 -->
      <aside class="edit-aside">
        <div class="aside-section">
          <div class="aside-label">Slug <span class="req">*</span></div>
          <el-input
            v-model="form.slug"
            placeholder="URL 路径标识"
            clearable
            size="small"
          />
        </div>

        <div class="aside-section">
          <div class="aside-label">摘要</div>
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="文章摘要（选填）"
            size="small"
          />
        </div>

        <div class="aside-section">
          <div class="aside-label">分类 <span class="req">*</span></div>
          <el-select
            v-model="form.categoryId"
            placeholder="选择分类"
            style="width: 100%"
            size="small"
          >
            <el-option
              v-for="c in articleStore.categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </div>

        <div class="aside-section">
          <div class="aside-label">标签</div>
          <el-select
            v-model="form.tagIds"
            multiple
            placeholder="选择标签"
            style="width: 100%"
            size="small"
          >
            <el-option
              v-for="t in articleStore.tags"
              :key="t.id"
              :label="t.name"
              :value="t.id"
            />
          </el-select>
        </div>

        <div class="aside-section">
          <div class="aside-label">封面图</div>
          <el-upload
            :show-file-list="false"
            :http-request="handleCoverUpload"
            accept="image/*"
            class="cover-uploader"
          >
            <img
              v-if="form.coverImage"
              :src="form.coverImage"
              class="cover-preview"
            />
            <div v-else class="cover-placeholder">
              <span class="iconfont icon-image-fill" />
              <span>点击上传</span>
            </div>
          </el-upload>
          <el-input
            v-model="form.coverImage"
            placeholder="或直接输入图片 URL"
            clearable
            size="small"
            style="margin-top: 6px"
          />
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.article-edit {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f0f2f5;
}

/* ---- 顶栏 ---- */
.edit-topbar {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 8px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}
.edit-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.edit-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* ---- 标题行 ---- */
.title-row {
  background: #fff;
  padding: 10px 16px;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}
.title-input :deep(.el-input__inner) {
  font-size: 20px;
  font-weight: 600;
  border: none;
  box-shadow: none;
  padding-left: 0;
}

/* ---- 主体 ---- */
.edit-body {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

/* 编辑器面板 */
.editor-panel {
  position: relative;
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-toolbar-emoji {
  position: absolute;
  left: 666px;
  top: 2px;
  z-index: 20;
}
.editor-toolbar-emoji :deep(.emoji-trigger) {
  width: 30px;
  height: 30px;
}
.editor-toolbar-emoji :deep(.emoji-panel) {
  top: 36px;
  bottom: auto;
  z-index: 3000;
}

/* md-editor-v3 填满面板高度 */
.md-editor-fill {
  flex: 1;
  height: 100% !important;
}

/* 去除编辑器默认边框，融入整体风格 */
.editor-panel :deep(.md-editor) {
  border: none;
  border-radius: 0;
  height: 100%;
}
.editor-panel :deep(.md-editor-toolbar-wrapper) {
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}
.editor-panel :deep(.md-editor-content) {
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
}

/* 侧边栏 */
.edit-aside {
  width: 230px;
  flex-shrink: 0;
  overflow-y: auto;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  padding: 14px 12px;
}
.aside-section {
  margin-bottom: 18px;
}
.aside-label {
  font-size: 12px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 5px;
}
.req {
  color: #f56c6c;
}

/* 封面上传 */
.cover-uploader {
  width: 100%;
}
.cover-uploader :deep(.el-upload) {
  width: 100%;
  display: block;
}
.cover-preview {
  width: 100%;
  border-radius: 6px;
  object-fit: cover;
  cursor: pointer;
  max-height: 110px;
}
.cover-placeholder {
  width: 100%;
  box-sizing: border-box;
  height: 78px;
  border: 1px dashed #d3d6db;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
  cursor: pointer;
  transition:
    border-color 0.2s,
    color 0.2s;
}
.cover-placeholder:hover {
  border-color: #303133;
  color: #303133;
}
.cover-placeholder .iconfont {
  font-size: 22px;
}
</style>
