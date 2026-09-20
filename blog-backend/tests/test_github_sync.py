from unittest import TestCase

from app.services.github_sync import category_from_path, git_file_key, markdown_files_from_tree, match_existing_article, parse_front_matter, resolve_repo_path


class GithubSyncTests(TestCase):
    def test_filters_tree_by_markdown_and_path_prefix(self):
        files = markdown_files_from_tree({"tree": [{"path":"notes/a.md","type":"blob","sha":"1"},{"path":"notes/a.png","type":"blob"},{"path":"other/b.md","type":"blob"}]}, "me", "repo", "main", "notes")
        self.assertEqual(files[0].path, "notes/a.md")
        self.assertIn("notes/a.md", files[0].download_url)

    def test_parses_java_compatible_front_matter(self):
        front = parse_front_matter("---\ntitle: 测试\nchapterOrder: 2\nis_core: true\n---\n# 正文")
        self.assertEqual((front.title, front.chapter_order, front.is_core, front.body_content), ("测试", 2, 1, "# 正文"))

    def test_uses_java_article_match_priority(self):
        existing = [{"id": 1, "githubUrl": "old", "gitFileKey": "key", "githubSha": "sha", "title": "old"}, {"id": 2, "title": "标题"}]
        self.assertEqual(match_existing_article(existing, "url", "key", "sha", "标题")["id"], 1)
        self.assertEqual(git_file_key("Owner", "Repo", "main", "notes\\a.md"), "owner/repo/main/notes/a.md")
        self.assertEqual(category_from_path("01-Java/a.md"), ("Java", 1))
        self.assertEqual(resolve_repo_path("notes/a/post.md", "../image.png"), "notes/image.png")
        self.assertIsNone(resolve_repo_path("post.md", "../../secret.png"))
