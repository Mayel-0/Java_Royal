package com.example.java_royal.service;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service métier pour la gestion des utilisateurs.
 * Applique le pattern MVC en centralisant la logique SQL.
 */
public class UserService {

    /**
     * Authentifie un utilisateur et retourne ses données (username, level, xp)
     */
    public static User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, email, current_level, current_xp, total_xp FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedHash = null;
                // Récupère le mot de passe hashé depuis la table
                String selectPassword = "SELECT password FROM users WHERE username = ?";
                try (PreparedStatement passStmt = connection.prepareStatement(selectPassword)) {
                    passStmt.setString(1, username);
                    ResultSet passResult = passStmt.executeQuery();
                    if (passResult.next()) {
                        storedHash = passResult.getString("password");
                    }
                }

                if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                    return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getInt("current_level"),
                        resultSet.getInt("current_xp"),
                        resultSet.getInt("total_xp")
                    );
                }
            }
        }
        return null; // Authentification échouée
    }

    /**
     * Récupère les données complètes d'un utilisateur par son ID
     */
    public static User getUserById(long id) throws SQLException {
        String sql = "SELECT id, username, email, current_level, current_xp, total_xp FROM users WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    resultSet.getString("email"),
                    resultSet.getInt("current_level"),
                    resultSet.getInt("current_xp"),
                    resultSet.getInt("total_xp")
                );
            }
        }
        return null;
    }

    /**
     * Récupère les données complètes d'un utilisateur par son username
     */
    public static User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, email, current_level, current_xp, total_xp FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    resultSet.getString("email"),
                    resultSet.getInt("current_level"),
                    resultSet.getInt("current_xp"),
                    resultSet.getInt("total_xp")
                );
            }
        }
        return null;
    }

    /**
     * Met à jour l'XP et le niveau d'un utilisateur
     */
    public static void updateUserXp(long userId, int newXp, int newLevel) throws SQLException {
        String sql = "UPDATE users SET current_xp = ?, current_level = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newXp);
            statement.setInt(2, newLevel);
            statement.setLong(3, userId);
            statement.executeUpdate();
        }
    }

    /**
     * Marque un utilisateur comme ayant complété l'introduction
     * (peut être utilisé plus tard pour tracker les tutoriels)
     */
    public static void updateUserTutorialStatus(long userId, boolean completed) throws SQLException {
        // Futur: ajouter une colonne tutorial_completed à la table users
        // Pour l'instant, on peut utiliser une table séparée ou juste le level
    }
}

