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
     * 分类ID
     */
    private Long categoryId;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer pageSize;
}
