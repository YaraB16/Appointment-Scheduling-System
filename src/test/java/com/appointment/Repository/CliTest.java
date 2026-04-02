package com.appointment.Repository;

import com.appointment.service.AuthService;
import com.appointment.service.BookingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CliTest {

    @Test
    void shortId_returnsShortenedId() throws Exception {
        Cli cli = new Cli(mock(AuthService.class), mock(BookingService.class));

        var method = Cli.class.getDeclaredMethod("shortId", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(cli, "1234567890abcdef");

        assertEquals("12345678", result);
    }

    @Test
    void shortId_returnsSame_whenShort() throws Exception {
        Cli cli = new Cli(mock(AuthService.class), mock(BookingService.class));

        var method = Cli.class.getDeclaredMethod("shortId", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(cli, "1234");

        assertEquals("1234", result);
    }

    @Test
    void requireAdmin_throwsException_whenNotLoggedIn() throws Exception {
        Cli cli = new Cli(mock(AuthService.class), mock(BookingService.class));

        var method = Cli.class.getDeclaredMethod("requireAdmin");
        method.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> method.invoke(cli));

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Admin login required"));
    }
}