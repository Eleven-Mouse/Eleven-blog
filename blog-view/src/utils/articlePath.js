const safeDecode = (value) => {
  if (!value) return ''
  try {
    return decodeURIComponent(value)
  } catch {
    return value
  }
}

const normalizePart = (value) => String(safeDecode(value || '')).trim()
const stripNumericPrefix = (value) => String(value || '').replace(/^\d+[-_.\s]*/, '').trim()
const isMarkdownFile = (value) => /\.mdx?$/i.test(String(value || '').trim())

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
    const parsedUrl = new URL(raw)
    const parts = parsedUrl.pathname
      .split('/')
      .filter(Boolean)
      .map((part) => safeDecode(part))

    if (parsedUrl.hostname === 'raw.githubusercontent.com') {
      return parts.length > 3 ? parts.slice(3).join('/') : ''
    }

    if (parsedUrl.hostname === 'github.com') {
      const blobIndex = parts.findIndex((part) => part === 'blob')
      if (blobIndex >= 0 && parts.length > blobIndex + 2) {
        return parts.slice(blobIndex + 2).join('/')
      }
    }

    return parts.join('/')
  } catch {
    return ''
  }
}

export const getArticleFolderLabel = (article, topicName = '') => {
  const path = extractGithubPath(article?.githubUrl || '')
  if (!path) return ''

  const parts = path
    .split('/')
    .map((part) => normalizePart(part))
    .filter(Boolean)
  const fileIndex = parts.findLastIndex(isMarkdownFile)
  if (fileIndex <= 0) return ''

  const topicNames = Array.from(
    new Set(
      [article?.categoryName, topicName]
        .map((name) => normalizePart(name))
        .map(stripNumericPrefix)
        .filter(Boolean),
    ),
  )
  const topicIndex = topicNames.length
    ? parts.findIndex((part) =>
        topicNames.some((name) => name === stripNumericPrefix(normalizePart(part))),
      )
    : -1
  const folderIndex = fileIndex - 1

  return folderIndex > topicIndex ? parts[folderIndex] : ''
}
