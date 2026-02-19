import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    {
      path: '/',
      component: () => import('@/view/Layout/index.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/view/Home/index.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'article/:slug',
          name: 'article',
          component: () => import('@/view/Article/index.vue'),
          meta: { title: '文章' }
        },
        {
          path: 'category/:slug',
          name: 'category',
          component: () => import('@/view/Category/index.vue'),
          meta: { title: '分类' }
        },
        {
          path: 'tag/:slug',
          name: 'tag',
          component: () => import('@/view/Tag/index.vue'),
          meta: { title: '标签' }
        },
        {
          path: 'archive',
          name: 'archive',
          component: () => import('@/view/Archive/index.vue'),
          meta: { title: '归档' }
        },
        {
          path: 'links',
          name: 'links',
          component: () => import('@/view/Links/index.vue'),
          meta: { title: '友链' }
        },
        {
          path: 'message',
          name: 'message',
          component: () => import('@/view/Message/index.vue'),
          meta: { title: '留言板' }
        },
        {
          path: 'about',
          name: 'about',
          component: () => import('@/view/About/index.vue'),
          meta: { title: '关于' }
        }
      ]
    }
  ]
})

router.beforeEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - FeiTwnd` : 'FeiTwnd'
  return true
})

export default router
