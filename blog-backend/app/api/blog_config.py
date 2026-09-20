from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.result import Result, success


async def get_blog_config(session: AsyncSession) -> Result[dict[str, str | None]]:
    """读取站点配置，保持 GET /api/blog/config 的原始响应结构。"""

    rows = await session.execute(text("SELECT config_key, config_value FROM system_config"))
    config = {row.config_key: row.config_value for row in rows}
    return success(config)
