package com.appointment.service.Notification;

import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationCenterAdapterTest {

    @Test
    void constructor_throwsException_whenCenterIsNull() {
        assertThrows(NullPointerException.class, () -> new NotificationCenterAdapter(null));
    }

    @Test
    void send_delegatesToNotificationCenterBroadcast() {
        NotificationCenter center = mock(NotificationCenter.class);
        NotificationCenterAdapter adapter = new NotificationCenterAdapter(center);

        User user = new User("Reem", "reem@gmail.com", "1234", UserRole.USER);

        adapter.send(user, "hello");

        verify(center).broadcast(user, "hello");
    }
}