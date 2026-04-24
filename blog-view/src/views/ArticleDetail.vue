<template>
  <div class="article-page">
    <div v-if="loading" class="loading-tip">文章加载中...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>

    <template v-if="article">
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
            <MdPreview
              editorId="preview-only"
              :modelValue="article.content"
              @onGetCatalog="onGetCatalog"
              :headingId="(index) => `heading-${index}`"
              :markedHeadingId="(index) => `heading-${index}`"
            />
          </article>

          <!-- Comments -->
          <div class="article-page__comments">
            <el-divider />
            <CommentsCard v-if="article.id" :blog-id="article.id" />
          </div>
        </div>
      </div>
    </template>

    <!-- TOC floating button (all screen sizes) -->
    <button v-if="catalogList.length > 0" class="mobile-toc-btn" @click="showMobileToc = true">
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
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
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
const showMobileToc = ref(false)
const lightboxSrc = ref('')
const activeHeading = ref('')

const onGetCatalog = (list) => {
  catalogList.value = list.map((item, index) => ({
    ...item,
    uniqueId: `heading-${index}`,
  }))
  nextTick(() => {
    const previewEl = document.querySelector('#preview-only .md-editor-preview')
    if (!previewEl) return
    previewEl.querySelectorAll('h1, h2, h3, h4, h5, h6').forEach((el, i) => {
      el.setAttribute('id', `heading-${i}`)
    })
    setupLightbox(previewEl)
    setupCodeCopy(previewEl)
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

const formatTime = (datetime) => {
  if (!datetime) return '--'
  return new Date(datetime).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

const setupLightbox = (container) => {
  container.querySelectorAll('img').forEach((img) => {
    img.style.cursor = 'zoom-in'
    img.addEventListener('click', () => {
      lightboxSrc.value = img.src
    })
  })
}

const setupCodeCopy = (container) => {
  container.querySelectorAll('pre').forEach((pre) => {
    pre.style.position = 'relative'
    const btn = document.createElement('button')
    btn.className = 'code-copy-btn'
    btn.textContent = 'Copy'
    btn.addEventListener('click', async () => {
      const code = pre.querySelector('code')
      if (code) {
        await navigator.clipboard.writeText(code.textContent)
        btn.textContent = 'Copied!'
        setTimeout(() => {
          btn.textContent = 'Copy'
        }, 2000)
      }
    })
    pre.appendChild(btn)
  })
}

const updateActiveHeading = () => {
  const headings = catalogList.value
    .map((item) => ({
      id: item.uniqueId,
      el: document.getElementById(item.uniqueId),
    }))
    .filter((h) => h.el)

  for (let i = headings.length - 1; i >= 0; i--) {
    if (headings[i].el.getBoundingClientRect().top <= 120) {
      activeHeading.value = headings[i].id
      break
    }
  }
}

onMounted(async () => {
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
.article-page__main {
  max-width: 760px;
  margin: 0 auto;
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

/* ---------- Content — Premium Reading ---------- */
.article-page__content :deep(.md-editor-preview) {
  background: transparent !important;
  padding: 0 !important;
  font-family:
    'Inter',
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    'PingFang SC',
    'Hiragino Sans GB',
    'Microsoft YaHei',
    sans-serif;
}

.article-page__content :deep(.md-editor-preview p) {
  font-size: 17px;
  line-height: 1.9;
  color: var(--text-primary);
  margin: 0 0 24px;
}

.article-page__content :deep(.md-editor-preview h1),
.article-page__content :deep(.md-editor-preview h2),
.article-page__content :deep(.md-editor-preview h3),
.article-page__content :deep(.md-editor-preview h4) {
  color: var(--text-primary);
  font-weight: 700;
  margin: 48px 0 20px;
  line-height: 1.4;
  scroll-margin-top: 80px;
  letter-spacing: -0.02em;
}

.article-page__content :deep(.md-editor-preview h2) {
  font-size: 1.5rem;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-light);
}

.article-page__content :deep(.md-editor-preview h3) {
  font-size: 1.25rem;
}

.article-page__content :deep(.md-editor-preview blockquote) {
  margin: 28px 0;
  padding: 16px 24px;
  border-left: 4px solid var(--accent);
  background: var(--accent-light);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
  color: var(--text-secondary);
  font-style: italic;
}

.article-page__content :deep(.md-editor-preview code:not(pre code)) {
  padding: 2px 8px;
  font-size: 14px;
  background: var(--bg-inline-code);
  color: var(--accent);
  border-radius: 6px;
  font-family: 'SF Mono', 'Fira Code', Consolas, monospace;
  font-weight: 500;
}

.article-page__content :deep(.md-editor-preview pre) {
  margin: 32px 0;
  padding: 24px;
  background: var(--bg-code);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.article-page__content :deep(.md-editor-preview pre code) {
  font-size: 14px;
  line-height: 1.7;
  font-family: 'SF Mono', 'Fira Code', Consolas, monospace;
}

.article-page__content :deep(.md-editor-preview img) {
  border-radius: var(--radius-md);
  margin: 24px 0;
  max-width: 100%;
  box-shadow: var(--shadow-sm);
}

.article-page__content :deep(.md-editor-preview a) {
  color: var(--accent);
  text-decoration: underline;
  text-underline-offset: 3px;
  text-decoration-thickness: 1px;
}

.article-page__content :deep(.md-editor-preview ul),
.article-page__content :deep(.md-editor-preview ol) {
  padding-left: 24px;
  margin-bottom: 24px;
}

.article-page__content :deep(.md-editor-preview li) {
  margin: 8px 0;
  line-height: 1.9;
  color: var(--text-primary);
}

.article-page__content :deep(.md-editor-preview table) {
  width: 100%;
  border-collapse: collapse;
  margin: 28px 0;
  font-size: 14.5px;
}

.article-page__content :deep(.md-editor-preview th),
.article-page__content :deep(.md-editor-preview td) {
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  text-align: left;
}

.article-page__content :deep(.md-editor-preview th) {
  background: var(--bg-secondary);
  font-weight: 600;
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
  display: flex;
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

/* ---------- Code Copy Button ---------- */
:global(.code-copy-btn) {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 10px;
  font-size: 12px;
  color: var(--text-muted);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  opacity: 0;
  transition:
    opacity 0.2s,
    background 0.2s;
}

:global(.code-copy-btn:hover) {
  background: var(--accent);
  color: #fff;
  border-color: var(--accent);
}

:global(pre:hover .code-copy-btn) {
  opacity: 1;
}

/* ---------- TOC Drawer Nav ---------- */
.toc-nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
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

/* ---------- Comments ---------- */
.article-page__comments {
  margin-top: 56px;
}

.article-page__comments :deep(.el-divider) {
  border-color: var(--border-light);
}

/* ---------- Responsive ---------- */
@media (max-width: 768px) {
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

  .article-page__content :deep(.md-editor-preview pre) {
    padding: 14px;
    font-size: 13px;
  }

  .mobile-toc-btn {
    bottom: 16px;
    right: 16px;
    width: 44px;
    height: 44px;
  }
}
</style>
