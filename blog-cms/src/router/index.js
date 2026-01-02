import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "layout",
      component: () => import("@/views/Layout.vue"),
      redirect: "/home",
      children: [
        {
          path: "home",
          name: "home",
          component: () => import("@/views/Home.vue"),
        },
        {
          path: "articlemgmt",
          name: "articlemgmt",
          component: () => import("@/views/blog/article/ArticleMgmt.vue"),
        },
        {
          path: "writearticle",
          name: "writearticle",
          component: () => import("@/views/blog/article/WriteArticle.vue"),
        },
        {
          path: "categorisemgmt",
          name: "categorisemgmt",
          component: () => import("@/views/blog/categorise/CategoriseMgmt.vue"),
        },
        {
          path: "commentmgmt",
          name: "commentmgmt",
          component: () => import("@/views/blog/comment/CommentMgmt.vue"),
        },
        {
          path: "tagsmgmt",
          name: "tagsmgmt",
          component: () => import("@/views/blog/tags/TagsMgmt.vue"),
        },
        {
          path: "upload",
          name: "upload",
          component: () => import("@/views/blog/article/UploadArticle.vue"),
        },

        {
          // :id 表示这是一个动态参数
          path: "/article/edit/:id?",
          name: "EditArticle",
          component: () => import("@/components/ArticleFormCard.vue"),
          meta: { title: "发布/编辑文章" },
        },
      ],
    },
  ],

  // 路由切换时自动滚动到页面顶部
  scrollBehavior(to, from, savedPosition) {
    // 如果有保存的位置（例如，使用浏览器的后退/前进按钮），则返回该位置
    if (savedPosition) {
      return savedPosition;
    } else {
      // 否则，始终滚动到页面顶部
      return { top: 0, left: 0, behavior: "smooth" };
    }
  },
});
export default router;
