package com.appointment.service.rules;

import com.appointment.Domain.Appointment;

public class TypeRule implements BookingRuleStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getType() != null;
    }

    @Override
    public String message() {
        return "Appointment type is required.";
    }
}
