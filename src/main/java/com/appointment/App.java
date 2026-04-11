package com.appointment;

import com.appointment.Repository.AdminRepository;
import com.appointment.Repository.AppointmentRepository;
import com.appointment.Repository.Cli;
import com.appointment.Repository.InMemoryAdminRepository;
import com.appointment.Repository.InMemoryAppointmentRepository;
import com.appointment.service.AuthService;
import com.appointment.service.BookingService;
import com.appointment.service.Notification.EmailNotificationService;
import com.appointment.service.Notification.NotificationService;
import com.appointment.service.Notification.SmtpConfig;
import com.appointment.service.rules.DurationRule;
import com.appointment.service.rules.ParticipantLimitRule;
import com.appointment.service.rules.TypeRule;

import java.time.Clock;
import java.time.Duration;

public class App {
    public static void main(String[] args) {
        AppointmentRepository appointmentRepository = new InMemoryAppointmentRepository();
        AdminRepository adminRepository = new InMemoryAdminRepository();

        AuthService authService = new AuthService(adminRepository);

        String emailUsername = System.getenv("EMAIL_USERNAME");
        String emailPassword = System.getenv("EMAIL_PASSWORD");

        if (emailUsername == null || emailPassword == null
                || emailUsername.isBlank() || emailPassword.isBlank()) {
            throw new IllegalStateException(
                    "EMAIL_USERNAME and EMAIL_PASSWORD must be set in environment variables."
            );
        }

        SmtpConfig smtpConfig = new SmtpConfig(
                "smtp.gmail.com",
                587,
                emailUsername,
                emailPassword
        );

        NotificationService notificationService = new EmailNotificationService(smtpConfig);

        BookingService bookingService = new BookingService(
                appointmentRepository,
                notificationService,
                Clock.systemDefaultZone()
        );

        // Rules
        bookingService.addRule(new DurationRule(Duration.ofMinutes(30)));
        bookingService.addRule(new ParticipantLimitRule(1));
        bookingService.addRule(new TypeRule());

        Cli cli = new Cli(authService, bookingService);
        cli.run();
    }
}