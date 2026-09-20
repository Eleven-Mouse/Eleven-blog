import asyncio

from app.api.sync import run_background_sync
from app.core.database import session_factory
from app.core.redis import redis_client
from app.services.view_sync import sync_view_counts


async def _repeat(interval_seconds: int, task) -> None:
    while True:
        await asyncio.sleep(interval_seconds)
        try:
            await task()
        except Exception:
            pass


async def sync_views_job() -> None:
    async with session_factory() as session:
        await sync_view_counts(session, redis_client)


def scheduled_jobs() -> list[asyncio.Task]:
    return [
        asyncio.create_task(_repeat(300, sync_views_job)),
        asyncio.create_task(_repeat(1800, run_background_sync)),
    ]
