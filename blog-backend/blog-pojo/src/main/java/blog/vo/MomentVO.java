package blog.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class MomentVO implements Serializable
{
    /**
     * 动态ID
     */
    private Long id;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 动态图片，多个图片用逗号分隔
     */
    private String image;


    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

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

    private Integer viewCount;
    /**
     * 状态文本（草稿、已发布）
     */
    private String statusText;
}
