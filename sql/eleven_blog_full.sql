-- =============================================
-- Eleven Blog 完整数据库建表脚本
-- 匹配所有 Entity 类 + Mapper XML
-- MySQL 8.0+
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------------------------
-- 1. 用户表
-- -------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`              BIGINT AUTO_INCREMENT COMMENT '用户ID' PRIMARY KEY,
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`        VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`        VARCHAR(50)  NULL COMMENT '昵称',
    `email`           VARCHAR(100) NULL COMMENT '邮箱',
    `avatar`          VARCHAR(255) NULL COMMENT '头像URL',
    `role`            VARCHAR(20)  DEFAULT 'USER' COMMENT '角色：ADMIN/USER',
    `status`          TINYINT      DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `last_login_time` DATETIME     NULL COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(50)  NULL COMMENT '最后登录IP',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -------------------------------------------
-- 2. 分类表
-- -------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          BIGINT AUTO_INCREMENT COMMENT '分类ID' PRIMARY KEY,
    `name`        VARCHAR(50) NOT NULL COMMENT '分类名称',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- -------------------------------------------
-- 3. 标签表
-- -------------------------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
    `id`          BIGINT AUTO_INCREMENT COMMENT '标签ID' PRIMARY KEY,
    `name`        VARCHAR(50) NOT NULL COMMENT '标签名称',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- -------------------------------------------
-- 4. 文章表
-- -------------------------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
    `id`            BIGINT AUTO_INCREMENT COMMENT '文章ID' PRIMARY KEY,
    `author_id`     BIGINT       NULL COMMENT '作者ID',
    `title`         VARCHAR(200) NOT NULL COMMENT '文章标题',
    `summary`       VARCHAR(500) NULL COMMENT '文章摘要',
    `content`       LONGTEXT     NULL COMMENT '文章内容（Markdown格式）',
    `cover_image`   VARCHAR(1000) NULL COMMENT '封面图片URL',
    `category_id`   BIGINT       NULL COMMENT '分类ID',
    `view_count`    INT          DEFAULT 0 COMMENT '浏览次数',
    `tags`          VARCHAR(256) NULL COMMENT '标签ID列表（逗号分隔）',
    `github_url`    VARCHAR(500) NULL COMMENT 'GitHub 文件路径',
    `sync_status`   INT          DEFAULT 0 COMMENT '同步状态：0-未同步 1-已同步 2-同步失败',
    `last_sync_time` DATETIME    NULL COMMENT '最后同步时间',
    `github_sha`    VARCHAR(40)  NULL COMMENT 'GitHub 文件SHA（增量更新比对）',
    `is_comment`    TINYINT      DEFAULT 1 COMMENT '是否允许评论：0-否 1-是',
    `status`        TINYINT      DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已删除',
    `publish_time`  DATETIME     NULL COMMENT '发布时间',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- -------------------------------------------
-- 5. 评论表
-- 字段命名与 CommentMapper.xml 完全一致
-- -------------------------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id`              BIGINT AUTO_INCREMENT COMMENT '评论ID' PRIMARY KEY,
    `nickname`        VARCHAR(100) NOT NULL COMMENT '昵称',
    `email`           VARCHAR(200) NULL COMMENT '邮箱',
    `website`         VARCHAR(500) NULL COMMENT '个人网站',
    `avatar`          VARCHAR(500) NULL COMMENT '头像URL',
    `ip`              VARCHAR(50)  NULL COMMENT 'IP地址',
    `content`         TEXT         NOT NULL COMMENT '评论内容',
    `page`            VARCHAR(200) NULL COMMENT '页面标识（留言板等）',
    `blog_id`         BIGINT       NULL COMMENT '文章ID',
    `status`          TINYINT      DEFAULT 1 COMMENT '是否公开：0-否 1-是',
    `notice`          TINYINT      DEFAULT 0 COMMENT '邮件通知：0-否 1-是',
    `parentCommentId` BIGINT       NULL COMMENT '父评论ID（空=顶级评论）',
    `like_count`      INT          DEFAULT 0 COMMENT '点赞数',
    `location`        VARCHAR(100) NULL COMMENT 'IP归属地',
    `is_owner`        TINYINT      DEFAULT 0 COMMENT '是否博主：0-否 1-是',
    `floor`           INT          NULL COMMENT '楼层号（仅顶级评论）',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_blog_id` (`blog_id`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_parent` (`parentCommentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- -------------------------------------------
-- 6. 友情链接表
-- -------------------------------------------
DROP TABLE IF EXISTS `friend_link`;
CREATE TABLE `friend_link` (
    `id`          BIGINT AUTO_INCREMENT COMMENT '友链ID' PRIMARY KEY,
    `name`        VARCHAR(100) NOT NULL COMMENT '网站名称',
    `url`         VARCHAR(255) NOT NULL COMMENT '网站URL',
    `description` VARCHAR(200) NULL COMMENT '网站描述',
    `logo`        VARCHAR(1000) NULL COMMENT '网站Logo',
    `sort_order`  INT          DEFAULT 0 COMMENT '排序（越小越靠前）',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态：0-待审核 1-已通过 2-已拒绝',
    `email`       VARCHAR(100) NULL COMMENT '站长邮箱',
    `is_top`      TINYINT      DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
    `is_public`   TINYINT      DEFAULT 1 COMMENT '是否公开：0-否 1-是',
    `view_count`  INT          DEFAULT 0 COMMENT '浏览次数',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友链表';

-- -------------------------------------------
-- 7. 动态表
-- -------------------------------------------
DROP TABLE IF EXISTS `moment`;
CREATE TABLE `moment` (
    `id`           BIGINT AUTO_INCREMENT COMMENT '动态ID' PRIMARY KEY,
    `content`      TEXT          NOT NULL COMMENT '动态内容',
    `image`        VARCHAR(1000) NULL COMMENT '图片（多张逗号分隔）',
    `status`       TINYINT       DEFAULT 1 COMMENT '状态：0-草稿 1-已发布',
    `view_count`   INT           DEFAULT 0 COMMENT '浏览次数',
    `publish_time` DATETIME      NULL COMMENT '发布时间',
    `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_status` (`status`),
    INDEX `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态表';

-- -------------------------------------------
-- 8. 操作日志表
-- 字段名与 OperationLog.java 完全对应
-- -------------------------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id`             BIGINT AUTO_INCREMENT COMMENT '日志ID' PRIMARY KEY,
    `user_id`        BIGINT        NULL COMMENT '操作用户ID',
    `username`       VARCHAR(50)   NULL COMMENT '操作用户名',
    `module`         VARCHAR(50)   NULL COMMENT '操作模块',
    `operation`      VARCHAR(20)   NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/LOGIN/LOGOUT',
    `description`    VARCHAR(500)  NULL COMMENT '操作描述',
    `method`         VARCHAR(10)   NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
    `url`            VARCHAR(255)  NULL COMMENT '请求URL',
    `ip`             VARCHAR(50)   NULL COMMENT 'IP地址',
    `params`         TEXT          NULL COMMENT '请求参数（JSON）',
    `result`         TEXT          NULL COMMENT '响应结果（JSON）',
    `execution_time` BIGINT        NULL COMMENT '执行时间（毫秒）',
    `status`         TINYINT       DEFAULT 1 COMMENT '状态：0-失败 1-成功',
    `error_msg`      TEXT          NULL COMMENT '错误信息',
    `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- -------------------------------------------
-- 9. 系统配置表
-- -------------------------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
    `id`           BIGINT AUTO_INCREMENT COMMENT '配置ID' PRIMARY KEY,
    `config_key`   VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT         NULL COMMENT '配置值',
    `config_type`  VARCHAR(20)  DEFAULT 'STRING' COMMENT '类型：STRING/NUMBER/BOOLEAN/JSON',
    `description`  VARCHAR(200) NULL COMMENT '配置描述',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- -------------------------------------------
-- 10. 访问日志表
-- -------------------------------------------
DROP TABLE IF EXISTS `visit_log`;
CREATE TABLE `visit_log` (
    `id`          BIGINT AUTO_INCREMENT COMMENT '日志ID' PRIMARY KEY,
    `ip`          VARCHAR(50)   NULL COMMENT 'IP地址',
    `location`    VARCHAR(100)  NULL COMMENT 'IP归属地',
    `url`         VARCHAR(255)  NULL COMMENT '访问URL',
    `referer`     VARCHAR(255)  NULL COMMENT '来源URL',
    `user_agent`  VARCHAR(500)  NULL COMMENT '用户代理',
    `browser`     VARCHAR(50)   NULL COMMENT '浏览器',
    `os`          VARCHAR(50)   NULL COMMENT '操作系统',
    `device`      VARCHAR(20)   NULL COMMENT '设备类型：PC/Mobile/Tablet',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问日志表';

-- -------------------------------------------
-- 11. 附件表
-- -------------------------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
    `id`            BIGINT AUTO_INCREMENT COMMENT '附件ID' PRIMARY KEY,
    `original_name` VARCHAR(255) NULL COMMENT '原始文件名',
    `stored_name`   VARCHAR(255) NULL COMMENT '存储文件名',
    `file_path`     VARCHAR(500) NULL COMMENT '文件路径',
    `file_url`      VARCHAR(500) NULL COMMENT '文件URL',
    `file_type`     VARCHAR(50)  NULL COMMENT '文件类型',
    `file_size`     BIGINT       NULL COMMENT '文件大小（字节）',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件表';

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 初始数据
-- =============================================

-- 管理员用户（密码：admin123，BCrypt加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `avatar`, `role`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '博主', 'your-email@example.com', NULL, 'ADMIN', 1);

-- 系统配置 - 基础
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('site_name', 'Eleven Blog', 'STRING', '站点名称'),
('site_description', '一个简洁的博客', 'STRING', '站点描述'),
('site_author', 'Eleven', 'STRING', '站点作者'),
('site_record_number', '', 'STRING', '备案号'),
('site_github_url', '', 'STRING', 'GitHub地址'),
('site_notice', '', 'STRING', '站点公告');

-- 系统配置 - GitHub 同步
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('github_sync_repo', '', 'STRING', 'GitHub 笔记仓库地址（如：owner/repo）'),
('github_sync_branch', 'main', 'STRING', '同步分支'),
('github_sync_token', '', 'STRING', 'GitHub Personal Access Token'),
('github_sync_auto_publish', 'false', 'BOOLEAN', '自动发现的文章是否自动发布'),
('webhook_secret', '', 'STRING', 'GitHub Webhook Secret');

-- 系统配置 - 邮件
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('mail_enabled', 'false', 'BOOLEAN', '是否启用邮件通知'),
('mail_host', '', 'STRING', 'SMTP服务器地址'),
('mail_port', '465', 'STRING', 'SMTP端口'),
('mail_username', '', 'STRING', '邮箱账号'),
('mail_password', '', 'STRING', '邮箱密码/授权码'),
('mail_from_name', 'Eleven Blog', 'STRING', '发件人名称');

-- 系统配置 - 博主信息
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('blog_owner_email', '', 'STRING', '博主邮箱'),
('blog_owner_nickname', '博主', 'STRING', '博主昵称'),
('blog_owner_avatar', '', 'STRING', '博主头像URL'),
('blog_owner_github_id', '0', 'NUMBER', '博主GitHub ID');
