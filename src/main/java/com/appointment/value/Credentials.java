package com.appointment.value;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing login credentials.
 * Phase 1: can be used with raw password or hashed password.
 */
public final class Credentials {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String email;
    private final String passwordHash;

    private Credentials(String email, String passwordHash) {
        this.email = Objects.requireNonNull(email, "email").trim();
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        if (this.email.isBlank()) throw new IllegalArgumentException("email is required");
        if (!EMAIL_PATTERN.matcher(this.email).matches()) throw new IllegalArgumentException("invalid email format");
        if (this.passwordHash.isBlank()) throw new IllegalArgumentException("password hash is required");
    }

    /** Creates credentials from a raw password by hashing it. */
    public static Credentials fromRawPassword(String email, String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword");
        if (rawPassword.isBlank()) throw new IllegalArgumentException("password is required");
        return new Credentials(email, sha256(rawPassword));
    }

    /** Creates credentials when you already have a stored hash. */
    public static Credentials fromPasswordHash(String email, String passwordHash) {
        return new Credentials(email, passwordHash);
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    /** Checks if a raw password matches the stored hash. */
    public boolean matchesRawPassword(String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword");
        return passwordHash.equals(sha256(rawPassword));
    }

    private static String sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // Should never happen in Java
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Credentials{email='" + email + "', passwordHash='***'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credentials other)) return false;
        return email.equalsIgnoreCase(other.email) && passwordHash.equals(other.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email.toLowerCase(), passwordHash);
    }
}