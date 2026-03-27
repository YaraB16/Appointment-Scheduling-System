package com.appointment.service.Notification;

import com.appointment.Domain.User;

import java.util.Objects;


public class NotificationCenterAdapter implements NotificationService {

    private final NotificationCenter center;

    public NotificationCenterAdapter(NotificationCenter center) {
        this.center = Objects.requireNonNull(center);
    }

    @Override
    public void send(User user, String message) {
        center.broadcast(user, message);
    }
}