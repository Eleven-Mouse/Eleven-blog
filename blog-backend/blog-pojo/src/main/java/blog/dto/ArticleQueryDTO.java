package blog.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 文章查询DTO
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ArticleQueryDTO implements Serializable {

    /**
     * 关键词（标题）
     */
    private String keyword;

    /**
     * 标签（逗号分隔的字符串）
     */
    private String tags;

    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer pageSize;
}
