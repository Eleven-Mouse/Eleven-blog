package blog.controller.user;

import blog.result.Result;
import blog.service.ArticleSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 静默同步触发接口
 * 用于前端非阻塞触发自动发现同步，不向用户展示过程。
 */
@RestController
@RequestMapping("/api/blog/sync")
@Slf4j
public class SilentSyncController {

    private static final long MIN_TRIGGER_INTERVAL_MS = 5 * 60 * 1000L; // 5分钟

    private final ArticleSyncService articleSyncService;
    private final TaskExecutor taskExecutor;
    private final AtomicLong lastTriggerTime = new AtomicLong(0);
    private final AtomicBoolean syncing = new AtomicBoolean(false);

    public SilentSyncController(ArticleSyncService articleSyncService,
                                @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        this.articleSyncService = articleSyncService;
        this.taskExecutor = taskExecutor;
    }

    /**
     * 触发一次静默自动发现（异步）
     */
    @GetMapping("/silent")
    public Result<Void> triggerSilentSync() {
        long now = System.currentTimeMillis();
        long last = lastTriggerTime.get();
        if (now - last < MIN_TRIGGER_INTERVAL_MS) {
            return Result.success();
        }
        if (!lastTriggerTime.compareAndSet(last, now)) {
            return Result.success();
        }
        if (!syncing.compareAndSet(false, true)) {
            return Result.success();
        }

        taskExecutor.execute(() -> {
            try {
                articleSyncService.autoDiscover();
            } catch (Exception e) {
                log.warn("静默同步触发异常: {}", e.getMessage());
            } finally {
                syncing.set(false);
            }
        });
        return Result.success();
    }
}

