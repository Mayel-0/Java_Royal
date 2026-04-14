package com.example.java_royal.service;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service métier pour la gestion des utilisateurs.
 * Applique le pattern MVC en centralisant la logique SQL.
 */
public class UserService {

    /**
     * Authentifie un utilisateur et retourne ses données complètes
     *
     * @param identifier Nom d'utilisateur ou email
     * @param password Mot de passe en clair
     * @return User si authentifié, null sinon
     * @throws SQLException Si erreur base de données
     */
    public static User authenticate(String identifier, String password) throws SQLException {
        String sql = "SELECT id, username, email, password, current_level, current_xp, total_xp " +
                    "FROM users WHERE username = ? OR email = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identifier);
            statement.setString(2, identifier);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("password");

                    // Vérifie le mot de passe avec BCrypt
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        // Authentification réussie, retourne l'User complet
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
        }

        return null; // Authentification échouée
    }

    /**
     * Récupère les données complètes d'un utilisateur par son ID
     *
     * @param id ID de l'utilisateur
     * @return User avec toutes ses données
     * @throws SQLException Si erreur base de données
     */
    public static User getUserById(long id) throws SQLException {
        System.out.println("[UserService] getUserById() called with id: " + id);

        if (id <= 0) {
            System.err.println("[UserService] ❌ ID invalide: " + id);
            return null;
        }

        String sql = "SELECT id, username, email, current_level, current_xp, total_xp " +
                    "FROM users WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            System.out.println("[UserService] Executing query: " + sql + " with id=" + id);
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getInt("current_level"),
                        resultSet.getInt("current_xp"),
                        resultSet.getInt("total_xp")
                    );
                    System.out.println("[UserService] ✅ User trouvé: " + user);
                    return user;
                } else {
                    System.err.println("[UserService] ❌ Aucun résultat pour id=" + id);
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("[UserService] ❌ Exception: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    /**
     * Récupère les données d'un utilisateur par son nom d'utilisateur
     *
     * @param username Nom d'utilisateur
     * @return User avec toutes ses données
     * @throws SQLException Si erreur base de données
     */
    public static User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, email, current_level, current_xp, total_xp " +
                    "FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
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
        }

        return null;
    }

    /**
     * Met à jour les points d'expérience et le niveau d'un utilisateur
     *
     * @param userId ID de l'utilisateur
     * @param newXp Nouveaux points XP pour ce niveau
     * @param newLevel Nouveau niveau
     * @throws SQLException Si erreur base de données
     */
    public static void updateUserXp(long userId, int newXp, int newLevel) throws SQLException {
        String sql = "UPDATE users SET current_level = ?, current_xp = ?, " +
                    "total_xp = total_xp + ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, newLevel);
            statement.setInt(2, newXp);
            statement.setInt(3, newXp); // Ajoute au total
            statement.setLong(4, userId);

            statement.executeUpdate();
            System.out.println("[UserService] ✅ XP mis à jour pour user " + userId);
        }
    }

    /**
     * Met à jour le level d'un utilisateur
     *
     * @param userId ID de l'utilisateur
     * @param newLevel Nouveau level
     * @throws SQLException Si erreur base de données
     */
    public static void updateUserLevel(long userId, int newLevel) throws SQLException {
        String sql = "UPDATE users SET current_level = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newLevel);
            statement.setLong(2, userId);

            statement.executeUpdate();
            System.out.println("[UserService] ✅ Level mis à jour pour user " + userId);
        }
    }

    /**
     * Récupère les meilleurs joueurs triés par XP totale (descendant)
     *
     * @param limit Nombre maximum de joueurs à retourner
     * @return Liste des joueurs ordonnés par total_xp DESC
     * @throws SQLException Si erreur base de données
     */
    public static List<LeaderboardEntry> getTopPlayers(int limit) throws SQLException {
        List<LeaderboardEntry> topPlayers = new ArrayList<>();
        String sql = "SELECT username, current_level, total_xp FROM users ORDER BY total_xp DESC LIMIT ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    LeaderboardEntry entry = new LeaderboardEntry(
                        resultSet.getString("username"),
                        resultSet.getInt("current_level"),
                        resultSet.getInt("total_xp")
                    );
                    topPlayers.add(entry);
                }
            }

            System.out.println("[UserService] ✅ " + topPlayers.size() + " joueurs récupérés du classement");
        }

        return topPlayers;
    }

    /**
     * Classe interne représentant une entrée du leaderboard
     */
    public static class LeaderboardEntry {
        private final String username;
        private final int level;
        private final int totalXp;

        public LeaderboardEntry(String username, int level, int totalXp) {
            this.username = username;
            this.level = level;
            this.totalXp = totalXp;
        }

        public String getUsername() {
            return username;
        }

        public int getLevel() {
            return level;
        }

        public int getTotalXp() {
            return totalXp;
        }
    }
}

