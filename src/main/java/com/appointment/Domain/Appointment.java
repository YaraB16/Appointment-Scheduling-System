package com.appointment.Domain;

import com.appointment.value.TimeSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Appointment {

    private final String id;
    private final TimeSlot slot;
    private final AppointmentType type;

    private AppointmentStatus status;
    private final List<User> participants = new ArrayList<>();

    /**
     * Creates an available appointment slot.
     *
     * @param slot time slot
     * @param type appointment type
     */
    public Appointment(TimeSlot slot, AppointmentType type) {
        this.id = UUID.randomUUID().toString();
        this.slot = Objects.requireNonNull(slot, "slot");
        this.type = Objects.requireNonNull(type, "type");
        this.status = AppointmentStatus.AVAILABLE;
    }

    /**
     * @return appointment id
     */
    public String getId() {
        return id;
    }

    /**
     * @return time slot
     */
    public TimeSlot getSlot() {
        return slot;
    }

    /**
     * @return type
     */
    public AppointmentType getType() {
        return type;
    }

    /**
     * @return status
     */
    public AppointmentStatus getStatus() {
        return status;
    }

    /**
     * @return participants (immutable copy)
     */
    public List<User> getParticipants() {
        return List.copyOf(participants);
    }

    /**
     * Convenience for rule checks.
     *
     * @return number of participants
     */
    public int getParticipantCount() {
        return participants.size();
    }

    /**
     * Confirms the slot for a user.
     *
     * @param user user to add as participant
     */
    public void confirmFor(User user) {
        Objects.requireNonNull(user, "user");
        if (status != AppointmentStatus.AVAILABLE) {
            throw new IllegalStateException("Slot not available");
        }
        participants.add(user);
        status = AppointmentStatus.CONFIRMED;
    }

    /**
     * Cancels a confirmed appointment.
     */
    public void cancel() {
        if (status != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED appointments can be cancelled");
        }
        status = AppointmentStatus.CANCELED; // لازم enum يكون CANCELED
    }

    /**
     * Makes slot available again (clears participants).
     */
    public void makeAvailableAgain() {
        participants.clear();
        status = AppointmentStatus.AVAILABLE;
    }
}