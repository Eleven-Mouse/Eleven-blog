package blog.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论数据传输对象（用于创建和更新）
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 博主回复
     */
    private Boolean adminComment;

    /**
     * 所属文章id
     */
    private Long blogId;
}
