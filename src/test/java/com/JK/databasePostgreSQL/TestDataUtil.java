package com.JK.databasePostgreSQL;

import com.JK.databasePostgreSQL.domain.Author;
import com.JK.databasePostgreSQL.domain.Book;
import com.JK.databasePostgreSQL.domain.BookModelForService;

public final class TestDataUtil {

    public TestDataUtil() {
    }

    public static Author createTestAuthor() {
        return Author.builder()
                .id(1L)
                .name("Thomas")
                .age(20)
                .build();
    }
    public static Author createTestAuthorB() {
        return Author.builder()
                .id(2L)
                .name("Kevin")
                .age(30)
                .build();
    }
    public static Author createTestAuthorC() {
        return Author.builder()
                .id(3L)
                .name("John")
                .age(10)
                .build();
    }

    public static Book createTestBook() {
        return Book.builder()
                .isbn("99921-58-10-7")
                .title("Walk in humble")
                .author(createTestAuthor())
                .build();
    }
    public static Book createTestBookB() {
        return Book.builder()
                .isbn("44212-23-20-0")
                .title("Brotherhood")
                .author(createTestAuthorB())
                .build();
    }
    public static Book createTestBookC() {
        return Book.builder()
                .isbn("00121-53-10-1")
                .title("Love is abstract")
                .author(createTestAuthorC())
                .build();
    }

    public static BookModelForService createTestBookD() {
        return BookModelForService.builder()
                .isbn("00121-53-10-1")
                .title("Love is abstract")
                .authorId(3L)
                .build();
    }
}
