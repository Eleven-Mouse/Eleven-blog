from unittest import TestCase
from unittest.mock import patch
from urllib.parse import parse_qs, urlparse

from app.api.oauth import build_github_login_url
from app.core.config import Settings


class OauthTests(TestCase):
    def test_builds_github_url_with_existing_callback_contract(self):
        settings = Settings(
            github_oauth_client_id="client-id",
            github_oauth_redirect_uri="https://blog.example/oauth/callback",
        )
        with patch("app.api.oauth.get_settings", return_value=settings):
            url = build_github_login_url()
        query = parse_qs(urlparse(url).query)
        self.assertEqual(query["client_id"], ["client-id"])
        self.assertEqual(query["redirect_uri"], ["https://blog.example/oauth/callback"])
        self.assertEqual(query["scope"], ["user:email"])
        self.assertTrue(query["state"][0])
