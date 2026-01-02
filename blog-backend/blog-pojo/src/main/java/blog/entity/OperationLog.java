package blog.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class OperationLog {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型：CREATE-创建，UPDATE-更新，DELETE-删除，LOGIN-登录，LOGOUT-登出
     */
    private String operation;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 响应结果
     */
    private String result;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 操作状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

