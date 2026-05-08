package com.skillbuilders.user_servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.skillbuilders.dao.InsertUserCoursesDAO;
import com.skillbuilders.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/addtofavourites")
public class AddToFavourites extends HttpServlet {
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
            String courseType = jsonObject.get("course_type").getAsString();

            InsertUserCoursesDAO dao = new InsertUserCoursesDAO();
            boolean success = dao.addCourseToUser(userId, courseId, courseType);
            if (success) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Course added to favourites successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Course already exists in your favourites.\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().write("{\"status\":\"error\",\"message\":\"You have already added this course.\"}");
        }
    }
}
