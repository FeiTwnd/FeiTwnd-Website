<script setup>
import { ref, onMounted, shallowRef } from 'vue'
import { getOverview, getViewStatistics, getVisitorStatistics } from '@/api/report'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

/* ---- 概览数据 ---- */
const overview = ref({})
const loadingOverview = ref(false)

const statCards = [
  { key: 'totalViewCount',       label: '总浏览量',   icon: 'icon-eye'     },
  { key: 'totalVisitorCount',    label: '总访客数',   icon: 'icon-user'    },
  { key: 'todayViewCount',       label: '今日浏览',   icon: 'icon-today'   },
  { key: 'todayNewVisitorCount', label: '今日新访客', icon: 'icon-new'     },
  { key: 'totalArticleCount',    label: '文章总数',   icon: 'icon-article' },
  { key: 'totalCommentCount',    label: '评论总数',   icon: 'icon-comment' },
  { key: 'pendingCommentCount',  label: '待审评论',   icon: 'icon-pending' }
]

const fetchOverview = async () => {
  loadingOverview.value = true
  try {
    const res = await getOverview()
    overview.value = res.data ?? {}
  } finally {
    loadingOverview.value = false
  }
}

/* ---- 折线图 ---- */
const viewChartEl    = ref(null)
const visitorChartEl = ref(null)
const viewChart      = shallowRef(null)
const visitorChart   = shallowRef(null)

const dateRange = ref([
  dayjs().subtract(6, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
])

const shortcuts = [
  { text: '最近 7 天',  value: () => [dayjs().subtract(6, 'day').toDate(), new Date()] },
  { text: '最近 30 天', value: () => [dayjs().subtract(29, 'day').toDate(), new Date()] }
]

const makeLineOption = (title, categories, data, color) => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 20, top: 30, bottom: 30 },
  xAxis: {
    type: 'category',
    data: categories,
    axisLine: { lineStyle: { color: '#e4e7ed' } },
    axisLabel: { color: '#909399', fontSize: 12 }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#f0f0f0' } },
    axisLabel: { color: '#909399', fontSize: 12 }
  },
  series: [{
    name: title,
    type: 'line',
    data,
    smooth: true,
    symbol: 'circle',
    symbolSize: 5,
    lineStyle: { color, width: 2 },
    itemStyle: { color },
    areaStyle: {
      color: {
        type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: color + '33' },
          { offset: 1, color: color + '00' }
        ]
      }
    }
  }]
})

const fetchCharts = async () => {
  const [begin, end] = dateRange.value
  const [viewRes, visitorRes] = await Promise.all([
    getViewStatistics({ begin, end }),
    getVisitorStatistics({ begin, end })
  ])

  const viewData    = viewRes.data ?? []
  const visitorData = visitorRes.data ?? []
  const categories  = viewData.map(d => d.date)

  viewChart.value?.setOption(makeLineOption('浏览量', categories, viewData.map(d => d.count), '#000000'))
  visitorChart.value?.setOption(makeLineOption('访客数', categories, visitorData.map(d => d.count), '#606266'))
}

const initCharts = () => {
  viewChart.value    = echarts.init(viewChartEl.value)
  visitorChart.value = echarts.init(visitorChartEl.value)
  fetchCharts()
}

onMounted(() => {
  fetchOverview()
  initCharts()
})
</script>

<template>
  <div class="dashboard">
    <!-- 概览卡片 -->
    <div v-loading="loadingOverview" class="stat-grid">
      <div v-for="card in statCards" :key="card.key" class="stat-card">
        <div class="stat-icon">
          <!-- ICON: {{ card.icon }} -->
          <span :class="['iconfont', card.icon]" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ overview[card.key] ?? '-' }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <!-- 折线图 -->
    <div class="chart-row">
      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">浏览量趋势</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="shortcuts"
            size="small"
            @change="fetchCharts"
          />
        </div>
        <div ref="viewChartEl" class="chart-body" />
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">访客数趋势</span>
        </div>
        <div ref="visitorChartEl" class="chart-body" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.stat-card {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px 16px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .iconfont {
  font-size: 22px;
  color: #303133;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 900px) {
  .chart-row { grid-template-columns: 1fr; }
}

.chart-card {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chart-body {
  height: 260px;
}
</style>
