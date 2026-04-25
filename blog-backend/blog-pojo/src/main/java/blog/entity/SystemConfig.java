package blog.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 通用键值对配置，用于博主信息、站点设置等
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 值类型：STRING / NUMBER / BOOLEAN
     */
    private String configType;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
