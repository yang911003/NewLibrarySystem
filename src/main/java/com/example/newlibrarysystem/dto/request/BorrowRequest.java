package com.example.newlibrarysystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BorrowRequest {

    @NotBlank(message = "ISBN 不可為空")
    private String isbn;
}