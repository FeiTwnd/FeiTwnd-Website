<script setup>
import { ref, onMounted, shallowRef } from 'vue'
import {
  getOverview,
  getViewStatistics,
  getVisitorStatistics,
  getArticleViewTop10,
  getProvinceDistribution
} from '@/api/report'
import { getConfigByKey } from '@/api/settings'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

/* ---- 概览数据 ---- */
const overview = ref({})
const loadingOverview = ref(false)

const statCards = [
  { key: 'totalViewCount', label: '总浏览量', icon: 'icon-eye' },
  { key: 'totalVisitorCount', label: '总访客数', icon: 'icon-user' },
  { key: 'todayViewCount', label: '今日浏览', icon: 'icon-today' },
  { key: 'todayNewVisitorCount', label: '今日新访客', icon: 'icon-new' },
  { key: 'totalArticleCount', label: '文章总数', icon: 'icon-article' },
  { key: 'totalCommentCount', label: '评论总数', icon: 'icon-comment' },
  { key: 'pendingCommentCount', label: '待审评论', icon: 'icon-pending' }
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

/* ---- 运行天数 ---- */
const runDays = ref(null)
const runStartDate = ref('')

const fetchRunDays = async () => {
  try {
    const res = await getConfigByKey('start-time')
    const dateStr = res.data?.configValue ?? ''
    if (dateStr) {
      runStartDate.value = dateStr
      const start = dayjs(dateStr)
      runDays.value = dayjs().diff(start, 'day')
    }
  } catch (e) {
    console.error('获取运行天数失败', e)
  }
}

/* ---- 折线图配置 ---- */
const viewChartEl = ref(null)
const visitorChartEl = ref(null)
const barChartEl = ref(null)
const pieChartEl = ref(null)
const viewChart = shallowRef(null)
const visitorChart = shallowRef(null)
const barChart = shallowRef(null)
const pieChart = shallowRef(null)

const makeShortcuts = () => [
  {
    text: '最近 7 天',
    value: () => [dayjs().subtract(6, 'day').toDate(), new Date()]
  },
  {
    text: '最近 30 天',
    value: () => [dayjs().subtract(29, 'day').toDate(), new Date()]
  }
]

const defaultRange = () => [
  dayjs().subtract(6, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
]

const viewDateRange = ref(defaultRange())
const visitorDateRange = ref(defaultRange())

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
  series: [
    {
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
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: color + '33' },
            { offset: 1, color: color + '00' }
          ]
        }
      }
    }
  ]
})

/* 浏览量折线图（独立日期） */
const fetchViewChart = async () => {
  const [begin, end] = viewDateRange.value
  const res = await getViewStatistics({ begin, end })
  const data = res.data ?? []
  viewChart.value?.setOption(
    makeLineOption(
      '浏览量',
      data.map((d) => d.date),
      data.map((d) => d.count),
      '#000000'
    )
  )
}

/* 访客折线图（独立日期） */
const fetchVisitorChart = async () => {
  const [begin, end] = visitorDateRange.value
  const res = await getVisitorStatistics({ begin, end })
  const data = res.data ?? []
  visitorChart.value?.setOption(
    makeLineOption(
      '访客数',
      data.map((d) => d.date),
      data.map((d) => d.count),
      '#606266'
    )
  )
}

/* 文章阅读量 TOP10 柱状图 */
const fetchBarChart = async () => {
  const res = await getArticleViewTop10()
  const data = (res.data ?? []).slice(0, 10).reverse()
  barChart.value?.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 130, right: 20, top: 16, bottom: 24 },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f0f0f0' } },
      axisLabel: { color: '#909399', fontSize: 11 }
    },
    yAxis: {
      type: 'category',
      data: data.map((d) =>
        d.title && d.title.length > 14 ? d.title.slice(0, 14) + '…' : d.title
      ),
      axisLabel: { color: '#606266', fontSize: 12 },
      axisLine: { lineStyle: { color: '#e4e7ed' } }
    },
    series: [
      {
        name: '阅读量',
        type: 'bar',
        data: data.map((d) => d.viewCount ?? d.count),
        barMaxWidth: 20,
        itemStyle: { color: '#303133', borderRadius: [0, 4, 4, 0] }
      }
    ]
  })
}

/* 访客省份饼图 */
const fetchPieChart = async () => {
  const res = await getProvinceDistribution()
  const raw = res.data ?? []
  const pieData = raw.map((d) => ({
    name: d.province || '未知',
    value: d.count
  }))
  pieChart.value?.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { color: '#606266', fontSize: 12 },
      icon: 'circle'
    },
    series: [
      {
        name: '访客省份',
        type: 'pie',
        radius: ['40%', '68%'],
        center: ['38%', '50%'],
        avoidLabelOverlap: true,
        label: { show: false },
        labelLine: { show: false },
        data: pieData,
        itemStyle: { borderColor: '#fff', borderWidth: 2 }
      }
    ]
  })
}

const initCharts = () => {
  viewChart.value = echarts.init(viewChartEl.value)
  visitorChart.value = echarts.init(visitorChartEl.value)
  barChart.value = echarts.init(barChartEl.value)
  pieChart.value = echarts.init(pieChartEl.value)
  fetchViewChart()
  fetchVisitorChart()
  fetchBarChart()
  fetchPieChart()
}

onMounted(() => {
  fetchOverview()
  fetchRunDays()
  initCharts()
})
</script>

<template>
  <div class="dashboard">
    <!-- 概览卡片 -->
    <div v-loading="loadingOverview" class="stat-grid">
      <div v-for="card in statCards" :key="card.key" class="stat-card">
        <div class="stat-icon">
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
            v-model="viewDateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="makeShortcuts()"
            size="small"
            @change="fetchViewChart"
          />
        </div>
        <div ref="viewChartEl" class="chart-body" />
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">访客数趋势</span>
          <el-date-picker
            v-model="visitorDateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="makeShortcuts()"
            size="small"
            @change="fetchVisitorChart"
          />
        </div>
        <div ref="visitorChartEl" class="chart-body" />
      </div>
    </div>

    <!-- 柱状图 + 饼图 -->
    <div class="chart-row">
      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">阅读量 TOP 10</span>
        </div>
        <div ref="barChartEl" class="chart-body" />
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">访客省份分布</span>
        </div>
        <div ref="pieChartEl" class="chart-body" />
      </div>
    </div>

    <!-- 网站运行时长 -->
    <div v-if="runDays !== null" class="run-banner">
      <div class="run-banner-inner">
        <span class="run-icon iconfont icon-time" />
        <span class="run-text">本站已稳定运行 </span>
        <span class="run-days">{{ runDays }}</span>
        <span class="run-unit">天</span>
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

.run-banner {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 10px 28px;
}

.run-banner-inner {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.run-icon {
  font-size: 20px;
  color: #303133;
  margin-right: 2px;
}

.run-text {
  font-size: 15px;
  color: #606266;
}

.run-days {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
  margin: 0 2px;
}

.run-unit {
  font-size: 15px;
  color: #606266;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 900px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
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
