<template>
  <div class="home-container">
    <el-row :gutter="20" class="main-content">
      <el-col :xs="20" :sm="10" :md="18">
        <div class="article-list">
          <ArticleCard v-for="(article, index) in articles" :key="index" :article="article" />
        </div>
        <div v-if="loading" :data="getArticles" style="width: 100%" class="loading-tip">
          正在加载文章...
        </div>
        <div v-if="error" class="error-tip">{{ error }}</div>

        <div class="pagination-container" v-if="!loading && !error && articles.length > 0">
          <el-pagination
            layout="prev, pager, next"
            :total="pagination.total"
            :page-size="pagination.size"
            :current-page="pagination.currentPage"
            @current-change="handlePageChange"
          />
        </div>
      </el-col>
      <!-- Sidebar -->
      <el-col :xs="24" :sm="24" :md="6">
        <div class="sidebar">
          <InfoCard class="sidebar-card" />
          <TagsCard class="sidebar-card" />
          <CategoryCard class="sidebar-card" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import InfoCard from '@/components/InfoCard.vue'
import TagsCard from '@/components/TagsCard.vue'
import CategoryCard from '@/components/CategoryCard.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { fetchArticles } from '@/api/article.js'

const route = useRoute()
// 文章列表
const articles = ref([])
// 分页信息
const pagination = ref({
  currentPage: 1,
  total: 0,
  size: 4,
})

// 加载和错误状态
const loading = ref(true)
const error = ref(null)

// 获取文章列表
const getArticles = async (page = 1) => {
  loading.value = true
  error.value = null
  try {
    const keyword = route.query.keyword || ''
    const response = await fetchArticles({
      page,
      size: pagination.value.size,
      keyword: keyword || undefined,
    })
    articles.value = response.data
    pagination.value.total = response.pagination.total
    pagination.value.currentPage = response.pagination.currentPage
  } catch (err) {
    error.value = '获取文章列表失败，请稍后再试。'
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 分页
const handlePageChange = (page) => {
  getArticles(page)
  // 手动滚动到页面顶部
  const container = document.querySelector('.main-scroll-container')
  container.scrollTo({ top: 0, behavior: 'smooth' })
}

// 组件挂载时获取第一页数据
onMounted(() => {
  getArticles(1)
})

// 监听路由上的 keyword 变化，实时更新搜索结果
watch(
  () => route.query.keyword,
  () => {
    // 每次搜索关键字变化时，从第一页重新请求
    getArticles(1)
  },
)
</script>

<style scoped>
.home-container {
  max-width: 1050px;
  margin-top: 60px;
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0; /* 初始状态为透明 */
}

.hero-content h1 {
  font-size: 4rem;
  margin: 0;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.7);
}

.hero-content p {
  font-size: 1.5rem;
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.7);
}

.article-card {
  margin-bottom: 20px;
}

.article-meta {
  font-size: 0.9em;
  color: #aaa;
}

.article-meta span {
  margin-right: 15px;
}

.sidebar-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
/* 修改分页组件选中页码数字颜色 */
.pagination-container ::v-deep(.el-pager li.is-active) {
  color: var(--pagination-active-color);
  font-weight: bold;
}

/* 鼠标悬停在选中页码上保持同样颜色 */
.pagination-container ::v-deep(.el-pager li.is-active:hover) {
  color: var(--pagination-active-color);
}

/* 修改分页组件未选中页码的悬停颜色 */
.pagination-container ::v-deep(.el-pager li:not(.is-active):hover) {
  color: var(--pagination-hover-color) !important; /* 使用 !important 确保覆盖默认样式 */
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
