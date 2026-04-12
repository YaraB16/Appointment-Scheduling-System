package com.appointment.Domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserSuccessfully() {
        User user = new User("Yara", "yara@test.com", "1234", UserRole.USER);

        assertNotNull(user.getId());
        assertEquals("Yara", user.getName());
        assertEquals("yara@test.com", user.getEmail());
        assertEquals("1234", user.getPassword());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    void shouldFormatToStringCorrectly() {
        User user = new User("Yara", "yara@test.com", "1234", UserRole.USER);

        assertEquals("Yara (yara@test.com)", user.toString());
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(NullPointerException.class,
                () -> new User(null, "yara@test.com", "1234", UserRole.USER));
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        assertThrows(NullPointerException.class,
                () -> new User("Yara", null, "1234", UserRole.USER));
    }

    @Test
    void shouldThrowWhenPasswordIsNull() {
        assertThrows(NullPointerException.class,
                () -> new User("Yara", "yara@test.com", null, UserRole.USER));
    }

    @Test
    void shouldThrowWhenRoleIsNull() {
        assertThrows(NullPointerException.class,
                () -> new User("Yara", "yara@test.com", "1234", null));
    }
}