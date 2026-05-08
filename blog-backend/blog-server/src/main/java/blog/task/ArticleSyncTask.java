package blog.task;

import blog.service.ArticleSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 文章同步定时任务
 * 1. 每10分钟：同步所有已绑定 github_url 的文章
 * 2. 每30分钟：自动发现仓库中的新 .md 文件
 * 使用 AtomicBoolean 防止定时任务与 Webhook 并发执行
 */
@Component
@Slf4j
public class ArticleSyncTask {

    private final ArticleSyncService articleSyncService;
    private final AtomicBoolean syncing = new AtomicBoolean(false);

    public ArticleSyncTask(ArticleSyncService articleSyncService) {
        this.articleSyncService = articleSyncService;
    }

    /**
     * 每10分钟：增量同步已有文章内容
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void syncGithubArticles() {
        if (!syncing.compareAndSet(false, true)) {
            log.info("同步任务跳过：已有同步任务在执行中");
            return;
        }
        try {
            log.info("===== 开始执行 GitHub 文章同步任务 =====");
            int count = articleSyncService.syncAllArticles();
            log.info("===== GitHub 文章同步任务完成，成功更新{}篇 =====", count);
        } catch (Exception e) {
            log.error("GitHub 文章同步任务异常", e);
        } finally {
            syncing.set(false);
        }
    }

    /**
     * 每30分钟：自动发现仓库新文件，匹配或创建文章
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void autoDiscoverArticles() {
        if (!syncing.compareAndSet(false, true)) {
            log.info("自动发现任务跳过：已有同步任务在执行中");
            return;
        }
        try {
            log.info("===== 开始执行 GitHub 自动发现任务 =====");
            Map<String, Integer> result = articleSyncService.autoDiscover();
            log.info("===== 自动发现完成：匹配={}, 新建={}, 失败={} =====",
                    result.get("matched"), result.get("created"), result.get("failed"));
        } catch (Exception e) {
            log.error("GitHub 自动发现任务异常", e);
        } finally {
            syncing.set(false);
        }
    }
}
