<template>
  <header class="navbar">
    <div class="navbar__inner page-container">
      <router-link to="/home" class="navbar__logo">
        <span class="navbar__logo-text">Kunxing</span>
        <span class="navbar__logo-dot">.</span>
      </router-link>

      <nav class="navbar__nav">
        <router-link to="/home" class="navbar__nav-item" :class="{ 'is-active': isHomeActive }">
          棣栭〉
        </router-link>
        <router-link
          v-for="topic in topics"
          :key="`topic-nav-${topic.id}`"
          :to="`/topic/${topic.id}`"
          class="navbar__nav-item"
          :class="{ 'is-active': isTopicItemActive(topic) }"
        >
          {{ topic.name }}
        </router-link>
      </nav>

      <div class="navbar__actions">
        <button
          v-if="isArticleRoute"
          class="navbar__icon-btn topic-tree-toggle-btn"
          :class="{ 'is-active': uiStore.topicTreeOpen }"
          @click="toggleTopicTree"
          aria-label="目录树"
        >
          <svg
            viewBox="0 0 24 24"
            width="18"
            height="18"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path d="M4 6h16M4 12h16M4 18h10" />
          </svg>
        </button>
        <button class="navbar__icon-btn" @click="toggleSearch" aria-label="Search">
          <svg
            viewBox="0 0 24 24"
            width="18"
            height="18"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <circle cx="11" cy="11" r="7" />
            <path d="M21 21l-4.35-4.35" />
          </svg>
        </button>
        <a
          :href="blogConfig.config.blog_github_url"
          target="_blank"
          class="navbar__icon-btn"
          aria-label="GitHub"
        >
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path
              d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.604-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.464-1.11-1.464-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.294 2.747-1.025 2.747-1.025.546 1.377.202 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.163 22 16.418 22 12c0-5.523-4.477-10-10-10z"
            />
          </svg>
        </a>
        <ThemeToggle />
        <button class="navbar__hamburger" @click="drawerOpen = true" aria-label="Menu">
          <span /><span /><span />
        </button>
        <button
          v-if="canManualSync"
          class="navbar__icon-btn"
          :class="{ 'is-disabled': syncLoading }"
          :disabled="syncLoading"
          @click="handleManualSync"
          :aria-label="syncLoading ? '姝ｅ湪鍚屾鏂囩珷' : '鍚屾鏂囩珷'"
          :title="syncLoading ? '姝ｅ湪鍚屾鏂囩珷' : '鍚屾鏂囩珷'"
        >
          <RefreshRight :class="{ 'is-spinning': syncLoading }" />
        </button>
      </div>
    </div>

    <div class="navbar__progress" :style="{ width: scrollProgress + '%' }" />
  </header>

  <!-- Search Overlay (teleported to body for full-page coverage) -->
  <teleport to="body">
    <transition name="fade">
      <div v-if="searchOpen" class="search-overlay" @click.self="toggleSearch">
        <div class="search-panel">
          <div class="search-panel__header">
            <svg
              class="search-panel__icon"
              viewBox="0 0 24 24"
              width="20"
              height="20"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <circle cx="11" cy="11" r="7" />
              <path d="M21 21l-4.35-4.35" />
            </svg>
            <input
              ref="searchInputRef"
              v-model="searchQuery"
              class="search-panel__input"
              placeholder="鎼滅储鏂囩珷..."
              @input="onSearchInput"
              @keydown.enter="onSearchEnter"
              @keydown.down.prevent="highlightNext"
              @keydown.up.prevent="highlightPrev"
              autocomplete="off"
            />
            <div class="search-panel__hints">
              <kbd v-if="searchQuery">Enter</kbd>
              <kbd>Esc</kbd>
            </div>
          </div>

          <div v-if="searchLoading" class="search-panel__loading">
            <div class="search-spinner" />
            <span>鎼滅储涓?..</span>
          </div>

          <div v-else-if="searchResults.length" class="search-panel__results">
            <div
              v-for="(item, idx) in searchResults"
              :key="item.id"
              class="search-result-item"
              :class="{ 'is-highlighted': idx === highlightedIndex }"
              @click="handleSelectArticle(item)"
              @mouseenter="highlightedIndex = idx"
            >
              <div class="search-result-item__title" v-html="highlightText(item.title)" />
              <div class="search-result-item__meta">
                <span v-if="item.categoryName" class="search-result-item__cat">{{
                  item.categoryName
                }}</span>
                <span v-if="item.publishTime" class="search-result-item__date">{{
                  formatDate(item.publishTime)
                }}</span>
              </div>
              <p v-if="item.summary" class="search-result-item__summary">{{ item.summary }}</p>
            </div>
          </div>

          <div
            v-else-if="searchQuery && searchFetched && !searchLoading"
            class="search-panel__empty"
          >
            娌℃湁鎵惧埌鐩稿叧鏂囩珷
          </div>

          <div v-else-if="!searchQuery" class="search-panel__hint">
            杈撳叆鍏抽敭璇嶆悳绱㈡枃绔狅紝鎸?Enter 鏌ョ湅鍏ㄩ儴缁撴灉
          </div>
        </div>
      </div>
    </transition>
  </teleport>

  <!-- Mobile Drawer -->
  <teleport to="body">
    <el-drawer
      v-model="drawerOpen"
      direction="ltr"
      size="280px"
      :show-close="false"
      class="mobile-drawer"
    >
      <div class="mobile-directory">
        <div v-if="drawerTreeLoading" class="mobile-directory__tip">姝ｅ湪鍔犺浇鐩綍...</div>
        <div v-else-if="drawerTreeError" class="mobile-directory__tip mobile-directory__tip--error">{{ drawerTreeError }}</div>
        <div v-else-if="mobileTree.length" class="mobile-tree">
          <section v-for="topic in mobileTree" :key="`mobile-topic-${topic.id}`" class="mobile-tree__topic">
            <button class="mobile-tree__topic-trigger" @click="toggleMobileTopic(topic.id)">
              <span class="mobile-tree__topic-name">{{ topic.name }}</span>
              <span class="mobile-tree__topic-arrow" :class="{ 'is-open': isMobileTopicOpen(topic.id) }">▾</span>
            </button>

            <div class="mobile-tree__topic-body" :class="{ 'is-open': isMobileTopicOpen(topic.id) }">
              <div v-if="topic.loading" class="mobile-tree__status">姝ｅ湪鍔犺浇鏂囩珷...</div>
              <div v-else-if="topic.loadError" class="mobile-tree__status is-error">{{ topic.loadError }}</div>
              <template v-else-if="hasSecondLevelGroups(topic)">
                <section
                  v-for="group in getTopicGroups(topic)"
                  :key="`mobile-group-${topic.id}-${group.key}`"
                  class="mobile-tree__group"
                >
                  <button class="mobile-tree__group-trigger" @click="toggleMobileGroup(topic.id, group.key)">
                    <span class="mobile-tree__group-name">{{ group.label }}</span>
                    <span
                      class="mobile-tree__group-arrow"
                      :class="{ 'is-open': isMobileGroupOpen(topic.id, group.key) }"
                      >▾</span
                    >
                  </button>
                  <div
                    class="mobile-tree__group-body"
                    :class="{ 'is-open': isMobileGroupOpen(topic.id, group.key) }"
                  >
                    <div class="mobile-tree__articles">
                      <router-link
                        v-for="article in group.articles"
                        :key="article.id"
                        :to="`/article/${article.id}`"
                        class="mobile-tree__article"
                        :class="{ 'is-active': Number(activeDrawerArticleId) === Number(article.id) }"
                        @click="drawerOpen = false"
                      >
                        <span class="mobile-tree__article-index">{{ article.chapterOrder ?? 0 }}</span>
                        <span class="mobile-tree__article-title">{{ article.title }}</span>
                      </router-link>
                    </div>
                  </div>
                </section>
                <div v-if="!getTopicGroups(topic).length" class="mobile-tree__status">该专题暂无文章</div>
              </template>
              <div v-else-if="topic.rootArticles?.length" class="mobile-tree__articles mobile-tree__articles--root">
                <router-link
                  v-for="article in topic.rootArticles"
                  :key="article.id"
                  :to="`/article/${article.id}`"
                  class="mobile-tree__article"
                  :class="{ 'is-active': Number(activeDrawerArticleId) === Number(article.id) }"
                  @click="drawerOpen = false"
                >
                  <span class="mobile-tree__article-index">{{ article.chapterOrder ?? 0 }}</span>
                  <span class="mobile-tree__article-title">{{ article.title }}</span>
                </router-link>
              </div>
              <div v-else class="mobile-tree__status">该专题暂无文章</div>
            </div>
          </section>
        </div>
        <div v-else class="mobile-directory__tip">鏆傛棤鐩綍鏁版嵁</div>
      </div>
    </el-drawer>
  </teleport>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { RefreshRight } from '@element-plus/icons-vue'
import { fetchArticles } from '@/api/article.js'
import { fetchArticlesByCategoryId, fetchCategories } from '@/api/categories'
import { triggerSilentGithubSync } from '@/api/sync'
import { resolveContentMode } from '@/content/siteContent'
import ThemeToggle from './ThemeSwitcher.vue'
import { useBlogConfigStore } from '@/stores/blogConfig'
import { useUiStore } from '@/stores/ui'

const blogConfig = useBlogConfigStore()
const uiStore = useUiStore()

const route = useRoute()
const router = useRouter()

const topics = ref([])
const drawerOpen = ref(false)
const searchOpen = ref(false)
const searchQuery = ref('')
const searchInputRef = ref(null)
const searchResults = ref([])
const searchLoading = ref(false)
const searchFetched = ref(false)
const highlightedIndex = ref(-1)
const scrollProgress = ref(0)
const mobileTree = ref([])
const drawerTreeLoading = ref(false)
const drawerTreeError = ref('')
const openMobileTopicIds = ref(new Set())
const openMobileGroupKeys = ref(new Set())
const loadedMobileTopicIds = ref(new Set())
const syncLoading = ref(false)
const contentMode = ref('unknown')

let categoryPollTimer = null
let debounceTimer = null
const TOPIC_FETCH_PAGE_SIZE = 200
const isHomeActive = computed(() => route.path === '/home' || route.path === '/')
const isArticleRoute = computed(() => route.path.startsWith('/article/'))
const activeDrawerArticleId = computed(() =>
  route.path.startsWith('/article/') ? Number(route.params.id || 0) : 0,
)
const canManualSync = computed(() => contentMode.value === 'api')

const isTopicItemActive = (topic) => {
  if (!route.path.startsWith('/topic/')) return false
  return Number(route.params.id) === Number(topic?.id)
}

const toggleSearch = () => {
  searchOpen.value = !searchOpen.value
  if (searchOpen.value) {
    nextTick(() => {
      searchInputRef.value?.focus?.()
    })
  } else {
    resetSearch()
  }
}

const resetSearch = () => {
  searchQuery.value = ''
  searchResults.value = []
  searchLoading.value = false
  searchFetched.value = false
  highlightedIndex.value = -1
  clearTimeout(debounceTimer)
}

const onSearchInput = () => {
  clearTimeout(debounceTimer)
  highlightedIndex.value = -1
  const keyword = searchQuery.value.trim()
  if (!keyword) {
    searchResults.value = []
    searchFetched.value = false
    return
  }
  searchLoading.value = true
  debounceTimer = setTimeout(async () => {
    try {
      const res = await fetchArticles({ page: 1, size: 8, keyword })
      searchResults.value = res?.data || []
    } catch {
      searchResults.value = []
    } finally {
      searchLoading.value = false
      searchFetched.value = true
    }
  }, 300)
}

const onSearchEnter = () => {
  if (highlightedIndex.value >= 0 && searchResults.value[highlightedIndex.value]) {
    handleSelectArticle(searchResults.value[highlightedIndex.value])
  } else if (searchQuery.value.trim()) {
    const keyword = searchQuery.value.trim()
    searchOpen.value = false
    drawerOpen.value = false
    resetSearch()
    router.push({ path: '/home', query: { keyword } })
  }
}

const highlightNext = () => {
  if (!searchResults.value.length) return
  highlightedIndex.value = (highlightedIndex.value + 1) % searchResults.value.length
}

const highlightPrev = () => {
  if (!searchResults.value.length) return
  highlightedIndex.value =
    highlightedIndex.value <= 0 ? searchResults.value.length - 1 : highlightedIndex.value - 1
}

const handleSelectArticle = (item) => {
  if (!item?.id) return
  router.push(`/article/${item.id}`)
  searchOpen.value = false
  drawerOpen.value = false
  resetSearch()
}

const toggleTopicTree = () => {
  uiStore.toggleTopicTree()
}

const emitTopicsRefresh = () => {
  window.dispatchEvent(new Event('blog:topics-refresh'))
}

const formatSyncResultMessage = (result) => {
  if (!result || !Object.keys(result).length) {
    return '同步已完成，本次没有新的变更'
  }
  const matched = Number(result.matched || 0)
  const created = Number(result.created || 0)
  const deleted = Number(result.deleted || 0)
  const deletedCategories = Number(result.deletedCategories || 0)
  const failed = Number(result.failed || 0)
  return `同步完成：匹配 ${matched} 篇，新建 ${created} 篇，删除 ${deleted} 篇，清理分类 ${deletedCategories} 个，失败 ${failed} 篇`
}

const handleManualSync = async () => {
  if (syncLoading.value) return
  syncLoading.value = true
  const loadingMessage = ElMessage({
    message: '姝ｅ湪鍚屾鏂囩珷',
    type: 'info',
    duration: 0,
    showClose: true,
  })

  try {
    const result = await triggerSilentGithubSync()
    loadingMessage.close()
    emitTopicsRefresh()
    ElMessage.success(formatSyncResultMessage(result))
  } catch (error) {
    loadingMessage.close()
    ElMessage.error(error?.message || '鏂囩珷鍚屾澶辫触锛岃绋嶅悗閲嶈瘯')
  } finally {
    syncLoading.value = false
  }
}

const mobileGroupKey = (topicId, groupKey) => `${Number(topicId)}:${String(groupKey || '')}`
const isMobileTopicOpen = (topicId) => openMobileTopicIds.value.has(Number(topicId || 0))
const isMobileGroupOpen = (topicId, groupKey) =>
  openMobileGroupKeys.value.has(mobileGroupKey(topicId, groupKey))

const safeDecode = (value) => {
  if (!value) return ''
  try {
    return decodeURIComponent(value)
  } catch {
    return value
  }
}

const normalizePart = (value) => String(safeDecode(value || '')).trim()
const isMarkdownFile = (name) => /\.mdx?$/i.test(String(name || '').trim())
const leadingNumber = (value) => {
  const m = String(value || '')
    .trim()
    .match(/^(\d+)/)
  return m ? Number(m[1]) : null
}
const compareLabel = (a, b) => {
  const aText = String(a || '').trim()
  const bText = String(b || '').trim()
  const aNum = leadingNumber(aText)
  const bNum = leadingNumber(bText)
  if (aNum !== null && bNum !== null && aNum !== bNum) return aNum - bNum
  if (aNum !== null && bNum === null) return -1
  if (aNum === null && bNum !== null) return 1
  return aText.localeCompare(bText, 'zh-CN', { numeric: true, sensitivity: 'base' })
}
const compareArticles = (a, b) => {
  const aOrder = Number.isFinite(Number(a?.chapterOrder)) ? Number(a.chapterOrder) : null
  const bOrder = Number.isFinite(Number(b?.chapterOrder)) ? Number(b.chapterOrder) : null
  if (aOrder !== null && bOrder !== null && aOrder !== bOrder) return aOrder - bOrder
  if (aOrder !== null && bOrder === null) return -1
  if (aOrder === null && bOrder !== null) return 1
  return compareLabel(a?.title, b?.title)
}

const extractGithubPath = (url) => {
  if (!url) return ''
  const raw = String(url).trim()
  if (!raw) return ''

  if (!/^https?:\/\//i.test(raw)) {
    return raw
      .replace(/\\/g, '/')
      .replace(/^\.?\//, '')
      .split('/')
      .map((part) => safeDecode(part))
      .filter(Boolean)
      .join('/')
  }

  try {
    const u = new URL(raw)
    const parts = u.pathname.split('/').filter(Boolean).map((part) => safeDecode(part))
    if (u.hostname === 'raw.githubusercontent.com') {
      return parts.length > 3 ? parts.slice(3).join('/') : ''
    }
    if (u.hostname === 'github.com') {
      const blobIndex = parts.findIndex((p) => p === 'blob')
      if (blobIndex >= 0 && parts.length > blobIndex + 2) {
        return parts.slice(blobIndex + 2).join('/')
      }
    }
    return parts.join('/')
  } catch {
    return ''
  }
}

const groupLabelFromArticle = (article, topicName) => {
  const path = extractGithubPath(article.githubUrl || '')
  if (!path) return ''
  const parts = path
    .split('/')
    .map((part) => normalizePart(part))
    .filter(Boolean)
  const topicNames = Array.from(
    new Set(
      [article.categoryName, topicName]
        .map((name) => normalizePart(name))
        .filter(Boolean),
    ),
  )
  const topicIndex = topicNames.length
    ? parts.findIndex((part) => topicNames.some((name) => name === part))
    : -1

  if (topicIndex >= 0) {
    if (parts.length >= topicIndex + 3) {
      const folder = parts[topicIndex + 1]
      const fileLike = parts[topicIndex + 2]
      if (folder && fileLike) return folder
    }
    return ''
  }

  if (parts.length >= 3) {
    const maybeFolder = parts[1]
    const maybeFile = parts[2]
    if (maybeFolder && maybeFile && isMarkdownFile(maybeFile)) return maybeFolder
  }
  return ''
}

const buildGroups = (articles, topicName) => {
  const map = new Map()
  const rootArticles = []
  for (const article of articles) {
    const label = groupLabelFromArticle(article, topicName)
    if (!label) {
      rootArticles.push(article)
      continue
    }
    const key = label
    if (!map.has(key)) {
      map.set(key, { key, label, articles: [] })
    }
    map.get(key).articles.push(article)
  }
  const folderGroups = Array.from(map.values()).sort((a, b) =>
    compareLabel(a.label, b.label),
  )
  return { rootArticles, folderGroups }
}

const getTopicGroups = (topic) => {
  const groups = topic?.folderGroups ? [...topic.folderGroups] : []
  const rootArticles = topic?.rootArticles || []
  if (rootArticles.length) {
    groups.unshift({ key: '__root__', label: '未分组', articles: rootArticles })
  }
  return groups
}

const hasSecondLevelGroups = (topic) => (topic?.folderGroups || []).length > 0

const loadTopicArticlesIntoTree = async (topicId) => {
  const id = Number(topicId || 0)
  if (!id || loadedMobileTopicIds.value.has(id)) return
  const idx = mobileTree.value.findIndex((topic) => Number(topic.id) === id)
  if (idx < 0) return

  const markLoading = [...mobileTree.value]
  markLoading[idx] = { ...markLoading[idx], loading: true, loadError: '' }
  mobileTree.value = markLoading

  try {
    const res = await fetchArticlesByCategoryId(id, { page: 1, size: 1000 })
    const list = (res?.data || []).sort(compareArticles)
    const topicName = mobileTree.value[idx]?.name || ''
    const { rootArticles, folderGroups } = buildGroups(list, topicName)
    const next = [...mobileTree.value]
    next[idx] = {
      ...next[idx],
      loading: false,
      loadError: '',
      rootArticles,
      folderGroups,
    }
    mobileTree.value = next
    loadedMobileTopicIds.value = new Set(loadedMobileTopicIds.value).add(id)
  } catch {
    const next = [...mobileTree.value]
    next[idx] = {
      ...next[idx],
      loading: false,
      loadError: '鏂囩珷鍔犺浇澶辫触锛岃閲嶈瘯',
      rootArticles: [],
      folderGroups: [],
    }
    mobileTree.value = next
  }
}

const toggleMobileTopic = async (topicId) => {
  const id = Number(topicId || 0)
  if (!id) return
  const next = new Set(openMobileTopicIds.value)
  if (next.has(id)) {
    next.delete(id)
    openMobileTopicIds.value = next
    return
  }
  next.add(id)
  openMobileTopicIds.value = next
  await loadTopicArticlesIntoTree(id)
}

const toggleMobileGroup = (topicId, groupKey) => {
  const next = new Set(openMobileGroupKeys.value)
  const key = mobileGroupKey(topicId, groupKey)
  if (next.has(key)) next.delete(key)
  else next.add(key)
  openMobileGroupKeys.value = next
}

const highlightText = (text) => {
  if (!text || !searchQuery.value.trim()) return text
  const keyword = searchQuery.value.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  return text.replace(new RegExp(`(${keyword})`, 'gi'), '<mark>$1</mark>')
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

const fetchAllTopicArticles = async () => {
  const articles = []
  let page = 1

  while (true) {
    const res = await fetchArticles({ page, size: TOPIC_FETCH_PAGE_SIZE })
    const list = Array.isArray(res?.data) ? res.data : []
    const totalPage = Number(res?.pagination?.totalPage || 0)

    articles.push(...list)

    if (!totalPage || page >= totalPage) {
      break
    }
    page += 1
  }

  return articles
}

const buildTopicsFromArticles = (articles, categories) => {
  const categoryMetaMap = new Map(
    (Array.isArray(categories) ? categories : [])
      .filter((item) => item?.id)
      .map((item) => [
        Number(item.id),
        {
          id: Number(item.id),
          name: String(item.name || '').trim(),
          sortOrder: Number(item.sortOrder || 0),
        },
      ]),
  )

  const topicMap = new Map()
  for (const article of Array.isArray(articles) ? articles : []) {
    const id = Number(article?.categoryId || 0)
    const fallbackName = categoryMetaMap.get(id)?.name || ''
    const name = String(article?.categoryName || fallbackName).trim()
    if (!id || !name || name === '棣栭〉') continue
    if (topicMap.has(id)) continue

    const meta = categoryMetaMap.get(id)
    topicMap.set(id, {
      id,
      name: meta?.name || name,
      sortOrder: meta?.sortOrder || 0,
    })
  }

  return Array.from(topicMap.values()).sort((a, b) => {
    const orderDiff = (a.sortOrder || 0) - (b.sortOrder || 0)
    if (orderDiff !== 0) return orderDiff
    return String(a.name || '').localeCompare(String(b.name || ''), 'zh-CN')
  })
}

const onScroll = () => {
  const y = window.scrollY
  const docH = document.documentElement.scrollHeight - window.innerHeight
  scrollProgress.value = docH > 0 ? Math.min((y / docH) * 100, 100) : 0
}

const onKeydown = (e) => {
  if (e.key === 'Escape') toggleSearch()
  if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    e.preventDefault()
    toggleSearch()
  }
}

const loadTopics = async () => {
  drawerTreeLoading.value = true
  drawerTreeError.value = ''
  try {
    const [articles, categories] = await Promise.all([
      fetchAllTopicArticles(),
      fetchCategories().catch(() => []),
    ])
    const filtered = buildTopicsFromArticles(articles, categories)
    topics.value = filtered.map((item) => ({ id: item.id, name: item.name }))
    mobileTree.value = filtered.map((item) => ({
      id: item.id,
      name: item.name,
      loading: false,
      loadError: '',
      rootArticles: [],
      folderGroups: [],
    }))
  } catch {
    topics.value = []
    mobileTree.value = []
    drawerTreeError.value = '目录加载失败，请稍后重试。'
  } finally {
    drawerTreeLoading.value = false
  }
}

onMounted(() => {
  resolveContentMode()
    .then((mode) => {
      contentMode.value = mode
    })
    .catch(() => {
      contentMode.value = 'api'
    })
  loadTopics()
  window.addEventListener('blog:topics-refresh', loadTopics)
  window.addEventListener('scroll', onScroll, { passive: true })
  window.addEventListener('keydown', onKeydown)
  // 姣?鍒嗛挓闈欓粯鍒锋柊鍒嗙被锛岀‘淇濆悗绔悓姝ュ悗鍓嶇鑷姩鏇存柊
  categoryPollTimer = setInterval(loadTopics, 5 * 60 * 1000)
})

onUnmounted(() => {
  window.removeEventListener('blog:topics-refresh', loadTopics)
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('keydown', onKeydown)
  clearInterval(categoryPollTimer)
})

watch(
  () => route.path,
  () => {
    drawerOpen.value = false
  },
)

watch(
  () => drawerOpen.value,
  async (open) => {
    if (!open || !mobileTree.value.length || openMobileTopicIds.value.size) return
    const first = mobileTree.value[0]
    if (first?.id) {
      await toggleMobileTopic(first.id)
    }
  },
)
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  z-index: 1000;
  background: var(--bg-header);
  backdrop-filter: blur(var(--navbar-blur)) saturate(180%);
  -webkit-backdrop-filter: blur(var(--navbar-blur)) saturate(180%);
  border-bottom: 1px solid var(--border-light);
  transition:
    transform 0.35s cubic-bezier(0.4, 0, 0.2, 1),
    background 0.3s,
    box-shadow 0.3s;
  animation: slideDown 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.navbar::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(var(--accent-rgb), 0.15) 50%, transparent);
  opacity: 0;
  transition: opacity 0.3s;
}

.navbar:hover::after {
  opacity: 1;
}

.navbar__inner {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 8px;
}

.navbar__logo {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  text-decoration: none;
  margin-right: 28px;
  flex-shrink: 0;
  transition: opacity var(--transition-fast);
  letter-spacing: -0.03em;
}
.navbar__logo:hover {
  opacity: 0.85;
}
.navbar__logo-dot {
  color: var(--accent);
}

.navbar__nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.navbar__nav-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition:
    color 0.2s,
    background 0.2s;
  cursor: pointer;
  white-space: nowrap;
}
.navbar__nav-item:hover {
  color: var(--text-primary);
  background: var(--bg-secondary);
}
.navbar__nav-item.is-active {
  color: var(--accent);
  font-weight: 600;
}

.navbar__actions {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}
.navbar__icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: none;
  border: none;
  color: var(--text-secondary);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition:
    color 0.2s,
    background 0.2s;
  text-decoration: none;
}
.navbar__icon-btn:hover {
  color: var(--text-primary);
  background: var(--bg-secondary);
}
.navbar__icon-btn.is-active {
  color: var(--accent);
  background: var(--accent-light);
}
.navbar__icon-btn.is-disabled {
  opacity: 0.72;
  cursor: not-allowed;
}
.navbar__icon-btn :deep(svg) {
  width: 18px;
  height: 18px;
}
.navbar__icon-btn :deep(.is-spinning) {
  animation: spin 0.9s linear infinite;
}

.navbar__hamburger {
  display: none;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 36px;
  height: 36px;
  background: none;
  border: none;
  cursor: pointer;
  gap: 5px;
  padding: 0;
}
.navbar__hamburger span {
  display: block;
  width: 20px;
  height: 2px;
  background: var(--text-primary);
  border-radius: 2px;
  transition:
    transform 0.3s,
    opacity 0.3s;
}

.navbar__progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--accent), rgba(var(--accent-rgb), 0.6));
  transition: width 0.1s linear;
  border-radius: 0 1px 1px 0;
  z-index: 1;
}

/* Search overlay (teleported to body 鈫?:global) */
:global(.search-overlay) {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: var(--bg-overlay);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 100px;
}
:global(.search-panel) {
  width: 90%;
  max-width: 600px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  animation: scaleIn 0.2s ease-out;
}
:global(.search-panel__header) {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-light);
}
:global(.search-panel__icon) {
  flex-shrink: 0;
  color: var(--text-muted);
}
:global(.search-panel__input) {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 16px;
  color: var(--text-primary);
  font-family: inherit;
}
:global(.search-panel__input)::placeholder {
  color: var(--text-muted);
}
:global(.search-panel__hints) {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}
:global(.search-panel__hints) kbd {
  display: inline-block;
  padding: 2px 6px;
  font-size: 11px;
  font-family: inherit;
  color: var(--text-muted);
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  line-height: 1.4;
}

:global(.search-panel__loading) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 32px 20px;
  color: var(--text-muted);
  font-size: 14px;
}
:global(.search-spinner) {
  width: 16px;
  height: 16px;
  border: 2px solid var(--border-color);
  border-top-color: var(--accent);
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

:global(.search-panel__results) {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px;
}
:global(.search-result-item) {
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background 0.15s;
}
:global(.search-result-item.is-highlighted),
:global(.search-result-item):hover {
  background: var(--bg-secondary);
}
:global(.search-result-item__title) {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  margin-bottom: 4px;
}
:global(.search-result-item__title) mark {
  background: none;
  color: var(--accent);
  font-weight: 700;
}
:global(.search-result-item__meta) {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 6px;
}
:global(.search-result-item__cat) {
  padding: 1px 6px;
  background: var(--accent-light);
  color: var(--accent);
  border-radius: 3px;
  font-weight: 500;
}
:global(.search-result-item__summary) {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

:global(.search-panel__empty) {
  padding: 32px 20px;
  text-align: center;
  color: var(--text-muted);
  font-size: 14px;
}
:global(.search-panel__hint) {
  padding: 24px 20px;
  text-align: center;
  color: var(--text-muted);
  font-size: 13px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Drawer (teleported 鈫?:global) */
:global(.drawer-header) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}
:global(.drawer-header__logo) {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

:global(.mobile-directory) {
  padding: 8px 10px 20px;
}

:global(.mobile-directory__tip) {
  display: block;
  padding: 12px 8px;
  font-size: 13px;
  color: var(--text-muted);
  text-align: left;
}

:global(.mobile-directory__tip--error) {
  color: #ef4444;
}

:global(.mobile-tree) {
  display: grid;
  gap: 2px;
}

:global(.mobile-tree__topic) {
  border-bottom: 1px solid var(--border-light);
  background: transparent;
  overflow: visible;
}

:global(.mobile-tree__topic-trigger),
:global(.mobile-tree__group-trigger) {
  width: 100%;
  border: none;
  background: transparent;
  display: grid;
  align-items: center;
  text-align: left;
  color: var(--text-secondary);
  cursor: pointer;
  transition:
    color 0.2s,
    background 0.2s;
}

:global(.mobile-tree__topic-trigger) {
  grid-template-columns: 1fr auto;
  gap: 8px;
  padding: 12px 8px;
  font-size: 14px;
  font-weight: 700;
}

:global(.mobile-tree__topic-trigger:hover),
:global(.mobile-tree__group-trigger:hover) {
  color: var(--text-primary);
  background: var(--bg-secondary);
}

:global(.mobile-tree__topic-arrow),
:global(.mobile-tree__group-arrow) {
  justify-self: end;
  font-size: 11px;
  transition: transform 0.24s ease;
}

:global(.mobile-tree__topic-arrow.is-open),
:global(.mobile-tree__group-arrow.is-open) {
  transform: rotate(180deg);
}

:global(.mobile-tree__topic-body),
:global(.mobile-tree__group-body) {
  max-height: 0;
  overflow: hidden;
  opacity: 0;
  transform: translateY(-6px);
  transition:
    max-height 0.32s ease,
    opacity 0.24s ease,
    transform 0.24s ease;
}

:global(.mobile-tree__topic-body.is-open),
:global(.mobile-tree__group-body.is-open) {
  max-height: 9999px;
  opacity: 1;
  transform: translateY(0);
}

:global(.mobile-tree__group) {
  border-top: 1px solid var(--border-light);
}

:global(.mobile-tree__group-trigger) {
  grid-template-columns: 1fr auto;
  gap: 8px;
  padding: 10px 16px;
  font-size: 13px;
}

:global(.mobile-tree__group-name) {
  font-weight: 600;
}

:global(.mobile-tree__articles) {
  padding: 4px 8px 8px 8px;
  display: grid;
  gap: 2px;
}

:global(.mobile-tree__article) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 10px 7px 12px;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 12px;
  transition:
    color 0.18s,
    background 0.18s;
}

:global(.mobile-tree__article:hover),
:global(.mobile-tree__article.is-active) {
  color: var(--accent);
  background: var(--accent-light);
}

:global(.mobile-tree__article-index) {
  min-width: 18px;
  font-size: 11px;
  color: var(--text-muted);
}

:global(.mobile-tree__article-title) {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:global(.mobile-tree__status) {
  padding: 10px 16px 12px;
  font-size: 12px;
  color: var(--text-muted);
}

:global(.mobile-tree__status.is-error) {
  color: #ef4444;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.96) translateY(-8px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .topic-tree-toggle-btn {
    display: none !important;
  }
  .navbar__nav {
    display: none !important;
  }
  .navbar__hamburger {
    display: flex;
  }
  .navbar__logo {
    margin-right: 0;
  }
  :global(.search-overlay) {
    padding-top: 70px;
  }
  :global(.search-panel__header) {
    padding: 12px 16px;
  }
}

@media (max-width: 1024px) {
  .topic-tree-toggle-btn {
    display: none !important;
  }
}
</style>
