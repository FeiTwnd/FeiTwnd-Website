<script setup>
import { ref, onMounted } from 'vue'
import { useOperationLogStore } from '@/stores'
import dayjs from 'dayjs'

const logStore = useOperationLogStore()

/* ---- 搜索 ---- */
const searchForm = ref({
  operationTarget: '',
  operationType: '',
  startTime: '',
  endTime: ''
})
const page = ref(1)
const size = ref(15)
const selected = ref([])

/** 操作类型选项（与后端枚举对齐） */
const operationTypeOptions = [
  { label: '新增', value: 'INSERT' },
  { label: '更新', value: 'UPDATE' },
  { label: '删除', value: 'DELETE' }
]

const load = () => {
  logStore.fetchList({
    page: page.value,
    pageSize: size.value,
    ...searchForm.value
  })
}

const handleSearch = () => {
  page.value = 1
  load()
}

const handleReset = () => {
  searchForm.value = {
    operationTarget: '',
    operationType: '',
    startTime: '',
    endTime: ''
  }
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
  await ElMessageBox.confirm('确认删除该条操作日志？', '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await logStore.remove([row.id])
  ElMessage.success('删除成功')
  load()
}

const batchDelete = async () => {
  if (!selected.value.length) return ElMessage.warning('请先选择日志')
  await ElMessageBox.confirm(
    `确认删除选中的 ${selected.value.length} 条日志？`,
    '警告',
    { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  )
  await logStore.remove(selected.value.map((r) => r.id))
  ElMessage.success('批量删除成功')
  load()
}

/** 格式化时间 */
const fmtDate = (d) => (d ? dayjs(d).format('YYYY-MM-DD HH:mm:ss') : '-')

const resultLabel = (r) => (r === 1 ? '成功' : '失败')

const typeLabel = (t) => {
  const m = { INSERT: '新增', UPDATE: '更新', DELETE: '删除' }
  return m[t] ?? t
}

onMounted(load)
</script>

<template>
  <div class="log-page">
    <!-- 搜索栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchForm.operationTarget"
          placeholder="操作对象"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <span class="iconfont icon-search" />
          </template>
        </el-input>
        <el-select
          v-model="searchForm.operationType"
          placeholder="全部操作"
          clearable
          class="select-sm"
        >
          <el-option
            v-for="opt in operationTypeOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
        <el-date-picker
          v-model="searchForm.startTime"
          type="datetime"
          placeholder="开始时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
          clearable
          class="date-picker-sm"
        />
        <el-date-picker
          v-model="searchForm.endTime"
          type="datetime"
          placeholder="结束时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
          clearable
          class="date-picker-sm"
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
    <div v-loading="logStore.loading" class="table-wrap">
      <el-table
        :data="logStore.list"
        border
        stripe
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="操作类型" width="100" align="center">
          <template #default="{ row }">
            {{ typeLabel(row.operationType) }}
          </template>
        </el-table-column>
        <el-table-column prop="operationTarget" label="操作对象" width="160" />
        <el-table-column
          prop="targetId"
          label="目标 ID"
          width="120"
          align="center"
          show-overflow-tooltip
        />
        <el-table-column
          prop="operateData"
          label="操作数据"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column label="操作结果" width="90" align="center">
          <template #default="{ row }">
            <span>{{ resultLabel(row.result) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="errorMessage"
          label="错误信息"
          min-width="160"
          show-overflow-tooltip
        />
        <el-table-column label="操作时间" width="180" align="center">
          <template #default="{ row }">{{
            fmtDate(row.operationTime)
          }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="deleteOne(row)">
              删除
            </el-button>
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
        :total="logStore.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.log-page {
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

.date-picker-sm {
  width: 170px;
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
