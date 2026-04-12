package com.appointment;

import com.appointment.Repository.Cli;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void buildCli_createsCliSuccessfully_whenCredentialsAreValid() {
        Cli cli = App.buildCli("sender@gmail.com", "app-password");

        assertNotNull(cli);
    }

    @Test
    void buildCli_throwsException_whenUsernameIsNull() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> App.buildCli(null, "app-password")
        );

        assertEquals(
                "EMAIL_USERNAME and EMAIL_PASSWORD must be set in environment variables.",
                ex.getMessage()
        );
    }

    @Test
    void buildCli_throwsException_whenPasswordIsNull() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> App.buildCli("sender@gmail.com", null)
        );

        assertEquals(
                "EMAIL_USERNAME and EMAIL_PASSWORD must be set in environment variables.",
                ex.getMessage()
        );
    }

    @Test
    void buildCli_throwsException_whenUsernameIsBlank() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> App.buildCli("   ", "app-password")
        );

        assertEquals(
                "EMAIL_USERNAME and EMAIL_PASSWORD must be set in environment variables.",
                ex.getMessage()
        );
    }

    @Test
    void buildCli_throwsException_whenPasswordIsBlank() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> App.buildCli("sender@gmail.com", "   ")
        );

        assertEquals(
                "EMAIL_USERNAME and EMAIL_PASSWORD must be set in environment variables.",
                ex.getMessage()
        );
    }
}