from unittest import TestCase
from unittest.mock import patch

from app.core.config import Settings
from app.services.mail import build_new_comment_message, build_reply_message


class MailTests(TestCase):
    def test_builds_java_compatible_reply_mail(self):
        with patch("app.services.mail.get_settings", return_value=Settings(blog_owner_nickname="奶龙", spring_mail_username="a@b.com")):
            message = build_reply_message("to@b.com", "小明", "小红", "收到", "文章")
        self.assertEqual(message["To"], "to@b.com")
        self.assertIn("新回复", message["Subject"])
        self.assertIn("小红 回复", message.get_content())

    def test_builds_new_comment_mail(self):
        with patch("app.services.mail.get_settings", return_value=Settings(blog_owner_nickname="奶龙", blog_owner_email="owner@b.com")):
            message = build_new_comment_message("访客", "你好", "文章")
        self.assertEqual(message["To"], "owner@b.com")
