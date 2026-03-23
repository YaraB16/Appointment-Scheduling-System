package com.appointment.Domain;
/**
 * Administrator user with username/password.
 * (Phase 1: simple in-memory auth)
 * @author team
 * @version 1.0
 */
public class Administrator {
    private final String username;
    private final String password; // Phase 1: plain text (لاحقاً hashing)

    public Administrator(String username, String password) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("password required");
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
