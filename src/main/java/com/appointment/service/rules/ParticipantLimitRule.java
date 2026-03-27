package com.appointment.service.rules;

import com.appointment.Domain.Appointment;


public class ParticipantLimitRule implements BookingRuleStrategy {

    private final int max;

    /**
     * @param max max participants (>0)
     */
    public ParticipantLimitRule(int max) {
        if (max <= 0) throw new IllegalArgumentException("max must be > 0");
        this.max = max;
    }

    @Override
    public boolean isValid(Appointment appointment) {
        // after confirmFor(user) count becomes 1
        return appointment.getParticipantCount() <= max;
    }

    @Override
    public String message() {
        return "Participants must be <= " + max;
    }
}