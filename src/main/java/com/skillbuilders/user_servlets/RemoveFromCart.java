package com.skillbuilders.user_servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.skillbuilders.dao.RemoveUserCourseDAO;
import com.skillbuilders.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/removefromcart")
public class RemoveFromCart extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final RemoveUserCourseDAO courseDAO = new RemoveUserCourseDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        // FIXED: userId from session, not from JSON body
        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        StringBuilder requestData = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) requestData.append(line);
        JsonObject jsonData = new Gson().fromJson(requestData.toString(), JsonObject.class);
        int courseId = jsonData.get("courseid").getAsInt();

        boolean success = false;
        try {
            success = courseDAO.removeFromCart(userId, courseId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("message", success
            ? "Course removed from cart successfully!"
            : "Failed to remove course from cart.");
        PrintWriter out = response.getWriter();
        out.write(jsonResponse.toString());
    }
}
