package com.skillbuilders.course_servlets;

import com.google.gson.Gson;
import com.skillbuilders.dao.FetchCourseDAO;
import com.skillbuilders.util.Course;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/coursesearch")
public class FetchSearchCourse extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        String searchString = request.getParameter("searchString");
        if (searchString == null || searchString.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Missing or empty searchString parameter\"}");
            return;
        }

        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        FetchCourseDAO fetchCourseDAO = new FetchCourseDAO();
        List<Course> courseList = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            String courseQuery = "SELECT courseid FROM courses WHERE name LIKE ? AND approved = 'true'";
            try (PreparedStatement courseStmt = connection.prepareStatement(courseQuery)) {
                courseStmt.setString(1, "%" + searchString + "%");
                try (ResultSet courseRs = courseStmt.executeQuery()) {
                    List<Integer> enrolledCourseIds = new ArrayList<>();
                    String userCoursesQuery = "SELECT courseid FROM usercourses WHERE userid = ?";
                    // FIXED: was setString(1, userId) — should be setInt for an int column
                    try (PreparedStatement userCoursesStmt = connection.prepareStatement(userCoursesQuery)) {
                        userCoursesStmt.setInt(1, userId);
                        try (ResultSet userCoursesRs = userCoursesStmt.executeQuery()) {
                            while (userCoursesRs.next()) enrolledCourseIds.add(userCoursesRs.getInt("courseid"));
                        }
                    }
                    while (courseRs.next()) {
                        int courseId = courseRs.getInt("courseid");
                        if (!enrolledCourseIds.contains(courseId)) {
                            Course course = fetchCourseDAO.getCourseDetails(courseId, connection);
                            if (course != null) courseList.add(course);
                        }
                    }
                }
            }
            response.getWriter().write(new Gson().toJson(courseList));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }
}
