/**
 * 为 notes/ 下的 markdown 文件补充 frontmatter publishTime，
 * 取值为该文件在 notes 仓库中的首次提交日期，保证博客文章
 * 的发表时间不随 Vercel 重新部署而变化。
 *
 * 用法：
 *   node scripts/add-publish-time.mjs           # 预览模式，只打印将要做的修改
 *   node scripts/add-publish-time.mjs --write   # 实际写入文件
 *
 * 已有日期字段（publishTime/date/publish_date/createdAt）的文件会被跳过，
 * 因此可以重复执行：新文章提交到 notes 仓库后再跑一次即可补上日期。
 */
import fs from 'node:fs/promises'
import path from 'node:path'
import { execFileSync } from 'node:child_process'
import { fileURLToPath } from 'node:url'

const projectRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const notesDir = path.join(projectRoot, 'notes')
const writeMode = process.argv.includes('--write')
const MARKDOWN_EXTENSIONS = new Set(['.md'])
const DATE_KEYS = ['publishTime', 'date', 'publish_date', 'createdAt']
const SKIP_DIRS = new Set(['.git', '.obsidian', '.trash', '.idea', '.claude', '.agents'])

const listMarkdownFiles = async (directory, prefix = '') => {
  const files = []
  const entries = await fs.readdir(directory, { withFileTypes: true })
  for (const entry of entries) {
    if (entry.isDirectory()) {
      if (SKIP_DIRS.has(entry.name)) continue
      files.push(...(await listMarkdownFiles(path.join(directory, entry.name), path.posix.join(prefix, entry.name))))
    } else if (entry.isFile() && MARKDOWN_EXTENSIONS.has(path.extname(entry.name).toLowerCase())) {
      files.push({ absolute: path.join(directory, entry.name), repoPath: path.posix.join(prefix, entry.name) })
    }
  }
  return files
}

const firstCommitDate = (repoPath) => {
  try {
    const output = execFileSync(
      'git',
      ['log', '--follow', '--diff-filter=A', '--format=%aI', '--', repoPath],
      { cwd: notesDir, encoding: 'utf8' },
    )
    const dates = output.split('\n').map((line) => line.trim()).filter(Boolean)
    // git 按时间倒序列出，最后一行是最早的（首次添加）记录
    return dates.at(-1) || null
  } catch {
    return null
  }
}

const frontMatterOf = (source) => {
  if (!source.startsWith('---\n')) return null
  const endIndex = source.indexOf('\n---\n', 4)
  if (endIndex < 0) return null
  return source.slice(4, endIndex)
}

const hasDateField = (yamlBlock) =>
  yamlBlock
    .split('\n')
    .some((line) => DATE_KEYS.some((key) => new RegExp(`^${key}\\s*:`, 'i').test(line)))

const withPublishTime = (source, date) => {
  const line = `publishTime: ${date}\n`
  if (!source.startsWith('---\n')) {
    return `---\n${line}---\n\n${source}`
  }
  // 已有 frontmatter 但缺少日期字段：插入到开头之后
  return `---\n${line}${source.slice(4)}`
}

const main = async () => {
  const files = await listMarkdownFiles(notesDir)
  let added = 0
  let skipped = 0
  const missing = []

  for (const { absolute, repoPath } of files) {
    const source = await fs.readFile(absolute, 'utf8')
    const yamlBlock = frontMatterOf(source)
    if (yamlBlock && hasDateField(yamlBlock)) {
      skipped += 1
      continue
    }

    const date = firstCommitDate(repoPath)
    if (!date) {
      missing.push(repoPath)
      continue
    }

    if (writeMode) {
      await fs.writeFile(absolute, withPublishTime(source, date), 'utf8')
    }
    added += 1
    console.log(`${writeMode ? '[written]' : '[preview]'} ${repoPath} -> publishTime: ${date}`)
  }

  console.log(`\n共 ${files.length} 个文件：${added} 个${writeMode ? '已写入' : '待写入'}，${skipped} 个已有日期，${missing.length} 个无 git 记录`)
  if (missing.length > 0) {
    console.warn('以下文件从未提交过，无法获取首次提交日期，请手动添加 publishTime：')
    missing.forEach((name) => console.warn(`  - ${name}`))
  }
  if (!writeMode && added > 0) {
    console.log('\n这是预览模式，确认无误后执行：node scripts/add-publish-time.mjs --write')
  }
}

main().catch((error) => {
  console.error('[add-publish-time] failed:', error)
  process.exitCode = 1
})
