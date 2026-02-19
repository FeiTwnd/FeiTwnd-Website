import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/view/Home/index.vue'),
      meta: { title: 'CV - FeiTwnd' }
    }
  ]
})

router.beforeEach((to) => {
  document.title = to.meta.title || 'CV'
  return true
})

export default router
