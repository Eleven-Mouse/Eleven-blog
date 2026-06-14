const IMAGE_EXTENSIONS = new Set(['png', 'jpg', 'jpeg', 'gif', 'webp', 'svg', 'bmp', 'avif'])
const EMBED_DOCUMENT_EXTENSIONS = new Set(['pdf'])
const FILE_EXTENSIONS = new Set([
  ...IMAGE_EXTENSIONS,
  ...EMBED_DOCUMENT_EXTENSIONS,
  'pdf',
  'ppt',
  'pptx',
  'doc',
  'docx',
  'xls',
  'xlsx',
  'zip',
  'rar',
  '7z',
  'txt',
  'md',
])

const stripQueryAndHash = (value) => String(value || '').split('#')[0].split('?')[0]

const getExt = (target) => {
  const cleaned = stripQueryAndHash(target).trim()
  const dot = cleaned.lastIndexOf('.')
  if (dot < 0 || dot === cleaned.length - 1) return ''
  return cleaned.slice(dot + 1).toLowerCase()
}

const isLikelyAsset = (target) => FILE_EXTENSIONS.has(getExt(target))

const filenameFromTarget = (target) => {
  const cleaned = stripQueryAndHash(target).replace(/\\/g, '/')
  const pieces = cleaned.split('/')
  return pieces[pieces.length - 1] || cleaned
}

const toAssetUrl = (target) => {
  const cleaned = String(target || '').trim()
  if (!cleaned) return ''
  if (
    cleaned.startsWith('http://') ||
    cleaned.startsWith('https://') ||
    cleaned.startsWith('/images/') ||
    cleaned.startsWith('/upload/')
  ) {
    return cleaned
  }
  if (cleaned.startsWith('/')) {
    return cleaned
  }
  return `/upload/${cleaned}`
}

export const transformObsidianAssetLinks = (markdown) => {
  if (!markdown) return markdown

  // Embed form: ![[file.ext|alt]]
  let output = String(markdown).replace(/!\[\[([^\]|]+)(?:\|([^\]]+))?]]/g, (_, rawTarget, rawAlias) => {
    const target = String(rawTarget || '').trim()
    if (!isLikelyAsset(target)) return _
    const url = toAssetUrl(target)
    if (!url) return _

    const alias = String(rawAlias || '').trim()
    const ext = getExt(target)
    if (IMAGE_EXTENSIONS.has(ext)) {
      return `![${alias}](${url})`
    }
    if (EMBED_DOCUMENT_EXTENSIONS.has(ext)) {
      return `![${alias}](${url})`
    }
    const text = alias || filenameFromTarget(target)
    return `[${text}](${url})`
  })

  // Link form: [[file.ext|text]]
  output = output.replace(/\[\[([^\]|]+)(?:\|([^\]]+))?]]/g, (full, rawTarget, rawAlias, offset, source) => {
    if (offset > 0 && source[offset - 1] === '!') return full
    const target = String(rawTarget || '').trim()
    if (!isLikelyAsset(target)) return full

    const url = toAssetUrl(target)
    if (!url) return full

    const alias = String(rawAlias || '').trim()
    const text = alias || filenameFromTarget(target)
    return `[${text}](${url})`
  })

  return output
}
