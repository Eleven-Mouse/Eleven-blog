from pathlib import Path
from uuid import uuid4

from fastapi import UploadFile

from app.core.config import get_settings
from app.schemas.result import Result, success


async def save_avatar(file: UploadFile) -> Result[str]:
    if not file.filename:
        return Result(code=0, msg="上传文件不能为空")
    if not (file.content_type or "").startswith("image/"):
        return Result(code=0, msg="仅支持上传图片文件")
    content = await file.read(2 * 1024 * 1024 + 1)
    if len(content) > 2 * 1024 * 1024:
        return Result(code=0, msg="头像文件不能超过2MB")
    suffix = Path(file.filename).suffix or ".jpg"
    filename = f"avatar_{uuid4().hex[:8]}{suffix}"
    target = Path(get_settings().file_upload_dir) / "avatar" / filename
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_bytes(content)
    return success(f"/images/avatar/{filename}")
