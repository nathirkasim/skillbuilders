package com.skillbuilders.user_servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.skillbuilders.dao.ReviewDAO;
import com.skillbuilders.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/insertreview")
public class InsertReview extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        // FIXED: userId from session, not from JSON body
        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        try {
            BufferedReader reader = request.getReader();
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) jsonInput.append(line);
            JsonObject jsonObject = gson.fromJson(jsonInput.toString(), JsonObject.class);
            int courseId      = jsonObject.get("courseid").getAsInt();
            String reviewText = jsonObject.get("review").getAsString();
            int rating        = jsonObject.get("rating").getAsInt();

            ReviewDAO dao = new ReviewDAO();
            boolean success = dao.addReview(userId, courseId, rating, reviewText);
            if (success) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Review inserted successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to insert review.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"An error occurred while inserting the review.\"}");
        }
    }
}
