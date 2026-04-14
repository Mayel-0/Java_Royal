package com.example.java_royal.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {
    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  username TEXT NOT NULL UNIQUE,
                  email TEXT NOT NULL UNIQUE,
                  password TEXT NOT NULL,
                  current_level INTEGER DEFAULT 1,
                  current_xp INTEGER DEFAULT 0,
                  total_xp INTEGER DEFAULT 0,
                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String createSudokuScoresTable = """
                CREATE TABLE IF NOT EXISTS sudoku_scores (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER NOT NULL,
                  score INTEGER NOT NULL,
                  time_seconds INTEGER NOT NULL,
                  moves INTEGER NOT NULL,
                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (user_id) REFERENCES users(id)
                )
                """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute(createUsersTable);
            statement.execute(createSudokuScoresTable);
        }
    }
}
