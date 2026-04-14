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

        String createTrueOrFalseTable = """
                CREATE TABLE IF NOT EXISTS true_or_false (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  question_text TEXT NOT NULL UNIQUE,
                  correct_answer INTEGER NOT NULL CHECK (correct_answer IN (0, 1))
                )
                """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute(createUsersTable);
            statement.execute(createTrueOrFalseTable);
            seedTrueOrFalseQuestions(statement);
        }
    }

    private static void seedTrueOrFalseQuestions(Statement statement) throws SQLException {
        statement.executeUpdate("""
                INSERT OR IGNORE INTO true_or_false(question_text, correct_answer) VALUES
                ('Le P.E.K.K.A est une unite de type feminin.', 1),
                ('La decharge (Zap) peut reinitialiser la tour de l''enfer.', 1),
                ('Le Chevalier coute 4 elixirs.', 0),
                ('Le Sort de Buche peut toucher les unites aeriennes.', 0),
                ('Le Geant Royal attaque uniquement les batiments.', 1),
                ('La Princesse peut tirer sur une tour sans etre ciblee par celle-ci.', 1),
                ('Le Zappy met 10 secondes a charger son tir.', 0),
                ('Le Mineur inflige moins de degats aux tours qu''aux unites.', 1),
                ('Le Sort de Foudre cible les trois unites ayant le plus de points de vie.', 1),
                ('L''Electro-Sorcier peut attaquer deux cibles a la fois.', 1),
                ('Le Bourreau peut toucher les unites aeriennes avec sa hache.', 1),
                ('Le Golem de Glace explose en infligeant des degats a sa mort.', 1),
                ('La Sorciere de Nuit invoque des chauves-souris.', 1),
                ('Le Sort de Rage augmente les degats des unites.', 0),
                ('Le Chevaucheur de Cochon peut sauter par-dessus la riviere.', 1),
                ('Une carte "Champion" peut etre cyclee comme une carte normale.', 0),
                ('La Tour de l''Enfer perd de la puissance de degats au fil du temps.', 0),
                ('Le Molosse de Lave explose en liberant des Roquets de Lave.', 1),
                ('Le P.E.K.K.A est plus rapide que le Mini P.E.K.K.A.', 0),
                ('Le Tronc (The Log) repousse toutes les unites du jeu, y compris les aeriennes.', 0)
                """);
    }
}

