import { defineStore } from 'pinia'
import { fetchBlogConfig } from '@/api/blogConfig'

const CACHE_KEY = 'blog_config'

const DEFAULTS = {
  blog_avatar: '/avatar.png',
  blog_name: 'Eleven-Mouse',
  blog_bio: '热爱编程，热爱生活',
  blog_github_url: 'https://github.com/Eleven-Mouse',
  blog_footer_desc: 'ERROR 404 — 社交生活未找到。正在用代码填补中...',
  blog_copyright_name: 'Eleven-Mouse',
}

function loadCached() {
  try {
    const raw = localStorage.getItem(CACHE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export const useBlogConfigStore = defineStore('blogConfig', {
  state: () => ({
    config: loadCached() || { ...DEFAULTS },
    loaded: false,
  }),
  actions: {
    async fetchConfig() {
      try {
        const data = await fetchBlogConfig()
        if (data && typeof data === 'object') {
          this.config = { ...DEFAULTS, ...data }
          localStorage.setItem(CACHE_KEY, JSON.stringify(this.config))
        }
        this.loaded = true
      } catch {
        this.loaded = true
      }
    },
  },
})
