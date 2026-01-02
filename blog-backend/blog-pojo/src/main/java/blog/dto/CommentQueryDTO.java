package blog.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 评论查询DTO
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CommentQueryDTO {

    /**
     * 文章ID（筛选条件）
     */
    private Long blogId;

    /**
     * 页面标识（筛选条件）
     */
    private String page;

    /**
     * 是否公开（筛选条件）
     */
    private Boolean status;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 页码
     */
    private Integer pageNum = 1;
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
     * 每页大小
     */
    private Integer pageSize = 10;
}














