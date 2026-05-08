-- =============================================
-- GitHub 同步功能增强 - MySQL 8.0 兼容版本
-- =============================================

-- 1. article 表增加 github_sha 字段
-- 如果字段已存在会报错，忽略即可
ALTER TABLE article ADD COLUMN github_sha VARCHAR(40) DEFAULT NULL
    COMMENT 'GitHub 文件 SHA，用于增量更新比对';

-- 2. 新增系统配置项
INSERT INTO system_config (config_key, config_value, config_type, description, create_time, update_time)
VALUES ('github_sync_token', '', 'STRING', 'GitHub Personal Access Token', NOW(), NOW());

INSERT INTO system_config (config_key, config_value, config_type, description, create_time, update_time)
VALUES ('github_sync_auto_publish', 'false', 'BOOLEAN', '自动发现的文章是否自动发布', NOW(), NOW());

INSERT INTO system_config (config_key, config_value, config_type, description, create_time, update_time)
VALUES ('webhook_secret', '', 'STRING', 'GitHub Webhook Secret', NOW(), NOW());
