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
/**
 * Service class responsible for managing appointment booking operations.
 *
 * <p>This class implements the core business logic of the system,
 * including appointment creation, booking, cancellation, modification,
 * and notification handling.</p>
 *
 * <p>It coordinates between the repository layer, validation rules,
 * and notification services.</p>
 */
public class BookingService {
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final Clock clock;
    private final List<BookingRuleStrategy> rules = new ArrayList<>();
    /**
     * Constructs a BookingService with required dependencies.
     *
     * @param appointmentRepository repository used to store appointments
     * @param notificationService service used to send notifications
     * @param clock clock used for time-based operations
     * @throws NullPointerException if any dependency is null
     */
    public BookingService(AppointmentRepository appointmentRepository,
                          NotificationService notificationService,
                          Clock clock) {
        this.appointmentRepository = Objects.requireNonNull(appointmentRepository);
        this.notificationService = Objects.requireNonNull(notificationService);
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * Adds a booking rule to be applied before confirming an appointment.
     *
     * @param rule the booking rule strategy
     * @throws NullPointerException if rule is null
     */
    public void addRule(BookingRuleStrategy rule) {
        rules.add(Objects.requireNonNull(rule));
    }

    public Appointment createSlot(TimeSlot slot, AppointmentType type) {
        validateSlot(slot);

        long minutes = java.time.Duration.between(slot.getStart(), slot.getEnd()).toMinutes();
        if (minutes > 30) {
            throw new IllegalArgumentException("Appointment duration must not exceed 30 minutes.");
        }

        Appointment appointment = new Appointment(slot, type);
        appointmentRepository.save(appointment);
        return appointment;
    }
    /**
     * Returns all appointments in the system.
     *
     * @return list of appointments
     */
    public List<Appointment> listAll() {
        return appointmentRepository.findAll();
    }
    /**
     * Returns all available appointment slots.
     *
     * @return list of available appointments
     */
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
    /**
     * Books an available appointment for a user after validation.
     *
     * @param appointmentId the appointment ID
     * @param user the user booking the appointment
     * @return the confirmed appointment
     * @throws NullPointerException if inputs are null
     * @throws IllegalArgumentException if appointment not found
     * @throws IllegalStateException if slot unavailable or rule fails
     */
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
    /**
     * Cancels a confirmed appointment and makes it available again.
     *
     * @param appointmentId the appointment ID
     * @throws IllegalArgumentException if appointment not found
     * @throws IllegalStateException if appointment not confirmed
     */
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
    /**
     * Cancels an appointment only if it is in the future.
     *
     * @param appointmentId the appointment ID
     * @throws IllegalStateException if appointment is not in the future
     */
    public void cancelFutureOnly(String appointmentId) {
        Appointment appointment = requireAppointment(appointmentId);
        LocalDateTime now = LocalDateTime.now(clock);

        if (!appointment.getSlot().getStart().isAfter(now)) {
            throw new IllegalStateException("Only future appointments can be cancelled.");
        }

        cancelAndMakeAvailable(appointmentId);
    }

    /**
     * Modifies a future appointment.
     *
     * @param appointmentId the appointment ID
     * @param newSlot the new time slot
     * @return updated appointment
     * @throws IllegalStateException if appointment is not in the future
     */
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
    /**
     * Modifies an appointment as an administrator.
     *
     * @param appointmentId the appointment ID
     * @param newSlot the new time slot
     * @return updated appointment
     */
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
    /**
     * Sends reminders for appointments occurring within the next given hours.
     *
     * @param hours number of hours ahead
     * @return number of reminders sent
     * @throws IllegalArgumentException if hours <= 0
     */
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
    /**
     * Retrieves an appointment by ID.
     *
     * @param appointmentId appointment ID
     * @return appointment
     * @throws IllegalArgumentException if not found
     */
    private Appointment requireAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }
        return appointment;
    }
    /**
     * Validates a time slot.
     *
     * @param slot time slot
     * @throws IllegalArgumentException if invalid
     */
    private void validateSlot(TimeSlot slot) {
        Objects.requireNonNull(slot, "slot required");

        if (!slot.getEnd().isAfter(slot.getStart())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
    }
}