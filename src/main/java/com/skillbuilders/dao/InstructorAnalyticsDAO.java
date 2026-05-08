package com.skillbuilders.dao;

import com.skillbuilders.util.DBConnection;
import java.sql.*;
import java.util.*;

/**
 * InstructorAnalyticsDAO — Provides enrollment analytics, earnings tracking,
 * and course view count data for instructor dashboards.
 */
public class InstructorAnalyticsDAO {

    /**
     * Get total number of students enrolled across all instructor's courses.
     */
    public int getTotalEnrolledStudents(int instructorId, Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(SUM(enrolled_count), 0) AS total FROM courses WHERE instructorid = ? AND approved = 'true'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Get total revenue earned = SUM(price * enrolled_count) for approved courses.
     */
    public double getTotalEarnings(int instructorId, Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(SUM(price * enrolled_count), 0) AS total FROM courses WHERE instructorid = ? AND approved = 'true'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    /**
     * Get earnings per course: returns list of {courseid, name, price, enrolled, revenue}.
     */
    public List<Map<String, Object>> getEarningsPerCourse(int instructorId, Connection conn) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT courseid, name, price, enrolled_count, (price * enrolled_count) AS revenue FROM courses WHERE instructorid = ? AND approved = 'true' ORDER BY revenue DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("courseId", rs.getInt("courseid"));
                    row.put("name",     rs.getString("name"));
                    row.put("price",    rs.getFloat("price"));
                    row.put("enrolled", rs.getInt("enrolled_count"));
                    row.put("revenue",  rs.getDouble("revenue"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    /**
     * Increment view count for a course (called on course preview page load).
     */
    public void incrementCourseViewCount(int courseId, Connection conn) throws SQLException {
        String sql = "UPDATE courses SET view_count = COALESCE(view_count, 0) + 1 WHERE courseid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    /**
     * Get view count per course for an instructor.
     */
    public List<Map<String, Object>> getViewCountsPerCourse(int instructorId, Connection conn) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT courseid, name, COALESCE(view_count, 0) AS view_count FROM courses WHERE instructorid = ? ORDER BY view_count DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("courseId",  rs.getInt("courseid"));
                    row.put("name",      rs.getString("name"));
                    row.put("viewCount", rs.getLong("view_count"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    /**
     * Get enrollment trend: returns enrollment count grouped by month (last 6 months).
     */
    public List<Map<String, Object>> getEnrollmentTrend(int instructorId, Connection conn) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(uc.enrolled_at, '%b %Y') AS month, COUNT(*) AS count " +
                     "FROM usercourses uc " +
                     "JOIN courses c ON uc.courseid = c.courseid " +
                     "WHERE c.instructorid = ? AND uc.course_type = 'enrolled' " +
                     "  AND uc.enrolled_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                     "GROUP BY DATE_FORMAT(uc.enrolled_at, '%Y-%m') " +
                     "ORDER BY DATE_FORMAT(uc.enrolled_at, '%Y-%m') ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("month", rs.getString("month"));
                    row.put("count", rs.getInt("count"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    /**
     * Get average rating across all instructor courses.
     */
    public double getAverageRating(int instructorId, Connection conn) throws SQLException {
        String sql = "SELECT AVG(rating) AS avg_rating FROM courses WHERE instructorid = ? AND rating_count > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("avg_rating");
            }
        }
        return 0.0;
    }
}
