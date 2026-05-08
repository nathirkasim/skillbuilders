package com.skillbuilders.admin_servlets;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.skillbuilders.dao.AdminAuthenticationDAO;
import com.skillbuilders.util.SessionManager;

@WebServlet("/loginadmin")
public class AdminLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        JsonObject jsonResponse = new JsonObject();

        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (email == null || email.trim().isEmpty()) {
                jsonResponse.addProperty("result", "failure");
                jsonResponse.addProperty("message", "Email is required.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            if (password == null || password.trim().isEmpty()) {
                jsonResponse.addProperty("result", "failure");
                jsonResponse.addProperty("message", "Password is required.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            AdminAuthenticationDAO dao = new AdminAuthenticationDAO();
            // FIXED: now returns adminId (int > 0) instead of 1/0 flag
            int adminId = dao.loginAdmin(email.trim(), password.trim());

            if (adminId > 0) {
                // FIXED: use SessionManager to store adminid consistently (was storing adminLoggedIn=true)
                SessionManager.createAdminSession(request, adminId);
                jsonResponse.addProperty("result", "success");
                jsonResponse.addProperty("message", "Login successful.");
            } else {
                jsonResponse.addProperty("result", "failure");
                jsonResponse.addProperty("message", "Invalid credentials.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "failure");
            jsonResponse.addProperty("message", "An error occurred during login.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
