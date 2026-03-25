package com.appointment.service.Notification;

/**
 * Subject interface for managing observers.
 * @author team
 * @version 1.0
 */
public interface Subject {
    void register(Observer observer);
    void unregister(Observer observer);
}