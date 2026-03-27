
import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentType;
import com.appointment.service.rules.DurationRule;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DurationRuleTest {

    @Test
    void valid_whenDurationLessOrEqualMax() {
        DurationRule rule = new DurationRule(Duration.ofMinutes(30));

        Appointment a = new Appointment(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)),
                AppointmentType.URGENT
        );

        assertTrue(rule.isValid(a));
    }

    @Test
    void invalid_whenDurationExceedsMax() {
        DurationRule rule = new DurationRule(Duration.ofMinutes(30));

        Appointment a = new Appointment(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 31)),
                AppointmentType.URGENT
        );

        assertFalse(rule.isValid(a));
        assertTrue(rule.message().contains("30"));
    }
}