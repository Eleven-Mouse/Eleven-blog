from unittest import IsolatedAsyncioTestCase

from app.api.comments import validate_comment
from app.schemas.comment import CommentCreate
from app.services.comment_spam import java_string_hash


class CommentTests(IsolatedAsyncioTestCase):
    def test_accepts_frontend_camel_case_fields(self):
        comment = CommentCreate.model_validate(
            {"nickname": "奶龙", "content": "测试评论", "blogId": 1, "parentCommentId": None}
        )
        self.assertEqual(comment.blog_id, 1)
        self.assertIsNone(validate_comment(comment))

    def test_rejects_missing_comment_target(self):
        comment = CommentCreate(nickname="奶龙", content="测试评论")
        self.assertEqual(validate_comment(comment), "页面标识(page)和文章ID(blogId)不能同时为空")

    def test_uses_java_compatible_content_hash(self):
        self.assertEqual(java_string_hash("abc"), 96354)
