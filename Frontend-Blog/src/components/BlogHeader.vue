<script setup>
import { ref, computed, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useBlogStore } from '@/stores'

const router = useRouter()
const route = useRoute()
const blogStore = useBlogStore()

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
  <header class="site-header">
    <div class="header-inner">
      <!-- 左侧: 站名 + 导航 -->
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
            <router-link
              v-else
              :to="item.to"
              class="nav-link"
              :class="{ active: route.path === item.to }"
            >
              <i :class="['iconfont', item.icon]" /> {{ item.label }}
            </router-link>
          </template>
        </nav>
      </div>

      <!-- 右侧: 音乐播放器 + 搜索 -->
      <div class="header-right">
        <!-- 迷你音乐播放器 -->
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

          <!-- 音乐列表面板 -->
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

        <!-- 搜索 -->
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

        <!-- 移动端汉堡 -->
        <button class="mobile-menu-btn" @click="toggleMobileNav">
          <span :class="['bar', { open: mobileNavVisible }]" />
        </button>
      </div>
    </div>

    <!-- 移动端导航 -->
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
    </nav>
  </header>
</template>

<style scoped>
.site-header {
  background: var(--blog-bg);
  border-bottom: 1px solid #e4e7ed;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1060px;
  margin: 0 auto;
  padding: 0 24px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 左侧 */
.header-left {
  display: flex;
  align-items: center;
  gap: 28px;
}
.site-title {
  font-family: var(--blog-serif);
  font-size: 18px;
  font-weight: 800;
  color: #303133;
  text-decoration: none;
  letter-spacing: 0.5px;
  white-space: nowrap;
}
.nav-desktop {
  display: flex;
  align-items: center;
  gap: 2px;
}
.nav-link {
  font-size: 13px;
  color: #606266;
  text-decoration: none;
  padding: 6px 10px;
  border-radius: 4px;
  transition:
    color 0.15s,
    background 0.15s;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.nav-link .iconfont {
  font-size: 14px;
}
.nav-link:hover {
  color: #303133;
  background: #f5f7fa;
}
.nav-link.active {
  color: #000;
  font-weight: 600;
  background: #f5f7fa;
}

/* 右侧 */
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
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  transition: border-color 0.15s;
}
.mini-player:hover {
  border-color: #909399;
}
.player-cover {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e4e7ed;
  flex-shrink: 0;
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
  color: #606266;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.player-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #606266;
  font-size: 15px;
  transition: color 0.15s;
  display: flex;
  align-items: center;
}
.player-btn:hover {
  color: #000;
}

/* 音乐列表面板 */
.music-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 280px;
  max-height: 360px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
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
.music-panel-item.active {
  background: #f5f7fa;
}
.music-panel-item.active .music-panel-name {
  color: #000;
  font-weight: 600;
}
.music-panel-cover {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  object-fit: cover;
  flex-shrink: 0;
  border: 1px solid #ebeef5;
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

/* 搜索 */
.search-area {
  display: flex;
  align-items: center;
  gap: 0;
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
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 5px 10px;
  font-size: 13px;
  background: #fff;
  color: #303133;
  outline: none;
  font-family: inherit;
}
.search-input:focus {
  border-color: #000;
}
.search-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  color: #606266;
  font-size: 16px;
  transition: color 0.15s;
  display: flex;
  align-items: center;
}
.search-toggle:hover {
  color: #000;
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
  background: #303133;
  position: absolute;
  left: 5px;
  transition: transform 0.2s;
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
  border-top: 1px solid #ebeef5;
  background: var(--blog-bg);
  padding: 8px 24px 12px;
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

@media (max-width: 768px) {
  .nav-desktop {
    display: none;
  }
  .mini-player-wrap {
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
