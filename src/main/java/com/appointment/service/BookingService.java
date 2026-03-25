package com.appointment.service;

import com.appointment.Domain.Appointment;
import com.appointment.Domain.AppointmentStatus;
import com.appointment.Domain.User;
import com.appointment.Domain.value.TimeSlot;
import com.appointment.Repository.AppointmentRepository;
import com.appointment.service.Notification.NotificationService;
import com.appointment.service.rules.BookingRuleStrategy;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * BookingService (Application/Service layer).
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>View available appointment slots (US1.3)</li>
 *   <li>Book appointments and enforce business rules (US2.1, US2.2, US2.3)</li>
 *   <li>Cancel appointments (Sprint 4 baseline)</li>
 *   <li>Send notifications on booking/cancellation (US3.1 baseline integration)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Notes (Phase 1):
 * - In-memory persistence via AppointmentRepository (e.g., LinkedList).
 * - Notifications are abstracted via NotificationService (mockable in tests).
 * - Rules are injected using Strategy pattern (BookingRuleStrategy list).
 * </p>
 *
 * @author team
 * @version 1.0
 */
public class BookingService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    private final List<BookingRuleStrategy> rules = new ArrayList<>();

    /**
     * @param appointmentRepository repository (in-memory or DB later)
     * @param notificationService notification sender (mockable)
     * @param clock clock for time-based validation/testing
     */
    public BookingService(AppointmentRepository appointmentRepository,
                          NotificationService notificationService,
                          Clock clock) {
        this.appointmentRepository = Objects.requireNonNull(appointmentRepository);
        this.notificationService = Objects.requireNonNull(notificationService);
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * Adds a booking rule strategy (Strategy pattern).
     * @param rule booking rule
     */
    public void addRule(BookingRuleStrategy rule) {
        rules.add(Objects.requireNonNull(rule));
    }

    /**
     * US1.3 - View available appointment slots.
     * @return list of appointments with status AVAILABLE
     */
    public List<Appointment> viewAvailableSlots() {
        List<Appointment> all = appointmentRepository.findAll();
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : all) {
            if (a.getStatus() == AppointmentStatus.AVAILABLE) out.add(a);
        }
        return out;
    }

    /**
     * US2.1 - Book appointment.
     * <p>
     * Steps:
     * - Load appointment
     * - Ensure AVAILABLE
     * - Apply rules
     * - Confirm for user
     * - Persist
     * - Notify
     * </p>
     *
     * @param appointmentId id of appointment slot
     * @param user user booking the appointment
     * @return updated appointment (CONFIRMED)
     */
    public Appointment book(String appointmentId, User user) {
        Objects.requireNonNull(appointmentId, "appointmentId required");
        Objects.requireNonNull(user, "user required");

        Appointment a = appointmentRepository.findById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        if (a.getStatus() != AppointmentStatus.AVAILABLE) throw new IllegalStateException("Slot not available");

        // Apply booking rules (duration, capacity, type-specific, etc.)
        for (BookingRuleStrategy r : rules) {
            if (!r.isValid(a)) throw new IllegalStateException(r.message());
        }

        a.confirmFor(user);
        appointmentRepository.save(a);

        notificationService.send(user, "Your appointment is confirmed: " + a.getId());
        return a;
    }

    /**
     * Sprint 4 baseline: Cancel an appointment and make slot available again.
     * (If you need "only future appointments can be cancelled", call cancelFutureOnly()).
     *
     * @param appointmentId appointment id
     */
    public void cancelAndMakeAvailable(String appointmentId) {
        Objects.requireNonNull(appointmentId, "appointmentId required");

        Appointment a = appointmentRepository.findById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentId);

        if (a.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED appointments can be cancelled");
        }

        // Move to cancelled then make available again (matches acceptance: slot becomes available)
        a.cancel();
        a.makeAvailableAgain();
        appointmentRepository.save(a);

        // Phase 1: notify the first participant if exists
        if (!a.getParticipants().isEmpty()) {
            User u = a.getParticipants().get(0);
            notificationService.send(u, "Your appointment was cancelled: " + a.getId());
        }
    }

    /**
     * Sprint 4: Cancel only if appointment start is in the future.
     *
     * @param appointmentId appointment id
     */
    public void cancelFutureOnly(String appointmentId) {
        Objects.requireNonNull(appointmentId, "appointmentId required");

        Appointment a = appointmentRepository.findById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentId);

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime start = a.getSlot().getStart();

        if (!start.isAfter(now)) {
            throw new IllegalStateException("Only future appointments can be cancelled");
        }

        cancelAndMakeAvailable(appointmentId);
    }

    /**
     * Sprint 4: Modify an appointment time slot (future-only).
     * <p>
     * This method returns a NEW Appointment object only if you design Appointment as immutable.
     * In our earlier simple domain Appointment has a final TimeSlot, so modification requires
     * a new appointment slot design.
     * </p>
     *
     * <p><b>Phase 1 note:</b> If you want modify, change Appointment to allow updating slot.</p>
     *
     * @param appointmentId appointment id
     * @param newSlot new slot
     */
    public void modifyFutureOnly(String appointmentId, TimeSlot newSlot) {
        Objects.requireNonNull(appointmentId, "appointmentId required");
        Objects.requireNonNull(newSlot, "newSlot required");

        Appointment a = appointmentRepository.findById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + appointmentId);

        LocalDateTime now = LocalDateTime.now(clock);
        if (!a.getSlot().getStart().isAfter(now)) {
            throw new IllegalStateException("Only future appointments can be modified");
        }

        // By default Appointment has final slot; so we explicitly fail with guidance.
        throw new UnsupportedOperationException(
                "Modify not supported with current Appointment design (slot is final). " +
                        "Change Appointment to allow updating TimeSlot or implement 'reschedule' by creating a new slot."
        );
    }
}