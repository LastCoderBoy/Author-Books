package com.JK.databasePostgreSQL.dao.impl;

import com.JK.databasePostgreSQL.TestDataUtil;
import com.JK.databasePostgreSQL.dao.impl.AuthorDaoImpl.AuthorRowMapper;
import com.JK.databasePostgreSQL.domain.Author;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
public class AuthorDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AuthorDaoImpl underTest;

    @Test
    public void testTheAuthorCREATEMethod(){
        //Creating an Author Object
        Author author = TestDataUtil.createTestAuthor();

        //Testing the method
        underTest.create(author);

        //Verifying the method
        verify(jdbcTemplate).update(
                eq("INSERT INTO authors (name, age) VALUES (?, ?)"),
                eq("Thomas"), eq(20));

        verify(jdbcTemplate).update(eq("INSERT INTO authors (name, age) VALUES (?, ?)"), isA(Object[].class));

    }


    @Test
    public void testTheAuthorREADMethod(){
        //Given
        Author author = TestDataUtil.createTestAuthorC();
        underTest.create(author);

        when(jdbcTemplate.query(eq("SELECT * FROM authors WHERE id = ?"),
                any(AuthorRowMapper.class),
                eq(author.getId())))
                .thenReturn(List.of(author));

        //Test the method
        Optional<Author> resultAuthor = underTest.readOne(3L);

        //Assertions
        verify(jdbcTemplate).query(
                eq("SELECT * FROM authors WHERE id = ?"),
                ArgumentMatchers.<AuthorRowMapper>any(),
                eq(3L));
        assertTrue(resultAuthor.isPresent());
        assertThat(resultAuthor.get()).isEqualTo(author);
    }

    @Test
    public void testThatAuthorReadAllMethod(){
        //Given
        List<Author> authorList = new ArrayList<>();
        Author author = TestDataUtil.createTestAuthor();
        Author authorB = TestDataUtil.createTestAuthorB();
        Author authorC = TestDataUtil.createTestAuthorC();
        authorList.add(author);
        authorList.add(authorB);
        authorList.add(authorC);

        when(jdbcTemplate.query(eq("SELECT * FROM authors"), Mockito.<RowMapper<Author>>any()))
                .thenReturn(authorList);

        //Test the method
        List<Author> resultAuthorList = underTest.readAll();

        //Assertions
        verify(jdbcTemplate).query(
                eq("SELECT * FROM authors"),
                ArgumentMatchers.<AuthorRowMapper>any());
        assertEquals(authorList,resultAuthorList);
        assertEquals(3, resultAuthorList.size());
        assertThat(resultAuthorList.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testTheAuthorUpdateMethod(){
        // Given
        Long authorId = 1L;
        Author existingAuthor = TestDataUtil.createTestAuthor();
        Author updatedAuthor = new Author(null, "Mark", 35);

        // Mocking
        when(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)",
                Boolean.class,
                authorId
        )).thenReturn(true);

        when(jdbcTemplate.update(
                anyString(),
                isA(Object[].class)
        )).thenReturn(1);

        // Testing the update method
        underTest.update(authorId, updatedAuthor);

        // Assertions
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> paramsCaptor = ArgumentCaptor.forClass(Object[].class);

        verify(jdbcTemplate).update(queryCaptor.capture(), paramsCaptor.capture());

        String expectedQuery = "UPDATE authors SET name = ?, age = ? WHERE id = ?";
        assertEquals(expectedQuery, queryCaptor.getValue());

        Object[] expectedParams = {"Mark", 35, authorId};
        assertArrayEquals(expectedParams, paramsCaptor.getValue());
    }

    @Test
    public void testTheAuthorDeleteMethod(){
        // Given
        Long authorId = 1L;

        // Mocking
        when(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM authors WHERE id = ?)",
                Boolean.class,
                authorId
        )).thenReturn(true);

        when(jdbcTemplate.update(
                "DELETE FROM authors WHERE id=?",
                authorId
        )).thenReturn(1);

        // Testing the delete method
        underTest.delete(authorId);

        // Assertions
        verify(jdbcTemplate).update(
                "DELETE FROM authors WHERE id=?",
                authorId
        );
    }

}
