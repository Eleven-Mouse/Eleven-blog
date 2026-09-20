from datetime import datetime
from unittest import IsolatedAsyncioTestCase
from unittest.mock import AsyncMock, patch

from app.api.archive import get_archive


class ArchiveTests(IsolatedAsyncioTestCase):
    async def test_groups_articles_by_publish_year_and_month(self):
        with patch(
            "app.api.archive.list_articles",
            new=AsyncMock(return_value=[{"id": 1, "publishTime": datetime(2026, 8, 1)}, {"id": 2, "publishTime": None}]),
        ):
            result = await get_archive(AsyncMock())
        self.assertEqual(result.data["total"], 2)
        self.assertEqual([item["id"] for item in result.data["archive"]["2026-08"]], [1])
        self.assertEqual([item["id"] for item in result.data["archive"]["未知"]], [2])
