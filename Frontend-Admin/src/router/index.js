import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/view/Home/index.vue'),
      meta: {
        title: '首页'
      }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/view/Login/index.vue'),
      meta: {
        title: '登录'
      }
    }
  ]
})

router.beforeEach((to) => {
  //如果用户未登录，切访问的是非登录页就跳转到登录页
  const userStore = useUserStore()
  if (!userStore.isLoggedIn() && to.path !== '/login') {
    ElMessage.warning('请先登录')
    return '/login'
  }
  //设置页面标题
  document.title = to.meta.title || '网站名称'
  return true
})

export default router
