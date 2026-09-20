from redis.asyncio import Redis


def java_string_hash(value: str) -> int:
    """生成与 Java String.hashCode() 相同的值，以共享迁移期 Redis 键。"""

    result = 0
    for char in value:
        result = (31 * result + ord(char)) & 0xFFFFFFFF
    return result - 0x100000000 if result >= 0x80000000 else result


async def check_comment_spam(redis: Redis, ip: str, content: str) -> str | None:
    content_key = f"comment:spam:content_{ip}_{java_string_hash(content)}"
    if not await redis.set(content_key, "1", ex=300, nx=True):
        return "请勿重复发送相同评论"

    short_key = f"comment:spam:short_{ip}"
    short_count = await redis.incr(short_key)
    if short_count == 1:
        await redis.expire(short_key, 60)
    if short_count > 3:
        return "评论太频繁，请稍后再试"

    daily_key = f"comment:spam:daily_{ip}"
    daily_count = await redis.incr(daily_key)
    if daily_count == 1:
        await redis.expire(daily_key, 86400)
    if daily_count > 20:
        return "今日评论次数已达上限"
    return None
