package com.appointment.value;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMessageTest {

    @Test
    void constructor_createsNotificationMessageSuccessfully() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0);

        NotificationMessage message = new NotificationMessage(
                NotificationMessage.Type.INFO,
                "Hello",
                "Body text",
                now
        );

        assertEquals(NotificationMessage.Type.INFO, message.getType());
        assertEquals("Hello", message.getTitle());
        assertEquals("Body text", message.getBody());
        assertEquals(now, message.getCreatedAt());
    }

    @Test
    void constructor_replacesNullBodyWithEmptyString() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0);

        NotificationMessage message = new NotificationMessage(
                NotificationMessage.Type.INFO,
                "Hello",
                null,
                now
        );

        assertEquals("", message.getBody());
    }

    @Test
    void constructor_throwsException_whenTypeIsNull() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0);

        assertThrows(NullPointerException.class,
                () -> new NotificationMessage(null, "Hello", "Body", now));
    }

    @Test
    void constructor_throwsException_whenTitleIsNull() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new NotificationMessage(NotificationMessage.Type.INFO, null, "Body", now));

        assertEquals("title is required", ex.getMessage());
    }

    @Test
    void constructor_throwsException_whenTitleIsBlank() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new NotificationMessage(NotificationMessage.Type.INFO, "   ", "Body", now));

        assertEquals("title is required", ex.getMessage());
    }

    @Test
    void constructor_throwsException_whenCreatedAtIsNull() {
        assertThrows(NullPointerException.class,
                () -> new NotificationMessage(NotificationMessage.Type.INFO, "Hello", "Body", null));
    }

    @Test
    void info_factory_createsInfoMessage() {
        NotificationMessage message = NotificationMessage.info("Info title", "Info body");

        assertEquals(NotificationMessage.Type.INFO, message.getType());
        assertEquals("Info title", message.getTitle());
        assertEquals("Info body", message.getBody());
        assertNotNull(message.getCreatedAt());
    }

    @Test
    void booked_factory_createsBookedMessage() {
        NotificationMessage message = NotificationMessage.booked("A123");

        assertEquals(NotificationMessage.Type.BOOKED, message.getType());
        assertEquals("Appointment confirmed", message.getTitle());
        assertTrue(message.getBody().contains("A123"));
        assertNotNull(message.getCreatedAt());
    }

    @Test
    void canceled_factory_createsCanceledMessage() {
        NotificationMessage message = NotificationMessage.canceled("A123");

        assertEquals(NotificationMessage.Type.CANCELED, message.getType());
        assertEquals("Appointment cancelled", message.getTitle());
        assertTrue(message.getBody().contains("A123"));
        assertNotNull(message.getCreatedAt());
    }

    @Test
    void render_returnsOnlyTitleWhenBodyIsBlank() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0);

        NotificationMessage message = new NotificationMessage(
                NotificationMessage.Type.INFO,
                "Hello",
                "   ",
                now
        );

        assertEquals("[INFO] Hello", message.render());
    }

    @Test
    void render_returnsTitleAndBodyWhenBodyIsNotBlank() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0);

        NotificationMessage message = new NotificationMessage(
                NotificationMessage.Type.REMINDER,
                "Reminder",
                "Appointment at 3 PM",
                now
        );

        assertEquals("[REMINDER] Reminder - Appointment at 3 PM", message.render());
    }

    @Test
    void toString_containsMainFields() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0);

        NotificationMessage message = new NotificationMessage(
                NotificationMessage.Type.INFO,
                "Hello",
                "Body text",
                now
        );

        String text = message.toString();

        assertTrue(text.contains("INFO"));
        assertTrue(text.contains("Hello"));
        assertTrue(text.contains("Body text"));
        assertTrue(text.contains("2026-04-12T12:00"));
    }
}