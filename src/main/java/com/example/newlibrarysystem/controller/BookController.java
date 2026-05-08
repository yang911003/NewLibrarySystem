package com.example.newlibrarysystem.controller;

import com.example.newlibrarysystem.dto.response.ApiResponse;
import com.example.newlibrarysystem.dto.response.BookResponse;
import com.example.newlibrarysystem.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        return ResponseEntity.ok(ApiResponse.ok(bookService.getAllBooks()));
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable String isbn) {
        return ResponseEntity.ok(ApiResponse.ok(bookService.getBookByIsbn(isbn)));
    }
}