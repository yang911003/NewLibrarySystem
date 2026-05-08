package com.example.newlibrarysystem.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private Long inventoryId;
    private String isbn;
    private LocalDateTime storeTime;
    private String status;
}