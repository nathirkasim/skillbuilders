package com.skillbuilders.instructor_servlets;

import com.skillbuilders.dao.InstructorMessageDAO;
import com.skillbuilders.util.InstructorMessage;
import com.skillbuilders.util.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/getmessages")
public class GetMessages extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        // FIXED: was taking instructorId from JSON body (security hole) — use session instead
        if (!SessionManager.requireInstructorSession(request, response)) return;
        int instructorId = SessionManager.getInstructorId(request);

        JsonObject jsonResponse = new JsonObject();
        try {
            InstructorMessageDAO dao = new InstructorMessageDAO();
            List<InstructorMessage> messages = dao.getMessagesByInstructorId(instructorId);

            JsonArray messagesArray = new JsonArray();
            for (InstructorMessage msg : messages) {
                JsonObject messageObj = new JsonObject();
                messageObj.addProperty("messageId",    msg.getMessageId());
                messageObj.addProperty("instructorId", msg.getInstructorId());
                messageObj.addProperty("courseId",     msg.getCourseId());
                messageObj.addProperty("name",         msg.getName());
                messageObj.addProperty("message",      msg.getMessage());
                messageObj.addProperty("viewed",       msg.getViewed());
                messagesArray.add(messageObj);
            }
            jsonResponse.add("messages", messagesArray);
            jsonResponse.addProperty("result", "success");
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("result", "failure");
            jsonResponse.addProperty("message", "An error occurred while retrieving the messages.");
        }
        response.getWriter().write(jsonResponse.toString());
    }
}
