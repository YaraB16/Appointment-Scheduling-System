package com.appointment.Repository;

import com.appointment.Domain.Appointment;

import java.util.List;


public interface AppointmentRepository {

    /**
     * Save or update appointment.
     *
     * @param a appointment
     */
    void save(Appointment a);

    /**
     * Find all appointments.
     *
     * @return list of all appointments
     */
    List<Appointment> findAll();

    /**
     * Find appointment by id.
     *
     * @param id appointment id
     * @return appointment or null
     */
    Appointment findById(String id);
}