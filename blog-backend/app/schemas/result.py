from typing import Generic, TypeVar

from pydantic import BaseModel

T = TypeVar("T")


class Result(BaseModel, Generic[T]):
    """与现有 Java 服务一致的统一响应格式。"""

    code: int = 1
    msg: str = "操作成功"
    data: T | None = None


def success(data: T | None = None, msg: str = "操作成功") -> Result[T]:
    return Result(code=1, msg=msg, data=data)
