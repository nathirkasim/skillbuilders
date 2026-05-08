package com.skillbuilders.course_servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skillbuilders.dao.FetchCourseDAO;
import com.skillbuilders.util.Course;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.Lecture;
import com.skillbuilders.util.Question;
import com.skillbuilders.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/previewcourses")
public class PreviewCourses extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // FIXED: was public — admin-only endpoint must require admin session
        if (!SessionManager.requireAdminSession(request, response)) return;

        JsonArray coursesJsonArray = new JsonArray();
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT courseid FROM courses WHERE approved = 'false'";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                FetchCourseDAO fetchCourseDAO = new FetchCourseDAO();
                while (rs.next()) {
                    int courseId = rs.getInt("courseid");
                    try {
                        Course course = fetchCourseDAO.getCourseDetails(courseId, connection);
                        if (course != null) coursesJsonArray.add(convertCourseToJson(course));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        response.getWriter().write(coursesJsonArray.toString());
    }

    private JsonObject convertCourseToJson(Course course) {
        JsonObject json = new JsonObject();
        json.addProperty("courseId",      course.getCourseId());
        json.addProperty("name",          course.getName());
        json.addProperty("instructorId",  course.getInstructorId());
        json.addProperty("price",         course.getPrice());
        json.addProperty("rating",        course.getRating());
        json.addProperty("ratingCount",   course.getRatingCount());
        json.addProperty("duration",      course.getDuration());
        json.addProperty("moduleCount",   course.getModuleCount());
        json.addProperty("enrolledCount", course.getEnrolledCount());
        json.addProperty("timeDate",      course.getTimeDate());
        json.addProperty("thumbnail",     course.getThumbnail());
        json.addProperty("description",   course.getDescription());
        JsonArray streams = new JsonArray();
        for (String s : course.getStreams()) streams.add(s);
        json.add("streams", streams);
        JsonArray prereqs = new JsonArray();
        for (String p : course.getPrerequisites()) prereqs.add(p);
        json.add("prerequisites", prereqs);
        JsonArray lectures = new JsonArray();
        for (Lecture lecture : course.getLectures()) {
            JsonObject lj = new JsonObject();
            lj.addProperty("moduleNumber", lecture.getModuleNumber());
            lj.addProperty("moduleName",   lecture.getModuleName());
            lj.addProperty("link",         lecture.getLink());
            JsonArray questions = new JsonArray();
            for (Question q : lecture.getQuestions()) {
                JsonObject qj = new JsonObject();
                qj.addProperty("questionNumber", q.getQuestionNumber());
                qj.addProperty("question",  q.getQuestion());
                qj.addProperty("option1",   q.getOption1());
                qj.addProperty("option2",   q.getOption2());
                qj.addProperty("option3",   q.getOption3());
                qj.addProperty("option4",   q.getOption4());
                qj.addProperty("answer",    q.getAnswer());
                questions.add(qj);
            }
            lj.add("questions", questions);
            lectures.add(lj);
        }
        json.add("lectures", lectures);
        return json;
    }
}
