package com.JK.databasePostgreSQL.controller;

import com.JK.databasePostgreSQL.dao.impl.AuthorDaoImpl;
import com.JK.databasePostgreSQL.domain.Author;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/author")
public class AuthorController {

    @Autowired
    private final AuthorDaoImpl authorService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody Author author){
        authorService.create(author);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Author>> getAuthor(@PathVariable Long id) {
        Optional<Author> author = authorService.readOne(id);
        return author.isPresent()
                ? ResponseEntity.ok(author)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> authors = authorService.readAll();
        return ResponseEntity.ok(authors);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAuthor(@PathVariable Long id, @RequestBody Author newAuthor) {
        authorService.update(id, newAuthor);
        return ResponseEntity.ok("Author updated successfully!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.ok("Author deleted successfully!");
    }
}
