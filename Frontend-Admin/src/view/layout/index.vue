<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores'

const route = useRoute()
const userStore = useUserStore()

/** 侧边栏是否收起 */
const collapsed = ref(false)

const activeMenu = computed(() => route.path)

/** 文章编辑页需要全高无内边距布局 */
const isEditorPage = computed(() => route.path.startsWith('/article/edit'))

const navItems = [
  { path: '/dashboard', icon: 'icon-dashboard', label: '仪表盘' },
  { path: '/article/list', icon: 'icon-article', label: '文章管理' },
  { path: '/category', icon: 'icon-folder', label: '分类 / 标签' },
  { path: '/comment', icon: 'icon-comment', label: '评论管理' },
  { path: '/message', icon: 'icon-message', label: '留言管理' },
  { path: '/friend-link', icon: 'icon-link', label: '友链管理' },
  { path: '/music', icon: 'icon-music', label: '音乐管理' },
  { path: '/rss', icon: 'icon-rss', label: 'RSS 订阅' },
  { path: '/visitor', icon: 'icon-user', label: '访客管理' },
  { path: '/view-record', icon: 'icon-eye', label: '浏览记录' },
  { path: '/operation-log', icon: 'icon-log', label: '操作日志' },
  { path: '/profile', icon: 'icon-profile', label: '个人资料' },
  { path: '/settings', icon: 'icon-setting', label: '系统设置' }
]

const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录？', '提示', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logoutAction()
  })
}
</script>

<template>
  <div class="admin-shell">
    <!-- 左侧菜单 -->
    <aside :class="['sidebar', { collapsed }]">
      <div class="sidebar-logo">
        <span v-if="!collapsed" class="logo-text">管理控制台</span>
        <span v-else class="logo-icon">
          <!-- ICON: icon-dashboard -->
          <span class="iconfont icon-dashboard" />
        </span>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          :class="['nav-item', { active: activeMenu.startsWith(item.path) }]"
        >
          <!-- ICON: {{ item.icon }} -->
          <span :class="['iconfont', item.icon]" />
          <span v-if="!collapsed" class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <button class="collapse-btn" @click="collapsed = !collapsed">
        <!-- ICON: icon-arrow-left / icon-arrow-right -->
        <span
          :class="[
            'iconfont',
            collapsed ? 'icon-arrow-right' : 'icon-arrow-left'
          ]"
        />
      </button>
    </aside>

    <!-- 右侧主区域 -->
    <div class="main-wrapper">
      <!-- 顶部栏 -->
      <header class="topbar">
        <div class="topbar-breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }"
              >首页</el-breadcrumb-item
            >
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <span class="user-name">
            <!-- ICON: icon-user -->
            <span class="iconfont icon-user" />
            {{ userStore.userInfo?.nickname || '管理员' }}
          </span>
          <button class="logout-btn" @click="handleLogout">
            <!-- ICON: icon-logout -->
            <span class="iconfont icon-logout" />
            退出
          </button>
        </div>
      </header>

      <!-- 页面内容 -->
      <main :class="['page-main', { 'editor-page': isEditorPage }]">
        <router-view v-slot="{ Component }">
          <component :is="Component" :key="$route.fullPath" />
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-shell {
  display: flex;
  height: 100vh;
  background-color: #f5f7fa;
}

/* ---- 侧边栏 ---- */
.sidebar {
  width: 220px;
  background-color: #ffffff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #e4e7ed;
  font-size: 16px;
  font-weight: 700;
  color: #303133;
  letter-spacing: 1px;
  white-space: nowrap;
  overflow: hidden;
}

.logo-icon .iconfont {
  font-size: 22px;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition:
    background 0.15s,
    color 0.15s;
  text-decoration: none;
  white-space: nowrap;
  overflow: hidden;
}

.nav-item:hover {
  background-color: #f5f7fa;
  color: #303133;
}

.nav-item.active {
  background-color: #f5f7fa;
  color: #000000;
  font-weight: 600;
}

.nav-item .iconfont {
  font-size: 18px;
  flex-shrink: 0;
}

.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-top: 1px solid #e4e7ed;
  background: transparent;
  cursor: pointer;
  color: #909399;
  transition: color 0.15s;
}

.collapse-btn:hover {
  color: #303133;
}

/* ---- 顶部栏 ---- */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.topbar {
  height: 60px;
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.user-name .iconfont {
  font-size: 16px;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  transition: all 0.15s;
}

.logout-btn:hover {
  border-color: #000000;
  color: #000000;
}

/* ---- 主内容 ---- */
.page-main {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.page-main.editor-page {
  padding: 0;
  overflow: hidden;
}
</style>
