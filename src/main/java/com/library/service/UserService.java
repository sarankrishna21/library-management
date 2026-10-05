package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.util.ValidationUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service handling business operations and validations for Users.
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean addUser(String userId, String name, String email, String phone, String role) {
        if (!ValidationUtil.isValidString(userId)) {
            System.err.println("[VALIDATION ERROR] User ID cannot be empty.");
            return false;
        }
        if (!ValidationUtil.isValidString(name)) {
            System.err.println("[VALIDATION ERROR] Name cannot be empty.");
            return false;
        }
        if (ValidationUtil.isValidString(email) && !ValidationUtil.isValidEmail(email)) {
            System.err.println("[VALIDATION ERROR] Invalid email format (e.g. user@domain.com).");
            return false;
        }

        try {
            if (userRepository.exists(userId.trim())) {
                System.err.println("[ERROR] Duplicate User ID: User '" + userId + "' already exists.");
                return false;
            }

            User user = new User(userId.trim(), name.trim(),
                    email != null ? email.trim() : "",
                    phone != null ? phone.trim() : "",
                    ValidationUtil.isValidString(role) ? role.trim() : "Student");

            userRepository.save(user);
            System.out.println("[SUCCESS] User '" + userId + "' added successfully.");
            return true;

        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while adding user: " + e.getMessage());
            return false;
        }
    }

    public Optional<User> getUser(String userId) {
        if (!ValidationUtil.isValidString(userId)) {
            System.err.println("[VALIDATION ERROR] User ID cannot be empty.");
            return Optional.empty();
        }

        try {
            Optional<User> userOpt = userRepository.findById(userId.trim());
            if (userOpt.isEmpty()) {
                System.out.println("[INFO] User '" + userId + "' not found.");
            }
            return userOpt;
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while reading user: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean updateUser(String userId, String name, String email, String phone, String role) {
        if (!ValidationUtil.isValidString(userId)) {
            System.err.println("[VALIDATION ERROR] User ID cannot be empty.");
            return false;
        }
        if (ValidationUtil.isValidString(email) && !ValidationUtil.isValidEmail(email)) {
            System.err.println("[VALIDATION ERROR] Invalid email format.");
            return false;
        }

        try {
            Optional<User> existingOpt = userRepository.findById(userId.trim());
            if (existingOpt.isEmpty()) {
                System.err.println("[ERROR] Cannot update: User '" + userId + "' does not exist.");
                return false;
            }

            User user = existingOpt.get();
            if (ValidationUtil.isValidString(name)) user.setName(name.trim());
            if (ValidationUtil.isValidString(email)) user.setEmail(email.trim());
            if (ValidationUtil.isValidString(phone)) user.setPhone(phone.trim());
            if (ValidationUtil.isValidString(role)) user.setRole(role.trim());

            userRepository.save(user);
            System.out.println("[SUCCESS] User '" + userId + "' updated successfully.");
            return true;

        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        if (!ValidationUtil.isValidString(userId)) {
            System.err.println("[VALIDATION ERROR] User ID cannot be empty.");
            return false;
        }

        try {
            boolean deleted = userRepository.deleteById(userId.trim());
            if (deleted) {
                System.out.println("[SUCCESS] User '" + userId + "' deleted successfully.");
            } else {
                System.err.println("[ERROR] Cannot delete: User '" + userId + "' does not exist.");
            }
            return deleted;
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while deleting user: " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (IOException e) {
            System.err.println("[ERROR] Database failure while scanning users: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
