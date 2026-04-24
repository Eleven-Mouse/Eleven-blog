<div align="center">

# Eleven Blog

**一个以简约为主的个人主题博客系统**

English | **中文**

</div>

---

## 项目简介

Eleven Blog 是一个全栈个人博客平台，包含**博客前台**、**后台管理 CMS** 和 **RESTful API 后端**。支持 Markdown 文章发布、评论管理、动态（短文）、友链管理、分类与标签，以及带交互式图表的统计仪表盘。

## 截图展示

<div align="center">

### 博客前台

<img width="1920" height="869" alt="image" src="https://github.com/user-attachments/assets/2713d415-2be5-4cad-9e85-fce19dcca8e4" />
<img width="1920" height="869" alt="image" src="https://github.com/user-attachments/assets/be9f10af-e10c-47e2-99b5-02f1e87116b4" />
<img width="418" height="885" alt="image" src="https://github.com/user-attachments/assets/2d2abad2-6681-44c6-ab3f-cc9fb66a6c7a" />
<img width="413" height="778" alt="image" src="https://github.com/user-attachments/assets/a04a9936-5d82-470b-a902-8fbd729e7b73" />


### 后台管理

<img width="1000" height="500" alt="写文章" src="https://github.com/user-attachments/assets/2904549a-f101-419c-b7a4-d3d60be38fff" />

</div>

## 功能特性

| 功能 | 说明 |
| :--- | :--- |
| 文章管理 | Markdown 编辑器撰写、编辑、发布/草稿文章，支持封面图、分类与标签 |
| 评论系统 | 嵌套评论，支持邮箱、网站、头像；后台审核管理 |
| 动态 | 类似推特的短文发布，支持图片附件 |
| 分类与标签 | 多维度分类；基于标签的文章筛选 |
| 友链管理 | 博客友情链接管理，支持 Logo 和描述 |
| 文件上传 | 图片上传，支持存储卷挂载 |
| 数据仪表盘 | 统计卡片、贡献热力图、分类饼图、标签旭日图 |
| 身份认证 | 基于 JWT 的无状态认证，支持 access/refresh token 轮换 |
| 浏览量统计 | 基于 Redis 的浏览量计数（IP 去重），定时同步到 MySQL |
| 深色/浅色主题 | 主题切换，持久化偏好设置 |
| 归档时间线 | 按时间线浏览文章归档 |
| Docker 部署 | Docker Compose 一键部署（5 个服务） |

## 架构图

```mermaid
graph TB
    subgraph Client["客户端"]
        Visitor["访客浏览器 :3000"]
        Admin["管理员浏览器 :8080"]
    end

    subgraph Frontend["前端服务"]
        BlogView["blog-view<br/>Vue 3 + Vite + Nginx"]
        BlogCMS["blog-cms<br/>Vue 3 + Vite + Nginx"]
    end

    subgraph Backend["后端服务"]
        API["Spring Boot 3.5.6 :8081"]
        Redis[("Redis<br/>浏览量缓存")]
        MySQL[("MySQL 8.0<br/>主数据库")]
    end

    Visitor --> BlogView
    Admin --> BlogCMS
    BlogView -->|"GET /api/**"| API
    BlogCMS -->|"POST /admin/** (JWT)"| API
    API --> Redis
    API --> MySQL
```

```mermaid
graph LR
    subgraph Backend["blog-backend Maven 多模块"]
        Common["blog-common<br/>JWT / 工具类 / 配置"]
        POJO["blog-pojo<br/>Entity / DTO / VO"]
        Server["blog-server<br/>Controller / Service / Mapper"]
    end
    Server --> Common
    Server --> POJO
    POJO --> Common
```

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
- Spring Security + JWT
- MyBatis 3.0.5
- MySQL 8.0
- Redis
- PageHelper
- Lombok

</td>
<td>

- Vue 3.5
- Vite 7.3
- Element Plus 2.13
- Pinia 3
- Vue Router 4
- md-editor-v3
- ECharts 6 (CMS)
- Axios

</td>
<td>

- Docker Compose
- Nginx (反向代理)
- MySQL 8.0 容器
- Redis Alpine 容器
- 数据卷持久化
- 健康检查

</td>
</tr>
</table>

## 快速开始

### 环境要求

- Java 21+
- Node.js 20.19+ 或 22.12+
- MySQL 8.0
- Redis

### 方式一：Docker Compose（推荐）

```bash
git clone https://github.com/your-username/Eleven-blog.git
cd Eleven-blog
docker compose up -d
```

| 服务 | 端口 | 说明 |
| :--- | :--- | :--- |
| `blog-view` | `:3000` | 博客前台 |
| `blog-cms` | `:8080` | 后台管理 |
| `blog-backend` | `:8081` | REST API |
| `mysql_db` | 内部 | 数据库 |
| `redis` | 内部 | 缓存 |

### 方式二：本地开发

**1. 数据库初始化**

```bash
mysql -u root -p
CREATE DATABASE eleven_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eleven_blog;
SOURCE sql/init.sql;
```

**2. 启动后端**

```bash
cd blog-backend
# 修改 src/main/resources/application.yml 中的数据库和 Redis 连接信息
mvn clean package -DskipTests
java -jar blog-server/target/blog-server-1.0-SNAPSHOT.jar
```

**3. 启动博客前台**

```bash
cd blog-view
npm install
# 修改 .env 配置 VITE_APP_API_URL
npm run dev     # 开发模式 :3000
npm run build   # 生产构建
```

**4. 启动后台管理**

```bash
cd blog-cms
npm install
# 修改 .env 配置 VITE_APP_API_URL
npm run dev     # 开发模式 :8080
npm run build   # 生产构建
```

## 项目结构

```
Eleven-blog/
├── blog-backend/                    # Spring Boot 后端（Maven 多模块）
│   ├── blog-common/                 #   公共模块：JWT、工具类、Security 配置
│   ├── blog-pojo/                   #   数据层：Entity、DTO、VO
│   ├── blog-server/                 #   核心模块：Controller、Service、Mapper（启动入口）
│   ├── Dockerfile
│   └── pom.xml
├── blog-view/                       # 博客前台（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/                     #   API 请求模块
│   │   ├── components/              #   可复用组件（ArticleCard、CommentsCard 等）
│   │   ├── views/                   #   页面（Home、ArticleDetail、Archive 等）
│   │   ├── router/                  #   Vue Router 路由配置
│   │   ├── store/ & stores/         #   Pinia 状态管理（auth、theme）
│   │   ├── utils/                   #   Axios 实例
│   │   └── assets/                  #   SCSS 主题、图片
│   ├── Dockerfile
│   ├── nginx.conf
│   └── vite.config.js
├── blog-cms/                        # 后台管理系统（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/                     #   管理 API 模块
│   │   ├── components/              #   仪表盘图表、表单、表格组件
│   │   ├── views/                   #   管理页面（Dashboard、ArticleMgmt 等）
│   │   ├── router/                  #   带权限守卫的路由配置
│   │   ├── store/                   #   Pinia 认证状态管理
│   │   ├── composables/             #   useTable、useFormat 组合式函数
│   │   └── utils/                   #   Axios（自动刷新 Token）
│   ├── Dockerfile
│   ├── nginx.conf
│   └── vite.config.js
├── sql/
│   └── init.sql                     # 数据库初始化脚本
├── upload_data/                     # 文件上传目录（Docker 挂载卷）
├── docker-compose.yml               # 全栈编排配置
└── README.md
```

## API 示例

### 公开接口（`/api/**`）— 无需认证

```bash
# 获取已发布文章列表（分页）
GET /api/articles?page=1&pageSize=10

# 获取单篇文章
GET /api/articles/{id}

# 获取归档时间线
GET /api/archive

# 获取分类列表
GET /api/categories

# 获取标签列表
GET /api/tags

# 发表评论
POST /api/comments
Content-Type: application/json
{
  "nickname": "访客",
  "email": "guest@example.com",
  "content": "很棒的文章！",
  "blogId": 1,
  "parentCommentId": null
}
```

### 管理接口（`/admin/**`）— 需要 JWT

```bash
# 登录
POST /admin/auth/login
{ "username": "admin", "password": "password" }

# 响应
{
  "code": 1,
  "msg": "success",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci..."
  }
}

# 创建文章
POST /admin/articles
Authorization: Bearer eyJhbGci...
{
  "title": "我的第一篇文章",
  "content": "# 你好世界\nMarkdown 内容...",
  "categoryId": 1,
  "tags": "1,2,3",
  "status": 1
}
```

## 数据库设计

| 表名 | 说明 |
| :--- | :--- |
| `article` | 文章（Markdown，状态：草稿/已发布/已删除） |
| `category` | 分类 |
| `tags` | 标签 |
| `comment` | 嵌套评论（含审核） |
| `moment` | 动态短文（含图片） |
| `friend_link` | 友情链接 |

## 路线图

- [ ] 全文搜索（Elasticsearch 集成）
- [ ] 新评论邮件通知
- [ ] RSS / Atom 订阅源
- [ ] 多语言（i18n）支持
- [ ] 文章系列/合集功能
- [ ] 社交媒体 OAuth 登录
- [ ] Sitemap 自动生成
- [ ] 图片 CDN 集成
- [ ] 自动化 CI/CD 流水线
- [ ] 单元测试与集成测试

## 贡献指南

1. **Fork** 本仓库
2. **创建** 功能分支（`git checkout -b feature/amazing-feature`）
3. **提交** 更改（`git commit -m 'Add amazing feature'`）
4. **推送** 分支（`git push origin feature/amazing-feature`）
5. **发起** Pull Request

### 开发规范

- 遵循各子项目现有代码风格
- 后端：Java 21 规范，Spring Boot 最佳实践
- 前端：Vue 3 组合式 API，Element Plus 组件
- 提交前请测试你的更改

## 许可证

本项目基于 MIT 许可证开源。

---

<div align="center">

**如果这个项目对你有帮助，请给个 Star 吧！**

</div>
