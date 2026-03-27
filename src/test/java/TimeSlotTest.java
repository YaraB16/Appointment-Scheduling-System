

import com.appointment.value.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void constructor_requiresEndAfterStart() {
        LocalDateTime t = LocalDateTime.of(2026, 3, 27, 10, 0);
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(t, t));
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(t.plusHours(1), t));
    }

    @Test
    void duration_isCorrect() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 27, 10, 0);
        LocalDateTime end = start.plusMinutes(30);

        TimeSlot slot = new TimeSlot(start, end);
        assertEquals(30, slot.duration().toMinutes());
    }

    @Test
    void overlaps_trueWhenRangesOverlap() {
        LocalDateTime base = LocalDateTime.of(2026, 3, 27, 10, 0);

        TimeSlot a = new TimeSlot(base, base.plusMinutes(30));          // 10:00-10:30
        TimeSlot b = new TimeSlot(base.plusMinutes(15), base.plusMinutes(45)); // 10:15-10:45

        assertTrue(a.overlaps(b));
        assertTrue(b.overlaps(a));
    }

    @Test
    void overlaps_falseWhenTouchingAtBoundary() {
        LocalDateTime base = LocalDateTime.of(2026, 3, 27, 10, 0);

        TimeSlot a = new TimeSlot(base, base.plusMinutes(30));          // 10:00-10:30
        TimeSlot b = new TimeSlot(base.plusMinutes(30), base.plusMinutes(60)); // 10:30-11:00

        assertFalse(a.overlaps(b));
        assertFalse(b.overlaps(a));
    }
}