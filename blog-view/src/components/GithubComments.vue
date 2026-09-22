<template>
  <section class="github-comments">
    <h3 class="github-comments__title">评论</h3>
    <div ref="containerRef" class="github-comments__container" />
  </section>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useThemeStore } from '@/stores/theme'

const repo = String(import.meta.env.VITE_UTTERANCES_REPO || 'Eleven-Mouse/Eleven-blog').trim()
const containerRef = ref(null)
const themeStore = useThemeStore()
let commentsObserver = null
let commentsLoaded = false

const utterancesTheme = () => (themeStore.theme === 'dark' ? 'github-dark' : 'github-light')

const loadComments = () => {
  const container = containerRef.value
  if (!container || commentsLoaded) return
  commentsLoaded = true

  const script = document.createElement('script')
  script.src = 'https://utteranc.es/client.js'
  script.async = true
  script.crossOrigin = 'anonymous'
  script.setAttribute('repo', repo)
  script.setAttribute('issue-term', 'pathname')
  script.setAttribute('label', 'comment')
  script.setAttribute('theme', utterancesTheme())
  container.appendChild(script)
}

const syncTheme = () => {
  if (!commentsLoaded) return
  const frame = containerRef.value?.querySelector('iframe.utterances-frame')
  if (!frame?.contentWindow) {
    loadComments()
    return
  }
  frame.contentWindow.postMessage(
    { type: 'set-theme', theme: utterancesTheme() },
    'https://utteranc.es',
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
    { rootMargin: '400px 0px' },
  )
  commentsObserver.observe(container)
}

onMounted(observeComments)
watch(() => themeStore.theme, syncTheme)
onBeforeUnmount(() => {
  commentsObserver?.disconnect()
  commentsObserver = null
  if (containerRef.value) containerRef.value.innerHTML = ''
})
</script>

<style scoped>
.github-comments {
  width: min(100%, 920px);
  margin: 48px auto 0;
}

.github-comments__title {
  margin: 0 0 18px;
  color: var(--text-primary);
  font-size: 1.35rem;
}

.github-comments__container {
  min-height: 180px;
}
</style>
