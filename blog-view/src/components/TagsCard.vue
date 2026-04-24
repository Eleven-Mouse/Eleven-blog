<template>
  <div class="sidebar-tags modern-card">
    <div class="section-title">热门标签</div>
    <div v-if="loading" class="sidebar-tags__loading">加载中...</div>
    <div v-if="error" class="sidebar-tags__error">{{ error }}</div>
    <div v-if="tagsData.length" class="sidebar-tags__list">
      <router-link
        v-for="tag in tagsData"
        :key="tag.id"
        :to="`/tag/${tag.id}`"
        class="tag"
      >
        {{ tag.name }}
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchTags } from '@/api/tags'

const loading = ref(false)
const error = ref(null)
const tagsData = ref([])

const getTags = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchTags()
    tagsData.value = response || []
  } catch (err) {
    error.value = '获取标签失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => { getTags() })
</script>

<style scoped>
.sidebar-tags {
  padding: 20px;
}

.sidebar-tags__list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.sidebar-tags__loading,
.sidebar-tags__error {
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
  padding: 8px 0;
}
</style>
