<template>
  <div class="topic-redirect page-container">
    <div v-if="loading" class="loading-tip">正在打开专题...</div>
    <div v-else-if="error" class="error-tip">{{ error }}</div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchArticlesByCategoryId } from '@/api/categories'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const error = ref('')

const openTopic = async () => {
  const topicId = route.params.id
  if (!topicId) {
    error.value = '未找到专题ID'
    return
  }
  loading.value = true
  error.value = ''
  try {
    const res = await fetchArticlesByCategoryId(topicId, { page: 1, size: 1000 })
    const list = (res?.data || []).sort((a, b) => (a.chapterOrder || 0) - (b.chapterOrder || 0))
    if (!list.length) {
      error.value = '该专题下暂无文章'
      return
    }
    await router.replace(`/article/${list[0].id}`)
  } catch (err) {
    error.value = '专题打开失败，请稍后重试。'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  openTopic()
})
</script>

<style scoped>
.topic-redirect {
  padding-top: 74px;
}
</style>
