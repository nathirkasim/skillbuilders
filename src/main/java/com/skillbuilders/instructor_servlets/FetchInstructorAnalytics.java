package com.skillbuilders.instructor_servlets;

import com.google.gson.Gson;
import com.skillbuilders.dao.InstructorAnalyticsDAO;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.SessionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * FetchInstructorAnalytics servlet — Returns JSON analytics data for the
 * instructor dashboard: enrollment stats, earnings, view counts, trend.
 * Endpoint: POST /fetchinstructoranalytics
 */
@WebServlet("/fetchinstructoranalytics")
public class FetchInstructorAnalytics extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        if (!SessionManager.requireInstructorSession(request, response)) return;

        int instructorId = SessionManager.getInstructorId(request);
        InstructorAnalyticsDAO dao = new InstructorAnalyticsDAO();

        try (Connection conn = DBConnection.getConnection()) {
            Map<String, Object> analytics = new LinkedHashMap<>();
            analytics.put("totalEnrolledStudents", dao.getTotalEnrolledStudents(instructorId, conn));
            analytics.put("totalEarnings",         dao.getTotalEarnings(instructorId, conn));
            analytics.put("averageRating",         dao.getAverageRating(instructorId, conn));
            analytics.put("earningsPerCourse",     dao.getEarningsPerCourse(instructorId, conn));
            analytics.put("viewCountsPerCourse",   dao.getViewCountsPerCourse(instructorId, conn));
            analytics.put("enrollmentTrend",       dao.getEnrollmentTrend(instructorId, conn));
            response.getWriter().write(gson.toJson(analytics));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to load analytics.\"}");
        }
    }
}
