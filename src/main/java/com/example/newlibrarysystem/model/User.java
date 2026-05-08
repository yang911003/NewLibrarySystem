package com.example.newlibrarysystem.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long          userId;
    private String        phoneNumber;
    private String        password;
    private String        userName;
    private LocalDateTime registrationTime;
    private LocalDateTime lastLoginTime;
}