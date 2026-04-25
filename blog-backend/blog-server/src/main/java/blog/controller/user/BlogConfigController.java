package blog.controller.user;

import blog.result.Result;
import blog.service.SystemConfigService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 博客配置公开接口
 *
 * @author Eleven
 * @version 1.0
 */
@RestController("userBlogConfigController")
@RequestMapping("/api/blog")
@Slf4j
@ApiOperation("博客配置")
public class BlogConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取博客配置（公开接口，无需认证）
     */
    @GetMapping("/config")
    @ApiOperation("获取博客配置")
    public Result<Map<String, String>> getBlogConfig() {
        Map<String, String> config = systemConfigService.getAllConfigAsMap();
        return Result.success(config);
    }
}
