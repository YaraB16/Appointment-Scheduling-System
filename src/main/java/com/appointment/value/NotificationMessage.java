package com.appointment.value;

import java.time.LocalDateTime;
import java.util.Objects;

public final class NotificationMessage {

    public enum Type {
        INFO, BOOKED, CANCELED, REMINDER
    }

    private final Type type;
    private final String title;
    private final String body;
    private final LocalDateTime createdAt;

    public NotificationMessage(Type type, String title, String body, LocalDateTime createdAt) {
        this.type = Objects.requireNonNull(type, "type");
        this.title = requireNotBlank(title, "title");
        this.body = body == null ? "" : body;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static NotificationMessage info(String title, String body) {
        return new NotificationMessage(Type.INFO, title, body, LocalDateTime.now());
    }

    public static NotificationMessage booked(String appointmentId) {
        return new NotificationMessage(
                Type.BOOKED,
                "Appointment confirmed",
                "Your appointment is confirmed. Id=" + appointmentId,
                LocalDateTime.now()
        );
    }

    public static NotificationMessage canceled(String appointmentId) {
        return new NotificationMessage(
                Type.CANCELED,
                "Appointment cancelled",
                "Your appointment was cancelled. Id=" + appointmentId,
                LocalDateTime.now()
        );
    }

    public Type getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** Simple rendering for Console/Email/SMS channels. */
    public String render() {
        if (body.isBlank()) return "[" + type + "] " + title;
        return "[" + type + "] " + title + " - " + body;
    }

    private static String requireNotBlank(String s, String field) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " is required");
        return s;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}