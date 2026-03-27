package com.appointment.service.rules;

import com.appointment.Domain.Appointment;


public interface BookingRuleStrategy {

    /**
     * @param appointment appointment
     * @return true if valid
     */
    boolean isValid(Appointment appointment);

    /**
     * @return message if invalid
     */
    String message();
}