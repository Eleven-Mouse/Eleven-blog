from unittest import IsolatedAsyncioTestCase

from app.services.view_count import increment_and_get_view_count


class FakeRedis:
    def __init__(self):
        self.first_visit = True
        self.count = 0

    async def set(self, *_args, **_kwargs):
        first_visit, self.first_visit = self.first_visit, False
        return first_visit

    async def hincrby(self, *_args):
        self.count += 1
        return self.count

    async def hget(self, *_args):
        return str(self.count) if self.count else None


class ViewCountTests(IsolatedAsyncioTestCase):
    async def test_same_ip_is_counted_once_within_window(self):
        redis = FakeRedis()

        self.assertEqual(await increment_and_get_view_count(redis, 7, "127.0.0.1"), 1)
        self.assertEqual(await increment_and_get_view_count(redis, 7, "127.0.0.1"), 1)
