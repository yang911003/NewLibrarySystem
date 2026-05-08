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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req) {
        LoginResponse resp = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok("登入成功", resp));
    }
}