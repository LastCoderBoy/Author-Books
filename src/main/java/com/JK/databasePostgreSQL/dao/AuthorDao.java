package com.JK.databasePostgreSQL.dao;

import com.JK.databasePostgreSQL.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    void create(Author author);

    Optional<Author> readOne (Long authorID);

    List<Author> readAll();

    void update(Long id, Author author);

    void delete(Long id);
}
