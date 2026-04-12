package com.appointment.service.Notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmtpConfigTest {

    @Test
    void shouldStoreAllConstructorValues() {
        SmtpConfig config = new SmtpConfig(
                "smtp.gmail.com",
                587,
                "test@gmail.com",
                "Fake-app-password"
        );

        assertEquals("smtp.gmail.com", config.getHost());
        assertEquals(587, config.getPort());
        assertEquals("test@gmail.com", config.getUsername());
        assertEquals("Fake-app-password", config.getPassword());
    }
}