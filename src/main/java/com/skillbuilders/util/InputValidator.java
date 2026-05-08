package com.skillbuilders.util;

import java.util.regex.Pattern;

/**
 * InputValidator — Centralized input sanitization and validation.
 * Use before any DB operation to prevent SQL injection and XSS.
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern SAFE_NAME_PATTERN =
        Pattern.compile("^[A-Za-z0-9 .,'\\-]{1,100}$");
    private static final int MAX_TEXT_LENGTH = 5000;

    private InputValidator() {}

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && SAFE_NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 128;
    }

    /** Strip HTML/script tags to prevent XSS in stored text. */
    public static String sanitizeText(String input) {
        if (input == null) return "";
        String sanitized = input
            .replaceAll("<script[^>]*>.*?</script>", "")
            .replaceAll("<[^>]+>", "")
            .trim();
        return sanitized.length() > MAX_TEXT_LENGTH
            ? sanitized.substring(0, MAX_TEXT_LENGTH) : sanitized;
    }

    /** Trim and truncate a string safely. */
    public static String clean(String input, int maxLen) {
        if (input == null) return "";
        String trimmed = input.trim();
        return trimmed.length() > maxLen ? trimmed.substring(0, maxLen) : trimmed;
    }
}
