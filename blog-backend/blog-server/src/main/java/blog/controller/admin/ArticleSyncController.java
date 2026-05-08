package blog.controller.admin;

import blog.result.Result;
import blog.service.ArticleSyncService;
import blog.service.GithubRepoScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文章同步控制器（外挂模块，不修改原 ArticleController）
 * 提供手动触发同步 + 自动发现接口
 */
@RestController
@RequestMapping("/admin/articles/sync")
@Slf4j
public class ArticleSyncController {

    private final ArticleSyncService articleSyncService;

    public ArticleSyncController(ArticleSyncService articleSyncService) {
        this.articleSyncService = articleSyncService;
    }

    /**
     * 手动同步单篇文章
     * POST /admin/articles/sync/{id}
     */
    @PostMapping("/{id}")
    public Result<String> syncArticle(@PathVariable Long id) {
        log.info("手动触发同步，文章ID={}", id);
        boolean success = articleSyncService.syncArticle(id);
        if (success) {
            return Result.success("同步成功");
        }
        return Result.error("同步失败，请检查 github_url 配置或 GitHub 网络连通性");
    }

    /**
     * 手动同步所有文章
     * POST /admin/articles/sync/all
     */
    @PostMapping("/all")
    public Result<String> syncAllArticles() {
        log.info("手动触发全量同步");
        int count = articleSyncService.syncAllArticles();
        return Result.success("同步完成，成功更新" + count + "篇文章");
    }

    /**
     * 自动发现：扫描 GitHub 仓库 → 匹配已有文章 + 创建新文章
     * POST /admin/articles/sync/discover
     */
    @PostMapping("/discover")
    public Result<Map<String, Integer>> autoDiscover() {
        log.info("手动触发自动发现");
        Map<String, Integer> result = articleSyncService.autoDiscover();
        return Result.success(result);
    }

    /**
     * 预览：扫描仓库返回文件列表（不执行写操作）
     * GET /admin/articles/sync/discover/preview
     */
    @GetMapping("/discover/preview")
    public Result<List<GithubRepoScanner.MdFileInfo>> previewDiscover() {
        List<GithubRepoScanner.MdFileInfo> files = articleSyncService.previewDiscover();
        return Result.success(files);
    }
}
