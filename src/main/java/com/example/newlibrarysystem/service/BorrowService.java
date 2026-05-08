package com.example.newlibrarysystem.service;

import com.example.newlibrarysystem.dto.response.BorrowRecordResponse;
import com.example.newlibrarysystem.exception.BusinessException;
import com.example.newlibrarysystem.repository.BorrowingRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowingRecordRepository borrowingRecordRepository;

    /**
     * 借書：Transaction 同時由 Spring @Transactional 與 SP 內部 Transaction 雙層保護
     */
    @Transactional
    public Long borrowBook(Long userId, String isbn) {
        Map<String, Object> result = borrowingRecordRepository.borrowBook(userId, isbn);

        int code = ((Number) result.get("p_result")).intValue();
        String msg = (String) result.get("p_message");

        if (code == 0) {
            throw new BusinessException(msg);
        }
        if (code == -1) {
            throw new BusinessException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Long inventoryId = ((Number) result.get("p_inventory_id")).longValue();
        log.info("Book borrowed: userId={}, isbn={}, inventoryId={}", userId, isbn, inventoryId);
        return inventoryId;
    }

    /**
     * 還書：Transaction 同時由 Spring @Transactional 與 SP 內部 Transaction 雙層保護
     */
    @Transactional
    public void returnBook(Long userId, Long inventoryId) {
        Map<String, Object> result = borrowingRecordRepository.returnBook(userId, inventoryId);

        int code = ((Number) result.get("p_result")).intValue();
        String msg = (String) result.get("p_message");

        if (code == 0) {
            throw new BusinessException(msg);
        }
        if (code == -1) {
            throw new BusinessException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Book returned: userId={}, inventoryId={}", userId, inventoryId);
    }

    /**
     * 查詢使用者所有借閱紀錄
     */
    public List<BorrowRecordResponse> getUserBorrows(Long userId) {
        return borrowingRecordRepository.findByUserId(userId);
    }
}