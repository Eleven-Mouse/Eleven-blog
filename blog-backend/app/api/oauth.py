from secrets import token_urlsafe
from urllib.parse import urlencode

import httpx

from app.core.config import get_settings
from app.schemas.result import Result, success


def build_github_login_url() -> str:
    settings = get_settings()
    params = {
        "client_id": settings.github_oauth_client_id,
        "redirect_uri": settings.github_oauth_redirect_uri,
        "scope": "user:email",
        "state": token_urlsafe(24),
    }
    return f"https://github.com/login/oauth/authorize?{urlencode(params)}"


async def handle_github_callback(code: str, client: httpx.AsyncClient) -> Result[dict[str, object]]:
    settings = get_settings()
    token_response = await client.post(
        "https://github.com/login/oauth/access_token",
        data={"client_id": settings.github_oauth_client_id, "client_secret": settings.github_oauth_client_secret, "code": code},
        headers={"Accept": "application/json"},
    )
    token_response.raise_for_status()
    access_token = token_response.json().get("access_token")
    if not access_token:
        return Result(code=0, msg="GitHub 登录失败: 获取 GitHub access_token 失败")
    user_response = await client.get(
        "https://api.github.com/user",
        headers={"Authorization": f"Bearer {access_token}", "Accept": "application/json"},
    )
    user_response.raise_for_status()
    user = user_response.json()
    github_id = int(user.get("id", 0))
    return success({
        "nickname": user.get("name") or user.get("login") or "GitHub用户",
        "avatar": user.get("avatar_url", ""),
        "githubId": github_id,
        "isOwner": settings.blog_owner_github_id > 0 and settings.blog_owner_github_id == github_id,
    })
