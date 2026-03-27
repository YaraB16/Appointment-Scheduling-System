package com.appointment.service.Notification;



import com.appointment.Domain.User;

import java.util.Objects;


public class ConsoleNotificationService implements NotificationService {

    /**
     * Prints notification message to standard output.
     *
     * @param user target user
     * @param message message text
     */
    @Override
    public void send(User user, String message) {
        Objects.requireNonNull(user, "user is required");
        if (message == null) message = "";

        System.out.println("[NOTIFY] to=" + user.getName() + " msg=" + message);
    }
}