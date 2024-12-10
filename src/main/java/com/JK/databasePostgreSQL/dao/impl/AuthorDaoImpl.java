package com.JK.databasePostgreSQL.dao.impl;

import com.JK.databasePostgreSQL.dao.AuthorDao;
import com.JK.databasePostgreSQL.domain.Author;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AuthorDaoImpl implements AuthorDao {

    private final JdbcTemplate jdbcTemplate;
    public AuthorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Author author) {
        jdbcTemplate.update(
                "INSERT INTO authors (name, age) VALUES (?, ?)",
                    author.getName(), author.getAge());
    }

    @Override
    public Optional<Author> readOne(Long authorID) {
        List<Author> authorList = jdbcTemplate.query(
                "SELECT * FROM authors WHERE id = ?",
                new AuthorRowMapper(), authorID);
        return authorList.stream().findFirst();
    }

    @Override
    public List<Author> readAll() {
        List<Author> authorList = jdbcTemplate.query(
                "SELECT * FROM authors",
                new AuthorRowMapper());
        return authorList;
    }

    @Override
    public void update(Long id, Author author) {
        boolean authorExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)",
                Boolean.class,
                id
        );
        if (!authorExists) {
            throw new IllegalArgumentException("Author with ID " + id + " does not exist");
        }
        StringBuilder queryBuilder = new StringBuilder("UPDATE authors SET ");
        List<Object> params = new ArrayList<>();

        if (author.getName() != null) {
            queryBuilder.append("name = ?, ");
            params.add(author.getName());
        }
        if (author.getAge() != null) {
            queryBuilder.append("age = ?, ");
            params.add(author.getAge());
        }
        if (author.getId() != null) {
            queryBuilder.append("id = ?, ");
            params.add(author.getId());
        }

        queryBuilder.setLength(queryBuilder.length() - 2);

        queryBuilder.append(" WHERE id = ?");
        params.add(id);

        jdbcTemplate.update(queryBuilder.toString(), params.toArray());
    }


    @Override
    public void delete(Long id) {
        boolean authorExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)",
                Boolean.class,
                id
        );
        if (!authorExists) {
            throw new IllegalArgumentException("Author with ID " + id + " does not exist");
        }
        jdbcTemplate.update(
                "DELETE FROM authors WHERE id=?",
                id
        );
    }

    public static class AuthorRowMapper implements RowMapper<Author>{

        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            return
                    Author.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .age(rs.getInt("age"))
                            .build();
        }
    }
}
