package com.appointment.Repository;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentType;
import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import com.appointment.service.AuthService;
import com.appointment.service.BookingService;
import com.appointment.value.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private final AuthService authService;
    private final BookingService bookingService;

    public Cli(AuthService authService, BookingService bookingService) {
        this.authService = authService;
        this.bookingService = bookingService;
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("\n1) List 2) Admin: Create slot 3) Book 4) Cancel 5) Admin login 0) Exit");
            System.out.print("> ");
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> listAll();
                    case "2" -> createSlot(in);
                    case "3" -> book(in);
                    case "4" -> cancel(in);
                    case "5" -> adminLogin(in);
                    case "0" -> { System.out.println("Bye."); return; }
                    default -> System.out.println("Unknown option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listAll() {
        List<Appointment> all = bookingService.listAll();
        if (all.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        for (Appointment a : all) {
            System.out.println(a.getId() + " | " + a.getType() + " | " + a.getSlot()
                    + " | participants=" + a.getParticipantCount() + " | " + a.getStatus());
        }
    }

    private void createSlot(Scanner in) {
        System.out.print("Type (URGENT/FOLLOW_UP/ASSESSMENT/VIRTUAL/IN_PERSON/INDIVIDUAL/GROUP): ");
        AppointmentType type = AppointmentType.valueOf(in.nextLine().trim().toUpperCase());

        System.out.print("Start (yyyy-MM-ddTHH:mm): ");
        LocalDateTime start = LocalDateTime.parse(in.nextLine().trim());
        System.out.print("End (yyyy-MM-ddTHH:mm): ");
        LocalDateTime end = LocalDateTime.parse(in.nextLine().trim());

        Appointment a = bookingService.createSlot(new TimeSlot(start, end), type);
        System.out.println("Created slot: " + a.getId());
    }

    private void book(Scanner in) {
        System.out.print("Appointment id to book: ");
        String appointmentId = in.nextLine().trim();

        System.out.print("User name: ");
        String name = in.nextLine().trim();
        System.out.print("User email: ");
        String email = in.nextLine().trim();

        User user = new User(name, email, "N/A", UserRole.USER);
        Appointment a = bookingService.book(appointmentId, user);
        System.out.println("Booked: " + a.getId());
    }

    private void cancel(Scanner in) {
        System.out.print("Appointment id: ");
        String id = in.nextLine().trim();
        bookingService.cancelAndMakeAvailable(id);
        System.out.println("Canceled & made available again: " + id);
    }

    private void adminLogin(Scanner in) {
        System.out.print("Admin email: ");
        String u = in.nextLine().trim();
        System.out.print("Admin password: ");
        String p = in.nextLine().trim();
        User admin = authService.login(u, p);
        System.out.println(admin != null ? "Admin logged in: " + admin.getEmail() : "Invalid credentials.");
    }
}