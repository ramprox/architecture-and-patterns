package ru.ramprox.server.model;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private boolean isAuthenticated;

    public String getName() {
        return name;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public User(String name, boolean isAuthenticated) {
        this.name = name;
        this.isAuthenticated = isAuthenticated;
    }
}
