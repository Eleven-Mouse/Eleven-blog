<div align="center">

# Eleven Blog

**A minimalist personal blog powered by GitHub as a content source**

Write in Markdown, version with Git, publish with a `git push`.

[![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot 3.5.6](https://img.shields.io/badge/Spring%20Boot-3.5.6-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue 3.5](https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![MySQL 8.0](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis 7](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

English | [中文](README_CN.md)

</div>

---

## Screenshots

<div align="center">
<img width="1920" height="869" alt="Homepage" src="https://github.com/user-attachments/assets/100f169a-0a2c-40b1-bef0-7dfeb850387c" />
<img width="1920" height="869" alt="Article detail" src="https://github.com/user-attachments/assets/174200d1-e589-429a-a719-16675eb9b095" />
<img width="1920" height="869" alt="Dark theme" src="https://github.com/user-attachments/assets/52956207-f557-437d-9106-c94a2267a0d7" />
</div>

---

## Why This Project

Traditional blog platforms (WordPress, Hexo, Hugo) either require a database-backed editor or a local build-and-deploy cycle. Eleven Blog takes a different approach:

**Your GitHub repository is the content source.** Maintain Markdown files in your own repo, and Eleven Blog automatically discovers, syncs, and publishes them. It supports YAML Front Matter, Obsidian-style embeds, and automatic asset mirroring. Once you set up a Webhook, a simple `git push` triggers incremental synchronization.

---

## Features

| Feature | Description |
|:---|:---|
| GitHub Content Sync | Auto-discovers `.md` files in a repo, matches existing articles or creates new ones, with full Front Matter parsing |
| Webhook-driven Sync | Configure a GitHub Webhook; `git push` triggers incremental sync with HMAC-SHA256 signature verification |
| Obsidian Compatible | Supports `![[image.png]]` embeds and `[[wiki-links]]`, with automatic asset mirroring to local storage |
| Auto Category Inference | Categories derived from file paths, with numeric prefix sorting (e.g. `01-Java/`, `02-Network/`) |
| Markdown Rendering | Full Markdown rendering with syntax highlighting, table of contents (TOC), and image lightbox |
| Nested Comments | Multi-level replies, floor numbering, comment likes, pinning, and GitHub OAuth owner identification |
| Anti-spam | Redis-based rate limiting at multiple levels: content dedup, per-IP short-window throttling, daily caps |
| View Counter | Redis counting with IP dedup (10-min window), periodic batch sync to MySQL |
| Dark / Light Theme | Global theme toggle with localStorage persistence |
| Interactive About Page | Typed.js-powered conversational "about me" UI |
| Docker One-command Deploy | Docker Compose with MySQL + Redis + backend + frontend, including health checks |
| Email Notifications | Async comment reply notifications via JavaMailSender |

---

## Architecture

```
┌──────────────┐     ┌──────────────┐
│   Visitor     │     │ GitHub Repo  │
│   Browser     │     │ (Markdown)   │
│   ( :80)      │     │              │
└──────┬───────┘     └──────┬───────┘
       │                    │ Webhook (push)
       ▼                    │
┌──────────────────────────┤
│   blog-view (Nginx)      │
│   Vue 3 + Vite + Element │
│   Pinia + Vue Router     │
└──────────┬───────────────┘
           │ /api/**
           ▼
┌──────────────────────────┐
│   blog-backend (:8081)   │
│   Spring Boot 3.5.6      │──── GitHub API (Trees / Raw)
│   Spring Security + JWT  │
│   MyBatis + PageHelper   │
└─────┬────────────┬───────┘
      │            │
      ▼            ▼
┌───────────┐ ┌──────────┐
│ MySQL 8.0 │ │ Redis 7  │
│ (Primary) │ │ (Cache)  │
└───────────┘ └──────────┘
```

### Backend Maven Multi-module

```
blog-backend/
├── blog-common/     # JWT filter, Security config, unified response, utilities
├── blog-pojo/       # Entity, DTO, VO data objects
└── blog-server/     # Controllers, Services, Mappers, scheduled tasks (entry point)
```

---

## Tech Stack

<table>
<tr>
<th width="33%">Backend</th>
<th width="33%">Frontend</th>
<th width="33%">Infrastructure</th>
</tr>
<tr>
<td>

- Java 21
- Spring Boot 3.5.6
- Spring Security + JWT (jjwt)
- MyBatis 3.0.5 + PageHelper
- Spring Data Redis
- Spring Boot Mail
- ip2region (offline IP geolocation)
- Spring Boot Actuator
- Lombok

</td>
<td>

- Vue 3.5 (Composition API)
- Vite 7.3
- Vue Router 4
- Pinia 3
- Element Plus 2.13
- Axios (auto-retry)
- marked + highlight.js
- md-editor-v3
- typed.js (chat UI)

</td>
<td>

- Docker Compose
- Nginx (reverse proxy + Gzip)
- MySQL 8.0 container
- Redis 7 Alpine container
- Multi-stage Docker build
- Health checks (Actuator)
- Volume persistence

</td>
</tr>
</table>

---

## Getting Started

### Prerequisites

- Java 21+
- Node.js 20.19+ or 22.12+
- MySQL 8.0
- Redis 7+
- Docker & Docker Compose (for containerized deployment)

### Option A: Docker Compose (Recommended)

```bash
git clone https://github.com/your-username/Eleven-blog.git
cd Eleven-blog

# Copy and fill in environment variables
cp .env.backend.example .env.backend
# Edit .env.backend with your actual values

# Start all services
docker compose up -d
```

| Service | Port | Description |
|:---|:---|:---|
| `blog-view` | `:80` | Public blog (Nginx) |
| `blog-backend` | `:8081` | REST API (Spring Boot) |
| `mysql` | Internal | MySQL 8.0 database |
| `redis` | Internal | Redis cache |

### Option B: Local Development

**1. Initialize the database**

```bash
mysql -u root -p
CREATE DATABASE eleven_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eleven_blog;
SOURCE sql/kunxing_blog_cloud_minimal.sql;
```

**2. Start the backend**

```bash
cd blog-backend
# Edit src/main/resources/application.yml with your DB and Redis credentials
mvn clean package -DskipTests
java -jar blog-server/target/blog-server-1.0-SNAPSHOT.jar
```

**3. Start the frontend**

```bash
cd blog-view
npm install
# Dev server with automatic API proxy to localhost:8081
npm run dev

# Or production build
npm run build
```

---

## Environment Variables

The backend reads configuration from a `.env.backend` file or environment variables:

| Variable | Description | Example |
|:---|:---|:---|
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `your_password` |
| `MYSQL_DATABASE` | Database name | `eleven_blog` |
| `REDIS_PASSWORD` | Redis password | `your_redis_pwd` |
| `JWT_SECRET` | JWT signing secret | `a-long-random-string` |
| `GITHUB_OAUTH_CLIENT_ID` | GitHub OAuth app ID | `Ov23...` |
| `GITHUB_OAUTH_CLIENT_SECRET` | GitHub OAuth secret | `xxx...` |
| `GITHUB_SYNC_OWNER` | Sync repo owner | `your-username` |
| `GITHUB_SYNC_REPO` | Sync repo name | `my-blog-posts` |
| `GITHUB_SYNC_TOKEN` | GitHub Personal Access Token | `ghp_xxx...` |
| `WEBHOOK_SECRET` | Webhook signing secret | `your_secret` |
| `BLOG_OWNER_EMAIL` | Blog owner email | `you@example.com` |
| `BLOG_OWNER_NICKNAME` | Blog owner display name | `kunxing` |
| `BLOG_OWNER_GITHUB_ID` | Blog owner GitHub ID | `12345` |

See [.env.backend.example](.env.backend.example) for the full list.

---

## Project Structure

```
Eleven-blog/
├── blog-backend/                    # Spring Boot backend (Maven multi-module)
│   ├── blog-common/                 #   JWT, Security config, utilities, unified response
│   ├── blog-pojo/                   #   Entity, DTO, VO
│   ├── blog-server/                 #   Controllers, Services, Mappers, scheduled tasks
│   │   └── src/main/java/blog/
│   │       ├── controller/
│   │       │   ├── user/            #   Public endpoints (articles, categories, comments, OAuth, webhook)
│   │       │   └── admin/           #   Admin endpoints (comment pinning)
│   │       ├── service/impl/        #   Business logic (GitHub sync, anti-spam, view counting)
│   │       └── task/                #   Scheduled tasks (view count sync, article sync)
│   ├── Dockerfile                   #   Multi-stage build
│   └── pom.xml
├── blog-view/                       # Public blog frontend (Vue 3 + Vite)
│   ├── src/
│   │   ├── api/                     #   API request modules
│   │   ├── components/              #   Components (Header, CommentsCard, TopicTree, etc.)
│   │   ├── views/                   #   Pages (Home, ArticleDetail, Archive, Tags, About)
│   │   ├── router/                  #   Route configuration
│   │   ├── stores/                  #   Pinia stores (theme, blogConfig, ui)
│   │   ├── utils/                   #   Axios instance (auto-retry), Markdown utilities
│   │   └── assets/                  #   SCSS themes, animations, images
│   ├── Dockerfile
│   ├── nginx-view.conf
│   └── vite.config.js
├── sql/
│   └── kunxing_blog_cloud_minimal.sql  # Database initialization script
├── upload_data/                     # File upload directory (Docker volume mount)
├── docker-compose.yml               # Full-stack orchestration
├── nginx.conf                       # Nginx reverse proxy configuration
└── .env.backend.example             # Environment variable template
```

---

## API Overview

### Public Endpoints (No Auth Required)

| Method | Path | Description |
|:---|:---|:---|
| `GET` | `/api/articles` | List articles (paginated, filterable by category/keyword) |
| `GET` | `/api/articles/{id}` | Article detail (auto-increments view count) |
| `GET` | `/api/articles/random` | Random article recommendations |
| `GET` | `/api/categories` | List categories |
| `GET` | `/api/categories/{id}/articles` | Articles by category |
| `GET` | `/api/archive` | Archive timeline |
| `GET` | `/api/tags` | List tags |
| `GET` | `/api/comments` | List comments (filterable by article/page) |
| `POST` | `/api/comments/comment` | Submit a comment (with anti-spam checks) |
| `POST` | `/api/comments/like/{id}` | Like a comment |
| `GET` | `/api/oauth/github/url` | Get GitHub OAuth authorization URL |
| `GET` | `/api/oauth/github/callback` | GitHub OAuth callback |
| `GET` | `/api/blog/config` | Get site configuration |
| `GET` | `/api/blog/sync/silent` | Trigger silent article sync (5-min cooldown) |
| `POST` | `/webhook/github` | GitHub Webhook endpoint (HMAC signature verified) |

### Admin Endpoints (JWT + ROLE_ADMIN Required)

| Method | Path | Description |
|:---|:---|:---|
| `POST` | `/admin/auth/login` | Login to obtain tokens |
| `POST` | `/admin/auth/refresh` | Refresh access token |
| `PUT` | `/admin/comments/{id}/pin` | Pin/unpin a comment |
| `POST` | `/api/articles` | Create article |
| `PUT` | `/api/articles/{id}` | Update article |
| `DELETE` | `/api/articles/{id}` | Delete article |

---

## Database Schema

| Table | Description |
|:---|:---|
| `user` | Admin user with BCrypt-encoded password |
| `article` | Blog posts (Markdown content, sync status tracking) |
| `category` | Categories (slug, sort order, cover image) |
| `comment` | Nested comments (floor numbering, IP geolocation, pinning, moderation) |
| `system_config` | Site configuration (key-value, supports STRING/NUMBER/BOOLEAN types) |

---

## GitHub Sync Workflow

This is the core feature. Content is managed through a GitHub repository rather than a traditional admin panel:

```
GitHub Repo (Markdown files)
    │
    │  git push
    ▼
Webhook → HMAC-SHA256 verification → triggers sync
    │
    ▼
ArticleSyncService
    ├── GithubRepoScanner (Git Trees API to scan the repo)
    ├── MarkdownFrontMatter (parses YAML Front Matter)
    ├── Asset mirroring (images/PDFs downloaded locally, links rewritten)
    └── Matching strategy (URL → Git Key → SHA → Title)
```

### Auto-Discover Matching Strategy

When a new `.md` file appears in the repo, the system matches against existing articles in this priority order:

1. Normalized GitHub URL exact match
2. Git file key (`owner/repo/branch/path`) match
3. GitHub Blob SHA match
4. Article title exact match

If no match is found, a new article is created automatically.

### Front Matter Support

```yaml
---
title: TCP Three-Way Handshake Explained
category: Computer Networking
tags: [TCP, networking, protocols]
chapter_order: 3
reading_minutes: 15
is_core: true
summary: Deep dive into how the TCP three-way handshake works
---
```

Categories are automatically inferred from file paths (e.g. `network/tcp/intro.md` → category "network"), with numeric prefix sorting support.

---

## Engineering Highlights

### Tiered HTTP Caching

`CacheControlInterceptor` applies differentiated cache policies based on resource type:

| Resource | Browser Cache | CDN Cache |
|:---|:---|:---|
| Categories / Tags / Config | 10 min | 1 hour |
| Article List / Archive | 3 min | 10 min |
| Article Detail / Comments | 1 min | 3 min |

All responses include `stale-while-revalidate` for seamless cache transitions.

### View Count Architecture

```
Visitor views article → Redis INCR (IP + article ID dedup, 10-min window)
                          │
                          │ Scheduled task (every 5 min)
                          ▼
                    Batch write to MySQL
```

No database writes on each page view. Redis Hash `article:view_count` centralizes all counters.

### Comment Anti-spam (Redis Multi-layer Defense)

- Same content + same IP: deduplicated within 5 minutes
- Same IP: max 3 comments per 60 seconds
- Same IP: max 20 comments per day

### Webhook Security

- HMAC-SHA256 signature verification
- Repository whitelist check
- Anti-storm protection (minimum 30-second interval)
- Request body size limit (256 KB)
- Async sync processing for fast webhook response

### Production-grade Docker

- Backend: Multi-stage build (Maven build → JRE runtime), G1GC with OOM heap dump
- Frontend: Nginx Alpine, Gzip compression, SPA routing, static asset caching
- Health checks on all services
- MySQL with utf8mb4 charset, Redis with AOF persistence

---

## Roadmap

- [ ] Full-text search (Elasticsearch)
- [ ] RSS / Atom feed generation
- [ ] Sitemap auto-generation
- [ ] Multi-language (i18n) support
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Unit and integration test coverage
- [ ] Image CDN / OSS integration
- [ ] Article series / collections

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Backend: Java 21 conventions, Spring Boot best practices
- Frontend: Vue 3 Composition API with `<script setup>`
- Make sure the application runs correctly before submitting

---

## License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">

**If this project helps you, consider giving it a star! ⭐**

</div>
