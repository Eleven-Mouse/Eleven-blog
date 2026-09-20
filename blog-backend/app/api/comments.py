from typing import Any
import asyncio

from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.comment import CommentCreate
from app.schemas.result import Result, success
from app.core.config import get_settings
from app.services.comment_spam import check_comment_spam
from app.services.mail import build_new_comment_message, build_reply_message, send_message

COMMENT_COLUMNS = """
    c.id, c.nickname, c.email, c.website, c.avatar, c.ip, c.content, c.page,
    c.blog_id AS blogId, c.status, c.notice, c.create_time AS createTime,
    c.parentCommentId, c.like_count AS likeCount, c.location, c.is_owner AS isOwner,
    c.floor, c.is_pinned AS isPinned, c.pin_time AS pinTime, a.title
"""


async def get_comments(
    session: AsyncSession, page: str | None, blog_id: int | None
) -> Result[list[dict[str, Any]]]:
    clauses = ["c.status = true"]
    params: dict[str, Any] = {}
    if page and page.strip():
        clauses.append("c.page = :page")
        params["page"] = page
    if blog_id is not None:
        clauses.append("c.blog_id = :blog_id")
        params["blog_id"] = blog_id
    rows = await session.execute(
        text(
            f"""SELECT {COMMENT_COLUMNS} FROM comment c
                LEFT JOIN article a ON c.blog_id = a.id
                WHERE {' AND '.join(clauses)}
                ORDER BY CASE WHEN c.is_pinned = 1 THEN 0 ELSE 1 END,
                         CASE WHEN c.is_pinned = 1 THEN c.pin_time ELSE c.create_time END,
                         c.create_time"""
        ),
        params,
    )
    return success([dict(row) for row in rows.mappings()])


def validate_comment(comment: CommentCreate) -> str | None:
    if not comment.content or not comment.content.strip():
        return "评论内容不能为空"
    if not comment.nickname or not comment.nickname.strip():
        return "昵称不能为空"
    if len(comment.content.strip()) > 1000:
        return "评论内容不能超过1000字符"
    if len(comment.nickname.strip()) > 50:
        return "昵称不能超过50字符"
    if len(comment.content.strip()) < 2:
        return "评论内容太短"
    if comment.parent_comment_id is None and comment.blog_id is None and not (comment.page or "").strip():
        return "页面标识(page)和文章ID(blogId)不能同时为空"
    return None


async def like_comment(session: AsyncSession, comment_id: int) -> Result[None]:
    exists = await session.execute(text("SELECT id FROM comment WHERE id = :id"), {"id": comment_id})
    if exists.first() is None:
        return Result(code=0, msg="评论不存在")
    await session.execute(
        text("UPDATE comment SET like_count = like_count + 1 WHERE id = :id"), {"id": comment_id}
    )
    await session.commit()
    return success()


def resolve_local_location(ip: str) -> str:
    return "本地" if ip in {"::1", "localhost"} or ip.startswith(("127.", "10.", "192.168.", "172.")) else "未知"


async def create_comment(
    session: AsyncSession, comment: CommentCreate, ip: str, redis: Any
) -> Result[None]:
    error = validate_comment(comment)
    if error:
        return Result(code=0, msg=error)

    content = comment.content.strip()
    spam_error = await check_comment_spam(redis, ip, content)
    if spam_error:
        return Result(code=0, msg=spam_error)

    page = comment.page.strip() if comment.page and comment.page.strip() else None
    blog_id = comment.blog_id
    floor: int | None
    if comment.parent_comment_id is not None:
        parent = await session.execute(
            text("SELECT page, blog_id, email, nickname FROM comment WHERE id = :id"), {"id": comment.parent_comment_id}
        )
        row = parent.mappings().first()
        if row is None:
            return Result(code=0, msg="回复的父评论不存在")
        page, blog_id, floor = row["page"], row["blog_id"], None
        parent_email, parent_nickname = row["email"], row["nickname"]
    else:
        page = None if blog_id is not None else page
        condition, params = ("blog_id = :blog_id", {"blog_id": blog_id}) if blog_id is not None else ("page = :page", {"page": page})
        maximum = await session.execute(text(f"SELECT MAX(floor) FROM comment WHERE {condition} AND parentCommentId IS NULL"), params)
        floor = (maximum.scalar() or 0) + 1
        parent_email, parent_nickname = None, None

    settings = get_settings()
    is_owner = settings.blog_owner_github_id > 0 and settings.blog_owner_github_id == comment.github_id
    nickname = settings.blog_owner_nickname if is_owner else comment.nickname.strip()
    avatar = comment.avatar or (None if is_owner else "default-guest-avatar.png")
    await session.execute(
        text("""INSERT INTO comment (nickname, email, website, avatar, ip, content, page, status, notice,
                parentCommentId, blog_id, like_count, location, is_owner, floor, is_pinned)
                VALUES (:nickname, :email, :website, :avatar, :ip, :content, :page, true, :notice,
                :parent_comment_id, :blog_id, 0, :location, :is_owner, :floor, false)"""),
        {"nickname": nickname, "email": (comment.email or "").strip(), "website": (comment.website or "").strip(),
         "avatar": avatar, "ip": ip, "content": content, "page": page, "notice": bool(comment.notice),
         "parent_comment_id": comment.parent_comment_id, "blog_id": blog_id, "location": resolve_local_location(ip),
         "is_owner": is_owner, "floor": floor},
    )
    await session.commit()
    title = "文章" if blog_id is not None else "留言板"
    if not is_owner:
        asyncio.create_task(send_message(build_new_comment_message(nickname, content, title)))
    if parent_email and not (is_owner and parent_email.lower() == get_settings().blog_owner_email.lower()):
        asyncio.create_task(send_message(build_reply_message(parent_email, parent_nickname, nickname, content, title)))
    return success()
