package com.example.newlibrarysystem.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecord {
    private Long recordId;
    private Long userId;
    private Long inventoryId;
    private LocalDateTime borrowingTime;
    private LocalDateTime returnTime;
}