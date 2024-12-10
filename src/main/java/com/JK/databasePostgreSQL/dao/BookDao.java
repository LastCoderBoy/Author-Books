package com.JK.databasePostgreSQL.dao;

import com.JK.databasePostgreSQL.domain.Book;
import com.JK.databasePostgreSQL.domain.BookModelForService;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    void create(BookModelForService book);

    Optional<Book> readOne(String isbn);

    List<Book> readAll();

    void update(String isbn, BookModelForService book);

    void delete(String isbn);
}
