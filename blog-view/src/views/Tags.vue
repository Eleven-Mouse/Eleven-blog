<template>
  <div class="tags-card">
    <h3 class="tags-title">目录</h3>
    <div v-if="loading">正在加载文章...</div>
    <div v-if="error">{{ error }}</div>
    <div v-if="articleslist.length">
      <el-row class="Atitle">
        <el-col :span="12">
          <div class="grid-content ep-bg-purple">
            <router-link
              v-for="article in articleslist.slice(0, Math.ceil(articleslist.length / 2))"
              :key="article.id"
              :to="`/article/${article.id}`"
              class="articlestitle"
            >
              {{ article.title }}
            </router-link>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="grid-content ep-bg-purple-light">
            <router-link
              v-for="article in articleslist.slice(Math.ceil(articleslist.length / 2))"
              :key="article.id"
              :to="`/article/${article.id}`"
              class="articlestitle"
            >
              {{ article.title }}
            </router-link>
          </div>
        </el-col>
      </el-row>
    </div>
    <div v-else-if="!loading">该标签下暂无文章。</div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticlesByTagId } from '@/api/tags.js'

const route = useRoute()
const articleslist = ref([])
const loading = ref(false)
const error = ref(null)
const articlesId = ref(route.params.id)

const geyArticlesByTagId = async (id) => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchArticlesByTagId(id)
    articleslist.value = response.data || []
  } catch (err) {
    error.value = '获取文章列表失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  geyArticlesByTagId(articlesId.value)
})

watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      articlesId.value = newId
      geyArticlesByTagId(newId)
    }
  },
)
</script>

<style scoped>
.tags-card {
  padding: 20px 0;
  border-top: 1px solid var(--card-border-color, #3a3a3a);
  border-bottom: 1px solid var(--card-border-color, #3a3a3a);
  margin: 40px 0;
  width: 650px;
}
.Atitle {
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0; /* 初始状态为透明 */
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
.tags-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--app-text-color, #545252);
  margin-top: 0;
  margin-bottom: 25px;
  right: 20px;
}

.directory-list {
  list-style: none;
  padding: 0;
  margin: 0;
  /* 使用 CSS Columns 实现两栏布局 */
  column-count: 2;
  column-gap: 40px;
  /* 使用 CSS Counters 来创建自定义编号 */
  counter-reset: directory-counter;
}

.directory-list li {
  margin-bottom: 15px;
  counter-increment: directory-counter;
  line-height: 1.6;
}
.articlestitle {
  display: block;
  margin-bottom: 10px;
  color: #6f6f6f;
  text-decoration: none;
  transition: color 0.3s;
}
.directory-list li::before {
  /* 显示自定义编号 */
  content: counter(directory-counter) '.';
  font-weight: 600;
  color: #888; /* 图片中编号的颜色 */
  margin-right: 10px;
}

.directory-list li a {
  color: #6f6f6f; /* 图片中链接的颜色 */
  text-decoration: none;
  transition: color 0.3s;
}

.directory-list li a:hover {
  color: #333;
}

/* 在小屏幕上，恢复为单栏布局 */
@media (max-width: 768px) {
  .directory-list {
    column-count: 1;
  }
}
</style>
