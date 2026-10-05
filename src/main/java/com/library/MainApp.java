package com.library;

import com.library.model.Book;
import com.library.model.LibraryTransaction;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.LibraryService;
import com.library.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main Console Application providing interactive CLI for Library Management System.
 */
public class MainApp {

    private final BookService bookService;
    private final UserService userService;
    private final LibraryService libraryService;

    public MainApp() {
        this.bookService = new BookService();
        this.userService = new UserService();
        this.libraryService = new LibraryService();
    }

    public static void main(String[] args) {
        MainApp app = new MainApp();
        app.runMenu();
    }

    public void runMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("==================================================");
        System.out.println("   WELCOME TO LIBRARY MANAGEMENT SYSTEM (HBase)");
        System.out.println("==================================================");

        while (!exit) {
            printMenu();
            System.out.print("\nEnter your choice (0-15): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleAddBook(scanner);
                    break;
                case "2":
                    handleGetBook(scanner);
                    break;
                case "3":
                    handleUpdateBook(scanner);
                    break;
                case "4":
                    handleDeleteBook(scanner);
                    break;
                case "5":
                    handleListBooks();
                    break;
                case "6":
                    handleSearchBooks(scanner);
                    break;
                case "7":
                    handleAddUser(scanner);
                    break;
                case "8":
                    handleGetUser(scanner);
                    break;
                case "9":
                    handleUpdateUser(scanner);
                    break;
                case "10":
                    handleDeleteUser(scanner);
                    break;
                case "11":
                    handleListUsers();
                    break;
                case "12":
                    handleBorrowBook(scanner);
                    break;
                case "13":
                    handleReturnBook(scanner);
                    break;
                case "14":
                    handleViewTransaction(scanner);
                    break;
                case "15":
                    handleListTransactions();
                    break;
                case "0":
                    exit = true;
                    System.out.println("\nThank you for using Library Management System. Goodbye!");
                    break;
                default:
                    System.out.println("⚠️ Invalid choice. Please enter a number between 0 and 15.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println(" 1. Add Book");
        System.out.println(" 2. Get Book");
        System.out.println(" 3. Update Book");
        System.out.println(" 4. Delete Book");
        System.out.println(" 5. List All Books");
        System.out.println(" 6. Search Books");
        System.out.println(" 7. Add User");
        System.out.println(" 8. Get User");
        System.out.println(" 9. Update User");
        System.out.println("10. Delete User");
        System.out.println("11. List All Users");
        System.out.println("12. Borrow Book");
        System.out.println("13. Return Book");
        System.out.println("14. View Transaction");
        System.out.println("15. List All Transactions");
        System.out.println(" 0. Exit");
        System.out.println("-------------------------------------------");
    }

    // --- Book Handlers ---
    private void handleAddBook(Scanner scanner) {
        System.out.println("\n--- [1] Add New Book ---");
        System.out.print("Enter Book ID (e.g. BOOK101): ");
        String id = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        System.out.print("Enter Price (₹): ");
        double price = parseDoubleOrDefault(scanner.nextLine(), -1.0);

        bookService.addBook(id, title, author, category, price);
    }

    private void handleGetBook(Scanner scanner) {
        System.out.println("\n--- [2] Get Book Details ---");
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine();

        Optional<Book> bookOpt = bookService.getBook(id);
        bookOpt.ifPresent(book -> System.out.println("\n" + book));
    }

    private void handleUpdateBook(Scanner scanner) {
        System.out.println("\n--- [3] Update Book ---");
        System.out.print("Enter Book ID to Update: ");
        String id = scanner.nextLine();
        System.out.print("Enter New Title (leave blank to keep unchanged): ");
        String title = scanner.nextLine();
        System.out.print("Enter New Author (leave blank to keep unchanged): ");
        String author = scanner.nextLine();
        System.out.print("Enter New Category (leave blank to keep unchanged): ");
        String category = scanner.nextLine();
        System.out.print("Enter New Price (leave blank to keep unchanged): ");
        String priceInput = scanner.nextLine();
        double price = priceInput.trim().isEmpty() ? 0.0 : parseDoubleOrDefault(priceInput, 0.0);
        System.out.print("Is Available? (true/false): ");
        boolean available = parseBooleanOrDefault(scanner.nextLine(), true);

        bookService.updateBook(id, title, author, category, price, available);
    }

    private void handleDeleteBook(Scanner scanner) {
        System.out.println("\n--- [4] Delete Book ---");
        System.out.print("Enter Book ID to Delete: ");
        String id = scanner.nextLine();
        bookService.deleteBook(id);
    }

    private void handleListBooks() {
        System.out.println("\n--- [5] List All Books ---");
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("[INFO] No books found.");
        } else {
            System.out.println("---------------------------------------------------------------------------------------------------------");
            books.forEach(System.out::println);
            System.out.println("---------------------------------------------------------------------------------------------------------");
            System.out.println("Total Books: " + books.size());
        }
    }

    private void handleSearchBooks(Scanner scanner) {
        System.out.println("\n--- [6] Search Books ---");
        System.out.println("Search By: 1. Title  2. Author  3. Category");
        System.out.print("Select Field (1-3): ");
        String fieldChoice = scanner.nextLine().trim();

        String field = "title";
        if ("2".equals(fieldChoice)) field = "author";
        else if ("3".equals(fieldChoice)) field = "category";

        System.out.print("Enter Search Keyword: ");
        String keyword = scanner.nextLine();

        List<Book> results = bookService.searchBooks(field, keyword);
        if (results.isEmpty()) {
            System.out.println("[INFO] No matching books found.");
        } else {
            System.out.println("\n--- Search Results (" + results.size() + " matches) ---");
            results.forEach(System.out::println);
        }
    }

    // --- User Handlers ---
    private void handleAddUser(Scanner scanner) {
        System.out.println("\n--- [7] Add New User ---");
        System.out.print("Enter User ID (e.g. USR101): ");
        String id = scanner.nextLine();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Role (Student/Faculty/Admin): ");
        String role = scanner.nextLine();

        userService.addUser(id, name, email, phone, role);
    }

    private void handleGetUser(Scanner scanner) {
        System.out.println("\n--- [8] Get User Details ---");
        System.out.print("Enter User ID: ");
        String id = scanner.nextLine();

        Optional<User> userOpt = userService.getUser(id);
        userOpt.ifPresent(user -> System.out.println("\n" + user));
    }

    private void handleUpdateUser(Scanner scanner) {
        System.out.println("\n--- [9] Update User ---");
        System.out.print("Enter User ID to Update: ");
        String id = scanner.nextLine();
        System.out.print("Enter New Name (leave blank to keep unchanged): ");
        String name = scanner.nextLine();
        System.out.print("Enter New Email (leave blank to keep unchanged): ");
        String email = scanner.nextLine();
        System.out.print("Enter New Phone (leave blank to keep unchanged): ");
        String phone = scanner.nextLine();
        System.out.print("Enter New Role (leave blank to keep unchanged): ");
        String role = scanner.nextLine();

        userService.updateUser(id, name, email, phone, role);
    }

    private void handleDeleteUser(Scanner scanner) {
        System.out.println("\n--- [10] Delete User ---");
        System.out.print("Enter User ID to Delete: ");
        String id = scanner.nextLine();
        userService.deleteUser(id);
    }

    private void handleListUsers() {
        System.out.println("\n--- [11] List All Users ---");
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("[INFO] No users found.");
        } else {
            System.out.println("----------------------------------------------------------------------------------------");
            users.forEach(System.out::println);
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println("Total Users: " + users.size());
        }
    }

    // --- Transaction Handlers ---
    private void handleBorrowBook(Scanner scanner) {
        System.out.println("\n--- [12] Borrow Book ---");
        System.out.print("Enter Transaction ID (e.g. TXN201): ");
        String txnId = scanner.nextLine();
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();

        libraryService.borrowBook(txnId, userId, bookId);
    }

    private void handleReturnBook(Scanner scanner) {
        System.out.println("\n--- [13] Return Book ---");
        System.out.print("Enter Transaction ID: ");
        String txnId = scanner.nextLine();

        libraryService.returnBook(txnId);
    }

    private void handleViewTransaction(Scanner scanner) {
        System.out.println("\n--- [14] View Transaction ---");
        System.out.print("Enter Transaction ID: ");
        String txnId = scanner.nextLine();

        Optional<LibraryTransaction> txnOpt = libraryService.getTransaction(txnId);
        txnOpt.ifPresent(txn -> System.out.println("\n" + txn));
    }

    private void handleListTransactions() {
        System.out.println("\n--- [15] List All Transactions ---");
        List<LibraryTransaction> txns = libraryService.getAllTransactions();
        if (txns.isEmpty()) {
            System.out.println("[INFO] No transactions found.");
        } else {
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            txns.forEach(System.out::println);
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            System.out.println("Total Transactions: " + txns.size());
        }
    }

    // --- Helper Utilities ---
    private double parseDoubleOrDefault(String input, double defaultValue) {
        try {
            return Double.parseDouble(input.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean parseBooleanOrDefault(String input, boolean defaultValue) {
        if (input == null || input.trim().isEmpty()) return defaultValue;
        return "true".equalsIgnoreCase(input.trim());
    }
}
