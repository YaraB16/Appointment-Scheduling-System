package com.appointment.util;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class SystemDateTimeProviderTest {

    @Test
    void shouldReturnNowFromProvidedClock() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-12T10:15:30Z"),
                ZoneId.of("UTC")
        );

        SystemDateTimeProvider provider = new SystemDateTimeProvider(fixedClock);

        assertEquals(LocalDateTime.of(2026, 4, 12, 10, 15, 30), provider.now());
    }

    @Test
    void shouldReturnSameClock() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-12T10:15:30Z"),
                ZoneId.of("UTC")
        );

        SystemDateTimeProvider provider = new SystemDateTimeProvider(fixedClock);

        assertSame(fixedClock, provider.clock());
    }

    @Test
    void shouldThrowWhenClockIsNull() {
        assertThrows(NullPointerException.class, () -> new SystemDateTimeProvider(null));
    }
}