from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession


async def sync_view_counts(session: AsyncSession, redis) -> int:
    counts = await redis.hgetall("article:view_count")
    for article_id, count in counts.items():
        await session.execute(text("UPDATE article SET view_count = :count WHERE id = :id"), {"id": int(article_id), "count": int(count)})
    if counts:
        await session.commit()
    return len(counts)
