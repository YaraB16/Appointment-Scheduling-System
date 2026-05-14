package com.appointment.value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record TimeSlot(LocalDateTime start,
                       LocalDateTime end) {

    public TimeSlot {

        if (start == null || end == null) {
            throw new IllegalArgumentException("start/end required");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end before start");
        }
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    public Duration duration() {
        return Duration.between(start, end);
    }

    public boolean overlaps(TimeSlot other) {
        Objects.requireNonNull(other, "other required");
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    @Override
    public String toString() {
        return start + " -> " + end;
    }


}