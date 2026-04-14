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

        String createFlappyScoresTable = """
                CREATE TABLE IF NOT EXISTS flappy_scores (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER NOT NULL,
                  score INTEGER NOT NULL,
                  mode TEXT NOT NULL,
                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (user_id) REFERENCES users(id)
                )
                """;

        String createHangmanScoresTable = """
                CREATE TABLE IF NOT EXISTS hangman_scores (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER NOT NULL,
                  total_score INTEGER NOT NULL,
                  best_score INTEGER NOT NULL,
                  rounds INTEGER NOT NULL,
                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (user_id) REFERENCES users(id)
                )
                """;

        String createHangmanWordsTable = """
                CREATE TABLE IF NOT EXISTS hangman_words (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  difficulty TEXT NOT NULL,
                  word TEXT NOT NULL,
                  category TEXT NOT NULL,
                  hint TEXT NOT NULL
                )
                """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute(createUsersTable);
            statement.execute(createSudokuScoresTable);
            statement.execute(createFlappyScoresTable);
            statement.execute(createHangmanScoresTable);
            statement.execute(createHangmanWordsTable);

            seedHangmanWordsIfNeeded(connection, statement);
        }
    }

    private static void seedHangmanWordsIfNeeded(Connection connection, Statement statement) throws SQLException {
        try (var resultSet = statement.executeQuery("SELECT COUNT(*) FROM hangman_words")) {
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return;
            }
        }

        String insert = """
                INSERT INTO hangman_words (difficulty, word, category, hint) VALUES
                ('FACILE', 'PRINCE', 'Clash', 'Un combattant melee avec une lance'),
                ('FACILE', 'GEANT', 'Clash', 'Un tank tres lent'),
                ('FACILE', 'SORCIER', 'Clash', 'Il lance des projectiles magiques'),
                ('FACILE', 'ARENE', 'Jeu', 'Lieu des affrontements'),
                ('FACILE', 'COFFRE', 'Jeu', 'Il contient des recompenses'),
                ('FACILE', 'CARTE', 'Jeu', 'Unite ou sort a poser'),
                ('FACILE', 'DRAGON', 'Creature', 'Il vole et souffle des flammes'),
                ('FACILE', 'ROI', 'Univers', 'Il defend une tour principale'),
                ('FACILE', 'TOUR', 'Univers', 'Structure defensive'),
                ('FACILE', 'BATAILLE', 'Action', 'Affrontement en temps reel'),
                ('NORMAL', 'MORTIER', 'Clash', 'Batiment qui tire de loin'),
                ('NORMAL', 'MOUSQUETAIRE', 'Clash', 'Unite a distance precise'),
                ('NORMAL', 'MIRROIR', 'Strategie', 'Duplique la carte precedente'),
                ('NORMAL', 'CHEVALIER', 'Clash', 'Un guerrier peu couteux'),
                ('NORMAL', 'BARBARES', 'Clash', 'Groupe de combattants robustes'),
                ('NORMAL', 'ARCHERS', 'Clash', 'Duo d''unites a distance'),
                ('NORMAL', 'ELIXIR', 'Ressource', 'Permet de jouer des cartes'),
                ('NORMAL', 'TORNADE', 'Sort', 'Regroupe les unites ennemies'),
                ('NORMAL', 'DEMOLITION', 'Action', 'Destruction complete d''une cible'),
                ('NORMAL', 'CHAMPION', 'Classe', 'Unite avec pouvoir special'),
                ('EXPERT', 'SEISME', 'Sort', 'Affecte batiments et troupes au sol'),
                ('EXPERT', 'SQUELETTE', 'Creature', 'Unite tres fragile mais rapide'),
                ('EXPERT', 'CATAPULTE', 'Arme', 'Machine de siege a trajectoire courbe'),
                ('EXPERT', 'LABYRINTHE', 'Concept', 'Chemin complexe avec impasses'),
                ('EXPERT', 'STRATEGIQUE', 'Concept', 'Base sur l''anticipation'),
                ('EXPERT', 'ELECTROCUTION', 'Effet', 'Impact electrique violent'),
                ('EXPERT', 'PRECIPITATION', 'Concept', 'Action trop rapide'),
                ('EXPERT', 'METAMORPHOSE', 'Concept', 'Changement de forme'),
                ('EXPERT', 'CONCENTRATION', 'Concept', 'Etat de focus maximal'),
                ('EXPERT', 'SYNCHRONISATION', 'Concept', 'Coordination parfaite');
                """;

        statement.executeUpdate(insert);
    }
}
