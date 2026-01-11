package com.roktim.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^01[0-9]{9}$"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d).{8,}$"
    );

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        if (isEmpty(password) || isEmpty(confirmPassword)) return false;
        return password.equals(confirmPassword);
    }

    public static boolean isValidPositiveInteger(String value) {
        if (isEmpty(value)) return false;
        try {
            int num = Integer.parseInt(value);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) return false;
        return username.matches("^[A-Za-z0-9]{4,20}$");
    }

    public static String getEmailErrorMessage() {
        return "Please enter a valid email address (e.g., user@example.com)";
    }

    public static String getPhoneErrorMessage() {
        return "Please enter a valid phone number (11 digits starting with 01)";
    }

    public static String getPasswordErrorMessage() {
        return "Password must be at least 8 characters with letters and numbers";
    }

    public static String getUsernameErrorMessage() {
        return "Username must be 4-20 characters (letters and numbers only)";
    }
}