from unittest import IsolatedAsyncioTestCase

from app.api.articles import get_article, get_articles_page, page_result


class FakeResult:
    def mappings(self):
        return self

    def __iter__(self):
        return iter(
            [
                {"id": 1, "title": "第一篇", "categoryId": 1},
                {"id": 2, "title": "第二篇", "categoryId": 1},
                {"id": 3, "title": "第三篇", "categoryId": 2},
            ]
        )

    def first(self):
        return {"id": 1, "title": "第一篇", "viewCount": 99}


class FakeSession:
    async def execute(self, _, __=None):
        return FakeResult()


class ArticleTests(IsolatedAsyncioTestCase):
    def test_page_result_uses_existing_pagination_fields(self):
        result = page_result([{"id": 1}, {"id": 2}, {"id": 3}], page=2, size=2)

        self.assertEqual(result["data"], [{"id": 3}])
        self.assertEqual(
            result["pagination"],
            {"currentPage": 2, "totalPage": 2, "total": 3, "size": 2},
        )

    async def test_article_list_returns_java_compatible_envelope(self):
        result = await get_articles_page(
            FakeSession(),
            category_id=None,
            category=None,
            keyword=None,
            page=1,
            size=2,
        )

        self.assertEqual(result.code, 1)
        self.assertEqual(result.data["pagination"]["total"], 3)
        self.assertEqual([article["id"] for article in result.data["data"]], [1, 2])

    async def test_article_detail_preserves_article_fields(self):
        result = await get_article(FakeSession(), article_id=1)

        self.assertEqual(result.data["viewCount"], 99)
