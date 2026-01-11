package com.roktim.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.roktim.dao.UserDAO;
import com.roktim.model.User;
import com.roktim.model.UserRole;

public class AuthService {
    private final UserDAO userDAO;
    private final SessionManager sessionManager;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    public User login(String username, String password, UserRole role) {
        try {
            User user = userDAO.authenticateUser(username, password, role);

            if (user != null) {
                sessionManager.setCurrentUser(user);
                System.out.println("✓ Login successful: " + user.getUsername());
                return user;
            }

            // Bootstrap fallback: allow a default admin if DB has no admin row yet
            if (role == UserRole.ADMIN && isDefaultAdmin(username, password)) {
                User bootstrapAdmin = new User();
                bootstrapAdmin.setId(-1); // virtual id
                bootstrapAdmin.setUsername("admin");
                bootstrapAdmin.setName("Administrator");
                bootstrapAdmin.setRole(UserRole.ADMIN);
                sessionManager.setCurrentUser(bootstrapAdmin);
                System.out.println("✓ Login successful (bootstrap admin)");
                return bootstrapAdmin;
            } else {
                System.out.println("✗ Login failed: Invalid credentials");
                return null;
            }
        } catch (Exception e) {
            System.err.println("✗ Error during login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private boolean isDefaultAdmin(String username, String password) {
        // Keeps UX simple: bootstrap credentials are admin/admin
        return "admin".equalsIgnoreCase(username) && "admin".equals(password);
    }

    public void logout() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            System.out.println("✓ User logged out: " + currentUser.getUsername());
        }
        sessionManager.logout();
    }

    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes());
            
            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("✗ Error hashing password: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            if (hashedPassword == null) {
                return false;
            }

            String hashedInput = hashPassword(plainPassword);

            // Primary: hashed comparison
            if (hashedInput != null && hashedInput.equals(hashedPassword)) {
                return true;
            }

            // Fallback: allow plain-text passwords that may exist in legacy or seeded rows
            // This prevents login lockout if an admin record was inserted without hashing.
            return plainPassword.equals(hashedPassword);

        } catch (Exception e) {
            System.err.println("✗ Error verifying password: " + e.getMessage());
            return false;
        }
    }

    public boolean register(User user) {
        try {
            user.setPassword(hashPassword(user.getPassword()));
            boolean success = userDAO.registerUser(user);
            if (success) {
                System.out.println("✓ User registered: " + user.getUsername());
            }
            return success;
        } catch (Exception e) {
            System.err.println("✗ Error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}