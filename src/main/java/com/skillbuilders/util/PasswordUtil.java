package com.skillbuilders.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordUtil — Secure password hashing using SHA-256 + salt.
 * Replaces plain-text password storage in UserAuthenticationDAO and InstructorAuthenticationDAO.
 * Usage: hash on register, verify on login.
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {}

    /** Generate a random salt string. */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /** Hash password with given salt using SHA-256. Returns "salt$hash" */
    public static String hashPassword(String plainPassword, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashed = md.digest(plainPassword.getBytes());
            return salt + "$" + Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Hash a plain password for storage.
     * Generates a salt internally and returns "salt$hash".
     */
    public static String hashPassword(String plainPassword) {
        String salt = generateSalt();
        return hashPassword(plainPassword, salt);
    }

    /**
     * Verify a plain password against a stored "salt$hash" string.
     * Falls back to plain equality for legacy passwords not yet migrated.
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (storedHash == null || !storedHash.contains("$")) {
            // Legacy plain-text comparison (migration path)
            return plainPassword.equals(storedHash);
        }
        String[] parts = storedHash.split("\\$", 2);
        if (parts.length != 2) return false;
        String reHashed = hashPassword(plainPassword, parts[0]);
        return reHashed.equals(storedHash);
    }
}
