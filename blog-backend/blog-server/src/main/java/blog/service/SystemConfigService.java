package blog.service;

import java.util.Map;

/**
 * 系统配置服务接口
 *
 * @author Eleven
 * @version 1.0
 */
public interface SystemConfigService {

    /**
     * 获取所有配置，返回 key-value 映射
     */
    Map<String, String> getAllConfigAsMap();

    /**
     * 批量更新配置（有则更新，无则新增）
     */
    void updateConfig(Map<String, String> configMap);
}
