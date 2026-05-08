package com.skillbuilders.admin_servlets;

import com.skillbuilders.dao.InstructorMessageDAO;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.InstructorMessage;
import com.skillbuilders.util.SessionManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/addmessage")
public class AddMessage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        // FIXED: no session check existed
        if (!SessionManager.requireAdminSession(request, response)) return;

        JsonObject jsonResponse = new JsonObject();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) sb.append(line);

        try (Connection connection = DBConnection.getConnection()) {
            JsonObject jsonObject = JsonParser.parseString(sb.toString()).getAsJsonObject();
            int courseId   = jsonObject.get("courseId").getAsInt();
            String name    = jsonObject.get("name").getAsString();
            String message = jsonObject.get("message").getAsString();
            int instructorId = fetchInstructorId(courseId, connection);

            if (instructorId < 1) {
                jsonResponse.addProperty("result", "failure");
                jsonResponse.addProperty("message", "Course not found.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            InstructorMessage msg = new InstructorMessage(0, instructorId, courseId, name, message, "false");
            InstructorMessageDAO dao = new InstructorMessageDAO();
            int messageId = dao.addMessage(msg);

            if (messageId > 0) {
                jsonResponse.addProperty("result", "success");
                jsonResponse.addProperty("message", "Message added successfully.");
                jsonResponse.addProperty("messageId", messageId);
            } else {
                jsonResponse.addProperty("result", "failure");
                jsonResponse.addProperty("message", "Failed to add message.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "failure");
            jsonResponse.addProperty("message", "Database connection error.");
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "failure");
            jsonResponse.addProperty("message", "An error occurred while adding the message.");
        }
        response.getWriter().write(jsonResponse.toString());
    }

    private int fetchInstructorId(int courseId, Connection connection) throws SQLException {
        String query = "SELECT instructorid FROM courses WHERE courseid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("instructorid");
            }
        }
        return -1;
    }
}
