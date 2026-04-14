package com.example.java_royal.service;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.session.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SudokuScoreService {
    private static final String INSERT_SCORE = """
            INSERT INTO sudoku_scores (user_id, score, time_seconds, moves)
            VALUES (?, ?, ?, ?)
            """;

    public void saveScore(int score, int timeSeconds, int moves) throws SQLException {
        long userId = UserSession.getInstance().getId();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SCORE)) {
            statement.setLong(1, userId);
            statement.setInt(2, score);
            statement.setInt(3, timeSeconds);
            statement.setInt(4, moves);
            statement.executeUpdate();
        }
    }
}
