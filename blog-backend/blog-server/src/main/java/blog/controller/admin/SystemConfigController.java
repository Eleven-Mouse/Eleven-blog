package blog.controller.admin;

import blog.result.Result;
import blog.service.SystemConfigService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置管理接口（仅管理员）
 *
 * @author Eleven
 * @version 1.0
 */
@RestController("adminSystemConfigController")
@RequestMapping("/admin/system")
@Slf4j
@ApiOperation("系统配置管理")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取所有系统配置
     */
    @GetMapping("/config")
    @ApiOperation("获取系统配置")
    public Result<Map<String, String>> getConfig() {
        Map<String, String> config = systemConfigService.getAllConfigAsMap();
        return Result.success(config);
    }

    /**
     * 批量更新系统配置
     */
    @PutMapping("/config")
    @ApiOperation("更新系统配置")
    public Result<Void> updateConfig(@RequestBody Map<String, String> configMap) {
        log.info("更新系统配置，共{}项", configMap.size());
        try {
            systemConfigService.updateConfig(configMap);
            return Result.success("配置更新成功");
        } catch (Exception e) {
            log.error("更新系统配置失败: {}", e.getMessage(), e);
            return Result.error("配置更新失败: " + e.getMessage());
        }
    }
}
