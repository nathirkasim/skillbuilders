package com.skillbuilders.admin_servlets;

import com.skillbuilders.dao.InstructorMessageDAO;
import com.skillbuilders.util.SessionManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/deletemessage")
public class DeleteMessage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        // FIXED: no session check existed
        if (!SessionManager.requireAdminSession(request, response)) return;

        JsonObject jsonResponse = new JsonObject();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) sb.append(line);
            JsonObject jsonObject = JsonParser.parseString(sb.toString()).getAsJsonObject();
            int messageId = jsonObject.get("messageId").getAsInt();

            InstructorMessageDAO dao = new InstructorMessageDAO();
            boolean isDeleted = dao.deleteMessage(messageId);

            if (isDeleted) {
                jsonResponse.addProperty("result", "success");
                jsonResponse.addProperty("message", "Message deleted successfully.");
            } else {
                jsonResponse.addProperty("result", "failure");
                jsonResponse.addProperty("message", "Failed to delete message.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "failure");
            jsonResponse.addProperty("message", "An error occurred while deleting the message.");
        }
        response.getWriter().write(jsonResponse.toString());
    }
}
