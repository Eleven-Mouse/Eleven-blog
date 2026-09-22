# Eleven Blog Frontend

Eleven Blog 的静态站点工程。项目在构建期读取 Markdown，生成文章 JSON 和资源文件，再由 Vite 输出纯静态站点。

完整项目说明见根目录 [`README.md`](../README.md)。

## 开发环境

- Node.js `20.19+` 或 `22.12+`
- npm `10+`

项目已提供可用于本地预览的默认配置。需要个性化时，以 `.env.example` 为模板编辑 `.env.local`；已有 `.env.local` 时不要覆盖。

```bash
npm ci
npm run dev
```

开发服务器默认运行在 `http://localhost:3000`。

## 内容来源

默认优先读取 `notes/`：

```text
notes/
├── Java/
│   ├── 01-基础.md
│   └── image.png
└── Network/
    └── TCP.md
```

如果用 GitHub 仓库作为内容源，可配置 `GITHUB_CONTENT_*` 环境变量，或复制 `content.config.example.json` 为 `content.config.json`。环境变量优先。

构建流程：

```text
notes / GitHub
    │
    ▼
scripts/generate-static-site.mjs
    │ 生成 site.json、文章 JSON、公开资源
    ▼
vite build
    │
    ▼
dist/
```

## 命令

| 命令 | 说明 |
|:---|:---|
| `npm run dev` | 生成内容并启动开发服务器 |
| `npm run build:content` | 只生成静态内容 |
| `npm run build` | 生成内容并构建生产包 |
| `npm run preview` | 预览生产构建 |
| `npm run lint` | 执行 ESLint |

## 部署

Vercel 配置：

| 配置项 | 值 |
|:---|:---|
| Root Directory | `blog-view` |
| Install Command | `npm ci` |
| Build Command | `npm run build` |
| Output Directory | `dist` |

评论由 Giscus 提供。部署前请确认目标仓库已经安装 Giscus GitHub App，且 `VITE_GISCUS_*` 与 GitHub Discussions 分类一致。

项目不需要后端 API、数据库、Redis 或文件上传服务。
