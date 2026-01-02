create table article
(
    id           bigint auto_increment comment '文章ID'
        primary key,
    title        varchar(200)                       not null comment '文章标题',
    summary      varchar(500)                       null comment '文章摘要',
    content      longtext                           null comment '文章内容（Markdown格式）',
    category_id  bigint                             null comment '分类ID',
    view_count   int      default 0                 null comment '浏览次数',
    is_comment   tinyint  default 1                 null comment '是否允许评论：0-否，1-是',
    status       tinyint  default 1                 null comment '状态：0-草稿，1-已发布，2-已删除',
    publish_time datetime                           null comment '发布时间',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    cover_image  varchar(1000)                      null,
    tags         varchar(256)                       null
)
    comment '文章表';

create index idx_category_id
    on article (category_id);

create index idx_publish_time
    on article (publish_time);

create index idx_status
    on article (status);

create table category
(
    id            bigint auto_increment comment '分类ID'
        primary key,
    name          varchar(50)                        not null comment '分类名称',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    article_count int                                null,
    constraint uk_name
        unique (name)
)
    comment '分类表';

create table comment
(
    id           bigint auto_increment comment '评论ID'
        primary key,
    nickname     varchar(100)                       not null comment '昵称',
    email        varchar(200)                       null comment '邮箱',
    website      varchar(500)                       null comment '网站',
    logo         varchar(500)                       null comment '头像地址',
    ip           varchar(50)                        null comment 'IP地址',
    content      text                               not null comment '评论内容',
    qq           varchar(20)                        null comment 'QQ号',
    page         varchar(200)                       null comment '所在页面标识',
    article_id   bigint                             null comment '文章ID',
    status       tinyint  default 1                 null comment '是否公开：0-否，1-是',
    email_notify tinyint  default 0                 null comment '邮件提醒：0-否，1-是',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    articleTitle varchar(388)                       null,
    update_time  datetime default (now())           null
)
    comment '评论表';

create index idx_article_id
    on comment (article_id);

create index idx_create_time
    on comment (create_time);

create table friend_link
(
    id          bigint auto_increment comment '友链ID'
        primary key,
    name        varchar(100)                       not null comment '网站名称',
    url         varchar(255)                       not null comment '网站URL',
    logo        varchar(1000)                      null comment '网站头像/Logo',
    description varchar(200)                       null comment '网站描述',
    status      tinyint  default 1                 null comment '状态：0-禁用，1-启用',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    view_count  int                                null
)
    comment '友链表';

create table moment
(
    id           bigint auto_increment comment '动态ID'
        primary key,
    content      text                               not null comment '动态内容',
    image        varchar(1000)                      null comment '动态图片（多个图片用逗号分隔）',
    is_public    tinyint  default 1                 null comment '是否公开：0-私密，1-公开',
    status       tinyint  default 1                 null comment '动态状态：0-草稿，1-已发布',
    view_count   int      default 0                 null comment '浏览次数',
    publish_time datetime                           null comment '发布时间',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '动态表';

create index idx_create_time
    on moment (create_time);

create index idx_is_public
    on moment (is_public);

create index idx_publish_time
    on moment (publish_time);

create index idx_status
    on moment (status);

create table operation_log
(
    id             bigint auto_increment comment '日志ID'
        primary key,
    module         varchar(50)                        null comment '操作模块',
    type           varchar(20)                        null comment '操作类型：CREATE-新增，UPDATE-修改，DELETE-删除，QUERY-查询',
    description    varchar(500)                       null comment '操作描述',
    request_method varchar(10)                        null comment '请求方法：GET/POST/PUT/DELETE',
    request_url    varchar(255)                       null comment '请求URL',
    request_params text                               null comment '请求参数（JSON）',
    response_data  text                               null comment '响应数据（JSON）',
    user_id        bigint                             null comment '操作用户ID',
    username       varchar(50)                        null comment '操作用户名',
    ip             varchar(50)                        null comment 'IP地址',
    user_agent     varchar(500)                       null comment '用户代理',
    execution_time int                                null comment '执行时间（毫秒）',
    status         tinyint  default 1                 null comment '状态：0-失败，1-成功',
    error_message  text                               null comment '错误信息',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '操作日志表';

create index idx_create_time
    on operation_log (create_time);

create index idx_user_id
    on operation_log (user_id);

create table system_config
(
    id           bigint auto_increment comment '配置ID'
        primary key,
    config_key   varchar(100)                          not null comment '配置键',
    config_value text                                  null comment '配置值',
    config_type  varchar(20) default 'STRING'          null comment '配置类型：STRING/NUMBER/BOOLEAN/JSON',
    description  varchar(200)                          null comment '配置描述',
    create_time  datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_config_key
        unique (config_key)
)
    comment '系统配置表';

create table tags
(
    id            bigint auto_increment comment '标签ID'
        primary key,
    name          varchar(50)                           not null comment '标签名称',
    color         varchar(20) default '#007bff'         null comment '标签颜色',
    create_time   datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    article_count int                                   null,
    constraint uk_name
        unique (name)
)
    comment '标签表';

create table user
(
    id              bigint auto_increment comment '用户ID'
        primary key,
    username        varchar(50)                           not null comment '用户名',
    password        varchar(255)                          not null comment '密码（加密）',
    nickname        varchar(50)                           null comment '昵称',
    email           varchar(100)                          null comment '邮箱',
    avatar          varchar(255)                          null comment '头像URL',
    role            varchar(20) default 'USER'            null comment '角色：ADMIN-管理员，USER-普通用户',
    status          tinyint     default 1                 null comment '状态：0-禁用，1-启用',
    last_login_time datetime                              null comment '最后登录时间',
    last_login_ip   varchar(50)                           null comment '最后登录IP',
    create_time     datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_username
        unique (username)
)
    comment '用户表';

create definer = root@localhost view v_article_stats as
select `a`.`id`                               AS `article_id`,
       `a`.`title`                            AS `title`,
       `a`.`view_count`                       AS `view_count`,
       `a`.`like_count`                       AS `like_count`,
       `a`.`comment_count`                    AS `comment_count`,
       `c`.`name`                             AS `category_name`,
       `u`.`nickname`                         AS `author_name`,
       group_concat(`t`.`name` separator ',') AS `tags`,
       `a`.`publish_time`                     AS `publish_time`,
       `a`.`status`                           AS `status`
from ((((`eleven_blog`.`article` `a` left join `eleven_blog`.`category` `c`
         on ((`a`.`category_id` = `c`.`id`))) left join `eleven_blog`.`user` `u`
        on ((`a`.`author_id` = `u`.`id`))) left join `eleven_blog`.`article_tag` `at`
       on ((`a`.`id` = `at`.`article_id`))) left join `eleven_blog`.`tag` `t` on ((`at`.`tag_id` = `t`.`id`)))
group by `a`.`id`, `a`.`title`, `a`.`view_count`, `a`.`like_count`, `a`.`comment_count`, `c`.`name`, `u`.`nickname`,
         `a`.`publish_time`, `a`.`status`;


