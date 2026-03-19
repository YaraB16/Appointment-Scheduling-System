package com.appointment.Domain;

import java.util.Objects;
import java.util.UUID;

public class User {
private final String id;
private final String name;
private final String email;
private final String password;
private final String role;

    public User(String name, String email, String password, String role) {
        this.password = password;
        this.role = role;
        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
    }
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
