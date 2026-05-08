package com.skillbuilders.dao;

import java.sql.*;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.InputValidator;
import com.skillbuilders.util.PasswordUtil;

public class InstructorAuthenticationDAO {

    public boolean registerInstructor(String name, String email, String password)
            throws SQLException, ClassNotFoundException {

        if (!InputValidator.isValidEmail(email))       throw new IllegalArgumentException("Invalid email.");
        if (!InputValidator.isValidName(name))         throw new IllegalArgumentException("Invalid name.");
        if (!InputValidator.isValidPassword(password)) throw new IllegalArgumentException("Password too short.");

        String checkQuery  = "SELECT COUNT(*) FROM instructors WHERE email = ?";
        String insertQuery = "INSERT INTO instructors (name, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, email.trim().toLowerCase());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false;

            String hashedPassword = PasswordUtil.hashPassword(password);
            try (PreparedStatement ins = conn.prepareStatement(insertQuery)) {
                ins.setString(1, InputValidator.sanitizeText(name));
                ins.setString(2, email.trim().toLowerCase());
                ins.setString(3, hashedPassword);
                return ins.executeUpdate() > 0;
            }
        }
    }

    public int loginInstructor(String email, String password)
            throws ClassNotFoundException, SQLException {
        String query = "SELECT instructorid, password FROM instructors WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    if (PasswordUtil.verifyPassword(password, stored)) {
                        return rs.getInt("instructorid");
                    }
                }
            }
        }
        return 0;
    }
}
