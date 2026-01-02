package blog.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 分类实体类
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Category {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 文章数量
     */
    private Integer articleCount;
}

