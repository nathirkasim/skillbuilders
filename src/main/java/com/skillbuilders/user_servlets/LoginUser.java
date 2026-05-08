package com.skillbuilders.user_servlets;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.skillbuilders.dao.UserAuthenticationDAO;
import com.skillbuilders.util.SessionManager;

@WebServlet("/loginuser")
public class LoginUser extends HttpServlet {
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

            UserAuthenticationDAO dao = new UserAuthenticationDAO();
            int userId = dao.loginUser(email.trim(), password.trim());

            if (userId > 0) {
                // FIXED: use SessionManager for consistent session creation
                SessionManager.createUserSession(request, userId);
                jsonResponse.addProperty("result", "success");
                jsonResponse.addProperty("message", "Login successful.");
            } else {
                jsonResponse.addProperty("result", "failure");
                jsonResponse.addProperty("message", "Email or password is incorrect.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "failure");
            jsonResponse.addProperty("message", "An error occurred while processing the request.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
