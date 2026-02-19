<script setup>
import { ref, onMounted } from 'vue'
import { useCategoryStore } from '@/stores'

const categoryStore = useCategoryStore()

/* ---- Tab ---- */
const activeTab = ref('category')

/* ---- 分类弹窗 ---- */
const catDialogVisible = ref(false)
const catForm = ref({ id: null, name: '', slug: '', icon: '', sort: null })
const catEditing = ref(false)

const openCatDialog = (row = null) => {
  catEditing.value = !!row
  catForm.value = row
    ? {
        id: row.id,
        name: row.name,
        slug: row.slug,
        icon: row.icon ?? '',
        sort: row.sort ?? null
      }
    : { id: null, name: '', slug: '', icon: '', sort: null }
  catDialogVisible.value = true
}

const saveCat = async () => {
  if (!catForm.value.name.trim()) return ElMessage.warning('分类名称不能为空')
  await categoryStore.saveCategory({ ...catForm.value })
  ElMessage.success(catEditing.value ? '修改成功' : '创建成功')
  catDialogVisible.value = false
}

const deleteCat = async (row) => {
  await ElMessageBox.confirm(`确认删除分类「${row.name}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await categoryStore.removeCategories([row.id])
  ElMessage.success('删除成功')
}

/* ---- 标签弹窗 ---- */
const tagDialogVisible = ref(false)
const tagForm = ref({ id: null, name: '', slug: '' })
const tagEditing = ref(false)

const openTagDialog = (row = null) => {
  tagEditing.value = !!row
  tagForm.value = row
    ? { id: row.id, name: row.name, slug: row.slug ?? '' }
    : { id: null, name: '', slug: '' }
  tagDialogVisible.value = true
}

const saveTag = async () => {
  if (!tagForm.value.name.trim()) return ElMessage.warning('标签名称不能为空')
  await categoryStore.saveTag({ ...tagForm.value })
  ElMessage.success(tagEditing.value ? '修改成功' : '创建成功')
  tagDialogVisible.value = false
}

const deleteTag = async (row) => {
  await ElMessageBox.confirm(`确认删除标签「${row.name}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).catch(() => {
    throw new Error('cancel')
  })
  await categoryStore.removeTags([row.id])
  ElMessage.success('删除成功')
}

onMounted(() => {
  categoryStore.fetchCategories()
  categoryStore.fetchTags()
})
</script>

<template>
  <div class="category-page">
    <el-tabs v-model="activeTab" class="tabs-wrap">
      <!-- ---- 分类管理 ---- -->
      <el-tab-pane label="分类管理" name="category">
        <div class="tab-toolbar">
          <el-button type="primary" @click="openCatDialog()">
            <span class="iconfont icon-plus" /> 新建分类
          </el-button>
        </div>
        <el-table
          :data="categoryStore.categories"
          border
          stripe
          v-loading="categoryStore.loading"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="分类名称" min-width="140" />
          <el-table-column prop="slug" label="Slug" min-width="130" />
          <el-table-column
            prop="icon"
            label="图标"
            width="120"
            show-overflow-tooltip
          />
          <el-table-column prop="sort" label="排序" width="80" align="center" />
          <el-table-column label="操作" width="140" align="center">
            <template #default="{ row }">
              <el-button link size="small" @click="openCatDialog(row)"
                >编辑</el-button
              >
              <el-divider direction="vertical" />
              <el-button link size="small" type="danger" @click="deleteCat(row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ---- 标签管理 ---- -->
      <el-tab-pane label="标签管理" name="tag">
        <div class="tab-toolbar">
          <el-button type="primary" @click="openTagDialog()">
            <span class="iconfont icon-plus" /> 新建标签
          </el-button>
        </div>
        <el-table
          :data="categoryStore.tags"
          border
          stripe
          v-loading="categoryStore.loading"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="标签名称" min-width="140" />
          <el-table-column prop="slug" label="Slug" min-width="180" />
          <el-table-column label="操作" width="140" align="center">
            <template #default="{ row }">
              <el-button link size="small" @click="openTagDialog(row)"
                >编辑</el-button
              >
              <el-divider direction="vertical" />
              <el-button link size="small" @click="deleteTag(row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 分类弹窗 -->
    <el-dialog
      v-model="catDialogVisible"
      :title="catEditing ? '编辑分类' : '新建分类'"
      width="460px"
    >
      <el-form :model="catForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="catForm.name" placeholder="分类名称" clearable />
        </el-form-item>
        <el-form-item label="Slug" required>
          <el-input
            v-model="catForm.slug"
            placeholder="URL 路径标识（如: tech）"
            clearable
          />
        </el-form-item>
        <el-form-item label="图标">
          <el-input
            v-model="catForm.icon"
            placeholder="图标类名（可选）"
            clearable
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="catForm.sort"
            :min="0"
            :precision="0"
            controls-position="right"
            style="width: 120px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="catDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCat">确认</el-button>
      </template>
    </el-dialog>

    <!-- 标签弹窗 -->
    <el-dialog
      v-model="tagDialogVisible"
      :title="tagEditing ? '编辑标签' : '新建标签'"
      width="380px"
    >
      <el-form :model="tagForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="tagForm.name" placeholder="标签名称" clearable />
        </el-form-item>
        <el-form-item label="Slug" required>
          <el-input
            v-model="tagForm.slug"
            placeholder="URL 路径标识（如: vue-js）"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTag">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.category-page {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
}

.tabs-wrap {
  height: 100%;
}

.tab-toolbar {
  margin-bottom: 14px;
}

.tab-toolbar .iconfont {
  font-size: 14px;
  margin-right: 4px;
}
</style>
