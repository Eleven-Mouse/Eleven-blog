-- ============================================
-- Eleven Blog 数据库增量更新脚本
-- 仅添加缺失的表和字段，不影响已有数据
-- 使用前请先备份: mysqldump -u root -p eleven_blog > backup.sql
-- ============================================

USE eleven_blog;

-- =============================================
-- 1. 补缺失的表（不会影响已存在的表）
-- =============================================

CREATE TABLE IF NOT EXISTS article (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(200)                       NOT NULL COMMENT '文章标题',
    summary      VARCHAR(500)                       NULL COMMENT '文章摘要',
    content      LONGTEXT                           NULL COMMENT '文章内容',
    category_id  BIGINT                             NULL COMMENT '分类ID',
    view_count   INT      DEFAULT 0                 NULL COMMENT '浏览次数',
    is_comment   TINYINT  DEFAULT 1                 NULL COMMENT '是否允许评论',
    status       TINYINT  DEFAULT 1                 NULL COMMENT '状态',
    publish_time DATETIME                           NULL COMMENT '发布时间',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    cover_image  VARCHAR(1000)                      NULL,
    tags         VARCHAR(256)                       NULL
);

CREATE TABLE IF NOT EXISTS category (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    article_count INT                                NULL,
    UNIQUE KEY uk_name (name)
);

CREATE TABLE IF NOT EXISTS comment (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname        VARCHAR(100)                       NOT NULL COMMENT '昵称',
    email           VARCHAR(200)                       NULL COMMENT '邮箱',
    website         VARCHAR(500)                       NULL COMMENT '网站',
    avatar          VARCHAR(500)                       NULL COMMENT '头像地址',
    ip              VARCHAR(50)                        NULL COMMENT 'IP地址',
    content         TEXT                               NOT NULL COMMENT '评论内容',
    like_count      INT      DEFAULT 0                 NULL COMMENT '点赞数',
    location        VARCHAR(100)                       NULL COMMENT 'IP归属地',
    page            VARCHAR(200)                       NULL COMMENT '所在页面标识',
    blog_id         BIGINT                             NULL COMMENT '文章ID',
    status          TINYINT  DEFAULT 1                 NULL COMMENT '是否公开',
    is_owner        TINYINT  DEFAULT 0                 NULL COMMENT '是否博主',
    floor           INT                                NULL COMMENT '楼层号',
    notice          TINYINT  DEFAULT 0                 NULL COMMENT '邮件提醒',
    parentCommentId BIGINT                             NULL COMMENT '父评论ID',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS friend_link (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    url         VARCHAR(255) NOT NULL,
    logo        VARCHAR(1000) NULL,
    description VARCHAR(200) NULL,
    status      TINYINT DEFAULT 1 NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    view_count  INT NULL
);

CREATE TABLE IF NOT EXISTS moment (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    content      TEXT                               NOT NULL,
    image        VARCHAR(1000)                      NULL,
    is_public    TINYINT  DEFAULT 1                 NULL,
    status       TINYINT  DEFAULT 1                 NULL,
    view_count   INT      DEFAULT 0                 NULL,
    publish_time DATETIME                           NULL,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS operation_log (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    module         VARCHAR(50)                        NULL,
    type           VARCHAR(20)                        NULL,
    description    VARCHAR(500)                       NULL,
    request_method VARCHAR(10)                        NULL,
    request_url    VARCHAR(255)                       NULL,
    request_params TEXT                               NULL,
    response_data  TEXT                               NULL,
    user_id        BIGINT                             NULL,
    username       VARCHAR(50)                        NULL,
    ip             VARCHAR(50)                        NULL,
    user_agent     VARCHAR(500)                       NULL,
    execution_time INT                                NULL,
    status         TINYINT  DEFAULT 1                 NULL,
    error_message  TEXT                               NULL,
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS system_config (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key   VARCHAR(100) NOT NULL,
    config_value TEXT         NULL,
    config_type  VARCHAR(20) DEFAULT 'STRING' NULL,
    description  VARCHAR(200) NULL,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_config_key (config_key)
);

CREATE TABLE IF NOT EXISTS tags (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    color         VARCHAR(20) DEFAULT '#007bff' NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    article_count INT NULL,
    UNIQUE KEY uk_name (name)
);

CREATE TABLE IF NOT EXISTS user (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    nickname        VARCHAR(50) NULL,
    email           VARCHAR(100) NULL,
    avatar          VARCHAR(255) NULL,
    role            VARCHAR(20) DEFAULT 'USER' NULL,
    status          TINYINT DEFAULT 1 NULL,
    last_login_time DATETIME NULL,
    last_login_ip   VARCHAR(50) NULL,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
);

-- =============================================
-- 2. 补缺失的字段（逐列检测，已存在则跳过）
-- =============================================

-- article 表
SET @col = 'cover_image'; SET @tbl = 'article'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(1000) NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'tags'; SET @tbl = 'article'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(256) NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'summary'; SET @tbl = 'article'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(500) NULL COMMENT ''文章摘要'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'is_comment'; SET @tbl = 'article'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' TINYINT DEFAULT 1 NULL COMMENT ''是否允许评论'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- category 表
SET @col = 'article_count'; SET @tbl = 'category'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' INT NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- comment 表（新增功能相关字段）
SET @col = 'like_count'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' INT DEFAULT 0 NULL COMMENT ''点赞数'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'location'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(100) NULL COMMENT ''IP归属地'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'is_owner'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' TINYINT DEFAULT 0 NULL COMMENT ''是否博主'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'floor'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' INT NULL COMMENT ''楼层号'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'notice'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' TINYINT DEFAULT 0 NULL COMMENT ''邮件提醒'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'avatar'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(500) NULL COMMENT ''头像地址'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'ip'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(50) NULL COMMENT ''IP地址'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'parentCommentId'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' BIGINT NULL COMMENT ''父评论ID'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'email'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(200) NULL COMMENT ''邮箱'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'website'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(500) NULL COMMENT ''网站'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'status'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' TINYINT DEFAULT 1 NULL COMMENT ''是否公开'''), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- friend_link 表
SET @col = 'view_count'; SET @tbl = 'friend_link'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' INT NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- user 表
SET @col = 'nickname'; SET @tbl = 'user'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(50) NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'email'; SET @tbl = 'user'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(100) NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'avatar'; SET @tbl = 'user'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(255) NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'last_login_time'; SET @tbl = 'user'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' DATETIME NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @col = 'last_login_ip'; SET @tbl = 'user'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND COLUMN_NAME=@col) = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ', @col, ' VARCHAR(50) NULL'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =============================================
-- 3. 补缺失的索引（先检测再创建）
-- =============================================

-- article 表索引
SET @idx = 'idx_category_id'; SET @tbl = 'article'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (category_id)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @idx = 'idx_publish_time'; SET @tbl = 'article'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (publish_time)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @idx = 'idx_status'; SET @tbl = 'article'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (status)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- comment 表索引
SET @idx = 'idx_article_id'; SET @tbl = 'comment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (blog_id)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- moment 表索引
SET @idx = 'idx_create_time'; SET @tbl = 'moment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (create_time)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @idx = 'idx_is_public'; SET @tbl = 'moment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (is_public)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @idx = 'idx_publish_time'; SET @tbl = 'moment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (publish_time)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @idx = 'idx_status'; SET @tbl = 'moment'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (status)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- operation_log 表索引
SET @idx = 'idx_create_time'; SET @tbl = 'operation_log'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (create_time)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @idx = 'idx_user_id'; SET @tbl = 'operation_log'; SET @sql = (SELECT IF( (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='eleven_blog' AND TABLE_NAME=@tbl AND INDEX_NAME=@idx) = 0, CONCAT('CREATE INDEX ', @idx, ' ON ', @tbl, ' (user_id)'), 'SELECT 1')); PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =============================================
-- 4. 补缺失的默认数据
-- =============================================

-- 默认管理员（不存在时插入）
INSERT INTO user (username, password, nickname, role, status)
SELECT 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin', 'ADMIN', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin');

-- 博客配置默认数据（逐条检测，不存在时插入）
INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'blog_avatar', '/avatar.png', 'STRING', '博主头像URL'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'blog_avatar');

INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'blog_name', 'Eleven-Mouse', 'STRING', '博主昵称'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'blog_name');

INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'blog_bio', '热爱编程，热爱生活', 'STRING', '博主个性签名'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'blog_bio');

INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'blog_github_url', 'https://github.com/Eleven-Mouse', 'STRING', 'GitHub主页地址'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'blog_github_url');

INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'blog_footer_desc', 'ERROR 404 — 社交生活未找到。正在用代码填补中...', 'STRING', '页脚描述文字'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'blog_footer_desc');

INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'blog_copyright_name', 'Eleven-Mouse', 'STRING', '版权所有者名称'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'blog_copyright_name');

-- =============================================
-- 更新完成
-- =============================================
