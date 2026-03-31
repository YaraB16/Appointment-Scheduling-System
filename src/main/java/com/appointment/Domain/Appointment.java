package com.appointment.Domain;

import com.appointment.value.TimeSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Appointment {

    private final String id;
    private TimeSlot slot;
    private final AppointmentType type;
    private AppointmentStatus status;
    private final List<User> participants = new ArrayList<>();

    public Appointment(TimeSlot slot, AppointmentType type) {
        this.id = UUID.randomUUID().toString();
        this.slot = Objects.requireNonNull(slot, "slot");
        this.type = Objects.requireNonNull(type, "type");
        this.status = AppointmentStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public AppointmentType getType() {
        return type;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public List<User> getParticipants() {
        return List.copyOf(participants);
    }

    public int getParticipantCount() {
        return participants.size();
    }

    public void confirmFor(User user) {
        Objects.requireNonNull(user, "user");

        if (status != AppointmentStatus.AVAILABLE) {
            throw new IllegalStateException("Slot not available.");
        }

        participants.add(user);
        status = AppointmentStatus.CONFIRMED;
    }

    public void cancel() {
        if (status != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED appointments can be cancelled.");
        }

        status = AppointmentStatus.CANCELED;
    }

    public void makeAvailableAgain() {
        participants.clear();
        status = AppointmentStatus.AVAILABLE;
    }

    public void reschedule(TimeSlot newSlot) {
        Objects.requireNonNull(newSlot, "newSlot");

        if (!newSlot.getEnd().isAfter(newSlot.getStart())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        this.slot = newSlot;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", slot=" + slot +
                ", type=" + type +
                ", status=" + status +
                ", participants=" + participants.size() +
                '}';
    }
}