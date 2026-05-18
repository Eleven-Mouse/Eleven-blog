<template>
  <aside class="topic-drawer">
    <div v-if="loading" class="loading-tip">正在加载目录...</div>
    <div v-else-if="error" class="error-tip">{{ error }}</div>

    <div v-else-if="activeTopic" class="topic-drawer__list">
      <section class="drawer-topic">
        <div class="drawer-topic__content">
          <div v-if="activeTopic.rootArticles?.length" class="drawer-group__articles">
            <router-link
              v-for="article in activeTopic.rootArticles"
              :key="article.id"
              :to="`/article/${article.id}`"
              class="drawer-article"
              :class="{ 'is-active': Number(activeArticleId) === Number(article.id) }"
            >
              <span class="drawer-article__index">{{ article.chapterOrder ?? 0 }}</span>
              <span class="drawer-article__title">{{ article.title }}</span>
            </router-link>
          </div>

          <section v-for="group in activeTopic.folderGroups" :key="group.key" class="drawer-group">
            <button class="drawer-group__trigger" @click="toggleGroup(activeTopic.id, group.key)">
              <span class="drawer-group__name">{{ group.label }}</span>
              <span
                class="drawer-group__arrow"
                :class="{ 'is-open': isGroupOpen(activeTopic.id, group.key) }"
                >▾</span
              >
            </button>

            <div v-if="isGroupOpen(activeTopic.id, group.key)" class="drawer-group__articles">
              <router-link
                v-for="article in group.articles"
                :key="article.id"
                :to="`/article/${article.id}`"
                class="drawer-article"
                :class="{ 'is-active': Number(activeArticleId) === Number(article.id) }"
              >
                <span class="drawer-article__index">{{ article.chapterOrder ?? 0 }}</span>
                <span class="drawer-article__title">{{ article.title }}</span>
              </router-link>
            </div>
          </section>
        </div>
      </section>
    </div>
    <div v-else class="error-tip">未匹配到当前专题目录</div>
  </aside>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { fetchArticlesByCategoryId, fetchCategories } from '@/api/categories'

const props = defineProps({
  activeTopicId: {
    type: [String, Number],
    default: null,
  },
  activeArticleId: {
    type: [String, Number],
    default: null,
  },
})

const loading = ref(false)
const error = ref('')
const treeData = ref([])
const openGroupKeys = ref(new Set())
const loadedTopicIds = ref(new Set())

const topicGroupKey = (topicId, key) => `${topicId}:${key}`
const isGroupOpen = (topicId, key) => openGroupKeys.value.has(topicGroupKey(topicId, key))
const activeTopic = computed(() =>
  treeData.value.find((topic) => Number(topic.id) === Number(props.activeTopicId || 0)),
)

const toggleGroup = (topicId, key) => {
  const next = new Set(openGroupKeys.value)
  const groupKey = topicGroupKey(topicId, key)
  if (next.has(groupKey)) next.delete(groupKey)
  else next.add(groupKey)
  openGroupKeys.value = next
}

const sameKeySet = (a, b) => {
  if (a.size !== b.size) return false
  for (const k of a) {
    if (!b.has(k)) return false
  }
  return true
}

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

  // 以“专题目录”为基准判断是否存在二级目录：
  // topic/file.md => 直接文章
  // topic/folder/file.md => 显示 folder
  if (topicIndex >= 0) {
    if (parts.length >= topicIndex + 3) {
      const folder = parts[topicIndex + 1]
      const fileLike = parts[topicIndex + 2]
      if (folder && fileLike) return folder
    }
    return ''
  }

  // 回退逻辑：path=/topic/folder/file.md 时取 folder；否则当作根层文章
  if (parts.length >= 3) {
    const maybeFolder = parts[1]
    const maybeFile = parts[2]
    if (maybeFolder && maybeFile && isMarkdownFile(maybeFile)) return maybeFolder
  }
  return ''
}

const extractGithubPath = (url) => {
  if (!url) return ''
  const raw = String(url).trim()
  if (!raw) return ''

  // 兼容本地同步产生的相对路径/Windows 路径
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
      // /owner/repo/branch/path/to/file.md
      return parts.length > 3 ? parts.slice(3).join('/') : ''
    }
    if (u.hostname === 'github.com') {
      // /owner/repo/blob/branch/path/to/file.md
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
    String(a.label).localeCompare(String(b.label), 'zh-CN'),
  )
  return { rootArticles, folderGroups }
}

const ensureActiveOpened = () => {
  const topic = activeTopic.value
  if (!topic) return
  const nextGroups = new Set()
  const activeId = Number(props.activeArticleId || 0)
  if (!activeId) {
    if (!sameKeySet(openGroupKeys.value, nextGroups)) {
      openGroupKeys.value = nextGroups
    }
    return
  }
  const group = topic.folderGroups.find((g) => g.articles.some((a) => Number(a.id) === activeId))
  if (group) {
    nextGroups.add(topicGroupKey(topic.id, group.key))
  }
  if (!sameKeySet(openGroupKeys.value, nextGroups)) {
    openGroupKeys.value = nextGroups
  }
}

const loadTopicArticles = async (topicId) => {
  const id = Number(topicId || 0)
  if (!id || loadedTopicIds.value.has(id)) return
  const idx = treeData.value.findIndex((item) => Number(item.id) === id)
  if (idx < 0) return

  try {
    const res = await fetchArticlesByCategoryId(id, { page: 1, size: 1000 })
    const articles = (res?.data || []).sort((a, b) => (a.chapterOrder || 0) - (b.chapterOrder || 0))
    const topic = treeData.value[idx]
    const { rootArticles, folderGroups } = buildGroups(articles, topic.name)

    const next = [...treeData.value]
    next[idx] = {
      ...topic,
      total: articles.length,
      rootArticles,
      folderGroups,
    }
    treeData.value = next
    loadedTopicIds.value = new Set(loadedTopicIds.value).add(id)
  } catch {
    // 保持该分类为空目录，避免刷新时抛错阻断页面
    const next = [...treeData.value]
    next[idx] = {
      ...next[idx],
      total: 0,
      rootArticles: [],
      folderGroups: [],
    }
    treeData.value = next
  }
}

const loadTree = async () => {
  loading.value = true
  error.value = ''
  try {
    const topics = (await fetchCategories()) || []
    treeData.value = topics
      .sort((a, b) => {
      const orderDiff = (a.sortOrder || 0) - (b.sortOrder || 0)
      if (orderDiff !== 0) return orderDiff
      return String(a.name || '').localeCompare(String(b.name || ''), 'zh-CN')
    })
      .map((topic) => ({ ...topic, total: 0, rootArticles: [], folderGroups: [] }))
    await loadTopicArticles(props.activeTopicId)
    ensureActiveOpened()
  } catch (err) {
    error.value = '目录加载失败，请稍后重试。'
    console.error(err)
  } finally {
    loading.value = false
  }
}

watch(
  () => [props.activeTopicId, props.activeArticleId, treeData.value.length, activeTopic.value?.id],
  async () => {
    await loadTopicArticles(props.activeTopicId)
    ensureActiveOpened()
  },
)

onMounted(() => {
  loadTree()
})
</script>

<style scoped>
.topic-drawer {
  position: sticky;
  top: 88px;
  max-height: calc(100vh - 110px);
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding: 14px;
}

.topic-drawer::-webkit-scrollbar {
  width: 0;
  height: 0;
}

.topic-drawer__list {
  display: grid;
  gap: 8px;
}

.drawer-topic {
  border: none;
  border-radius: 0;
  overflow: visible;
  background: transparent;
}

.drawer-topic__trigger,
.drawer-group__trigger {
  width: 100%;
  border: none;
  background: transparent;
  color: var(--text-primary);
  text-align: left;
  cursor: pointer;
}

.drawer-topic__trigger {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 8px;
  padding: 10px 12px;
  font-size: 14px;
  font-weight: 700;
}

.drawer-topic__trigger.is-active {
  color: var(--accent);
  background: var(--accent-light);
}

.drawer-topic__meta,
.drawer-group__meta {
  font-size: 11px;
  color: var(--text-muted);
}

.drawer-topic__arrow,
.drawer-group__arrow {
  transition: transform 0.2s;
}

.drawer-topic__arrow.is-open,
.drawer-group__arrow.is-open {
  transform: rotate(180deg);
}

.drawer-topic__content {
  border-top: none;
  padding: 6px;
}

.drawer-group {
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.drawer-group__trigger {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  padding: 8px 10px;
  font-size: 13px;
  color: var(--text-secondary);
}

.drawer-group__name {
  font-weight: 600;
}

.drawer-group__trigger:hover {
  background: var(--bg-secondary);
}

.drawer-group__articles {
  display: grid;
  gap: 2px;
  padding: 2px 0 6px 0;
}

.drawer-article {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px 6px 14px;
  border-radius: var(--radius-sm);
  text-decoration: none;
  color: var(--text-secondary);
  font-size: 13px;
}

.drawer-article:hover {
  background: var(--bg-secondary);
  color: var(--accent);
}

.drawer-article.is-active {
  background: var(--accent-light);
  color: var(--accent);
}

.drawer-article__index {
  min-width: 20px;
  font-size: 11px;
  color: var(--text-muted);
}

.drawer-article__title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 600;
}

@media (max-width: 1024px) {
  .topic-drawer {
    position: static;
    max-height: none;
  }
}
</style>
