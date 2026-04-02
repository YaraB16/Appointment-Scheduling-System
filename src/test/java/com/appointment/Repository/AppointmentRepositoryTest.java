package com.appointment.Repository;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentStatus;
import com.appointment.Domain.AppointmentType;
import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentRepositoryTest {

    @Test
    void save_addsNewAppointment_andFindAllReturnsIt() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment appointment = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 22, 10, 0),
                        LocalDateTime.of(2026, 4, 22, 10, 30)
                ),
                AppointmentType.URGENT
        );

        repo.save(appointment);

        List<Appointment> all = repo.findAll();

        assertEquals(1, all.size());
        assertEquals(appointment.getId(), all.get(0).getId());
    }

    @Test
    void save_updatesExistingAppointment_whenSameIdIsSavedAgain() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment appointment = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 22, 10, 0),
                        LocalDateTime.of(2026, 4, 22, 10, 30)
                ),
                AppointmentType.URGENT
        );

        repo.save(appointment);

        User user = new User("Reem", "reem@gmail.com", "1234", UserRole.USER);
        appointment.confirmFor(user);

        repo.save(appointment);

        List<Appointment> all = repo.findAll();
        assertEquals(1, all.size());
        assertEquals(AppointmentStatus.CONFIRMED, all.get(0).getStatus());
        assertEquals(1, all.get(0).getParticipantCount());
    }

    @Test
    void findById_returnsAppointment_whenFullIdMatches() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment appointment = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 22, 11, 0),
                        LocalDateTime.of(2026, 4, 22, 11, 30)
                ),
                AppointmentType.FOLLOW_UP
        );

        repo.save(appointment);

        Appointment found = repo.findById(appointment.getId());

        assertNotNull(found);
        assertEquals(appointment.getId(), found.getId());
    }

    @Test
    void findById_returnsAppointment_whenPrefixMatchesUniquely() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment appointment = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 22, 12, 0),
                        LocalDateTime.of(2026, 4, 22, 12, 30)
                ),
                AppointmentType.ASSESSMENT
        );

        repo.save(appointment);

        String shortId = appointment.getId().substring(0, 8);
        Appointment found = repo.findById(shortId);

        assertNotNull(found);
        assertEquals(appointment.getId(), found.getId());
    }

    @Test
    void findById_returnsNull_whenNoAppointmentMatches() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment found = repo.findById("not-found-id");

        assertNull(found);
    }

    @Test
    void findById_throwsException_whenPrefixIsAmbiguous() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        Appointment a1 = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 22, 13, 0),
                        LocalDateTime.of(2026, 4, 22, 13, 30)
                ),
                AppointmentType.VIRTUAL
        );

        Appointment a2 = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 4, 22, 14, 0),
                        LocalDateTime.of(2026, 4, 22, 14, 30)
                ),
                AppointmentType.IN_PERSON
        );

        repo.save(a1);
        repo.save(a2);

        String ambiguousPrefix = "";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> repo.findById(ambiguousPrefix)
        );

        assertTrue(ex.getMessage().contains("Ambiguous appointment id prefix"));
    }

    @Test
    void save_throwsException_whenAppointmentIsNull() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> repo.save(null)
        );

        assertEquals("appointment", ex.getMessage());
    }

    @Test
    void findById_throwsException_whenIdIsNull() {
        InMemoryAppointmentRepository repo = new InMemoryAppointmentRepository();

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> repo.findById(null)
        );

        assertEquals("id", ex.getMessage());
    }
}