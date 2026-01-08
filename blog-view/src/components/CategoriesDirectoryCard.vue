<template>
  <div class="directory-card">
    <el-text class="mx-1"> <h3 class="directory-title">目录</h3></el-text>
    <div v-if="loading">正在加载文章...</div>
    <div v-if="error">{{ error }}</div>

    <div v-if="articleslist.length" class="dir-list">
      <el-row :gutter="100">
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
    <div v-else-if="!loading" style="display: flex; justify-content: center">
      该分类下暂无文章。
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticlesByCategoryId } from '@/api/categories'

const route = useRoute()
const articleslist = ref([])
const loading = ref(false)
const error = ref(null)
const categoryId = ref(route.params.id)

const getArticlesByCategoryId = async (id) => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchArticlesByCategoryId(id)
    articleslist.value = response.data || []
  } catch (err) {
    error.value = '获取分类列表失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getArticlesByCategoryId(categoryId.value)
})

watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      categoryId.value = newId
      getArticlesByCategoryId(newId)
    }
  },
)
</script>

<style scoped>
.directory-card {
  padding: 20px 0;
  border-top: 1px solid var(--card-border-color, #3a3a3a);
  border-bottom: 1px solid var(--card-border-color, #3a3a3a);
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
.directory-title {
  font-size: 1.6rem;
  color: var(--app-secondary-text-color, #a2a2a2);
}

.dir-list {
  counter-reset: directory-counter;
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0; /* 初始状态为透明 */
}

.dir-list li {
  margin-bottom: 15px;
  counter-increment: directory-counter;
  line-height: 1.6;
}
.articlestitle {
  display: block;
  margin-bottom: 10px;
  color: #676767;
  transition: color 0.3s;
  text-decoration: none;
  color: #555555;
  transition: color 0.3s;
  text-shadow: #666;
}

.articlestitle:hover {
  color: #000000;
}

/* 在小屏幕上，恢复为单栏布局 */
@media (max-width: 768px) {
  .dir-list {
    column-count: 1;
  }
}
</style>
