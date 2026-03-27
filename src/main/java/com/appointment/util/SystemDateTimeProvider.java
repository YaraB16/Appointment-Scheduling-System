package com.appointment.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Centralized date-time provider.
 * Useful for swapping the clock in tests (fixed clock) without touching business code.
 */
public class SystemDateTimeProvider {

    private final Clock clock;

    /** Uses system default zone clock. */
    public SystemDateTimeProvider() {
        this(Clock.systemDefaultZone());
    }

    public SystemDateTimeProvider(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public Clock clock() {
        return clock;
    }
}