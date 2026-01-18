import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/view/Home/index.vue'),
      meta: {
        title: 'FeiTwnd | 首页'
      }
    }
  ]
})

router.beforeEach((to) => {
  //设置页面标题
  document.title = to.meta.title || '网站名称'
  return true
})

export default router
