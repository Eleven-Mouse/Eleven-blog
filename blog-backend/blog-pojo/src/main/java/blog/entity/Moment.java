package blog.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 动态实体类
 * 用于发布简短的动态信息，类似微博或朋友圈
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Moment {

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
     * 是否公开：0-私密，1-公开
     */
    private Integer isPublic;

    /**
     * 动态状态：0-草稿，1-已发布
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
}


