<template>
  <div
    :id="contentId"
    ref="rootRef"
    class="article-markdown"
    v-html="rendered.html"
    @click="handleContentClick"
  ></div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import { Marked } from 'marked'
import hljs from 'highlight.js/lib/common'

const props = defineProps({
  content: {
    type: String,
    default: '',
  },
  contentId: {
    type: String,
    default: '',
  },
  headingPrefix: {
    type: String,
    default: 'heading',
  },
})

const emit = defineEmits(['catalog-change', 'image-click'])

const rootRef = ref(null)

const sanitizeMarkdownSource = (source) =>
  String(source || '').replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')

const escapeHtml = (value) =>
  String(value || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

const escapeAttr = (value) => escapeHtml(value)

const stripHtml = (value) =>
  String(value || '')
    .replace(/<[^>]+>/g, '')
    .replace(/&nbsp;/g, ' ')
    .trim()

const isSafeUrl = (value) => {
  const normalized = String(value || '').trim().toLowerCase()
  if (!normalized) return false
  return !normalized.startsWith('javascript:') && !normalized.startsWith('vbscript:')
}

const renderMarkdown = (source, headingPrefix) => {
  const catalog = []
  const renderer = {
    heading({ tokens, depth }) {
      const innerHtml = this.parser.parseInline(tokens)
      const text = stripHtml(innerHtml)
      const uniqueId = `${headingPrefix}-${catalog.length}`
      if (text) {
        catalog.push({
          text,
          level: depth,
          uniqueId,
        })
      }
      return `<h${depth} id="${uniqueId}">${innerHtml}</h${depth}>`
    },
    code({ text, lang }) {
      const rawLang = String(lang || '').trim().toLowerCase()
      const language = rawLang && hljs.getLanguage(rawLang) ? rawLang : 'plaintext'
      const codeHtml =
        language === 'plaintext'
          ? escapeHtml(text)
          : hljs.highlight(text, { language }).value
      const languageLabel = rawLang || 'text'
      return `
        <pre data-language="${escapeAttr(languageLabel)}">
          <button type="button" class="article-markdown__copy-btn">复制代码</button>
          <code class="hljs language-${escapeAttr(language)}">${codeHtml}</code>
        </pre>
      `
    },
    image({ href, title, text }) {
      const src = isSafeUrl(href) ? href : ''
      if (!src) return ''
      const alt = String(text || '').trim()
      const caption = title || alt
      return `
        <figure class="article-markdown__figure">
          <img
            src="${escapeAttr(src)}"
            alt="${escapeAttr(alt)}"
            ${title ? `title="${escapeAttr(title)}"` : ''}
            loading="lazy"
            decoding="async"
            data-preview-src="${escapeAttr(src)}"
          />
          ${caption ? `<figcaption>${escapeHtml(caption)}</figcaption>` : ''}
        </figure>
      `
    },
    link({ href, title, tokens }) {
      const safeHref = isSafeUrl(href) ? href : '#'
      const contentHtml = this.parser.parseInline(tokens)
      const isExternal = /^https?:\/\//i.test(String(safeHref))
      return `<a href="${escapeAttr(safeHref)}"${title ? ` title="${escapeAttr(title)}"` : ''}${isExternal ? ' target="_blank" rel="noreferrer"' : ''}>${contentHtml}</a>`
    },
  }

  const parser = new Marked({
    gfm: true,
    breaks: true,
    renderer,
  })

  return {
    html: String(parser.parse(sanitizeMarkdownSource(source)) || ''),
    catalog,
  }
}

const rendered = computed(() => renderMarkdown(props.content, props.headingPrefix))

watch(
  rendered,
  async (value) => {
    await nextTick()
    emit('catalog-change', value.catalog)
  },
  { immediate: true },
)

const copyCode = async (button) => {
  const codeEl = button.closest('pre')?.querySelector('code')
  const code = codeEl?.textContent || ''
  if (!code) return

  const originalText = button.textContent
  try {
    await navigator.clipboard.writeText(code)
    button.textContent = '已复制'
  } catch (error) {
    console.error(error)
    button.textContent = '复制失败'
  }

  window.setTimeout(() => {
    button.textContent = originalText || '复制代码'
  }, 1800)
}

const handleContentClick = async (event) => {
  const copyButton = event.target.closest('.article-markdown__copy-btn')
  if (copyButton instanceof HTMLButtonElement) {
    await copyCode(copyButton)
    return
  }

  const imageEl = event.target.closest('img[data-preview-src]')
  if (imageEl instanceof HTMLImageElement) {
    emit('image-click', imageEl.dataset.previewSrc || imageEl.currentSrc || imageEl.src)
  }
}
</script>

<style scoped>
.article-markdown {
  color: var(--text-primary);
}

.article-markdown :deep(*) {
  box-sizing: border-box;
}

.article-markdown :deep(p) {
  margin: 0 0 24px;
  font-size: 17px;
  line-height: 1.92;
  color: var(--text-primary);
}

.article-markdown :deep(h1),
.article-markdown :deep(h2),
.article-markdown :deep(h3),
.article-markdown :deep(h4),
.article-markdown :deep(h5),
.article-markdown :deep(h6) {
  margin: 52px 0 20px;
  color: var(--text-primary);
  line-height: 1.35;
  font-weight: 800;
  scroll-margin-top: 92px;
}

.article-markdown :deep(h1) {
  font-size: 2rem;
}

.article-markdown :deep(h2) {
  font-size: 1.55rem;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-light);
}

.article-markdown :deep(h3) {
  font-size: 1.25rem;
}

.article-markdown :deep(h4) {
  font-size: 1.08rem;
}

.article-markdown :deep(ul),
.article-markdown :deep(ol) {
  margin: 0 0 24px;
  padding-left: 24px;
}

.article-markdown :deep(li) {
  margin: 8px 0;
  line-height: 1.85;
  color: var(--text-primary);
}

.article-markdown :deep(blockquote) {
  margin: 30px 0;
  padding: 18px 22px;
  border-left: 4px solid var(--accent);
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
  background: var(--accent-light);
  color: var(--text-secondary);
}

.article-markdown :deep(blockquote p:last-child) {
  margin-bottom: 0;
}

.article-markdown :deep(hr) {
  margin: 40px 0;
  border: none;
  border-top: 1px solid var(--border-light);
}

.article-markdown :deep(code:not(pre code)) {
  padding: 2px 8px;
  border-radius: 6px;
  background: var(--bg-inline-code);
  color: var(--accent);
  font-size: 14px;
  font-family: 'SF Mono', 'Fira Code', Consolas, monospace;
}

.article-markdown :deep(pre) {
  position: relative;
  margin: 32px 0;
  padding: 44px 18px 18px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-code);
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  box-shadow: var(--shadow-xs);
}

.article-markdown :deep(pre::before) {
  content: attr(data-language);
  position: absolute;
  top: 12px;
  left: 18px;
  font-size: 11px;
  line-height: 1;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-muted);
}

.article-markdown :deep(pre code) {
  display: block;
  background: transparent;
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.72;
  font-family: 'SF Mono', 'Fira Code', Consolas, monospace;
}

.article-markdown :deep(.article-markdown__copy-btn) {
  position: absolute;
  top: 10px;
  right: 12px;
  border: 1px solid var(--border-color);
  border-radius: 999px;
  background: var(--bg-primary);
  color: var(--text-muted);
  padding: 4px 10px;
  font-size: 12px;
  cursor: pointer;
  transition:
    color var(--transition-fast),
    border-color var(--transition-fast),
    background var(--transition-fast);
}

.article-markdown :deep(.article-markdown__copy-btn:hover) {
  color: var(--accent);
  border-color: rgba(var(--accent-rgb), 0.35);
  background: var(--accent-light);
}

.article-markdown :deep(.article-markdown__figure) {
  margin: 30px 0;
}

.article-markdown :deep(.article-markdown__figure img) {
  display: block;
  width: auto;
  max-width: 100%;
  height: auto;
  margin: 0 auto;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  cursor: zoom-in;
}

.article-markdown :deep(.article-markdown__figure figcaption) {
  margin-top: 10px;
  text-align: center;
  font-size: 13px;
  color: var(--text-muted);
}

.article-markdown :deep(a) {
  color: var(--accent);
  text-decoration: underline;
  text-decoration-thickness: 1px;
  text-underline-offset: 3px;
}

.article-markdown :deep(table) {
  display: block;
  width: 100%;
  margin: 28px 0;
  border-collapse: collapse;
  overflow-x: auto;
}

.article-markdown :deep(th),
.article-markdown :deep(td) {
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  text-align: left;
  white-space: nowrap;
}

.article-markdown :deep(th) {
  background: var(--bg-secondary);
  font-weight: 700;
  color: var(--text-primary);
}

.article-markdown :deep(.hljs) {
  color: var(--text-primary);
  background: transparent;
}

.article-markdown :deep(.hljs-comment),
.article-markdown :deep(.hljs-quote) {
  color: var(--text-muted);
  font-style: italic;
}

.article-markdown :deep(.hljs-keyword),
.article-markdown :deep(.hljs-selector-tag),
.article-markdown :deep(.hljs-literal),
.article-markdown :deep(.hljs-section),
.article-markdown :deep(.hljs-link) {
  color: #c2410c;
}

.article-markdown :deep(.hljs-string),
.article-markdown :deep(.hljs-title),
.article-markdown :deep(.hljs-attribute),
.article-markdown :deep(.hljs-symbol),
.article-markdown :deep(.hljs-bullet),
.article-markdown :deep(.hljs-addition) {
  color: #0f766e;
}

.article-markdown :deep(.hljs-number),
.article-markdown :deep(.hljs-built_in),
.article-markdown :deep(.hljs-type),
.article-markdown :deep(.hljs-meta) {
  color: #7c3aed;
}

.article-markdown :deep(.hljs-variable),
.article-markdown :deep(.hljs-template-variable),
.article-markdown :deep(.hljs-operator) {
  color: #2563eb;
}

html[data-theme='dark'] .article-markdown :deep(.hljs-keyword),
html[data-theme='dark'] .article-markdown :deep(.hljs-selector-tag),
html[data-theme='dark'] .article-markdown :deep(.hljs-literal),
html[data-theme='dark'] .article-markdown :deep(.hljs-section),
html[data-theme='dark'] .article-markdown :deep(.hljs-link) {
  color: #fb923c;
}

html[data-theme='dark'] .article-markdown :deep(.hljs-string),
html[data-theme='dark'] .article-markdown :deep(.hljs-title),
html[data-theme='dark'] .article-markdown :deep(.hljs-attribute),
html[data-theme='dark'] .article-markdown :deep(.hljs-symbol),
html[data-theme='dark'] .article-markdown :deep(.hljs-bullet),
html[data-theme='dark'] .article-markdown :deep(.hljs-addition) {
  color: #5eead4;
}

html[data-theme='dark'] .article-markdown :deep(.hljs-number),
html[data-theme='dark'] .article-markdown :deep(.hljs-built_in),
html[data-theme='dark'] .article-markdown :deep(.hljs-type),
html[data-theme='dark'] .article-markdown :deep(.hljs-meta) {
  color: #c4b5fd;
}

html[data-theme='dark'] .article-markdown :deep(.hljs-variable),
html[data-theme='dark'] .article-markdown :deep(.hljs-template-variable),
html[data-theme='dark'] .article-markdown :deep(.hljs-operator) {
  color: #93c5fd;
}

@media (max-width: 768px) {
  .article-markdown :deep(p) {
    font-size: 16px;
    line-height: 1.82;
  }

  .article-markdown :deep(h1) {
    font-size: 1.68rem;
  }

  .article-markdown :deep(h2) {
    font-size: 1.35rem;
  }

  .article-markdown :deep(pre) {
    padding: 40px 14px 14px;
  }

  .article-markdown :deep(th),
  .article-markdown :deep(td) {
    padding: 10px 12px;
  }
}
</style>
