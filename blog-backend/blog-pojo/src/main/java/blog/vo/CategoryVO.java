package blog.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CategoryVO implements Serializable
{
    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 专题别名（用于前端路由）
     */
    private String slug;

    /**
     * 专题简介
     */
    private String description;

    /**
     * 专题封面
     */
    private String coverImage;

    /**
     * 排序（越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
