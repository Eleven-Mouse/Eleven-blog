
<div align="center">
# Eleven Blog

**以 GitHub 为内容源的极简个人博客系统**

用 Markdown 写作，用 Git 管理版本，推送到 GitHub 即可自动发布。

[![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot 3.5.6](https://img.shields.io/badge/Spring%20Boot-3.5.6-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue 3.5](https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![MySQL 8.0](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis 7](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[English](README_EN.md) | 中文

</div>

---

## 截图

<div align="center">
 <img width="2550" height="1227" alt="首页" src="https://github.com/user-attachments/assets/5edfdaaf-56fc-4a67-99b5-d02c162392a2" />
<img width="2549" height="1227" alt="文章详细" src="https://github.com/user-attachments/assets/0a908201-f225-4090-b0d4-cf986cacdd36" />

</div>

---

## 为什么做这个项目

传统博客系统（WordPress、Hexo 等）要么依赖数据库手动编辑，要么需要本地构建部署。Eleven Blog 选择了不同的方式：

**用 GitHub 仓库作为内容源。** 你只需在自己的 GitHub 仓库中维护 Markdown 文件，Eleven Blog 会自动发现、同步并发布它们。支持 YAML Front Matter、Obsidian 风格嵌入、图片自动镜像到本地。设置 Webhook 后，`git push` 即可触发增量同步。

---

## 核心特性

| 特性 | 说明 |
|:---|:---|
| GitHub 内容同步 | 自动发现仓库中的 `.md` 文件，匹配已有文章或创建新文章，支持 Front Matter 解析 |
| Webhook 实时同步 | 配置 GitHub Webhook 后，`git push` 自动触发增量内容同步（HMAC-SHA256 验签） |
| Obsidian 兼容 | 支持 `![[image.png]]` 嵌入语法和 `[[wiki-link]]`，资产自动镜像到本地存储 |
| 分类自动推断 | 从文件路径自动推断分类，支持数字前缀排序（如 `01-Java/`、`02-Network/`） |
| Markdown 文章 | 完整的 Markdown 渲染，支持代码高亮、目录导航（TOC）、图片灯箱 |
| 嵌套评论系统 | 支持多级回复、楼层编号、评论点赞、评论置顶，GitHub OAuth 识别博主身份 |
| 评论反垃圾 | 基于 Redis 的多层频率限制：内容去重、IP 短窗口限流、每日上限 |
| 浏览量统计 | Redis 计数 + IP 去重（10 分钟窗口），定时批量同步到 MySQL |
| 深色/浅色主题 | 全局主题切换，偏好持久化到 localStorage |
| 交互式关于页 | 基于 typed.js 的对话式"关于我"页面 |
| Docker 一键部署 | Docker Compose 编排 MySQL + Redis + 后端 + 前端，含健康检查 |
| 邮件通知 | 评论回复异步邮件通知（JavaMailSender + `@Async`） |

---

## 技术架构

```
┌──────────────┐     ┌──────────────┐
│   访客浏览器   │     │  GitHub 仓库  │
│    ( :80)    │     │  (Markdown)  │
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
│ (主数据库)  │ │ (计数+缓存)│
└───────────┘ └──────────┘
```

### 后端 Maven 多模块

```
blog-backend/
├── blog-common/     # JWT 过滤器、Security 配置、统一响应封装、工具类
├── blog-pojo/       # Entity、DTO、VO 数据对象
└── blog-server/     # Controller、Service、Mapper、定时任务（启动入口）
```

---

## 技术栈

<table>
<tr>
<th width="33%">后端</th>
<th width="33%">前端</th>
<th width="33%">基础设施</th>
</tr>
<tr>
<td>

- Java 21
- Spring Boot 3.5.6
- Spring Security + JWT (jjwt)
- MyBatis 3.0.5 + PageHelper
- Spring Data Redis
- Spring Boot Mail
- ip2region（离线 IP 归属地）
- Spring Boot Actuator
- Lombok

</td>
<td>

- Vue 3.5 (Composition API)
- Vite 7.3
- Vue Router 4
- Pinia 3
- Element Plus 2.13
- Axios（自动重试）
- marked + highlight.js
- md-editor-v3
- typed.js（对话式 UI）

</td>
<td>

- Docker Compose
- Nginx（反向代理 + Gzip）
- MySQL 8.0 容器
- Redis 7 Alpine 容器
- 多阶段 Docker 构建
- 健康检查（Actuator）
- 数据卷持久化

</td>
</tr>
</table>

---

## 快速开始

### 环境要求

- Java 21+
- Node.js 20.19+ 或 22.12+
- MySQL 8.0
- Redis 7+
- Docker & Docker Compose（容器部署）

### 方式一：Docker Compose（推荐）

```bash
git clone https://github.com/your-username/Eleven-blog.git
cd Eleven-blog

# 复制环境变量文件并填写配置
cp .env.backend.example .env.backend
# 编辑 .env.backend 填入真实值

# 创建 .env 文件
cp .env .env.example  # 参考示例配置

# 启动所有服务
docker compose up -d
```

| 服务 | 端口 | 说明 |
|:---|:---|:---|
| `blog-view` | `:80` | 博客前台（Nginx） |
| `blog-backend` | `:8081` | REST API（Spring Boot） |
| `mysql` | 内部 | MySQL 8.0 数据库 |
| `redis` | 内部 | Redis 缓存 |

### 方式二：本地开发

**1. 初始化数据库**

```bash
mysql -u root -p
CREATE DATABASE eleven_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eleven_blog;
SOURCE sql/kunxing_blog_cloud_minimal.sql;
```

**2. 启动后端**

```bash
cd blog-backend
# 编辑 src/main/resources/application.yml 配置数据库和 Redis 连接
mvn clean package -DskipTests
java -jar blog-server/target/blog-server-1.0-SNAPSHOT.jar
```

**3. 启动前端**

```bash
cd blog-view
npm install
# 开发模式（自动代理 API 请求到 localhost:8081）
npm run dev

# 或生产构建
npm run build
```

---

## 环境变量

后端通过 `.env.backend` 文件或环境变量配置，关键变量如下：

| 变量 | 说明 | 示例 |
|:---|:---|:---|
| `MYSQL_ROOT_PASSWORD` | MySQL root 密码 | `your_password` |
| `MYSQL_DATABASE` | 数据库名 | `eleven_blog` |
| `REDIS_PASSWORD` | Redis 密码 | `your_redis_pwd` |
| `JWT_SECRET` | JWT 签名密钥 | `随机长字符串` |
| `GITHUB_OAUTH_CLIENT_ID` | GitHub OAuth 应用 ID | `Ov23...` |
| `GITHUB_OAUTH_CLIENT_SECRET` | GitHub OAuth 密钥 | `xxx...` |
| `GITHUB_SYNC_OWNER` | 同步仓库所有者 | `your-username` |
| `GITHUB_SYNC_REPO` | 同步仓库名 | `my-blog-posts` |
| `GITHUB_SYNC_TOKEN` | GitHub Personal Access Token | `ghp_xxx...` |
| `WEBHOOK_SECRET` | Webhook 签名密钥 | `your_secret` |
| `BLOG_OWNER_EMAIL` | 博主邮箱 | `you@example.com` |
| `BLOG_OWNER_NICKNAME` | 博主昵称 | `kunxing` |
| `BLOG_OWNER_GITHUB_ID` | 博主 GitHub ID | `12345` |

完整变量列表参见 [.env.backend.example](.env.backend.example)。

---

## 项目结构

```
Eleven-blog/
├── blog-backend/                    # Spring Boot 后端（Maven 多模块）
│   ├── blog-common/                 #   JWT、Security 配置、工具类、统一响应
│   ├── blog-pojo/                   #   Entity、DTO、VO
│   ├── blog-server/                 #   Controller、Service、Mapper、定时任务
│   │   └── src/main/java/blog/
│   │       ├── controller/
│   │       │   ├── user/            #   公开接口（文章、分类、评论、OAuth、Webhook）
│   │       │   └── admin/           #   管理接口（评论置顶）
│   │       ├── service/impl/        #   业务逻辑（GitHub 同步、评论反垃圾、浏览量）
│   │       └── task/                #   定时任务（浏览量同步、文章同步）
│   ├── Dockerfile                   #   多阶段构建
│   └── pom.xml
├── blog-view/                       # 博客前台（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/                     #   API 请求模块
│   │   ├── components/              #   组件（Header、CommentsCard、TopicTree 等）
│   │   ├── views/                   #   页面（Home、ArticleDetail、Archive、Tags、About）
│   │   ├── router/                  #   路由配置
│   │   ├── stores/                  #   Pinia Store（theme、blogConfig、ui）
│   │   ├── utils/                   #   Axios 实例（自动重试）、Markdown 工具
│   │   └── assets/                  #   SCSS 主题样式、动画、图片
│   ├── Dockerfile
│   ├── nginx-view.conf
│   └── vite.config.js
├── sql/
│   └── kunxing_blog_cloud_minimal.sql  # 数据库初始化脚本
├── upload_data/                     # 文件上传目录（Docker 卷挂载）
├── docker-compose.yml               # 全栈编排
├── nginx.conf                       # Nginx 反向代理配置
└── .env.backend.example             # 环境变量示例
```

---

## API 概览

### 公开接口（无需认证）

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| `GET` | `/api/articles` | 文章列表（分页，支持分类/关键词筛选） |
| `GET` | `/api/articles/{id}` | 文章详情（自动计数浏览量） |
| `GET` | `/api/articles/random` | 随机推荐文章 |
| `GET` | `/api/categories` | 分类列表 |
| `GET` | `/api/categories/{id}/articles` | 分类下的文章 |
| `GET` | `/api/archive` | 归档时间线 |
| `GET` | `/api/tags` | 标签列表 |
| `GET` | `/api/comments` | 评论列表（支持按文章/页面筛选） |
| `POST` | `/api/comments/comment` | 发表评论（含反垃圾检测） |
| `POST` | `/api/comments/like/{id}` | 评论点赞 |
| `GET` | `/api/oauth/github/url` | 获取 GitHub OAuth 授权链接 |
| `GET` | `/api/oauth/github/callback` | GitHub OAuth 回调 |
| `GET` | `/api/blog/config` | 获取站点配置 |
| `GET` | `/api/blog/sync/silent` | 静默触发文章同步（5 分钟冷却） |
| `POST` | `/webhook/github` | GitHub Webhook 接收端（HMAC 签名验证） |

### 管理接口（需要 JWT + ROLE_ADMIN）

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| `POST` | `/admin/auth/login` | 登录获取 Token |
| `POST` | `/admin/auth/refresh` | 刷新 Access Token |
| `PUT` | `/admin/comments/{id}/pin` | 评论置顶/取消置顶 |
| `POST` | `/api/articles` | 创建文章 |
| `PUT` | `/api/articles/{id}` | 更新文章 |
| `DELETE` | `/api/articles/{id}` | 删除文章 |

---

## 数据库设计

| 表 | 说明 |
|:---|:---|
| `user` | 用户（管理员），含 BCrypt 加密密码 |
| `article` | 文章（Markdown 内容，同步状态追踪） |
| `category` | 分类（支持 slug、排序、封面图） |
| `comment` | 嵌套评论（楼层编号、IP 归属地、置顶、审核状态） |
| `system_config` | 站点配置（键值对，支持 STRING/NUMBER/BOOLEAN 类型） |

---

## GitHub 同步工作流

这是本项目的核心功能。文章内容不是在后台手动编辑，而是通过 GitHub 仓库管理：

```
GitHub 仓库（Markdown 文件）
    │
    │  git push
    ▼
Webhook → HMAC-SHA256 验签 → 触发同步
    │
    ▼
ArticleSyncService
    ├── GithubRepoScanner（Git Trees API 扫描仓库）
    ├── MarkdownFrontMatter（解析 YAML Front Matter）
    ├── 资产镜像（图片/PDF 下载到本地，重写链接）
    └── 匹配策略（URL → Git Key → SHA → 标题）
```

### 自动发现匹配策略

当仓库中出现新的 `.md` 文件时，系统按以下优先级匹配已有文章：

1. 标准化后的 GitHub URL 精确匹配
2. Git 文件键（`owner/repo/branch/path`）匹配
3. GitHub Blob SHA 匹配
4. 文章标题精确匹配

无匹配则自动创建新文章。

### Front Matter 支持

```yaml
---
title: TCP 三次握手详解
category: 计算机网络
tags: [TCP, 网络, 协议]
chapter_order: 3
reading_minutes: 15
is_core: true
summary: 深入理解 TCP 三次握手的工作原理
---
```

支持从文件路径自动推断分类（如 `network/tcp/intro.md` → 分类 "network"），支持数字前缀排序。

---

## 工程化亮点

### 分层 HTTP 缓存

`CacheControlInterceptor` 根据资源类型设置差异化的缓存策略：

| 资源类型 | 浏览器缓存 | CDN 缓存 |
|:---|:---|:---|
| 分类/标签/配置 | 10 分钟 | 1 小时 |
| 文章列表/归档 | 3 分钟 | 10 分钟 |
| 文章详情/评论 | 1 分钟 | 3 分钟 |

均附带 `stale-while-revalidate`，确保缓存刷新时用户体验不中断。

### 浏览量统计架构

```
用户访问文章 → Redis INCR（IP + 文章 ID 去重，10 分钟窗口）
                    │
                    │ 定时任务（每 5 分钟）
                    ▼
              批量写入 MySQL
```

避免每次访问都写数据库，Redis Hash `article:view_count` 集中管理所有计数。

### 评论反垃圾（Redis 多层防护）

- 同一内容 + 同一 IP：5 分钟内去重
- 同一 IP：60 秒内最多 3 条评论
- 同一 IP：每日最多 20 条评论

### Webhook 安全

- HMAC-SHA256 签名验证
- 仓库白名单检查
- 防风暴保护（最小 30 秒间隔）
- 请求体大小限制（256 KB）
- 异步处理同步任务，快速响应 GitHub

### Docker 生产级配置

- 后端：多阶段构建（Maven build → JRE runtime），G1GC + OOM HeapDump
- 前端：Nginx Alpine，Gzip 压缩，SPA 路由，静态资源缓存
- 所有服务配置健康检查
- MySQL 字符集 utf8mb4，Redis AOF 持久化

---

## 路线图

- [ ] 全文搜索（Elasticsearch）
- [ ] RSS / Atom 订阅源
- [ ] Sitemap 自动生成
- [ ] 多语言（i18n）
- [ ] CI/CD 流水线（GitHub Actions）
- [ ] 单元测试与集成测试覆盖
- [ ] 图片 CDN / OSS 集成
- [ ] 文章系列 / 合集功能

---

## 贡献指南

1. Fork 本仓库
2. 创建功能分支（`git checkout -b feature/amazing-feature`）
3. 提交更改（`git commit -m 'Add amazing feature'`）
4. 推送分支（`git push origin feature/amazing-feature`）
5. 发起 Pull Request

### 开发规范

- 后端：Java 21 规范，Spring Boot 最佳实践
- 前端：Vue 3 Composition API + `<script setup>`
- 提交前请确保代码可以正常运行

---

## 许可证

本项目基于 [MIT License](LICENSE) 开源。

---

<div align="center">

**如果这个项目对你有帮助，欢迎给个 Star ⭐**

</div>
