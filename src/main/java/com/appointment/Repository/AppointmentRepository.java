package com.appointment.Repository;

import com.appointment.Domain.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

        private List<Appointment> appointments = new ArrayList<>();
        public void save(Appointment a) { }
        public List<Appointment> findAll() {
            return List.of();
        }

}
