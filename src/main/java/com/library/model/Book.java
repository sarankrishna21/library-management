package com.library.model;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private double price;
    private boolean available;

    public Book() {}

    public Book(String bookId, String title, String author, String category, double price, boolean available) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("Book ID: %-10s | Title: %-32s | Author: %-20s | Category: %-12s | Price: ₹%-6.2f | Available: %s",
                bookId, title, author, category, price, available);
    }
}
