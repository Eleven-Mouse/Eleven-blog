package blog.service;

import java.util.List;
import java.util.Map;

/**
 * 文章同步服务
 * 从 GitHub 拉取 Markdown 并更新数据库
 */
public interface ArticleSyncService {

    /**
     * 同步单篇文章（从 GitHub 拉取内容并更新数据库）
     *
     * @param articleId 文章ID
     * @return true=同步成功，false=同步失败
     */
    boolean syncArticle(Long articleId);

    /**
     * 同步所有配置了 github_url 的文章
     *
     * @return 成功同步的文章数量
     */
    int syncAllArticles();

    /**
     * 自动发现：扫描 GitHub 仓库，匹配已有文章或创建新文章
     *
     * @return 发现结果摘要（matched=匹配数，created=新建数，failed=失败数）
     */
    Map<String, Integer> autoDiscover();

    /**
     * 预览：扫描仓库返回发现的文件列表（不执行任何写操作）
     *
     * @return 发现的 Markdown 文件列表
     */
    List<GithubRepoScanner.MdFileInfo> previewDiscover();
}
