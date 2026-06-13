const CONTENT_SOURCE = String(import.meta.env.VITE_CONTENT_SOURCE || 'auto')
  .trim()
  .toLowerCase()

const STATIC_SITE_URL = String(import.meta.env.VITE_STATIC_SITE_URL || '/content/site.json').trim()

let staticSitePromise = null
let resolvedModePromise = null

const toNumberOrNull = (value) => {
  const num = Number(value)
  return Number.isFinite(num) ? num : null
}

const toTimestamp = (value) => {
  const ts = new Date(value || 0).getTime()
  return Number.isFinite(ts) ? ts : 0
}

const compareArticlesDesc = (a, b) => {
  const publishDiff = toTimestamp(b.publishTime) - toTimestamp(a.publishTime)
  if (publishDiff !== 0) return publishDiff

  const updateDiff = toTimestamp(b.updateTime) - toTimestamp(a.updateTime)
  if (updateDiff !== 0) return updateDiff

  return Number(b.id || 0) - Number(a.id || 0)
}

const paginate = (items, params = {}) => {
  const page = Math.max(1, Number(params.page) || 1)
  const size = Math.max(1, Number(params.size) || 10)
  const total = items.length
  const start = (page - 1) * size

  return {
    data: items.slice(start, start + size),
    pagination: {
      currentPage: page,
      totalPage: Math.ceil(total / size),
      total,
      size,
    },
  }
}

const normalizeCategory = (category) => ({
  ...category,
  id: toNumberOrNull(category?.id) ?? category?.id,
  sortOrder: toNumberOrNull(category?.sortOrder) ?? 0,
  articleCount: toNumberOrNull(category?.articleCount) ?? 0,
})

const normalizeArticle = (article, categoryMap) => {
  const normalizedCategoryId = toNumberOrNull(article?.categoryId)
  const category = normalizedCategoryId !== null ? categoryMap.get(normalizedCategoryId) : null

  return {
    ...article,
    id: toNumberOrNull(article?.id) ?? article?.id,
    categoryId: normalizedCategoryId ?? article?.categoryId ?? null,
    categoryName: article?.categoryName || category?.name || '',
    chapterOrder: toNumberOrNull(article?.chapterOrder),
    readingMinutes: toNumberOrNull(article?.readingMinutes),
    isCore: toNumberOrNull(article?.isCore),
    viewCount: toNumberOrNull(article?.viewCount) ?? 0,
    isComment: toNumberOrNull(article?.isComment),
    tagIds: Array.isArray(article?.tagIds)
      ? article.tagIds.map((id) => toNumberOrNull(id) ?? id).filter((id) => id !== null && id !== '')
      : [],
    tags: Array.isArray(article?.tags)
      ? article.tags.map((tag) => String(tag || '').trim()).filter(Boolean)
      : [],
  }
}

const normalizeTag = (tag, articleMap) => {
  const tagId = toNumberOrNull(tag?.id) ?? tag?.id
  const directArticleIds = Array.isArray(tag?.articleIds)
    ? tag.articleIds
        .map((id) => toNumberOrNull(id) ?? id)
        .filter((id) => id !== null && id !== '' && articleMap.has(Number(id)))
    : []

  const relatedArticleIds = directArticleIds.length
    ? directArticleIds
    : Array.from(articleMap.values())
        .filter((article) => {
          const normalizedId = toNumberOrNull(tagId)
          if (normalizedId !== null && article.tagIds.includes(normalizedId)) return true
          return article.tags.includes(String(tag?.name || '').trim())
        })
        .map((article) => article.id)

  return {
    ...tag,
    id: tagId,
    name: String(tag?.name || '').trim(),
    articleIds: relatedArticleIds,
    articleCount: toNumberOrNull(tag?.articleCount) ?? relatedArticleIds.length,
  }
}

const deriveTagsFromArticles = (articles) => {
  const tagMap = new Map()

  articles.forEach((article) => {
    article.tags.forEach((name) => {
      if (!tagMap.has(name)) {
        tagMap.set(name, {
          id: name,
          name,
          articleIds: [],
          articleCount: 0,
        })
      }
      const tag = tagMap.get(name)
      tag.articleIds.push(article.id)
      tag.articleCount = tag.articleIds.length
    })
  })

  return Array.from(tagMap.values())
}

const normalizeSiteData = (raw) => {
  const categories = Array.isArray(raw?.categories) ? raw.categories.map(normalizeCategory) : []
  const categoryMap = new Map(categories.map((category) => [Number(category.id), category]))

  const articles = Array.isArray(raw?.articles)
    ? raw.articles.map((article) => normalizeArticle(article, categoryMap)).sort(compareArticlesDesc)
    : []
  const articleMap = new Map(articles.map((article) => [Number(article.id), article]))

  const tags =
    Array.isArray(raw?.tags) && raw.tags.length
      ? raw.tags.map((tag) => normalizeTag(tag, articleMap))
      : deriveTagsFromArticles(articles)

  categories.forEach((category) => {
    if (!category.articleCount) {
      category.articleCount = articles.filter(
        (article) => Number(article.categoryId || 0) === Number(category.id || 0),
      ).length
    }
  })

  return {
    generatedAt: raw?.generatedAt || '',
    config: raw?.config && typeof raw.config === 'object' ? raw.config : {},
    categories,
    categoryMap,
    articles,
    articleMap,
    tags,
    tagMap: new Map(tags.map((tag) => [String(tag.id), tag])),
  }
}

const loadStaticSiteData = async () => {
  if (!staticSitePromise) {
    staticSitePromise = fetch(STATIC_SITE_URL, { cache: 'force-cache' })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Static site data missing: ${response.status}`)
        }
        return response.json()
      })
      .then(normalizeSiteData)
      .catch((error) => {
        staticSitePromise = null
        throw error
      })
  }

  return staticSitePromise
}

export const resolveContentMode = async () => {
  if (CONTENT_SOURCE === 'static') return 'static'
  if (CONTENT_SOURCE === 'api') return 'api'

  if (!resolvedModePromise) {
    resolvedModePromise = loadStaticSiteData()
      .then(() => 'static')
      .catch(() => 'api')
  }

  return resolvedModePromise
}

export const withContentSource = async (staticResolver, apiResolver) => {
  const mode = await resolveContentMode()
  if (mode === 'static') {
    const site = await loadStaticSiteData()
    return staticResolver(site)
  }
  return apiResolver()
}

const matchKeyword = (article, keyword) => {
  const normalizedKeyword = String(keyword || '').trim().toLowerCase()
  if (!normalizedKeyword) return true

  return [article.title, article.summary, article.content, article.categoryName]
    .map((value) => String(value || '').toLowerCase())
    .some((value) => value.includes(normalizedKeyword))
}

export const getStaticArticleList = (site, params = {}) => {
  let list = [...site.articles]

  if (params.keyword) {
    list = list.filter((article) => matchKeyword(article, params.keyword))
  }

  if (params.categoryId !== undefined && params.categoryId !== null && params.categoryId !== '') {
    const categoryId = Number(params.categoryId)
    list = list.filter((article) => Number(article.categoryId || 0) === categoryId)
  }

  if (params.category) {
    const categoryName = String(params.category).trim()
    list = list.filter((article) => String(article.categoryName || '').trim() === categoryName)
  }

  return paginate(list, params)
}

export const getStaticArticleById = (site, id) => {
  const article = site.articleMap.get(Number(id))
  if (!article) {
    throw new Error('Article not found')
  }
  return article
}

export const getStaticCategories = (site) =>
  [...site.categories].sort((a, b) => {
    const orderDiff = Number(a.sortOrder || 0) - Number(b.sortOrder || 0)
    if (orderDiff !== 0) return orderDiff
    return String(a.name || '').localeCompare(String(b.name || ''), 'zh-CN')
  })

export const getStaticCategoryById = (site, id) => {
  const category = site.categoryMap.get(Number(id))
  if (!category) {
    throw new Error('Category not found')
  }
  return category
}

export const getStaticArchive = (site) => {
  const archive = {}

  site.articles.forEach((article) => {
    const publishTime = article.publishTime || article.createTime || article.updateTime
    if (!publishTime) return

    const date = new Date(publishTime)
    const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
    if (!archive[key]) {
      archive[key] = []
    }
    archive[key].push(article)
  })

  return {
    total: site.articles.length,
    archive: Object.fromEntries(
      Object.entries(archive).sort((a, b) => String(b[0]).localeCompare(String(a[0]))),
    ),
  }
}

export const getStaticTags = (site) =>
  [...site.tags].sort((a, b) => String(a.name || '').localeCompare(String(b.name || ''), 'zh-CN'))

export const getStaticArticlesByTagId = (site, id, params = {}) => {
  const tag = site.tagMap.get(String(id)) || site.tagMap.get(String(Number(id)))
  if (!tag) {
    return paginate([], params)
  }

  const articleIds = new Set(tag.articleIds.map((articleId) => Number(articleId)))
  const list = site.articles.filter((article) => articleIds.has(Number(article.id)))

  return paginate(list, params)
}

export const getStaticBlogConfig = (site) => site.config || {}
