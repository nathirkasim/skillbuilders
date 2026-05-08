package com.skillbuilders.user_servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.skillbuilders.dao.UpdateUserInformationDAO;
import com.skillbuilders.util.SessionManager;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/updateuserinformation")
public class UpdateUserInformation extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // FIXED: get userId from session, not from request param (was a security hole)
        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        String field    = request.getParameter("field");
        String newValue = request.getParameter("value");

        if (field == null || field.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Missing required parameter: field\"}");
            return;
        }

        String[] interestedStreams = null;
        if ("interestedStreams".equalsIgnoreCase(field)) {
            if (newValue != null && !newValue.isEmpty()) {
                interestedStreams = newValue.split("\\s*,\\s*");
            }
        }

        UpdateUserInformationDAO dao = new UpdateUserInformationDAO();
        boolean success = false;

        try {
            switch (field.toLowerCase()) {
                case "name":              success = dao.updateUsername(String.valueOf(userId), newValue); break;
                case "gender":            success = dao.updateGender(String.valueOf(userId), newValue); break;
                case "professionalsummary": success = dao.updateSummary(String.valueOf(userId), newValue); break;
                case "dob":               success = dao.updateDOB(String.valueOf(userId), newValue); break;
                case "phonenumber":       success = dao.updatePhoneNumber(String.valueOf(userId), newValue); break;
                case "country":           success = dao.updateCountry(String.valueOf(userId), newValue); break;
                case "city":              success = dao.updateCity(String.valueOf(userId), newValue); break;
                case "grade":             success = dao.updateGrade(String.valueOf(userId), newValue); break;
                case "stream":            success = dao.updateCurrentStream(String.valueOf(userId), newValue); break;
                case "profile":           success = dao.updateCurrentProfile(String.valueOf(userId), newValue); break;
                case "interestedstreams": success = dao.updateInterestedStreams(String.valueOf(userId), interestedStreams); break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"status\":\"failure\",\"message\":\"Invalid field: " + field + "\"}");
                    return;
            }

            if (success) {
                out.write("{\"status\":\"success\",\"message\":\"Update successful.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"status\":\"failure\",\"message\":\"Update failed.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\":\"error\",\"message\":\"An error occurred.\"}");
        }
    }
}
