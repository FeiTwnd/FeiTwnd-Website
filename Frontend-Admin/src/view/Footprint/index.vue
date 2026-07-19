<script setup>
import { ref, computed, onMounted } from 'vue'
import { useFootprintStore, useUserStore } from '@/stores'
import { uploadFile } from '@/api/settings'
import cityGeoJSON from '@/assets/city/city.json'

const footprintStore = useFootprintStore()
const userStore = useUserStore()

// 省 code -> 省名称映射
const cityMap = {
  110000: '北京市',
  120000: '天津市',
  130000: '河北省',
  140000: '山西省',
  150000: '内蒙古自治区',
  210000: '辽宁省',
  220000: '吉林省',
  230000: '黑龙江省',
  310000: '上海市',
  320000: '江苏省',
  330000: '浙江省',
  340000: '安徽省',
  350000: '福建省',
  360000: '江西省',
  370000: '山东省',
  410000: '河南省',
  420000: '湖北省',
  430000: '湖南省',
  440000: '广东省',
  450000: '广西壮族自治区',
  460000: '海南省',
  500000: '重庆市',
  510000: '四川省',
  520000: '贵州省',
  530000: '云南省',
  540000: '西藏自治区',
  610000: '陕西省',
  620000: '甘肃省',
  630000: '青海省',
  640000: '宁夏回族自治区',
  650000: '新疆维吾尔自治区',
  710000: '台湾省',
  810000: '香港特别行政区',
  820000: '澳门特别行政区'
}

// 通过 feature 获取省 adcode（非省级用 acroutes[1]，省级用自身 adcode）
const getProvinceAdcode = (feature) => {
  if (!feature) return null
  if (feature.properties.level === 'province') return feature.properties.adcode
  const acroutes = feature.properties.acroutes
  return acroutes && acroutes.length > 1 ? acroutes[1] : null
}

// 通过 city_code 反查省名
const getProvinceName = (cityCode) => {
  const feature = cityGeoJSON.features.find(
    (f) => f.properties.adcode === Number(cityCode)
  )
  if (!feature) return ''
  const provinceAdcode = getProvinceAdcode(feature)
  return provinceAdcode ? cityMap[provinceAdcode] || '' : ''
}

// 省列表：从所有 feature 的 acroutes 中收集（仅有台湾省是 level=province）
const provinceList = computed(() => {
  const adcodes = new Set()
  cityGeoJSON.features.forEach((f) => {
    const adcode = getProvinceAdcode(f)
    if (adcode && cityMap[adcode]) adcodes.add(adcode)
  })
  return Array.from(adcodes)
    .map((adcode) => ({ adcode, name: cityMap[adcode] }))
    .sort((a, b) => a.adcode - b.adcode)
})

// 选中省后获取市列表：有市级子级则列市级，否则列省自身（直辖市/特区/台湾）
const getCityList = (provinceAdcode) => {
  if (!provinceAdcode) return []
  const cities = cityGeoJSON.features
    .filter(
      (f) =>
        f.properties.level === 'city' &&
        f.properties.parent &&
        f.properties.parent.adcode === provinceAdcode
    )
    .map((f) => ({
      adcode: f.properties.adcode,
      name: f.properties.name
    }))
    .sort((a, b) => a.adcode - b.adcode)
  if (cities.length > 0) return cities
  const name = cityMap[provinceAdcode]
  return name ? [{ adcode: provinceAdcode, name }] : []
}

/* ---- 搜索 & 分页 ---- */
const searchQuery = ref('')
const page = ref(1)
const size = ref(15)

const load = () => {
  footprintStore.fetchList({
    page: page.value,
    pageSize: size.value,
    cityName: searchQuery.value.trim() || undefined
  })
}

const handleSearch = () => {
  page.value = 1
  load()
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

/* ---- 主表格 ---- */
const selected = ref([])
const handleSelectionChange = (rows) => {
  selected.value = rows
}

/* ---- 新增/编辑弹窗 ---- */
const dialogVisible = ref(false)
const isEditing = ref(false)
const cityOptions = ref([])

const form = ref({
  id: null,
  cityCode: '',
  cityName: '',
  visitTime: '',
  isVisible: 1
})

const selectedProvinceAdcode = ref(null)

const openDialog = (row = null) => {
  isEditing.value = !!row
  if (row) {
    form.value = {
      id: row.id,
      cityCode: row.cityCode,
      cityName: row.cityName,
      visitTime: row.visitTime ?? '',
      isVisible: row.isVisible ?? 1
    }
    const feature = cityGeoJSON.features.find(
      (f) => f.properties.adcode === Number(row.cityCode)
    )
    const provinceAdcode = getProvinceAdcode(feature)
    selectedProvinceAdcode.value = provinceAdcode
    cityOptions.value = getCityList(provinceAdcode)
  } else {
    form.value = {
      id: null,
      cityCode: '',
      cityName: '',
      visitTime: '',
      isVisible: 1
    }
    selectedProvinceAdcode.value = null
    cityOptions.value = []
  }
  dialogVisible.value = true
}

const onProvinceChange = (adcode) => {
  selectedProvinceAdcode.value = adcode
  form.value.cityCode = ''
  form.value.cityName = ''
  cityOptions.value = getCityList(adcode)
}

const onCityChange = (adcode) => {
  const city = cityOptions.value.find((c) => c.adcode === adcode)
  if (city) {
    form.value.cityCode = String(city.adcode)
    form.value.cityName = city.name
  }
}

const saving = ref(false)
const handleSave = async () => {
  if (!form.value.cityCode) return ElMessage.warning('请选择城市')
  saving.value = true
  try {
    await footprintStore.saveFootprint({ ...form.value })
    ElMessage.success(isEditing.value ? '修改成功' : '创建成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

/* ---- 删除 ---- */
const deleteOne = async (row) => {
  await ElMessageBox.confirm(
    `确认删除城市「${row.cityName}」的足迹？`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  )
  await footprintStore.removeFootprints([row.id])
  ElMessage.success('删除成功')
  load()
}

const batchDelete = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择足迹')
  await ElMessageBox.confirm(
    `确认删除选中的 ${selected.value.length} 条足迹？`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  )
  await footprintStore.removeFootprints(selected.value.map((r) => r.id))
  ElMessage.success('批量删除成功')
  load()
}

/* ---- 图片详情弹窗 ---- */
const imageDialogVisible = ref(false)
const imageCityId = ref(null)
const imageCityName = ref('')
const uploadingImage = ref(false)
const imageSaving = ref(false)

const imageFormVisible = ref(false)
const isEditingImage = ref(false)
const imageForm = ref({
  id: null,
  cityId: null,
  imageUrl: '',
  sort: null,
  isVisible: 1
})

const openImageDialog = (row) => {
  imageCityId.value = row.id
  imageCityName.value = row.cityName
  footprintStore.fetchCityImages(row.id)
  imageFormVisible.value = false
  imageDialogVisible.value = true
}

const handleImageUpload = async (options) => {
  uploadingImage.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    await footprintStore.saveCityImage({
      cityId: imageCityId.value,
      imageUrl: res.data,
      sort: null,
      isVisible: 1
    })
    ElMessage.success('图片上传成功')
  } finally {
    uploadingImage.value = false
  }
}

const openImageForm = (row = null) => {
  isEditingImage.value = !!row
  imageForm.value = row
    ? {
        id: row.id,
        cityId: row.cityId,
        imageUrl: row.imageUrl ?? '',
        sort: row.sort ?? null,
        isVisible: row.isVisible ?? 1
      }
    : {
        id: null,
        cityId: imageCityId.value,
        imageUrl: '',
        sort: null,
        isVisible: 1
      }
  imageFormVisible.value = true
}

const handleImageSave = async () => {
  if (!imageForm.value.imageUrl.trim())
    return ElMessage.warning('图片URL不能为空')
  imageSaving.value = true
  try {
    await footprintStore.saveCityImage({ ...imageForm.value })
    ElMessage.success(isEditingImage.value ? '修改成功' : '添加成功')
    imageFormVisible.value = false
  } finally {
    imageSaving.value = false
  }
}

const deleteImage = async (row) => {
  await ElMessageBox.confirm('确认删除该图片？', '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await footprintStore.removeCityImage(row.id, row.cityId)
  ElMessage.success('删除成功')
}

onMounted(() => {
  if (!userStore.isGuest) load()
})
</script>

<template>
  <div class="footprint-page">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchQuery"
          placeholder="搜索城市..."
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <span class="iconfont icon-search" />
          </template>
        </el-input>
        <el-button @click="handleSearch">查询</el-button>
      </div>
      <div class="toolbar-right">
        <el-button plain :disabled="!selected.length" @click="batchDelete">
          <span class="iconfont icon-delete" />
          批量删除
        </el-button>
        <el-button type="primary" @click="openDialog()">
          <span class="iconfont icon-plus" />
          新增足迹
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div v-loading="footprintStore.loading" class="table-wrap">
      <el-table
        :data="footprintStore.list"
        border
        stripe
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column label="省份" min-width="120">
          <template #default="{ row }">
            {{ getProvinceName(row.cityCode) }}
          </template>
        </el-table-column>
        <el-table-column prop="cityName" label="城市" min-width="120" />
        <el-table-column label="访问时间" width="140" align="center">
          <template #default="{ row }">
            {{ row.visitTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="可见" width="75" align="center">
          <template #default="{ row }">
            <span>{{ row.isVisible ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div class="row-actions">
              <el-button link size="small" @click="openImageDialog(row)">
                图集
              </el-button>
              <el-divider direction="vertical" />
              <el-button link size="small" @click="openDialog(row)">
                编辑
              </el-button>
              <el-divider direction="vertical" />
              <el-button link size="small" @click="deleteOne(row)">
                删除
              </el-button>
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
        :total="footprintStore.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑足迹' : '新增足迹'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="80px" class="dialog-form">
        <el-form-item label="省份" required>
          <el-select
            :model-value="selectedProvinceAdcode"
            placeholder="请选择省份"
            style="width: 100%"
            @change="onProvinceChange"
          >
            <el-option
              v-for="p in provinceList"
              :key="p.adcode"
              :label="p.name"
              :value="p.adcode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="城市" required>
          <el-select
            :model-value="Number(form.cityCode) || ''"
            placeholder="请先选择省份"
            style="width: 100%"
            @change="onCityChange"
          >
            <el-option
              v-for="c in cityOptions"
              :key="c.adcode"
              :label="c.name"
              :value="c.adcode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="访问时间">
          <el-date-picker
            v-model="form.visitTime"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch
            v-model="form.isVisible"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave"
          >确认</el-button
        >
      </template>
    </el-dialog>

    <!-- 图片详情弹窗 -->
    <el-dialog
      v-model="imageDialogVisible"
      :title="`${imageCityName} — 图片管理`"
      width="720px"
      :close-on-click-modal="false"
      @close="imageFormVisible = false"
    >
      <div class="image-toolbar">
        <el-upload
          :show-file-list="false"
          :http-request="handleImageUpload"
          accept="image/*"
        >
          <el-button size="small" :loading="uploadingImage">
            <span class="iconfont icon-upload" />上传图片
          </el-button>
        </el-upload>
        <el-button size="small" type="primary" @click="openImageForm()">
          <span class="iconfont icon-plus" />手动添加
        </el-button>
      </div>

      <div v-if="imageFormVisible" class="image-form-box">
        <el-form :model="imageForm" label-width="80px" inline>
          <el-form-item label="图片URL" required>
            <el-input
              v-model="imageForm.imageUrl"
              placeholder="输入图片URL"
              style="width: 280px"
              clearable
            />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number
              v-model="imageForm.sort"
              :min="0"
              :precision="0"
              controls-position="right"
              style="width: 100px"
            />
          </el-form-item>
          <el-form-item label="可见">
            <el-switch
              v-model="imageForm.isVisible"
              :active-value="1"
              :inactive-value="0"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="small"
              :loading="imageSaving"
              @click="handleImageSave"
            >
              保存
            </el-button>
            <el-button size="small" @click="imageFormVisible = false">
              取消
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-loading="footprintStore.imagesLoading" class="image-grid">
        <div
          v-for="img in footprintStore.images"
          :key="img.id"
          class="image-card"
        >
          <el-image
            :src="img.imageUrl"
            :preview-src-list="footprintStore.images.map((i) => i.imageUrl)"
            :initial-index="footprintStore.images.indexOf(img)"
            fit="cover"
            class="image-thumb"
          />
          <div class="image-card-info">
            <span class="image-sort">排序: {{ img.sort ?? '-' }}</span>
            <span class="image-visible">{{
              img.isVisible ? '可见' : '隐藏'
            }}</span>
          </div>
          <div class="image-card-actions">
            <el-button link size="small" @click="openImageForm(img)">
              编辑
            </el-button>
            <el-divider direction="vertical" />
            <el-button link size="small" @click="deleteImage(img)">
              删除
            </el-button>
          </div>
        </div>
        <div
          v-if="!footprintStore.imagesLoading && !footprintStore.images.length"
          class="image-empty"
        >
          暂无图片，请上传或手动添加
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.footprint-page {
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
}

.toolbar-right {
  display: flex;
  align-items: center;
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

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
}

.row-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-form {
  padding: 4px 0;
}

/* ---- 图片弹窗样式 ---- */
.image-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.image-toolbar .iconfont {
  font-size: 13px;
  margin-right: 4px;
}

.image-form-box {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 12px 16px;
  margin-bottom: 16px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.image-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.image-thumb {
  width: 100%;
  height: 160px;
  object-fit: cover;
  display: block;
}

.image-card-info {
  display: flex;
  justify-content: space-between;
  padding: 8px 10px 4px;
  font-size: 12px;
  color: #909399;
}

.image-card-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 10px 8px;
}

.image-empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 32px 0;
  color: #c0c4cc;
  font-size: 14px;
}
</style>
