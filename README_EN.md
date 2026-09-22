<div align="center">

# Eleven Blog

**A static personal blog built with Vue 3 and Vite**

Markdown lives in a separate `notes` repository. The build generates article data and static assets for deployment to Vercel or any static host.

[![Vue 3](https://img.shields.io/badge/Vue-3.5-42B883?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![Vite 7](https://img.shields.io/badge/Vite-7.3-646CFF?logo=vite&logoColor=white)](https://vite.dev/)
[![Node.js](https://img.shields.io/badge/Node.js-20.19%2B%20%7C%2022.12%2B-5FA04E?logo=nodedotjs&logoColor=white)](https://nodejs.org/)
[![Vercel](https://img.shields.io/badge/Deploy-Vercel-000000?logo=vercel&logoColor=white)](https://vercel.com/)
[![Docker](https://img.shields.io/badge/Docker-Nginx-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

English | [中文](README.md)

</div>

---

## Overview

Eleven Blog no longer requires a database, Redis, or a long-running backend. Articles are parsed during the build, and the browser only receives static HTML, JSON, and assets.

```text
Obsidian / Markdown
        │
        ▼
blog-view/notes (Git submodule)
        │
        ▼
generate-static-site.mjs
  Parse Front Matter, generate article data, mirror assets
        │
        ▼
Vite build
        │
        ▼
Vercel / Nginx / any static host
```

When the local `notes/` directory is empty, the build can fetch Markdown from GitHub. Use a read-only token for private content repositories.

## Features

- Local `notes/` content first, managed as an independent Git submodule
- Optional GitHub repository content source
- YAML Front Matter, directory categories, numeric prefix sorting, and tags
- Obsidian-style image, attachment, and video asset rewriting
- Article content split into per-article JSON files for lazy loading
- Giscus comments backed by GitHub Discussions
- Light and dark themes, archive, tags, categories, and article outline
- Zero-backend Vercel deployment plus optional Docker and Nginx hosting

## Getting Started

### Requirements

- Node.js `20.19+` or `22.12+`
- npm `10+`
- Git

### 1. Clone the app and content submodule

```bash
git clone --recurse-submodules https://github.com/Eleven-Mouse/Eleven-blog.git
cd Eleven-blog/blog-view
```

For an existing clone:

```bash
git submodule update --init --recursive
```

### 2. Configure environment variables

The project includes defaults for local preview. For overrides, use `.env.example` as the template for `.env.local`. If `.env.local` already exists, do not overwrite it because it may contain Vercel CLI credentials.

### 3. Start development

```bash
npm ci
npm run dev
```

The development server runs at `http://localhost:3000`. `npm run dev` generates static content before starting Vite.

### 4. Build and preview

```bash
npm run build
npm run preview
```

The production output is written to `blog-view/dist/`.

## Content Workflow

The content directory is a Git submodule:

```text
Eleven-blog/
└── blog-view/
    └── notes/    -> https://github.com/Eleven-Mouse/Notes.git
```

The first directory level becomes the default category:

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

Recommended publishing flow:

```bash
# Commit and push the content repository first.
git -C blog-view/notes add .
git -C blog-view/notes commit -m "docs: add new article"
git -C blog-view/notes push

# Then update the submodule pointer in the app repository.
git add blog-view/notes
git commit -m "chore: update notes"
git push
```

### Front Matter

```yaml
---
title: Redis Persistence
category: Storage
tags: [Redis, Persistence]
chapter_order: 4
reading_minutes: 12
summary: Compare RDB, AOF, and hybrid persistence.
is_comment: true
---
```

If `title` is omitted, the file name is used. If `category` is omitted, the first directory level is used after removing numeric prefixes such as `1-` or `02-`.

## Configuration

See [`blog-view/.env.example`](blog-view/.env.example) for the complete environment template.

| Variable | Required | Purpose |
|:---|:---:|:---|
| `VITE_CONTENT_SOURCE` | Yes | Set to `static` to use build-time content |
| `VITE_GISCUS_REPO` | Yes | Giscus repository in `owner/repo` format |
| `VITE_GISCUS_REPO_ID` | Yes | GitHub repository ID |
| `VITE_GISCUS_CATEGORY` | Yes | GitHub Discussions category name |
| `VITE_GISCUS_CATEGORY_ID` | Yes | GitHub Discussions category ID |
| `VITE_GISCUS_MAPPING` | No | Comment mapping mode, defaults to `pathname` |
| `GITHUB_CONTENT_OWNER` | No | Content repository owner, used only without local `notes/` |
| `GITHUB_CONTENT_REPO` | No | Content repository name |
| `GITHUB_CONTENT_BRANCH` | No | Content branch, defaults to `main` |
| `GITHUB_CONTENT_ROOT` | No | Optional repository subdirectory |
| `GITHUB_CONTENT_TOKEN` | No | Read-only token for private repositories or higher API limits; never expose it to the browser |
| `BLOG_STATIC_CONFIG_JSON` | No | JSON configuration written into the generated site |

Alternatively, copy [`blog-view/content.config.example.json`](blog-view/content.config.example.json) to `content.config.json`. Environment variables take precedence over file configuration.

## Project Structure

```text
Eleven-blog/
├── blog-view/
│   ├── notes/                    # Content submodule
│   ├── public/
│   │   ├── content/              # Generated public content and assets
│   │   ├── giscus-*.css          # Giscus themes
│   │   └── avatar.png
│   ├── scripts/
│   │   └── generate-static-site.mjs
│   ├── src/
│   │   ├── api/                  # Static data adapter
│   │   ├── components/
│   │   ├── content/              # Static content loader
│   │   ├── stores/
│   │   ├── utils/
│   │   └── views/
│   ├── content.config.example.json
│   ├── .env.example
│   ├── vite.config.js
│   └── vercel.json
├── docker-compose.yml            # Optional Nginx self-hosting
└── .env.example                  # Docker Compose build variables
```

## Deployment

### Vercel

Use these project settings:

| Setting | Value |
|:---|:---|
| Root Directory | `blog-view` |
| Install Command | `npm ci` |
| Build Command | `npm run build` |
| Output Directory | `dist` |

Set any overrides in the Vercel dashboard and make sure the build environment can access the content submodule or GitHub content repository.

### Docker + Nginx

Docker uses `blog-view/notes/` when available. To fetch content from GitHub instead, copy the root environment template first:

```bash
cp .env.example .env
docker compose up -d --build
```

The default address is `http://localhost:8080`. Set `BLOG_PORT` to change it.

## Commands

Run all commands from `blog-view/`:

| Command | Purpose |
|:---|:---|
| `npm run dev` | Generate content and start the Vite development server |
| `npm run build:content` | Generate article data and assets only |
| `npm run build` | Generate content and build for production |
| `npm run preview` | Preview the production build locally |
| `npm run lint` | Run ESLint |

## Notes

- `GITHUB_CONTENT_TOKEN` is build-only. Do not prefix it with `VITE_`.
- `blog-view/notes` is a separate Git repository. Commit content and the parent submodule pointer separately.
- The project has no built-in admin, database, or server-side comments.
