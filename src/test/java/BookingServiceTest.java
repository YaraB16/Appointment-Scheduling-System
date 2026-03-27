
import com.appointment.Domain.*;
import com.appointment.Repository.InMemoryAppointmentRepository;
import com.appointment.service.BookingService;
import com.appointment.service.rules.DurationRule;
import com.appointment.service.Notification.FakeNotificationService;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    @Test
    void book_confirmsAppointment_andSendsNotification() {
        var repo = new InMemoryAppointmentRepository();
        var notifications = new FakeNotificationService();
        var service = new BookingService(repo, notifications, Clock.systemUTC());
        service.addRule(new DurationRule(Duration.ofMinutes(30)));

        Appointment slot = service.createSlot(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@mail.com", "1111", UserRole.USER);
        Appointment booked = service.book(slot.getId(), user);

        assertEquals(AppointmentStatus.CONFIRMED, booked.getStatus());
        assertEquals(1, booked.getParticipantCount());
        assertEquals(1, notifications.sent().size());
        assertTrue(notifications.sent().get(0).message().contains("confirmed"));
    }

    @Test
    void book_fails_whenRuleInvalid() {
        var repo = new InMemoryAppointmentRepository();
        var notifications = new FakeNotificationService();
        var service = new BookingService(repo, notifications, Clock.systemUTC());
        service.addRule(new DurationRule(Duration.ofMinutes(15))); // max 15

        Appointment slot = service.createSlot(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)), // 30
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@mail.com", "1111", UserRole.USER);

        assertThrows(IllegalStateException.class, () -> service.book(slot.getId(), user));
        assertEquals(0, notifications.sent().size());
    }

    @Test
    void cancelAndMakeAvailable_makesSlotAvailableAgain_andNotifiesOriginalUser() {
        var repo = new InMemoryAppointmentRepository();
        var notifications = new FakeNotificationService();
        var service = new BookingService(repo, notifications, Clock.systemUTC());

        Appointment slot = service.createSlot(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@mail.com", "1111", UserRole.USER);
        service.book(slot.getId(), user);

        service.cancelAndMakeAvailable(slot.getId());

        Appointment after = repo.findById(slot.getId());
        assertNotNull(after);
        assertEquals(AppointmentStatus.AVAILABLE, after.getStatus());
        assertEquals(0, after.getParticipantCount());

        assertTrue(notifications.sent().stream().anyMatch(s -> s.message().toLowerCase().contains("cancel")));
    }

    @Test
    void cancelFutureOnly_fails_whenStartNotInFuture() {
        var repo = new InMemoryAppointmentRepository();
        var notifications = new FakeNotificationService();

        // fixed time: 2026-03-27T10:00Z
        Clock fixed = Clock.fixed(Instant.parse("2026-03-27T10:00:00Z"), ZoneOffset.UTC);
        var service = new BookingService(repo, notifications, fixed);

        Appointment slot = service.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 3, 27, 10, 0), // equal to now (not future)
                        LocalDateTime.of(2026, 3, 27, 10, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@mail.com", "1111", UserRole.USER);
        service.book(slot.getId(), user);

        assertThrows(IllegalStateException.class, () -> service.cancelFutureOnly(slot.getId()));
    }
}