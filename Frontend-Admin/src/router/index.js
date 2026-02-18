import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/view/Login/index.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/view/layout/index.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: '/dashboard',
          name: 'dashboard',
          component: () => import('@/view/Dashboard/index.vue'),
          meta: { title: '仪表盘' }
        },
        {
          path: '/article/list',
          name: 'articleList',
          component: () => import('@/view/Article/index.vue'),
          meta: { title: '文章管理' }
        },
        {
          path: '/article/edit',
          name: 'articleCreate',
          component: () => import('@/view/Article/ArticleEdit.vue'),
          meta: { title: '新建文章' }
        },
        {
          path: '/article/edit/:id',
          name: 'articleEdit',
          component: () => import('@/view/Article/ArticleEdit.vue'),
          meta: { title: '编辑文章' }
        },
        {
          path: '/category',
          name: 'category',
          component: () => import('@/view/Category/index.vue'),
          meta: { title: '分类管理' }
        },
        {
          path: '/comment',
          name: 'comment',
          component: () => import('@/view/Comment/index.vue'),
          meta: { title: '评论管理' }
        },
        {
          path: '/settings',
          name: 'settings',
          component: () => import('@/view/Settings/index.vue'),
          meta: { title: '系统设置' }
        },
        {
          path: '/friend-link',
          name: 'friendLink',
          component: () => import('@/view/FriendLink/index.vue'),
          meta: { title: '友链管理' }
        },
        {
          path: '/message',
          name: 'message',
          component: () => import('@/view/Message/index.vue'),
          meta: { title: '留言管理' }
        },
        {
          path: '/visitor',
          name: 'visitor',
          component: () => import('@/view/Visitor/index.vue'),
          meta: { title: '访客管理' }
        },
        {
          path: '/operation-log',
          name: 'operationLog',
          component: () => import('@/view/OperationLog/index.vue'),
          meta: { title: '操作日志' }
        },
        {
          path: '/profile',
          name: 'profile',
          component: () => import('@/view/Profile/index.vue'),
          meta: { title: '个人资料' }
        },
        {
          path: '/music',
          name: 'music',
          component: () => import('@/view/Music/index.vue'),
          meta: { title: '音乐管理' }
        },
        {
          path: '/rss',
          name: 'rss',
          component: () => import('@/view/Rss/index.vue'),
          meta: { title: 'RSS 订阅' }
        },
        {
          path: '/view-record',
          name: 'viewRecord',
          component: () => import('@/view/ViewRecord/index.vue'),
          meta: { title: '浏览记录' }
        }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const isLoggedIn = !!localStorage.getItem('admin_token')

  // 未登录 → 跳登录页（已在登录页则放行）
  if (!isLoggedIn && to.path !== '/login') {
    return { path: '/login', replace: true }
  }

  // 已登录 → 禁止再访问登录页，直接进首页
  if (isLoggedIn && to.path === '/login') {
    return { path: '/dashboard', replace: true }
  }

  document.title = `${to.meta?.title || '管理'} - FeiTwnd管理`
  return true
})

export default router
