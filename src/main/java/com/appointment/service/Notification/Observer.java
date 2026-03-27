package com.appointment.service.Notification;

import com.appointment.Domain.User;


public interface Observer {
    /**
     * @param user user
     * @param message message
     */
    void notify(User user, String message);
}