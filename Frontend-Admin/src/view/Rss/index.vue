<script setup>
import { ref, onMounted } from 'vue'
import { useAnalyticsStore } from '@/stores'
import dayjs from 'dayjs'

const analyticsStore = useAnalyticsStore()

const searchForm = ref({ email: '', isActive: '' })
const page = ref(1)
const size = ref(15)
const selected = ref([])

const load = () => {
  analyticsStore.fetchRssList({
    page: page.value,
    pageSize: size.value,
    email: searchForm.value.email || undefined,
    isActive:
      searchForm.value.isActive === '' ? undefined : searchForm.value.isActive
  })
}

const handleSearch = () => {
  page.value = 1
  load()
}

const handleReset = () => {
  searchForm.value = { email: '', isActive: '' }
  handleSearch()
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

const handleSelectionChange = (rows) => {
  selected.value = rows
}

/* ---- 删除 ---- */
const deleteOne = async (row) => {
  await ElMessageBox.confirm(`确认删除订阅「${row.email}」？`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await analyticsStore.removeRss([row.id])
  ElMessage.success('删除成功')
  load()
}

const batchDelete = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择订阅')
  await ElMessageBox.confirm(
    `确认删除选中的 ${selected.value.length} 条订阅？`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  )
  await analyticsStore.removeRss(selected.value.map((r) => r.id))
  ElMessage.success('批量删除成功')
  load()
}

const fmtDate = (d) => (d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '-')

onMounted(load)
</script>

<template>
  <div class="rss-page">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchForm.email"
          placeholder="搜索订阅邮箱"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <span class="iconfont icon-search" />
          </template>
        </el-input>
        <el-select
          v-model="searchForm.isActive"
          placeholder="全部状态"
          clearable
          class="select-sm"
        >
          <el-option label="已激活" :value="1" />
          <el-option label="未激活" :value="0" />
        </el-select>
        <el-button @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button plain :disabled="!selected.length" @click="batchDelete">
          <!-- ICON: icon-delete -->
          <span class="iconfont icon-delete" />
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div v-loading="analyticsStore.rssLoading" class="table-wrap">
      <el-table
        :data="analyticsStore.rssList"
        border
        stripe
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column
          prop="nickname"
          label="昵称"
          width="130"
          show-overflow-tooltip
        />
        <el-table-column
          prop="email"
          label="订阅邮箱"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <span>{{ row.isActive ? '已激活' : '未激活' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订阅时间" width="160" align="center">
          <template #default="{ row }">{{
            fmtDate(row.subscribeTime)
          }}</template>
        </el-table-column>
        <el-table-column label="取消时间" width="160" align="center">
          <template #default="{ row }">{{
            fmtDate(row.unSubscribeTime) || '-'
          }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="deleteOne(row)"
              >删除</el-button
            >
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
        :total="analyticsStore.rssTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.rss-page {
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

.select-sm {
  width: 130px;
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
</style>
