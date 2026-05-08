package com.skillbuilders.util;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.JsonObject;

@WebServlet("/getcurrentuser")
public class GetCurrentUser extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        JsonObject jsonResponse = new JsonObject();

        if (session != null) {
            Integer userId = (Integer) session.getAttribute("userid");
            if (userId != null) {
                jsonResponse.addProperty("userId", userId);
            } else {
                jsonResponse.addProperty("error", "No user found in session.");
            }
        } else {
            jsonResponse.addProperty("error", "Session expired.");
        }
        response.getWriter().write(jsonResponse.toString());
    }
}
