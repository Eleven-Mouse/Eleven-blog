package blog.service;

/**
 * GitHub Markdown 文件获取器
 * 从 GitHub Raw URL 拉取 Markdown 内容
 */
public interface GithubMarkdownFetcher {

    /**
     * 从 GitHub Raw URL 获取 Markdown 内容
     *
     * @param githubUrl GitHub Raw URL（如 https://raw.githubusercontent.com/owner/repo/branch/path.md）
     * @return Markdown 内容，失败时返回 null
     */
    String fetchMarkdown(String githubUrl);
}
