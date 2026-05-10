package com.appointment.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {

    @Test
    void fromRawPassword_createsCredentialsSuccessfully() {
        Credentials credentials = Credentials.fromRawPassword("user@test.com", "secret123");

        assertEquals("user@test.com", credentials.getEmail());
        assertNotNull(credentials.getPasswordHash());
        assertFalse(credentials.getPasswordHash().isBlank());
    }

    @Test
    void fromRawPassword_trimsEmail() {
        Credentials credentials = Credentials.fromRawPassword("  user@test.com  ", "secret123");

        assertEquals("user@test.com", credentials.getEmail());
    }

    @Test
    void fromRawPassword_throwsException_whenRawPasswordIsNull() {
        assertThrows(NullPointerException.class,
                () -> Credentials.fromRawPassword("user@test.com", null));
    }

    @Test
    void fromRawPassword_throwsException_whenRawPasswordIsBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Credentials.fromRawPassword("user@test.com", "   "));

        assertEquals("password is required", ex.getMessage());
    }

    @Test
    void fromPasswordHash_createsCredentialsSuccessfully() {
        Credentials credentials = Credentials.fromPasswordHash("user@test.com", "hashed-value");

        assertEquals("user@test.com", credentials.getEmail());
        assertEquals("hashed-value", credentials.getPasswordHash());
    }

    @Test
    void fromPasswordHash_throwsException_whenEmailIsInvalid() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Credentials.fromPasswordHash("invalid-email", "hashed-value"));

        assertEquals("invalid email format", ex.getMessage());
    }

    @Test
    void fromPasswordHash_throwsException_whenEmailIsBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Credentials.fromPasswordHash("   ", "hashed-value"));

        assertEquals("email is required", ex.getMessage());
    }

    @Test
    void fromPasswordHash_throwsException_whenPasswordHashIsBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Credentials.fromPasswordHash("user@test.com", "   "));

        assertEquals("password hash is required", ex.getMessage());
    }

    @Test
    void matchesRawPassword_returnsTrueForMatchingPassword() {
        Credentials credentials = Credentials.fromRawPassword("user@test.com", "secret123");

        assertTrue(credentials.matchesRawPassword("secret123"));
    }

    @Test
    void matchesRawPassword_returnsFalseForDifferentPassword() {
        Credentials credentials = Credentials.fromRawPassword("user@test.com", "secret123");

        assertFalse(credentials.matchesRawPassword("wrong-password"));
    }

    @Test
    void matchesRawPassword_throwsException_whenRawPasswordIsNull() {
        Credentials credentials = Credentials.fromRawPassword("user@test.com", "secret123");

        assertThrows(NullPointerException.class,
                () -> credentials.matchesRawPassword(null));
    }

    @Test
    void toString_hidesPasswordHash() {
        Credentials credentials = Credentials.fromRawPassword("user@test.com", "secret123");

        String text = credentials.toString();

        assertTrue(text.contains("user@test.com"));
        assertTrue(text.contains("***"));
        assertFalse(text.contains(credentials.getPasswordHash()));
    }

    @Test
    void equals_returnsTrueForSameEmailIgnoringCaseAndSameHash() {
        Credentials c1 = Credentials.fromPasswordHash("USER@test.com", "same-hash");
        Credentials c2 = Credentials.fromPasswordHash("user@test.com", "same-hash");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void equals_returnsFalseForDifferentHash() {
        Credentials c1 = Credentials.fromPasswordHash("user@test.com", "hash-1");
        Credentials c2 = Credentials.fromPasswordHash("user@test.com", "hash-2");

        assertNotEquals(c1, c2);
    }

    @Test
    void equals_returnsFalseForDifferentType() {
        Credentials c1 = Credentials.fromPasswordHash("user@test.com", "hash-1");

        assertNotEquals(c1, "not credentials");
    }
    @Test
    void fromPasswordHash_throwsException_whenEmailIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> Credentials.fromPasswordHash(null, "hashed-value"));

        assertEquals("email", ex.getMessage());
    }

    @Test
    void fromPasswordHash_throwsException_whenPasswordHashIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> Credentials.fromPasswordHash("user@test.com", null));

        assertEquals("passwordHash", ex.getMessage());
    }

    @Test
    void fromPasswordHash_acceptsEmailWithSubdomain() {
        Credentials credentials =
                Credentials.fromPasswordHash("user@mail.test.com", "hashed-value");

        assertEquals("user@mail.test.com", credentials.getEmail());
    }

    @Test
    void fromPasswordHash_throwsException_whenEmailHasSpacesInside() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Credentials.fromPasswordHash("user name@test.com", "hashed-value"));

        assertEquals("invalid email format", ex.getMessage());
    }

    @Test
    void equals_returnsTrue_whenSameObject() {
        Credentials credentials =
                Credentials.fromPasswordHash("user@test.com", "hash-1");

        assertEquals(credentials, credentials);
    }

    @Test
    void equals_returnsFalseForDifferentEmail() {
        Credentials c1 = Credentials.fromPasswordHash("user1@test.com", "same-hash");
        Credentials c2 = Credentials.fromPasswordHash("user2@test.com", "same-hash");

        assertNotEquals(c1, c2);
    }
}