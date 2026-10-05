package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.util.ValidationUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service handling business operations and validations for Books.
 */
public class BookService {

    private final BookRepository bookRepository;

    public BookService() {
        this.bookRepository = new BookRepository();
    }

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public boolean addBook(String bookId, String title, String author, String category, double price) {
        if (!ValidationUtil.isValidString(bookId)) {
            System.err.println("[VALIDATION ERROR] Book ID cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isValidString(title)) {
            System.err.println("[VALIDATION ERROR] Title cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isValidString(author)) {
            System.err.println("[VALIDATION ERROR] Author cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isValidString(category)) {
            System.err.println("[VALIDATION ERROR] Category cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isValidPrice(price)) {
            System.err.println("[VALIDATION ERROR] Price cannot be negative.");
            return false;
        }

        try {
            if (bookRepository.exists(bookId.trim())) {
                System.err.println("[ERROR] Duplicate Book ID: Book '" + bookId + "' already exists.");
                return false;
            }

            Book book = new Book(bookId.trim(), title.trim(), author.trim(), category.trim(), price, true);
            bookRepository.save(book);
            System.out.println("[SUCCESS] Book '" + bookId + "' added successfully.");
            return true;

        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while adding book: " + e.getMessage());
            return false;
        }
    }

    public Optional<Book> getBook(String bookId) {
        if (!ValidationUtil.isValidString(bookId)) {
            System.err.println("[VALIDATION ERROR] Book ID cannot be empty.");
            return Optional.empty();
        }

        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId.trim());
            if (bookOpt.isEmpty()) {
                System.out.println("[INFO] Book '" + bookId + "' not found.");
            }
            return bookOpt;
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while reading book: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean updateBook(String bookId, String title, String author, String category, double price, boolean available) {
        if (!ValidationUtil.isValidString(bookId)) {
            System.err.println("[VALIDATION ERROR] Book ID cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isValidPrice(price)) {
            System.err.println("[VALIDATION ERROR] Price cannot be negative.");
            return false;
        }

        try {
            Optional<Book> existingOpt = bookRepository.findById(bookId.trim());
            if (existingOpt.isEmpty()) {
                System.err.println("[ERROR] Cannot update: Book '" + bookId + "' does not exist.");
                return false;
            }

            Book book = existingOpt.get();
            if (ValidationUtil.isValidString(title)) book.setTitle(title.trim());
            if (ValidationUtil.isValidString(author)) book.setAuthor(author.trim());
            if (ValidationUtil.isValidString(category)) book.setCategory(category.trim());
            book.setPrice(price);
            book.setAvailable(available);

            bookRepository.save(book);
            System.out.println("[SUCCESS] Book '" + bookId + "' updated successfully.");
            return true;

        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(String bookId) {
        if (!ValidationUtil.isValidString(bookId)) {
            System.err.println("[VALIDATION ERROR] Book ID cannot be empty.");
            return false;
        }

        try {
            boolean deleted = bookRepository.deleteById(bookId.trim());
            if (deleted) {
                System.out.println("[SUCCESS] Book '" + bookId + "' deleted successfully.");
            } else {
                System.err.println("[ERROR] Cannot delete: Book '" + bookId + "' does not exist.");
            }
            return deleted;
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while deleting book: " + e.getMessage());
            return false;
        }
    }

    public List<Book> getAllBooks() {
        try {
            return bookRepository.findAll();
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while scanning books: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Book> searchBooks(String fieldName, String keyword) {
        if (!ValidationUtil.isValidString(keyword)) {
            System.err.println("[VALIDATION ERROR] Search keyword cannot be empty.");
            return Collections.emptyList();
        }

        try {
            return bookRepository.search(fieldName, keyword);
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure during book search: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
