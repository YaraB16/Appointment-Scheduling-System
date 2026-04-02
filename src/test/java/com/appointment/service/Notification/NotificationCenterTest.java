package com.appointment.service.Notification;

import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationCenterTest {

    @Test
    void register_andBroadcast_notifiesAllObservers() {
        NotificationCenter center = new NotificationCenter();

        Observer o1 = mock(Observer.class);
        Observer o2 = mock(Observer.class);

        User user = new User("Reem", "reem@gmail.com", "1234", UserRole.USER);

        center.register(o1);
        center.register(o2);

        center.broadcast(user, "hello");

        verify(o1).notify(user, "hello");
        verify(o2).notify(user, "hello");
    }

    @Test
    void unregister_removesObserver() {
        NotificationCenter center = new NotificationCenter();

        Observer o1 = mock(Observer.class);
        Observer o2 = mock(Observer.class);

        User user = new User("Reem", "reem@gmail.com", "1234", UserRole.USER);

        center.register(o1);
        center.register(o2);
        center.unregister(o1);

        center.broadcast(user, "msg");

        verify(o2).notify(user, "msg");
        verify(o1, never()).notify(any(), anyString());
    }

    @Test
    void register_throwsException_whenNull() {
        NotificationCenter center = new NotificationCenter();

        assertThrows(NullPointerException.class, () -> center.register(null));
    }

    @Test
    void broadcast_withNoObservers_doesNothing() {
        NotificationCenter center = new NotificationCenter();

        User user = new User("Reem", "reem@gmail.com", "1234", UserRole.USER);

        assertDoesNotThrow(() -> center.broadcast(user, "hello"));
    }
}