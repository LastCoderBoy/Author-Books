package com.JK.databasePostgreSQL.dao.impl;

import com.JK.databasePostgreSQL.dao.BookDao;
import com.JK.databasePostgreSQL.domain.Author;
import com.JK.databasePostgreSQL.domain.Book;
import com.JK.databasePostgreSQL.domain.BookModelForService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BookDaoImpl implements BookDao {

    private final JdbcTemplate jdbcTemplate;
    public BookDaoImpl (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(BookModelForService book) {
        boolean authorExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)",
                Boolean.class,
                book.getAuthorId()
        );
        if (!authorExists) {
            throw new IllegalArgumentException("Author with ID " + book.getAuthorId() + " does not exist");
        }
        jdbcTemplate.update(
                "INSERT INTO author_books (isbn, title, author_id) VALUES (?, ?, ?)",
                book.getIsbn(),
                book.getTitle(),
                book.getAuthorId()
        );
    }

    @Override
    public Optional<Book> readOne(String isbn) {
        try {
            Book book = jdbcTemplate.queryForObject(
                    "SELECT * FROM author_books WHERE isbn = ?",
                    new BookRowMapper(),
                    isbn
            );
            return Optional.ofNullable(book);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> readAll() {
        List<Book> bookList = jdbcTemplate.query(
                "SELECT * FROM author_books",
                new BookRowMapper());
        return bookList.stream().toList();
    }

    @Override
    public void update(String isbn, BookModelForService bookUpdateRequest) {
        // Check if the original book exists
        boolean bookExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM author_books WHERE isbn = ?)",
                Boolean.class,
                isbn
        );
        if (!bookExists) {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " does not exist");
        }

        // Prepare SQL query for the update
        StringBuilder updateQuery = new StringBuilder("UPDATE author_books SET ");
        List<Object> params = new ArrayList<>();

        // Conditionally update fields
        if (bookUpdateRequest.getIsbn() != null) {
            updateQuery.append("isbn = ?, ");
            params.add(bookUpdateRequest.getIsbn());
        }
        if (bookUpdateRequest.getTitle() != null) {
            updateQuery.append("title = ?, ");
            params.add(bookUpdateRequest.getTitle());
        }
        if (bookUpdateRequest.getAuthorId() != null) {
            // Check if the author exists by the ID before updating
            boolean authorExists = jdbcTemplate.queryForObject(
                    "SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)",
                    Boolean.class,
                    bookUpdateRequest.getAuthorId()
            );
            if (!authorExists) {
                throw new IllegalArgumentException("Author with ID " + bookUpdateRequest.getAuthorId() + " does not exist");
            }
            updateQuery.append("author_id = ?, ");
            params.add(bookUpdateRequest.getAuthorId());
        }

        // Remove the last comma and space
        updateQuery.setLength(updateQuery.length() - 2);
        updateQuery.append(" WHERE isbn = ?");
        params.add(isbn);

        jdbcTemplate.update(updateQuery.toString(), params.toArray());
    }


    @Override
    public void delete(String isbn) {
        int deletedRows = jdbcTemplate.update(
                "DELETE FROM author_books WHERE isbn = ?",
                isbn
        );
        if (deletedRows == 0) {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " does not exist");
        }
    }

    public static class BookRowMapper implements RowMapper<Book>{

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = Author.builder()
                    .id(rs.getLong("author_id"))
                    .build();

            return Book.builder()
                    .isbn(rs.getString("isbn"))
                    .title(rs.getString("title"))
                    .author(author)
                    .build();
        }
    }
}
