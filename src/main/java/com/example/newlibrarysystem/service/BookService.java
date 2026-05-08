package com.example.newlibrarysystem.service;

import com.example.newlibrarysystem.dto.response.BookResponse;
import com.example.newlibrarysystem.exception.BusinessException;
import com.example.newlibrarysystem.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll();
    }

    public BookResponse getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException("找不到此書籍：" + isbn,
                        HttpStatus.NOT_FOUND));
    }
}