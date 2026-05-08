package com.example.newlibrarysystem.service;

import com.example.newlibrarysystem.common.JwtUtil;
import com.example.newlibrarysystem.dto.request.LoginRequest;
import com.example.newlibrarysystem.dto.request.RegisterRequest;
import com.example.newlibrarysystem.dto.response.LoginResponse;
import com.example.newlibrarysystem.exception.BusinessException;
import com.example.newlibrarysystem.model.User;
import com.example.newlibrarysystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;

    /**
     * 使用者註冊
     * 密碼以 BCrypt（含鹽值）雜湊後儲存，防止明碼外洩
     */
    public void register(RegisterRequest req) {
        String hashedPassword = passwordEncoder.encode(req.getPassword());

        Map<String, Object> result =
                userRepository.register(req.getPhoneNumber(), hashedPassword, req.getUserName());

        int code = ((Number) result.get("p_result")).intValue();
        String msg = (String) result.get("p_message");

        if (code == 0) {
            throw new BusinessException(msg);
        }
        if (code == -1) {
            throw new BusinessException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("User registered: phone={}", req.getPhoneNumber());
    }

    /**
     * 使用者登入，驗證成功後發放 JWT Token
     */
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByPhoneNumber(req.getPhoneNumber())
                .orElseThrow(() -> new BusinessException("手機號碼或密碼錯誤",
                        HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("手機號碼或密碼錯誤", HttpStatus.UNAUTHORIZED);
        }

        userRepository.updateLastLogin(user.getUserId());

        String token = jwtUtil.generateToken(user.getUserId(), user.getPhoneNumber());

        log.info("User logged in: userId={}", user.getUserId());
        return new LoginResponse(token, user.getUserId(),
                                 user.getUserName(), user.getPhoneNumber());
    }
}