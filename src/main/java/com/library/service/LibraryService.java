package com.library.service;

import com.library.model.Book;
import com.library.model.LibraryTransaction;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.repository.UserRepository;
import com.library.util.ValidationUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service managing borrow/return transaction business logic.
 */
public class LibraryService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LibraryService() {
        this.bookRepository = new BookRepository();
        this.userRepository = new UserRepository();
        this.transactionRepository = new TransactionRepository();
    }

    public LibraryService(BookRepository bookRepo, UserRepository userRepo, TransactionRepository txnRepo) {
        this.bookRepository = bookRepo;
        this.userRepository = userRepo;
        this.transactionRepository = txnRepo;
    }

    /**
     * Borrow a book for a user.
     */
    public boolean borrowBook(String transactionId, String userId, String bookId) {
        if (!ValidationUtil.isValidString(transactionId)) {
            System.err.println("[VALIDATION ERROR] Transaction ID cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isValidString(userId)) {
            System.err.println("[VALIDATION ERROR] User ID cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isValidString(bookId)) {
            System.err.println("[VALIDATION ERROR] Book ID cannot be empty.");
            return false;
        }

        try {
            // 1. Transaction ID uniqueness
            if (transactionRepository.exists(transactionId.trim())) {
                System.err.println("[ERROR] Duplicate Transaction ID: '" + transactionId + "' already exists.");
                return false;
            }

            // 2. User existence
            Optional<User> userOpt = userRepository.findById(userId.trim());
            if (userOpt.isEmpty()) {
                System.err.println("[ERROR] Borrow failed: User '" + userId + "' does not exist.");
                return false;
            }

            // 3. Book existence and availability
            Optional<Book> bookOpt = bookRepository.findById(bookId.trim());
            if (bookOpt.isEmpty()) {
                System.err.println("[ERROR] Borrow failed: Book '" + bookId + "' does not exist.");
                return false;
            }

            Book book = bookOpt.get();
            if (!book.isAvailable()) {
                System.err.println("[ERROR] Borrow failed: Book '" + bookId + "' (" + book.getTitle() + ") is currently UNAVAILABLE.");
                return false;
            }

            // 4. Update book availability -> false
            book.setAvailable(false);
            bookRepository.save(book);

            // 5. Save transaction
            String nowStr = LocalDateTime.now().format(DATE_FORMATTER);
            LibraryTransaction txn = new LibraryTransaction(
                    transactionId.trim(),
                    userId.trim(),
                    bookId.trim(),
                    nowStr,
                    "N/A",
                    "BORROWED"
            );
            transactionRepository.save(txn);

            System.out.println("[SUCCESS] Book '" + bookId + "' borrowed successfully by User '" + userId + "'. Transaction ID: " + transactionId);
            return true;

        } catch (IOException e) {
            System.err.println("[ERROR] Database failure during borrow operation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return a borrowed book.
     */
    public boolean returnBook(String transactionId) {
        if (!ValidationUtil.isValidString(transactionId)) {
            System.err.println("[VALIDATION ERROR] Transaction ID cannot be empty.");
            return false;
        }

        try {
            Optional<LibraryTransaction> txnOpt = transactionRepository.findById(transactionId.trim());
            if (txnOpt.isEmpty()) {
                System.err.println("[ERROR] Return failed: Transaction '" + transactionId + "' does not exist.");
                return false;
            }

            LibraryTransaction txn = txnOpt.get();
            if ("RETURNED".equalsIgnoreCase(txn.getStatus())) {
                System.err.println("[ERROR] Return failed: Transaction '" + transactionId + "' has ALREADY been returned.");
                return false;
            }

            // 1. Update Transaction status -> RETURNED, returnDate -> now
            String nowStr = LocalDateTime.now().format(DATE_FORMATTER);
            txn.setStatus("RETURNED");
            txn.setReturnDate(nowStr);
            transactionRepository.save(txn);

            // 2. Update Book availability -> true
            Optional<Book> bookOpt = bookRepository.findById(txn.getBookId());
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                book.setAvailable(true);
                bookRepository.save(book);
            }

            System.out.println("[SUCCESS] Book '" + txn.getBookId() + "' returned successfully. Transaction '" + transactionId + "' updated to RETURNED.");
            return true;

        } catch (IOException e) {
            System.err.println("[ERROR] Database failure during return operation: " + e.getMessage());
            return false;
        }
    }

    public Optional<LibraryTransaction> getTransaction(String transactionId) {
        if (!ValidationUtil.isValidString(transactionId)) {
            System.err.println("[VALIDATION ERROR] Transaction ID cannot be empty.");
            return Optional.empty();
        }

        try {
            Optional<LibraryTransaction> txnOpt = transactionRepository.findById(transactionId.trim());
            if (txnOpt.isEmpty()) {
                System.out.println("[INFO] Transaction '" + transactionId + "' not found.");
            }
            return txnOpt;
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while reading transaction: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<LibraryTransaction> getAllTransactions() {
        try {
            return transactionRepository.findAll();
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while scanning transactions: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
