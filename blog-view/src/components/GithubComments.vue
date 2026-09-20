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

const utterancesTheme = () => (themeStore.theme === 'dark' ? 'github-dark' : 'github-light')

const loadComments = () => {
  const container = containerRef.value
  if (!container) return

  container.innerHTML = ''
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

onMounted(loadComments)
watch(() => themeStore.theme, syncTheme)
onBeforeUnmount(() => {
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
