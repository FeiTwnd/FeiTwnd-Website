<script setup>
import { ref, onMounted } from 'vue'
import {
  getConfigList,
  createConfig,
  updateConfig,
  deleteConfigs,
  getPersonalInfo,
  updatePersonalInfo,
  uploadFile
} from '@/api/settings'
import {
  changePassword,
  changeNickname,
  changeEmail,
  sendCode
} from '@/api/auth'
import { useUserStore } from '@/stores'

/* ---- Tab ---- */
const activeTab = ref('personal')

/* ---- 个人信息 ---- */
const personalForm = ref({
  nickname: '',
  tag: '',
  description: '',
  avatar: '',
  email: '',
  website: '',
  github: '',
  location: ''
})
const savingPersonal = ref(false)

const fetchPersonal = async () => {
  const res = await getPersonalInfo()
  Object.assign(personalForm.value, res.data ?? {})
}

const handleAvatarUpload = async (options) => {
  const fd = new FormData()
  fd.append('file', options.file)
  const res = await uploadFile(fd)
  personalForm.value.avatar = res.data
  ElMessage.success('头像上传成功')
}

const savePersonal = async () => {
  savingPersonal.value = true
  try {
    await updatePersonalInfo({ ...personalForm.value })
    ElMessage.success('个人信息保存成功')
  } finally {
    savingPersonal.value = false
  }
}

/* ---- 系统配置 ---- */
const configs = ref([])
const loadingConfig = ref(false)
const configDialogVisible = ref(false)
const configForm = ref({
  id: null,
  configKey: '',
  configValue: '',
  configType: '',
  description: ''
})
const configEditing = ref(false)

const fetchConfigs = async () => {
  loadingConfig.value = true
  try {
    const res = await getConfigList()
    configs.value = res.data ?? []
  } finally {
    loadingConfig.value = false
  }
}

const openConfigDialog = (row = null) => {
  configEditing.value = !!row
  configForm.value = row
    ? {
        id: row.id,
        configKey: row.configKey,
        configValue: row.configValue,
        configType: row.configType ?? '',
        description: row.description ?? ''
      }
    : {
        id: null,
        configKey: '',
        configValue: '',
        configType: '',
        description: ''
      }
  configDialogVisible.value = true
}

const saveConfig = async () => {
  if (!configForm.value.configKey.trim())
    return ElMessage.warning('配置键不能为空')
  if (configEditing.value) {
    await updateConfig({ ...configForm.value })
  } else {
    await createConfig({ ...configForm.value })
  }
  ElMessage.success(configEditing.value ? '修改成功' : '创建成功')
  configDialogVisible.value = false
  fetchConfigs()
}

const deleteConfig = async (row) => {
  await ElMessageBox.confirm(`确认删除配置「${row.configKey}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await deleteConfigs([row.id])
  ElMessage.success('删除成功')
  fetchConfigs()
}

onMounted(() => {
  fetchPersonal()
  fetchConfigs()
})

/* ---- 账号安全 ---- */
const pwdFormRef = ref(null)
const savingPwd = ref(false)
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

/** 修改密码 */
const handleChangePassword = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    return ElMessage.warning('请填写完整密码信息')
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmNewPassword) {
    return ElMessage.warning('两次新密码输入不一致')
  }
  savingPwd.value = true
  try {
    await changePassword({ ...pwdForm.value })
    ElMessage.success('密码修改成功，即将退出登录')
    const userStore = useUserStore()
    await userStore.logoutAction()
  } finally {
    savingPwd.value = false
  }
}

/* 修改昵称 */
const nicknameVal = ref('')
const savingNickname = ref(false)

const handleChangeNickname = async () => {
  if (!nicknameVal.value.trim()) return ElMessage.warning('昵称不能为空')
  savingNickname.value = true
  try {
    await changeNickname({ nickname: nicknameVal.value })
    ElMessage.success('昵称修改成功')
    const userStore = useUserStore()
    await userStore.fetchUserInfo()
  } finally {
    savingNickname.value = false
  }
}

/* 换绑邮箱 */
const emailForm = ref({ email: '', code: '' })
const savingEmail = ref(false)
const sendingEmailCode = ref(false)
const emailCounting = ref(false)
const emailCountdown = ref(60)
let emailTimer = null

const sendEmailCode = async () => {
  const { useUserStore } = await import('@/stores')
  const userStore = useUserStore()
  sendingEmailCode.value = true
  try {
    await sendCode({ username: userStore.userInfo?.username ?? '' })
    ElMessage.success('验证码已发送')
    emailCounting.value = true
    emailCountdown.value = 60
    emailTimer = setInterval(() => {
      emailCountdown.value--
      if (emailCountdown.value <= 0) {
        clearInterval(emailTimer)
        emailCounting.value = false
      }
    }, 1000)
  } finally {
    sendingEmailCode.value = false
  }
}

const handleChangeEmail = async () => {
  if (!emailForm.value.email.trim() || !emailForm.value.code.trim()) {
    return ElMessage.warning('请填写新邮箱和验证码')
  }
  savingEmail.value = true
  try {
    await changeEmail({ ...emailForm.value })
    ElMessage.success('邮箱更换成功')
    emailForm.value = { email: '', code: '' }
  } finally {
    savingEmail.value = false
  }
}
</script>

<template>
  <div class="settings-page">
    <el-tabs v-model="activeTab" class="tabs-wrap">
      <!-- ---- 个人信息 ---- -->
      <el-tab-pane label="个人信息" name="personal">
        <div class="personal-wrap">
          <!-- 头像上传 -->
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="handleAvatarUpload"
          >
            <img
              v-if="personalForm.avatar"
              :src="personalForm.avatar"
              class="avatar-preview"
            />
            <div v-else class="avatar-placeholder">
              <!-- ICON: icon-user -->
              <span class="iconfont icon-user" />
            </div>
          </el-upload>

          <el-form
            :model="personalForm"
            label-width="90px"
            class="personal-form"
          >
            <el-form-item label="昵称">
              <el-input
                v-model="personalForm.nickname"
                placeholder="昵称"
                clearable
              />
            </el-form-item>
            <el-form-item label="标签" required>
              <el-input
                v-model="personalForm.tag"
                placeholder="如：全栈开发者 / 前端工程师"
                clearable
              />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input
                v-model="personalForm.description"
                type="textarea"
                :rows="3"
                placeholder="一句话介绍自己"
              />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input
                v-model="personalForm.email"
                placeholder="联系邮箱"
                clearable
              />
            </el-form-item>
            <el-form-item label="个人网站">
              <el-input
                v-model="personalForm.website"
                placeholder="https://..."
                clearable
              />
            </el-form-item>
            <el-form-item label="GitHub">
              <el-input
                v-model="personalForm.github"
                placeholder="https://github.com/xxx"
                clearable
              />
            </el-form-item>
            <el-form-item label="所在地">
              <el-input
                v-model="personalForm.location"
                placeholder="如：中国 · 广州"
                clearable
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="savingPersonal"
                @click="savePersonal"
                >保存</el-button
              >
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- ---- 系统配置 ---- -->
      <el-tab-pane label="系统配置" name="config">
        <div class="tab-toolbar">
          <el-button type="primary" @click="openConfigDialog()">
            <!-- ICON: icon-plus -->
            <span class="iconfont icon-plus" /> 新增配置
          </el-button>
        </div>
        <el-table :data="configs" border stripe v-loading="loadingConfig">
          <el-table-column prop="configKey" label="配置键" min-width="160" />
          <el-table-column
            prop="configValue"
            label="配置值"
            min-width="180"
            show-overflow-tooltip
          />
          <el-table-column prop="configType" label="类型" width="100" />
          <el-table-column
            prop="description"
            label="描述"
            min-width="160"
            show-overflow-tooltip
          />
          <el-table-column label="操作" width="140" align="center">
            <template #default="{ row }">
              <el-button link size="small" @click="openConfigDialog(row)"
                >编辑</el-button
              >
              <el-divider direction="vertical" />
              <el-button
                link
                size="small"
                type="danger"
                @click="deleteConfig(row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ---- 账号安全 ---- -->
      <el-tab-pane label="账号安全" name="security">
        <div class="security-wrap">
          <!-- 修改密码 -->
          <div class="security-section">
            <h3 class="section-title">修改密码</h3>
            <el-form :model="pwdForm" label-width="100px" class="security-form">
              <el-form-item label="当前密码">
                <el-input
                  v-model="pwdForm.oldPassword"
                  type="password"
                  show-password
                  placeholder="输入当前密码"
                  style="max-width: 320px"
                />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input
                  v-model="pwdForm.newPassword"
                  type="password"
                  show-password
                  placeholder="输入新密码"
                  style="max-width: 320px"
                />
              </el-form-item>
              <el-form-item label="确认新密码">
                <el-input
                  v-model="pwdForm.confirmNewPassword"
                  type="password"
                  show-password
                  placeholder="再次输入新密码"
                  style="max-width: 320px"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="savingPwd"
                  @click="handleChangePassword"
                  >保存密码</el-button
                >
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <!-- 修改昵称 -->
          <div class="security-section">
            <h3 class="section-title">修改昵称</h3>
            <div class="inline-action">
              <el-input
                v-model="nicknameVal"
                placeholder="输入新昵称"
                clearable
                style="max-width: 320px"
              />
              <el-button
                type="primary"
                :loading="savingNickname"
                @click="handleChangeNickname"
                >保存昵称</el-button
              >
            </div>
          </div>

          <el-divider />

          <!-- 换绑邮箱 -->
          <div class="security-section">
            <h3 class="section-title">换绑邮箱</h3>
            <el-form
              :model="emailForm"
              label-width="100px"
              class="security-form"
            >
              <el-form-item label="新邮箱">
                <el-input
                  v-model="emailForm.email"
                  placeholder="输入新邮箱地址"
                  clearable
                  style="max-width: 320px"
                />
              </el-form-item>
              <el-form-item label="验证码">
                <div class="code-row">
                  <el-input
                    v-model="emailForm.code"
                    placeholder="输入验证码"
                    style="max-width: 200px"
                  />
                  <el-button
                    :loading="sendingEmailCode"
                    :disabled="emailCounting"
                    @click="sendEmailCode"
                  >
                    {{
                      emailCounting ? `${emailCountdown}s 后重试` : '获取验证码'
                    }}
                  </el-button>
                </div>
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="savingEmail"
                  @click="handleChangeEmail"
                  >确认换绑</el-button
                >
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 配置弹窗 -->
    <el-dialog
      v-model="configDialogVisible"
      :title="configEditing ? '编辑配置' : '新增配置'"
      width="480px"
    >
      <el-form :model="configForm" label-width="80px">
        <el-form-item label="配置键">
          <el-input
            v-model="configForm.configKey"
            placeholder="例如 site.title"
          />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input
            v-model="configForm.configValue"
            type="textarea"
            :rows="3"
            placeholder="配置值"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="configForm.configType"
            placeholder="选择类型"
            clearable
            style="width: 100%"
          >
            <el-option label="string" value="string" />
            <el-option label="number" value="number" />
            <el-option label="boolean" value="boolean" />
            <el-option label="json" value="json" />
            <el-option label="text" value="text" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="configForm.description"
            type="textarea"
            :rows="2"
            placeholder="配置说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveConfig">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.settings-page {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
}

.tab-toolbar {
  margin-bottom: 14px;
}

.tab-toolbar .iconfont {
  font-size: 14px;
  margin-right: 4px;
}

/* ---- 个人信息 ---- */
.personal-wrap {
  display: flex;
  gap: 32px;
  align-items: flex-start;
  padding: 12px 0;
}

.avatar-uploader {
  flex-shrink: 0;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #f5f7fa;
  border: 1px dashed #d3d6db;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.avatar-placeholder .iconfont {
  font-size: 36px;
  color: #c0c4cc;
}

.personal-form {
  flex: 1;
  max-width: 480px;
}

/* ---- 账号安全 ---- */
.security-wrap {
  padding: 8px 0;
}

.security-section {
  padding: 8px 0 16px;
}

.section-title {
  margin: 0 0 16px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.security-form {
  max-width: 520px;
}

.inline-action {
  display: flex;
  align-items: center;
  gap: 12px;
}

.code-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
