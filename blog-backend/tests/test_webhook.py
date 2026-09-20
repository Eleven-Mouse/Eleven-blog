from unittest import TestCase

from app.api import webhook
from app.api.webhook import repo_matches, verify_signature


class WebhookTests(TestCase):
    def test_verifies_github_hmac(self):
        payload = b'{"repository":{"name":"blog","owner":{"login":"me"}}}'
        signature = "sha256=" + __import__("hmac").new(b"secret", payload, __import__("hashlib").sha256).hexdigest()
        self.assertTrue(verify_signature(payload, signature, "secret"))
        self.assertTrue(repo_matches(payload, "ME", "BLOG"))

    def test_rate_limits_webhook_syncs_for_30_seconds(self):
        webhook._last_sync_time = 0
        self.assertTrue(webhook.allow_sync(100))
        self.assertFalse(webhook.allow_sync(129))
        self.assertTrue(webhook.allow_sync(130))
