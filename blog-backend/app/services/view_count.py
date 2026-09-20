from redis.asyncio import Redis

VIEW_COUNT_KEY = "article:view_count"


async def increment_and_get_view_count(redis: Redis, article_id: int, client_ip: str) -> int:
    """复刻 Java 服务的 Redis 浏览量去重规则。"""

    first_visit = await redis.set(
        f"article:view:record:{article_id}:{client_ip}", "1", ex=600, nx=True
    )
    if first_visit:
        return int(await redis.hincrby(VIEW_COUNT_KEY, str(article_id), 1))

    count = await redis.hget(VIEW_COUNT_KEY, str(article_id))
    return int(count) if count is not None else 0
