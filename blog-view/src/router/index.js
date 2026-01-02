import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      component: () => import('@/views/Layout.vue'),
      redirect: '/home',
      children: [
        {
          path: 'home',
          name: 'home',
          component: () => import('@/views/Home.vue'),
        },
        {
          path: 'tag/:id',
          name: 'tag',
          component: () => import('@/views/Tags.vue'),
        },
        {
          path: 'archive',
          name: 'archive',
          component: () => import('@/views/Archive.vue'),
        },
        {
          path: 'about',
          name: 'about',
          component: () => import('@/views/About.vue'),
        },
        {
          path: 'friendlinks',
          name: 'friendLinks',
          component: () => import('@/views/FriendLinks.vue'),
        },

        {
          path: 'article/:id',
          name: 'articleDetail',
          component: () => import('@/views/ArticleDetail.vue'),
        },
        {
          path: 'category/:id',
          name: 'categoryDetail',
          component: () => import('@/views/CategoryDetail.vue'),
        },
      ],
    },
  ],
  // 路由切换时自动滚动到页面顶部
  scrollBehavior(to, from, savedPosition) {
    // 如果有保存的位置（例如，使用浏览器的后退/前进按钮），则返回该位置
    if (savedPosition) {
      return savedPosition
    } else {
      // 否则，始终滚动到页面顶部
      return { top: 0, left: 0, behavior: 'smooth' }
    }
  },
})

export default router
