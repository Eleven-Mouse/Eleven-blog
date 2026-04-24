<template>
  <div class="archive-page page-container">
    <div class="page-header">
      <h1 class="page-header__title">归档</h1>
      <p class="page-header__desc">真棒！目前共计 {{ totalArticles }} 篇文章。</p>
    </div>

    <div v-if="loading" class="loading-tip">正在加载归档数据...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <div v-if="!loading && !error" class="archive-list">
      <div v-for="(articles, month) in archiveData" :key="month" class="archive-group">
        <div class="archive-group__label">{{ month }}</div>
        <div class="archive-group__items">
          <router-link
            v-for="article in articles"
            :key="article.id"
            :to="`/article/${article.id}`"
            class="archive-item"
          >
            <span class="archive-item__date">{{ formatDate(article.publishTime) }}</span>
            <span class="archive-item__title">{{ article.title }}</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchArchive } from '@/api/archive.js'

const archiveData = ref({})
const totalArticles = ref(0)
const loading = ref(false)
const error = ref(null)

const getArchiveData = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchArchive()
    archiveData.value = response.archive || {}
    totalArticles.value = response.total || 0
  } catch (err) {
    error.value = '获取归档数据失败，请稍后再试。'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const formatDate = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  const m = (date.getMonth() + 1).toString().padStart(2, '0')
  const d = date.getDate().toString().padStart(2, '0')
  return `${m}-${d}`
}

onMounted(() => { getArchiveData() })
</script>

<style scoped>
.archive-list {
  max-width: 700px;
  margin: 0 auto;
}

.archive-group {
  margin-bottom: 40px;
}

.archive-group__label {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--accent);
  margin-bottom: 14px;
  padding-left: 16px;
  border-left: 3px solid var(--accent);
  letter-spacing: -0.01em;
}

.archive-group__items {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.archive-item {
  display: flex;
  align-items: baseline;
  gap: 20px;
  padding: 10px 16px;
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: background var(--transition-fast), color var(--transition-fast),
    transform var(--transition-fast);
}

.archive-item:hover {
  background: var(--accent-light);
  transform: translateX(4px);
}

.archive-item__date {
  font-size: 13px;
  color: var(--text-muted);
  flex-shrink: 0;
  width: 48px;
  font-variant-numeric: tabular-nums;
}

.archive-item__title {
  font-size: 15px;
  color: var(--text-secondary);
  transition: color var(--transition-fast);
}

.archive-item:hover .archive-item__title {
  color: var(--accent);
}

@media (max-width: 768px) {
  .archive-item {
    flex-direction: column;
    gap: 2px;
    padding: 8px 16px;
  }

  .archive-item__date {
    width: auto;
    font-size: 12px;
  }
}
</style>
