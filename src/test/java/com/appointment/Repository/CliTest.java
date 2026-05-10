package com.appointment.Repository;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentType;
import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import com.appointment.service.AuthService;
import com.appointment.service.BookingService;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CliTest {

    @Test
    void run_exitsImmediately() throws Exception {
        AuthService authService = mock(AuthService.class);
        BookingService bookingService = mock(BookingService.class);

        String output = runCli(authService, bookingService, "0\n");

        assertTrue(output.contains("MAIN MENU"));
        assertTrue(output.contains("Bye."));
    }

    @Test
    void userMenu_listsEmptySlotsAndBackThenExit() throws Exception {
        AuthService authService = mock(AuthService.class);
        BookingService bookingService = mock(BookingService.class);

        when(bookingService.viewAvailableSlots()).thenReturn(List.of());

        String output = runCli(authService, bookingService,
                "1\n" +   // user
                        "1\n" +   // list
                        "0\n" +   // back
                        "0\n");   // exit

        assertTrue(output.contains("USER MENU"));
        assertTrue(output.contains("(empty)"));
        verify(bookingService, atLeastOnce()).viewAvailableSlots();
    }

    @Test
    void userMenu_bookAppointment_callsBookingService() throws Exception {
        AuthService authService = mock(AuthService.class);
        BookingService bookingService = mock(BookingService.class);

        Appointment appointment = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        when(bookingService.viewAvailableSlots()).thenReturn(List.of(appointment));
        when(bookingService.book(eq(appointment.getId()), any(User.class))).thenReturn(appointment);

        String output = runCli(authService, bookingService,
                "1\n" +                       // user
                        "2\n" +                       // book
                        appointment.getId() + "\n" +  // appointment id
                        "Yara\n" +                    // name
                        "yara@test.com\n" +           // email
                        "0\n" +                       // back
                        "0\n");                       // exit

        assertTrue(output.contains("Booked successfully"));
        verify(bookingService).book(eq(appointment.getId()), any(User.class));
    }

    @Test
    void userMenu_cancelFutureBooking_callsBookingService() throws Exception {
        AuthService authService = mock(AuthService.class);
        BookingService bookingService = mock(BookingService.class);

        String output = runCli(authService, bookingService,
                "1\n" +      // user
                        "3\n" +      // cancel
                        "abc123\n" + // id
                        "0\n" +      // back
                        "0\n");      // exit

        assertTrue(output.contains("Canceled successfully"));
        verify(bookingService).cancelFutureOnly("abc123");
    }

    @Test
    void adminMenu_loginFailure_returnsToMainMenu() throws Exception {
        AuthService authService = mock(AuthService.class);
        BookingService bookingService = mock(BookingService.class);

        when(authService.login("admin@mail.com", "wrong")).thenReturn(null);

        String output = runCli(authService, bookingService,
                "2\n" +              // admin
                        "admin@mail.com\n" + // email
                        "wrong\n" +          // password
                        "0\n");              // exit

        assertTrue(output.contains("Invalid credentials."));
        verify(authService).login("admin@mail.com", "wrong");
    }

    @Test
    void adminMenu_createSendRemindersAndLogout_successfully() throws Exception {
        AuthService authService = mock(AuthService.class);
        BookingService bookingService = mock(BookingService.class);

        User admin = new User("Admin", "admin@mail.com", "1234", UserRole.ADMIN);

        Appointment created = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        when(authService.login("admin@mail.com", "1234")).thenReturn(admin);
        when(bookingService.createSlot(any(TimeSlot.class), eq(AppointmentType.URGENT))).thenReturn(created);
        when(bookingService.sendRemindersForUpcomingHours(12)).thenReturn(2);

        String output = runCli(authService, bookingService,
                "2\n" +                    // admin
                        "admin@mail.com\n" +
                        "1234\n" +

                        "2\n" +                    // create slot
                        "URGENT\n" +
                        "12-04-2026 12:00\n" +
                        "12-04-2026 12:30\n" +

                        "5\n" +                    // send reminders
                        "12\n" +

                        "6\n" +                    // logout
                        "0\n");                   // exit

        // للتأكد (ممكن تحذفيه بعد ما يزبط)
        // System.out.println(output);

        assertTrue(output.contains("Admin logged in"));
        assertTrue(output.contains("Created slot successfully"));
        assertTrue(output.contains("Reminder messages sent"));
        assertTrue(output.contains("Admin logged out"));

        verify(authService).login("admin@mail.com", "1234");
        verify(bookingService).createSlot(any(TimeSlot.class), eq(AppointmentType.URGENT));
        verify(bookingService).sendRemindersForUpcomingHours(12);
    }
    @Test
    void adminMenu_invalidChoice_printsUnknownOption() throws Exception {
        AuthService authService = mock(AuthService.class);
        BookingService bookingService = mock(BookingService.class);

        User admin = new User("Admin", "admin@mail.com", "1234", UserRole.ADMIN);
        when(authService.login("admin@mail.com", "1234")).thenReturn(admin);

        String output = runCli(authService, bookingService,
                "2\n" +
                        "admin@mail.com\n" +
                        "1234\n" +
                        "9\n" +   // invalid option in admin menu
                        "6\n" +   // logout
                        "0\n");   // exit from main menu

        assertTrue(output.contains("Unknown option."));
    }

    private String runCli(AuthService authService, BookingService bookingService, String input) throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream testOut = new PrintStream(out, true, StandardCharsets.UTF_8);

        Logger cliLogger = Logger.getLogger(Cli.class.getName());
        boolean originalUseParentHandlers = cliLogger.getUseParentHandlers();
        Handler[] originalHandlers = cliLogger.getHandlers();

        Handler testLoggerHandler = new StreamHandler(testOut, new SimpleFormatter()) {
            @Override
            public synchronized void publish(java.util.logging.LogRecord record) {
                super.publish(record);
                flush();
            }
        };

        try {
            System.setIn(testIn);
            System.setOut(testOut);

            for (Handler handler : originalHandlers) {
                cliLogger.removeHandler(handler);
            }

            cliLogger.setUseParentHandlers(false);
            cliLogger.addHandler(testLoggerHandler);

            Cli cli = new Cli(authService, bookingService);
            cli.run();

            testOut.flush();
            testLoggerHandler.flush();

            return out.toString(StandardCharsets.UTF_8);

        } finally {
            cliLogger.removeHandler(testLoggerHandler);
            testLoggerHandler.close();

            for (Handler handler : originalHandlers) {
                cliLogger.addHandler(handler);
            }

            cliLogger.setUseParentHandlers(originalUseParentHandlers);

            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}