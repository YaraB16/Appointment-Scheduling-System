package com.appointment.value;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void constructor_createsValidTimeSlot() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 12, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 12, 11, 0);

        TimeSlot slot = new TimeSlot(start, end);

        assertEquals(start, slot.getStart());
        assertEquals(end, slot.getEnd());
    }

    @Test
    void constructor_throwsException_whenStartIsNull() {
        LocalDateTime end = LocalDateTime.of(2026, 4, 12, 11, 0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new TimeSlot(null, end));

        assertEquals("start/end required", ex.getMessage());
    }

    @Test
    void constructor_throwsException_whenEndIsNull() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 12, 10, 0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new TimeSlot(start, null));

        assertEquals("start/end required", ex.getMessage());
    }

    @Test
    void constructor_throwsException_whenEndBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 12, 11, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 12, 10, 0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new TimeSlot(start, end));

        assertEquals("end before start", ex.getMessage());
    }

    @Test
    void duration_returnsCorrectDuration() {
        TimeSlot slot = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 30)
        );

        assertEquals(Duration.ofMinutes(90), slot.duration());
    }

    @Test
    void overlaps_returnsTrue_whenSlotsOverlap() {
        TimeSlot s1 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 0)
        );

        TimeSlot s2 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 30),
                LocalDateTime.of(2026, 4, 12, 11, 30)
        );

        assertTrue(s1.overlaps(s2));
    }

    @Test
    void overlaps_returnsFalse_whenNoOverlap() {
        TimeSlot s1 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 0)
        );

        TimeSlot s2 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 11, 0),
                LocalDateTime.of(2026, 4, 12, 12, 0)
        );

        assertFalse(s1.overlaps(s2));
    }

    @Test
    void overlaps_throwsException_whenOtherIsNull() {
        TimeSlot slot = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 0)
        );

        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> slot.overlaps(null));

        assertEquals("other required", ex.getMessage());
    }

    @Test
    void toString_returnsCorrectFormat() {
        TimeSlot slot = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 0)
        );

        String text = slot.toString();

        assertTrue(text.contains("2026-04-12T10:00"));
        assertTrue(text.contains("->"));
    }

    @Test
    void equals_returnsTrue_forSameValues() {
        TimeSlot s1 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 0)
        );

        TimeSlot s2 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 0)
        );

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void equals_returnsFalse_forDifferentValues() {
        TimeSlot s1 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 0)
        );

        TimeSlot s2 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 12, 0),
                LocalDateTime.of(2026, 4, 12, 13, 0)
        );

        assertNotEquals(s1, s2);
    }

    @Test
    void equals_returnsFalse_forDifferentType() {
        TimeSlot s1 = new TimeSlot(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                LocalDateTime.of(2026, 4, 12, 11, 0)
        );

        assertNotEquals(s1, "not a TimeSlot");
    }
}