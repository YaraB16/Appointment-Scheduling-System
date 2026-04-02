import com.appointment.Domain.*;
import com.appointment.Repository.InMemoryAppointmentRepository;
import com.appointment.service.BookingService;
import com.appointment.service.Notification.NotificationService;
import com.appointment.service.rules.DurationRule;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Test
    void book_confirmsAppointment_andSendsNotification() {
        var repo = new InMemoryAppointmentRepository();
        NotificationService notifications = mock(NotificationService.class);
        var service = new BookingService(repo, notifications, Clock.systemUTC());
        service.addRule(new DurationRule(Duration.ofMinutes(30)));

        Appointment slot = service.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 3, 27, 10, 0),
                        LocalDateTime.of(2026, 3, 27, 10, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@mail.com", "1111", UserRole.USER);
        Appointment booked = service.book(slot.getId(), user);

        assertEquals(AppointmentStatus.CONFIRMED, booked.getStatus());
        assertEquals(1, booked.getParticipantCount());

        verify(notifications, times(1))
                .send(eq(user), contains("confirmed"));
    }

    @Test
    void book_fails_whenRuleInvalid() {
        var repo = new InMemoryAppointmentRepository();
        NotificationService notifications = mock(NotificationService.class);
        var service = new BookingService(repo, notifications, Clock.systemUTC());
        service.addRule(new DurationRule(Duration.ofMinutes(15)));

        Appointment slot = service.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 3, 27, 10, 0),
                        LocalDateTime.of(2026, 3, 27, 10, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@mail.com", "1111", UserRole.USER);

        assertThrows(IllegalStateException.class, () -> service.book(slot.getId(), user));
        verify(notifications, never()).send(any(User.class), anyString());
    }

    @Test
    void cancelAndMakeAvailable_makesSlotAvailableAgain_andNotifiesOriginalUser() {
        var repo = new InMemoryAppointmentRepository();
        NotificationService notifications = mock(NotificationService.class);
        var service = new BookingService(repo, notifications, Clock.systemUTC());

        Appointment slot = service.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 3, 27, 10, 0),
                        LocalDateTime.of(2026, 3, 27, 10, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@mail.com", "1111", UserRole.USER);
        service.book(slot.getId(), user);
        service.cancelAndMakeAvailable(slot.getId());

        Appointment after = repo.findById(slot.getId());
        assertNotNull(after);
        assertEquals(AppointmentStatus.AVAILABLE, after.getStatus());
        assertEquals(0, after.getParticipantCount());

        verify(notifications, atLeastOnce())
                .send(eq(user), contains("cancel"));
    }

    @Test
    void cancelFutureOnly_fails_whenStartNotInFuture() {
        var repo = new InMemoryAppointmentRepository();
        NotificationService notifications = mock(NotificationService.class);

        Clock fixed = Clock.fixed(
                Instant.parse("2026-03-27T10:00:00Z"),
                ZoneOffset.UTC
        );

        var service = new BookingService(repo, notifications, fixed);

        Appointment slot = service.createSlot(
                new TimeSlot(
                        LocalDateTime.of(2026, 3, 27, 10, 0),
                        LocalDateTime.of(2026, 3, 27, 10, 30)
                ),
                AppointmentType.URGENT
        );

        User user = new User("Yara", "yara@mail.com", "1111", UserRole.USER);
        service.book(slot.getId(), user);

        assertThrows(IllegalStateException.class, () -> service.cancelFutureOnly(slot.getId()));
    }
}