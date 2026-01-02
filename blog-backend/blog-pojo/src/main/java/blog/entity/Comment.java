package blog.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论实体类
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Comment {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 评论内容
     */
    private String content;


    /**
     * 所在页面
     */
    private String page;

    /**
     * 是否公开：0-否，1-是
     */
    private Boolean status;

    /**
     * 邮件提醒：0-否，1-是
     */
    private Boolean notice;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;
    /**
     * 父评论id
     */
    private Long parentCommentId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章ID（如果是文章评论）
     */
    private Long blogId;
    /**
     * 博主回复
     */
    private Boolean adminComment;
    /**
     * 回复该评论的评论
     */
    private List<Comment> replyComments = new ArrayList<>();

}



