package com.kenzie.appserver.service.model;


import java.util.UUID;

public class User {
    private final String username;
    private final String name;
    private final String hashedPassword;

    public User(String username, String name, String hashedPassword) {
        this.username = username;
        this.name = name;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
