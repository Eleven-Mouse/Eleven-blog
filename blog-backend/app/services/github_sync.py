from dataclasses import dataclass
from urllib.parse import quote
from pathlib import PurePosixPath
import re
import posixpath
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

import httpx


@dataclass(frozen=True)
class MarkdownFile:
    name: str
    title: str
    download_url: str
    path: str
    sha: str | None


@dataclass(frozen=True)
class FrontMatter:
    title: str | None = None
    category: str | None = None
    summary: str | None = None
    chapter_order: int | None = None
    reading_minutes: int | None = None
    is_core: int | None = None
    home_featured: bool | None = None
    body_content: str | None = None


def parse_front_matter(markdown: str | None) -> FrontMatter:
    if not markdown or not markdown.startswith("---"):
        return FrontMatter(body_content=markdown)
    lines = markdown.splitlines(keepends=True)
    if not lines or lines[0].strip() != "---":
        return FrontMatter(body_content=markdown)
    closing = next((index for index, line in enumerate(lines[1:], 1) if line.strip() == "---"), None)
    if closing is None:
        return FrontMatter(body_content=markdown)
    values: dict[str, str] = {}
    for line in lines[1:closing]:
        if ":" in line:
            key, value = line.split(":", 1)
            if key.strip():
                values[key.strip().lower()] = value.strip()
    def integer(*keys: str) -> int | None:
        for key in keys:
            try:
                return int(values[key])
            except (KeyError, ValueError):
                pass
        return None
    def boolean(*keys: str) -> bool | None:
        for key in keys:
            value = values.get(key, "").lower()
            if value in {"true", "1", "yes", "y"}: return True
            if value in {"false", "0", "no", "n"}: return False
        return None
    return FrontMatter(values.get("title"), values.get("category"), values.get("summary"),
        integer("chapter_order", "chapterorder"), integer("reading_minutes", "readingminutes"),
        1 if boolean("is_core", "iscore") is True else 0 if boolean("is_core", "iscore") is False else None,
        boolean("home_featured", "homefeatured"), "".join(lines[closing + 1:]))


def raw_markdown_url(owner: str, repo: str, branch: str, path: str) -> str:
    return f"https://raw.githubusercontent.com/{owner}/{repo}/{branch}/" + "/".join(quote(part) for part in path.split("/"))


def markdown_files_from_tree(tree: dict, owner: str, repo: str, branch: str, path_prefix: str = "") -> list[MarkdownFile]:
    prefix = path_prefix.strip("/")
    files = []
    for item in tree.get("tree", []):
        path = item.get("path", "")
        if item.get("type") != "blob" or not path.endswith(".md") or (prefix and path != prefix and not path.startswith(prefix + "/")):
            continue
        name = path.rsplit("/", 1)[-1]
        files.append(MarkdownFile(name, name[:-3], raw_markdown_url(owner, repo, branch, path), path, item.get("sha")))
    return files


async def scan_markdown_files(client: httpx.AsyncClient, owner: str, repo: str, branch: str, path: str, token: str | None = None) -> list[MarkdownFile]:
    headers = {"Accept": "application/vnd.github.v3+json", "User-Agent": "ElevenBlog-Sync/1.0"}
    if token: headers["Authorization"] = f"Bearer {token}"
    response = await client.get(f"https://api.github.com/repos/{owner}/{repo}/git/trees/{branch}?recursive=1", headers=headers)
    response.raise_for_status()
    data = response.json()
    if data.get("truncated"):
        return await scan_directory_fallback(client, owner, repo, branch, path, headers)
    return markdown_files_from_tree(data, owner, repo, branch, path)


async def scan_directory_fallback(client: httpx.AsyncClient, owner: str, repo: str, branch: str, path: str, headers: dict[str, str]) -> list[MarkdownFile]:
    response = await client.get(f"https://api.github.com/repos/{owner}/{repo}/contents/{path}?ref={branch}", headers=headers)
    response.raise_for_status()
    files: list[MarkdownFile] = []
    for item in response.json():
        if item.get("type") == "dir":
            files.extend(await scan_directory_fallback(client, owner, repo, branch, item["path"], headers))
        elif item.get("type") == "file" and item.get("name", "").endswith(".md"):
            files.append(MarkdownFile(item["name"], item["name"][:-3], item.get("download_url") or raw_markdown_url(owner, repo, branch, item["path"]), item["path"], item.get("sha")))
    return files


def resolve_repo_path(markdown_path: str, target: str) -> str | None:
    clean = target.split("?", 1)[0].split("#", 1)[0]
    resolved = PurePosixPath(clean.lstrip("/")) if clean.startswith("/") else PurePosixPath(markdown_path).parent / clean
    normalized = posixpath.normpath(str(resolved))
    return None if normalized in {".", ".."} or normalized.startswith("../") else normalized


def git_file_key(owner: str, repo: str, branch: str, path: str) -> str | None:
    normalized = str(PurePosixPath(path.replace("\\", "/")))
    if not owner or not repo or not branch or normalized in {".", ".."} or normalized.startswith("../"):
        return None
    return f"{owner.lower()}/{repo.lower()}/{branch}/{normalized}"


def category_from_path(path: str) -> tuple[str, int | None] | None:
    first = path.split("/", 1)[0].strip()
    if not first or "/" not in path:
        return None
    matched = re.fullmatch(r"(\d+)\s*[-_. ]\s*(.+)", first)
    return (matched.group(2).strip(), int(matched.group(1))) if matched else (first, None)


def match_existing_article(existing: list[dict], url: str, key: str | None, sha: str | None, title: str) -> dict | None:
    for article in existing:
        if article.get("githubUrl") == url:
            return article
    for article in existing:
        if key and article.get("gitFileKey") == key:
            return article
    for article in existing:
        if sha and article.get("githubSha") == sha:
            return article
    return next((article for article in existing if article.get("title") == title), None)


async def resolve_category_id(session: AsyncSession, front: FrontMatter, path: str) -> int | None:
    category = front.category or (category_from_path(path) or (None, None))[0]
    if not category:
        return None
    name, order = category_from_path(category + "/x") or (category, None)
    row = (await session.execute(text("SELECT id, sort_order FROM category WHERE name = :name LIMIT 1"), {"name": name})).mappings().first()
    if row:
        if order is not None and row["sort_order"] != order:
            await session.execute(text("UPDATE category SET sort_order = :order WHERE id = :id"), {"order": order, "id": row["id"]})
        return row["id"]
    result = await session.execute(text("INSERT INTO category(name, sort_order) VALUES (:name, :order)"), {"name": name, "order": order if order is not None else 1000})
    return result.lastrowid


async def persist_discovered_article(session: AsyncSession, existing: dict | None, file: MarkdownFile, front: FrontMatter, content: str) -> str:
    title = front.title or file.title
    category_id = await resolve_category_id(session, front, file.path)
    values = {"title": title, "url": file.download_url, "sha": file.sha, "category_id": category_id,
              "chapter": front.chapter_order or 0, "minutes": front.reading_minutes or 8, "core": front.is_core or 0}
    if existing:
        await session.execute(text("""UPDATE article SET title=:title, github_url=:url, github_sha=:sha, category_id=:category_id,
            chapter_order=:chapter, reading_minutes=:minutes, is_core=:core, sync_status=1, last_sync_time=NOW() WHERE id=:id"""), values | {"id": existing["id"]})
        await session.commit()
        return "matched"
    await session.execute(text("""INSERT INTO article(title, content, github_url, github_sha, category_id, chapter_order, reading_minutes, is_core,
        sync_status, is_comment, view_count, publish_time) VALUES (:title,:content,:url,:sha,:category_id,:chapter,:minutes,:core,1,1,0,NOW())"""), values | {"content": content})
    await session.commit()
    return "created"


async def fetch_markdown(client: httpx.AsyncClient, url: str, token: str | None) -> str | None:
    headers = {"Accept": "text/plain", "User-Agent": "ElevenBlog-Sync/1.0"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    for _ in range(3):
        try:
            response = await client.get(url, headers=headers)
            if response.is_success and response.text:
                return response.text
        except httpx.HTTPError:
            pass
    return None


async def auto_discover(session: AsyncSession, client: httpx.AsyncClient, config: dict[str, str]) -> dict[str, int]:
    owner = config.get("github_sync_owner", "").strip()
    repo = config.get("github_sync_repo", "").strip()
    if "/" in repo and not owner:
        owner, repo = repo.split("/", 1)
    result = {"matched": 0, "created": 0, "deleted": 0, "failed": 0}
    if not owner or not repo:
        return result
    branch, path, token = config.get("github_sync_branch") or "main", config.get("github_sync_path") or "", config.get("github_sync_token") or None
    files = await scan_markdown_files(client, owner, repo, branch, path, token)
    existing_rows = await session.execute(text("SELECT id, title, github_url AS githubUrl, github_sha AS githubSha FROM article"))
    existing = [dict(row) for row in existing_rows.mappings()]
    for file in files:
        content = await fetch_markdown(client, file.download_url, token)
        if content is None:
            result["failed"] += 1
            continue
        front = parse_front_matter(content)
        matched = match_existing_article(existing, file.download_url, git_file_key(owner, repo, branch, file.path), file.sha, front.title or file.title)
        outcome = await persist_discovered_article(session, matched, file, front, content)
        result[outcome] += 1
    return result
