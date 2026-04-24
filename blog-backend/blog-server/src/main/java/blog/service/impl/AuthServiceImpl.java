package blog.service.impl;

import blog.dto.Login.LoginRequest;
import blog.dto.Login.LoginResponse;
import blog.dto.Login.RefreshRequest;
import blog.service.AuthService;
import blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService
{
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsService userDetailsService;

    public LoginResponse login(LoginRequest request) {
        // 1. 调用 Spring Security 进行认证
        // 如果认证失败（密码错误），这里会直接抛出 AuthenticationException
        Authentication authentication = authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. 认证通过，获取用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 3. 生成双 Token
        String accessToken = jwtUtil.createAccessToken(username, roles);
        String refreshToken = jwtUtil.createRefreshToken(username);

        // 4. 返回结果
        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public LoginResponse refresh(RefreshRequest request) {
        // 1. 解析 refreshToken 获取用户名
        Claims claims = jwtUtil.parseToken(request.getRefreshToken());
        String username = claims.getSubject();

        // 2. 校验 refreshToken 是否在 Redis 中且一致
        if (!jwtUtil.validataRefreshToken(username, request.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 无效或已过期");
        }

        // 3. 加载用户信息，获取角色
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 4. 生成新的 accessToken，保留原有 refreshToken
        String newAccessToken = jwtUtil.createAccessToken(username, roles);

        return new LoginResponse(newAccessToken, request.getRefreshToken());
    }
}
