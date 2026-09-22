import fs from 'node:fs/promises'
import { readFileSync } from 'node:fs'
import { Buffer } from 'node:buffer'
import path from 'node:path'
import process from 'node:process'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const projectRoot = path.resolve(__dirname, '..')
const publicContentDir = path.join(projectRoot, 'public', 'content')
const outputFile = path.join(publicContentDir, 'site.json')
const generatedContentDir = path.join(projectRoot, 'src', 'generated')
const sourceOutputFile = path.join(generatedContentDir, 'site.json')
const generatedHomeFile = path.join(generatedContentDir, 'home.json')
const generatedArticlesDir = path.join(generatedContentDir, 'articles')
const assetOutputDir = path.join(publicContentDir, 'assets')
const localNotesDir = path.join(projectRoot, 'notes')
const contentConfigFile = path.join(projectRoot, 'content.config.json')

const loadContentConfig = () => {
  try {
    return JSON.parse(readFileSync(contentConfigFile, 'utf8'))
  } catch (error) {
    if (error.code !== 'ENOENT') {
      console.warn('[static-content] content.config.json is invalid:', error.message)
    }
    return {}
  }
}

const contentConfig = loadContentConfig()
const owner = String(process.env.GITHUB_CONTENT_OWNER || contentConfig.owner || '').trim()
const repo = String(process.env.GITHUB_CONTENT_REPO || contentConfig.repo || '').trim()
const branch = String(
  process.env.GITHUB_CONTENT_BRANCH || contentConfig.branch || 'main',
).trim()
const rootPath = String(process.env.GITHUB_CONTENT_ROOT || contentConfig.root || '')
  .trim()
  .replace(/^\/+|\/+$/g, '')
const token = String(process.env.GITHUB_CONTENT_TOKEN || '').trim()
const configJson = String(process.env.BLOG_STATIC_CONFIG_JSON || '').trim()

const MARKDOWN_EXTENSIONS = new Set(['.md', '.mdx'])
const ASSET_EXTENSIONS = new Set([
  '.png',
  '.jpg',
  '.jpeg',
  '.gif',
  '.webp',
  '.svg',
  '.bmp',
  '.avif',
  '.pdf',
  '.ppt',
  '.pptx',
  '.doc',
  '.docx',
  '.xls',
  '.xlsx',
  '.zip',
  '.rar',
  '.7z',
  '.txt',
  '.mp4',
  '.mov',
  '.mp3',
  '.wav',
])

const createdAssetPaths = new Set()

const apiHeaders = {
  Accept: 'application/vnd.github+json',
  'User-Agent': 'eleven-blog-static-builder',
}

if (token) {
  apiHeaders.Authorization = `Bearer ${token}`
}

const ensureDir = async (dir) => {
  await fs.mkdir(dir, { recursive: true })
}

const writeSiteData = async (siteData) => {
  await ensureDir(publicContentDir)
  await fs.rm(generatedContentDir, { recursive: true, force: true })
  await ensureDir(generatedContentDir)
  await ensureDir(generatedArticlesDir)

  const articles = Array.isArray(siteData.articles) ? siteData.articles : []
  const featuredArticle =
    articles.find(
      (article) => String(article.id) === String(siteData.config?.home_featured_article_id || ''),
    ) ||
    articles.find((article) => article.title === '首页') ||
    articles[0] ||
    null
  const articleSummaries = articles.map((article) => {
    const summary = { ...article }
    delete summary.content
    return summary
  })

  await Promise.all([
    fs.writeFile(outputFile, JSON.stringify(siteData, null, 2), 'utf8'),
    fs.writeFile(
      sourceOutputFile,
      JSON.stringify({ ...siteData, articles: articleSummaries }, null, 2),
      'utf8',
    ),
    fs.writeFile(
      generatedHomeFile,
      JSON.stringify(
        {
          id: featuredArticle?.id ?? null,
          content: featuredArticle?.content ?? '',
        },
        null,
        2,
      ),
      'utf8',
    ),
    ...articles.map((article) =>
      fs.writeFile(
        path.join(generatedArticlesDir, `${article.id}.json`),
        JSON.stringify(
          {
            id: article.id,
            content: article.content || '',
          },
          null,
          2,
        ),
        'utf8',
      ),
    ),
  ])
}

const log = (...args) => {
  console.log('[static-content]', ...args)
}

const warn = (...args) => {
  console.warn('[static-content]', ...args)
}

const stableHash = (value) => {
  let hash = 2166136261
  for (const char of String(value || '')) {
    hash ^= char.charCodeAt(0)
    hash = Math.imul(hash, 16777619)
  }
  return Math.abs(hash >>> 0)
}

const normalizeRepoPath = (value) =>
  String(value || '')
    .replace(/\\/g, '/')
    .replace(/^\/+|\/+$/g, '')

const joinRepoPath = (...parts) =>
  normalizeRepoPath(
    parts
      .filter(Boolean)
      .map((part) => String(part))
      .join('/'),
  )

const getExtension = (target) => path.posix.extname(String(target || '').trim()).toLowerCase()
const isMarkdownFile = (target) => MARKDOWN_EXTENSIONS.has(getExtension(target))
const isAssetFile = (target) => ASSET_EXTENSIONS.has(getExtension(target))

const parseScalar = (raw) => {
  const value = String(raw || '').trim()
  if (!value) return ''
  if ((value.startsWith('"') && value.endsWith('"')) || (value.startsWith("'") && value.endsWith("'"))) {
    return value.slice(1, -1)
  }
  if (value === 'true') return true
  if (value === 'false') return false
  if (value === 'null') return null
  if (/^-?\d+(\.\d+)?$/.test(value)) return Number(value)
  if (value.startsWith('[') && value.endsWith(']')) {
    return value
      .slice(1, -1)
      .split(',')
      .map((item) => parseScalar(item))
      .filter((item) => item !== '')
  }
  return value
}

const parseFrontMatter = (source) => {
  if (!String(source || '').startsWith('---\n')) {
    return { data: {}, content: source }
  }

  const endIndex = source.indexOf('\n---\n', 4)
  if (endIndex < 0) {
    return { data: {}, content: source }
  }

  const yamlBlock = source.slice(4, endIndex)
  const content = source.slice(endIndex + 5)
  const data = {}
  let currentKey = ''

  for (const line of yamlBlock.split('\n')) {
    const arrayMatch = line.match(/^\s*-\s+(.*)$/)
    if (arrayMatch && currentKey) {
      if (!Array.isArray(data[currentKey])) {
        data[currentKey] = []
      }
      data[currentKey].push(parseScalar(arrayMatch[1]))
      continue
    }

    const pairMatch = line.match(/^([A-Za-z0-9_-]+):\s*(.*)$/)
    if (!pairMatch) {
      currentKey = ''
      continue
    }

    const [, key, rawValue] = pairMatch
    currentKey = key
    const parsedValue = parseScalar(rawValue)
    data[key] = rawValue.trim() === '' ? [] : parsedValue
  }

  return { data, content }
}

const extractSummary = (markdown) =>
  String(markdown || '')
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/`[^`]*`/g, ' ')
    .replace(/!\[[^\]]*]\([^)]+\)/g, ' ')
    .replace(/\[[^\]]*]\([^)]+\)/g, ' ')
    .replace(/^#+\s+/gm, '')
    .replace(/[*_>~-]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
    .slice(0, 140)

const stripNumericPrefix = (name) => String(name || '').replace(/^\d+[-_.\s]*/, '').trim()

const titleFromPath = (filePath) => stripNumericPrefix(path.posix.basename(filePath, path.posix.extname(filePath)))

const orderFromPath = (filePath) => {
  const fileName = path.posix.basename(filePath)
  const match = fileName.match(/^(\d+)/)
  return match ? Number(match[1]) : null
}

const parseTags = (value) => {
  if (Array.isArray(value)) return value.map((item) => String(item || '').trim()).filter(Boolean)
  if (typeof value === 'string') {
    return value
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean)
  }
  return []
}

const toIsoString = (value, fallback) => {
  if (!value) return fallback
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? fallback : date.toISOString()
}

const rawFileUrl = (repoPath) =>
  `https://raw.githubusercontent.com/${owner}/${repo}/${branch}/${repoPath
    .split('/')
    .map(encodeURIComponent)
    .join('/')}`

const blobFileUrl = (repoPath) =>
  `https://github.com/${owner}/${repo}/blob/${branch}/${repoPath
    .split('/')
    .map(encodeURIComponent)
    .join('/')}`

const githubGetJson = async (url) => {
  const response = await fetch(url, { headers: apiHeaders })
  if (!response.ok) {
    throw new Error(`GitHub API request failed: ${response.status} ${url}`)
  }
  return response.json()
}

const githubGetText = async (repoPath) => {
  const response = await fetch(rawFileUrl(repoPath), { headers: token ? { Authorization: `Bearer ${token}` } : {} })
  if (!response.ok) {
    throw new Error(`Failed to fetch ${repoPath}: ${response.status}`)
  }
  return response.text()
}

const githubGetBuffer = async (repoPath) => {
  const response = await fetch(rawFileUrl(repoPath), { headers: token ? { Authorization: `Bearer ${token}` } : {} })
  if (!response.ok) {
    throw new Error(`Failed to fetch asset ${repoPath}: ${response.status}`)
  }
  return Buffer.from(await response.arrayBuffer())
}

const localNotesPath = (repoPath) =>
  path.join(localNotesDir, ...normalizeRepoPath(repoPath).split('/'))

const listLocalMarkdownFiles = async () => {
  const files = []

  const walk = async (directory, prefix = '') => {
    const entries = await fs.readdir(directory, { withFileTypes: true })
    for (const entry of entries) {
      const repoPath = joinRepoPath(prefix, entry.name)
      if (entry.isDirectory()) {
        await walk(path.join(directory, entry.name), repoPath)
      } else if (entry.isFile() && isMarkdownFile(repoPath)) {
        files.push(repoPath)
      }
    }
  }

  try {
    await walk(localNotesDir)
  } catch (error) {
    if (error.code !== 'ENOENT') throw error
  }

  return files.sort((a, b) => a.localeCompare(b, 'en'))
}

const readMarkdownFile = async (repoPath, useLocalNotes) =>
  useLocalNotes
    ? fs.readFile(localNotesPath(repoPath), 'utf8')
    : githubGetText(repoPath)

const readBinaryAsset = async (repoPath, useLocalNotes) =>
  useLocalNotes
    ? fs.readFile(localNotesPath(repoPath))
    : githubGetBuffer(repoPath)

const resolveRelativePath = (articlePath, target) => {
  const cleanTarget = String(target || '').trim()
  if (!cleanTarget || cleanTarget.startsWith('http://') || cleanTarget.startsWith('https://') || cleanTarget.startsWith('/')) {
    return cleanTarget
  }
  const normalized = cleanTarget.split('#')[0].split('?')[0]
  const articleDir = path.posix.dirname(articlePath)
  return normalizeRepoPath(path.posix.normalize(path.posix.join(articleDir, normalized)))
}

const publicAssetUrl = (repoPath) => `/content/assets/${normalizeRepoPath(repoPath)}`

const markAssetForCopy = (repoPath) => {
  if (!repoPath || !isAssetFile(repoPath)) return ''
  createdAssetPaths.add(repoPath)
  return publicAssetUrl(repoPath)
}

const rewriteMarkdownAssets = (markdown, articlePath) => {
  let output = String(markdown || '')

  output = output.replace(/!\[\[([^\]|]+)(?:\|([^\]]+))?]]/g, (full, rawTarget, rawAlias) => {
    const repoPath = resolveRelativePath(articlePath, rawTarget)
    if (!isAssetFile(repoPath)) return full
    const assetUrl = markAssetForCopy(repoPath)
    const alt = String(rawAlias || '').trim()
    return `![${alt}](${assetUrl})`
  })

  output = output.replace(/\[\[([^\]|]+)(?:\|([^\]]+))?]]/g, (full, rawTarget, rawAlias, offset, source) => {
    if (offset > 0 && source[offset - 1] === '!') return full
    const repoPath = resolveRelativePath(articlePath, rawTarget)
    if (!isAssetFile(repoPath)) return full
    const assetUrl = markAssetForCopy(repoPath)
    const text = String(rawAlias || path.posix.basename(repoPath)).trim() || path.posix.basename(repoPath)
    return `[${text}](${assetUrl})`
  })

  output = output.replace(/(!?\[[^\]]*]\()([^)]+)(\))/g, (full, prefix, rawTarget, suffix) => {
    const trimmedTarget = String(rawTarget || '').trim()
    if (
      !trimmedTarget ||
      trimmedTarget.startsWith('http://') ||
      trimmedTarget.startsWith('https://') ||
      trimmedTarget.startsWith('#') ||
      trimmedTarget.startsWith('/content/assets/')
    ) {
      return full
    }
    const repoPath = resolveRelativePath(articlePath, trimmedTarget)
    if (!isAssetFile(repoPath)) return full
    const assetUrl = markAssetForCopy(repoPath)
    return `${prefix}${assetUrl}${suffix}`
  })

  return output
}

const fetchMarkdownFiles = async () => {
  const treeUrl = `https://api.github.com/repos/${owner}/${repo}/git/trees/${encodeURIComponent(branch)}?recursive=1`
  const payload = await githubGetJson(treeUrl)
  const prefix = rootPath ? `${rootPath}/` : ''

  return (payload.tree || [])
    .filter((item) => item?.type === 'blob')
    .map((item) => String(item.path || ''))
    .filter((repoPath) => {
      if (prefix && repoPath !== rootPath && !repoPath.startsWith(prefix)) return false
      return isMarkdownFile(repoPath)
    })
    .sort((a, b) => a.localeCompare(b, 'en'))
}

const parseConfig = () => {
  if (!configJson) return {}
  try {
    const parsed = JSON.parse(configJson)
    return parsed && typeof parsed === 'object' ? parsed : {}
  } catch (error) {
    warn('BLOG_STATIC_CONFIG_JSON is not valid JSON, ignored.', error.message)
    return {}
  }
}

const buildCategoryData = (articles) => {
  const categoryMap = new Map()

  articles.forEach((article) => {
    const name = String(article.categoryName || '').trim()
    if (!name) return
    if (!categoryMap.has(name)) {
      categoryMap.set(name, {
        id: stableHash(`category:${name}`),
        name,
        slug: name.toLowerCase().replace(/\s+/g, '-'),
        description: '',
        coverImage: '',
        sortOrder: categoryMap.size + 1,
        articleCount: 0,
      })
    }
    const category = categoryMap.get(name)
    category.articleCount += 1
    article.categoryId = category.id
  })

  return Array.from(categoryMap.values())
}

const buildTagData = (articles) => {
  const tagMap = new Map()

  articles.forEach((article) => {
    article.tagIds = []
    article.tags.forEach((name) => {
      if (!tagMap.has(name)) {
        tagMap.set(name, {
          id: stableHash(`tag:${name}`),
          name,
          articleIds: [],
        })
      }
      const tag = tagMap.get(name)
      tag.articleIds.push(article.id)
      tag.articleCount = tag.articleIds.length
      article.tagIds.push(tag.id)
    })
  })

  return Array.from(tagMap.values()).sort((a, b) => String(a.name).localeCompare(String(b.name), 'zh-CN'))
}

const buildArticles = async (markdownFiles, generatedAt, useLocalNotes) => {
  const articles = []

  for (const repoPath of markdownFiles) {
    const raw = await readMarkdownFile(repoPath, useLocalNotes)
    const { data, content } = parseFrontMatter(raw)
    const relativePath = rootPath ? repoPath.slice(rootPath.length).replace(/^\/+/, '') : repoPath
    const segments = relativePath.split('/').filter(Boolean)
    const fallbackCategoryName = segments.length > 1 ? stripNumericPrefix(segments[0]) : '未分类'
    const categoryName = String(data.category || fallbackCategoryName || '未分类').trim()
    const rewrittenContent = rewriteMarkdownAssets(content, repoPath)
    const title = String(data.title || titleFromPath(repoPath)).trim() || titleFromPath(repoPath)
    const publishTime = toIsoString(
      data.publishTime || data.date || data.publish_date || data.createdAt,
      generatedAt,
    )
    const updateTime = toIsoString(
      data.updateTime || data.updatedAt || data.lastmod || data.date,
      publishTime,
    )

    articles.push({
      id: stableHash(`article:${repoPath}`),
      title,
      content: rewrittenContent,
      summary: String(data.summary || data.description || extractSummary(content)).trim(),
      coverImage: String(data.coverImage || data.cover || '').trim(),
      categoryId: null,
      categoryName,
      chapterOrder: Number(data.chapterOrder || data.order || orderFromPath(repoPath) || 0) || null,
      readingMinutes: Number(data.readingMinutes || data.readingTime || 0) || null,
      isCore: data.isCore === true || data.isCore === 1 ? 1 : 0,
      viewCount: 0,
      githubUrl: useLocalNotes ? repoPath : blobFileUrl(repoPath),
      syncStatus: 1,
      lastSyncTime: generatedAt,
      isComment: data.isComment === false || data.comments === false ? 0 : 1,
      publishTime,
      createTime: publishTime,
      updateTime,
      tags: parseTags(data.tags),
    })
  }

  return articles.sort((a, b) => new Date(b.publishTime).getTime() - new Date(a.publishTime).getTime())
}

const copyAssets = async (useLocalNotes) => {
  await fs.rm(assetOutputDir, { recursive: true, force: true })
  await ensureDir(assetOutputDir)

  const writeAsset = async (repoPath) => {
    const buffer = await readBinaryAsset(repoPath, useLocalNotes)
    const outputPath = path.join(assetOutputDir, ...normalizeRepoPath(repoPath).split('/'))
    await ensureDir(path.dirname(outputPath))
    await fs.writeFile(outputPath, buffer)
  }

  for (const repoPath of createdAssetPaths) {
    await writeAsset(repoPath)

    if (!/\.(mp4|mov)$/i.test(repoPath)) continue
    const companionPaths = [
      repoPath.replace(/\.(mp4|mov)$/i, '-poster.webp'),
      repoPath.replace(/\.(mp4|mov)$/i, '.gif'),
    ]

    for (const companionPath of companionPaths) {
      if (createdAssetPaths.has(companionPath)) continue
      try {
        await writeAsset(companionPath)
      } catch (error) {
        if (useLocalNotes && error.code === 'ENOENT') continue
        warn(`Failed to copy video companion ${companionPath}: ${error.message}`)
      }
    }
  }
}

const main = async () => {
  await ensureDir(publicContentDir)

  const localMarkdownFiles = await listLocalMarkdownFiles()
  const useLocalNotes = localMarkdownFiles.length > 0

  if (useLocalNotes) {
    log(`Loading ${localMarkdownFiles.length} markdown files from local notes/`)
  } else if (!owner || !repo) {
    try {
      const existingSiteData = JSON.parse(await fs.readFile(outputFile, 'utf8'))
      log('No GitHub source env found. Reusing existing public/content/site.json.')
      await writeSiteData(existingSiteData)
    } catch {
      warn('No GitHub source env found. Generating empty static content.')
      await writeSiteData({
        generatedAt: '',
        config: {},
        categories: [],
        tags: [],
        articles: [],
      })
    }
    return
  }

  const generatedAt = new Date().toISOString()
  if (!useLocalNotes) {
    log(`Loading markdown from ${owner}/${repo}@${branch}${rootPath ? ` (${rootPath})` : ''}`)
  }

  const markdownFiles = useLocalNotes ? localMarkdownFiles : await fetchMarkdownFiles()
  if (!markdownFiles.length) {
    warn('No markdown files found. Skip static content generation.')
    return
  }

  const articles = await buildArticles(markdownFiles, generatedAt, useLocalNotes)
  const categories = buildCategoryData(articles)
  const tags = buildTagData(articles)
  const config = parseConfig()

  if (!config.home_featured_article_id) {
    const featured = articles.find((article) => article.title === '首页') || articles[0]
    if (featured) {
      config.home_featured_article_id = featured.id
    }
  }

  await copyAssets(useLocalNotes)
  await writeSiteData({
    generatedAt,
    config,
    categories,
    tags,
    articles,
  })

  log(`Generated ${articles.length} articles, ${categories.length} categories, ${tags.length} tags.`)
}

main().catch((error) => {
  console.error('[static-content] generation failed:', error)
  process.exitCode = 1
})
