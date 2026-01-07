package blog.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class UserDetailsServiceImpl implements UserDetailsService
{

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {

        // 假设数据库里只有一个管理员：admin
        if (!"admin".equals(username)) {
            throw new UsernameNotFoundException("用户名不存在: " + username);
        }

        // 模拟从数据库取出的密码（它是加密过的）
        // 这里的密文对应的是 "123456"
        String dbPassword = new BCryptPasswordEncoder().encode("123456");

        // 返回 Spring Security 需要的对象
        return User.builder()
                .username("admin")
                // 数据库里的加密密码
                .password(dbPassword)
                // 用户的权限/角色
                .authorities("ROLE_ADMIN")
                .build();
    }

}
