package blog.dto;

import lombok.*;

/**
 * 友链查询DTO
 *
 * @author Eleven
 * @version 1.0
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class FriendLinkQueryDTO {

    /**
     * 昵称
     */
    private String name;

    /**
     * 友链状态：0-私密，1-公开
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}




















