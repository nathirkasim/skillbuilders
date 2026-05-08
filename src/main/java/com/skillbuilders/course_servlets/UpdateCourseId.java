package com.skillbuilders.course_servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/updatecourseid")
public class UpdateCourseId extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) jsonBuilder.append(line);
        }

        JsonObject jsonObject = JsonParser.parseString(jsonBuilder.toString()).getAsJsonObject();
        // FIXED: store as Integer (was String) to be consistent with FetchCourseById which stores as int
        int courseId = jsonObject.get("courseId").getAsInt();
        request.getSession(true).setAttribute("currentCourseId", courseId);

        PrintWriter out = response.getWriter();
        out.write("{\"status\":\"success\",\"courseId\":" + courseId + "}");
        out.flush();
    }
}
