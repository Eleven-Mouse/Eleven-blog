import random
from typing import Any

from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.result import Result, success

ARTICLE_COLUMNS = """
    a.id, a.title, a.content, a.cover_image AS coverImage,
    a.category_id AS categoryId, a.chapter_order AS chapterOrder,
    a.reading_minutes AS readingMinutes, a.is_core AS isCore,
    a.view_count AS viewCount, a.is_comment AS isComment,
    a.publish_time AS publishTime, a.create_time AS createTime,
    a.update_time AS updateTime, a.github_url AS githubUrl,
    a.sync_status AS syncStatus, a.last_sync_time AS lastSyncTime,
    c.name AS categoryName
"""

ARTICLE_FROM = f"""
    FROM article a
    LEFT JOIN category c ON a.category_id = c.id
"""


async def list_articles(
    session: AsyncSession,
    *,
    category_id: int | None = None,
    category: str | None = None,
    keyword: str | None = None,
) -> list[dict[str, Any]]:
    """按 Java 版相同字段与顺序读取文章。"""

    clauses: list[str] = []
    params: dict[str, Any] = {}
    if category_id is not None:
        clauses.append("a.category_id = :category_id")
        params["category_id"] = category_id
    if category and category.strip():
        clauses.append("c.name = :category")
        params["category"] = category
    if keyword and keyword.strip():
        clauses.append("a.title LIKE :keyword")
        params["keyword"] = f"%{keyword}%"

    where = f"WHERE {' AND '.join(clauses)}" if clauses else ""
    query = text(
        f"""SELECT {ARTICLE_COLUMNS}
        {ARTICLE_FROM}
        {where}
        ORDER BY a.category_id ASC, a.chapter_order ASC,
                 COALESCE(a.publish_time, a.create_time) DESC"""
    )
    rows = await session.execute(query, params)
    return [dict(row) for row in rows.mappings()]


def page_result(articles: list[dict[str, Any]], page: int, size: int) -> dict[str, Any]:
    total = len(articles)
    start = (page - 1) * size
    records = articles[start : start + size] if start < total else []
    return {
        "data": records,
        "pagination": {
            "currentPage": page,
            "totalPage": (total + size - 1) // size,
            "total": total,
            "size": size,
        },
    }


async def get_articles_page(
    session: AsyncSession,
    *,
    category_id: int | None,
    category: str | None,
    keyword: str | None,
    page: int,
    size: int,
) -> Result[dict[str, Any]]:
    articles = await list_articles(session, category_id=category_id, category=category, keyword=keyword)
    return success(page_result(articles, page, size))


async def get_article(session: AsyncSession, article_id: int) -> Result[dict[str, Any]]:
    rows = await session.execute(
        text(f"SELECT {ARTICLE_COLUMNS} {ARTICLE_FROM} WHERE a.id = :article_id"),
        {"article_id": article_id},
    )
    article = rows.mappings().first()
    if article is None:
        return Result(code=0, msg="文章不存在")
    return success(dict(article))


async def get_random_articles(session: AsyncSession, limit: int) -> Result[list[dict[str, Any]]]:
    articles = await list_articles(session)
    random.shuffle(articles)
    return success(articles[:limit])
