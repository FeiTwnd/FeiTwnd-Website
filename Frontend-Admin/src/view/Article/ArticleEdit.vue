<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useArticleStore } from '@/stores'
import { uploadFile } from '@/api/settings'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

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
  isPublished: 0
})

const quillRef = ref(null)
const saving = ref(false)
const uploadingCover = ref(false)
const uploadingImg = ref(false)
const showPreview = ref(true)

/* ---- 字数统计（剥除 HTML 标签后统计） ---- */
const wordCount = computed(() => {
  const text = form.value.contentMarkdown.replace(/<[^>]+>/g, '').trim()
  if (!text) return 0
  const chinese = (text.match(/[\u4e00-\u9fff]/g) || []).length
  const english = (text.replace(/[\u4e00-\u9fff]/g, ' ').match(/\b[a-zA-Z0-9]+\b/g) || []).length
  return chinese + english
})

/* ---- Quill 工具栏 ---- */
const toolbarOptions = [
  [{ header: [1, 2, 3, 4, false] }],
  ['bold', 'italic', 'underline', 'strike'],
  [{ color: [] }, { background: [] }],
  [{ align: [] }],
  [{ list: 'ordered' }, { list: 'bullet' }],
  [{ indent: '-1' }, { indent: '+1' }],
  ['blockquote', 'code-block'],
  ['link', 'image'],
  ['clean'],
]

/* ---- 自定义图片上传（替换默认 URL 弹窗） ---- */
const onEditorReady = (quill) => {
  const toolbar = quill.getModule('toolbar')
  toolbar.addHandler('image', () => {
    const input = document.createElement('input')
    input.setAttribute('type', 'file')
    input.setAttribute('accept', 'image/*')
    input.click()
    input.onchange = async () => {
      const file = input.files?.[0]
      if (!file) return
      uploadingImg.value = true
      try {
        const fd = new FormData()
        fd.append('file', file)
        const res = await uploadFile(fd)
        const url = res.data
        const range = quill.getSelection()
        quill.insertEmbed(range?.index ?? 0, 'image', url)
        quill.setSelection((range?.index ?? 0) + 1)
        ElMessage.success('图片上传成功')
      } catch {
        ElMessage.error('图片上传失败')
      } finally {
        uploadingImg.value = false
      }
    }
  })
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

/* ---- 标题失焦自动生成 slug ---- */
const autoSlug = () => {
  if (form.value.title && !form.value.slug) {
    form.value.slug = form.value.title
      .toLowerCase()
      .replace(/[\u4e00-\u9fff]+/g, '-')
      .replace(/[^a-z0-9-]/g, '-')
      .replace(/-+/g, '-')
      .replace(/^-|-$/g, '')
      .substring(0, 50) || `article-${Date.now()}`
  }
}

/* ---- 保存 / 发布 ---- */
const handleSave = async (isPublished) => {
  if (!form.value.title.trim()) return ElMessage.warning('请输入文章标题')
  if (!form.value.slug.trim()) return ElMessage.warning('请输入 URL 标识 (Slug)')
  const content = form.value.contentMarkdown
  if (!content || content === '<p><br></p>')
    return ElMessage.warning('请输入文章内容')
  if (!form.value.categoryId) return ElMessage.warning('请选择文章分类')
  saving.value = true
  try {
    form.value.isPublished = isPublished
    await articleStore.saveArticle({ ...form.value })
    ElMessage.success(isPublished ? '发布成功' : '保存草稿成功')
    router.push('/article/list')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
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
        contentMarkdown: res.contentHtml || res.contentMarkdown || '',
        isPublished: res.isPublished ?? 0
      })
    }
  }
})
</script>

<template>
  <div class="article-edit">
    <!-- 顶部操作栏 -->
    <div class="edit-topbar">
      <span class="edit-title">{{ isEdit ? '编辑文章' : '新建文章' }}</span>

      <div class="topbar-center">
        <el-tag type="info" size="small">字数：{{ wordCount }}</el-tag>
        <el-tag v-if="uploadingImg" type="warning" size="small">图片上传中…</el-tag>
      </div>

      <div class="edit-actions">
        <el-button size="small" :type="showPreview ? 'primary' : ''" @click="showPreview = !showPreview">
          {{ showPreview ? '隐藏预览' : '显示预览' }}
        </el-button>
        <el-button size="small" @click="router.push('/article/list')">取消</el-button>
        <el-button size="small" :loading="saving" @click="handleSave(0)">保存草稿</el-button>
        <el-button size="small" type="primary" :loading="saving" @click="handleSave(1)">发布</el-button>
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
      <!-- 编辑器面板 -->
      <div class="editor-panel">
        <QuillEditor
          ref="quillRef"
          v-model:content="form.contentMarkdown"
          content-type="html"
          theme="snow"
          :toolbar="toolbarOptions"
          @ready="onEditorReady"
        />
      </div>

      <!-- 预览面板 -->
      <div v-show="showPreview" class="preview-panel">
        <div class="preview-header">预览效果</div>
        <!-- eslint-disable-next-line vue/no-v-html -->
        <div
          class="preview-body article-content"
          v-html="form.contentMarkdown || '<p style=\'color:#ccc\'>编辑器内容将在此预览…</p>'"
        />
      </div>

      <!-- 元数据侧边栏 -->
      <aside class="edit-aside">
        <div class="aside-section">
          <div class="aside-label">Slug <span class="req">*</span></div>
          <el-input v-model="form.slug" placeholder="URL 路径标识" clearable size="small" />
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
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%" size="small">
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
            collapse-tags
            placeholder="选择标签"
            style="width:100%"
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
            <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <span class="iconfont icon-image" />
              <span>点击上传</span>
            </div>
          </el-upload>
          <el-input
            v-model="form.coverImage"
            placeholder="或直接输入图片 URL"
            clearable
            size="small"
            style="margin-top:6px"
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
  height: calc(100vh - 56px);
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
.topbar-center {
  display: flex;
  align-items: center;
  gap: 8px;
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
  overflow: hidden;
}

/* 编辑器面板 */
.editor-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #fff;
  border-right: 1px solid #e4e7ed;
}
.editor-panel :deep(.ql-toolbar) {
  border-top: none;
  border-left: none;
  border-right: none;
  background: #fafafa;
  flex-shrink: 0;
}
.editor-panel :deep(.ql-container) {
  flex: 1;
  overflow-y: auto;
  font-size: 15px;
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
  border: none;
}
.editor-panel :deep(.ql-editor) {
  padding: 20px 24px;
  line-height: 1.9;
  min-height: 300px;
}
.editor-panel :deep(.ql-editor img) {
  max-width: 100%;
  border-radius: 4px;
}

/* 预览面板 */
.preview-panel {
  width: 420px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #fff;
  border-right: 1px solid #e4e7ed;
}
.preview-header {
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  background: #fafafa;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}
.preview-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  font-size: 15px;
  color: #303133;
}

/* 预览内容样式 */
.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3),
.article-content :deep(h4) { font-weight: 600; margin: 1.2em 0 0.5em; line-height: 1.4; }
.article-content :deep(h1) { font-size: 1.7em; }
.article-content :deep(h2) { font-size: 1.4em; border-bottom: 1px solid #eee; padding-bottom: 4px; }
.article-content :deep(h3) { font-size: 1.2em; }
.article-content :deep(p) { margin: 0.75em 0; line-height: 1.85; }
.article-content :deep(img) { max-width: 100%; border-radius: 6px; display: block; margin: 8px auto; }
.article-content :deep(a) { color: #409eff; text-decoration: none; }
.article-content :deep(a:hover) { text-decoration: underline; }
.article-content :deep(blockquote) {
  border-left: 4px solid #dfe2e5;
  padding: 4px 12px;
  color: #6a737d;
  margin: 0.8em 0;
  background: #f9f9f9;
  border-radius: 0 4px 4px 0;
}
.article-content :deep(pre) {
  background: #f6f8fa;
  padding: 12px 16px;
  border-radius: 6px;
  overflow-x: auto;
  font-family: 'Courier New', Consolas, monospace;
  font-size: 13px;
  line-height: 1.6;
}
.article-content :deep(code) {
  background: #f0f0f0;
  padding: 2px 5px;
  border-radius: 3px;
  font-family: 'Courier New', Consolas, monospace;
  font-size: 13px;
}
.article-content :deep(pre code) { background: none; padding: 0; }
.article-content :deep(ul),
.article-content :deep(ol) { padding-left: 2em; margin: 0.75em 0; }
.article-content :deep(li) { margin: 0.3em 0; line-height: 1.8; }
.article-content :deep(table) { width: 100%; border-collapse: collapse; margin: 0.8em 0; }
.article-content :deep(th),
.article-content :deep(td) { border: 1px solid #ddd; padding: 6px 10px; text-align: left; }
.article-content :deep(th) { background: #f2f2f2; font-weight: 600; }

/* 侧边栏 */
.edit-aside {
  width: 230px;
  flex-shrink: 0;
  overflow-y: auto;
  background: #fff;
  padding: 14px 12px;
}
.aside-section { margin-bottom: 18px; }
.aside-label {
  font-size: 12px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 5px;
}
.req { color: #f56c6c; }

/* 封面上传 */
.cover-uploader { width: 100%; }
.cover-preview {
  width: 100%;
  border-radius: 6px;
  object-fit: cover;
  cursor: pointer;
  max-height: 110px;
}
.cover-placeholder {
  width: 100%;
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
  transition: border-color 0.2s, color 0.2s;
}
.cover-placeholder:hover { border-color: #409eff; color: #409eff; }
.cover-placeholder .iconfont { font-size: 22px; }
</style>
