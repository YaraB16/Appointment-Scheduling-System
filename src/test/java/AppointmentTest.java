
import com.appointment.Domain.*;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    @Test
    void newAppointment_isAvailable() {
        TimeSlot slot = new TimeSlot(
                LocalDateTime.of(2026, 3, 27, 10, 0),
                LocalDateTime.of(2026, 3, 27, 10, 30)
        );
        Appointment a = new Appointment(slot, AppointmentType.URGENT);

        assertEquals(AppointmentStatus.AVAILABLE, a.getStatus());
        assertEquals(0, a.getParticipantCount());
    }

    @Test
    void confirmFor_movesToConfirmedAndAddsParticipant() {
        Appointment a = new Appointment(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)),
                AppointmentType.FOLLOW_UP
        );
        User u = new User("Yara", "yara@mail.com", "1111", UserRole.USER);

        a.confirmFor(u);

        assertEquals(AppointmentStatus.CONFIRMED, a.getStatus());
        assertEquals(1, a.getParticipantCount());
        assertEquals("yara@mail.com", a.getParticipants().get(0).getEmail());
    }

    @Test
    void confirmFor_failsIfNotAvailable() {
        Appointment a = new Appointment(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)),
                AppointmentType.FOLLOW_UP
        );
        User u = new User("Yara", "yara@mail.com", "1111", UserRole.USER);

        a.confirmFor(u);
        assertThrows(IllegalStateException.class, () -> a.confirmFor(u));
    }

    @Test
    void cancel_onlyConfirmedCanCancel() {
        Appointment a = new Appointment(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)),
                AppointmentType.URGENT
        );

        assertThrows(IllegalStateException.class, a::cancel);

        a.confirmFor(new User("Yara", "yara@mail.com", "1111", UserRole.USER));
        a.cancel();

        assertEquals(AppointmentStatus.CANCELED, a.getStatus());
    }

    @Test
    void makeAvailableAgain_clearsParticipantsAndSetsAvailable() {
        Appointment a = new Appointment(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)),
                AppointmentType.URGENT
        );
        a.confirmFor(new User("Yara", "yara@mail.com", "1111", UserRole.USER));

        a.makeAvailableAgain();

        assertEquals(AppointmentStatus.AVAILABLE, a.getStatus());
        assertEquals(0, a.getParticipantCount());
    }
}