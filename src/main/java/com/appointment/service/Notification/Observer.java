package com.appointment.service.Notification;

import com.appointment.Domain.User;

/**
 * Observer interface for notification channels (Email/SMS/Console).
 * @author team
 * @version 1.0
 */
public interface Observer {
    /**
     * @param user user
     * @param message message
     */
    void notify(User user, String message);
}