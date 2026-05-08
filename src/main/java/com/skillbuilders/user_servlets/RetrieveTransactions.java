package com.skillbuilders.user_servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skillbuilders.util.DBConnection;
import com.skillbuilders.util.SessionManager;

@WebServlet("/retrievetransactions")
public class RetrieveTransactions extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (!SessionManager.requireUserSession(request, response)) return;
        int userId = SessionManager.getUserId(request);

        // FIXED: was querying 'transactionid' and 'details' columns that don't exist in schema
        // Correct columns: id, userid, courseid, amount, time_date
        String query = "SELECT t.id, t.courseid, t.amount, t.time_date, c.name AS course_name " +
                       "FROM transactions t " +
                       "LEFT JOIN courses c ON t.courseid = c.courseid " +
                       "WHERE t.userid = ? ORDER BY t.time_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                JsonArray transactionsArray = new JsonArray();
                while (rs.next()) {
                    JsonObject transaction = new JsonObject();
                    transaction.addProperty("id",          rs.getInt("id"));
                    transaction.addProperty("courseid",    rs.getInt("courseid"));
                    transaction.addProperty("course_name", rs.getString("course_name"));
                    transaction.addProperty("amount",      rs.getFloat("amount"));
                    transaction.addProperty("time_date",   rs.getTimestamp("time_date").toString());
                    transactionsArray.add(transaction);
                }
                out.write(transactionsArray.toString());
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"An error occurred while fetching transactions.\"}");
        }
    }
}
