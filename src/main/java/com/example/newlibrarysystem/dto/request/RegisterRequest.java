package com.example.newlibrarysystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "手機號碼不可為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phoneNumber;

    @NotBlank(message = "密碼不可為空")
    @Size(min = 8, max = 50, message = "密碼長度須為 8~50 個字元")
    private String password;

    @NotBlank(message = "使用者名稱不可為空")
    @Size(max = 100, message = "使用者名稱不可超過 100 個字元")
    private String userName;
}