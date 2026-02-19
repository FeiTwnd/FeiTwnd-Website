<script setup>
import { ref, onMounted } from 'vue'
import { useAnalyticsStore } from '@/stores'
import dayjs from 'dayjs'

const analyticsStore = useAnalyticsStore()

const searchForm = ref({ pagePath: '', visitorId: '' })
const page = ref(1)
const size = ref(15)
const selected = ref([])

const load = () => {
  analyticsStore.fetchViewList({
    page: page.value,
    pageSize: size.value,
    pagePath: searchForm.value.pagePath || undefined,
    visitorId: searchForm.value.visitorId || undefined
  })
}

const handleSearch = () => {
  page.value = 1
  load()
}

const handleReset = () => {
  searchForm.value = { pagePath: '', visitorId: '' }
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
  await ElMessageBox.confirm('确认删除该条浏览记录？', '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await analyticsStore.removeViewRecords([row.id])
  ElMessage.success('删除成功')
  load()
}

const batchDelete = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择记录')
  await ElMessageBox.confirm(
    `确认删除选中的 ${selected.value.length} 条浏览记录？`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  )
  await analyticsStore.removeViewRecords(selected.value.map((r) => r.id))
  ElMessage.success('批量删除成功')
  load()
}

const fmtDate = (d) => (d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '-')

onMounted(load)
</script>

<template>
  <div class="view-record-page">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchForm.pagePath"
          placeholder="搜索页面路径"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <span class="iconfont icon-search" />
          </template>
        </el-input>
        <el-input
          v-model="searchForm.visitorId"
          placeholder="访客 ID"
          clearable
          class="search-input-sm"
          @keyup.enter="handleSearch"
        />
        <el-button @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button plain :disabled="!selected.length" @click="batchDelete">
          <span class="iconfont icon-delete" />
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div v-loading="analyticsStore.viewLoading" class="table-wrap">
      <el-table
        :data="analyticsStore.viewList"
        border
        stripe
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column
          prop="pageTitle"
          label="页面标题"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="pagePath"
          label="页面路径"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column prop="ipAddress" label="访客 IP" width="150" />
        <el-table-column
          prop="referer"
          label="来源"
          min-width="160"
          show-overflow-tooltip
        />
        <el-table-column label="浏览时间" width="160" align="center">
          <template #default="{ row }">{{ fmtDate(row.viewTime) }}</template>
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
        :total="analyticsStore.viewTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.view-record-page {
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

.search-input-sm {
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
