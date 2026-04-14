package com.example.java_royal.service;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.session.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HangmanScoreService {
    private static final String INSERT_SCORE = """
            INSERT INTO hangman_scores (user_id, total_score, best_score, rounds)
            VALUES (?, ?, ?, ?)
            """;

    public void saveScore(int totalScore, int bestScore, int rounds) throws SQLException {
        long userId = UserSession.getInstance().getId();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SCORE)) {
            statement.setLong(1, userId);
            statement.setInt(2, totalScore);
            statement.setInt(3, bestScore);
            statement.setInt(4, rounds);
            statement.executeUpdate();
        }
    }
}

