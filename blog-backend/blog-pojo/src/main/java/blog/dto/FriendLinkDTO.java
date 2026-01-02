package blog.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 友链DTO
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@Getter
@Setter
public class FriendLinkDTO {

    /**
     * 昵称
     */
    private String name;

    /**
     * 网站URL
     */
    private String url;

    /**
     * 网站描述
     */
    private String description;

    /**
     * 网站Logo/头像地址
     */
    private String logo;

    /**
     * 状态：0-私密，1-公开
     */
    private Integer status;
}
