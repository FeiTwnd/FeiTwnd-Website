<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import cityGeoJSON from '@/assets/city/city.json'
import { getVisibleFootprints, getCityImages } from '@/api/footprint'
import { useThemeStore } from '@/stores'

const themeStore = useThemeStore()
const isDark = computed(() => {
  if (themeStore.mode === 'dark') return true
  if (themeStore.mode === 'light') return false
  return window.matchMedia('(prefers-color-scheme: dark)').matches
})

const provinceMap = {
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

const getProvinceAdcode = (feature) => {
  if (!feature) return null
  if (feature.properties.level === 'province') return feature.properties.adcode
  const acroutes = feature.properties.acroutes
  return acroutes && acroutes.length > 1 ? acroutes[1] : null
}

const SPECIAL_ADCODES = new Set([
  110000, 120000, 310000, 500000, 710000, 810000, 820000
])

const buildMapGeoJSON = () => {
  const features = []
  const specialGroups = {}

  for (const f of cityGeoJSON.features) {
    const provinceCode = getProvinceAdcode(f)
    if (SPECIAL_ADCODES.has(provinceCode)) {
      if (!specialGroups[provinceCode]) specialGroups[provinceCode] = []
      specialGroups[provinceCode].push(f)
    } else if (f.properties.level === 'city') {
      features.push({
        type: 'Feature',
        properties: { name: String(f.properties.adcode) },
        geometry: f.geometry
      })
    }
  }

  for (const [code, subFeatures] of Object.entries(specialGroups)) {
    const allCoords = []
    for (const f of subFeatures) {
      if (f.geometry.type === 'Polygon') allCoords.push(f.geometry.coordinates)
      else if (f.geometry.type === 'MultiPolygon')
        allCoords.push(...f.geometry.coordinates)
    }
    features.push({
      type: 'Feature',
      properties: { name: String(code) },
      geometry: { type: 'MultiPolygon', coordinates: allCoords }
    })
  }

  return { type: 'FeatureCollection', features }
}

const footprints = ref([])
const chartRef = ref(null)
let chart = null
let resizeObserver = null

// adcode -> { cityName, provinceName }
const nameMap = new Map()
for (const f of cityGeoJSON.features) {
  const code = f.properties.adcode
  const provinceCode = getProvinceAdcode(f)
  const provinceName = provinceCode ? provinceMap[provinceCode] || '' : ''
  nameMap.set(code, { cityName: f.properties.name, provinceName })
}
for (const [code, name] of Object.entries(provinceMap)) {
  if (!nameMap.has(Number(code))) {
    nameMap.set(Number(code), { cityName: name, provinceName: name })
  }
}

const formatDate = (d) => {
  if (!d) return ''
  const [y, m, day] = d.split('-')
  return `${parseInt(y)}年${parseInt(m)}月${parseInt(day)}日`
}

/* ---- 统计 ---- */
const visitedCities = computed(() => footprints.value.length)

const visitedProvinces = computed(() => {
  const provinces = new Set()
  footprints.value.forEach((f) => {
    const info = nameMap.get(Number(f.cityCode))
    if (info?.provinceName) provinces.add(info.provinceName)
  })
  return provinces.size
})

/* ---- 图片弹窗 ---- */
const imageVisible = ref(false)
const imageCityName = ref('')
const imageList = ref([])
const imageLoading = ref(false)

const openImages = async (cityCode, cityName) => {
  const footprint = footprints.value.find(
    (f) => String(f.cityCode) === String(cityCode)
  )
  if (!footprint) return
  imageCityName.value = cityName || footprint.cityName
  imageVisible.value = true
  imageLoading.value = true
  try {
    const res = await getCityImages(footprint.id)
    imageList.value = res.data?.data ?? []
  } finally {
    imageLoading.value = false
  }
}

const getChartOptions = () => {
  const dark = isDark.value
  return {
    backgroundColor: dark ? '#181818' : '#fff',
    tooltip: {
      trigger: 'item',
      backgroundColor: dark ? '#232323' : '#fff',
      borderColor: dark ? '#333' : '#e4e7ed',
      borderWidth: 1,
      padding: [12, 16],
      extraCssText: dark
        ? 'border-radius:6px;box-shadow:0 2px 12px rgba(0,0,0,0.3);'
        : 'border-radius:6px;box-shadow:0 2px 12px rgba(0,0,0,0.06);',
      formatter: (params) => {
        const adcode = Number(params.name)
        const info = nameMap.get(adcode)
        if (!info) return params.name
        const sameAsProvince = info.provinceName === info.cityName
        const fp = footprints.value.find(
          (f) => String(f.cityCode) === String(adcode)
        )
        let html = `<span style="font-family:'Noto Serif SC',Georgia,serif;font-size:15px;font-weight:600;color:${dark ? '#e5e5e5' : '#303133'};">${info.cityName}</span>`
        if (!sameAsProvince) {
          html += `<br/><span style="font-size:12px;color:${dark ? '#808080' : '#909399'};">${info.provinceName}</span>`
        }
        if (fp?.visitTime) {
          html += `<br/><span style="font-size:12px;color:${dark ? '#808080' : '#909399'};">${formatDate(fp.visitTime)}</span>`
        }
        return html
      }
    },
    series: [
      {
        type: 'map',
        map: 'china-cities',
        roam: false,
        selectedMode: false,
        zoom: 1.6,
        center: [104.5, 36],
        aspectScale: 0.85,
        label: { show: false },
        itemStyle: {
          areaColor: dark ? '#252525' : '#ededed',
          borderColor: dark ? '#333' : '#d4d4d4',
          borderWidth: 0.6
        },
        emphasis: {
          label: { show: false },
          itemStyle: { areaColor: dark ? '#2e2e2e' : '#e0e0e0' }
        },
        data: footprints.value.map((f) => ({
          name: String(f.cityCode),
          itemStyle: {
            areaColor: dark ? '#666' : '#5a5a5a',
            borderColor: dark ? '#777' : '#888'
          },
          emphasis: {
            itemStyle: { areaColor: dark ? '#808080' : '#404040' }
          }
        }))
      }
    ]
  }
}

const initChart = () => {
  if (!chartRef.value) return
  echarts.registerMap('china-cities', buildMapGeoJSON())
  chart = echarts.init(chartRef.value)
  chart.setOption(getChartOptions())

  chart.on('click', 'map', (params) => {
    const code = String(params.name)
    const footprint = footprints.value.find((f) => String(f.cityCode) === code)
    if (footprint) {
      const info = nameMap.get(Number(code))
      openImages(code, info ? info.cityName : footprint.cityName)
    }
  })
}

onMounted(async () => {
  document.documentElement.classList.add('footprint-page')
  document.title = '足迹 - FeiTwnd'

  await nextTick()
  initChart()

  resizeObserver = new ResizeObserver(() => chart?.resize())
  if (chartRef.value) resizeObserver.observe(chartRef.value)

  try {
    const res = await getVisibleFootprints()
    footprints.value = res.data?.data ?? []
    chart?.setOption(getChartOptions(), true)
  } catch {
    /* ignore */
  }
})

const themeWatchStop = watch(isDark, () => {
  if (chart) chart.setOption(getChartOptions(), true)
})

onUnmounted(() => {
  document.documentElement.classList.remove('footprint-page')
  resizeObserver?.disconnect()
  themeWatchStop?.()
  chart?.dispose()
})
</script>

<template>
  <div class="footprint-fullpage" :class="{ dark: isDark }">
    <div ref="chartRef" class="map-full" />

    <!-- 底栏 -->
    <div class="map-footer">
      <div class="footer-stat">
        <span class="stat-num">{{ visitedCities }}</span>
        <span class="stat-label">座城市</span>
      </div>
      <div class="footer-divider" />
      <div class="footer-stat">
        <span class="stat-num">{{ visitedProvinces }}</span>
        <span class="stat-label">个省</span>
      </div>
      <div class="footer-legend">
        <span class="legend-dot visited" />
        <span class="legend-label">已访</span>
        <span class="legend-dot" />
        <span class="legend-label">未访</span>
      </div>
    </div>

    <Teleport to="body">
      <Transition name="img-fade">
        <div
          v-if="imageVisible"
          class="image-overlay"
          @click.self="imageVisible = false"
        >
          <div class="image-dialog">
            <div class="image-dialog-header">
              <h3>{{ imageCityName }}</h3>
              <button class="image-close" @click="imageVisible = false">
                <svg
                  viewBox="0 0 24 24"
                  width="20"
                  height="20"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </svg>
              </button>
            </div>
            <div v-loading="imageLoading" class="image-dialog-body">
              <div
                v-if="!imageList.length && !imageLoading"
                class="image-empty"
              >
                暂无图片
              </div>
              <div class="image-grid">
                <div v-for="img in imageList" :key="img.id" class="image-item">
                  <img
                    :src="img.imageUrl"
                    :alt="imageCityName"
                    loading="lazy"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style>
html.footprint-page,
html.footprint-page body {
  background: #fff !important;
}
html.dark.footprint-page,
html.dark.footprint-page body {
  background: #181818 !important;
}
</style>

<style scoped>
.footprint-fullpage {
  position: fixed;
  inset: 0;
  background: #fff;
  user-select: none;
}
.footprint-fullpage.dark {
  background: #181818;
}
.map-full {
  width: 100%;
  height: 100%;
}

/* ---- 底栏 ---- */
.map-footer {
  position: absolute;
  bottom: 28px;
  right: 32px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition:
    background-color 0.3s,
    border-color 0.3s,
    box-shadow 0.3s;
}
.dark .map-footer {
  background: rgba(35, 35, 35, 0.92);
  border-color: #333;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

.footer-stat {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.stat-num {
  font-family: 'Noto Serif SC', Georgia, 'Times New Roman', serif;
  font-size: 22px;
  font-weight: 600;
  line-height: 1;
  color: #303133;
  transition: color 0.3s;
}
.dark .stat-num {
  color: #e5e5e5;
}
.stat-label {
  font-size: 12px;
  color: #909399;
  transition: color 0.3s;
}
.dark .stat-label {
  color: #808080;
}

.footer-divider {
  width: 1px;
  height: 20px;
  background: #e4e7ed;
  transition: background-color 0.3s;
}
.dark .footer-divider {
  background: #333;
}

.footer-legend {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-left: 4px;
}
.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
  background: #ededed;
  border: 0.6px solid #d4d4d4;
  transition:
    background-color 0.3s,
    border-color 0.3s;
}
.legend-dot.visited {
  background: #5a5a5a;
  border-color: #888;
}
.dark .legend-dot {
  background: #252525;
  border-color: #333;
}
.dark .legend-dot.visited {
  background: #666;
  border-color: #777;
}
.legend-label {
  font-size: 11px;
  color: #909399;
  transition: color 0.3s;
}
.dark .legend-label {
  color: #808080;
}

/* ---- 图片弹窗 ---- */
.image-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}
.image-dialog {
  background: #fff;
  border-radius: 12px;
  width: 100%;
  max-width: 760px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.image-dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}
.image-dialog-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.image-close {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #909399;
  border-radius: 4px;
  display: flex;
  transition:
    color 0.15s,
    background 0.15s;
}
.image-close:hover {
  color: #303133;
  background: #f5f7fa;
}
.image-dialog-body {
  padding: 16px 20px 20px;
  overflow-y: auto;
  min-height: 100px;
}
.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}
.image-item {
  border-radius: 6px;
  overflow: hidden;
  aspect-ratio: 4 / 3;
  background: #f5f7fa;
}
.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.image-empty {
  text-align: center;
  padding: 32px 0;
  color: #909399;
  font-size: 14px;
}

.img-fade-enter-active,
.img-fade-leave-active {
  transition: opacity 0.2s;
}
.img-fade-enter-from,
.img-fade-leave-to {
  opacity: 0;
}

@media (max-width: 600px) {
  .map-footer {
    bottom: 16px;
    right: 16px;
    gap: 12px;
    padding: 10px 16px;
  }
  .stat-num {
    font-size: 18px;
  }
  .image-dialog {
    max-height: 90vh;
    border-radius: 8px;
  }
  .image-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
