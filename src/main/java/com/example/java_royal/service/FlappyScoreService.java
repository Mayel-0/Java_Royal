package com.example.java_royal.service;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.session.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FlappyScoreService {
    private static final String INSERT_SCORE = """
            INSERT INTO flappy_scores (user_id, score, mode)
            VALUES (?, ?, ?)
            """;

    public void saveScore(int score, String mode) throws SQLException {
        long userId = UserSession.getInstance().getId();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SCORE)) {
            statement.setLong(1, userId);
            statement.setInt(2, score);
            statement.setString(3, mode);
            statement.executeUpdate();
        }
    }
}

