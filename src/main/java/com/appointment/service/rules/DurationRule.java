package com.appointment.service.rules;

import com.appointment.Domain.Appointment;

import java.time.Duration;
import java.util.Objects;


public class DurationRule implements BookingRuleStrategy {

    private final Duration maxDuration;

    /**
     * @param maxDuration maximum allowed duration
     */
    public DurationRule(Duration maxDuration) {
        this.maxDuration = Objects.requireNonNull(maxDuration, "maxDuration");
    }

    @Override
    public boolean isValid(Appointment appointment) {
        Duration actual = appointment.getSlot().duration();
        return actual.compareTo(maxDuration) <= 0;
    }

    @Override
    public String message() {
        return "Duration must be <= " + maxDuration.toMinutes() + " minutes.";
    }
}