package com.library.controller;

import com.library.model.LibraryTransaction;
import com.library.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for borrow/return/transaction operations.
 * Delegates all business logic to LibraryService.
 */
@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController() {
        this.libraryService = new LibraryService();
    }

    // ─── POST /api/library/borrow ─────────────────────────────────────────────
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, Object> body) {
        String transactionId = getString(body, "transactionId");
        String userId        = getString(body, "userId");
        String bookId        = getString(body, "bookId");

        if (transactionId == null || userId == null || bookId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "transactionId, userId, and bookId are required."));
        }

        boolean success = libraryService.borrowBook(transactionId, userId, bookId);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Book borrowed successfully.",
                            "transactionId", transactionId,
                            "userId", userId,
                            "bookId", bookId
                    ));
        }
        // Service prints the exact reason — could be 404 (not found), 409 (already borrowed), etc.
        // We return 400 to indicate the request could not be fulfilled as-is.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Borrow failed. Book may not exist, be unavailable, or transaction ID is duplicate."));
    }

    // ─── POST /api/library/return ─────────────────────────────────────────────
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody Map<String, Object> body) {
        String transactionId = getString(body, "transactionId");

        if (transactionId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "transactionId is required."));
        }

        // Check transaction exists first
        Optional<LibraryTransaction> txnOpt = libraryService.getTransaction(transactionId);
        if (txnOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Transaction not found: " + transactionId));
        }

        if ("RETURNED".equalsIgnoreCase(txnOpt.get().getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Book already returned for transaction: " + transactionId));
        }

        boolean success = libraryService.returnBook(transactionId);
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "message", "Book returned successfully.",
                    "transactionId", transactionId
            ));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Return failed. Check server logs."));
    }

    // ─── GET /api/library/transactions ───────────────────────────────────────
    @GetMapping("/transactions")
    public ResponseEntity<List<LibraryTransaction>> getAllTransactions() {
        return ResponseEntity.ok(libraryService.getAllTransactions());
    }

    // ─── GET /api/library/transactions/{transactionId} ───────────────────────
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<?> getTransaction(@PathVariable String transactionId) {
        Optional<LibraryTransaction> txn = libraryService.getTransaction(transactionId);
        if (txn.isPresent()) {
            return ResponseEntity.ok(txn.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Transaction not found: " + transactionId));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String getString(Map<String, Object> body, String key) {
        Object val = body.get(key);
        return (val != null && !val.toString().isBlank()) ? val.toString().trim() : null;
    }
}
