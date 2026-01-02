package blog.vo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 友链VO类
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class FriendLinkVO {

    /**
     * 友链ID
     */
    private Long id;

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

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

