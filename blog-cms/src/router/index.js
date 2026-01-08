import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../store/auth";
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "login",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/",
      name: "layout",
      component: () => import("../views/Layout.vue"),
      redirect: "/home",
      meta: { requiresAuth: true },
      children: [
        {
          path: "home",
          name: "home",
          component: () => import("../views/Home.vue"),
        },
        {
          path: "articlemgmt",
          name: "articlemgmt",
          component: () => import("../views/blog/article/ArticleMgmt.vue"),
        },
        {
          path: "writearticle",
          name: "writearticle",
          component: () => import("../views/blog/article/WriteArticle.vue"),
        },
        {
          path: "categorisemgmt",
          name: "categorisemgmt",
          component: () =>
            import("../views/blog/categorise/CategoriseMgmt.vue"),
        },
        {
          path: "commentmgmt",
          name: "commentmgmt",
          component: () => import("../views/blog/comment/CommentMgmt.vue"),
        },
        {
          path: "writemoment",
          name: "writemoment",
          component: () => import("../views/blog/moments/WriteMoment.vue"),
        },
        {
          path: "momentsmgmt",
          name: "momentsmgmt",
          component: () => import("../views/blog/moments/MomentsMgmt.vue"),
        },
        {
          path: "tagsmgmt",
          name: "tagsmgmt",
          component: () => import("../views/blog/tags/TagsMgmt.vue"),
        },
        {
          path: "upload",
          name: "upload",
          component: () => import("../views/blog/article/UploadArticle.vue"),
        },
        {
          path: "friendlinks",
          name: "friendlinks",
          component: () => import("../views/blog/friendlinks/FriendLinks.vue"),
        },
        {
          path: "/article/edit/:id?",
          name: "EditArticle",
          component: () => import("../components/ArticleFormCard.vue"),
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

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  // 如果去的页面需要登录，且没有 Token
  if (to.meta.requiresAuth && !authStore.accessToken) {
    next("/login"); // 滚去登录
  } else {
    next(); // 放行
  }
});
export default router;
