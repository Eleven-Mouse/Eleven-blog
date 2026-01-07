package blog.dto;

import lombok.*;


/**
 * 登录DTO
 *
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class LoginDTO {

    /**
     * 用户名
     */

    private String username;

    /**
     * 密码
     */

    private String password;

    /**
     * 记住我
     */
    private Boolean remember;
}


