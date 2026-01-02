package blog.controller.admin;

import blog.result.Result;
import blog.service.ArticleService;
import blog.service.CommentService;
import blog.service.FriendLinkService;
import blog.service.MomentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 *
 * @author Eleven
 * @version 1.0
 */
@RestController
@RequestMapping("/admin/dashboard")
@Slf4j
@Api(tags = "仪表盘管理")
public class DashboardController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FriendLinkService friendLinkService;

    @Autowired
    private MomentService momentService;

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    @ApiOperation("获取统计数据")
    public Result<Map<String, Object>> getStatistics() {
        log.info("获取仪表盘统计数据");

        try {
            Map<String, Object> statistics = new HashMap<>();

            // 文章总数
            Long articleCount = articleService.countTotal();
            statistics.put("articleCount", articleCount != null ? articleCount : 0);

            // 评论总数
            Long commentCount = commentService.countTotal();
            statistics.put("commentCount", commentCount != null ? commentCount : 0);

            // 动态总数（用作"附件"统计，因为可能没有专门的附件表）
            Long momentCount = momentService.countTotal();
            statistics.put("momentCount", momentCount != null ? momentCount : 0);

            // 友链总数
            Long friendLinkCount = friendLinkService.countTotal();
            statistics.put("friendLinkCount", friendLinkCount != null ? friendLinkCount : 0);

            log.info("统计数据：文章={}, 评论={}, 动态={}, 友链={}",
                    articleCount, commentCount, momentCount, friendLinkCount);

            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
}














