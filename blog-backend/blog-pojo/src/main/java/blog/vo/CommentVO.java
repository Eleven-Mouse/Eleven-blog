package blog.vo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论VO类
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CommentVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 网站地址
     */
    private String website;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 发表时间
     */
    private LocalDateTime createTime;

    /**
     * ip
     */
    private String ip;
    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章ID（如果是文章评论）
     */
    private Long blogId;

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
     * 博主回复
     */
    private Boolean adminComment;
    /**
     * 父评论id
     */
    private  Long  parentCommentId;
    private String parentNickname;
    /**
     * 父评论昵称
     */
    private String parentCommentNickname;
    /**
     * 回复该评论的评论
     */
    private List<CommentVO> replyComments = new ArrayList<>();
}



