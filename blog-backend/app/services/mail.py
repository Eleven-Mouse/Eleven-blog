import asyncio
import smtplib
from email.message import EmailMessage

from app.core.config import get_settings


def build_reply_message(to_email: str, to_nickname: str, from_nickname: str, content: str, title: str) -> EmailMessage:
    settings = get_settings()
    message = EmailMessage()
    message["From"] = settings.spring_mail_username
    message["To"] = to_email
    message["Subject"] = f"【{settings.blog_owner_nickname}的博客】您收到了一条新回复"
    message.set_content(f"{to_nickname}，您好！\n\n在文章「{title}」中，{from_nickname} 回复了您的评论：\n\n{content}\n\n—— 来自 {settings.blog_owner_nickname} 的博客")
    return message


def build_new_comment_message(nickname: str, content: str, title: str) -> EmailMessage:
    settings = get_settings()
    message = EmailMessage()
    message["From"] = settings.spring_mail_username
    message["To"] = settings.blog_owner_email or settings.spring_mail_username
    message["Subject"] = f"【{settings.blog_owner_nickname}的博客】您收到了一条新评论"
    message.set_content(f"{settings.blog_owner_nickname}，您好！\n\n在文章「{title}」中，访客 {nickname} 发表了一条新评论：\n\n{content}\n\n—— 来自 {settings.blog_owner_nickname} 的博客")
    return message


async def send_message(message: EmailMessage) -> None:
    settings = get_settings()
    if not settings.spring_mail_host or not settings.spring_mail_username:
        return
    def deliver() -> None:
        with smtplib.SMTP(settings.spring_mail_host, settings.spring_mail_port, timeout=10) as client:
            client.starttls()
            client.login(settings.spring_mail_username, settings.spring_mail_password)
            client.send_message(message)
    await asyncio.to_thread(deliver)
