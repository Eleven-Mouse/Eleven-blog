<template>
  <section class="giscus-comments">
    <h3 class="giscus-comments__title">评论</h3>
    <div class="giscus-comments__body">
      <div v-if="!loaded" class="giscus-comments__placeholder">评论加载中...</div>
      <div ref="containerRef" class="giscus-comments__container" />
    </div>
  </section>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useThemeStore } from '@/stores/theme'
import lightTheme from '../../public/giscus-light.css?raw'
import darkTheme from '../../public/giscus-dark.css?raw'

const config = {
  repo: String(import.meta.env.VITE_GISCUS_REPO || 'Eleven-Mouse/Eleven-blog').trim(),
  repoId: String(import.meta.env.VITE_GISCUS_REPO_ID || 'R_kgDOQxLe7w').trim(),
  category: String(import.meta.env.VITE_GISCUS_CATEGORY || 'Announcements').trim(),
  categoryId: String(
    import.meta.env.VITE_GISCUS_CATEGORY_ID || 'DIC_kwDOQxLe784DGJ97',
  ).trim(),
  mapping: String(import.meta.env.VITE_GISCUS_MAPPING || 'pathname').trim(),
  strict: String(import.meta.env.VITE_GISCUS_STRICT || '0').trim(),
  reactionsEnabled: String(import.meta.env.VITE_GISCUS_REACTIONS_ENABLED || '1').trim(),
  emitMetadata: String(import.meta.env.VITE_GISCUS_EMIT_METADATA || '0').trim(),
  inputPosition: String(import.meta.env.VITE_GISCUS_INPUT_POSITION || 'bottom').trim(),
  lang: String(import.meta.env.VITE_GISCUS_LANG || 'zh-CN').trim(),
}

const containerRef = ref(null)
const loaded = ref(false)
const themeStore = useThemeStore()
let commentsObserver = null
let frameObserver = null
let commentsLoaded = false

const giscusTheme = () => {
  const themeCss = themeStore.theme === 'dark' ? darkTheme : lightTheme
  return `data:text/css;charset=utf-8,${encodeURIComponent(themeCss)}`
}

const watchFrame = () => {
  frameObserver?.disconnect()
  frameObserver = new MutationObserver(() => {
    const frame = containerRef.value?.querySelector('iframe.giscus-frame')
    if (!frame || frame.dataset.loadListenerAttached === 'true') return

    frame.dataset.loadListenerAttached = 'true'
    frame.addEventListener(
      'load',
      () => {
        loaded.value = true
        frameObserver?.disconnect()
        frameObserver = null
      },
      { once: true },
    )
  })
  frameObserver.observe(containerRef.value, { childList: true, subtree: true })
}

const loadComments = () => {
  const container = containerRef.value
  if (!container || commentsLoaded) return
  if (!config.repo || !config.repoId || !config.categoryId) {
    console.error('Giscus 配置不完整，请检查 VITE_GISCUS_* 环境变量。')
    return
  }

  commentsLoaded = true
  watchFrame()

  const script = document.createElement('script')
  script.src = 'https://giscus.app/client.js'
  script.async = true
  script.crossOrigin = 'anonymous'
  script.dataset.repo = config.repo
  script.dataset.repoId = config.repoId
  script.dataset.category = config.category
  script.dataset.categoryId = config.categoryId
  script.dataset.mapping = config.mapping
  script.dataset.strict = config.strict
  script.dataset.reactionsEnabled = config.reactionsEnabled
  script.dataset.emitMetadata = config.emitMetadata
  script.dataset.inputPosition = config.inputPosition
  script.dataset.theme = giscusTheme()
  script.dataset.lang = config.lang
  script.dataset.loading = 'lazy'
  container.appendChild(script)
}

const syncTheme = () => {
  if (!commentsLoaded) return
  const frame = containerRef.value?.querySelector('iframe.giscus-frame')
  frame?.contentWindow?.postMessage(
    { giscus: { setConfig: { theme: giscusTheme() } } },
    'https://giscus.app',
  )
}

const observeComments = () => {
  const container = containerRef.value
  if (!container) return
  if (!('IntersectionObserver' in window)) {
    loadComments()
    return
  }

  commentsObserver = new IntersectionObserver(
    (entries) => {
      if (!entries.some((entry) => entry.isIntersecting)) return
      loadComments()
      commentsObserver?.disconnect()
      commentsObserver = null
    },
    { rootMargin: '1000px 0px' },
  )
  commentsObserver.observe(container)
}

onMounted(observeComments)
watch(() => themeStore.theme, syncTheme)
onBeforeUnmount(() => {
  commentsObserver?.disconnect()
  frameObserver?.disconnect()
  commentsObserver = null
  frameObserver = null
  if (containerRef.value) containerRef.value.innerHTML = ''
})
</script>

<style scoped>
.giscus-comments {
  width: min(100%, 920px);
  margin: 48px auto 0;
  padding: 20px;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  box-shadow: none;
}

html:not([data-theme='dark']) .giscus-comments {
  background: var(--bg-primary);
}

.giscus-comments__title {
  margin: 0 0 14px;
  color: var(--text-primary);
  font-size: 1.15rem;
}

.giscus-comments__body {
  position: relative;
  min-height: 180px;
}

.giscus-comments__placeholder {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  min-height: 180px;
  color: var(--text-muted);
  font-size: 13px;
}

.giscus-comments__container {
  min-height: 180px;
}

@media (max-width: 768px) {
  .giscus-comments {
    padding: 14px;
    border-radius: var(--radius-sm);
  }
}
</style>
