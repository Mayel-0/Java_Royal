package com.example.java_royal.session;

/**
 * Gestionnaire de session utilisateur - Singleton.
 * Stocke les données de l'utilisateur actuellement connecté.
 * Utilisé pour accès rapide sans requête DB.
 */
public final class UserSession {
    private static UserSession instance;

    private long id;
    private String username;
    private String email;
    private int currentLevel = 1;      // ← Initialiser à 1 (au lieu de 0)
    private int currentXp = 0;          // ← Initialiser à 0

    private UserSession() {
    }

    /**
     * Retourne l'instance unique du Singleton
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
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

    // ==================== SETTERS ====================

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    /**
     * Met à jour tous les champs de la session en une seule opération
     */
    public void update(long id, String username, String email, int currentLevel, int currentXp) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.currentLevel = currentLevel;
        this.currentXp = currentXp;
    }

    /**
     * Efface les données de session (déconnexion)
     */
    public void clear() {
        this.id = 0L;
        this.username = null;
        this.email = null;
        this.currentLevel = 1;
        this.currentXp = 0;
    }

    /**
     * Retourne une représentation textuelle de la session
     */
    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", currentLevel=" + currentLevel +
                ", currentXp=" + currentXp +
                '}';
    }
}

