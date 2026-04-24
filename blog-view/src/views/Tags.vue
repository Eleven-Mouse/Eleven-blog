<template>
  <div class="tags-page page-container">
    <div class="page-header">
      <h1 class="page-header__title">标签</h1>
      <p class="page-header__desc">按标签浏览文章</p>
    </div>

    <div v-if="loading" class="loading-tip">正在加载文章...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <div v-if="articlesList.length" class="tags-list">
      <div class="section-title">文章列表</div>
      <router-link
        v-for="article in articlesList"
        :key="article.id"
        :to="`/article/${article.id}`"
        class="tags-item stagger-item"
      >
        <span class="tags-item__bullet" />
        <span class="tags-item__title">{{ article.title }}</span>
      </router-link>
    </div>
    <div v-else-if="!loading" class="empty-tip">该标签下暂无文章。</div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticlesByTagId } from '@/api/tags.js'

const route = useRoute()
const articlesList = ref([])
const loading = ref(false)
const error = ref(null)

const getArticles = async (id) => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchArticlesByTagId(id)
    articlesList.value = response.data || []
  } catch (err) {
    error.value = '获取文章列表失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getArticles(route.params.id)
})

watch(
  () => route.params.id,
  (newId) => {
    if (newId) getArticles(newId)
  },
)
</script>

<style scoped>
.tags-list {
  max-width: 700px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
}

.tags-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: background 0.15s;
}

.tags-item:hover {
  background: var(--bg-secondary);
}

.tags-item__bullet {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent);
  flex-shrink: 0;
}

.tags-item__title {
  font-size: 15px;
  color: var(--text-secondary);
  transition: color 0.15s;
}

.tags-item:hover .tags-item__title {
  color: var(--accent);
}
</style>
