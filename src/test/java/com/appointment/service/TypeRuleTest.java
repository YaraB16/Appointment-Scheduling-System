package com.appointment.service;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentType;
import com.appointment.service.rules.TypeRule;
import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TypeRuleTest {

    @Test
    void isValid_returnsTrue_whenTypeIsNotNull() {
        TypeRule rule = new TypeRule();

        Appointment appointment = new Appointment(
                new TimeSlot(
                        LocalDateTime.of(2026, 3, 27, 10, 0),
                        LocalDateTime.of(2026, 3, 27, 10, 30)
                ),
                AppointmentType.URGENT
        );

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void message_returnsExpectedText() {
        TypeRule rule = new TypeRule();

        assertNotNull(rule.message());
        assertFalse(rule.message().isBlank());
    }
}