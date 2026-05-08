package com.skillbuilders.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.skillbuilders.util.DBConnection;

public class AdminAuthenticationDAO {

    /**
     * Authenticates the admin with the given email and password.
     * FIXED: returns adminId (int > 0) on success, 0 on failure.
     * Admin passwords are stored plain-text (seeded in init.sql).
     */
    public int loginAdmin(String email, String password) throws ClassNotFoundException, SQLException {
        String query = "SELECT adminid FROM admin WHERE email = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("adminid"); // FIXED: return actual adminid
                }
            }
        }
        return 0;
    }
}
