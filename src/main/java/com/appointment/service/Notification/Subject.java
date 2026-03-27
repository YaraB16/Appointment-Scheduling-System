package com.appointment.service.Notification;

public interface Subject {
    void register(Observer observer);
    void unregister(Observer observer);

}