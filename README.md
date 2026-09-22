<div align="center">

# Eleven Blog

**一个基于 Vue 3 + Vite 的静态个人博客**

Markdown 存放在独立的 `notes` 仓库中，构建阶段生成文章数据与静态资源，最终部署到 Vercel 或任意静态服务器。

[![Vue 3](https://img.shields.io/badge/Vue-3.5-42B883?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![Vite 7](https://img.shields.io/badge/Vite-7.3-646CFF?logo=vite&logoColor=white)](https://vite.dev/)
[![Node.js](https://img.shields.io/badge/Node.js-20.19%2B%20%7C%2022.12%2B-5FA04E?logo=nodedotjs&logoColor=white)](https://nodejs.org/)
[![Vercel](https://img.shields.io/badge/Deploy-Vercel-000000?logo=vercel&logoColor=white)](https://vercel.com/)
[![Docker](https://img.shields.io/badge/Docker-Nginx-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

[English](README_EN.md) | 中文

</div>

---

## 项目概览

Eleven Blog 不再依赖数据库、Redis 或常驻后端服务。文章在构建时完成解析，浏览器只访问静态 HTML、JSON 和资源文件。

```text
Obsidian / Markdown
        │
        ▼
blog-view/notes（Git submodule）
        │
        ▼
generate-static-site.mjs
  解析 Front Matter、生成文章数据、镜像资源
        │
        ▼
Vite build
        │
        ▼
Vercel / Nginx / 任意静态托管
```

如果本地 `notes` 目录为空，构建脚本也可以从 GitHub 仓库拉取 Markdown；私有内容仓库建议在构建环境中提供只读 Token。

## 主要能力

- 本地 `notes/` 内容优先，支持 Git 子模块独立管理文章
- 支持 GitHub 内容源作为本地笔记的构建回退
- 解析 YAML Front Matter、目录分类、数字前缀排序和标签
- 兼容 Obsidian 图片、附件、视频等资源引用并在构建时重写路径
- 文章内容按需拆分为独立 JSON，避免首页一次加载全部正文
- 评论使用 Giscus，由 GitHub Discussions 托管
- 支持亮色/暗色主题、归档、标签、分类和文章目录
- 支持 Vercel 零后端部署，也支持 Docker + Nginx 自托管

## 快速开始

### 环境要求

- Node.js `20.19+` 或 `22.12+`
- npm `10+`
- Git

### 1. 克隆项目与内容子模块

```bash
git clone --recurse-submodules https://github.com/Eleven-Mouse/Eleven-blog.git
cd Eleven-blog/blog-view
```

已克隆项目时，再初始化子模块：

```bash
git submodule update --init --recursive
```

### 2. 配置环境变量

项目已提供可用于本地预览的默认配置。需要个性化时，以 `.env.example` 为模板编辑 `.env.local`；如果本地已经存在 `.env.local`，不要直接覆盖，以免丢失 Vercel CLI 写入的本地凭据。

### 3. 启动开发环境

```bash
npm ci
npm run dev
```

开发服务器默认运行在 `http://localhost:3000`。`npm run dev` 会先执行一次静态内容生成。

### 4. 构建与预览

```bash
npm run build
npm run preview
```

构建产物位于 `blog-view/dist/`。

## 内容工作流

当前内容目录是一个 Git 子模块：

```text
Eleven-blog/
└── blog-view/
    └── notes/    -> https://github.com/Eleven-Mouse/Notes.git
```

文章目录按主题组织，第一级目录默认作为分类：

```text
notes/
├── 0-首页/
│   ├── 首页.md
│   └── cover.png
├── 3-存储层/
│   └── MySQL/
│       ├── 1-MVCC.md
│       └── 2-事务.md
└── 6-Agent/
    └── 1-Agent基础认知.md
```

推荐的发布流程：

```bash
# 先在内容仓库提交并推送
git -C blog-view/notes add .
git -C blog-view/notes commit -m "docs: add new article"
git -C blog-view/notes push

# 再更新主仓库记录的子模块指针
git add blog-view/notes
git commit -m "chore: update notes"
git push
```

### Front Matter

```yaml
---
title: Redis 持久化
category: 存储层
tags: [Redis, 持久化]
chapter_order: 4
reading_minutes: 12
summary: 对比 RDB、AOF 与混合持久化。
is_comment: true
---
```

未填写 `title` 时使用文件名；未填写 `category` 时使用文件所在的第一级目录，并自动去掉 `1-`、`02-` 这类排序前缀。

## 配置说明

环境变量示例位于 [`blog-view/.env.example`](blog-view/.env.example)。

| 变量 | 必需 | 说明 |
|:---|:---:|:---|
| `VITE_CONTENT_SOURCE` | 是 | 固定为 `static` 时使用构建期静态数据 |
| `VITE_GISCUS_REPO` | 是 | Giscus 所在仓库，格式为 `owner/repo` |
| `VITE_GISCUS_REPO_ID` | 是 | GitHub 仓库 ID |
| `VITE_GISCUS_CATEGORY` | 是 | Discussions 分类名称 |
| `VITE_GISCUS_CATEGORY_ID` | 是 | Discussions 分类 ID |
| `VITE_GISCUS_MAPPING` | 否 | 评论映射方式，默认 `pathname` |
| `GITHUB_CONTENT_OWNER` | 否 | 内容仓库 owner，仅在未使用本地 `notes/` 时需要 |
| `GITHUB_CONTENT_REPO` | 否 | 内容仓库名 |
| `GITHUB_CONTENT_BRANCH` | 否 | 内容分支，默认 `main` |
| `GITHUB_CONTENT_ROOT` | 否 | 只读取仓库中的子目录 |
| `GITHUB_CONTENT_TOKEN` | 否 | 私有仓库或需要规避 API 限流时使用，禁止暴露到浏览器 |
| `BLOG_STATIC_CONFIG_JSON` | 否 | 构建期写入的站点配置 JSON |

也可以复制 [`blog-view/content.config.example.json`](blog-view/content.config.example.json) 为 `content.config.json`，使用文件配置 GitHub 内容源。环境变量优先于文件配置。

## 项目结构

```text
Eleven-blog/
├── blog-view/
│   ├── notes/                    # 内容子模块
│   ├── public/
│   │   ├── content/              # 构建生成的公开内容与资源
│   │   ├── giscus-*.css          # Giscus 主题
│   │   └── avatar.png
│   ├── scripts/
│   │   └── generate-static-site.mjs
│   ├── src/
│   │   ├── api/                  # 静态数据访问适配层
│   │   ├── components/
│   │   ├── content/              # 静态内容读取
│   │   ├── stores/
│   │   ├── utils/
│   │   └── views/
│   ├── content.config.example.json
│   ├── .env.example
│   ├── vite.config.js
│   └── vercel.json
├── docker-compose.yml            # 可选的 Nginx 自托管
└── .env.example                  # Docker Compose 构建参数
```

## 部署

### Vercel

在 Vercel 项目设置中使用：

| 配置项 | 值 |
|:---|:---|
| Root Directory | `blog-view` |
| Install Command | `npm ci` |
| Build Command | `npm run build` |
| Output Directory | `dist` |

在 Vercel 中配置需要覆盖的环境变量，并确保构建环境能够访问内容子模块或 GitHub 内容仓库。

### Docker + Nginx

Docker 构建会优先使用 `blog-view/notes/`。需要从 GitHub 拉取内容时，可先复制根目录环境变量模板：

```bash
cp .env.example .env
docker compose up -d --build
```

默认访问地址为 `http://localhost:8080`，可通过 `BLOG_PORT` 修改端口。

## 常用命令

所有命令均在 `blog-view/` 下执行：

| 命令 | 说明 |
|:---|:---|
| `npm run dev` | 生成静态内容并启动 Vite 开发服务器 |
| `npm run build:content` | 只生成文章数据和资源 |
| `npm run build` | 生成静态内容并构建生产包 |
| `npm run preview` | 本地预览生产构建 |
| `npm run lint` | 运行 ESLint |

## 说明

- `GITHUB_CONTENT_TOKEN` 只用于构建脚本，不要添加 `VITE_` 前缀。
- `blog-view/notes` 是独立 Git 仓库，内容提交和主仓库子模块指针需要分别提交。
- 公开仓库没有内置后台、数据库或服务端评论系统。
