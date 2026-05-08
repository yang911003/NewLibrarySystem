package com.example.newlibrarysystem.repository;

import com.example.newlibrarysystem.dto.response.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${DB_NAME:library_system}")
    private String dbName;

    private final RowMapper<BookResponse> bookRowMapper = (rs, rowNum) -> {
        BookResponse b = new BookResponse();
        b.setIsbn(rs.getString("isbn"));
        b.setName(rs.getString("name"));
        b.setAuthor(rs.getString("author"));
        b.setIntroduction(rs.getString("introduction"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        return b;
    };

    private SimpleJdbcCall call(String procedureName) {
        return new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(dbName)
                .withProcedureName(procedureName);
    }

    public List<BookResponse> findAll() {
        SimpleJdbcCall jdbcCall = call("sp_get_all_books")
                .returningResultSet("books", bookRowMapper);

        Map<String, Object> out = jdbcCall.execute(new HashMap<>());
        @SuppressWarnings("unchecked")
        List<BookResponse> books = (List<BookResponse>) out.get("books");
        return books;
    }

    public Optional<BookResponse> findByIsbn(String isbn) {
        SimpleJdbcCall jdbcCall = call("sp_get_book_by_isbn")
                .declareParameters(new SqlParameter("p_isbn", Types.VARCHAR))
                .returningResultSet("book", bookRowMapper);

        Map<String, Object> params = new HashMap<>();
        params.put("p_isbn", isbn);

        Map<String, Object> out = jdbcCall.execute(params);
        @SuppressWarnings("unchecked")
        List<BookResponse> books = (List<BookResponse>) out.get("book");
        return (books != null && !books.isEmpty()) ? Optional.of(books.get(0)) : Optional.empty();
    }
}