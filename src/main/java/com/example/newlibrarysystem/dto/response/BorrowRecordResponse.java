package com.example.newlibrarysystem.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BorrowRecordResponse {
    private Long          recordId;
    private Long          userId;
    private Long          inventoryId;
    private LocalDateTime borrowingTime;
    private LocalDateTime returnTime;
    private String        isbn;
    private String        inventoryStatus;
    private String        bookName;
    private String        bookAuthor;

    public boolean isReturned() {
        return returnTime != null;
    }
}