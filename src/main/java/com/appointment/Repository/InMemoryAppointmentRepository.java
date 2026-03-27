package com.appointment.Repository;

import com.appointment.Domain.Appointment;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class InMemoryAppointmentRepository implements AppointmentRepository {

    private final LinkedList<Appointment> appointments = new LinkedList<>();

    @Override
    public void save(Appointment a) {
        Objects.requireNonNull(a, "appointment");

        // update if exists, else add
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(a.getId())) {
                appointments.set(i, a);
                return;
            }
        }
        appointments.add(a);
    }

    @Override
    public List<Appointment> findAll() {
        return new LinkedList<>(appointments);
    }

    @Override
    public Appointment findById(String id) {
        for (Appointment a : appointments) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }
}