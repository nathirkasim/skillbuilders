package com.skillbuilders.admin_servlets;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skillbuilders.dao.AddCourseDAO;
import com.skillbuilders.util.SessionManager;

@WebServlet("/approvecourse")
public class ApproveCourse extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        // FIXED: no session check existed — anyone could approve courses
        if (!SessionManager.requireAdminSession(request, response)) return;

        JsonObject jsonResponse = new JsonObject();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) sb.append(line);
            JsonObject jsonRequest = JsonParser.parseString(sb.toString()).getAsJsonObject();
            int courseId = jsonRequest.get("courseId").getAsInt();

            AddCourseDAO courseDAO = new AddCourseDAO();
            boolean isApproved = courseDAO.approveCourse(courseId);

            if (isApproved) {
                jsonResponse.addProperty("result", "success");
                jsonResponse.addProperty("message", "Course approved successfully.");
            } else {
                jsonResponse.addProperty("result", "error");
                jsonResponse.addProperty("message", "Failed to approve the course.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "error");
            jsonResponse.addProperty("message", "Database driver not found.");
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "error");
            jsonResponse.addProperty("message", "An error occurred.");
        }
        response.getWriter().write(jsonResponse.toString());
    }
}
