package blog.dto;

import lombok.*;

/**
 * 动态查询DTO
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class MomentQueryDTO {

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 状态：0-草稿，1-已发布，2-已删除
     */
    private Integer status;

    /**
     * 搜索关键词
     */
    private String keyword;

}


