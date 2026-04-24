<template>
  <div class="home">
    <!-- Hero Section -->

    <!-- Main Content -->
    <div class="page-container">
      <div class="content-grid">
        <!-- Articles -->
        <div class="home__articles">
          <div v-if="loading" class="loading-tip">正在加载文章...</div>
          <div v-if="error" class="error-tip">{{ error }}</div>

          <div v-if="!loading && articles.length" class="home__list">
            <div v-if="searchKeyword" class="home__search-info">
              搜索 "<strong>{{ searchKeyword }}</strong
              >" 找到 {{ pagination.total }} 篇文章
            </div>
            <ArticleCard
              v-for="(article, index) in articles"
              :key="article.id"
              :article="article"
              :style="{ animationDelay: `${index * 0.08}s` }"
            />
          </div>

          <div v-if="!loading && !error && articles.length === 0" class="empty-tip">
            {{ searchKeyword ? `没有找到与 "${searchKeyword}" 相关的文章` : '暂无文章' }}
          </div>

          <!-- Pagination -->
          <div class="pagination-wrap" v-if="!loading && articles.length > 0">
            <el-pagination
              layout="prev, pager, next"
              :total="pagination.total"
              :page-size="pagination.size"
              :current-page="pagination.currentPage"
              @current-change="handlePageChange"
            />
          </div>
        </div>

        <!-- Sidebar -->
        <aside class="home__sidebar sidebar-sticky">
          <InfoCard class="home__sidebar-card" />
          <TagsCard class="home__sidebar-card" />
          <CategoryCard class="home__sidebar-card" />
        </aside>
      </div>
    </div>
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
const articles = ref([])
const searchKeyword = ref('')
const pagination = ref({
  currentPage: 1,
  total: 0,
  size: 5,
})
const loading = ref(true)
const error = ref(null)

const getArticles = async (page = 1) => {
  loading.value = true
  error.value = null
  try {
    const keyword = route.query.keyword || ''
    searchKeyword.value = keyword
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

const handlePageChange = (page) => {
  getArticles(page)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  getArticles(1)
})

watch(
  () => route.query.keyword,
  () => {
    getArticles(1)
  },
)
</script>

<style scoped>
.home {
  padding-top: 65px;
}

/* ---------- Articles ---------- */
.home__articles {
  min-width: 0;
  overflow: hidden;
}

.home__list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.home__search-info {
  font-size: 14px;
  color: var(--text-secondary);
  padding: 14px 18px;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-xs);
}
.home__search-info strong {
  color: var(--accent);
}

.home__sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

@media (max-width: 1024px) {
  .home__sidebar {
    order: 2;
  }

  .home__sidebar-card {
    width: 100%;
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 48px 0 36px;
  }

  .hero__title {
    font-size: 1.75rem;
  }

  .hero__subtitle {
    font-size: 0.95rem;
  }

  .hero__orbs {
    display: none;
  }

  .home__sidebar {
    display: none;
  }
}
</style>
