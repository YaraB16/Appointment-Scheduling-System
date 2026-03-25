package com.appointment.Domain;


import com.appointment.Domain.TimeSlot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Appointment entity.
 * @author team
 * @version 1.0
 */
public class Appointment {
    private final String id;
    private final TimeSlot slot;
    private final AppointmentType type;

    private AppointmentStatus status;
    private final List<User> participants = new ArrayList<>();

    /**
     * @param id appointment id
     * @param slot time slot
     * @param type appointment type
     */
    public Appointment(String id, TimeSlot slot, AppointmentType type) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        this.id = id;
        this.slot = Objects.requireNonNull(slot);
        this.type = Objects.requireNonNull(type);
        this.status = AppointmentStatus.AVAILABLE;
    }

    public String getId() { return id; }
    public TimeSlot getSlot() { return slot; }
    public AppointmentType getType() { return type; }
    public AppointmentStatus getStatus() { return status; }

    public List<User> getParticipants() { return List.copyOf(participants); }

    public void confirmFor(User user) {
        Objects.requireNonNull(user);
        if (status != AppointmentStatus.AVAILABLE) throw new IllegalStateException("slot not available");
        participants.add(user);
        status = AppointmentStatus.CONFIRMED;
    }

    public void cancel() {
        if (status != AppointmentStatus.CONFIRMED) throw new IllegalStateException("only confirmed can be cancelled");
        status = AppointmentStatus.CANCELLED;
    }

    public void makeAvailableAgain() {
        participants.clear();
        status = AppointmentStatus.AVAILABLE;
    }
}