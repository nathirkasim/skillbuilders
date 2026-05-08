package com.skillbuilders.instructor_servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.skillbuilders.dao.AddCourseDAO;
import com.skillbuilders.util.Course;
import com.skillbuilders.util.Lecture;
import com.skillbuilders.util.Question;
import com.skillbuilders.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@WebServlet("/addcourse")
public class AddCourse extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();
    // FIXED: removed instance field `instructorId` — was thread-unsafe (shared across concurrent requests)

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        // FIXED: use SessionManager instead of raw session attribute access
        if (!SessionManager.requireInstructorSession(request, response)) return;
        int instructorId = SessionManager.getInstructorId(request); // local variable — thread-safe

        try {
            BufferedReader reader = request.getReader();
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) jsonInput.append(line);

            JsonObject jsonObject = gson.fromJson(jsonInput.toString(), JsonObject.class);
            Course course = getCourseObject(jsonObject, instructorId);

            AddCourseDAO dao = new AddCourseDAO();
            boolean success = true;
            String errorMsg = null;

            try {
                if      (!dao.insertCourse(course))              { success = false; errorMsg = "Error inserting course."; }
                else if (!dao.insertCourseStreams(course))       { success = false; errorMsg = "Error inserting course streams."; }
                else if (!dao.insertCoursePrerequisites(course)) { success = false; errorMsg = "Error inserting course prerequisites."; }
                else if (!dao.insertCourseLectures(course))      { success = false; errorMsg = "Error inserting course lectures."; }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid data format or processing error.\"}");
                return;
            }

            if (success) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Course data processed successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"" + errorMsg + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid data format or processing error.\"}");
        }
    }

    private Course getCourseObject(JsonObject jsonObject, int instructorId) {
        JsonObject courseDetails = jsonObject.getAsJsonObject("courseDetails");

        String title       = getString(courseDetails, "title", "");
        String description = getString(courseDetails, "description", "");
        float  duration    = getFloat(courseDetails, "duration", 0.0f);
        int    moduleCount = getInt(courseDetails, "modulesCount", 0);
        String thumbnail   = getString(courseDetails, "thumbnail", "");
        float  price       = getFloat(courseDetails, "price", 0.0f);
        String[] streams       = gson.fromJson(courseDetails.getAsJsonArray("streams"), String[].class);
        String[] prerequisites = gson.fromJson(courseDetails.getAsJsonArray("prerequisites"), String[].class);

        Course course = new Course(0, title, instructorId, "Instructor", price, 0, 0,
                                   duration, moduleCount, 0, null, thumbnail, description, streams, prerequisites);

        Type moduleListType = new TypeToken<List<JsonObject>>() {}.getType();
        List<JsonObject> modules = gson.fromJson(jsonObject.getAsJsonArray("modules"), moduleListType);
        int moduleNumber = 1;
        for (JsonObject moduleJson : modules) {
            String moduleName  = getString(moduleJson, "moduleName", null);
            String lectureLink = getString(moduleJson, "lectureLink", null);
            Lecture lecture = new Lecture(0, moduleNumber++, moduleName, lectureLink);

            if (moduleJson.has("questions") && !moduleJson.get("questions").isJsonNull()) {
                Type questionListType = new TypeToken<List<JsonObject>>() {}.getType();
                List<JsonObject> questions = gson.fromJson(moduleJson.getAsJsonArray("questions"), questionListType);
                int qNum = 1;
                for (JsonObject q : questions) {
                    String questionText = getString(q, "questionText", "");
                    String o1 = getArrayStr(q, "options", 0);
                    String o2 = getArrayStr(q, "options", 1);
                    String o3 = getArrayStr(q, "options", 2);
                    String o4 = getArrayStr(q, "options", 3);
                    String correct = getString(q, "correctOption", "");
                    lecture.addQuestion(new Question(qNum++, questionText, o1, o2, o3, o4, correct, 0, moduleNumber));
                }
            }
            course.addLecture(lecture);
        }
        return course;
    }

    private String getString(JsonObject o, String key, String def) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsString() : def;
    }
    private float getFloat(JsonObject o, String key, float def) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsFloat() : def;
    }
    private int getInt(JsonObject o, String key, int def) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsInt() : def;
    }
    private String getArrayStr(JsonObject o, String key, int idx) {
        if (!o.has(key) || o.get(key).isJsonNull()) return "";
        var arr = o.getAsJsonArray(key);
        return arr.size() > idx ? arr.get(idx).getAsString() : "";
    }
}
