<script setup>
import { ref, onMounted } from 'vue'
import { useAnalyticsStore } from '@/stores'
import dayjs from 'dayjs'

const analyticsStore = useAnalyticsStore()

const page = ref(1)
const size = ref(20)
const selected = ref([])

const load = () => {
  analyticsStore.fetchViewList({ page: page.value, pageSize: size.value })
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
        <span class="page-heading">
          <!-- ICON: icon-eye -->
          <span class="iconfont icon-eye" />
          浏览记录
        </span>
      </div>
      <div class="toolbar-right">
        <el-button
          type="danger"
          plain
          :disabled="!selected.length"
          @click="batchDelete"
        >
          <!-- ICON: icon-delete -->
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
          prop="articleTitle"
          label="文章标题"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column prop="visitorIp" label="访客 IP" width="150" />
        <el-table-column
          prop="province"
          label="归属地"
          width="130"
          show-overflow-tooltip
        />
        <el-table-column label="浏览时间" width="170" align="center">
          <template #default="{ row }">{{
            fmtDate(row.viewTime ?? row.createTime)
          }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" type="danger" @click="deleteOne(row)"
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
        :page-sizes="[10, 20, 50, 100]"
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
  gap: 12px;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.toolbar-right .iconfont {
  font-size: 14px;
  margin-right: 4px;
}

.page-heading {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.page-heading .iconfont {
  font-size: 18px;
  color: #606266;
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
