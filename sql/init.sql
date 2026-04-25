-- ============================================
-- Eleven Blog 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS eleven_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eleven_blog;

-- 文章表
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

CREATE INDEX idx_category_id ON article (category_id);
CREATE INDEX idx_publish_time ON article (publish_time);
CREATE INDEX idx_status ON article (status);

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    article_count INT                                NULL,
    UNIQUE KEY uk_name (name)
);

-- 评论表 (含点赞/楼层/博主标识/IP归属地)
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

CREATE INDEX idx_article_id ON comment (blog_id);
CREATE INDEX idx_create_time ON comment (create_time);

-- 友链表
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

-- 动态表
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

CREATE INDEX idx_create_time ON moment (create_time);
CREATE INDEX idx_is_public ON moment (is_public);
CREATE INDEX idx_publish_time ON moment (publish_time);
CREATE INDEX idx_status ON moment (status);

-- 操作日志表
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

CREATE INDEX idx_create_time ON operation_log (create_time);
CREATE INDEX idx_user_id ON operation_log (user_id);

-- 系统配置表
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

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    color         VARCHAR(20) DEFAULT '#007bff' NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    article_count INT NULL,
    UNIQUE KEY uk_name (name)
);

-- 用户表 (管理员)
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

-- 默认管理员账号 (密码: 123456, BCrypt加密)
INSERT INTO user (username, password, nickname, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin', 'ADMIN', 1);

-- 博客配置默认数据
INSERT INTO system_config (config_key, config_value, config_type, description) VALUES
('blog_avatar', '/avatar.png', 'STRING', '博主头像URL'),
('blog_name', 'Eleven-Mouse', 'STRING', '博主昵称'),
('blog_bio', '热爱编程，热爱生活', 'STRING', '博主个性签名'),
('blog_github_url', 'https://github.com/Eleven-Mouse', 'STRING', 'GitHub主页地址'),
('blog_footer_desc', 'ERROR 404 — 社交生活未找到。正在用代码填补中...', 'STRING', '页脚描述文字'),
('blog_copyright_name', 'Eleven-Mouse', 'STRING', '版权所有者名称');
