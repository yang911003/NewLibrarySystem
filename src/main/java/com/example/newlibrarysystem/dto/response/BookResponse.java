package com.example.newlibrarysystem.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookResponse {
    private String  isbn;
    private String  name;
    private String  author;
    private String  introduction;
    private int     totalCopies;
    private int     availableCopies;
}