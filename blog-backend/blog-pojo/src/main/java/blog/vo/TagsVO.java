package blog.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TagsVO implements Serializable
{
    private Long id;

    private String name;


    /**
     * 文章数量
     */
    private Integer articleCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
