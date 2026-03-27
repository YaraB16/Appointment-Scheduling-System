package com.appointment.service;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentStatus;
import com.appointment.Domain.AppointmentType;
import com.appointment.Domain.User;
import com.appointment.Repository.AppointmentRepository;
import com.appointment.service.Notification.NotificationService;
import com.appointment.service.rules.BookingRuleStrategy;
import com.appointment.value.TimeSlot;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookingService {
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final Clock clock;
    private final List<BookingRuleStrategy> rules = new ArrayList<>();

    public BookingService(AppointmentRepository appointmentRepository,
                          NotificationService notificationService,
                          Clock clock) {
        this.appointmentRepository = Objects.requireNonNull(appointmentRepository);
        this.notificationService = Objects.requireNonNull(notificationService);
        this.clock = Objects.requireNonNull(clock);
    }

    public void addRule(BookingRuleStrategy rule) {
        rules.add(Objects.requireNonNull(rule));
    }

    public Appointment createSlot(TimeSlot slot, AppointmentType type) {
        Appointment a = new Appointment(slot, type);
        appointmentRepository.save(a);
        return a;
    }

    public List<Appointment> listAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> viewAvailableSlots() {
        List<Appointment> all = appointmentRepository.findAll();
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : all) {
            if (a.getStatus() == AppointmentStatus.AVAILABLE) out.add(a);
        }
        return out;
    }

    public Appointment book(String appointmentId, User user) {
        Objects.requireNonNull(appointmentId, "appointmentId required");
        Objects.requireNonNull(user, "user required");

        Appointment a = appointmentRepository.findById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        if (a.getStatus() != AppointmentStatus.AVAILABLE) throw new IllegalStateException("Slot not available");

        for (BookingRuleStrategy r : rules) {
            if (!r.isValid(a)) throw new IllegalStateException(r.message());
        }

        a.confirmFor(user);
        appointmentRepository.save(a);
        notificationService.send(user, "Your appointment is confirmed: " + a.getId());
        return a;
    }

    public void cancelAndMakeAvailable(String appointmentId) {
        Objects.requireNonNull(appointmentId, "appointmentId required");
        Appointment a = appointmentRepository.findById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        if (a.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED appointments can be cancelled");
        }

        User first = a.getParticipants().isEmpty() ? null : a.getParticipants().get(0);

        a.cancel();
        a.makeAvailableAgain();
        appointmentRepository.save(a);

        if (first != null) {
            notificationService.send(first, "Your appointment was cancelled: " + a.getId());
        }
    }

    public void cancelFutureOnly(String appointmentId) {
        Objects.requireNonNull(appointmentId, "appointmentId required");
        Appointment a = appointmentRepository.findById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentId);

        LocalDateTime now = LocalDateTime.now(clock);
        if (!a.getSlot().getStart().isAfter(now)) {
            throw new IllegalStateException("Only future appointments can be cancelled");
        }
        cancelAndMakeAvailable(appointmentId);
    }
}