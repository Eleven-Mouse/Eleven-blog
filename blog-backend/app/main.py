from contextlib import asynccontextmanager
import asyncio
import time

import httpx
from fastapi import Depends, FastAPI, File, Query, Request, UploadFile
from fastapi.responses import JSONResponse
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.articles import get_article, get_articles_page, get_random_articles
from app.api.archive import get_archive
from app.api.blog_config import get_blog_config
from app.api.categories import get_categories, get_category
from app.api.comments import create_comment, get_comments, like_comment
from app.api.oauth import build_github_login_url, handle_github_callback
from app.api.sync import run_background_sync, trigger_silent_sync
from app.api.webhook import allow_sync, repo_matches, verify_signature
from app.api.upload import save_avatar
from app.core.database import engine, get_session
from app.core.config import get_settings
from app.core.redis import redis_client
from app.schemas.result import Result, success
from app.schemas.comment import CommentCreate
from app.services.view_count import increment_and_get_view_count
from app.services.scheduler import scheduled_jobs


@asynccontextmanager
async def lifespan(_: FastAPI):
    jobs = scheduled_jobs() if get_settings().scheduled_tasks_enabled else []
    yield
    for job in jobs:
        job.cancel()
    await redis_client.aclose()
    await engine.dispose()


app = FastAPI(
    title="Eleven Blog API",
    version="0.1.0",
    description="FastAPI 渐进式迁移服务；默认与 Java 后端并行运行。",
    lifespan=lifespan,
)


@app.get("/health")
async def health() -> JSONResponse:
    """迁移服务健康检查；仅当 MySQL 与 Redis 都可用时返回 UP。"""

    components: dict[str, dict[str, str]] = {}
    try:
        async with engine.connect() as connection:
            await connection.execute(text("SELECT 1"))
        components["db"] = {"status": "UP"}
    except Exception:
        components["db"] = {"status": "DOWN"}

    try:
        await redis_client.ping()
        components["redis"] = {"status": "UP"}
    except Exception:
        components["redis"] = {"status": "DOWN"}

    status = "UP" if all(item["status"] == "UP" for item in components.values()) else "DOWN"
    return JSONResponse(
        status_code=200 if status == "UP" else 503,
        content={"status": status, "components": components},
    )


@app.get("/api/blog/config", response_model=Result[dict[str, str | None]])
async def blog_config(session: AsyncSession = Depends(get_session)) -> Result[dict[str, str | None]]:
    return await get_blog_config(session)


@app.get("/api/articles", response_model=Result[dict])
async def articles(
    category_id: int | None = Query(default=None, alias="categoryId"),
    category: str | None = None,
    keyword: str | None = None,
    page: int = 1,
    size: int = 10,
    session: AsyncSession = Depends(get_session),
) -> Result[dict]:
    return await get_articles_page(
        session,
        category_id=category_id,
        category=category,
        keyword=keyword,
        page=page,
        size=size,
    )


@app.get("/api/articles/articles/random", response_model=Result[list[dict]])
async def random_articles(limit: int = 6, session: AsyncSession = Depends(get_session)) -> Result[list[dict]]:
    return await get_random_articles(session, limit)


@app.get("/api/articles/{article_id}", response_model=Result[dict])
async def article(
    article_id: int, request: Request, session: AsyncSession = Depends(get_session)
) -> Result[dict]:
    result = await get_article(session, article_id)
    if result.code == 0:
        return result

    forwarded_for = request.headers.get("x-forwarded-for")
    client_ip = forwarded_for.split(",")[0].strip() if forwarded_for else request.client.host
    view_count = await increment_and_get_view_count(redis_client, article_id, client_ip)
    result.data["viewCount"] = view_count
    return result


@app.get("/api/categories", response_model=Result[list[dict]])
async def categories(session: AsyncSession = Depends(get_session)) -> Result[list[dict]]:
    return await get_categories(session)


@app.get("/api/categories/{category_id}", response_model=Result[dict])
async def category(category_id: int, session: AsyncSession = Depends(get_session)) -> Result[dict]:
    return await get_category(session, category_id)


@app.get("/api/categories/{category_id}/articles", response_model=Result[dict])
async def category_articles(
    category_id: int, page: int = 1, size: int = 10, session: AsyncSession = Depends(get_session)
) -> Result[dict]:
    return await get_articles_page(
        session, category_id=category_id, category=None, keyword=None, page=page, size=size
    )


@app.get("/api/archive", response_model=Result[dict])
async def archive(session: AsyncSession = Depends(get_session)) -> Result[dict]:
    return await get_archive(session)


@app.get("/api/comments", response_model=Result[list[dict]])
async def comments(
    page: str | None = None,
    blog_id: int | None = Query(default=None, alias="blogId"),
    session: AsyncSession = Depends(get_session),
) -> Result[list[dict]]:
    return await get_comments(session, page, blog_id)


@app.post("/api/comments/like/{comment_id}", response_model=Result[None])
async def like(comment_id: int, session: AsyncSession = Depends(get_session)) -> Result[None]:
    return await like_comment(session, comment_id)


@app.post("/api/comments/comment", response_model=Result[None])
async def post_comment(
    comment: CommentCreate, request: Request, session: AsyncSession = Depends(get_session)
) -> Result[None]:
    forwarded_for = request.headers.get("x-forwarded-for")
    client_ip = forwarded_for.split(",")[0].strip() if forwarded_for else request.client.host
    return await create_comment(session, comment, client_ip, redis_client)


@app.get("/api/oauth/github/url", response_model=Result[str])
async def github_login_url() -> Result[str]:
    return success(build_github_login_url())


@app.get("/api/oauth/github/callback", response_model=Result[dict])
async def github_callback(code: str) -> Result[dict]:
    try:
        async with httpx.AsyncClient(timeout=10) as client:
            return await handle_github_callback(code, client)
    except httpx.HTTPError as error:
        return Result(code=0, msg=f"GitHub 登录失败: {error}")


@app.get("/api/blog/sync/silent", response_model=Result[dict])
async def silent_sync(session: AsyncSession = Depends(get_session)) -> Result[dict]:
    return await trigger_silent_sync(session)


@app.post("/webhook/github")
async def github_webhook(request: Request, session: AsyncSession = Depends(get_session)) -> JSONResponse:
    payload = await request.body()
    if len(payload) > 256 * 1024:
        return JSONResponse(status_code=413, content="payload too large")
    rows = await session.execute(text("SELECT config_key, config_value FROM system_config"))
    config = {row.config_key: row.config_value or "" for row in rows}
    secret = config.get("webhook_secret") or None
    if not verify_signature(payload, request.headers.get("x-hub-signature-256"), secret):
        return JSONResponse(status_code=403, content="invalid signature")
    if request.headers.get("x-github-event") != "push":
        return JSONResponse(content="ignored")
    if not repo_matches(payload, config.get("github_sync_owner"), config.get("github_sync_repo")):
        return JSONResponse(content="repo not matched")
    if not allow_sync(time.monotonic()):
        return JSONResponse(content="rate limited")
    asyncio.create_task(run_background_sync())
    return JSONResponse(content="ok")


@app.post("/api/upload/avatar", response_model=Result[str])
async def upload_avatar(file: UploadFile = File(...)) -> Result[str]:
    return await save_avatar(file)
