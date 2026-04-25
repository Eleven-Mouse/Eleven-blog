import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../store/auth";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "login",
      component: () => import("../views/Login.vue"),
      meta: { title: "登录" },
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
          meta: { title: "仪表盘", icon: "Odometer" },
        },
        {
          path: "articlemgmt",
          name: "articlemgmt",
          component: () =>
            import("../views/blog/article/ArticleMgmt.vue"),
          meta: { title: "文章管理", icon: "Document", group: "content" },
        },
        {
          path: "writearticle",
          name: "writearticle",
          component: () =>
            import("../views/blog/article/WriteArticle.vue"),
          meta: { title: "发布文章", icon: "EditPen", group: "content" },
        },
        {
          path: "/article/edit/:id?",
          name: "editArticle",
          component: () =>
            import("../components/editor/ArticleFormCard.vue"),
          meta: { title: "编辑文章", group: "content", hidden: true },
        },
        {
          path: "momentsmgmt",
          name: "momentsmgmt",
          component: () =>
            import("../views/blog/moments/MomentsMgmt.vue"),
          meta: {
            title: "动态管理",
            icon: "ChatDotRound",
            group: "content",
          },
        },
        {
          path: "writemoment",
          name: "writemoment",
          component: () =>
            import("../views/blog/moments/WriteMoment.vue"),
          meta: { title: "发布动态", icon: "Promotion", group: "content" },
        },
        {
          path: "categorisemgmt",
          name: "categorisemgmt",
          component: () =>
            import("../views/blog/categorise/CategoriseMgmt.vue"),
          meta: {
            title: "分类管理",
            icon: "FolderOpened",
            group: "system",
          },
        },
        {
          path: "tagsmgmt",
          name: "tagsmgmt",
          component: () =>
            import("../views/blog/tags/TagsMgmt.vue"),
          meta: { title: "标签管理", icon: "PriceTag", group: "system" },
        },
        {
          path: "commentmgmt",
          name: "commentmgmt",
          component: () =>
            import("../views/blog/comment/CommentMgmt.vue"),
          meta: { title: "评论管理", icon: "Comment", group: "system" },
        },
        {
          path: "friendlinks",
          name: "friendlinks",
          component: () =>
            import("../views/blog/friendlinks/FriendLinks.vue"),
          meta: { title: "友链管理", icon: "Link", group: "system" },
        },
        {
          path: "blogconfig",
          name: "blogconfig",
          component: () =>
            import("../views/blog/config/BlogConfig.vue"),
          meta: { title: "博客配置", icon: "Setting", group: "system" },
        },
        {
          path: "upload",
          name: "upload",
          component: () =>
            import("../views/blog/article/UploadArticle.vue"),
          meta: { title: "导入文章", icon: "Upload", group: "tools" },
        },
      ],
    },
  ],

  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0, left: 0, behavior: "smooth" };
    }
  },
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.accessToken) {
    next("/login");
  } else {
    next();
  }
});

export default router;
