package blog.controller.admin;

import blog.result.Result;
import blog.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private TagsService tagsService;
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

            // 动态总数
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




    @GetMapping("/statistics/contribution")
    @ApiOperation("获取文章贡献度热力图数据")
    public Result<List<Map<String, Object>>> getContributionData() {
        // 这里的 articleService.getContributionData() 调用上面的 SQL
        // 返回格式建议：List<Map<String, Object>>
        // 例如：[{date: "2024-01-01", count: 1}, {date: "2024-01-05", count: 3}]
        List<Map<String, Object>> list = articleService.getContributionData();
        return Result.success(list);
    }

    @GetMapping("/statistics/category")
    @ApiOperation("获取分类文章统计")
    public Result<List<Map<String, Object>>> getCategoryStatistics() {
        return Result.success(articleService.getCategoryCount());
    }


    @GetMapping("/statistics/tag")
    @ApiOperation("获取标签文章统计（旭日图）")
    public Result<List<Map<String, Object>>> getTagStatistics() {
        return Result.success(tagsService.getTagStatistics());
    }
}














