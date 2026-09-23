# Eleven Blog Frontend

Eleven Blog 的前端工程。基于 Vue 3 + Vite，构建期读取 Markdown 生成文章 JSON 与静态资源，产物为纯静态站点，无需后端 API、数据库或缓存服务。

完整项目说明见根目录 [`README.md`](../README.md)。

## 环境要求

- Node.js `20.19+` 或 `22.12+`
- npm `10+`

## 快速开始

```bash
npm ci
npm run dev
```

开发服务器默认运行在 `http://localhost:3000`。`predev` 钩子会先执行内容生成，无需手动构建。

## 环境变量

`.env` 已提供可运行的默认配置并被 Git 跟踪，本地开发通常无需改动。所有变量均有代码级默认值（见 `src/components/GiscusComments.vue`、`src/content/siteContent.js`）。

需要个性化时，以 `.env.example` 为模板编辑 `.env.local`（不提交）。本地 `notes/` 下有 Markdown 时，内容源始终以本地为准。

| 变量 | 默认值 | 说明 |
|:---|:---|:---|
| `VITE_CONTENT_SOURCE` | `static` | 数据来源：`static` 构建期生成，`auto` 回退到运行时 API |
| `VITE_GISCUS_REPO` | `Eleven-Mouse/Eleven-blog` | Giscus 评论仓库 |
| `VITE_GISCUS_REPO_ID` | `R_kgDOQxLe7w` | 仓库 ID |
| `VITE_GISCUS_CATEGORY` | `Announcements` | Discussions 分类名 |
| `VITE_GISCUS_CATEGORY_ID` | `DIC_kwDOQxLe784DGJ97` | 分类 ID |
| `VITE_GISCUS_MAPPING` | `pathname` | 评论与页面的映射方式 |
| `GITHUB_CONTENT_*` | 空 | 可选的 GitHub 内容源（owner/repo/branch/root/token），仅构建期使用，Token 禁止使用 `VITE_` 前缀 |
| `BLOG_STATIC_CONFIG_JSON` | 空 | 可选的站点信息（名称、简介、GitHub 链接），构建期注入 |

## 命令

| 命令 | 说明 |
|:---|:---|
| `npm run dev` | 生成内容并启动开发服务器 |
| `npm run build:content` | 只生成静态内容 |
| `npm run build` | 生成内容并构建生产包到 `dist/` |
| `npm run preview` | 本地预览生产构建 |
| `npm run lint` | 执行 ESLint |

## 内容来源

默认优先读取本地 `notes/`（Git 子模块）：

```text
notes/
├── Java/
│   ├── 01-基础.md
│   └── image.png
└── Network/
    └── TCP.md
```

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

改用 GitHub 仓库作为内容源时，配置 `GITHUB_CONTENT_*` 环境变量，或复制 `content.config.example.json` 为 `content.config.json`。环境变量优先。

## 部署

### Vercel

| 配置项 | 值 |
|:---|:---|
| Root Directory | `blog-view` |
| Install Command | `npm ci` |
| Build Command | `npm run build` |
| Output Directory | `dist` |

`vercel.json` 已配置 SPA 重写与缓存策略：`assets/` 一年不可变缓存，`content/` 资源 30 天，`site.json` 短缓存 + 后台刷新，`index.html` 不缓存。

### Docker + Nginx

```bash
docker build -t eleven-blog .
docker run -p 8080:80 eleven-blog
```

| 文件 | 用途 |
|:---|:---|
| `Dockerfile` | 两阶段构建：Node 构建产物 → Nginx 运行 |
| `nginx-view.conf` | 站点配置：gzip、静态资源缓存、SPA 回退 |
| `nginx-security-headers.conf` | 公共安全响应头（CSP、X-Frame-Options 等） |

安全头放在 `/etc/nginx/snippets/` 下而非 `conf.d/`，避免被 `include *.conf` 当作顶层配置二次加载；由于 nginx 的 `add_header` 不跨级继承，每个带 `add_header` 的 location 都需要单独 include 该文件。HSTS 已备好但默认注释，待 HTTPS 就绪后放开。

## 相关说明

评论由 Giscus 提供，部署前请确认目标仓库已安装 Giscus GitHub App，且 `VITE_GISCUS_*` 与 GitHub Discussions 分类一致。
