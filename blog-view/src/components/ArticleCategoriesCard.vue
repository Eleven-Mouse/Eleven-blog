<template>
  <div>
    <el-card class="categories-card">
      <template #header>
        <div class="card-header">
          <span
            ><el-icon :size="20"><FolderOpened /></el-icon> 文章分类</span
          >
        </div>
      </template>
      <ul class="category-list">
        <li v-for="category in categoriesData" :key="category.name" class="category-item">
          <router-link :to="`/categories/${category.name}`">
            <span>{{ category.name }}</span>
            <span>{{ category.count }}</span>
          </router-link>
        </li>
      </ul>
    </el-card>
  </div>
</template>

<script setup>
import { FolderOpened } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { fetchCategories } from '@/api/categories'

const categoriesData = ref([])
const loading = ref(false)
const error = ref(null)

const getCategories = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchCategories()
    categoriesData.value = response || []
  } catch (err) {
    error.value = '获取分类云数据失败，请稍后再试。'
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
.categories-card {
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0; /* 初始状态为透明 */
}
.card-header {
  display: grid;
  align-items: center;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item a {
  display: flex;
  justify-content: space-between;
  padding: 10px 5px;
  color: #646464;
  text-decoration: none;
  border-bottom: 1px solid #929292;
  transition: background-color 0.3s;
}

.category-item:last-child a {
  border-bottom: none;
}

.category-item a:hover {
  background-color: #c6c6c6;
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
