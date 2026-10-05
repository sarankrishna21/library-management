package com.library.model;

public class LibraryTransaction {
    private String transactionId;
    private String userId;
    private String bookId;
    private String borrowDate;
    private String returnDate;
    private String status;

    public LibraryTransaction() {}

    public LibraryTransaction(String transactionId, String userId, String bookId, String borrowDate, String returnDate, String status) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Txn ID: %-12s | User: %-8s | Book: %-8s | Borrow Date: %-19s | Return Date: %-19s | Status: %s",
                transactionId, userId, bookId, borrowDate, returnDate, status);
    }
}
