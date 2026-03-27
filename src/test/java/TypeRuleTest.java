

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentType;
import com.appointment.service.rules.TypeRule;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TypeRuleTest {

    @Test
    void alwaysValid_whenTypeNotNull() {
        TypeRule rule = new TypeRule();
        Appointment a = new Appointment(
                new TimeSlot(LocalDateTime.of(2026, 3, 27, 10, 0), LocalDateTime.of(2026, 3, 27, 10, 30)),
                AppointmentType.URGENT
        );

        assertTrue(rule.isValid(a));
    }
}