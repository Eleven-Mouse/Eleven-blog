from collections import defaultdict
from typing import Any

from sqlalchemy.ext.asyncio import AsyncSession

from app.api.articles import list_articles
from app.schemas.result import Result, success


async def get_archive(session: AsyncSession) -> Result[dict[str, Any]]:
    articles = await list_articles(session)
    archive: dict[str, list[dict[str, Any]]] = defaultdict(list)
    for article in articles:
        publish_time = article.get("publishTime")
        key = f"{publish_time.year}-{publish_time.month:02d}" if publish_time else "未知"
        archive[key].append(article)
    return success({"total": len(articles), "archive": dict(archive)})
