package blog.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章VO（用于前端展示）
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ArticleVO implements Serializable {

    /**
     * 文章ID
     */
    private Long id;

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
     * 分类名称
     */
    private String categoryName;

    /**
     * 浏览次数
     */
    private Integer viewCount;


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
     * 状态文本（草稿、已发布）
     */
    private String statusText;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

