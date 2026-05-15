<template>
  <div class="directory-card">
    <div class="section-title">文章列表</div>
    <div v-if="loading" class="loading-tip">正在加载文章...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <div v-if="articlesList.length" ref="listRef" class="directory-list">
      <router-link
        v-for="article in articlesList"
        :key="article.id"
        :to="`/article/${article.id}`"
        class="directory-item stagger-item"
      >
        <span class="directory-item__bullet" />
        <span class="directory-item__title">{{ article.title }}</span>
      </router-link>
    </div>
    <div v-else-if="!loading" class="empty-tip">该分类下暂无文章。</div>

    <div v-if="loadingMore" class="loading-tip">加载更多...</div>
    <div ref="sentinelRef" class="scroll-sentinel" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticlesByCategoryId } from '@/api/categories'

const route = useRoute()
const articlesList = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const error = ref(null)
const total = ref(0)
const currentPage = ref(0)
const pageSize = 10
const hasMore = ref(false)
const sentinelRef = ref(null)
let observer = null

const getArticles = async (id, isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
    currentPage.value = 0
    articlesList.value = []
  }
  error.value = null
  try {
    currentPage.value++
    const response = await fetchArticlesByCategoryId(id, { page: currentPage.value, size: pageSize })
    const newArticles = response.data || []
    const pagination = response.pagination || {}

    articlesList.value = [...articlesList.value, ...newArticles]

    total.value = pagination.total || 0
    hasMore.value = articlesList.value.length < total.value
  } catch (err) {
    error.value = '获取分类列表失败'
    console.error(err)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const setupObserver = () => {
  if (observer) observer.disconnect()
  observer = new IntersectionObserver(
    (entries) => {
      if (entries[0].isIntersecting && hasMore.value && !loadingMore.value && !loading.value) {
        getArticles(route.params.id, true)
      }
    },
    { rootMargin: '200px' },
  )
  if (sentinelRef.value) observer.observe(sentinelRef.value)
}

onMounted(() => {
  getArticles(route.params.id).then(setupObserver)
})

watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      getArticles(newId).then(setupObserver)
    }
  },
)

onUnmounted(() => {
  if (observer) observer.disconnect()
})
</script>

<style scoped>
.directory-card {
  max-width: 700px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
}
.directory-list {
  display: flex;
  flex-direction: column;
}

.directory-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: background 0.15s;
}

.directory-item:hover {
  background: var(--bg-secondary);
}

.directory-item__bullet {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent);
  flex-shrink: 0;
}

.directory-item__title {
  font-size: 15px;
  color: var(--text-secondary);
  transition: color 0.15s;
}

.directory-item:hover .directory-item__title {
  color: var(--accent);
}

.scroll-sentinel {
  height: 1px;
}

.loading-tip {
  text-align: center;
  padding: 16px;
  color: var(--text-tertiary, #999);
  font-size: 14px;
}
</style>
