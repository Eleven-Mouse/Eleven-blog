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
          path: 'moment',
          name: 'moment',
          component: () => import('@/views/Moment.vue'),
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
    // 如果路径变了（从页面A跳到页面B），但没有锚点，才回到顶部
    if (to.path !== from.path && !to.hash) {
      const container = document.querySelector('.main-scroll-container')
      if (container) {
        container.scrollTop = 0
      }
    }

    // 如果有锚点，我们什么都不做，让 el-anchor 自己去处理跳转
    if (to.hash) {
      return false // 返回 false 表示不干预滚动
    }
  },
})

export default router
