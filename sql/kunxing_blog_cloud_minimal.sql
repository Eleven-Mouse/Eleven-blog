-- ============================================================
-- kunxing-blog 云服务器初始化脚本（精简版）
-- 用途：
-- 1) 删除非核心表
-- 2) 保留并补齐核心表结构
-- 3) 写入/更新关键 system_config 配置
--
-- 目标核心表：
--   user, category, article, comment, system_config
--
-- 兼容：MySQL 8.0+
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 可按需改库名
CREATE DATABASE IF NOT EXISTS `kunxing_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `kunxing_blog`;

-- 1) 删除非核心表（动态）
SET @drop_sql = (
  SELECT IFNULL(
    CONCAT(
      'DROP TABLE ',
      GROUP_CONCAT(CONCAT('`', table_name, '`') SEPARATOR ', '),
      ';'
    ),
    'SELECT 1;'
  )
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_type = 'BASE TABLE'
    AND table_name NOT IN ('user', 'category', 'article', 'comment', 'system_config')
);

PREPARE stmt_drop FROM @drop_sql;
EXECUTE stmt_drop;
DEALLOCATE PREPARE stmt_drop;

-- 2) 核心表不存在则创建
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50) NULL,
  `email` VARCHAR(100) NULL,
  `avatar` VARCHAR(255) NULL,
  `role` VARCHAR(20) DEFAULT 'USER',
  `status` TINYINT DEFAULT 1,
  `last_login_time` DATETIME NULL,
  `last_login_ip` VARCHAR(50) NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL,
  `slug` VARCHAR(100) NULL,
  `description` VARCHAR(500) NULL,
  `cover_image` VARCHAR(500) NULL,
  `sort_order` INT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_category_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL,
  `content` LONGTEXT NULL,
  `cover_image` VARCHAR(1000) NULL,
  `category_id` BIGINT NULL,
  `chapter_order` INT DEFAULT 0,
  `reading_minutes` INT DEFAULT 8,
  `is_core` TINYINT DEFAULT 0,
  `view_count` INT DEFAULT 0,
  `github_url` VARCHAR(500) NULL,
  `sync_status` INT DEFAULT 0,
  `last_sync_time` DATETIME NULL,
  `github_sha` VARCHAR(40) NULL,
  `is_comment` TINYINT DEFAULT 1,
  `publish_time` DATETIME NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `idx_category_id` (`category_id`),
  KEY `idx_article_category_chapter` (`category_id`, `chapter_order`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `nickname` VARCHAR(100) NOT NULL,
  `email` VARCHAR(200) NULL,
  `website` VARCHAR(500) NULL,
  `avatar` VARCHAR(500) NULL,
  `ip` VARCHAR(50) NULL,
  `content` TEXT NOT NULL,
  `page` VARCHAR(200) NULL,
  `blog_id` BIGINT NULL,
  `status` TINYINT DEFAULT 1,
  `notice` TINYINT DEFAULT 0,
  `parentCommentId` BIGINT NULL,
  `like_count` INT DEFAULT 0,
  `location` VARCHAR(100) NULL,
  `is_owner` TINYINT DEFAULT 0,
  `floor` INT NULL,
  `is_pinned` TINYINT DEFAULT 0,
  `pin_time` DATETIME NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `idx_blog_id` (`blog_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_parent` (`parentCommentId`),
  KEY `idx_comment_pin` (`is_pinned`, `pin_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `system_config` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `config_key` VARCHAR(100) NOT NULL,
  `config_value` TEXT NULL,
  `config_type` VARCHAR(20) DEFAULT 'STRING',
  `description` VARCHAR(200) NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) 补齐关键配置（存在则更新）
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`)
VALUES
('site_name', 'kunxing-blog', 'STRING', '站点名称'),
('site_description', '一个简洁的博客', 'STRING', '站点描述'),
('site_author', 'kunxing', 'STRING', '站点作者'),
('site_record_number', '', 'STRING', '备案号'),
('site_github_url', '', 'STRING', 'GitHub地址'),
('site_notice', '', 'STRING', '站点公告'),
('github_sync_owner', '', 'STRING', 'GitHub 仓库 owner（用户名或组织名）'),
('github_sync_repo', '', 'STRING', 'GitHub 仓库名（不含 owner）'),
('github_sync_branch', 'main', 'STRING', '同步分支'),
('github_sync_path', '', 'STRING', '同步路径前缀（可选）'),
('github_sync_token', '', 'STRING', 'GitHub Personal Access Token'),
('github_sync_auto_publish', 'false', 'BOOLEAN', '自动发现文章是否自动发布'),
('webhook_secret', '', 'STRING', 'GitHub Webhook Secret'),
('mail_enabled', 'false', 'BOOLEAN', '邮件通知开关'),
('mail_host', '', 'STRING', 'SMTP服务器地址'),
('mail_port', '465', 'STRING', 'SMTP端口'),
('mail_username', '', 'STRING', '邮箱账号'),
('mail_password', '', 'STRING', '邮箱密码/授权码'),
('mail_from_name', 'kunxing-blog', 'STRING', '发件人名称'),
('blog_owner_email', '', 'STRING', '博主邮箱'),
('blog_owner_nickname', '博主', 'STRING', '博主昵称'),
('blog_owner_avatar', '', 'STRING', '博主头像URL'),
('blog_owner_github_id', '0', 'NUMBER', '博主GitHub ID'),
('site_home_intro_title', '面向工程实践的知识库', 'STRING', '首页介绍标题'),
('site_home_intro_desc', '按专题系统化整理技术知识，帮助你从入门到实战高效学习。', 'STRING', '首页介绍文案'),
('home_featured_article_id', '', 'STRING', '首页展示文章ID'),
('site_nav_items', '[{"label":"首页","path":"/home","type":"internal"},{"label":"专题","path":"/home#topics","type":"internal"},{"label":"归档","path":"/archive","type":"internal"},{"label":"关于","path":"/about","type":"internal"}]', 'JSON', '前台主导航配置')
ON DUPLICATE KEY UPDATE
  `config_value` = VALUES(`config_value`),
  `config_type` = VALUES(`config_type`),
  `description` = VALUES(`description`),
  `update_time` = NOW();

SET FOREIGN_KEY_CHECKS = 1;

-- 完成
