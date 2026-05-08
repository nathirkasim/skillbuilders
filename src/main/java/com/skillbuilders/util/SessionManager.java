package com.skillbuilders.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * SessionManager — Centralized session handling with auto-timeout
 * and secure session invalidation.
 */
public class SessionManager {

    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60; // 30 min

    private SessionManager() {}

    /** Create a new secure session for a user. */
    public static HttpSession createUserSession(HttpServletRequest request, int userId) {
        HttpSession session = request.getSession(true);
        session.setAttribute("userid", userId);
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        return session;
    }

    /** Create a new secure session for an instructor. */
    public static HttpSession createInstructorSession(HttpServletRequest request, int instructorId) {
        HttpSession session = request.getSession(true);
        session.setAttribute("instructorid", instructorId);
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        return session;
    }

    /** Create a new secure session for an admin. */
    public static HttpSession createAdminSession(HttpServletRequest request, int adminId) {
        HttpSession session = request.getSession(true);
        session.setAttribute("adminid", adminId);
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        return session;
    }

    /** Get user ID from session, returns -1 if not authenticated. */
    public static int getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return -1;
        Object uid = session.getAttribute("userid");
        return uid instanceof Integer ? (Integer) uid : -1;
    }

    /** Get instructor ID from session, returns -1 if not authenticated. */
    public static int getInstructorId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return -1;
        Object iid = session.getAttribute("instructorid");
        return iid instanceof Integer ? (Integer) iid : -1;
    }

    /** Get admin ID from session, returns -1 if not authenticated. */
    public static int getAdminId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return -1;
        Object aid = session.getAttribute("adminid");
        return aid instanceof Integer ? (Integer) aid : -1;
    }

    /** Invalidate the current session (logout). */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
    }

    /** Check user session validity, writes JSON 401 and returns false if invalid. */
    public static boolean requireUserSession(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        if (getUserId(request) < 1) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Session expired. Please log in again.\"}");
            return false;
        }
        return true;
    }

    /** Check instructor session validity, writes JSON 401 and returns false if invalid. */
    public static boolean requireInstructorSession(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        if (getInstructorId(request) < 1) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Session expired. Please log in again.\"}");
            return false;
        }
        return true;
    }

    // FIXED: was MISSING — admin servlets called requireAdminSession but it didn't exist
    /** Check admin session validity, writes JSON 401 and returns false if invalid. */
    public static boolean requireAdminSession(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        if (getAdminId(request) < 1) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Session expired. Please log in again.\"}");
            return false;
        }
        return true;
    }
}
