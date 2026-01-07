package blog.task;

import blog.mapper.ArticleMapper;
import blog.service.ViewCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ViewCountSyncTask
{
    @Autowired
    private ViewCountService viewCountService;

    @Autowired
    private ArticleMapper articleMapper;

    @Scheduled(cron ="0 0/5 * * * ?")
    public void syncViewCountToDatabase()
    {
        log.info("开始执行浏览量同步任务...");
        // 1. 从 Redis 获取所有文章的浏览量
        Map<Object, Object> viewCounts = viewCountService.getAllViewCounts();
        if(viewCounts.isEmpty())
        {
            return;
        }

        for (Map.Entry<Object, Object> entry : viewCounts.entrySet()) {
            Long articleId = Long.parseLong((String) entry.getKey());
            Integer count = Integer.parseInt((String) entry.getValue());

            articleMapper.updateViewCount(articleId,count);
            log.info("同步文章ID:{},浏览量:{}",articleId,count);
    }
        log.info("浏览量同步完成");
    }
}
