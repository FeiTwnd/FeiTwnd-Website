<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getVisibleFootprints, getCityImages } from '@/api/footprint'
import { useThemeStore } from '@/stores'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()

const isDark = computed(() => {
  if (themeStore.mode === 'dark') return true
  if (themeStore.mode === 'light') return false
  return window.matchMedia('(prefers-color-scheme: dark)').matches
})

const cityId = computed(() => Number(route.params.id))
const cityName = ref('')
const images = ref([])
const loading = ref(true)
const loadError = ref(false)

const leftImages = computed(() => images.value.filter((_, i) => i % 2 === 0))
const rightImages = computed(() => images.value.filter((_, i) => i % 2 === 1))

const shouldAnimate = computed(() => {
  return (
    images.value.length >= 4 &&
    leftImages.value.length > 0 &&
    rightImages.value.length > 0
  )
})

const animDuration = computed(() => {
  const count = Math.max(leftImages.value.length, rightImages.value.length)
  return Math.max(count * 10, 25)
})

/* ---- 暂停控制 ---- */
const leftPaused = ref(false)
const rightPaused = ref(false)

/* ---- 灯箱 ---- */
const lightboxVisible = ref(false)
const lightboxIdx = ref(0)

const openLightbox = (img) => {
  lightboxIdx.value = images.value.indexOf(img)
  lightboxVisible.value = true
}

const closeLightbox = () => {
  lightboxVisible.value = false
}

const goPrev = () => {
  if (lightboxIdx.value > 0) lightboxIdx.value--
}
const goNext = () => {
  if (lightboxIdx.value < images.value.length - 1) lightboxIdx.value++
}

const onKeydown = (e) => {
  if (!lightboxVisible.value) return
  if (e.key === 'Escape') closeLightbox()
  else if (e.key === 'ArrowLeft') goPrev()
  else if (e.key === 'ArrowRight') goNext()
}

watch(lightboxVisible, (v) => {
  if (v) window.addEventListener('keydown', onKeydown)
  else window.removeEventListener('keydown', onKeydown)
})

onMounted(async () => {
  document.documentElement.classList.add('city-gallery-page')
  document.title = '城市图集 - FeiTwnd'

  try {
    const [fpRes, imgRes] = await Promise.all([
      getVisibleFootprints(),
      getCityImages(cityId.value)
    ])
    const footprints = fpRes.data?.data ?? []
    const match = footprints.find((f) => f.id === cityId.value)
    cityName.value = match?.cityName ?? ''
    images.value = imgRes.data?.data ?? []
  } catch {
    loadError.value = true
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  document.documentElement.classList.remove('city-gallery-page')
})

const goBack = () => router.push('/footprint')
</script>

<template>
  <div class="gallery-page" :class="{ dark: isDark }">
    <!-- 顶栏 -->
    <header class="gallery-header">
      <button class="back-btn" @click="goBack" aria-label="返回足迹">
        <svg
          viewBox="0 0 24 24"
          width="22"
          height="22"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <polyline points="15 18 9 12 15 6" />
        </svg>
      </button>
      <h1 class="city-title">{{ cityName || '城市图集' }}</h1>
      <span v-if="images.length" class="img-count">{{ images.length }} 张</span>
    </header>

    <!-- 加载 -->
    <div v-if="loading" class="gallery-status">
      <div class="status-spinner" />
    </div>

    <!-- 错误 -->
    <div v-else-if="loadError" class="gallery-status">
      <p class="status-text">加载失败，请稍后重试</p>
      <button class="status-btn" @click="goBack">返回足迹</button>
    </div>

    <!-- 空数据 -->
    <div v-else-if="!images.length" class="gallery-status">
      <p class="status-text">该城市暂无图集</p>
      <button class="status-btn" @click="goBack">返回足迹</button>
    </div>

    <!-- 图集主体 -->
    <main v-else class="gallery-main">
      <!-- 左列 — 向下滚动 -->
      <div
        class="film-col"
        :class="{ paused: leftPaused }"
        @mouseenter="leftPaused = true"
        @mouseleave="leftPaused = false"
      >
        <div
          class="film-track"
          :class="{
            'anim-down': shouldAnimate && leftImages.length > 0,
            'no-anim': !shouldAnimate || !leftImages.length
          }"
          :style="shouldAnimate ? { '--dur': animDuration + 's' } : {}"
        >
          <template v-if="shouldAnimate && leftImages.length">
            <div
              v-for="img in leftImages"
              :key="'a' + img.id"
              class="film-frame"
              @click="openLightbox(img)"
            >
              <img :src="img.imageUrl" :alt="cityName" loading="lazy" />
            </div>
            <div
              v-for="img in leftImages"
              :key="'b' + img.id"
              class="film-frame"
              @click="openLightbox(img)"
            >
              <img :src="img.imageUrl" :alt="cityName" loading="lazy" />
            </div>
          </template>
          <template v-else>
            <div
              v-for="img in leftImages"
              :key="img.id"
              class="film-frame"
              @click="openLightbox(img)"
            >
              <img :src="img.imageUrl" :alt="cityName" loading="lazy" />
            </div>
          </template>
        </div>
      </div>

      <!-- 右列 — 向上滚动 -->
      <div
        class="film-col right"
        :class="{ paused: rightPaused }"
        @mouseenter="rightPaused = true"
        @mouseleave="rightPaused = false"
      >
        <div
          class="film-track"
          :class="{
            'anim-up': shouldAnimate && rightImages.length > 0,
            'no-anim': !shouldAnimate || !rightImages.length
          }"
          :style="shouldAnimate ? { '--dur': animDuration + 's' } : {}"
        >
          <template v-if="shouldAnimate && rightImages.length">
            <div
              v-for="img in rightImages"
              :key="'a' + img.id"
              class="film-frame"
              @click="openLightbox(img)"
            >
              <img :src="img.imageUrl" :alt="cityName" loading="lazy" />
            </div>
            <div
              v-for="img in rightImages"
              :key="'b' + img.id"
              class="film-frame"
              @click="openLightbox(img)"
            >
              <img :src="img.imageUrl" :alt="cityName" loading="lazy" />
            </div>
          </template>
          <template v-else>
            <div
              v-for="img in rightImages"
              :key="img.id"
              class="film-frame"
              @click="openLightbox(img)"
            >
              <img :src="img.imageUrl" :alt="cityName" loading="lazy" />
            </div>
          </template>
        </div>
      </div>

      <!-- 底部渐变遮罩 -->
      <div class="gallery-fade-top" />
      <div class="gallery-fade-bottom" />
    </main>

    <!-- 灯箱 -->
    <Teleport to="body">
      <Transition name="lightbox">
        <div
          v-if="lightboxVisible"
          class="lightbox-overlay"
          @click.self="closeLightbox"
        >
          <button
            class="lightbox-close"
            @click="closeLightbox"
            aria-label="关闭"
          >
            <svg
              viewBox="0 0 24 24"
              width="28"
              height="28"
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

          <button
            class="lightbox-arrow left"
            @click.stop="goPrev"
            :disabled="lightboxIdx === 0"
            aria-label="上一张"
          >
            <svg
              viewBox="0 0 24 24"
              width="32"
              height="32"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <polyline points="15 18 9 12 15 6" />
            </svg>
          </button>

          <div class="lightbox-stage">
            <img
              :src="images[lightboxIdx]?.imageUrl"
              :alt="cityName"
              class="lightbox-img"
            />
          </div>

          <button
            class="lightbox-arrow right"
            @click.stop="goNext"
            :disabled="lightboxIdx === images.length - 1"
            aria-label="下一张"
          >
            <svg
              viewBox="0 0 24 24"
              width="32"
              height="32"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <polyline points="9 18 15 12 9 6" />
            </svg>
          </button>

          <div class="lightbox-counter">
            {{ lightboxIdx + 1 }} / {{ images.length }}
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style>
html.city-gallery-page,
html.city-gallery-page body {
  background: #fff !important;
  overflow: hidden;
}
html.dark.city-gallery-page,
html.dark.city-gallery-page body {
  background: #181818 !important;
}
</style>

<style scoped>
/* ===== 页面容器 ===== */
.gallery-page {
  position: fixed;
  inset: 0;
  z-index: 1;
  background: #fff;
  user-select: none;
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s;
}
.gallery-page.dark {
  background: #181818;
}

/* ===== 顶栏 ===== */
.gallery-header {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  flex-shrink: 0;
}
.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #303133;
  cursor: pointer;
  transition:
    background 0.2s,
    color 0.3s;
  flex-shrink: 0;
}
.back-btn:hover {
  background: rgba(0, 0, 0, 0.06);
}
.dark .back-btn {
  color: #e5e5e5;
}
.dark .back-btn:hover {
  background: rgba(255, 255, 255, 0.08);
}

.city-title {
  margin: 0;
  font-family: 'Noto Serif SC', Georgia, 'Times New Roman', serif;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  letter-spacing: 0.5px;
  transition: color 0.3s;
}
.dark .city-title {
  color: #e5e5e5;
}

.img-count {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
  transition: color 0.3s;
}
.dark .img-count {
  color: #808080;
}

/* ===== 状态页（加载/错误/空） ===== */
.gallery-status {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  position: relative;
  z-index: 2;
}
.status-text {
  font-size: 15px;
  color: #909399;
  margin: 0;
  transition: color 0.3s;
}
.dark .status-text {
  color: #808080;
}
.status-btn {
  border: 1px solid #e4e7ed;
  background: #fff;
  color: #606266;
  font-size: 13px;
  padding: 6px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}
.status-btn:hover {
  border-color: #909399;
  color: #303133;
}
.dark .status-btn {
  background: #232323;
  border-color: #333;
  color: #b0b0b0;
}
.dark .status-btn:hover {
  border-color: #808080;
  color: #e5e5e5;
}

.status-spinner {
  width: 28px;
  height: 28px;
  border: 2px solid #e4e7ed;
  border-top-color: #909399;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
.dark .status-spinner {
  border-color: #333;
  border-top-color: #808080;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ===== 主体 ===== */
.gallery-main {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 32px;
  padding: 8px 24px 24px;
  overflow: hidden;
  position: relative;
  z-index: 1;
  min-height: 0;
}

/* 渐变遮罩 */
.gallery-fade-top,
.gallery-fade-bottom {
  position: absolute;
  left: 0;
  right: 0;
  height: 60px;
  pointer-events: none;
  z-index: 2;
  transition: opacity 0.3s;
}
.gallery-fade-top {
  top: 0;
  background: linear-gradient(to bottom, #fff 0%, transparent 100%);
}
.gallery-fade-bottom {
  bottom: 0;
  background: linear-gradient(to top, #fff 0%, transparent 100%);
}
.dark .gallery-fade-top {
  background: linear-gradient(to bottom, #181818 0%, transparent 100%);
}
.dark .gallery-fade-bottom {
  background: linear-gradient(to top, #181818 0%, transparent 100%);
}

/* ===== 列容器 ===== */
.film-col {
  flex: 1;
  max-width: 420px;
  overflow: hidden;
  mask-image: linear-gradient(
    to bottom,
    transparent 0%,
    black 10%,
    black 90%,
    transparent 100%
  );
}
.film-col.right {
  margin-top: 0;
}

/* ===== 滚动轨道 ===== */
.film-track {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 向下滚动 */
.film-track.anim-down {
  animation: scrollDown var(--dur, 30s) linear infinite;
}
/* 向上滚动 */
.film-track.anim-up {
  animation: scrollUp var(--dur, 30s) linear infinite;
}
/* 暂停 */
.film-col.paused .film-track.anim-down,
.film-col.paused .film-track.anim-up {
  animation-play-state: paused;
}

@keyframes scrollDown {
  from {
    transform: translateY(-50%);
  }
  to {
    transform: translateY(0);
  }
}
@keyframes scrollUp {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-50%);
  }
}

/* ===== 胶片框 ===== */
.film-frame {
  position: relative;
  background: #111;
  padding: 22px 5px;
  border-radius: 3px;
  cursor: pointer;
  flex-shrink: 0;
  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.1),
    0 6px 24px rgba(0, 0, 0, 0.06);
  transition:
    box-shadow 0.3s,
    transform 0.3s;
}
.film-frame:hover {
  box-shadow:
    0 4px 12px rgba(0, 0, 0, 0.15),
    0 8px 32px rgba(0, 0, 0, 0.1);
  transform: scale(1.02);
}
.dark .film-frame {
  background: #0d0d0d;
  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.3),
    0 6px 24px rgba(0, 0, 0, 0.2);
}
.dark .film-frame:hover {
  box-shadow:
    0 4px 12px rgba(0, 0, 0, 0.4),
    0 8px 32px rgba(0, 0, 0, 0.3);
}

/* 齿孔 */
.film-frame::before,
.film-frame::after {
  content: '';
  position: absolute;
  left: 8px;
  right: 8px;
  height: 22px;
  pointer-events: none;
  background-image:
    radial-gradient(circle, rgba(255, 255, 255, 0.25) 0.8px, transparent 1px),
    radial-gradient(circle, rgba(255, 255, 255, 0.25) 0.8px, transparent 1px);
  background-size:
    10px 10px,
    10px 10px;
  background-repeat: repeat-x;
  background-position:
    0 5px,
    0 calc(100% - 5px);
}
.film-frame::before {
  top: 0;
}
.film-frame::after {
  bottom: 0;
}

.film-frame img {
  display: block;
  width: 100%;
  height: auto;
  object-fit: cover;
  aspect-ratio: 4 / 3;
  border-radius: 1px;
}

/* ===== 灯箱 ===== */
.lightbox-overlay {
  position: fixed;
  inset: 0;
  z-index: 10000;
  background: rgba(0, 0, 0, 0.88);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.lightbox-close {
  position: absolute;
  top: 20px;
  right: 20px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition:
    color 0.2s,
    background 0.2s;
  z-index: 2;
  display: flex;
}
.lightbox-close:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.lightbox-stage {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 88vw;
  max-height: 85vh;
}
.lightbox-img {
  max-width: 88vw;
  max-height: 85vh;
  object-fit: contain;
  border-radius: 4px;
  box-shadow: 0 8px 48px rgba(0, 0, 0, 0.5);
}

.lightbox-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.06);
  border: none;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  z-index: 2;
}
.lightbox-arrow:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}
.lightbox-arrow:disabled {
  opacity: 0.2;
  cursor: default;
}
.lightbox-arrow.left {
  left: 24px;
}
.lightbox-arrow.right {
  right: 24px;
}

.lightbox-counter {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  font-family: 'Noto Serif SC', Georgia, 'Times New Roman', serif;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 1px;
}

/* 灯箱过渡 */
.lightbox-enter-active,
.lightbox-leave-active {
  transition: opacity 0.25s ease;
}
.lightbox-enter-active .lightbox-img,
.lightbox-leave-active .lightbox-img {
  transition: transform 0.25s ease;
}
.lightbox-enter-from,
.lightbox-leave-to {
  opacity: 0;
}
.lightbox-enter-from .lightbox-img {
  transform: scale(0.92);
}
.lightbox-leave-to .lightbox-img {
  transform: scale(0.92);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .gallery-header {
    padding: 12px 16px;
  }
  .city-title {
    font-size: 16px;
  }
  .gallery-main {
    gap: 16px;
    padding: 4px 12px 16px;
  }
  .film-col {
    max-width: none;
  }
  .lightbox-arrow {
    width: 40px;
    height: 40px;
  }
  .lightbox-arrow.left {
    left: 10px;
  }
  .lightbox-arrow.right {
    right: 10px;
  }
}

@media (max-width: 480px) {
  .gallery-main {
    flex-direction: column;
    gap: 8px;
    padding-top: 12px;
  }
  .film-col {
    flex: 1;
    mask-image: linear-gradient(
      to right,
      transparent 0%,
      black 8%,
      black 92%,
      transparent 100%
    );
    overflow: hidden;
  }
  .film-track {
    flex-direction: row;
    gap: 12px;
    height: 100%;
    align-items: center;
  }
  .film-track.anim-down {
    animation: scrollRight var(--dur, 30s) linear infinite;
  }
  .film-track.anim-up {
    animation: scrollLeft var(--dur, 30s) linear infinite;
  }
  @keyframes scrollRight {
    from {
      transform: translateX(-50%);
    }
    to {
      transform: translateX(0);
    }
  }
  @keyframes scrollLeft {
    from {
      transform: translateX(0);
    }
    to {
      transform: translateX(-50%);
    }
  }
  .film-frame {
    width: 200px;
    flex-shrink: 0;
  }
  .gallery-fade-top,
  .gallery-fade-bottom {
    height: 30px;
  }
}
</style>
