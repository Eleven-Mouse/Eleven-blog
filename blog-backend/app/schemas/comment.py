from pydantic import BaseModel, ConfigDict


class CommentCreate(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    nickname: str | None = None
    email: str | None = None
    website: str | None = None
    avatar: str | None = None
    content: str | None = None
    page: str | None = None
    notice: bool | None = None
    parent_comment_id: int | None = None
    blog_id: int | None = None
    github_id: int | None = None

    model_config = ConfigDict(
        populate_by_name=True,
        alias_generator=lambda value: "".join(
            word.title() if index else word
            for index, word in enumerate(value.split("_"))
        ),
    )
