package com.skillbuilders.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.InputValidator;
import com.skillbuilders.util.PasswordUtil;

public class UserAuthenticationDAO {

    public boolean registerUser(String name, String email, String password)
            throws SQLException, ClassNotFoundException {

        // Validate inputs
        if (!InputValidator.isValidEmail(email))    throw new IllegalArgumentException("Invalid email format.");
        if (!InputValidator.isValidName(name))      throw new IllegalArgumentException("Invalid name.");
        if (!InputValidator.isValidPassword(password)) throw new IllegalArgumentException("Password must be 6–128 characters.");

        String checkEmailQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
        String insertUserQuery = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement checkEmailStmt = connection.prepareStatement(checkEmailQuery)) {
            checkEmailStmt.setString(1, email.trim());
            ResultSet rs = checkEmailStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false;

            // Hash password before storage
            String hashedPassword = PasswordUtil.hashPassword(password);

            try (PreparedStatement insertStmt = connection.prepareStatement(insertUserQuery)) {
                insertStmt.setString(1, InputValidator.sanitizeText(name));
                insertStmt.setString(2, email.trim().toLowerCase());
                insertStmt.setString(3, hashedPassword);
                return insertStmt.executeUpdate() > 0;
            }
        }
    }

    public int loginUser(String email, String password)
            throws ClassNotFoundException, SQLException {

        String query = "SELECT userid, password FROM users WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    // Verify with PasswordUtil (supports both legacy plain and hashed)
                    if (PasswordUtil.verifyPassword(password, stored)) {
                        return rs.getInt("userid");
                    }
                }
            }
        }
        return 0;
    }
}
