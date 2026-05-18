import { defineStore } from 'pinia'
import { fetchBlogConfig } from '@/api/blogConfig'

const CACHE_KEY = 'blog_config'

const DEFAULTS = {
  blog_avatar: '/avatar.png',
  blog_name: 'kunxing-blog',
  blog_bio: '热爱编程，热爱生活',
  blog_github_url: 'https://github.com/Eleven-Mouse',
  blog_footer_desc: 'ERROR 404 — 社交生活未找到。正在用代码填补中...',
  blog_copyright_name: 'kunxing-blog',
  site_home_intro_title: '面向工程实践的知识库',
  site_home_intro_desc: '按专题系统化整理技术知识，帮助你从入门到实战高效学习。',
  home_featured_article_id: '',
  site_nav_items: JSON.stringify([
    { label: '知识首页', path: '/home', type: 'internal' },
    { label: '专题', path: '/home#topics', type: 'internal' },
    { label: '归档', path: '/archive', type: 'internal' },
    { label: '关于', path: '/about', type: 'internal' },
  ]),
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
    parseNavItems(raw) {
      if (!raw) return JSON.parse(DEFAULTS.site_nav_items)
      try {
        const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw
        if (!Array.isArray(parsed)) return JSON.parse(DEFAULTS.site_nav_items)
        return parsed
          .filter((item) => item && item.label && item.path)
          .map((item) => ({
            label: item.label,
            path: item.path,
            type: item.type || 'internal',
          }))
      } catch {
        return JSON.parse(DEFAULTS.site_nav_items)
      }
    },
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
