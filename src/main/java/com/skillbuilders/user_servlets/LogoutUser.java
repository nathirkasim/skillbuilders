package com.skillbuilders.user_servlets;

import com.skillbuilders.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** POST /logoutuser — Invalidates the current session and returns JSON.
 *  GET /logoutuser  — Invalidates and redirects to user-login.html */
@WebServlet("/logoutuser")
public class LogoutUser extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionManager.invalidateSession(request);
        response.setContentType("application/json");
        response.getWriter().write("{\"result\":\"success\",\"message\":\"Logged out.\"}");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionManager.invalidateSession(request);
        // FIXED: was html_files/user-login.html (non-existent folder)
        response.sendRedirect("user-login.html");
    }
}
