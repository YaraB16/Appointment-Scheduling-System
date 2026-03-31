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
        validateSlot(slot);

        Appointment appointment = new Appointment(slot, type);
        appointmentRepository.save(appointment);
        return appointment;
    }

    public List<Appointment> listAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> viewAvailableSlots() {
        List<Appointment> all = appointmentRepository.findAll();
        List<Appointment> available = new ArrayList<>();

        for (Appointment appointment : all) {
            if (appointment.getStatus() == AppointmentStatus.AVAILABLE) {
                available.add(appointment);
            }
        }
        return available;
    }

    public Appointment book(String appointmentId, User user) {
        Objects.requireNonNull(appointmentId, "appointmentId required");
        Objects.requireNonNull(user, "user required");

        Appointment appointment = requireAppointment(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.AVAILABLE) {
            throw new IllegalStateException("Slot not available.");
        }

        for (BookingRuleStrategy rule : rules) {
            if (!rule.isValid(appointment)) {
                throw new IllegalStateException(rule.message());
            }
        }

        appointment.confirmFor(user);
        appointmentRepository.save(appointment);
        notificationService.send(user, "Your appointment is confirmed: " + appointment.getId());
        return appointment;
    }

    public void cancelAndMakeAvailable(String appointmentId) {
        Objects.requireNonNull(appointmentId, "appointmentId required");

        Appointment appointment = requireAppointment(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED appointments can be cancelled.");
        }

        User firstParticipant = appointment.getParticipants().isEmpty()
                ? null
                : appointment.getParticipants().get(0);

        appointment.cancel();
        appointment.makeAvailableAgain();
        appointmentRepository.save(appointment);

        if (firstParticipant != null) {
            notificationService.send(firstParticipant,
                    "Your appointment was cancelled: " + appointment.getId());
        }
    }

    public void cancelFutureOnly(String appointmentId) {
        Appointment appointment = requireAppointment(appointmentId);
        LocalDateTime now = LocalDateTime.now(clock);

        if (!appointment.getSlot().getStart().isAfter(now)) {
            throw new IllegalStateException("Only future appointments can be cancelled.");
        }

        cancelAndMakeAvailable(appointmentId);
    }

    public Appointment modifyFutureAppointment(String appointmentId, TimeSlot newSlot) {
        Objects.requireNonNull(newSlot, "newSlot required");
        validateSlot(newSlot);

        Appointment appointment = requireAppointment(appointmentId);
        LocalDateTime now = LocalDateTime.now(clock);

        if (!appointment.getSlot().getStart().isAfter(now)) {
            throw new IllegalStateException("Only future appointments can be modified.");
        }

        appointment.reschedule(newSlot);
        appointmentRepository.save(appointment);
        return appointment;
    }

    public Appointment modifyAsAdmin(String appointmentId, TimeSlot newSlot) {
        Objects.requireNonNull(newSlot, "newSlot required");
        validateSlot(newSlot);

        Appointment appointment = requireAppointment(appointmentId);
        appointment.reschedule(newSlot);
        appointmentRepository.save(appointment);

        if (!appointment.getParticipants().isEmpty()) {
            User firstParticipant = appointment.getParticipants().get(0);
            notificationService.send(firstParticipant,
                    "Your appointment was updated: " + appointment.getId());
        }

        return appointment;
    }

    public int sendRemindersForUpcomingHours(int hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("Hours must be greater than zero.");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime limit = now.plusHours(hours);

        int count = 0;

        for (Appointment appointment : appointmentRepository.findAll()) {
            if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
                continue;
            }

            LocalDateTime start = appointment.getSlot().getStart();
            boolean withinWindow = !start.isBefore(now) && !start.isAfter(limit);

            if (!withinWindow) {
                continue;
            }

            for (User user : appointment.getParticipants()) {
                notificationService.send(user,
                        "Reminder: upcoming appointment at " + start + " (" + appointment.getType() + ")");
                count++;
            }
        }

        return count;
    }

    private Appointment requireAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }
        return appointment;
    }

    private void validateSlot(TimeSlot slot) {
        Objects.requireNonNull(slot, "slot required");

        if (!slot.getEnd().isAfter(slot.getStart())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
    }
}