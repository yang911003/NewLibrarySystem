package com.example.newlibrarysystem.controller;

import com.example.newlibrarysystem.dto.request.LoginRequest;
import com.example.newlibrarysystem.dto.request.RegisterRequest;
import com.example.newlibrarysystem.dto.response.ApiResponse;
import com.example.newlibrarysystem.dto.response.LoginResponse;
import com.example.newlibrarysystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
    public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok(ApiResponse.ok("註冊成功", null));
    }
//  @RequestBody 的意思就是「從 HTTP request body 讀取 JSON 並反序列化」，如果前端沒送 JSON，Spring 會直接報錯。

//    五、一句話總結
//  ▎ 前端用 Axios 帶 JWT token 發 HTTP 請求，Spring Security 先驗證身份，DispatcherServlet 分派給對應 Controller，Controller 呼叫 Service 處理邏輯，Service 透過 Repository 呼叫
//  ▎ MySQL Stored Procedure 存取資料，結果統一包成 ApiResponse JSON 回傳給前端渲染。

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req) {
        LoginResponse resp = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok("登入成功", resp));
    }
}