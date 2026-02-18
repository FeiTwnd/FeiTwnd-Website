<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useArticleStore } from '@/stores'
import { uploadFile } from '@/api/settings'
import { Editor } from '@bytemd/vue-next'
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import 'bytemd/dist/index.css'

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
  categoryId: '',
  tagIds: [],
  contentMarkdown: '',
  isPublished: 0
})

const plugins = [gfm(), highlight()]

const saving = ref(false)

/* ---- 图片上传 ---- */
const beforeUpload = (file) => {
  const ok = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'].includes(
    file.type
  )
  if (!ok) ElMessage.error('只支持 JPG/PNG/WEBP/GIF 格式')
  return ok
}

const handleCoverUpload = async (options) => {
  const fd = new FormData()
  fd.append('file', options.file)
  const res = await uploadFile(fd)
  form.value.coverImage = res.data
  ElMessage.success('封面上传成功')
}

/* ---- 提交 ---- */
const handleSave = async (isPublished) => {
  if (!form.value.title.trim()) return ElMessage.warning('请输入文章标题')
  if (!form.value.contentMarkdown.trim())
    return ElMessage.warning('请输入文章内容')
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
        title: res.title,
        slug: res.slug,
        summary: res.summary,
        coverImage: res.coverImage,
        categoryId: res.categoryId,
        tagIds: res.tagIds ?? [],
        contentMarkdown: res.contentMarkdown,
        isPublished: res.isPublished
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
      <div class="edit-actions">
        <el-button @click="router.push('/article/list')">取消</el-button>
        <el-button :loading="saving" @click="handleSave(0)">保存草稿</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave(1)"
          >发布</el-button
        >
      </div>
    </div>

    <div class="edit-body">
      <!-- 主编辑区 -->
      <div class="edit-main">
        <el-input
          v-model="form.title"
          placeholder="请输入文章标题"
          class="title-input"
          size="large"
        />
        <div class="editor-wrap">
          <Editor v-model:value="form.contentMarkdown" :plugins="plugins" />
        </div>
      </div>

      <!-- 右侧侧边栏 -->
      <aside class="edit-aside">
        <div class="aside-section">
          <div class="aside-label">Slug</div>
          <el-input v-model="form.slug" placeholder="自定义 URL 路径" />
        </div>

        <div class="aside-section">
          <div class="aside-label">摘要</div>
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="文章摘要"
          />
        </div>

        <div class="aside-section">
          <div class="aside-label">分类</div>
          <el-select
            v-model="form.categoryId"
            placeholder="选择分类"
            style="width: 100%"
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
            class="cover-uploader"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleCoverUpload"
          >
            <img
              v-if="form.coverImage"
              :src="form.coverImage"
              class="cover-preview"
            />
            <div v-else class="cover-placeholder">
              <!-- ICON: icon-image -->
              <span class="iconfont icon-image" />
              <span>上传封面</span>
            </div>
          </el-upload>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.article-edit {
  display: flex;
  flex-direction: column;
  gap: 0;
  height: calc(100vh - 108px);
}

.edit-topbar {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 14px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.edit-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.edit-actions {
  display: flex;
  gap: 8px;
}

.edit-body {
  display: flex;
  gap: 16px;
  flex: 1;
  overflow: hidden;
}

.edit-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
}

.title-input :deep(.el-input__inner) {
  font-size: 18px;
  font-weight: 600;
  border: none;
  border-bottom: 1px solid #e4e7ed;
  border-radius: 0;
  padding-left: 0;
}

.editor-wrap {
  flex: 1;
  overflow: hidden;
}

.editor-wrap :deep(.bytemd) {
  height: 100%;
  border: none;
}

/* ---- 侧边栏 ---- */
.edit-aside {
  width: 260px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 0;
  overflow-y: auto;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
}

.aside-section {
  margin-bottom: 18px;
}

.aside-label {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 6px;
}

/* ---- 封面上传 ---- */
.cover-uploader {
  width: 100%;
}

.cover-preview {
  width: 100%;
  border-radius: 6px;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100px;
  border: 1px dashed #d3d6db;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #909399;
  font-size: 13px;
  cursor: pointer;
}

.cover-placeholder .iconfont {
  font-size: 26px;
}
</style>
