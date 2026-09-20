from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """通过环境变量提供运行配置，避免将凭据写入代码。"""

    app_env: str = "development"
    database_url: str = "mysql+asyncmy://root:123456@localhost:3306/eleven_blog?charset=utf8mb4"
    redis_url: str = "redis://:123456@localhost:6379/1"
    blog_owner_nickname: str = "博主"
    blog_owner_github_id: int = 0
    github_oauth_client_id: str = ""
    github_oauth_client_secret: str = ""
    github_oauth_redirect_uri: str = "http://localhost:3000/oauth/callback"
    file_upload_dir: str = "upload"
    scheduled_tasks_enabled: bool = False
    spring_mail_host: str = ""
    spring_mail_port: int = 587
    spring_mail_username: str = ""
    spring_mail_password: str = ""
    blog_owner_email: str = ""

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")


@lru_cache
def get_settings() -> Settings:
    return Settings()
