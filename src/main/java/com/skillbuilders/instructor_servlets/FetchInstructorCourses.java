package com.skillbuilders.instructor_servlets;

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

@WebServlet("/fetchinstructorcourses")
public class FetchInstructorCourses extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();
    // FIXED: removed thread-unsafe instance field `instructorId`

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        if (!SessionManager.requireInstructorSession(request, response)) return;
        int instructorId = SessionManager.getInstructorId(request); // local — thread-safe

        String courseStatus = request.getParameter("status");
        List<Course> courses = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            FetchCourseDAO dao = new FetchCourseDAO();
            if ("uploaded".equalsIgnoreCase(courseStatus)) {
                courses = getUploadedCourses(instructorId, dao, connection);
            } else if ("inprogress".equalsIgnoreCase(courseStatus)) {
                courses = getInProgressCourses(instructorId, dao, connection);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid course status.\"}");
                return;
            }
            response.getWriter().write(gson.toJson(courses));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to fetch course data.\"}");
        }
    }

    private List<Course> getUploadedCourses(int instructorId, FetchCourseDAO dao, Connection conn) throws SQLException {
        return fetchCoursesByApproved(instructorId, "true", dao, conn);
    }

    private List<Course> getInProgressCourses(int instructorId, FetchCourseDAO dao, Connection conn) throws SQLException {
        return fetchCoursesByApproved(instructorId, "false", dao, conn);
    }

    private List<Course> fetchCoursesByApproved(int instructorId, String approved, FetchCourseDAO dao, Connection conn) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT courseid FROM courses WHERE instructorid = ? AND approved = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, instructorId);
            stmt.setString(2, approved);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course course = dao.getCourseDetails(rs.getInt("courseid"), conn);
                    if (course != null) courses.add(course);
                }
            }
        }
        return courses;
    }
}
