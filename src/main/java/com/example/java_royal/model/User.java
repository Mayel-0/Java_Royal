package com.example.java_royal.model;

public class User {
    private final long id;
    private final String username;
    private final String email;
    private final int currentLevel;
    private final int currentXp;
    private final int totalXp;

    public User(long id, String username, String email, int currentLevel, int currentXp, int totalXp) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.currentLevel = currentLevel;
        this.currentXp = currentXp;
        this.totalXp = totalXp;
    }

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
}

