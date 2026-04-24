<template>
  <div class="directory-card">
    <div class="section-title">文章列表</div>
    <div v-if="loading" class="loading-tip">正在加载文章...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <div v-if="articlesList.length" class="directory-list">
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticlesByCategoryId } from '@/api/categories'

const route = useRoute()
const articlesList = ref([])
const loading = ref(false)
const error = ref(null)

const getArticles = async (id) => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchArticlesByCategoryId(id)
    articlesList.value = response.data || []
  } catch (err) {
    error.value = '获取分类列表失败'
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
</style>
