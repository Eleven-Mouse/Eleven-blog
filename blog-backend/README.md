# Eleven Blog FastAPI 后端

这是 Eleven Blog 的 Python 后端迁移项目。它与 `../blog-backend/` 的 Spring Boot 服务并行运行：Java 默认监听 `8081`，Python 默认监听 `8082`。当前 Nginx 仍指向 Java，因此 Python 可以单独验证和随时回退。

## 结构总览

```text
blog-backend-python/
├── app/                         # FastAPI 应用源码
│   ├── main.py                  # 应用入口、路由注册、生命周期与健康检查
│   ├── api/                     # HTTP 接口层：参数接收、调用服务、返回统一响应
│   ├── core/                    # 配置、MySQL 连接池、Redis 客户端
│   ├── schemas/                 # 请求/响应数据模型
│   └── services/                # 业务逻辑：同步、防刷、邮件、定时任务等
├── tests/                       # 不依赖真实外部服务的单元测试
├── .env.example                 # 本地运行环境变量示例
├── Dockerfile                   # Python 服务镜像构建文件
├── pyproject.toml               # 依赖声明与项目元数据
├── uv.lock                      # uv 生成的精确依赖锁定文件
└── README.md                    # 本文档
```

## 核心文件职责

| 位置 | 作用 |
|---|---|
| `app/main.py` | FastAPI 入口；定义路由、`/health`、关闭 Redis/数据库连接、按开关启动定时任务。 |
| `app/core/config.py` | 读取环境变量，例如数据库、Redis、GitHub OAuth、SMTP、上传目录。 |
| `app/core/database.py` | 创建 SQLAlchemy 异步 MySQL 引擎和会话。 |
| `app/core/redis.py` | 创建共享 Redis 异步客户端。 |
| `app/schemas/result.py` | 统一响应：`{ code, msg, data }`，与 Java `Result` 对齐。 |
| `app/schemas/comment.py` | 评论创建请求模型，兼容前端 camelCase 字段。 |
| `app/api/articles.py` | 文章列表、详情、随机推荐和分页结构。 |
| `app/api/categories.py` | 分类列表与分类详情。 |
| `app/api/archive.py` | 按年月分组文章归档。 |
| `app/api/comments.py` | 评论查询、发布、点赞、楼层、博主识别与通知调度。 |
| `app/api/oauth.py` | GitHub OAuth 授权地址和回调处理。 |
| `app/api/sync.py` | 手动静默同步入口与独立数据库会话同步。 |
| `app/api/webhook.py` | GitHub Webhook 的 HMAC 验签和仓库匹配辅助逻辑。 |
| `app/api/upload.py` | 访客头像上传：图片 MIME 校验、2 MB 限制与本地保存。 |
| `app/services/github_sync.py` | Git Trees 扫描、Front Matter 解析、文章匹配、分类推断和同步写库。 |
| `app/services/comment_spam.py` | 评论内容去重和 Redis 频率限制；哈希规则与 Java 对齐。 |
| `app/services/view_count.py` | 文章详情按 IP 去重计数。 |
| `app/services/view_sync.py` | Redis 浏览量回写 MySQL。 |
| `app/services/scheduler.py` | 定时任务循环：浏览量回写、GitHub 自动发现。 |
| `app/services/mail.py` | SMTP 新评论/回复通知；未配置 SMTP 时跳过。 |

## 已迁移接口

- 文章、分类、归档、站点配置、评论、GitHub OAuth、头像上传。
- `GET /api/blog/sync/silent`：受冷却保护的手动同步。
- `POST /webhook/github`：请求体限制、验签、push 事件过滤和仓库校验。
- `/health`：检查 MySQL 与 Redis。

完整接口文档在启动后访问 `http://localhost:8082/docs`。

## 本地运行

```powershell
cd blog-backend-python
uv sync
Copy-Item .env.example .env
uv run uvicorn app.main:app --reload --port 8082
```

运行测试：

```powershell
uv run python -m unittest discover -s tests -v
```

依赖由 `pyproject.toml` 声明、`uv.lock` 锁定。新增依赖使用 `uv add <包名>`；不要再创建 `requirements.txt`。

## Docker 并行运行

根目录 `docker-compose.yml` 中的 `blog-backend-python` 服务会映射到 `8082`，并共享 MySQL、Redis 和 `upload_data`。在根目录配置 `.env.backend` 后运行：

```powershell
docker compose up -d blog-backend-python
curl http://localhost:8082/health
```

该服务不会自动切换 Nginx 流量。验证接口和数据结果均与 Java 一致后，再调整前端代理。

## 当前边界

Python 服务尚未完成管理员 JWT 管理端迁移，也未适配 Java 使用的 `ip2region.xdb` 离线 IP 库。GitHub 同步、SMTP、定时任务已具备实现，但应先在测试环境用真实 MySQL、Redis、GitHub 仓库和 SMTP 配置完成集成验证后再启用或切流。
