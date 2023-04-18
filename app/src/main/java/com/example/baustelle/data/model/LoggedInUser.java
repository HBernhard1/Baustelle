package com.example.baustelle.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private final String userId;
    private final String displayName;

    private final String userName;

    public LoggedInUser(String userId, String displayName, String userName) {
        this.userId = userId;
        this.displayName = displayName;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }
    public String getDisplayName() {
        return displayName;
    }

    public String getUserName() {
        return userName;
    }
}