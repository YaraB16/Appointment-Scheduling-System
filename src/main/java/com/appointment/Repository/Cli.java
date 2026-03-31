package com.appointment.Repository;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentType;
import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import com.appointment.service.AuthService;
import com.appointment.service.BookingService;
import com.appointment.value.TimeSlot;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private final AuthService authService;
    private final BookingService bookingService;

    private User loggedInAdmin;

    public Cli(AuthService authService, BookingService bookingService) {
        this.authService = authService;
        this.bookingService = bookingService;
    }

    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    
                    1) List available slots
                    2) Admin: Create slot
                    3) Book
                    4) Cancel future booking
                    5) Admin login
                    6) Admin logout
                    7) Send reminders (demo)
                    8) Admin: Modify slot
                    0) Exit
                    """);
            System.out.print("> ");
            String choice = in.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> listAvailable();
                    case "2" -> createSlot(in);
                    case "3" -> book(in);
                    case "4" -> cancel(in);
                    case "5" -> adminLogin(in);
                    case "6" -> adminLogout();
                    case "7" -> sendReminders(in);
                    case "8" -> adminModifySlot(in);
                    case "0" -> {
                        System.out.println("Bye.");
                        return;
                    }
                    default -> System.out.println("Unknown option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listAvailable() {
        List<Appointment> all = bookingService.viewAvailableSlots();
        if (all.isEmpty()) {
            System.out.println("(empty)");
            return;
        }

        System.out.println("Available slots:");
        for (Appointment a : all) {
            System.out.println(
                    shortId(a.getId()) + " | fullId=" + a.getId()
                            + " | type=" + a.getType()
                            + " | slot=" + a.getSlot()
                            + " | participants=" + a.getParticipantCount()
                            + " | status=" + a.getStatus()
            );
        }
    }

    private void createSlot(Scanner in) {
        requireAdmin();

        AppointmentType type = readAppointmentType(in);
        LocalDateTime start = readDateTime(in, "Start (yyyy-MM-ddTHH:mm): ");
        LocalDateTime end = readDateTime(in, "End (yyyy-MM-ddTHH:mm): ");

        Appointment appointment = bookingService.createSlot(new TimeSlot(start, end), type);
        System.out.println("Created slot successfully.");
        System.out.println("Short id: " + shortId(appointment.getId()));
        System.out.println("Full  id: " + appointment.getId());
    }

    private void book(Scanner in) {
        List<Appointment> available = bookingService.viewAvailableSlots();

        if (available.isEmpty()) {
            System.out.println("No available appointments.");
            return;
        }

        System.out.print("Choose appointment id: ");
        String appointmentId = in.nextLine().trim();

        System.out.print("User name: ");
        String name = in.nextLine().trim();

        System.out.print("User email: ");
        String email = in.nextLine().trim();

        User user = new User(name, email, "N/A", UserRole.USER);

        Appointment a = bookingService.book(appointmentId, user);

        System.out.println("Booked successfully: " + a.getId());
    }
    private void cancel(Scanner in) {
        System.out.print("Appointment id to cancel (short or full): ");
        String id = in.nextLine().trim();

        bookingService.cancelFutureOnly(id);
        System.out.println("Canceled successfully and slot is available again.");
    }

    private void adminLogin(Scanner in) {
        System.out.print("Admin email: ");
        String email = in.nextLine().trim();

        System.out.print("Admin password: ");
        String password = in.nextLine().trim();

        User admin = authService.login(email, password);
        if (admin == null) {
            System.out.println("Invalid credentials.");
            return;
        }

        loggedInAdmin = admin;
        System.out.println("Admin logged in: " + admin.getEmail());
    }

    private void adminLogout() {
        if (loggedInAdmin == null) {
            System.out.println("No admin is currently logged in.");
            return;
        }

        System.out.println("Admin logged out: " + loggedInAdmin.getEmail());
        loggedInAdmin = null;
    }

    private void sendReminders(Scanner in) {
        System.out.print("Send reminders for how many upcoming hours? ");
        String raw = in.nextLine().trim();
        int hours;

        try {
            hours = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid integer.");
            return;
        }

        int count = bookingService.sendRemindersForUpcomingHours(hours);
        System.out.println("Reminder messages sent: " + count);
    }

    private void adminModifySlot(Scanner in) {
        requireAdmin();

        System.out.print("Appointment id to modify (short or full): ");
        String id = in.nextLine().trim();

        LocalDateTime newStart = readDateTime(in, "New start (yyyy-MM-ddTHH:mm): ");
        LocalDateTime newEnd = readDateTime(in, "New end (yyyy-MM-ddTHH:mm): ");

        Appointment updated = bookingService.modifyAsAdmin(id, new TimeSlot(newStart, newEnd));
        System.out.println("Slot modified successfully: " + updated.getId());
    }

    private AppointmentType readAppointmentType(Scanner in) {
        System.out.print("Type (URGENT/FOLLOW_UP/ASSESSMENT/VIRTUAL/IN_PERSON/INDIVIDUAL/GROUP): ");
        String raw = in.nextLine().trim().toUpperCase();

        try {
            return AppointmentType.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid appointment type.");
        }
    }

    private LocalDateTime readDateTime(Scanner in, String prompt) {
        System.out.print(prompt);
        String raw = in.nextLine().trim();

        try {
            return LocalDateTime.parse(raw);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-ddTHH:mm");
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