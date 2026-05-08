package com.skillbuilders.user_servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.SessionManager;

@WebServlet("/fetchuserprogress")
public class FetchUserProgress extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        // FIXED: get userId from session, not from request param
        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        int courseId;
        try {
            courseId = Integer.parseInt(request.getParameter("courseid"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid course ID format.\"}");
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            Integer progress = getUserProgress(userId, courseId, connection);
            if (progress == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"No progress data found.\"}");
                return;
            }
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("progress", progress);
            response.getWriter().write(jsonResponse.toString());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to fetch progress.\"}");
        }
    }

    private Integer getUserProgress(int userId, int courseId, Connection conn) throws SQLException {
        String query = "SELECT progress FROM usercourses WHERE userid = ? AND courseid = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("progress");
            }
        }
        return null;
    }
}
