<template>
  <div class="article-detail-container">
    <div v-if="loading" class="loading-tip">文章加载中...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <div class="main-content-area">
      <div class="center">
        <el-card v-if="article" class="article-content-card">
          <div class="article-meta">
            <span>发布于：{{ formatTime(article.publishTime) }}</span>
          </div>
          <MdPreview
            editorId="preview-only"
            :modelValue="article.content"
            @onGetCatalog="onGetCatalog"
            :headingId="(index) => `heading-${index}`"
            :markedHeadingId="(index) => `heading-${index}`"
          />
        </el-card>
      </div>
      <div class="sidebar">
        <el-affix :offset="130" target=".main-content-area">
          <div class="catalog-card">
            <div class="catalog-title">目录导航</div>
            <el-anchor :container="scrollContainer" :offset="70" v-if="catalogList.length > 0">
              <el-anchor-link
                v-for="item in catalogList"
                :key="item.uniqueId"
                :href="`#${item.uniqueId}`"
                :title="item.text"
                :class="`indent-level-${item.level}`"
                @click.prevent
              />
            </el-anchor>
            <div v-else class="empty-catalog">暂无目录</div>
          </div>
        </el-affix>
      </div>
    </div>
    <el-divider></el-divider>
    <!-- 评论区 -->
    <CommentsCard v-if="article && article.id" :blog-id="article.id" class="comments-section" />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticleById } from '@/api/article.js'
import CommentsCard from '@/components/CommentsCard.vue'

import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'

const route = useRoute()
const article = ref(null)
const loading = ref(false)
const error = ref(null)
const catalogList = ref([])
const scrollContainer = ref(null)

// list 里的 text 就是现在正文里的实际 ID
const onGetCatalog = (list) => {
  catalogList.value = list.map((item, index) => ({
    ...item,
    // 这里绝对不要用 item.text，只用 index
    uniqueId: `heading-${index}`,
  }))

  nextTick(() => {
    // 找到预览区域内所有的标题标签
    const previewEl = document.querySelector('#preview-only .md-editor-preview')
    if (previewEl) {
      const headings = previewEl.querySelectorAll('h1, h2, h3, h4, h5, h6')
      headings.forEach((el, index) => {
        // 强制覆盖原来的 ID，改为 heading-0, heading-1...
        el.setAttribute('id', `heading-${index}`)
      })
    }
  })
}
const formatTime = (datetime) => {
  if (!datetime) return '--'
  const date = new Date(datetime)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

onMounted(async () => {
  const el = document.querySelector('.main-scroll-container')
  scrollContainer.value = el ? el : window
  const articleId = route.params.id
  if (!articleId) {
    error.value = '未找到文章ID'
    return
  }

  loading.value = true
  try {
    const data = await fetchArticleById(articleId)
    console.log('后端返回的文章详情:', data)
    article.value = data || null
  } catch (err) {
    error.value = '加载文章失败，请稍后再试。'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.main-content-area {
  width: 90vw;
  display: flex;
}
.sidebar {
  width: 200px;

  align-items: flex-start;
  top: 60px;
  margin-left: auto;
}
.catalog-card {
  max-height: calc(500px - 100px);
  overflow-y: auto;
  top: 60px;
  border: #333;
  scrollbar-width: 0;
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    width: 0;
    height: 0;
  }
}

.article-content-card {
  width: 900px;
  background-color: var(--card-bg-color);
  box-shadow: none;
  border: 0;
  transition:
    background-color 0.3s,
    border-color 0.3s;
  scroll-behavior: smooth;
  padding-left: 245px;
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0; /* 初始状态为透明 */
}

.article-meta {
  color: var(--app-text-color);
  float: inline-end;
  margin-left: 25px;
  box-shadow: none;
  border: 0;
  transition: color 0.3s;
}

.catalog-title {
  font-weight: bold;
  margin-bottom: 12px;
  font-size: 16px;
  padding-left: 10px;
  border-left: 4px solid #389747;
}
/* 目录层级缩进样式 */
:deep(.el-anchor__link) {
  font-size: 14px;
  line-height: 2;
}
.indent-level-1 {
  padding-left: 0px;
}
.indent-level-2 {
  padding-left: 15px;
}
.indent-level-3 {
  padding-left: 30px;
}
.indent-level-4 {
  padding-left: 45px;
}
.comments-section {
  width: 900px;
  margin: 0 auto;
}

:deep(#preview-only .md-editor-preview),
:deep(#preview-only .md-editor-preview-wrapper) {
  background-color: transparent !important;
}
:deep(.el-main) {
  overflow: visible !important;
}
@media screen and (max-width: 900px) {
  .right-sidebar {
    display: none;
  }

  .main-content-area {
    display: block;
  }
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
