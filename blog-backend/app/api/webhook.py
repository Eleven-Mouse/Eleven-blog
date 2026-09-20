import hashlib
import hmac
import json
import time

_last_sync_time = 0.0


def allow_sync(now: float) -> bool:
    global _last_sync_time
    if now - _last_sync_time < 30:
        return False
    _last_sync_time = now
    return True


def verify_signature(payload: bytes, signature: str | None, secret: str | None) -> bool:
    if not secret:
        return not signature
    if not signature:
        return False
    expected = "sha256=" + hmac.new(secret.encode(), payload, hashlib.sha256).hexdigest()
    return hmac.compare_digest(expected, signature)


def repo_matches(payload: bytes, owner: str | None, repo: str | None) -> bool:
    if not owner or not repo:
        return True
    try:
        data = json.loads(payload)
        actual_owner = data.get("repository", {}).get("owner", {}).get("login")
        actual_repo = data.get("repository", {}).get("name")
        return owner.lower() == actual_owner.lower() and repo.lower() == actual_repo.lower()
    except (ValueError, AttributeError):
        return True
