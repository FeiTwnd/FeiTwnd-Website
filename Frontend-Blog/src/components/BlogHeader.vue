<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useBlogStore } from '@/stores'

const router = useRouter()
const route = useRoute()
const blogStore = useBlogStore()

/* 搜索 */
const searchVisible = ref(false)
const keyword = ref('')
const mobileNavVisible = ref(false)

/* 音乐播放 */
const currentMusic = computed(() => blogStore.musics[0] || null)
const isPlaying = ref(false)
const audioRef = ref(null)
const musicIndex = ref(0)

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

const nextTrack = () => {
  if (!blogStore.musics.length) return
  musicIndex.value = (musicIndex.value + 1) % blogStore.musics.length
  isPlaying.value = false
  if (audioRef.value) {
    audioRef.value.load()
    audioRef.value.play().then(() => {
      isPlaying.value = true
    }).catch(() => {})
  }
}

const navItems = [
  { label: '主页', href: 'https://feitwnd.cc', external: true },
  { label: '归档', to: '/archive' },
  { label: '友链', to: '/links' },
  { label: '留言', to: '/message' },
  { label: '关于', to: '/about' }
]

const doSearch = () => {
  const kw = keyword.value.trim()
  if (!kw) return
  searchVisible.value = false
  keyword.value = ''
  router.push({ path: '/', query: { search: kw } })
}

const toggleSearch = () => {
  searchVisible.value = !searchVisible.value
  if (!searchVisible.value) keyword.value = ''
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
            >{{ item.label }}</a>
            <router-link
              v-else
              :to="item.to"
              class="nav-link"
              :class="{ active: route.path === item.to }"
            >{{ item.label }}</router-link>
          </template>
        </nav>
      </div>

      <!-- 右侧: 音乐播放器 + 搜索 -->
      <div class="header-right">
        <!-- 迷你音乐播放器 -->
        <div v-if="currentTrack" class="mini-player">
          <img
            v-if="currentTrack.coverImage"
            :src="currentTrack.coverImage"
            class="player-cover"
            :class="{ spinning: isPlaying }"
          />
          <span class="player-title">{{ currentTrack.title }}</span>
          <button class="player-btn" @click="togglePlay" title="播放/暂停">
            <i class="iconfont" :class="isPlaying ? 'icon-pause' : 'icon-play'" />
          </button>
          <button class="player-btn" @click="nextTrack" title="下一首">
            <i class="iconfont icon-next" />
          </button>
          <audio
            ref="audioRef"
            :src="currentTrack.musicUrl"
            preload="none"
            @ended="nextTrack"
          />
        </div>

        <!-- 搜索 -->
        <div class="search-area">
          <div class="search-box" :class="{ expanded: searchVisible }">
            <input
              v-show="searchVisible"
              v-model="keyword"
              type="text"
              placeholder="搜索文章..."
              class="search-input"
              @keyup.enter="doSearch"
            />
          </div>
          <button class="search-toggle" @click="toggleSearch" title="搜索">
            <i class="iconfont icon-search" />
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
      >{{ item.label }}</a>
      <a class="nav-mobile-link" @click="toggleSearch(); mobileNavVisible = false">搜索</a>
    </nav>
  </header>
</template>

<style scoped>
.site-header {
  background: var(--blog-bg);
  border-bottom: 1px solid #e8e8e4;
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
  color: #1a1a1a;
  text-decoration: none;
  letter-spacing: 0.5px;
  white-space: nowrap;
}
.nav-desktop {
  display: flex;
  align-items: center;
  gap: 4px;
}
.nav-link {
  font-size: 14px;
  color: #666;
  text-decoration: none;
  padding: 6px 10px;
  border-radius: 4px;
  transition: color .15s, background .15s;
  white-space: nowrap;
}
.nav-link:hover {
  color: #1a1a1a;
  background: rgba(0,0,0,.04);
}
.nav-link.active {
  color: #1a1a1a;
  font-weight: 600;
}

/* 右侧 */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 迷你播放器 */
.mini-player {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 4px;
  background: rgba(0,0,0,.02);
  border: 1px solid #eee;
}
.player-cover {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e8e8e4;
}
.player-cover.spinning {
  animation: spin 8s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.player-title {
  font-size: 12px;
  color: #555;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.player-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px 4px;
  color: #666;
  font-size: 14px;
  transition: color .15s;
  display: flex;
  align-items: center;
}
.player-btn:hover {
  color: #1a1a1a;
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
  transition: width .3s ease;
}
.search-box.expanded {
  width: 180px;
}
.search-input {
  width: 100%;
  border: 1px solid #d4d4d0;
  border-radius: 4px;
  padding: 5px 10px;
  font-size: 13px;
  background: #fff;
  color: #1a1a1a;
  outline: none;
  font-family: inherit;
}
.search-input:focus {
  border-color: #1a1a1a;
}
.search-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  color: #666;
  font-size: 16px;
  transition: color .15s;
  display: flex;
  align-items: center;
}
.search-toggle:hover {
  color: #1a1a1a;
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
.bar, .bar::before, .bar::after {
  display: block;
  width: 18px;
  height: 2px;
  background: #1a1a1a;
  position: absolute;
  left: 5px;
  transition: transform .2s;
}
.bar { top: 13px; }
.bar::before { content: ''; top: -6px; }
.bar::after { content: ''; top: 6px; }
.bar.open { background: transparent; }
.bar.open::before { top: 0; transform: rotate(45deg); }
.bar.open::after { top: 0; transform: rotate(-45deg); }

.nav-mobile {
  display: none;
  border-top: 1px solid #e8e8e4;
  background: var(--blog-bg);
  padding: 8px 24px 12px;
}
.nav-mobile-link {
  display: block;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
  text-decoration: none;
  border-bottom: 1px solid #f0f0ec;
  cursor: pointer;
}
.nav-mobile-link:hover { color: #1a1a1a; }

@media (max-width: 768px) {
  .nav-desktop { display: none; }
  .mini-player { display: none; }
  .search-box.expanded { width: 120px; }
  .mobile-menu-btn { display: block; }
  .nav-mobile { display: flex; flex-direction: column; }
  .header-inner { padding: 0 16px; }
}
</style>
