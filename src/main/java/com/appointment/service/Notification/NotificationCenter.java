package com.appointment.service.Notification;



import com.appointment.Domain.User;

import java.util.LinkedList;
import java.util.Objects;


public class NotificationCenter implements Subject {
    private final LinkedList<Observer> observers = new LinkedList<>();

    @Override
    public void register(Observer observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Broadcasts a message to all observers.
     * @param user user
     * @param message message
     */
    public void broadcast(User user, String message) {
        for (Observer o : observers) {
            o.notify(user, message);
        }
    }
}