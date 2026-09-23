<template>
  <div class="home page-container">
    <section class="home-content" :class="{ 'is-ready': homeReady }">
      <SproutLoader v-if="loading" text="正在加载首页文章..." />
      <div v-else-if="error" class="error-tip">{{ error }}</div>

      <template v-else-if="article">
        <article class="article-body home-block home-block--hero">
          <ArticleMarkdown
            content-id="home-featured-preview"
            :content="renderedFeaturedContent"
          />
        </article>
        <GiscusComments
          ref="commentsRef"
          class="home-comments"
          :class="{ 'home-comments--visible': commentsVisible }"
        />
      </template>
      <div v-else class="empty-tip">首页文章未找到，请确认标题为“首页”的文章存在。</div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch, nextTick, onUnmounted } from 'vue'
import { fetchArticleById, fetchArticles } from '@/api/article'
import { useBlogConfigStore } from '@/stores/blogConfig'
import { transformObsidianAssetLinks } from '@/utils/markdownAssets'
import ArticleMarkdown from '@/components/ArticleMarkdown.vue'
import GiscusComments from '@/components/GiscusComments.vue'
import SproutLoader from '@/components/common/SproutLoader.vue'

// 打字机效果按需加载，避免 typed.js 进入首屏主包
let typedPromise = null
const loadTyped = () => {
  if (!typedPromise) typedPromise = import('typed.js').then((module) => module.default)
  return typedPromise
}

const blogConfig = useBlogConfigStore()
const article = ref(null)
const loading = ref(false)
const error = ref('')
const commentsRef = ref(null)
const commentsVisible = ref(false)
let mediaObserver = null
let motionObserver = null
let titleTyped = null

const featuredId = computed(() => Number(blogConfig.config.home_featured_article_id || 0))
const renderedFeaturedContent = computed(() => transformObsidianAssetLinks(article.value?.content || ''))
const homeReady = computed(() => Boolean(article.value) && !loading.value && !error.value)

// 静态内容模式下数据几乎瞬时返回，为加载动画保留最短展示时长
const MIN_LOADING_MS = 700
let loadToken = 0

const loadFeaturedArticle = async () => {
  const token = ++loadToken
  loading.value = true
  error.value = ''
  const startedAt = Date.now()
  try {
    if (featuredId.value) {
      try {
        article.value = await fetchArticleById(featuredId.value)
        return
      } catch (err) {
        // 本地配置可能缓存了旧文章 ID，继续按“首页”标题查找。
        console.warn('首页配置的文章不存在，尝试按标题回退。', err)
      }
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
    error.value = '首页内容加载失败，请检查网络后重试。'
    console.error(err)
  } finally {
    // 数据返回过快时补足最短展示时长；期间若触发新一轮加载则交由其接管
    const remainMs = Math.max(0, MIN_LOADING_MS - (Date.now() - startedAt))
    if (remainMs > 0) {
      await new Promise((resolve) => setTimeout(resolve, remainMs))
    }
    if (token === loadToken) {
      loading.value = false
    }
  }
}

const hydrateHomeImages = () => {
  const container = document.querySelector('#home-featured-preview')
  if (!container) return
  container.querySelectorAll('img').forEach((img) => {
    if (img.closest('.github-snake')) return
    img.loading = 'lazy'
    img.fetchPriority = 'low'
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

const getRevealElements = (root) => {
  const elements = []
  const firstMedia = root.querySelector('.article-markdown__figure')
  if (firstMedia) elements.push(firstMedia)

  const introHeading = Array.from(root.querySelectorAll('h2')).find(
    (heading) => heading.textContent?.trim() === '前言',
  )
  if (!introHeading) return elements

  elements.push(introHeading)
  let sibling = introHeading.nextElementSibling
  while (sibling && sibling.tagName !== 'HR') {
    if (sibling.matches('p, ul, ol, blockquote, pre, table, figure')) {
      elements.push(sibling)
    }
    sibling = sibling.nextElementSibling
  }
  return elements
}

const setupTitleTyped = async () => {
  titleTyped?.destroy()
  titleTyped = null

  const root = document.querySelector('#home-featured-preview')
  const heading = root?.querySelector('h1')
  if (!heading) return

  const currentTitle = String(heading.textContent || '').trim()
  const title = heading.dataset.typewriterText || currentTitle
  if (title !== 'Kun Xing') return
  heading.dataset.typewriterText = title
  heading.setAttribute('aria-label', title)

  if (window.matchMedia?.('(prefers-reduced-motion: reduce)').matches) {
    heading.textContent = title
    return
  }

  heading.textContent = ''
  const Typed = await loadTyped()
  titleTyped = new Typed(heading, {
    strings: [title],
    typeSpeed: 180,
    startDelay: 300,
    showCursor: true,
    cursorChar: '|',
    contentType: 'text',
  })
}

const setupHomeMotion = () => {
  motionObserver?.disconnect()
  motionObserver = null

  const root = document.querySelector('#home-featured-preview')
  const commentElement = commentsRef.value?.$el
  const revealElements = root ? getRevealElements(root) : []

  if (window.matchMedia?.('(prefers-reduced-motion: reduce)').matches) {
    commentsVisible.value = true
    return
  }

  revealElements.forEach((element, index) => {
    element.classList.add('home-intro-reveal')
    element.style.setProperty('--home-reveal-delay', `${Math.min(index * 75, 300)}ms`)
  })

  const reveal = (element) => {
    if (element === commentElement) {
      commentsVisible.value = true
      return
    }
    element.classList.add('is-visible')
  }

  if (!('IntersectionObserver' in window)) {
    revealElements.forEach(reveal)
    if (commentElement) reveal(commentElement)
    return
  }

  motionObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return
        reveal(entry.target)
        motionObserver?.unobserve(entry.target)
      })
    },
    {
      threshold: 0.1,
      rootMargin: '0px 0px -8% 0px',
    },
  )

  revealElements.forEach((element) => motionObserver.observe(element))
  if (commentElement) motionObserver.observe(commentElement)
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
      setupTitleTyped()
      setupHomeMotion()
    })
  },
)

onUnmounted(() => {
  mediaObserver?.disconnect?.()
  mediaObserver = null
  motionObserver?.disconnect?.()
  motionObserver = null
  titleTyped?.destroy()
  titleTyped = null
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

.article-body {
  transition:
    transform 0.28s ease,
    filter 0.28s ease;
}

.article-body:hover {
  transform: translateY(-2px);
  filter: saturate(1.03);
}

.article-body {
  width: min(100%, 760px);
  margin: 0 auto;
}

.article-body :deep(img) {
  display: block;
  visibility: visible !important;
  opacity: 1 !important;
  max-width: 100%;
  height: auto;
  margin: 20px auto;
}

.home-content :deep(.home-intro-reveal) {
  opacity: 0;
  transform: translate3d(0, 24px, 0);
  transition:
    opacity 0.9s cubic-bezier(0.2, 0.8, 0.2, 1),
    transform 0.9s cubic-bezier(0.2, 0.8, 0.2, 1);
  transition-delay: var(--home-reveal-delay, 0ms);
  will-change: opacity, transform;
}

.home-content :deep(.home-intro-reveal.is-visible) {
  opacity: 1;
  transform: translate3d(0, 0, 0);
}

.home-content :deep(.typed-cursor) {
  color: var(--accent);
  font-weight: 400;
  animation: homeTypedCursor 0.75s step-end infinite;
}

.home-content :deep(h1[data-typewriter-text]) {
  display: inline-block;
}

.home-comments {
  margin-top: 40px;
  opacity: 0;
  transform: translate3d(0, 34px, 0);
  transition:
    opacity 0.95s cubic-bezier(0.2, 0.8, 0.2, 1),
    transform 0.95s cubic-bezier(0.2, 0.8, 0.2, 1);
  will-change: opacity, transform;
}

.home-comments--visible {
  opacity: 1;
  transform: translate3d(0, 0, 0);
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

@keyframes homeTypedCursor {
  0%,
  45% {
    opacity: 1;
  }
  46%,
  100% {
    opacity: 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .home-content,
  .home-block,
  .article-body {
    animation: none !important;
  }

  .article-body {
    transition: none;
  }

  .home-comments,
  .home-content :deep(.home-intro-reveal) {
    opacity: 1 !important;
    transform: none !important;
    transition: none !important;
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
