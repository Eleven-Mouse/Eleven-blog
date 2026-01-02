<template>
  <el-card class="category-card">
    <template #header>
      <div class="card-header">
        <span> 分类</span>
      </div>
    </template>
    <div v-if="loading">正在加载...</div>
    <div v-if="error">{{ error }}</div>
    <ul v-if="categories.length" class="category-list">
      <li v-for="category in categories" :key="category.id" class="category-item">
        <router-link :to="`/category/${category.id}`" class="category-link parent-category">
          {{ category.name }}
        </router-link>
      </li>
    </ul>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchCategories } from '@/api/categories'

const categories = ref([])
const loading = ref(false)
const error = ref(null)

//获取分类列表
const getCategories = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await fetchCategories()
    categories.value = data || []
  } catch (err) {
    error.value = '获取分类数据失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}
onMounted(() => {
  getCategories()
})
</script>

<style scoped>
.category-card {
  margin-bottom: 20px;
  background-color: var(--card-bg-color);
  border: 1px solid var(--card-border-color);
  box-shadow: var(--card-box-shadow);
  transition:
    background-color 0.3s,
    border-color 0.3s;
  box-shadow: none;
  border: 0;
}

.card-header {
  font-weight: bold;
  color: var(--app-text-color);
  transition: color 0.3s;
}

.category-list,
.child-category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  margin-bottom: 10px;
}

.parent-category {
  font-weight: bold;
  font-size: 15px;
}

.child-category-list {
  padding-left: 20px;
  margin-top: 8px;
}

.child-category-item {
  margin-bottom: 5px;
}

.category-link {
  text-decoration: none;
  color: var(--app-text-color);
  transition: color 0.3s;
}

.category-link:hover {
  color: var(--link-hover-color);
}

.child-category-text {
  color: var(--app-secondary-text-color);
  font-size: 14px;
  transition: color 0.3s;
}
</style>
