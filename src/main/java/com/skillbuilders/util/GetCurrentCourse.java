package com.skillbuilders.util;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.JsonObject;

@WebServlet("/getcurrentcourse")
public class GetCurrentCourse extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        JsonObject jsonResponse = new JsonObject();

        if (session != null) {
            Object courseIdObj = session.getAttribute("currentCourseId");
            if (courseIdObj != null) {
                try {
                    // FIXED: currentCourseId is now always stored as Integer — parse safely
                    int currentCourseId = Integer.parseInt(courseIdObj.toString());
                    jsonResponse.addProperty("currentCourseId", currentCourseId);
                } catch (NumberFormatException e) {
                    jsonResponse.addProperty("error", "Invalid course ID format in session.");
                }
            } else {
                jsonResponse.addProperty("error", "No course ID found in session.");
            }
        } else {
            jsonResponse.addProperty("error", "Session expired or not found.");
        }
        response.getWriter().write(jsonResponse.toString());
    }
}
