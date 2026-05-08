package com.skillbuilders.course_servlets;

import com.skillbuilders.dao.InstructorAnalyticsDAO;
import com.skillbuilders.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

/** POST /trackcourseview?courseid=X — Increments the view counter for a course. */
@WebServlet("/trackcourseview")
public class TrackCourseView extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String courseIdStr = request.getParameter("courseid");
        if (courseIdStr == null || courseIdStr.isBlank()) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Missing courseid\"}");
            return;
        }
        try {
            int courseId = Integer.parseInt(courseIdStr.trim());
            try (Connection conn = DBConnection.getConnection()) {
                new InstructorAnalyticsDAO().incrementCourseViewCount(courseId, conn);
            }
            response.getWriter().write("{\"status\":\"ok\"}");
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid courseid\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\"}");
        }
    }
}
