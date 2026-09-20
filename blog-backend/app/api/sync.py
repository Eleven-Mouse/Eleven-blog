import asyncio
import time

import httpx
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.result import Result, success
from app.services.github_sync import auto_discover
from app.core.database import session_factory

_last_trigger = 0.0
_sync_lock = asyncio.Lock()


async def trigger_silent_sync(session: AsyncSession) -> Result[dict[str, int]]:
    global _last_trigger
    now = time.monotonic()
    if now - _last_trigger < 300 or _sync_lock.locked():
        return success({})
    _last_trigger = now
    async with _sync_lock:
        try:
            return success(await execute_sync(session))
        except Exception:
            return success({})


async def run_background_sync() -> None:
    """供 Webhook 使用的独立会话同步任务。"""
    async with session_factory() as session:
        try:
            await execute_sync(session)
        except Exception:
            pass


async def execute_sync(session: AsyncSession) -> dict[str, int]:
    rows = await session.execute(text("SELECT config_key, config_value FROM system_config"))
    config = {row.config_key: row.config_value or "" for row in rows}
    async with httpx.AsyncClient(timeout=30) as client:
        return await auto_discover(session, client, config)
