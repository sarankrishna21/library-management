package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for User CRUD operations.
 * Delegates all logic to UserService — no HBase code here.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    // ─── GET /api/users ───────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ─── GET /api/users/{userId} ──────────────────────────────────────────────
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        Optional<User> user = userService.getUser(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found: " + userId));
    }

    // ─── POST /api/users ──────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody Map<String, Object> body) {
        String userId = getString(body, "userId");
        String name   = getString(body, "name");
        String email  = getString(body, "email");
        String phone  = getString(body, "phone");
        String role   = getString(body, "role");

        if (userId == null || name == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "userId and name are required."));
        }

        Optional<User> existing = userService.getUser(userId);
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User already exists: " + userId));
        }

        boolean created = userService.addUser(userId, name,
                email  != null ? email  : "",
                phone  != null ? phone  : "",
                role   != null ? role   : "Student");
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User created successfully.", "userId", userId));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create user. Check server logs."));
    }

    // ─── PUT /api/users/{userId} ──────────────────────────────────────────────
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId,
                                        @RequestBody Map<String, Object> body) {
        Optional<User> existing = userService.getUser(userId);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found: " + userId));
        }

        User current = existing.get();
        String name  = body.containsKey("name")  ? getString(body, "name")  : current.getName();
        String email = body.containsKey("email") ? getString(body, "email") : current.getEmail();
        String phone = body.containsKey("phone") ? getString(body, "phone") : current.getPhone();
        String role  = body.containsKey("role")  ? getString(body, "role")  : current.getRole();

        boolean updated = userService.updateUser(userId,
                name  != null ? name  : current.getName(),
                email != null ? email : current.getEmail(),
                phone != null ? phone : current.getPhone(),
                role  != null ? role  : current.getRole());
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "User updated successfully.", "userId", userId));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update user. Check server logs."));
    }

    // ─── DELETE /api/users/{userId} ───────────────────────────────────────────
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        Optional<User> existing = userService.getUser(userId);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found: " + userId));
        }

        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully.", "userId", userId));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete user. Check server logs."));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String getString(Map<String, Object> body, String key) {
        Object val = body.get(key);
        return (val != null && !val.toString().isBlank()) ? val.toString().trim() : null;
    }
}
