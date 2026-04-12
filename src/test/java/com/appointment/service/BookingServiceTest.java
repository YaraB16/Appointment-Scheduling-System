package com.appointment.service;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentStatus;
import com.appointment.Domain.AppointmentType;
import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import com.appointment.Repository.InMemoryAppointmentRepository;
import com.appointment.service.Notification.FakeNotificationService;
import com.appointment.service.rules.BookingRuleStrategy;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private InMemoryAppointmentRepository repo;
    private FakeNotificationService fakeNotification;
    private Clock fixedClock;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        repo = new InMemoryAppointmentRepository();
        fakeNotification = new FakeNotificationService();
        fixedClock = Clock.fixed(
                Instant.parse("2026-04-12T10:00:00Z"),
                ZoneId.of("UTC")
        );
        bookingService = new BookingService(repo, fakeNotification, fixedClock);
    }

    @Test
    void createSlot_createsAppointmentSuccessfully() {
        TimeSlot slot = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 12, 0),
                LocalDateTime.of(2026, 4, 12, 12, 30)
        );

        Appointment appointment = bookingService.createSlot(slot, AppointmentType.URGENT);

        assertNotNull(appointment);
        assertNotNull(appointment.getId());
        assertEquals(AppointmentType.URGENT, appointment.getType());
        assertEquals(AppointmentStatus.AVAILABLE, appointment.getStatus());
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void createSlot_throwsException_whenEndBeforeStart() {
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.createSlot(
                        new TimeSlot(
                                LocalDateTime.of(2026, 4, 12, 12, 30),
                                LocalDateTime.of(2026, 4, 12, 12, 0)
                        ),
                        AppointmentType.URGENT
                )
        );
    }

    @Test
    void listAll_returnsAllAppointments() {
        Appointment a1 = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        Appointment a2 = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 13, 0),
                        LocalDateTime.of(2026, 4, 12, 13, 30)
                ),
                AppointmentType.FOLLOW_UP
        );

        List<Appointment> all = bookingService.listAll();

        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(a -> a.getId().equals(a1.getId())));
        assertTrue(all.stream().anyMatch(a -> a.getId().equals(a2.getId())));
    }

    @Test
    void viewAvailableSlots_returnsOnlyAvailableAppointments() {
        Appointment available = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        Appointment confirmed = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 13, 0),
                        LocalDateTime.of(2026, 4, 12, 13, 30)
                ),
                AppointmentType.FOLLOW_UP
        );

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);
        bookingService.book(confirmed.getId(), user);

        List<Appointment> availableSlots = bookingService.viewAvailableSlots();

        assertEquals(1, availableSlots.size());
        assertEquals(available.getId(), availableSlots.get(0).getId());
    }

    @Test
    void book_confirmsAppointmentAndSendsNotification() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);

        Appointment result = bookingService.book(appointment.getId(), user);

        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        assertEquals(1, result.getParticipantCount());
        assertEquals(1, fakeNotification.sent().size());
        assertEquals(user, fakeNotification.sent().get(0).user());
        assertTrue(fakeNotification.sent().get(0).message().contains("confirmed"));
    }

    @Test
    void book_throwsException_whenAppointmentIdIsNull() {
        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);

        assertThrows(NullPointerException.class,
                () -> bookingService.book(null, user));
    }

    @Test
    void book_throwsException_whenUserIsNull() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        assertThrows(NullPointerException.class,
                () -> bookingService.book(appointment.getId(), null));
    }

    @Test
    void book_throwsException_whenAppointmentNotFound() {
        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.book("missing-id", user));
    }

    @Test
    void book_throwsException_whenSlotNotAvailable() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        User firstUser = new User("Yara", "yara@test.com", "123", UserRole.USER);
        User secondUser = new User("Reem", "reem@test.com", "123", UserRole.USER);

        bookingService.book(appointment.getId(), firstUser);

        assertThrows(IllegalStateException.class,
                () -> bookingService.book(appointment.getId(), secondUser));
    }

    @Test
    void book_throwsException_whenRuleFails() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        bookingService.addRule(new BookingRuleStrategy() {
            @Override
            public boolean isValid(Appointment appointment) {
                return false;
            }

            @Override
            public String message() {
                return "Rule failed";
            }
        });

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.book(appointment.getId(), user));

        assertEquals("Rule failed", ex.getMessage());
    }

    @Test
    void cancelAndMakeAvailable_resetsToAvailable() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);
        bookingService.book(appointment.getId(), user);

        int notificationsBeforeCancel = fakeNotification.sent().size();

        bookingService.cancelAndMakeAvailable(appointment.getId());

        Appointment updated = repo.findById(appointment.getId());
        assertEquals(AppointmentStatus.AVAILABLE, updated.getStatus());
        assertEquals(0, updated.getParticipantCount());
        assertEquals(notificationsBeforeCancel + 1, fakeNotification.sent().size());
        assertTrue(fakeNotification.sent().get(fakeNotification.sent().size() - 1).message().contains("cancelled"));
    }

    @Test
    void cancelAndMakeAvailable_throwsException_whenAppointmentIsNotConfirmed() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        assertThrows(IllegalStateException.class,
                () -> bookingService.cancelAndMakeAvailable(appointment.getId()));
    }

    @Test
    void cancelFutureOnly_cancelsWhenAppointmentIsFuture() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);
        bookingService.book(appointment.getId(), user);

        bookingService.cancelFutureOnly(appointment.getId());

        Appointment updated = repo.findById(appointment.getId());
        assertEquals(AppointmentStatus.AVAILABLE, updated.getStatus());
    }

    @Test
    void cancelFutureOnly_throwsException_whenAppointmentIsNotFuture() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 8, 0),
                        LocalDateTime.of(2026, 4, 12, 8, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);
        bookingService.book(appointment.getId(), user);

        assertThrows(IllegalStateException.class,
                () -> bookingService.cancelFutureOnly(appointment.getId()));
    }

    @Test
    void modifyFutureAppointment_updatesSlotWhenFuture() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        TimeSlot newSlot = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 14, 0),
                LocalDateTime.of(2026, 4, 12, 14, 30)
        );

        Appointment updated = bookingService.modifyFutureAppointment(appointment.getId(), newSlot);

        assertEquals(newSlot.getStart(), updated.getSlot().getStart());
        assertEquals(newSlot.getEnd(), updated.getSlot().getEnd());
    }

    @Test
    void modifyFutureAppointment_throwsException_whenAppointmentIsNotFuture() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 8, 0),
                        LocalDateTime.of(2026, 4, 12, 8, 30)
                ),
                AppointmentType.URGENT
        );

        TimeSlot newSlot = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 14, 0),
                LocalDateTime.of(2026, 4, 12, 14, 30)
        );

        assertThrows(IllegalStateException.class,
                () -> bookingService.modifyFutureAppointment(appointment.getId(), newSlot));
    }

    @Test
    void modifyAsAdmin_updatesSlotAndSendsNotificationWhenParticipantsExist() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);
        bookingService.book(appointment.getId(), user);

        int notificationsBeforeModify = fakeNotification.sent().size();

        TimeSlot newSlot = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 15, 0),
                LocalDateTime.of(2026, 4, 12, 15, 30)
        );

        Appointment updated = bookingService.modifyAsAdmin(appointment.getId(), newSlot);

        assertEquals(newSlot.getStart(), updated.getSlot().getStart());
        assertEquals(newSlot.getEnd(), updated.getSlot().getEnd());
        assertEquals(notificationsBeforeModify + 1, fakeNotification.sent().size());
        assertTrue(fakeNotification.sent().get(fakeNotification.sent().size() - 1).message().contains("updated"));
    }

    @Test
    void modifyAsAdmin_updatesSlotWithoutNotificationWhenNoParticipants() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 12, 0),
                        LocalDateTime.of(2026, 4, 12, 12, 30)
                ),
                AppointmentType.URGENT
        );

        int notificationsBeforeModify = fakeNotification.sent().size();

        TimeSlot newSlot = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 15, 0),
                LocalDateTime.of(2026, 4, 12, 15, 30)
        );

        Appointment updated = bookingService.modifyAsAdmin(appointment.getId(), newSlot);

        assertEquals(newSlot.getStart(), updated.getSlot().getStart());
        assertEquals(newSlot.getEnd(), updated.getSlot().getEnd());
        assertEquals(notificationsBeforeModify, fakeNotification.sent().size());
    }

    @Test
    void sendRemindersForUpcomingHours_throwsException_whenHoursLessThanOrEqualZero() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.sendRemindersForUpcomingHours(0));
    }

    @Test
    void sendRemindersForUpcomingHours_sendsReminderForConfirmedUpcomingAppointment() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 11, 0),
                        LocalDateTime.of(2026, 4, 12, 11, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);
        bookingService.book(appointment.getId(), user);

        int count = bookingService.sendRemindersForUpcomingHours(2);

        assertEquals(1, count);
        assertTrue(fakeNotification.sent().get(fakeNotification.sent().size() - 1).message().contains("Reminder"));
    }

    @Test
    void sendRemindersForUpcomingHours_ignoresAppointmentsOutsideWindow() {
        Appointment appointment = bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 16, 0),
                        LocalDateTime.of(2026, 4, 12, 16, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@test.com", "123", UserRole.USER);
        bookingService.book(appointment.getId(), user);

        int count = bookingService.sendRemindersForUpcomingHours(2);

        assertEquals(0, count);
    }

    @Test
    void sendRemindersForUpcomingHours_ignoresAvailableAppointments() {
        bookingService.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 12, 11, 0),
                        LocalDateTime.of(2026, 4, 12, 11, 30)
                ),
                AppointmentType.URGENT
        );

        int count = bookingService.sendRemindersForUpcomingHours(2);

        assertEquals(0, count);
    }
}