<template>
  <div>
    <el-text class="mx-1">
      <el-card class="tags-card">
        <template #header>
          <div class="card-header">
            <span> 热门标签</span>
          </div>
        </template>
        <div class="tagss">
          <div v-if="loading">正在加载...</div>
          <div v-if="error">{{ error }}</div>
          <p v-if="tagsData.length" class="tag-list">
            <el-tag v-for="tag in tagsData" :key="tag.id" class="tag-item" type="success">
              <router-link :to="`/tag/${tag.id}`" class="tag-link parent-category">
                {{ tag.name }}
              </router-link>
            </el-tag>
          </p>
        </div>
      </el-card></el-text
    >
  </div>
</template>
<script setup>
import { fetchTags } from '@/api/tags' // 假设你有一个 API 模块来获取标签数据
import { onMounted } from 'vue'
import { ref } from 'vue'

const loading = ref(false)
const error = ref(null)
const tagsData = ref([])

// 获取 tagscard 数据
const getTags = async () => {
  loading.value = true
  error.value = null
  //获得标签数据
  try {
    const response = await fetchTags() // 替换为你的 API 地址
    tagsData.value = response || []
  } catch (err) {
    error.value = '获取标签数据失败，请稍后再试。'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getTags()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  color: var(--app-text-color);
  transition: color 0.3s;
}
.tags-card {

  border: 1px solid var(--card-border-color);
  box-shadow: var(--card-box-shadow);
  transition:
    background-color 0.3s,
    border-color 0.3s;
  box-shadow: none;
  border: 0;
}
.el-tag {

  color: var(--tag-text-color);
  border-color: var(--tag-border-color);
  transition:
    background-color 0.3s,
    color 0.3s,
    border-color 0.3s;
}
.card-header .el-icon {
  margin-right: 8px;
}
.tag-link {
  text-decoration: none;
  display: flex;
  color: var(--tag-text-color);
  transition: color 0.3s;
}
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.parent-category {
  font-weight: bold;
  font-size: 13px;
}

.tag-item {
  display: flex;
  transition: all 0.2s;
}

.tag-item:hover {
  transform: scale(1.1);
}
</style>
