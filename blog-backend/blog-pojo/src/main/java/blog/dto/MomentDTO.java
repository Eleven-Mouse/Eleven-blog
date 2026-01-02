package blog.dto;

import lombok.*;

/**
 * 创建动态DTO
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class MomentDTO
{
    private Integer id;
    /**
     * 动态内容
     */
    private String content;

    /**
     * 动态图片，多个图片用逗号分隔
     */
    private String image;

    /**
     * 动态状态：0-草稿，1-已发布
     */
    private Integer status;

}


