package com.example.java_royal.model;

/**
 * Modèle représentant un utilisateur avec toutes ses données.
 * Inclut les informations de profil et les statistiques de jeu (niveau, XP).
 */
public class User {
    private final long id;
    private final String username;
    private final String email;
    private final int currentLevel;
    private final int currentXp;
    private final int totalXp;

    /**
     * Constructeur complet pour créer un User avec toutes ses données
     */
    public User(long id, String username, String email, int currentLevel, int currentXp, int totalXp) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.currentLevel = currentLevel;
        this.currentXp = currentXp;
        this.totalXp = totalXp;
    }

    /**
     * Constructeur simplifié (pour compatibilité)
     */
    public User(long id, String username) {
        this.id = id;
        this.username = username;
        this.email = "";
        this.currentLevel = 1;
        this.currentXp = 0;
        this.totalXp = 0;
    }

    // ==================== GETTERS ====================

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentXp() {
        return currentXp;
    }

    public int getTotalXp() {
        return totalXp;
    }

    /**
     * Calcule le seuil XP pour le prochain niveau
     * Formule: XP_Suivant = Niveau_Actuel × 100
     */
    public int getNextLevelThreshold() {
        return currentLevel * 100;
    }

    /**
     * Retourne le pourcentage de progression vers le niveau suivant
     */
    public double getXpProgressPercentage() {
        int threshold = getNextLevelThreshold();
        if (threshold <= 0) return 0;
        return Math.min(1.0, (double) currentXp / threshold);
    }


    /**
     * Retourne une représentation textuelle du User
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", currentLevel=" + currentLevel +
                ", currentXp=" + currentXp +
                ", totalXp=" + totalXp +
                '}';
    }
}

