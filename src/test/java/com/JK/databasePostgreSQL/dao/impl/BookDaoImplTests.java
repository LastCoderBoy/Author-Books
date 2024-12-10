package com.JK.databasePostgreSQL.dao.impl;

import com.JK.databasePostgreSQL.TestDataUtil;
import com.JK.databasePostgreSQL.domain.Book;
import com.JK.databasePostgreSQL.domain.BookModelForService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BookDaoImpl underTest;

    @Test
    public void testTheBookCreateMethod(){
        //Given
        BookModelForService book = TestDataUtil.createTestBookD();
        when(jdbcTemplate.queryForObject(
                eq("SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)"),
                eq(Boolean.class),
                eq(3L)
        )).thenReturn(true);

        //Testing the method
        underTest.create(book);

        verify(jdbcTemplate).update(
                eq("INSERT INTO author_books (isbn, title, author_id) VALUES (?, ?, ?)"),
                eq("00121-53-10-1"),
                eq("Love is abstract"),
                eq(3L)
        );
    }

    @Test
    public void testTheBookReadMethod(){
        Book book = TestDataUtil.createTestBook();
        String testIsbn = "99921-58-10-7";

        // Mock the queryForObject to return the test book
        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM author_books WHERE isbn = ?"),
                isA(BookDaoImpl.BookRowMapper.class),
                eq(testIsbn)
        )).thenReturn(book);

        //Testing the method
        Optional<Book> resultBook = underTest.readOne(testIsbn);

        //Assertions
        verify(jdbcTemplate).queryForObject(
                eq("SELECT * FROM author_books WHERE isbn = ?"),
                isA(BookDaoImpl.BookRowMapper.class),
                eq(book.getIsbn())
        );
        assertThat(resultBook)
                .isPresent()
                .containsSame(book);
    }

    @Test
    public void testTheBooksReadAllMethod(){
        //Given
        List<Book> booksList = new ArrayList<>();
        Book book1 = TestDataUtil.createTestBook();
        Book book2 = TestDataUtil.createTestBookB();
        Book book3 = TestDataUtil.createTestBookC();
        booksList.add(book3);
        booksList.add(book2);
        booksList.add(book1);
        when(jdbcTemplate.query(
                eq("SELECT * FROM author_books"),
                any(BookDaoImpl.BookRowMapper.class)
        )).thenReturn(booksList);

        //Testing the method
        List<Book> resultBooksList = underTest.readAll();

        //Assertions
        verify(jdbcTemplate).query(
                eq("SELECT * FROM author_books"),
                ArgumentMatchers.<BookDaoImpl.BookRowMapper>any());
        assertThat(resultBooksList)
                .isNotEmpty()
                .hasSize(booksList.size())
                .containsExactlyElementsOf(booksList);
    }

    @Test
    public void testTheBookUpdateMethod_Successfull(){
        // Given
        Book oldBook = TestDataUtil.createTestBookB();
        BookModelForService newBook = TestDataUtil.createTestBookD();

        when(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM author_books WHERE isbn = ?)",
                Boolean.class,
                oldBook.getIsbn()
        )).thenReturn(true);

        when(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)",
                Boolean.class,
                newBook.getAuthorId()
        )).thenReturn(true);

        // Testing the method
        underTest.update(oldBook.getIsbn(), newBook);

        // Assertions
        verify(jdbcTemplate).update(
                eq("UPDATE author_books SET isbn = ?, title = ?, author_id = ? WHERE isbn = ?"),
                eq(newBook.getIsbn()),
                eq(newBook.getTitle()),
                eq(newBook.getAuthorId()),
                eq(oldBook.getIsbn())
        );
        if (newBook.getTitle() != null) {
            assertThat(newBook.getTitle()).isEqualTo("Love is abstract");
        }
        if (newBook.getAuthorId() != null) {
            assertThat(newBook.getAuthorId()).isEqualTo(3L);
        }
        if (newBook.getIsbn() != null) {
            assertThat(newBook.getIsbn()).isEqualTo("00121-53-10-1");
        }
    }

    @Test
    public void testUpdateMethod_BookDoesNotExist() {
        // Given
        BookModelForService newBook = TestDataUtil.createTestBookD();

        when(jdbcTemplate.queryForObject(
                eq("SELECT EXISTS(SELECT 1 FROM author_books WHERE isbn = ?)"),
                eq(Boolean.class),
                eq("invalid-isbn")
        )).thenReturn(false);

        // Testing the method
        assertThrows(IllegalArgumentException.class, () -> {
            underTest.update("invalid-isbn", newBook);
        });
    }

    @Test
    public void testUpdateMethod_AuthorDoesNotExist() {
        // Given
        Book oldBook = TestDataUtil.createTestBookB();
        BookModelForService newBook = TestDataUtil.createTestBookD();
        when(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM author_books WHERE isbn = ?)",
                Boolean.class,
                oldBook.getIsbn()
        )).thenReturn(true);
        when(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)",
                Boolean.class,
                newBook.getAuthorId()
        )).thenReturn(false);

        // Testing the method
        assertThrows(IllegalArgumentException.class, () -> {
            underTest.update(oldBook.getIsbn(), newBook);
        });
    }

    @Test
    public void testTheBookDeleteMethod(){
        //Given
        Book book = TestDataUtil.createTestBook();
        when(jdbcTemplate.update(
                eq("DELETE FROM author_books WHERE isbn = ?"),
                eq(book.getIsbn())
        )).thenReturn(1);
        //When (Testing the method)
        underTest.delete(book.getIsbn());

        //Then
        verify(jdbcTemplate).update(
                "DELETE FROM author_books WHERE isbn = ?",
                book.getIsbn());
    }

    @Test
    public void testTheBookDeleteMethod_Failure() {
        // Given
        String invalidIsbn = "invalid-isbn";

        when(jdbcTemplate.update(
                eq("DELETE FROM author_books WHERE isbn = ?"),
                eq(invalidIsbn)
        )).thenReturn(0);

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            //When (Testing the method)
            underTest.delete(invalidIsbn);
        });


        verify(jdbcTemplate).update(
                "DELETE FROM author_books WHERE isbn = ?",
                invalidIsbn
        );
    }


}
