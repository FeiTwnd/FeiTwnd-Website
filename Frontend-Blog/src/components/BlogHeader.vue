<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBlogStore, useThemeStore } from '@/stores'

const router = useRouter()
const blogStore = useBlogStore()
const themeStore = useThemeStore()

const isDark = computed(() => {
  if (themeStore.mode === 'dark') return true
  if (themeStore.mode === 'light') return false
  return window.matchMedia('(prefers-color-scheme: dark)').matches
})

/* 滚动检测 */
const scrolled = ref(false)
const handleScroll = () => {
  scrolled.value = window.scrollY > 60
}
onMounted(() =>
  window.addEventListener('scroll', handleScroll, { passive: true })
)
onUnmounted(() => window.removeEventListener('scroll', handleScroll))

/* 搜索 */
const searchVisible = ref(false)
const keyword = ref('')
const searchInputRef = ref(null)
const mobileNavVisible = ref(false)

/* 音乐播放 */
const isPlaying = ref(false)
const audioRef = ref(null)
const musicIndex = ref(0)
const musicListVisible = ref(false)

const currentTrack = computed(() => blogStore.musics[musicIndex.value] || null)

const togglePlay = () => {
  if (!audioRef.value || !currentTrack.value) return
  if (isPlaying.value) {
    audioRef.value.pause()
  } else {
    audioRef.value.play()
  }
  isPlaying.value = !isPlaying.value
}

const playTrack = (index) => {
  musicIndex.value = index
  isPlaying.value = false
  nextTick(() => {
    if (audioRef.value) {
      audioRef.value.load()
      audioRef.value
        .play()
        .then(() => {
          isPlaying.value = true
        })
        .catch(() => {})
    }
  })
}

const nextTrack = () => {
  if (!blogStore.musics.length) return
  playTrack((musicIndex.value + 1) % blogStore.musics.length)
}

const toggleMusicList = () => {
  musicListVisible.value = !musicListVisible.value
}

const navItems = [
  {
    label: '主页',
    icon: 'icon-zhuye',
    href: 'https://feitwnd.cc',
    external: true
  },
  { label: '博客', icon: 'icon-boke', to: '/' },
  { label: '归档', icon: 'icon-guidang', to: '/archive' },
  { label: '友链', icon: 'icon-lianjie', to: '/links' },
  { label: '留言', icon: 'icon-liuyan', to: '/message' },
  { label: '关于', icon: 'icon-guanyu', to: '/about' }
]

const doSearch = () => {
  const kw = keyword.value.trim()
  if (!kw) return
  searchVisible.value = false
  keyword.value = ''
  router.push({ path: '/', query: { search: kw } })
}

const toggleSearch = () => {
  mobileNavVisible.value = false
  searchVisible.value = !searchVisible.value
  if (!searchVisible.value) {
    keyword.value = ''
  } else {
    nextTick(() => searchInputRef.value?.focus())
  }
}

const toggleMobileNav = () => {
  mobileNavVisible.value = !mobileNavVisible.value
}

const navTo = (item) => {
  mobileNavVisible.value = false
  if (item.external) {
    window.open(item.href, '_blank')
  } else {
    router.push(item.to)
  }
}
</script>

<template>
  <header class="site-header" :class="{ scrolled, dark: isDark }">
    <div class="header-inner">
      <div class="header-left">
        <router-link to="/" class="site-title">FeiTwnd's Blog</router-link>
        <nav class="nav-desktop">
          <template v-for="item in navItems" :key="item.label">
            <a
              v-if="item.external"
              :href="item.href"
              target="_blank"
              rel="noopener"
              class="nav-link"
            >
              <i :class="['iconfont', item.icon]" /> {{ item.label }}
            </a>
            <router-link v-else :to="item.to" class="nav-link">
              <i :class="['iconfont', item.icon]" /> {{ item.label }}
            </router-link>
          </template>
        </nav>
      </div>

      <div class="header-right">
        <div v-if="currentTrack" class="mini-player-wrap">
          <div class="mini-player" @click="toggleMusicList">
            <img
              v-if="currentTrack.coverImage"
              :src="currentTrack.coverImage"
              class="player-cover"
              :class="{ spinning: isPlaying }"
            />
            <span class="player-title">{{ currentTrack.title }}</span>
          </div>
          <button class="player-btn" @click.stop="togglePlay" title="播放/暂停">
            <i
              class="iconfont"
              :class="isPlaying ? 'icon-zanting' : 'icon-play-full'"
            />
          </button>
          <button class="player-btn" @click.stop="nextTrack" title="下一首">
            <i class="iconfont icon-next" />
          </button>
          <audio
            ref="audioRef"
            :src="currentTrack.musicUrl"
            preload="none"
            @ended="nextTrack"
          />
          <transition name="fade">
            <div v-show="musicListVisible" class="music-panel">
              <div class="music-panel-header">
                <span><i class="iconfont icon-yinle" /> 播放列表</span>
                <span class="music-panel-count"
                  >{{ blogStore.musics.length }} 首</span
                >
              </div>
              <ul class="music-panel-list">
                <li
                  v-for="(m, idx) in blogStore.musics"
                  :key="m.id"
                  class="music-panel-item"
                  :class="{ active: idx === musicIndex }"
                  @click="playTrack(idx)"
                >
                  <img
                    v-if="m.coverImage"
                    :src="m.coverImage"
                    class="music-panel-cover"
                  />
                  <div class="music-panel-info">
                    <span class="music-panel-name">{{ m.title }}</span>
                    <span class="music-panel-artist">{{ m.artist }}</span>
                  </div>
                  <i
                    v-if="idx === musicIndex && isPlaying"
                    class="iconfont icon-yinle playing-icon"
                  />
                </li>
              </ul>
            </div>
          </transition>
        </div>

        <!-- 暗色模式切换 -->
        <button
          class="theme-toggle"
          :title="isDark ? '切换到浅色模式' : '切换到暗色模式'"
          @click="themeStore.toggle"
        >
          <!-- 太阳图标（暗色时显示，点击切换到亮色） -->
          <svg
            v-if="isDark"
            viewBox="0 0 24 24"
            width="17"
            height="17"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <circle cx="12" cy="12" r="5" />
            <line x1="12" y1="1" x2="12" y2="3" />
            <line x1="12" y1="21" x2="12" y2="23" />
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
            <line x1="1" y1="12" x2="3" y2="12" />
            <line x1="21" y1="12" x2="23" y2="12" />
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
          </svg>
          <!-- 月亮图标（亮色时显示，点击切换到暗色） -->
          <svg
            v-else
            viewBox="0 0 24 24"
            width="17"
            height="17"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z" />
          </svg>
        </button>

        <div class="search-area">
          <div class="search-box" :class="{ expanded: searchVisible }">
            <input
              ref="searchInputRef"
              v-show="searchVisible"
              v-model="keyword"
              type="text"
              placeholder="搜索文章..."
              class="search-input"
              @keyup.enter="doSearch"
              @blur="searchVisible = false"
            />
          </div>
          <button class="search-toggle" @click="toggleSearch" title="搜索">
            <i class="iconfont icon-sousuo" />
          </button>
        </div>

        <button class="mobile-menu-btn" @click="toggleMobileNav">
          <span :class="['bar', { open: mobileNavVisible }]" />
        </button>
      </div>
    </div>

    <nav v-show="mobileNavVisible" class="nav-mobile">
      <a
        v-for="item in navItems"
        :key="item.label"
        class="nav-mobile-link"
        @click="navTo(item)"
      >
        <i :class="['iconfont', item.icon]" /> {{ item.label }}
      </a>
      <a class="nav-mobile-link" @click="toggleSearch">
        <i class="iconfont icon-sousuo" /> 搜索
      </a>
      <a class="nav-mobile-link" @click="themeStore.toggle">
        <template v-if="isDark">
          <svg
            viewBox="0 0 24 24"
            width="15"
            height="15"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            style="vertical-align: -2px; margin-right: 4px"
          >
            <circle cx="12" cy="12" r="5" />
            <line x1="12" y1="1" x2="12" y2="3" />
            <line x1="12" y1="21" x2="12" y2="23" />
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
            <line x1="1" y1="12" x2="3" y2="12" />
            <line x1="21" y1="12" x2="23" y2="12" />
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
          </svg>
          浅色模式
        </template>
        <template v-else>
          <svg
            viewBox="0 0 24 24"
            width="15"
            height="15"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            style="vertical-align: -2px; margin-right: 4px"
          >
            <path d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z" />
          </svg>
          暗色模式
        </template>
      </a>
    </nav>
  </header>
</template>

<style scoped>
.site-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: transparent;
  border-bottom: none;
  transition:
    background 0.3s,
    box-shadow 0.3s;
}
.site-header.scrolled {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
}
.site-header.dark.scrolled {
  background: rgba(35, 35, 35, 0.95);
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.3);
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 28px;
  height: 58px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 28px;
}
.site-title {
  font-family: var(--blog-serif);
  font-size: 18px;
  font-weight: 800;
  color: #fff;
  text-decoration: none;
  letter-spacing: 0.5px;
  white-space: nowrap;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  transition:
    color 0.3s,
    text-shadow 0.3s;
}
.scrolled .site-title {
  color: #303133;
  text-shadow: none;
}
.dark.scrolled .site-title {
  color: #e5e5e5;
}
.nav-desktop {
  display: flex;
  align-items: center;
  gap: 2px;
}
.nav-link {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: 6px 10px;
  border-radius: 4px;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  transition:
    color 0.2s,
    background 0.2s,
    text-shadow 0.3s;
}
.nav-link .iconfont {
  font-size: 14px;
}
.nav-link:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.15);
}
.scrolled .nav-link {
  color: #606266;
  text-shadow: none;
}
.scrolled .nav-link:hover {
  color: #303133;
  background: #f5f7fa;
}
.dark.scrolled .nav-link {
  color: #b0b0b0;
}
.dark.scrolled .nav-link:hover {
  color: #e5e5e5;
  background: #333;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 迷你播放器 */
.mini-player-wrap {
  display: flex;
  align-items: center;
  gap: 4px;
  position: relative;
}
.mini-player {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px 4px 4px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  cursor: pointer;
  backdrop-filter: blur(4px);
  transition:
    background 0.2s,
    border-color 0.2s;
}
.scrolled .mini-player {
  background: #f5f7fa;
  border-color: #e4e7ed;
}
.dark.scrolled .mini-player {
  background: #333;
  border-color: #444;
}
.mini-player:hover {
  border-color: rgba(255, 255, 255, 0.5);
}
.scrolled .mini-player:hover {
  border-color: #909399;
}
.dark.scrolled .mini-player:hover {
  border-color: #666;
}
.player-cover {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgba(255, 255, 255, 0.3);
  flex-shrink: 0;
}
.scrolled .player-cover {
  border-color: #e4e7ed;
}
.dark.scrolled .player-cover {
  border-color: #444;
}
.player-cover.spinning {
  animation: spin 8s linear infinite;
}
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
.player-title {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s;
}
.scrolled .player-title {
  color: #606266;
}
.dark.scrolled .player-title {
  color: #b0b0b0;
}
.player-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 15px;
  transition: color 0.2s;
  display: flex;
  align-items: center;
}
.player-btn:hover {
  color: #fff;
}
.scrolled .player-btn {
  color: #606266;
}
.scrolled .player-btn:hover {
  color: #000;
}
.dark.scrolled .player-btn {
  color: #b0b0b0;
}
.dark.scrolled .player-btn:hover {
  color: #fff;
}

/* 音乐列表 */
.music-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 280px;
  max-height: 360px;
  background: var(--blog-card);
  border: 1px solid var(--blog-border);
  border-radius: 8px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
  z-index: 200;
  overflow: hidden;
}
.music-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border-bottom: 1px solid #ebeef5;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}
.dark .music-panel-header {
  border-bottom-color: #333;
  color: #e5e5e5;
}
.music-panel-header .iconfont {
  font-size: 14px;
  margin-right: 4px;
}
.music-panel-count {
  font-size: 12px;
  color: #909399;
  font-weight: 400;
}
.music-panel-list {
  list-style: none;
  margin: 0;
  padding: 4px 0;
  max-height: 300px;
  overflow-y: auto;
}
.music-panel-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  cursor: pointer;
  transition: background 0.12s;
}
.music-panel-item:hover {
  background: #f5f7fa;
}
.dark .music-panel-item:hover {
  background: #333;
}
.music-panel-item.active {
  background: #f5f7fa;
}
.dark .music-panel-item.active {
  background: #333;
}
.music-panel-item.active .music-panel-name {
  color: #000;
  font-weight: 600;
}
.dark .music-panel-item.active .music-panel-name {
  color: #fff;
}
.music-panel-cover {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  object-fit: cover;
  flex-shrink: 0;
  border: 1px solid #ebeef5;
}
.dark .music-panel-cover {
  border-color: #444;
}
.music-panel-info {
  flex: 1;
  min-width: 0;
}
.music-panel-name {
  display: block;
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.dark .music-panel-name {
  color: #e5e5e5;
}
.music-panel-artist {
  display: block;
  font-size: 11px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.playing-icon {
  font-size: 14px;
  color: #000;
  flex-shrink: 0;
}
.dark .playing-icon {
  color: #fff;
}
.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 0.15s,
    transform 0.15s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* 暗色模式切换 */
.theme-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  color: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  transition: color 0.2s;
}
.theme-toggle:hover {
  color: #fff;
}
.scrolled .theme-toggle {
  color: #606266;
}
.scrolled .theme-toggle:hover {
  color: #000;
}
.dark.scrolled .theme-toggle {
  color: #b0b0b0;
}
.dark.scrolled .theme-toggle:hover {
  color: #fff;
}

/* 搜索 */
.search-area {
  display: flex;
  align-items: center;
}
.search-box {
  overflow: hidden;
  width: 0;
  transition: width 0.3s ease;
}
.search-box.expanded {
  width: 180px;
}
.search-input {
  width: 100%;
  border: 1px solid var(--blog-border);
  border-radius: 4px;
  padding: 5px 10px;
  font-size: 13px;
  background: var(--blog-card);
  color: var(--blog-text);
  outline: none;
  font-family: inherit;
}
.search-input:focus {
  border-color: #000;
}
.dark .search-input:focus {
  border-color: #aaa;
}
.search-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  transition: color 0.2s;
  display: flex;
  align-items: center;
}
.search-toggle:hover {
  color: #fff;
}
.scrolled .search-toggle {
  color: #606266;
}
.scrolled .search-toggle:hover {
  color: #000;
}
.dark.scrolled .search-toggle {
  color: #b0b0b0;
}
.dark.scrolled .search-toggle:hover {
  color: #fff;
}

/* 移动端 */
.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  width: 28px;
  height: 28px;
  position: relative;
}
.bar,
.bar::before,
.bar::after {
  display: block;
  width: 18px;
  height: 2px;
  background: #fff;
  position: absolute;
  left: 5px;
  transition:
    transform 0.2s,
    background 0.3s;
}
.scrolled .bar,
.scrolled .bar::before,
.scrolled .bar::after {
  background: #303133;
}
.dark.scrolled .bar,
.dark.scrolled .bar::before,
.dark.scrolled .bar::after {
  background: #e5e5e5;
}
.bar {
  top: 13px;
}
.bar::before {
  content: '';
  top: -6px;
}
.bar::after {
  content: '';
  top: 6px;
}
.bar.open {
  background: transparent;
}
.bar.open::before {
  top: 0;
  transform: rotate(45deg);
}
.bar.open::after {
  top: 0;
  transform: rotate(-45deg);
}
.nav-mobile {
  display: none;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(8px);
  padding: 8px 24px 12px;
  border-top: 1px solid #ebeef5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}
.dark .nav-mobile {
  background: rgba(35, 35, 35, 0.96);
  border-top-color: #333;
}
.nav-mobile-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 0;
  font-size: 14px;
  color: #606266;
  text-decoration: none;
  border-bottom: 1px solid #f2f6fc;
  cursor: pointer;
}
.nav-mobile-link .iconfont {
  font-size: 15px;
}
.nav-mobile-link:hover {
  color: #303133;
}
.dark .nav-mobile-link {
  color: #b0b0b0;
  border-bottom-color: #333;
}
.dark .nav-mobile-link:hover {
  color: #e5e5e5;
}

@media (max-width: 768px) {
  .nav-desktop {
    display: none;
  }
  .mini-player-wrap {
    display: none;
  }
  .theme-toggle {
    display: none;
  }
  .search-box.expanded {
    width: 120px;
  }
  .mobile-menu-btn {
    display: block;
  }
  .nav-mobile {
    display: flex;
    flex-direction: column;
  }
  .header-inner {
    padding: 0 16px;
  }
}
</style>
