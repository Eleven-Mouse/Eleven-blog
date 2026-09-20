from unittest import IsolatedAsyncioTestCase

from app.api.blog_config import get_blog_config


class FakeRows:
    def __iter__(self):
        return iter(
            [
                type("Row", (), {"config_key": "site_name", "config_value": "Eleven Blog"})(),
                type("Row", (), {"config_key": "site_notice", "config_value": ""})(),
            ]
        )


class FakeSession:
    async def execute(self, _):
        return FakeRows()


class BlogConfigTests(IsolatedAsyncioTestCase):
    async def test_returns_java_compatible_response(self):
        result = await get_blog_config(FakeSession())

        self.assertEqual(result.code, 1)
        self.assertEqual(result.msg, "操作成功")
        self.assertEqual(result.data, {"site_name": "Eleven Blog", "site_notice": ""})
