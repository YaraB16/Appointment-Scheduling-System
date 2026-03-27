package com.appointment.service.Notification;

import com.appointment.Domain.User;


public interface NotificationService {

    /**
     * Sends a notification message to a user.
     *
     * @param user the target user
     * @param message the message text
     */
    void send(User user, String message);
}