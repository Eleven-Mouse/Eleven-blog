-- Eleven Blog 数据库初始化脚本
-- 根据后端 Mapper 代码精确生成，字段完全对齐

CREATE DATABASE IF NOT EXISTS eleven_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eleven_blog;

-- 1. 分类表
CREATE TABLE IF NOT EXISTS category (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name        VARCHAR(50)    NOT NULL UNIQUE   COMMENT '分类名称',
    create_time DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '分类表';

-- 2. 标签表
CREATE TABLE IF NOT EXISTS tags (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name        VARCHAR(50)    NOT NULL UNIQUE   COMMENT '标签名称',
    create_time DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '标签表';

-- 3. 文章表
CREATE TABLE IF NOT EXISTS article (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    title        VARCHAR(200)   NOT NULL           COMMENT '文章标题',
    summary      VARCHAR(500)                      COMMENT '文章摘要',
    content      LONGTEXT                          COMMENT '文章内容（Markdown）',
    cover_image  VARCHAR(1000)                     COMMENT '封面图片',
    category_id  BIGINT                            COMMENT '分类ID',
    tags         VARCHAR(256)                      COMMENT '标签ID（逗号分隔）',
    author_id    BIGINT                            COMMENT '作者ID',
    view_count   INT            DEFAULT 0          COMMENT '浏览次数',
    is_comment   TINYINT        DEFAULT 1          COMMENT '是否允许评论：0-否 1-是',
    status       TINYINT        DEFAULT 1          COMMENT '状态：0-草稿 1-已发布 2-已删除',
    publish_time DATETIME                          COMMENT '发布时间',
    create_time  DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_publish_time (publish_time)
) COMMENT '文章表';

-- 4. 评论表
CREATE TABLE IF NOT EXISTS comment (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    nickname         VARCHAR(100)   NOT NULL           COMMENT '昵称',
    email            VARCHAR(200)                      COMMENT '邮箱',
    website          VARCHAR(500)                      COMMENT '网站',
    avatar           VARCHAR(500)                      COMMENT '头像',
    ip               VARCHAR(50)                       COMMENT 'IP地址',
    content          TEXT           NOT NULL           COMMENT '评论内容',
    page             VARCHAR(200)                      COMMENT '所在页面标识',
    blog_id          BIGINT                            COMMENT '文章ID',
    status           TINYINT        DEFAULT 1          COMMENT '是否公开：0-否 1-是',
    notice           TINYINT        DEFAULT 0          COMMENT '邮件通知：0-否 1-是',
    parentCommentId  BIGINT                            COMMENT '父评论ID',
    create_time      DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_blog_id (blog_id),
    INDEX idx_create_time (create_time)
) COMMENT '评论表';

-- 5. 动态表
CREATE TABLE IF NOT EXISTS moment (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '动态ID',
    content      TEXT           NOT NULL             COMMENT '动态内容',
    image        VARCHAR(1000)                      COMMENT '动态图片（逗号分隔）',
    status       TINYINT        DEFAULT 1            COMMENT '状态：0-草稿 1-已发布',
    publish_time DATETIME                           COMMENT '发布时间',
    create_time  DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_create_time (create_time)
) COMMENT '动态表';

-- 6. 友链表
CREATE TABLE IF NOT EXISTS friend_link (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '友链ID',
    name        VARCHAR(100)   NOT NULL             COMMENT '网站名称',
    url         VARCHAR(255)   NOT NULL             COMMENT '网站URL',
    description VARCHAR(200)                        COMMENT '网站描述',
    logo        VARCHAR(1000)                       COMMENT '网站头像',
    status      TINYINT        DEFAULT 1            COMMENT '状态：0-禁用 1-启用',
    view_count  INT            DEFAULT 0            COMMENT '浏览次数',
    create_time DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '友链表';
