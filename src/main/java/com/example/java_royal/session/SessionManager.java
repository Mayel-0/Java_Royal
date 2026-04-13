package com.example.java_royal.session;

import com.example.java_royal.model.User;

public final class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private User currentUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}

