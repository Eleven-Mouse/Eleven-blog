package blog.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建文章DTO
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ArticleDTO implements Serializable {

    /**
     * 文章标题
     */

    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容（Markdown格式）
     * 草稿时可以为空，发布时必填
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签（逗号分隔的字符串）
     */
    private String tags;


    /**
     * 是否允许评论（0-否，1-是）
     */
    private Integer isComment;

    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 浏览量
     */
    private Integer ViewCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
}

