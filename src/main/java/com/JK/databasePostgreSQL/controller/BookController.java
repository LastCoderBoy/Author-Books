package com.JK.databasePostgreSQL.controller;

import com.JK.databasePostgreSQL.dao.impl.BookDaoImpl;
import com.JK.databasePostgreSQL.domain.Book;
import com.JK.databasePostgreSQL.domain.BookModelForService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
public class BookController {

    @Autowired
    private final BookDaoImpl bookService;

    @PostMapping("/create")
    public ResponseEntity<String> createBook(@RequestBody BookModelForService book) {
        bookService.create(book);
        return ResponseEntity.ok("Book created successfully!");
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBook(@PathVariable String isbn) {
        Optional<Book> book = bookService.readOne(isbn);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.readAll();
        return ResponseEntity.ok(books);
    }

    @PutMapping("/update/{isbn}")
    public ResponseEntity<String> updateBook(@PathVariable String isbn, @RequestBody BookModelForService bookUpdateRequest) {
        bookService.update(isbn, bookUpdateRequest);
        return ResponseEntity.ok("Book updated successfully!");
    }

    @DeleteMapping("/delete/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable String isbn) {
        bookService.delete(isbn);
        return ResponseEntity.ok("Book deleted successfully!");
    }
}
