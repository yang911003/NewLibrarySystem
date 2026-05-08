package com.example.newlibrarysystem.controller;

import com.example.newlibrarysystem.dto.request.BorrowRequest;
import com.example.newlibrarysystem.dto.response.ApiResponse;
import com.example.newlibrarysystem.dto.response.BorrowRecordResponse;
import com.example.newlibrarysystem.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    /** 借書：需登入，自動選取第一本可用庫存 */
    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<Map<String, Long>>> borrow(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BorrowRequest req) {

        Long inventoryId = borrowService.borrowBook(userId, req.getIsbn());
        return ResponseEntity.ok(ApiResponse.ok("借閱成功",
                Map.of("inventoryId", inventoryId)));
    }

    /** 還書：需登入，帶入 inventoryId */
    @PutMapping("/return/{inventoryId}")
    public ResponseEntity<ApiResponse<Void>> returnBook(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long inventoryId) {

        borrowService.returnBook(userId, inventoryId);
        return ResponseEntity.ok(ApiResponse.ok("還書成功", null));
    }

    /** 查詢我的借閱紀錄：需登入 */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> myBorrows(
            @AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(ApiResponse.ok(borrowService.getUserBorrows(userId)));
    }
}