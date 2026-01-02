package blog.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 友情链接实体类
 * 用于管理博客的友情链接
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class FriendLink {

    /**
     * 友链ID
     */
    private Long id;

    /**
     * 网站名称
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
     * 显示顺序，数字越小越靠前
     */
    private Integer sortOrder;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝
     */
    private Integer status;

    /**
     * 站长邮箱
     */
    private String email;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;

    /**
     * 是否公开：0-否，1-是
     */
    private Integer isPublic;

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
