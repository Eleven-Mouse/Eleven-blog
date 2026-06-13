<template>
  <div class="article-page">
    <div v-if="loading" class="loading-tip">文章加载中...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <template v-if="article">
      <div
        class="article-page__workspace"
        :class="{
          'is-topic-open': uiStore.topicTreeOpen && showTopicTreePanel,
          'is-toc-open': showDesktopTocPanel,
          'is-panel-ready': panelTransitionReady,
        }"
      >
        <aside v-if="showTopicTreePanel" class="article-page__topic-panel">
          <div class="topic-panel-shell">
            <div class="topic-panel-shell__body">
              <TopicTreeSidebar
                :active-topic-id="article.categoryId"
                :active-article-id="article.id"
              />
            </div>
          </div>
        </aside>

        <div class="article-page__layout page-container">
          <!-- Main Column: header + content + comments -->
          <div class="article-page__main">
            <!-- Article Header -->
            <header class="article-page__header">
              <h1 class="article-page__title">{{ article.title }}</h1>
              <div class="article-page__meta">
                <span>
                  <svg
                    viewBox="0 0 16 16"
                    width="14"
                    height="14"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.5"
                  >
                    <circle cx="8" cy="8" r="6" />
                    <path d="M8 4v4l2.5 1.5" />
                  </svg>
                  {{ formatTime(article.publishTime) }}
                </span>
                <span v-if="article.categoryName">
                  <svg
                    viewBox="0 0 16 16"
                    width="14"
                    height="14"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.5"
                  >
                    <path d="M2 4h12v9H2z" />
                    <path d="M6 4V2h4v2" />
                  </svg>
                  {{ article.categoryName }}
                </span>
                <span v-if="article.viewCount">
                  <svg
                    viewBox="0 0 16 16"
                    width="14"
                    height="14"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.5"
                  >
                    <path d="M1 8s3-5 7-5 7 5 7 5-3 5-7 5-7-5-7-5z" />
                    <circle cx="8" cy="8" r="2" />
                  </svg>
                  {{ article.viewCount }} 阅读
                </span>
              </div>
            </header>

            <!-- Article Content -->
            <article class="article-page__content animate-fade-in-up">
              <ArticleMarkdown
                content-id="article-markdown-preview"
                :content="renderedArticleContent"
                heading-prefix="heading"
                @catalog-change="onCatalogChange"
                @image-click="openLightbox"
              />
            </article>
          </div>
        </div>

        <aside v-if="showDesktopTocPanel" class="article-page__toc-panel">
          <div class="topic-panel-shell">
            <div class="topic-panel-shell__body article-page__toc-body">
              <nav v-if="catalogList.length" class="toc-nav toc-nav--desktop">
                <a
                  v-for="item in catalogList"
                  :key="item.uniqueId"
                  :href="`#${item.uniqueId}`"
                  class="toc-link"
                  :class="[
                    `toc-link--${item.level}`,
                    { 'toc-link--active': activeHeading === item.uniqueId },
                  ]"
                  @click.prevent="handleDesktopTocClick(item.uniqueId)"
                  >{{ item.text }}</a
                >
              </nav>
              <div v-else class="toc-empty">暂无目录</div>
            </div>
          </div>
        </aside>
      </div>
    </template>

    <!-- TOC floating button (mobile only) -->
    <button v-if="article && !loading && !error" class="mobile-toc-btn" @click="showMobileToc = true">
      <svg
        viewBox="0 0 24 24"
        width="20"
        height="20"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
      >
        <path d="M4 6h16M4 12h16M4 18h10" />
      </svg>
    </button>

    <el-drawer v-model="showMobileToc" direction="rtl" size="260px" class="mobile-toc-drawer">
      <template #header>
        <span style="font-weight: 600">目录导航</span>
      </template>
      <nav v-if="catalogList.length" class="toc-nav">
        <a
          v-for="item in catalogList"
          :key="item.uniqueId"
          :href="`#${item.uniqueId}`"
          class="toc-link"
          :class="`toc-link--${item.level}`"
          @click.prevent="handleMobileTocClick(item.uniqueId)"
          >{{ item.text }}</a
        >
      </nav>
      <div v-else class="toc-empty">暂无目录</div>
    </el-drawer>

    <!-- Lightbox -->
    <teleport to="body">
      <div v-if="lightboxSrc" class="lightbox-overlay" @click="lightboxSrc = ''">
        <img :src="lightboxSrc" alt="preview" />
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, nextTick, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticleById } from '@/api/article.js'
import TopicTreeSidebar from '@/components/TopicTreeSidebar.vue'
import { useUiStore } from '@/stores/ui'
import { transformObsidianAssetLinks } from '@/utils/markdownAssets'
import ArticleMarkdown from '@/components/ArticleMarkdown.vue'

const route = useRoute()
const uiStore = useUiStore()
const article = ref(null)
const loading = ref(false)
const error = ref(null)
const catalogList = ref([])
const showMobileToc = ref(false)
const lightboxSrc = ref('')
const activeHeading = ref('')
const showDesktopTocPanel = computed(() => Boolean(showTopicTreePanel.value && !loading.value && !error.value))
const showTopicTreePanel = computed(() => article.value && article.value.title !== '首页')
const panelTransitionReady = ref(false)
const renderedArticleContent = computed(() => transformObsidianAssetLinks(article.value?.content || ''))

const onCatalogChange = (list) => {
  catalogList.value = Array.isArray(list) ? list : []
  nextTick(() => {
    updateActiveHeading()
  })
}

const scrollToHeading = (id) => {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const handleMobileTocClick = (id) => {
  showMobileToc.value = false
  nextTick(() => scrollToHeading(id))
}

const handleDesktopTocClick = (id) => {
  activeHeading.value = id
  nextTick(() => scrollToHeading(id))
}

const formatTime = (datetime) => {
  if (!datetime) return '--'
  return new Date(datetime).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

const openLightbox = (src) => {
  lightboxSrc.value = src || ''
}

const updateActiveHeading = () => {
  const headings = catalogList.value
    .map((item) => ({
      id: item.uniqueId,
      el: document.getElementById(item.uniqueId),
    }))
    .filter((h) => h.el)

  if (!headings.length) {
    activeHeading.value = ''
    return
  }

  activeHeading.value = headings[0].id
  for (let i = headings.length - 1; i >= 0; i--) {
    if (headings[i].el.getBoundingClientRect().top <= 120) {
      activeHeading.value = headings[i].id
      break
    }
  }
}

onMounted(async () => {
  uiStore.setTopicTreeOpen(true)
  const articleId = route.params.id
  if (!articleId) {
    error.value = '未找到文章ID'
    return
  }

  loading.value = true
  try {
    const data = await fetchArticleById(articleId)
    article.value = data || null
  } catch (err) {
    error.value = '加载文章失败，请稍后再试。'
    console.error(err)
  } finally {
    loading.value = false
  }

  window.addEventListener('scroll', updateActiveHeading, { passive: true })
})

watch(
  () => showTopicTreePanel.value,
  (show) => {
    if (!show) {
      panelTransitionReady.value = false
      return
    }
    panelTransitionReady.value = false
    nextTick(() => {
      requestAnimationFrame(() => {
        panelTransitionReady.value = true
      })
    })
  },
  { immediate: true },
)

const onKeydown = (e) => {
  if (e.key === 'Escape' && lightboxSrc.value) lightboxSrc.value = ''
}

onMounted(() => window.addEventListener('keydown', onKeydown))
onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown)
  window.removeEventListener('scroll', updateActiveHeading)
})
</script>

<style scoped>
.article-page {
  padding-top: 65px;
}
/* ---------- Layout ---------- */
.article-page__workspace {
  position: relative;
  --topic-panel-width: 360px;
  --toc-panel-width: 360px;
  --topic-panel-gap: 48px;
  --toc-panel-gap: 48px;
}

.article-page__topic-panel {
  position: fixed;
  top: 56px;
  left: 0;
  height: calc(100vh - 56px);
  width: var(--topic-panel-width);
  opacity: 0;
  transform: translate3d(-100%, 0, 0);
  pointer-events: none;
  will-change: transform, opacity;
  backface-visibility: hidden;
  contain: layout paint;
  transition: none;
}

.article-page__toc-panel {
  position: fixed;
  top: 56px;
  right: 0;
  height: calc(100vh - 56px);
  width: var(--toc-panel-width);
  opacity: 0;
  transform: translate3d(100%, 0, 0);
  pointer-events: none;
  will-change: transform, opacity;
  backface-visibility: hidden;
  contain: layout paint;
  transition: none;
}

.article-page__workspace.is-panel-ready .article-page__topic-panel,
.article-page__workspace.is-panel-ready .article-page__toc-panel {
  transition:
    opacity 0.24s ease,
    transform 0.28s cubic-bezier(0.4, 0, 0.2, 1);
}

.article-page__workspace.is-topic-open .article-page__topic-panel {
  opacity: 1;
  transform: translate3d(0, 0, 0);
  pointer-events: auto;
}

.article-page__workspace.is-toc-open .article-page__toc-panel {
  opacity: 1;
  transform: translate3d(0, 0, 0);
  pointer-events: auto;
}

.article-page__layout {
  width: 100%;
  min-width: 0;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 0px;
}

.article-page__main {
  min-width: 0;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: block;
}

.article-page__workspace.is-topic-open .article-page__layout {
  width: calc(100% - var(--topic-panel-width) - var(--topic-panel-gap));
  max-width: none;
  margin-left: calc(var(--topic-panel-width) + var(--topic-panel-gap));
  margin-right: 0;
  padding: 0 28px;
}

.article-page__workspace.is-toc-open .article-page__layout {
  width: calc(100% - var(--toc-panel-width) - var(--toc-panel-gap));
  max-width: none;
  margin-left: 0;
  margin-right: calc(var(--toc-panel-width) + var(--toc-panel-gap));
  padding: 0 28px;
}

.article-page__workspace.is-topic-open.is-toc-open .article-page__layout {
  width: calc(100% - var(--topic-panel-width) - var(--topic-panel-gap) - var(--toc-panel-width) - var(--toc-panel-gap));
  margin-left: calc(var(--topic-panel-width) + var(--topic-panel-gap));
  margin-right: calc(var(--toc-panel-width) + var(--toc-panel-gap));
}

.article-page__workspace.is-topic-open .article-page__main,
.article-page__workspace.is-toc-open .article-page__main {
  max-width: none;
  width: 100%;
  margin: 0;
}

.topic-panel-shell {
  height: calc(100vh - 74px);
  background: transparent;
  border: none;
  border-radius: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.topic-panel-shell__body {
  flex: 1;
  min-height: 0;
}

.article-page__toc-body {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding: 14px;
}

.article-page__toc-body::-webkit-scrollbar {
  width: 0;
  height: 0;
}

:deep(.article-page__topic-panel .topic-drawer) {
  position: static;
  top: auto;
  max-height: none;
  height: 100%;
  border: none;
  border-radius: 0;
  box-shadow: none;
  padding: 10px;
}

:deep(.article-page__topic-panel .topic-drawer__title) {
  display: none;
}

/* ---------- Header ---------- */
.article-page__header {
  padding-bottom: 40px;
  border-bottom: 1px solid var(--border-light);
  margin-bottom: 48px;
  animation: fadeInUp 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.article-page__title {
  font-size: 2.25rem;
  font-weight: 800;
  line-height: 1.3;
  color: var(--text-primary);
  margin-bottom: 18px;
  letter-spacing: -0.025em;
}

.article-page__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  font-size: 13px;
  color: var(--text-muted);
}

.article-page__meta span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

/* ---------- Content ---------- */
.article-page__header,
.article-page__content {
  width: min(100%, 860px);
  margin-left: auto;
  margin-right: auto;
}

.article-page__content {
  padding-bottom: 32px;
}

/* ---------- TOC Floating Button ---------- */
.mobile-toc-btn {
  position: fixed;
  bottom: 24px;
  right: 24px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--accent);
  color: #fff;
  border: none;
  box-shadow: var(--shadow-md);
  cursor: pointer;
  z-index: 100;
  display: none;
  align-items: center;
  justify-content: center;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.mobile-toc-btn:hover {
  transform: scale(1.08);
  box-shadow: var(--shadow-glow);
}

/* ---------- TOC Drawer Nav ---------- */
.toc-nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.toc-nav--desktop {
  padding: 6px 0;
}

.toc-link {
  display: block;
  padding: 5px 0;
  font-size: 13px;
  color: var(--text-muted);
  text-decoration: none;
  line-height: 1.5;
  transition: color var(--transition-fast);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-left: 10px;
}

.toc-link:hover {
  color: var(--accent);
}

.toc-link--active {
  color: var(--accent);
  background: var(--accent-light);
  border-radius: var(--radius-sm);
}

.toc-link--2 {
  padding-left: 22px;
}
.toc-link--3 {
  padding-left: 34px;
}
.toc-link--4 {
  padding-left: 46px;
}

.toc-empty {
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
  padding: 8px 0;
}

/* ---------- Responsive ---------- */
@media (max-width: 768px) {
  .article-page__workspace {
    display: block;
  }
  .article-page__layout {
    padding: 0 16px;
  }
  .article-page__topic-panel,
  .article-page__toc-panel {
    display: none;
  }
  .article-page__main {
    max-width: 100%;
  }

  .article-page__header {
    padding-bottom: 24px;
    margin-bottom: 28px;
  }

  .article-page__title {
    font-size: 1.5rem;
  }

  .article-page__meta {
    gap: 10px;
    font-size: 12px;
  }

  .mobile-toc-btn {
    bottom: 16px;
    right: 16px;
    width: 44px;
    height: 44px;
    display: flex;
  }
}

@media (max-width: 1024px) {
  .article-page__workspace {
    display: block;
  }
  .article-page__layout {
    padding: 0 24px;
    transform: none !important;
    width: 100% !important;
    margin-left: 0 !important;
    margin-right: 0 !important;
  }
  .article-page__topic-panel,
  .article-page__toc-panel {
    display: none;
  }
  .article-page__workspace.is-topic-open .article-page__main,
  .article-page__workspace.is-toc-open .article-page__main {
    max-width: 100%;
  }
  .article-page__main {
    max-width: 100%;
  }
  .mobile-toc-btn {
    display: flex;
  }
}
</style>
