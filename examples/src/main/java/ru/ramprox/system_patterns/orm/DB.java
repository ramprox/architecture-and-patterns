package ru.ramprox.system_patterns.orm;

import java.sql.*;

public class DB {

    private static Connection connection;
    private static String url = "jdbc:mysql://localhost:3306/?user=root&password=root";

    static {
        try {
            connection = DriverManager.getConnection(url);
            createDatabaseIfNotExists();
            createUserTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDatabaseIfNotExists() throws SQLException {
        Statement createStatement = connection.createStatement();
        createStatement.execute("CREATE DATABASE IF NOT EXISTS test_patterns;");
        Statement useDatabaseStatement = connection.createStatement();
        useDatabaseStatement.execute("use test_patterns;");
    }

    private static void createUserTable() throws SQLException {
        String queryBuilder = "CREATE TABLE IF NOT EXISTS users (" +
                "id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "username varchar(255) NOT NULL," +
                "password varchar(255) NOT NULL);";

        Statement createUserTableStatement = connection.createStatement();
        createUserTableStatement.execute(queryBuilder);
    }

    public static Connection getConnection() {
        return connection;
    }
}
