from typing import Any

from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.result import Result, success


async def get_categories(session: AsyncSession) -> Result[list[dict[str, Any]]]:
    rows = await session.execute(
        text(
            """SELECT c.id, c.name, c.slug, c.description, c.cover_image AS coverImage,
                      c.sort_order AS sortOrder, c.create_time AS createTime,
                      c.update_time AS updateTime, COALESCE(COUNT(a.id), 0) AS articleCount
               FROM category c
               LEFT JOIN article a ON c.id = a.category_id
               GROUP BY c.id, c.name, c.slug, c.description, c.cover_image, c.sort_order,
                        c.create_time, c.update_time
               ORDER BY c.sort_order ASC, c.create_time DESC"""
        )
    )
    return success([dict(row) for row in rows.mappings()])


async def get_category(session: AsyncSession, category_id: int) -> Result[dict[str, Any]]:
    rows = await session.execute(
        text(
            """SELECT id, name, slug, description, cover_image AS coverImage,
                      sort_order AS sortOrder, create_time AS createTime, update_time AS updateTime
               FROM category WHERE id = :category_id"""
        ),
        {"category_id": category_id},
    )
    category: dict[str, Any] | None = rows.mappings().first()
    if category is None:
        return Result(code=0, msg="分类不存在")
    return success(dict(category))
