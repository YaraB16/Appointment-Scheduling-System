package com.appointment.Repository;

import com.appointment.Domain.Appointment;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class InMemoryAppointmentRepository implements AppointmentRepository {

    private final LinkedList<Appointment> appointments = new LinkedList<>();

    @Override
    public void save(Appointment appointment) {
        Objects.requireNonNull(appointment, "appointment");

        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(appointment.getId())) {
                appointments.set(i, appointment);
                return;
            }
        }

        appointments.add(appointment);
    }

    @Override
    public List<Appointment> findAll() {
        return new LinkedList<>(appointments);
    }

    @Override
    public Appointment findById(String id) {
        Objects.requireNonNull(id, "id");

        String trimmed = id.trim();

        for (Appointment appointment : appointments) {
            if (appointment.getId().equals(trimmed)) {
                return appointment;
            }
        }

        Appointment prefixMatch = null;
        for (Appointment appointment : appointments) {
            if (appointment.getId().startsWith(trimmed)) {
                if (prefixMatch != null) {
                    throw new IllegalArgumentException("Ambiguous appointment id prefix: " + trimmed);
                }
                prefixMatch = appointment;
            }
        }

        return prefixMatch;
    }
}