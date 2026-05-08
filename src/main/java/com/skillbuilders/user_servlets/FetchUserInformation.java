package com.skillbuilders.user_servlets;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skillbuilders.dao.FetchUserInformationDAO;
import com.skillbuilders.util.SessionManager;
import com.skillbuilders.util.UserInformation;

@WebServlet("/fetchuserinformation")
public class FetchUserInformation extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        // FIXED: use SessionManager
        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        JsonObject jsonResponse = new JsonObject();
        try {
            FetchUserInformationDAO dao = new FetchUserInformationDAO();
            UserInformation userInfo = dao.getUserInformation(userId);

            jsonResponse.addProperty("profile", userInfo.getProfile());
            jsonResponse.addProperty("name", userInfo.getName());
            jsonResponse.addProperty("gender", userInfo.getGender());
            jsonResponse.addProperty("professionalSummary", userInfo.getProfessionalSummary());
            jsonResponse.addProperty("dob", userInfo.getDob());
            jsonResponse.addProperty("phoneNumber", userInfo.getPhoneNumber());
            jsonResponse.addProperty("country", userInfo.getCountry());
            jsonResponse.addProperty("city", userInfo.getCity());
            jsonResponse.addProperty("grade", userInfo.getGrade());
            jsonResponse.addProperty("stream", userInfo.getStream());

            JsonArray interestedStreams = new JsonArray();
            if (userInfo.getInterestedStreams() != null) {
                for (String stream : userInfo.getInterestedStreams()) {
                    interestedStreams.add(stream);
                }
            }
            jsonResponse.add("interestedStreams", interestedStreams);
            jsonResponse.addProperty("result", "success");

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "failure");
            jsonResponse.addProperty("message", "An error occurred while fetching user data.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
