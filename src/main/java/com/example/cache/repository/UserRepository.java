package com.example.cache.repository;

import com.example.cache.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findById(long id) {
        return jdbcTemplate.queryForObject(
                """
                SELECT id, name, email
                FROM users
                WHERE id = ?
                """,
                (rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                ),
                id
        );
    }

    public void updateName(long id, String name) {
        jdbcTemplate.update(
                """
                UPDATE users
                SET name = ?
                WHERE id = ?
                """,
                name,
                id
        );
    }

}
