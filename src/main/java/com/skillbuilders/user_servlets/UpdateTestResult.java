package com.skillbuilders.user_servlets;

import java.io.BufferedReader;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skillbuilders.dao.UpdateTestResultDAO;
import com.skillbuilders.util.SessionManager;

@WebServlet("/updatetestresult")
public class UpdateTestResult extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // FIXED: userId from session, not from JSON body
        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        StringBuilder jsonPayload = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) jsonPayload.append(line);

        JsonObject jsonObject = JsonParser.parseString(jsonPayload.toString()).getAsJsonObject();
        int courseId     = jsonObject.get("courseid").getAsInt();
        int moduleNumber = jsonObject.get("module_number").getAsInt();
        int totalMarks   = jsonObject.get("total_marks").getAsInt();
        int userMarks    = jsonObject.get("user_marks").getAsInt();
        String result    = jsonObject.get("result").getAsString();

        UpdateTestResultDAO dao = new UpdateTestResultDAO();
        boolean success = dao.updateTestResult(courseId, userId, moduleNumber, totalMarks, userMarks, result);

        JsonObject jsonResponse = new JsonObject();
        if (success) {
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Record inserted/updated successfully.");
        } else {
            jsonResponse.addProperty("status", "failure");
            jsonResponse.addProperty("message", "Higher marks already exist. No changes made.");
        }
        response.getWriter().write(jsonResponse.toString());
    }
}
