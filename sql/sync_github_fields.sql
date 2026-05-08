-- =============================================
-- GitHub 同步功能增强 - 数据库迁移脚本
-- 执行条件：MySQL 8.0+, eleven_blog 数据库
-- =============================================

-- 1. article 表增加 github_sha 字段（用于增量更新比对）
ALTER TABLE article ADD COLUMN IF NOT EXISTS github_sha VARCHAR(40) DEFAULT NULL
    COMMENT 'GitHub 文件 SHA，用于增量更新比对';

-- 2. 新增系统配置项（如果不存在则插入）
INSERT INTO system_config (config_key, config_value, config_type, description, create_time, update_time)
SELECT 'github_sync_token', '', 'STRING', 'GitHub Personal Access Token，用于提升API限流至5000次/小时', NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'github_sync_token');

INSERT INTO system_config (config_key, config_value, config_type, description, create_time, update_time)
SELECT 'github_sync_auto_publish', 'false', 'BOOLEAN', '自动发现的文章是否自动发布（true=发布，false=草稿）', NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'github_sync_auto_publish');

INSERT INTO system_config (config_key, config_value, config_type, description, create_time, update_time)
SELECT 'webhook_secret', '', 'STRING', 'GitHub Webhook Secret，用于验证推送事件来源', NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'webhook_secret');
