package com.library.test;

import com.library.model.Book;
import com.library.model.LibraryTransaction;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.LibraryService;
import com.library.service.UserService;

import java.util.List;
import java.util.Optional;

/**
 * End-to-End Automated Test Suite performing all 15 operational steps of Phase 14.
 */
public class EndToEndTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   PHASE 14 — COMPLETE END-TO-END SYSTEM TEST");
        System.out.println("==================================================");

        BookService bookService = new BookService();
        UserService userService = new UserService();
        LibraryService libraryService = new LibraryService();

        // 1. Add book
        System.out.println("\n[STEP 1] Add Book BOOK100...");
        boolean addBookOk = bookService.addBook("BOOK100", "Design Patterns", "Erich Gamma", "Technology", 599.0);

        // 2. Get book
        System.out.println("\n[STEP 2] Get Book BOOK100...");
        Optional<Book> book100Opt = bookService.getBook("BOOK100");
        book100Opt.ifPresent(System.out::println);

        // 3. Update book
        System.out.println("\n[STEP 3] Update Book BOOK100 price to 650.0...");
        boolean updateBookOk = bookService.updateBook("BOOK100", "Design Patterns", "Erich Gamma", "Technology", 650.0, true);

        // 4. Search book
        System.out.println("\n[STEP 4] Search Book by title 'Design'...");
        List<Book> searchResults = bookService.searchBooks("title", "Design");

        // 5. Add user
        System.out.println("\n[STEP 5] Add User USR100...");
        boolean addUserOk = userService.addUser("USR100", "Charlie Brown", "charlie@test.com", "9998887770", "Student");

        // 6. Get user
        System.out.println("\n[STEP 6] Get User USR100...");
        Optional<User> user100Opt = userService.getUser("USR100");
        user100Opt.ifPresent(System.out::println);

        // 7. Borrow book
        System.out.println("\n[STEP 7] Borrow Book BOOK100 by User USR100 (Txn TXN100)...");
        boolean borrowOk = libraryService.borrowBook("TXN100", "USR100", "BOOK100");

        // 8. Verify book becomes unavailable
        System.out.println("\n[STEP 8] Verify BOOK100 is unavailable...");
        Optional<Book> borrowedBookOpt = bookService.getBook("BOOK100");
        boolean isUnavailable = borrowedBookOpt.isPresent() && !borrowedBookOpt.get().isAvailable();
        System.out.println("Availability status: " + (isUnavailable ? "UNAVAILABLE (Correct)" : "AVAILABLE (Error)"));

        // 9. View transaction
        System.out.println("\n[STEP 9] View Transaction TXN100...");
        Optional<LibraryTransaction> txn100Opt = libraryService.getTransaction("TXN100");
        txn100Opt.ifPresent(System.out::println);

        // 10. Return book
        System.out.println("\n[STEP 10] Return Book using TXN100...");
        boolean returnOk = libraryService.returnBook("TXN100");

        // 11. Verify book becomes available again
        System.out.println("\n[STEP 11] Verify BOOK100 is available again...");
        Optional<Book> returnedBookOpt = bookService.getBook("BOOK100");
        boolean isAvailableAgain = returnedBookOpt.isPresent() && returnedBookOpt.get().isAvailable();
        System.out.println("Availability status: " + (isAvailableAgain ? "AVAILABLE (Correct)" : "UNAVAILABLE (Error)"));

        // 12. Delete test book
        System.out.println("\n[STEP 12] Delete test book BOOK100...");
        boolean deleteOk = bookService.deleteBook("BOOK100");

        // 13. List books
        System.out.println("\n[STEP 13] List all books...");
        List<Book> allBooks = bookService.getAllBooks();
        allBooks.forEach(System.out::println);

        // 14. List users
        System.out.println("\n[STEP 14] List all users...");
        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(System.out::println);

        // 15. List transactions
        System.out.println("\n[STEP 15] List all transactions...");
        List<LibraryTransaction> allTxns = libraryService.getAllTransactions();
        allTxns.forEach(System.out::println);

        System.out.println("\n==================================================");
        System.out.println(" END-TO-END TEST VERIFICATION SUMMARY:");
        System.out.println(" - Add Book OK         : " + addBookOk);
        System.out.println(" - Update Book OK      : " + updateBookOk);
        System.out.println(" - Search Match OK     : " + (!searchResults.isEmpty()));
        System.out.println(" - Add User OK         : " + addUserOk);
        System.out.println(" - Borrow Workflow OK  : " + (borrowOk && isUnavailable));
        System.out.println(" - Return Workflow OK  : " + (returnOk && isAvailableAgain));
        System.out.println(" - Delete Book OK      : " + deleteOk);
        System.out.println("==================================================");
    }
}
