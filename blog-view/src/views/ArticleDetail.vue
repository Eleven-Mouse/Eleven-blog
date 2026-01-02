<template>
  <div class="article-detail-container">
    <div v-if="loading" class="loading-tip">文章加载中...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <el-card v-if="article" class="article-content-card">
      <div class="article-meta">
        <span>发布于：{{ formattedCreateDate }}</span>
      </div>
      <MdPreview
        editorId="preview-only"
        :modelValue="article.content"
        @onGetCatalog="onGetCatalog"
      />
    </el-card>
    <!-- 评论区 -->
    <CommentsCard v-if="article && article.id" :blog-id="article.id" class="comments-section" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticleById } from '@/api/article.js'
import CommentsCard from '@/components/CommentsCard.vue'

// 1. Import the new editor and its styles
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'

const route = useRoute()
const article = ref(null)
const loading = ref(false)
const error = ref(null)
const headers = ref([]) // Used to store article headers for the TOC

// 2. Use the onGetCatalog event to get the table of contents
const onGetCatalog = (list) => {
  headers.value = list
}

// Computed property to format the date
const formattedCreateDate = computed(() => {
  if (article.value && article.value.createTime) {
    return new Date(article.value.createTime).toLocaleString()
  }
  return '未知日期'
})

// Fetch article data
onMounted(async () => {
  const articleId = route.params.id
  if (!articleId) {
    error.value = '未找到文章ID'
    return
  }

  loading.value = true
  try {
    article.value = await fetchArticleById(articleId)
  } catch (err) {
    error.value = '加载文章失败，请稍后再试。'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.article-detail-container {
  width: 1500px;
  margin: 0 auto;
  padding-top: 50px;
  border: 0;
}

.article-content-card {
  padding: 20px;
  border-radius: 8px;
  width: 860px;
  margin: 20px auto;
  background-color: var(--card-bg-color);
  box-shadow: none;
  border: 0;
  transition:
    background-color 0.3s,
    border-color 0.3s;
}

.article-meta {
  color: var(--app-text-color);
  float: inline-end;
  margin-top: 20px;
  margin-left: 25px;
  box-shadow: none;
  border: 0;
  transition: color 0.3s;
}

.toc {
  position: fixed;
  left: 50px;
  top: 120px;
  width: 250px;
}

.comments-section {
  width: 800px;
  margin: 20px auto;
}

/* 保持 md-editor-v3 预览区域与卡片背景一致 */
:deep(#preview-only .md-editor-preview),
:deep(#preview-only .md-editor-preview-wrapper) {
  background-color: transparent !important;
}
</style>
