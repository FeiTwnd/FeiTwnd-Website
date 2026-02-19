<script setup>
import { ref, onMounted } from 'vue'
import { useProfileStore } from '@/stores'
import { uploadFile } from '@/api/settings'

const profileStore = useProfileStore()

/* ---- 当前 Tab ---- */
const activeTab = ref('experience')

/* ============================================================
   经历管理
   ============================================================ */
const expFilter = ref('')
const expDialogVisible = ref(false)
const expEditing = ref(false)
const uploadingLogo = ref(false)

const handleLogoUpload = async (options) => {
  uploadingLogo.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    expForm.value.logoUrl = res.data
    ElMessage.success('上传成功')
  } finally {
    uploadingLogo.value = false
  }
}

const uploadingSkillIcon = ref(false)

const handleSkillIconUpload = async (options) => {
  uploadingSkillIcon.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    skillForm.value.icon = res.data
    ElMessage.success('上传成功')
  } finally {
    uploadingSkillIcon.value = false
  }
}

const expForm = ref({
  id: null,
  type: 0,
  title: '',
  subtitle: '',
  logoUrl: '',
  content: '',
  startDate: '',
  endDate: '',
  isVisible: 1
})
const expSaving = ref(false)

/** 加载经历列表（可按 type 过滤） */
const loadExperiences = () => {
  profileStore.fetchExperiences(
    expFilter.value === '' ? undefined : expFilter.value
  )
}

const openExpDialog = (row = null) => {
  expEditing.value = !!row
  expForm.value = row
    ? {
        id: row.id,
        type: row.type ?? 0,
        title: row.title,
        subtitle: row.subtitle ?? '',
        logoUrl: row.logoUrl ?? '',
        content: row.content ?? '',
        startDate: row.startDate ?? '',
        endDate: row.endDate ?? '',
        isVisible: row.isVisible ?? 1
      }
    : {
        id: null,
        type: 0,
        title: '',
        subtitle: '',
        logoUrl: '',
        content: '',
        startDate: '',
        endDate: '',
        isVisible: 1
      }
  expDialogVisible.value = true
}

const handleExpSave = async () => {
  if (!expForm.value.title.trim()) return ElMessage.warning('标题不能为空')
  expSaving.value = true
  try {
    await profileStore.saveExperience({ ...expForm.value })
    ElMessage.success(expEditing.value ? '修改成功' : '创建成功')
    expDialogVisible.value = false
    loadExperiences()
  } finally {
    expSaving.value = false
  }
}

const deleteExp = async (row) => {
  await ElMessageBox.confirm(`确认删除「${row.title}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await profileStore.removeExperiences([row.id])
  ElMessage.success('删除成功')
  loadExperiences()
}

/* ============================================================
   技能管理
   ============================================================ */
const skillDialogVisible = ref(false)
const skillEditing = ref(false)
const skillForm = ref({
  id: null,
  name: '',
  description: '',
  icon: '',
  sort: null,
  isVisible: 1
})
const skillSaving = ref(false)

const openSkillDialog = (row = null) => {
  skillEditing.value = !!row
  skillForm.value = row
    ? {
        id: row.id,
        name: row.name,
        description: row.description ?? '',
        icon: row.icon ?? '',
        sort: row.sort ?? null,
        isVisible: row.isVisible ?? 1
      }
    : {
        id: null,
        name: '',
        description: '',
        icon: '',
        sort: null,
        isVisible: 1
      }
  skillDialogVisible.value = true
}

const handleSkillSave = async () => {
  if (!skillForm.value.name.trim()) return ElMessage.warning('技能名称不能为空')
  skillSaving.value = true
  try {
    await profileStore.saveSkill({ ...skillForm.value })
    ElMessage.success(skillEditing.value ? '修改成功' : '创建成功')
    skillDialogVisible.value = false
    profileStore.fetchSkills()
  } finally {
    skillSaving.value = false
  }
}

const deleteSkill = async (row) => {
  await ElMessageBox.confirm(`确认删除技能「${row.name}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await profileStore.removeSkills([row.id])
  ElMessage.success('删除成功')
  profileStore.fetchSkills()
}

/* ============================================================
   社交媒体管理
   ============================================================ */
const socialDialogVisible = ref(false)
const socialEditing = ref(false)
const socialForm = ref({
  id: null,
  name: '',
  icon: '',
  link: '',
  sort: null,
  isVisible: 1
})
const socialSaving = ref(false)

const openSocialDialog = (row = null) => {
  socialEditing.value = !!row
  socialForm.value = row
    ? {
        id: row.id,
        name: row.name,
        icon: row.icon ?? '',
        link: row.link ?? '',
        sort: row.sort ?? null,
        isVisible: row.isVisible ?? 1
      }
    : { id: null, name: '', icon: '', link: '', sort: null, isVisible: 1 }
  socialDialogVisible.value = true
}

const handleSocialSave = async () => {
  if (!socialForm.value.name.trim())
    return ElMessage.warning('平台名称不能为空')
  socialSaving.value = true
  try {
    await profileStore.saveSocialMedia({ ...socialForm.value })
    ElMessage.success(socialEditing.value ? '修改成功' : '创建成功')
    socialDialogVisible.value = false
    profileStore.fetchSocialMedias()
  } finally {
    socialSaving.value = false
  }
}

const deleteSocial = async (row) => {
  await ElMessageBox.confirm(`确认删除「${row.name}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await profileStore.removeSocialMedias([row.id])
  ElMessage.success('删除成功')
  profileStore.fetchSocialMedias()
}

/* ---- 初始化 ---- */
onMounted(() => {
  loadExperiences()
  profileStore.fetchSkills()
  profileStore.fetchSocialMedias()
})
</script>

<template>
  <div class="profile-page">
    <el-tabs v-model="activeTab" class="tabs-wrap">
      <!-- ============ 经历管理 ============ -->
      <el-tab-pane label="经历管理" name="experience">
        <div class="tab-toolbar">
          <div class="tab-toolbar-left">
            <el-radio-group v-model="expFilter" @change="loadExperiences">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button :label="0">教育经历</el-radio-button>
              <el-radio-button :label="1">工作经历</el-radio-button>
              <el-radio-button :label="2">项目经历</el-radio-button>
            </el-radio-group>
          </div>
          <el-button type="primary" @click="openExpDialog()">
            <!-- ICON: icon-add -->
            <span class="iconfont icon-add" /> 新增经历
          </el-button>
        </div>
        <el-table
          :data="profileStore.experiences"
          border
          stripe
          v-loading="profileStore.loading"
        >
          <el-table-column prop="title" label="标题 / 名称" min-width="160" />
          <el-table-column
            prop="subtitle"
            label="副标题 / 职位"
            min-width="160"
          />
          <el-table-column label="类型" width="100" align="center">
            <template #default="{ row }">
              <span>{{
                row.type === 0 ? '教育' : row.type === 1 ? '工作' : '项目'
              }}</span>
            </template>
          </el-table-column>
          <el-table-column label="时间" width="200" align="center">
            <template #default="{ row }">
              {{ row.startDate ?? '-' }} ~ {{ row.endDate ?? '至今' }}
            </template>
          </el-table-column>
          <el-table-column
            prop="content"
            label="内容"
            min-width="200"
            show-overflow-tooltip
          />
          <el-table-column
            label="操作"
            width="140"
            align="center"
            fixed="right"
          >
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link size="small" @click="openExpDialog(row)"
                  >编辑</el-button
                >
                <el-divider direction="vertical" />
                <el-button
                  link
                  size="small"
                  type="danger"
                  @click="deleteExp(row)"
                  >删除</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ============ 技能管理 ============ -->
      <el-tab-pane label="技能管理" name="skill">
        <div class="tab-toolbar">
          <div />
          <el-button type="primary" @click="openSkillDialog()">
            <!-- ICON: icon-add -->
            <span class="iconfont icon-add" /> 新增技能
          </el-button>
        </div>
        <el-table
          :data="profileStore.skills"
          border
          stripe
          v-loading="profileStore.loading"
        >
          <el-table-column prop="name" label="技能名称" min-width="140" />
          <el-table-column
            prop="description"
            label="描述"
            min-width="200"
            show-overflow-tooltip
          />
          <el-table-column
            prop="icon"
            label="图标"
            width="160"
            show-overflow-tooltip
          />
          <el-table-column prop="sort" label="排序" width="80" align="center" />
          <el-table-column
            label="操作"
            width="140"
            align="center"
            fixed="right"
          >
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link size="small" @click="openSkillDialog(row)"
                  >编辑</el-button
                >
                <el-divider direction="vertical" />
                <el-button
                  link
                  size="small"
                  type="danger"
                  @click="deleteSkill(row)"
                  >删除</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ============ 社交媒体 ============ -->
      <el-tab-pane label="社交媒体" name="social">
        <div class="tab-toolbar">
          <div />
          <el-button type="primary" @click="openSocialDialog()">
            <!-- ICON: icon-add -->
            <span class="iconfont icon-add" /> 新增平台
          </el-button>
        </div>
        <el-table
          :data="profileStore.socialMedias"
          border
          stripe
          v-loading="profileStore.loading"
        >
          <el-table-column prop="name" label="平台名称" min-width="140" />
          <el-table-column label="链接" min-width="260">
            <template #default="{ row }">
              <a
                :href="row.link"
                target="_blank"
                rel="noopener"
                class="link-href"
              >
                {{ row.link }}
              </a>
            </template>
          </el-table-column>
          <el-table-column
            prop="icon"
            label="图标标识"
            min-width="130"
            show-overflow-tooltip
          />
          <el-table-column prop="sort" label="排序" width="80" align="center" />
          <el-table-column
            label="操作"
            width="140"
            align="center"
            fixed="right"
          >
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link size="small" @click="openSocialDialog(row)"
                  >编辑</el-button
                >
                <el-divider direction="vertical" />
                <el-button
                  link
                  size="small"
                  type="danger"
                  @click="deleteSocial(row)"
                  >删除</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- ---- 经历弹窗 ---- -->
    <el-dialog
      v-model="expDialogVisible"
      :title="expEditing ? '编辑经历' : '新增经历'"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form :model="expForm" label-width="90px">
        <el-form-item label="类型">
          <el-radio-group v-model="expForm.type">
            <el-radio :value="0">教育经历</el-radio>
            <el-radio :value="1">工作经历</el-radio>
            <el-radio :value="2">项目经历</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input
            v-model="expForm.title"
            placeholder="公司名 / 学校名 / 项目名"
            clearable
          />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input
            v-model="expForm.subtitle"
            placeholder="职位 / 专业 / 项目角色"
            clearable
          />
        </el-form-item>
        <el-form-item label="Logo">
          <div class="upload-row">
            <el-upload
              :show-file-list="false"
              :http-request="handleLogoUpload"
              accept="image/*"
            >
              <el-button size="small" :loading="uploadingLogo"
                ><span class="iconfont icon-upload" />上传Logo</el-button
              >
            </el-upload>
            <el-input
              v-model="expForm.logoUrl"
              placeholder="Logo URL（可手动输入）"
              clearable
              class="upload-url-input"
            />
          </div>
          <img
            v-if="expForm.logoUrl"
            :src="expForm.logoUrl"
            class="logo-preview"
          />
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-input
            v-model="expForm.startDate"
            placeholder="如：2022-07"
            clearable
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-input
            v-model="expForm.endDate"
            placeholder="如：2024-06，留空表示至今"
            clearable
          />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
            v-model="expForm.content"
            type="textarea"
            :rows="3"
            placeholder="详细描述"
          />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch
            v-model="expForm.isVisible"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="expDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="expSaving" @click="handleExpSave"
          >确认</el-button
        >
      </template>
    </el-dialog>

    <!-- ---- 技能弹窗 ---- -->
    <el-dialog
      v-model="skillDialogVisible"
      :title="skillEditing ? '编辑技能' : '新增技能'"
      width="440px"
      :close-on-click-modal="false"
    >
      <el-form :model="skillForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input
            v-model="skillForm.name"
            placeholder="如：Vue 3 / Python"
            clearable
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="skillForm.description"
            type="textarea"
            :rows="2"
            placeholder="简要描述（可选）"
          />
        </el-form-item>
        <el-form-item label="图标">
          <div
            style="
              display: flex;
              align-items: center;
              gap: 8px;
              flex-wrap: wrap;
            "
          >
            <el-upload
              :show-file-list="false"
              :http-request="handleSkillIconUpload"
              accept="image/*"
            >
              <el-button size="small" :loading="uploadingSkillIcon">
                <span class="iconfont icon-upload" />上传图标
              </el-button>
            </el-upload>
            <el-input
              v-model="skillForm.icon"
              placeholder="图标 URL（可手动填入）"
              clearable
              style="flex: 1; min-width: 160px"
            />
          </div>
          <img
            v-if="skillForm.icon"
            :src="skillForm.icon"
            style="
              width: 36px;
              height: 36px;
              object-fit: contain;
              margin-top: 6px;
              border: 1px solid #e4e7ed;
              border-radius: 4px;
            "
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="skillForm.sort"
            :min="0"
            :precision="0"
            controls-position="right"
            style="width: 120px"
          />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch
            v-model="skillForm.isVisible"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="skillDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="skillSaving"
          @click="handleSkillSave"
          >确认</el-button
        >
      </template>
    </el-dialog>

    <!-- ---- 社交媒体弹窗 ---- -->
    <el-dialog
      v-model="socialDialogVisible"
      :title="socialEditing ? '编辑社交媒体' : '新增社交媒体'"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form :model="socialForm" label-width="90px">
        <el-form-item label="平台名称" required>
          <el-input
            v-model="socialForm.name"
            placeholder="如：GitHub / Twitter"
            clearable
          />
        </el-form-item>
        <el-form-item label="主页链接">
          <el-input
            v-model="socialForm.link"
            placeholder="https://..."
            clearable
          />
        </el-form-item>
        <el-form-item label="图标标识">
          <el-input
            v-model="socialForm.icon"
            placeholder="图标类名或 URL（可选）"
            clearable
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="socialForm.sort"
            :min="0"
            :precision="0"
            controls-position="right"
            style="width: 120px"
          />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch
            v-model="socialForm.isVisible"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="socialDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="socialSaving"
          @click="handleSocialSave"
          >确认</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-page {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
}

.tab-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 10px;
}

.tab-toolbar .iconfont {
  font-size: 14px;
  margin-right: 4px;
}

.row-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

.link-href {
  color: #606266;
  font-size: 13px;
  text-decoration: none;
}

.link-href:hover {
  color: #000000;
  text-decoration: underline;
}

/* ---- 技能卡片网格 ---- */
.skill-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 14px;
  min-height: 80px;
}

.skill-card {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 14px 16px;
}

.skill-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.skill-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.skill-ops {
  display: flex;
  align-items: center;
  gap: 6px;
}

.op-icon {
  font-size: 14px;
  color: #909399;
  cursor: pointer;
  transition: color 0.15s;
}

.op-icon:hover {
  color: #303133;
}

.op-icon.danger:hover {
  color: #f56c6c;
}

.skill-progress :deep(.el-progress-bar__outer) {
  background-color: #e4e7ed;
}

/* ---- 滑块 ---- */
.slider-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.skill-slider {
  flex: 1;
}

.slider-val {
  font-size: 13px;
  color: #606266;
  width: 36px;
  flex-shrink: 0;
  text-align: right;
}

/* ---- 上传行 ---- */
.upload-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}
.upload-url-input {
  flex: 1;
}

.logo-preview {
  width: 56px;
  height: 56px;
  border-radius: 6px;
  object-fit: contain;
  margin-top: 8px;
  border: 1px solid #e4e7ed;
}
</style>
