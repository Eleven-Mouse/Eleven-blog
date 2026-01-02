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

// 计算页面滚动进度
const calculateScrollProgress = () => {
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const scrollableHeight = documentHeight - windowHeight
  const progress = scrollableHeight > 0 ? (scrollTop / scrollableHeight) * 100 : 0
  scrollProgress.value = Math.min(Math.max(progress, 0), 100)
}

// 监听滚动事件
const handleScroll = () => {
  calculateScrollProgress()
}

onMounted(() => {
  loadCategories()
  // 监听页面滚动
  window.addEventListener('scroll', handleScroll)
  // 初始化计算一次
  calculateScrollProgress()
})

onUnmounted(() => {
  // 移除滚动监听
  window.removeEventListener('scroll', handleScroll)
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
  height: 100%;
  padding: 0 20px;
  background-color: transparent;
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
  background-color: transparent !important;
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
  animation: slideDown 0.5s ease-out forwards;
}

.scroll-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 1001;
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
