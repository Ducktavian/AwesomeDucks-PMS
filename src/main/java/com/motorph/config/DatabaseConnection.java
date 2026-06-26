package com.motorph.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (in == null) {
                throw new RuntimeException(
                    "database.properties not found on classpath.\n" +
                    "Copy src/main/resources/database.properties.example " +
                    "to src/main/resources/database.properties and fill in your credentials."
                );
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database.properties", e);
        }
        URL      = props.getProperty("db.url");
        USER     = props.getProperty("db.user"); 
        PASSWORD = props.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
