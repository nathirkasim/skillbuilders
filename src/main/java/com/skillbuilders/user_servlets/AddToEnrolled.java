package com.skillbuilders.user_servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.skillbuilders.dao.EnrolledCourseDAO;
import com.skillbuilders.util.SessionManager;

@WebServlet("/addtoenrolled")
public class AddToEnrolled extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // FIXED: safe null-check — original would NPE if session was null
        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        String courseIdsParam = request.getParameter("courseids");
        if (courseIdsParam == null || courseIdsParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new Gson().toJson(Collections.singletonMap("message", "No course IDs provided.")));
            return;
        }

        String[] parts = courseIdsParam.split(",");
        List<Integer> courseIdList = new ArrayList<>();
        try {
            for (String part : parts) courseIdList.add(Integer.parseInt(part.trim()));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new Gson().toJson(Collections.singletonMap("message", "Invalid course ID format.")));
            return;
        }

        EnrolledCourseDAO dao = new EnrolledCourseDAO();
        boolean isUpdated = false;
        try {
            isUpdated = dao.updateCourseTypeToEnrolled(userId, courseIdList);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (isUpdated) {
            out.write("{\"result\":\"success\",\"message\":\"Successfully enrolled.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"result\":\"failure\",\"message\":\"Failed to enroll in courses.\"}");
        }
    }
}
