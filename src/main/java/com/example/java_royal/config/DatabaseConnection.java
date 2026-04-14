package com.example.java_royal.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static DatabaseConnection instance;
    private final String dbType;
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.dbType = valueOrDefault(dotenv.get("DB_TYPE"), "sqlite").toLowerCase();

        if ("sqlite".equals(dbType)) {
            String sqlitePath = valueOrDefault(dotenv.get("DB_SQLITE_PATH"), resolveSqlitePathFromResources());
            String normalizedPath = Path.of(sqlitePath).toAbsolutePath().normalize().toString();
            this.url = "jdbc:sqlite:" + normalizedPath;
            this.user = null;
            this.password = null;
        } else {
            this.url = dotenv.get("DB_URL");
            this.user = dotenv.get("DB_USER");
            this.password = dotenv.get("DB_PASSWORD");
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if ("sqlite".equals(dbType)) {
            return DriverManager.getConnection(url);
        }
        if (url == null || user == null) {
            throw new SQLException("Variables .env DB_URL ou DB_USER manquantes.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String valueOrDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private static String resolveSqlitePathFromResources() {
        URL resource = DatabaseConnection.class.getResource("/java_royal.db");
        if (resource != null && "file".equalsIgnoreCase(resource.getProtocol())) {
            try {
                return Path.of(resource.toURI()).toString();
            } catch (URISyntaxException ignored) {
                // Fallback local workspace path below.
            }
        }
        return "java_royal.db";
    }
}
