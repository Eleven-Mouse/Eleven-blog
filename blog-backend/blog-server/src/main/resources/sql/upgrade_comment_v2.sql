USE eleven_blog;

ALTER TABLE comment
    ADD COLUMN like_count  INT           DEFAULT 0      COMMENT '点赞数' AFTER content,
    ADD COLUMN location    VARCHAR(100)  DEFAULT NULL   COMMENT 'IP归属地' AFTER ip,
    ADD COLUMN is_owner    TINYINT       DEFAULT 0      COMMENT '是否博主: 0-否 1-是' AFTER status,
    ADD COLUMN floor       INT           DEFAULT NULL   COMMENT '楼层号' AFTER is_owner;

-- 为已有的一级评论按时间顺序补充楼层号
SET @floor_num = 0;
UPDATE comment c
    INNER JOIN (
        SELECT id, blog_id, page,
               @floor_num := IF(@prev_blog = blog_id AND @prev_page = page, @floor_num + 1, 1) AS new_floor,
               @prev_blog := blog_id,
               @prev_page := page
        FROM comment
        WHERE parentCommentId IS NULL
        ORDER BY blog_id, page, create_time
    ) AS ranked ON c.id = ranked.id
SET c.floor = ranked.new_floor;
