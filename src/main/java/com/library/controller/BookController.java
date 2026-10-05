package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Book CRUD and Search operations.
 * Delegates all logic to BookService — no HBase code here.
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    public BookController() {
        this.bookService = new BookService();
    }

    // ─── GET /api/books ──────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    // ─── GET /api/books/{bookId} ──────────────────────────────────────────────
    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBook(@PathVariable String bookId) {
        Optional<Book> book = bookService.getBook(bookId);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Book not found: " + bookId));
    }

    // ─── POST /api/books ─────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Map<String, Object> body) {
        String bookId   = getString(body, "bookId");
        String title    = getString(body, "title");
        String author   = getString(body, "author");
        String category = getString(body, "category");
        double price    = getDouble(body, "price");

        if (bookId == null || title == null || author == null || category == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "bookId, title, author, and category are required."));
        }

        // Check duplicate before calling service (service returns false for duplicate too,
        // but we need 409 vs 400 distinction)
        Optional<Book> existing = bookService.getBook(bookId);
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Book already exists: " + bookId));
        }

        boolean created = bookService.addBook(bookId, title, author, category, price);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Book created successfully.", "bookId", bookId));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create book. Check server logs."));
    }

    // ─── PUT /api/books/{bookId} ──────────────────────────────────────────────
    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateBook(@PathVariable String bookId,
                                        @RequestBody Map<String, Object> body) {
        Optional<Book> existing = bookService.getBook(bookId);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Book not found: " + bookId));
        }

        Book current = existing.get();
        String title    = body.containsKey("title")    ? getString(body, "title")    : current.getTitle();
        String author   = body.containsKey("author")   ? getString(body, "author")   : current.getAuthor();
        String category = body.containsKey("category") ? getString(body, "category") : current.getCategory();
        double price    = body.containsKey("price")    ? getDouble(body, "price")    : current.getPrice();
        boolean available = body.containsKey("available")
                ? Boolean.parseBoolean(String.valueOf(body.get("available")))
                : current.isAvailable();

        boolean updated = bookService.updateBook(bookId, title, author, category, price, available);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Book updated successfully.", "bookId", bookId));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update book. Check server logs."));
    }

    // ─── DELETE /api/books/{bookId} ───────────────────────────────────────────
    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable String bookId) {
        Optional<Book> existing = bookService.getBook(bookId);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Book not found: " + bookId));
        }

        boolean deleted = bookService.deleteBook(bookId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully.", "bookId", bookId));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete book. Check server logs."));
    }

    // ─── Search endpoints ──────────────────────────────────────────────────────
    @GetMapping("/search/title/{title}")
    public ResponseEntity<List<Book>> searchByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.searchBooks("title", title));
    }

    @GetMapping("/search/author/{author}")
    public ResponseEntity<List<Book>> searchByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.searchBooks("author", author));
    }

    @GetMapping("/search/category/{category}")
    public ResponseEntity<List<Book>> searchByCategory(@PathVariable String category) {
        return ResponseEntity.ok(bookService.searchBooks("category", category));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String getString(Map<String, Object> body, String key) {
        Object val = body.get(key);
        return (val != null && !val.toString().isBlank()) ? val.toString().trim() : null;
    }

    private double getDouble(Map<String, Object> body, String key) {
        Object val = body.get(key);
        if (val == null) return 0.0;
        try { return Double.parseDouble(val.toString()); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
