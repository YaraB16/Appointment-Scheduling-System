package com.appointment.Repository;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentType;
import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import com.appointment.service.AuthService;
import com.appointment.service.BookingService;
import com.appointment.value.TimeSlot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cli {
    private final AuthService authService;
    private final BookingService bookingService;
    private static final String UNKNOWN_OPTION_MESSAGE = "Unknown option.";
    private static final Logger LOGGER = Logger.getLogger(Cli.class.getName());
    private User loggedInAdmin;

    public Cli(AuthService authService, BookingService bookingService) {
        this.authService = authService;
        this.bookingService = bookingService;
    }

    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            LOGGER.info("""
                    
                    MAIN MENU
                    1) Continue as User
                    2) Continue as Admin
                    0) Exit
                    """);
            LOGGER.info("> ");
            String choice = in.nextLine().trim();

            switch (choice) {
                case "1" -> runUserMenu(in);
                case "2" -> runAdminMenu(in);
                case "0" -> {
                    LOGGER.info("Bye.");
                    return;
                }
                default -> LOGGER.info(UNKNOWN_OPTION_MESSAGE);
            }
        }
    }

    private void runUserMenu(Scanner in) {
        while (true) {
            LOGGER.info("""
                    
                    USER MENU
                    1) List available slots
                    2) Book appointment
                    3) Cancel future booking
                    0) Back
                    """);
            LOGGER.info("> ");
            if (!in.hasNextLine()) {
                return;
            }
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> listAvailable();
                    case "2" -> book(in);
                    case "3" -> cancel(in);
                    case "0" -> {
                        return;
                    }
                    default -> LOGGER.info(UNKNOWN_OPTION_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.info("Error: " + e.getMessage());
            }
        }
    }

    private void runAdminMenu(Scanner in) {
        if (loggedInAdmin == null) {
            adminLogin(in);
            if (loggedInAdmin == null) {
                return;
            }
        }

        while (true) {
            LOGGER.info("""
        
        ADMIN MENU
        1) List available slots
        2) Create slot
        3) Modify slot
        4) Cancel reservation
        5) Send reminders
        6) Logout
        0) Back
        """);
            LOGGER.info("> ");

            if (!in.hasNextLine()) {
                return;
            }
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> listAvailable();
                    case "2" -> createSlot(in);
                    case "3" -> adminModifySlot(in);
                    case "4" -> adminCancelReservation(in);
                    case "5" -> sendReminders(in);
                    case "6" -> {
                        adminLogout();
                        return;
                    }
                    case "0" -> {
                        return;
                    }
                    default -> LOGGER.info(UNKNOWN_OPTION_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.info("Error: " + e.getMessage());
            }
        }
    }
    private void adminCancelReservation(Scanner in) {
        requireAdmin();

        List<Appointment> allAppointments = bookingService.listAll();
        if (allAppointments.isEmpty()) {
            LOGGER.info("No appointments found yet.");
            return;
        }

        LOGGER.info("Appointment id to cancel (short or full): ");
        String id = in.nextLine().trim();

        bookingService.cancelAndMakeAvailable(id);
        LOGGER.info("Reservation cancelled successfully by admin.");
    }
    private void listAvailable() {
        List<Appointment> all = bookingService.viewAvailableSlots();
        if (all.isEmpty()) {
            LOGGER.info("(empty)");
            return;
        }

        LOGGER.info("Available slots:");
        for (Appointment a : all) {
            LOGGER.log(
                    Level.INFO,
                    "{0} | fullId={1} | type={2} | slot={3} | participants={4} | status={5}",
                    new Object[]{
                            shortId(a.getId()),
                            a.getId(),
                            a.getType(),
                            a.getSlot(),
                            a.getParticipantCount(),
                            a.getStatus()
                    }
            );
        }
    }

    private void createSlot(Scanner in) {
        requireAdmin();


        while (true) {
            try {
                AppointmentType type = readAppointmentType(in);
                LOGGER.info("Note: Maximum allowed appointment duration is 30 minutes.");

                LocalDateTime start = readDateTime(in, "Start (dd-MM-yyyy HH:mm): ");
                LocalDateTime end = readDateTime(in, "End (dd-MM-yyyy HH:mm): ");

                Appointment appointment = bookingService.createSlot(new TimeSlot(start, end), type);
                LOGGER.info("Created slot successfully.");
                LOGGER.log(Level.INFO,
                        "Short id: {0}",
                        shortId(appointment.getId()));
                LOGGER.info("Full  id: " + appointment.getId());
                return;

            } catch (IllegalArgumentException e) {
                LOGGER.info("Invalid slot: " + e.getMessage());
                LOGGER.info("Please enter the slot again.");
            }
        }
    }

    private void book(Scanner in) {
        List<Appointment> available = bookingService.viewAvailableSlots();

        if (available.isEmpty()) {
            LOGGER.info("No available appointments.");
            return;
        }

        LOGGER.info("Choose appointment id: ");
        String appointmentId = in.nextLine().trim();

        LOGGER.info("User name: ");
        String name = in.nextLine().trim();

        LOGGER.info("User email: ");
        String email = in.nextLine().trim();

        User user = new User(name, email, "N/A", UserRole.USER);

        Appointment a = bookingService.book(appointmentId, user);

        LOGGER.info("Booked successfully: " + a.getId());
        LOGGER.info("A confirmation email/notification will be sent automatically.");
    }

    private void cancel(Scanner in) {
        LOGGER.info("Appointment id to cancel (short or full): ");
        String id = in.nextLine().trim();

        bookingService.cancelFutureOnly(id);
        LOGGER.info("Canceled successfully and slot is available again.");
    }

    private void adminLogin(Scanner in) {
        LOGGER.info("Admin email: ");
        String email = in.nextLine().trim();

        LOGGER.info("Admin password: ");
        String password = in.nextLine().trim();

        User admin = authService.login(email, password);
        if (admin == null) {
            LOGGER.info("Invalid credentials.");
            return;
        }

        loggedInAdmin = admin;
        LOGGER.info("Admin logged in: " + admin.getEmail());
    }

    private void adminLogout() {
        if (loggedInAdmin == null) {
            LOGGER.info("No admin is currently logged in.");
            return;
        }

        LOGGER.info("Admin logged out: " + loggedInAdmin.getEmail());
        loggedInAdmin = null;
    }

    private void sendReminders(Scanner in) {
        requireAdmin();

        LOGGER.info("Send reminders for how many upcoming hours? ");
        String raw = in.nextLine().trim();
        int hours;

        try {
            hours = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            LOGGER.info("Please enter a valid integer.");
            return;
        }

        int count = bookingService.sendRemindersForUpcomingHours(hours);
        LOGGER.log(Level.INFO, "Reminder messages sent: {0}", count);
    }

    private void adminModifySlot(Scanner in) {
        requireAdmin();
        List<Appointment> allAppointments = bookingService.listAll();
        if (allAppointments.isEmpty()) {
            LOGGER.info("No appointments found yet.");
            return;
        }
        LOGGER.info("Appointment id to modify (short or full): ");
        String id = in.nextLine().trim();

        LocalDateTime newStart = readDateTime(in, "New start (dd-MM-yyyy HH:mm): ");
        LocalDateTime newEnd = readDateTime(in, "New end (dd-MM-yyyy HH:mm): ");

        Appointment updated = bookingService.modifyAsAdmin(id, new TimeSlot(newStart, newEnd));
        LOGGER.info("Slot modified successfully: " + updated.getId());
    }

    private AppointmentType readAppointmentType(Scanner in) {
        LOGGER.info("Type (URGENT/FOLLOW_UP/ASSESSMENT/VIRTUAL/IN_PERSON/INDIVIDUAL/GROUP): ");
        String raw = in.nextLine().trim().toUpperCase();

        try {
            return AppointmentType.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid appointment type.");
        }
    }


    private LocalDateTime readDateTime(Scanner in, String prompt) {
        LOGGER.info(prompt);
        String raw = in.nextLine().trim();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            return LocalDateTime.parse(raw, formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid format. Use: dd-MM-yyyy HH:mm (example: 11-04-2026 14:30)");
        }
    }

    private void requireAdmin() {
        if (loggedInAdmin == null) {
            throw new IllegalStateException("Access denied. Admin login required.");
        }
    }

    private String shortId(String id) {
        return id.length() <= 8 ? id : id.substring(0, 8);
    }
}