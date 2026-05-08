package com.skillbuilders.user_servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.skillbuilders.dao.FetchCourseDAO;
import com.skillbuilders.util.Course;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.SessionManager;

@WebServlet("/fetchusercourses")
public class FetchUserCourses extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();
    // FIXED: removed instance field USER_ID — was thread-unsafe (shared across requests)

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        // FIXED: get userId from session, not from request param
        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        String courseType = request.getParameter("type");
        if (courseType == null || courseType.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Missing 'type' parameter.\"}");
            return;
        }

        List<Integer> courseIds;
        try {
            courseIds = fetchCourseIds(userId, courseType);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Database error fetching course IDs.\"}");
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            List<Course> courses = new ArrayList<>();
            FetchCourseDAO dao = new FetchCourseDAO();
            for (int courseId : courseIds) {
                Course course = dao.getCourseDetails(courseId, connection);
                courses.add(course);
            }
            response.getWriter().write(gson.toJson(courses));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to fetch course data.\"}");
        }
    }

    // FIXED: consolidated 4 near-identical methods into one; also fixed thread-safety by accepting userId param
    private List<Integer> fetchCourseIds(int userId, String courseType)
            throws SQLException, ClassNotFoundException {
        List<Integer> courseIds = new ArrayList<>();
        String sql;
        if ("completed".equals(courseType)) {
            sql = "SELECT courseid FROM usercourses WHERE userid = ? AND course_type = 'enrolled' AND progress = 100";
        } else if ("enrolled".equals(courseType)) {
            sql = "SELECT courseid FROM usercourses WHERE userid = ? AND course_type = 'enrolled' AND progress < 100";
        } else {
            // cart, favourite
            sql = "SELECT courseid FROM usercourses WHERE userid = ? AND course_type = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            if (!"completed".equals(courseType) && !"enrolled".equals(courseType)) {
                stmt.setString(2, courseType);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) courseIds.add(rs.getInt("courseid"));
            }
        }
        return courseIds;
    }
}
