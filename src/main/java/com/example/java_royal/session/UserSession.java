package com.example.java_royal.session;

public final class UserSession {
    private static UserSession instance;

    private long id;
    private String username;
    private String email;

    private UserSession() {
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void update(long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public void clear() {
        this.id = 0L;
        this.username = null;
        this.email = null;
    }
}
