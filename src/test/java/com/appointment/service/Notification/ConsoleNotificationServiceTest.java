package com.appointment.service.Notification;

import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleNotificationServiceTest {

    @Test
    void send_printsNotificationMessage() throws Exception {
        ConsoleNotificationService service = new ConsoleNotificationService();
        User user = new User("Yara", "yara@gmail.com", "1234", UserRole.USER);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
            service.send(user, "hello");
        } finally {
            System.setOut(originalOut);
        }

        String printed = out.toString(StandardCharsets.UTF_8);

        assertTrue(printed.contains("[NOTIFY]"));
        assertTrue(printed.contains("to=Yara"));
        assertTrue(printed.contains("msg=hello"));
    }

    @Test
    void send_throwsException_whenUserIsNull() {
        ConsoleNotificationService service = new ConsoleNotificationService();

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> service.send(null, "hello")
        );

        assertEquals("user is required", ex.getMessage());
    }

    @Test
    void send_treatsNullMessageAsEmptyString() throws Exception {
        ConsoleNotificationService service = new ConsoleNotificationService();
        User user = new User("Yara", "yara@gmail.com", "1234", UserRole.USER);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
            service.send(user, null);
        } finally {
            System.setOut(originalOut);
        }

        String printed = out.toString(StandardCharsets.UTF_8);

        assertTrue(printed.contains("[NOTIFY]"));
        assertTrue(printed.contains("to=Yara"));
        assertTrue(printed.contains("msg="));
    }
}