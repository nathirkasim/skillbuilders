package com.skillbuilders.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_HOST     = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT     = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME     = System.getenv().getOrDefault("DB_NAME", "skillbuilders");
    private static final String DB_USER     = System.getenv().getOrDefault("DB_USER", "skillbuilder");
    // FIXED: updated default password to match docker-compose
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "skillbuilders2024");

    private static final String JDBC_URL =
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
        "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

    private DBConnection() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
}
