package blog.service.impl;

import blog.entity.SystemConfig;
import blog.mapper.SystemConfigMapper;
import blog.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务实现类
 *
 * @author Eleven
 * @version 1.0
 */
@Service
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Override
    public Map<String, String> getAllConfigAsMap() {
        List<SystemConfig> configs = systemConfigMapper.selectAll();
        Map<String, String> map = new LinkedHashMap<>();
        if (configs != null) {
            for (SystemConfig config : configs) {
                map.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return map;
    }

    @Override
    @Transactional
    public void updateConfig(Map<String, String> configMap) {
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || key.trim().isEmpty()) continue;

            SystemConfig existing = systemConfigMapper.selectByKey(key);
            if (existing != null) {
                existing.setConfigValue(value);
                existing.setUpdateTime(now);
                systemConfigMapper.updateByKey(existing);
            } else {
                SystemConfig newConfig = new SystemConfig();
                newConfig.setConfigKey(key);
                newConfig.setConfigValue(value);
                newConfig.setConfigType("STRING");
                newConfig.setCreateTime(now);
                newConfig.setUpdateTime(now);
                systemConfigMapper.insert(newConfig);
            }
        }
        log.info("批量更新系统配置，共{}项", configMap.size());
    }
}
