package blog.service;

import blog.dto.Login.LoginRequest;
import blog.dto.Login.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService
{

    LoginResponse login(LoginRequest request);
}
