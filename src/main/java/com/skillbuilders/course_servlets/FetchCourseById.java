package com.skillbuilders.course_servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.skillbuilders.dao.FetchCourseDAO;
import com.skillbuilders.util.Course;
import com.skillbuilders.util.DBConnection;

@WebServlet("/fetchcoursebyid")
public class FetchCourseById extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        int courseId;
        try {
            courseId = Integer.parseInt(request.getParameter("courseid"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid course ID format.\"}");
            return;
        }

        // Store course ID in session for later (e.g. /getcurrentcourse)
        request.getSession(true).setAttribute("currentCourseId", courseId);

        // FIXED: was using manual connection without try-with-resources (resource leak)
        try (Connection connection = DBConnection.getConnection()) {
            FetchCourseDAO dao = new FetchCourseDAO();
            Course course = dao.getCourseDetails(courseId, connection);
            if (course == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Course not found.\"}");
                return;
            }
            response.getWriter().write(gson.toJson(course));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to fetch course details.\"}");
        }
    }
}
