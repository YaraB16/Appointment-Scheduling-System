
package com.appointment.service.Notification;
import com.appointment.Domain.User;


import java.util.ArrayList;
import java.util.List;

public class FakeNotificationService implements NotificationService {

    public static record Sent(User user, String message) {}

    private final List<Sent> sent = new ArrayList<>();

    @Override
    public void send(User user, String message) {
        sent.add(new Sent(user, message));
    }

    public List<Sent> sent() {
        return List.copyOf(sent);
    }
}