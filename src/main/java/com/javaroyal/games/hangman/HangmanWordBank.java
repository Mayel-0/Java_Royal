package com.javaroyal.games.hangman;

import com.example.java_royal.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class HangmanWordBank {
    private static final String PICK_RANDOM_WORD = """
            SELECT word, category, hint
            FROM hangman_words
            WHERE difficulty = ?
            ORDER BY RANDOM()
            LIMIT 1
            """;

    public HangmanWord pickRandomWord(HangmanDifficulty difficulty, Random random) {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(PICK_RANDOM_WORD)) {
            statement.setString(1, difficulty.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new HangmanWord(
                            resultSet.getString("word"),
                            resultSet.getString("category"),
                            resultSet.getString("hint")
                    );
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les mots du pendu.", e);
        }

        return new HangmanWord("JAVA", "General", "Langage de programmation");
    }
}
