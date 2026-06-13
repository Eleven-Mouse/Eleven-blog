<template>
  <div class="home page-container">
    <section class="home-content" :class="{ 'is-ready': homeReady }">
      <div v-if="loading" class="loading-tip">正在加载首页文章...</div>
      <div v-else-if="error" class="error-tip">{{ error }}</div>

      <template v-else-if="article">
        <article class="article-body home-block home-block--hero">
          <ArticleMarkdown content-id="home-featured-preview" :content="renderedFeaturedContent" />
        </article>
        <section class="home-comments home-block home-block--comments">
          <el-divider />
          <CommentsCard :blog-id="article.id" />
        </section>
      </template>
      <div v-else class="empty-tip">首页文章未找到，请确认标题为“首页”的文章存在。</div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch, nextTick, onUnmounted } from 'vue'
import { fetchArticleById, fetchArticles } from '@/api/article'
import { useBlogConfigStore } from '@/stores/blogConfig'
import CommentsCard from '@/components/CommentsCard.vue'
import { transformObsidianAssetLinks } from '@/utils/markdownAssets'
import ArticleMarkdown from '@/components/ArticleMarkdown.vue'

const blogConfig = useBlogConfigStore()
const article = ref(null)
const loading = ref(false)
const error = ref('')
let mediaObserver = null

const featuredId = computed(() => Number(blogConfig.config.home_featured_article_id || 0))
const renderedFeaturedContent = computed(() => transformObsidianAssetLinks(article.value?.content || ''))
const homeReady = computed(() => Boolean(article.value) && !loading.value && !error.value)

const loadFeaturedArticle = async () => {
  loading.value = true
  error.value = ''
  try {
    if (featuredId.value) {
      article.value = await fetchArticleById(featuredId.value)
      return
    }

    // 兜底规则：标题严格等于“首页”的文章作为首页文章
    const res = await fetchArticles({ page: 1, size: 50, keyword: '首页' })
    const list = res?.data || []
    const matched = list.find((item) => item.title === '首页')
    if (matched?.id) {
      article.value = await fetchArticleById(matched.id)
    } else {
      // 次级兜底：如果没有“首页”标题文章，展示最新一篇文章，避免首页空白
      const latest = await fetchArticles({ page: 1, size: 1 })
      const latestList = latest?.data || []
      if (latestList[0]?.id) {
        article.value = await fetchArticleById(latestList[0].id)
      } else {
        article.value = null
      }
    }
  } catch (err) {
    article.value = null
    error.value = '首页文章加载失败，请检查“首页”文章是否存在。'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const hydrateHomeImages = () => {
  const container = document.querySelector('#home-featured-preview')
  if (!container) return
  container.querySelectorAll('img').forEach((img) => {
    img.loading = 'eager'
    img.decoding = 'async'
    img.style.display = 'block'
    img.style.visibility = 'visible'
    img.style.opacity = '1'
  })
}

const setupImageObserver = () => {
  mediaObserver?.disconnect?.()
  const root = document.querySelector('#home-featured-preview')
  if (!root) return
  mediaObserver = new MutationObserver(() => {
    hydrateHomeImages()
  })
  mediaObserver.observe(root, { childList: true, subtree: true })
}

watch(
  () => featuredId.value,
  () => {
    loadFeaturedArticle()
  },
  { immediate: true },
)

watch(
  () => article.value?.id,
  () => {
    nextTick(() => {
      hydrateHomeImages()
      setupImageObserver()
    })
  },
)

onUnmounted(() => {
  mediaObserver?.disconnect?.()
  mediaObserver = null
})
</script>

<style scoped>
.home {
  padding-top: 72px;
  padding-bottom: 40px;
}

.home-content {
  padding: 24px;
  animation: homeContentRise 0.55s cubic-bezier(0.2, 0.9, 0.2, 1) both;
}

.home-block {
  opacity: 0;
  transform: translate3d(0, 18px, 0);
  animation: homeBlockIn 0.62s cubic-bezier(0.2, 0.85, 0.25, 1) forwards;
}

.home-content.is-ready .home-block--hero {
  animation-delay: 0.06s;
}

.home-content.is-ready .home-block--comments {
  animation-delay: 0.18s;
}

.article-body,
.home-comments {
  transition:
    transform 0.28s ease,
    filter 0.28s ease;
}

.article-body:hover,
.home-comments:hover {
  transform: translateY(-2px);
  filter: saturate(1.03);
}

.article-body {
  width: min(100%, 920px);
  margin: 0 auto;
}

.article-body :deep(img) {
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
  max-width: 100%;
  height: auto;
  margin: 20px auto;
}
.home-comments {
  margin-top: 40px;
}

.empty-tip {
  color: var(--text-secondary);
}

@keyframes homeContentRise {
  from {
    opacity: 0;
    transform: translate3d(0, 10px, 0);
  }
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

@keyframes homeBlockIn {
  from {
    opacity: 0;
    transform: translate3d(0, 18px, 0);
  }
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

@media (prefers-reduced-motion: reduce) {
  .home-content,
  .home-block,
  .article-body,
  .home-comments {
    animation: none !important;
  }

  .article-body,
  .home-comments {
    transition: none;
  }
}

@media (max-width: 768px) {
  .home {
    padding-top: 68px;
  }

  .home-content {
    padding: 14px;
  }
}
</style>
