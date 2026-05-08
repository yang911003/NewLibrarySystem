package com.example.newlibrarysystem.repository;

import com.example.newlibrarysystem.dto.response.BorrowRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BorrowingRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${DB_NAME:library_system}")
    private String dbName;

    private final RowMapper<BorrowRecordResponse> recordRowMapper = (rs, rowNum) -> {
        BorrowRecordResponse r = new BorrowRecordResponse();
        r.setRecordId(rs.getLong("record_id"));
        r.setUserId(rs.getLong("user_id"));
        r.setInventoryId(rs.getLong("inventory_id"));
        r.setBorrowingTime(rs.getTimestamp("borrowing_time").toLocalDateTime());
        var returnTs = rs.getTimestamp("return_time");
        r.setReturnTime(returnTs != null ? returnTs.toLocalDateTime() : null);
        r.setIsbn(rs.getString("isbn"));
        r.setInventoryStatus(rs.getString("inventory_status"));
        r.setBookName(rs.getString("book_name"));
        r.setBookAuthor(rs.getString("book_author"));
        return r;
    };

    private SimpleJdbcCall call(String procedureName) {
        return new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(dbName)
                .withProcedureName(procedureName);
    }

    public Map<String, Object> borrowBook(Long userId, String isbn) {
        SimpleJdbcCall jdbcCall = call("sp_borrow_book")
                .declareParameters(
                        new SqlParameter("p_user_id",         Types.BIGINT),
                        new SqlParameter("p_isbn",            Types.VARCHAR),
                        new SqlOutParameter("p_result",       Types.INTEGER),
                        new SqlOutParameter("p_message",      Types.VARCHAR),
                        new SqlOutParameter("p_inventory_id", Types.BIGINT)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", userId);
        params.put("p_isbn",    isbn);
        return jdbcCall.execute(params);
    }

    public Map<String, Object> returnBook(Long userId, Long inventoryId) {
        SimpleJdbcCall jdbcCall = call("sp_return_book")
                .declareParameters(
                        new SqlParameter("p_user_id",      Types.BIGINT),
                        new SqlParameter("p_inventory_id", Types.BIGINT),
                        new SqlOutParameter("p_result",    Types.INTEGER),
                        new SqlOutParameter("p_message",   Types.VARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id",      userId);
        params.put("p_inventory_id", inventoryId);
        return jdbcCall.execute(params);
    }

    public List<BorrowRecordResponse> findByUserId(Long userId) {
        SimpleJdbcCall jdbcCall = call("sp_get_user_borrows")
                .declareParameters(new SqlParameter("p_user_id", Types.BIGINT))
                .returningResultSet("records", recordRowMapper);

        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", userId);

        Map<String, Object> out = jdbcCall.execute(params);
        @SuppressWarnings("unchecked")
        List<BorrowRecordResponse> records = (List<BorrowRecordResponse>) out.get("records");
        return records;
    }
}