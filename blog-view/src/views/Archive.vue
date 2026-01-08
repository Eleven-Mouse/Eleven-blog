<template>
  <div class="archive-container">
    <el-text class="mx-1">
      <div class="archive-header">
        <h1>归档</h1>
        <p>真棒！目前共计 {{ totalArticles }} 篇文章。</p>
      </div>
      <div v-if="loading">正在加载归档数据...</div>
      <div v-if="error">{{ error }}</div>
      <el-timeline v-if="!loading && !error">
        <el-timeline-item
          v-for="(articles, month) in archiveData"
          :key="month"
          :timestamp="`${month}`"
          placement="top"
          type="primary"
          :hollow="true"
          size="large"
        >
          <ul class="article-list">
            <li v-for="article in articles" :key="article.id" class="article-item">
              <span class="date">{{ formatDate(article.publishTime) }}</span>
              <router-link :to="`/article/${article.id}`" class="title">{{
                article.title
              }}</router-link>
            </li>
          </ul>
        </el-timeline-item>
      </el-timeline>
    </el-text>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchArchive } from '@/api/archive.js'

const archiveData = ref({})
const totalArticles = ref(0)
const loading = ref(false)
const error = ref(null)

// 获取归档数据
const getArchiveData = async () => {
  loading.value = true
  error.value = null
  try {
    //获得归档数据
    const response = await fetchArchive()
    //获得归档数据和文章总数
    archiveData.value = response.archive || {}
    totalArticles.value = response.total || 0
  } catch (err) {
    error.value = '获取归档数据失败，请稍后再试。'
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 格式化日期，只显示 'MM-DD'
const formatDate = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  return `${month}-${day}`
}

onMounted(() => {
  getArchiveData()
})
</script>

<style scoped>
.archive-container {
  width: 650px;
  padding: 20px 0;
  border-top: 1px solid var(--card-border-color, #3a3a3a);
  border-bottom: 1px solid var(--card-border-color, #3a3a3a);
  margin: 40px auto;
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0; /* 初始状态为透明 */
}
.archive-card {
  padding: 20px 0;
  border-top: 1px solid var(--card-border-color, #3a3a3a);
  border-bottom: 1px solid var(--card-border-color, #3a3a3a);
  margin: 40px 0;
}
.title-asd {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--app-text-color, #545252);
  margin-top: 0;
  margin-bottom: 25px;
}

.archive-header {
  text-align: center;
  margin-bottom: 40px;
}

.archive-header p {
  color: #888;
}

.article-list {
  list-style: none;
  padding: 0;
  position: relative;
}

.article-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.date {
  color: #888;
  font-size: 0.9em;
  margin-right: 120px;
  width: 80px;
}

.title {
  color: #434343;
  text-decoration: none;
  transition: color 0.3s;
  position: relative;
}

.title:hover {
  color: #333;
}

/* 下划线动画 */
.title::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -2px;
  width: 0%;
  height: 2px;
  background-color: currentColor;
  transition: width 0.3s ease-in-out;
}

.title:hover::after {
  width: 100%;
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
