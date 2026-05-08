package com.example.newlibrarysystem.repository;

import com.example.newlibrarysystem.model.User;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${DB_NAME:library_system}")
    private String dbName;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setPassword(rs.getString("password"));
        user.setUserName(rs.getString("user_name"));
        user.setRegistrationTime(rs.getTimestamp("registration_time").toLocalDateTime());
        var lastLogin = rs.getTimestamp("last_login_time");
        user.setLastLoginTime(lastLogin != null ? lastLogin.toLocalDateTime() : null);
        return user;
    };

    private SimpleJdbcCall call(String procedureName) {
        return new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(dbName)
                .withProcedureName(procedureName);
    }

    public Map<String, Object> register(String phoneNumber, String hashedPassword, String userName) {
        SimpleJdbcCall jdbcCall = call("sp_register_user")
                .declareParameters(
                        new SqlParameter("p_phone_number", Types.VARCHAR),
                        new SqlParameter("p_password",     Types.VARCHAR),
                        new SqlParameter("p_user_name",    Types.VARCHAR),
                        new SqlOutParameter("p_user_id",   Types.BIGINT),
                        new SqlOutParameter("p_result",    Types.INTEGER),
                        new SqlOutParameter("p_message",   Types.VARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("p_phone_number", phoneNumber);
        params.put("p_password",     hashedPassword);
        params.put("p_user_name",    userName);
        return jdbcCall.execute(params);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        SimpleJdbcCall jdbcCall = call("sp_get_user_by_phone")
                .declareParameters(
                        new SqlParameter("p_phone_number", Types.VARCHAR)
                )
                .returningResultSet("result", userRowMapper);

        Map<String, Object> params = new HashMap<>();
        params.put("p_phone_number", phoneNumber);

        Map<String, Object> out = jdbcCall.execute(params);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) out.get("result");
        return (users != null && !users.isEmpty()) ? Optional.of(users.get(0)) : Optional.empty();
    }

    public void updateLastLogin(Long userId) {
        SimpleJdbcCall jdbcCall = call("sp_update_last_login")
                .declareParameters(new SqlParameter("p_user_id", Types.BIGINT));

        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", userId);
        jdbcCall.execute(params);
    }
}