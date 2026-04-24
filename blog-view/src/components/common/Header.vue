<template>
  <header class="navbar" :class="{ 'navbar--hidden': isNavHidden }">
    <div class="navbar__inner page-container">
      <router-link to="/home" class="navbar__logo">
        <span class="navbar__logo-text">Eleven</span>
        <span class="navbar__logo-dot">.</span>
      </router-link>

      <nav class="navbar__nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="navbar__nav-item"
          :class="{ 'is-active': isActive(item) }"
        >
          {{ item.label }}
        </router-link>

        <div
          class="navbar__dropdown"
          @mouseenter="showCatDropdown = true"
          @mouseleave="showCatDropdown = false"
        >
          <span class="navbar__nav-item" :class="{ 'is-active': isCategoryActive }">
            Categories
            <svg class="navbar__arrow" viewBox="0 0 12 12" width="12" height="12">
              <path d="M2 4l4 4 4-4" fill="none" stroke="currentColor" stroke-width="1.5" />
            </svg>
          </span>
          <transition name="dropdown">
            <div v-if="showCatDropdown && categories.length" class="navbar__dropdown-menu">
              <router-link
                v-for="cat in categories"
                :key="cat.id"
                :to="`/category/${cat.id}`"
                class="navbar__dropdown-item"
                @click="showCatDropdown = false"
              >
                {{ cat.name }}
              </router-link>
            </div>
          </transition>
        </div>
      </nav>

      <div class="navbar__actions">
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
          href="https://github.com/Eleven-Mouse"
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
              placeholder="搜索文章..."
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
            <span>搜索中...</span>
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
            没有找到相关文章
          </div>

          <div v-else-if="!searchQuery" class="search-panel__hint">
            输入关键词搜索文章，按 Enter 查看全部结果
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
      <!-- Profile -->
      <div class="drawer-profile">
        <img src="/avatar.png" alt="Avatar" class="drawer-profile__avatar" />
        <div class="drawer-profile__name">Eleven-Mouse</div>
        <div class="drawer-profile__bio">有 bug？那不叫 bug，那叫 feature。</div>
        <div class="drawer-profile__social"></div>
      </div>

      <div class="drawer-divider" />

      <nav class="drawer-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="drawer-nav__item"
          @click="drawerOpen = false"
        >
          {{ item.label }}
        </router-link>
      </nav>

      <div class="drawer-divider" />

      <!-- Categories accordion -->
      <div class="drawer-accordion">
        <button class="drawer-accordion__trigger" @click="catExpanded = !catExpanded">
          <span>Categories</span>
          <svg
            class="drawer-accordion__arrow"
            :class="{ 'is-open': catExpanded }"
            viewBox="0 0 12 12"
            width="12"
            height="12"
          >
            <path d="M2 4l4 4 4-4" fill="none" stroke="currentColor" stroke-width="1.5" />
          </svg>
        </button>
        <div class="drawer-accordion__body" :class="{ 'is-open': catExpanded }">
          <router-link
            v-for="cat in categories"
            :key="cat.id"
            :to="`/category/${cat.id}`"
            class="drawer-nav__item drawer-nav__item--sub"
            @click="drawerOpen = false"
          >
            {{ cat.name }}
          </router-link>
        </div>
      </div>

      <!-- Tags accordion -->
      <div class="drawer-accordion">
        <button class="drawer-accordion__trigger" @click="tagExpanded = !tagExpanded">
          <span>Tags</span>
          <svg
            class="drawer-accordion__arrow"
            :class="{ 'is-open': tagExpanded }"
            viewBox="0 0 12 12"
            width="12"
            height="12"
          >
            <path d="M2 4l4 4 4-4" fill="none" stroke="currentColor" stroke-width="1.5" />
          </svg>
        </button>
        <div class="drawer-accordion__body" :class="{ 'is-open': tagExpanded }">
          <div class="drawer-tags__list">
            <router-link
              v-for="tag in tags"
              :key="tag.id || tag.name"
              :to="`/tag/${tag.id}`"
              class="tag"
              @click="drawerOpen = false"
            >
              {{ tag.name }}
            </router-link>
          </div>
        </div>
      </div>
    </el-drawer>
  </teleport>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchCategories } from '@/api/categories'
import { fetchTags } from '@/api/tags'
import { fetchArticles } from '@/api/article.js'
import ThemeToggle from './ThemeSwitcher.vue'

const route = useRoute()
const router = useRouter()

const navItems = [
  { path: '/home', label: 'Home' },
  { path: '/moment', label: 'Moment' },
  { path: '/archive', label: 'Archive' },
  { path: '/about', label: 'About' },
  { path: '/friendlinks', label: 'Links' },
]

const categories = ref([])
const tags = ref([])
const showCatDropdown = ref(false)
const drawerOpen = ref(false)
const searchOpen = ref(false)
const searchQuery = ref('')
const searchInputRef = ref(null)
const searchResults = ref([])
const searchLoading = ref(false)
const searchFetched = ref(false)
const highlightedIndex = ref(-1)
const scrollProgress = ref(0)
const isNavHidden = ref(false)
const catExpanded = ref(false)
const tagExpanded = ref(false)

let lastScrollY = 0
let debounceTimer = null

const isCategoryActive = computed(() => route.path.startsWith('/category'))

const isActive = (item) => {
  if (item.path === '/home') return route.path === '/home' || route.path === '/'
  return route.path === item.path
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

const onScroll = () => {
  const y = window.scrollY
  const docH = document.documentElement.scrollHeight - window.innerHeight
  scrollProgress.value = docH > 0 ? Math.min((y / docH) * 100, 100) : 0
  isNavHidden.value = y > 200 && y > lastScrollY
  lastScrollY = y
}

const onKeydown = (e) => {
  if (e.key === 'Escape') toggleSearch()
  if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    e.preventDefault()
    toggleSearch()
  }
}

const loadCategories = async () => {
  try {
    categories.value = await fetchCategories()
  } catch {}
}

const loadTags = async () => {
  try {
    tags.value = await fetchTags()
  } catch {}
}

onMounted(() => {
  loadCategories()
  loadTags()
  window.addEventListener('scroll', onScroll, { passive: true })
  window.addEventListener('keydown', onKeydown)
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('keydown', onKeydown)
})

watch(
  () => route.path,
  () => {
    showCatDropdown.value = false
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

.navbar--hidden {
  transform: translateY(-100%);
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
.navbar__arrow {
  transition: transform 0.2s;
}

.navbar__dropdown {
  position: relative;
}
.navbar__dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 160px;
  padding: 6px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
  z-index: 100;
}
.navbar__dropdown-item {
  display: block;
  padding: 8px 14px;
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: all 0.15s;
}
.navbar__dropdown-item:hover {
  color: var(--accent);
  background: var(--accent-light);
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition:
    opacity 0.15s,
    transform 0.15s;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-6px);
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

/* Search overlay (teleported to body → :global) */
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

/* Drawer (teleported → :global) */
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

:global(.drawer-profile) {
  text-align: center;
  padding: 8px 16px 16px;
}
:global(.drawer-profile__avatar) {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--accent);
  margin-bottom: 10px;
}
:global(.drawer-profile__name) {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}
:global(.drawer-profile__bio) {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 12px;
}
:global(.drawer-profile__social) {
  display: flex;
  justify-content: center;
  gap: 10px;
}
:global(.drawer-profile__social-link) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: var(--text-muted);
  background: var(--bg-secondary);
  text-decoration: none;
  transition: all 0.2s;
}
:global(.drawer-profile__social-link):hover {
  color: var(--text-primary);
  background: var(--bg-hover);
}

:global(.drawer-divider) {
  height: 1px;
  background: var(--border-light);
  margin: 0 16px 12px;
}
:global(.drawer-search) {
  margin-bottom: 8px;
  padding: 0 12px;
}
:global(.drawer-search) .el-input__wrapper {
  background: var(--bg-secondary);
}

:global(.drawer-nav) {
  display: flex;
  flex-direction: column;
}
:global(.drawer-nav__item) {
  display: flex;
  align-items: center;
  padding: 11px 16px;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.15s;
}
:global(.drawer-nav__item):hover,
:global(.drawer-nav__item.router-link-active) {
  color: var(--text-primary);
  background: var(--bg-secondary);
}
:global(.drawer-nav__item--sub) {
  font-size: 14px;
  font-weight: 400;
  padding: 9px 16px 9px 32px;
}

/* Accordion */
:global(.drawer-accordion) {
  margin-bottom: 2px;
}
:global(.drawer-accordion__trigger) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 11px 16px;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-secondary);
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.15s;
}
:global(.drawer-accordion__trigger):hover {
  color: var(--text-primary);
}
:global(.drawer-accordion__arrow) {
  transition: transform 0.25s;
}
:global(.drawer-accordion__arrow.is-open) {
  transform: rotate(180deg);
}

:global(.drawer-accordion__body) {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}
:global(.drawer-accordion__body.is-open) {
  max-height: 600px;
}

:global(.drawer-tags__list) {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 4px 16px 12px;
}
:global(.drawer-tags__list .tag) {
  font-size: 12px;
  background: none;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}
:global(.drawer-tags__list .tag):hover {
  background: none;
  color: var(--text-primary);
  border-color: var(--text-muted);
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
  .navbar__nav,
  .navbar__dropdown {
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
</style>
