package com.library.model;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String role;

    public User() {}

    public User(String userId, String name, String email, String phone, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return String.format("User ID: %-10s | Name: %-20s | Email: %-25s | Phone: %-15s | Role: %s",
                userId, name, email, phone, role);
    }
}
