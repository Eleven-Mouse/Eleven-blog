<template>
  <div class="header-wrapper">
    <div class="header-container">
      <div class="logo">
        <a href="/home">Eleven-Mouse </a>
      </div>
      <div class="search-bar">
        <el-autocomplete
          v-model="searchInput"
          class="search-input"
          placeholder="Please Input"
          :prefix-icon="Search"
          :fetch-suggestions="querySearchArticles"
          :trigger-on-focus="false"
          value-key="title"
          clearable
          @select="handleSelectArticle"
          :popper-append-to-body="false"
        />
      </div>
      <el-menu
        :default-active="activeIndex"
        class="nav-menu"
        mode="horizontal"
        background-color="transparent"
        :text-color="'var(--app-text-color)'"
        :active-text-color="'var(--app-text-color)'"
        :ellipsis="false"
        :router="true"
      >
        <el-menu-item index="/home"
          ><el-icon><HomeFilled /></el-icon>Home</el-menu-item
        >
        <el-sub-menu index="/categories">
          <template #title
            ><el-icon><Grid /></el-icon>categories</template
          >
          <el-menu-item
            v-for="category in categories"
            :key="category.id"
            :index="`/category/${category.id}`"
          >
            {{ category.name }}
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/moment"
          ><el-icon><Comment /></el-icon>Moment</el-menu-item
        >
        <el-menu-item index="/archive"
          ><el-icon><WalletFilled /></el-icon>Archive</el-menu-item
        >
        <el-menu-item index="/about"
          ><el-icon><UserFilled /></el-icon>About</el-menu-item
        >
        <el-menu-item index="/friendlinks"
          ><el-icon><Link /></el-icon>Links</el-menu-item
        >
      </el-menu>
      <div class="github-link">
        <a href="https://github.com" target="_blank">
          <span>GitHub</span> <el-icon size="20"><Promotion /></el-icon>
        </a>
      </div>
      <theme-switcher class="theme-switch" />
    </div>
    <!-- 页面滚动进度条 -->
    <el-progress
      :percentage="scrollProgress"
      :stroke-width="2.5"
      :show-text="false"
      class="scroll-progress"
      status="success"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchCategories } from '@/api/categories'
import { fetchArticles } from '@/api/article.js'
import {
  Search,
  Link,
  HomeFilled,
  Grid,
  UserFilled,
  WalletFilled,
  Promotion,
  Comment,
} from '@element-plus/icons-vue'
import ThemeSwitcher from './ThemeSwitcher.vue'

const route = useRoute()
const router = useRouter()
const activeIndex = ref('/home')
const searchInput = ref('')
const categories = ref([])
const searchLoading = ref(false)
const currentCategoryId = ref(null)
const scrollProgress = ref(0)

// 获取分类列表
const loadCategories = async () => {
  try {
    categories.value = await fetchCategories()
  } catch (error) {
    console.error('获取分类列表失败', error)
  }
}

// 监听路由变化，更新导航菜单激活状态
watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/home' || newPath === '/') {
      activeIndex.value = '/home'
    } else if (newPath.startsWith('/category/')) {
      activeIndex.value = '/categories'
      currentCategoryId.value = route.params.id
    } else {
      activeIndex.value = newPath
    }
  },
  { immediate: true },
)

// 监听路由变化，获取categoryId
watch(
  () => route.params.id,
  (newId) => {
    if (route.path.startsWith('/category/')) {
      currentCategoryId.value = newId
    }
  },
  { immediate: true },
)

const calculateScrollProgress = (e) => {
  // 注意：这里我们通过 e.target 获取滚动的元素
  const target = e.target
  if (!target) return

  const scrollTop = target.scrollTop
  const scrollHeight = target.scrollHeight
  const clientHeight = target.clientHeight

  const scrollableHeight = scrollHeight - clientHeight
  const progress = scrollableHeight > 0 ? (scrollTop / scrollableHeight) * 100 : 0

  scrollProgress.value = Math.min(Math.max(progress, 0), 100)
}

let scrollContainer = null

onMounted(() => {
  loadCategories()
  // 监听页面滚动
  // 注意：querySelector 前面的点 . 代表 class
  scrollContainer = document.querySelector('.main-scroll-container')

  // 2. 如果找到了，就手动添加原生事件监听
  if (scrollContainer) {
    scrollContainer.addEventListener('scroll', calculateScrollProgress)
  } else {
    console.warn('未找到滚动容器 .main-scroll-container')
  }
})

onUnmounted(() => {
  // 3. 组件销毁时，记得移除监听，防止内存泄漏
  if (scrollContainer) {
    scrollContainer.removeEventListener('scroll', calculateScrollProgress)
  }
})

// 自动补全：根据输入关键字异步获取文章标题列表
const querySearchArticles = async (queryString, cb) => {
  const keyword = queryString.trim()
  if (!keyword) {
    cb([])
    return
  }

  searchLoading.value = true
  try {
    const res = await fetchArticles({
      page: 1,
      size: 5,
      keyword,
    })
    const list = res?.data || []
    cb(list)
  } catch (e) {
    console.error('搜索文章失败', e)
    cb([])
  } finally {
    searchLoading.value = false
  }
}

// 选择某个搜索建议后，跳转到文章详情，并清空搜索框
const handleSelectArticle = (item) => {
  if (!item || !item.id) return
  router.push(`/article/${item.id}`)
  // 跳转后清空输入内容
  searchInput.value = ''
}
</script>

<style scoped>
.header-container {
  position: relative;
  display: flex;
  align-items: center;
}
/* 针对输入框的焦点状态 */
.search-bar ::v-deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--app-text-color) inset !important; /* !important确保覆盖默认样式 */
}

.logo a {
  font-size: 22px;
  font-weight: bold;
  color: var(--app-text-color);
  text-decoration: none;
  margin-right: 20px;
  background-image: url('../assets/\ \(4\).png');
}

.search-bar {
  width: 220px;
  margin: 0 auto;
}

.search-bar .el-input {
  --el-input-bg-color: var(--card-bg-color);
  --el-input-text-color: var(--app-text-color);
  --el-input-border-color: var(--card-border-color);
}

.nav-menu {
  border-bottom: none;
}

.nav-menu .el-menu-item:hover,
.nav-menu .el-sub-menu__title:hover {
  background-color: rgba(0, 0, 0, 0.1) !important;
}
/* 带有子菜单的标题悬停 */
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(0, 0, 0, 0.1) !important;
}
.github-link {
  margin-left: auto;
}

.github-link a {
  color: var(--app-text-color);
  text-decoration: none;
  display: flex;
  align-items: center;
}

.github-link a:hover {
  color: var(--app-text-color);
}

.github-link span {
  margin-left: 8px;
}

.theme-switch {
  margin-left: 20px;
}

.header-wrapper {
  position: relative;
  width: 100%;
  animation: slideDown 0.3s ease-out forwards;
}

.scroll-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  border-radius: 0;
}
@keyframes slideDown {
  from {
    transform: translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
