package com.appointment.service.Notification;

import com.appointment.Domain.User;
import com.appointment.Domain.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakeNotificationServiceTest {

    @Test
    void send_storesSentNotification() {
        FakeNotificationService service = new FakeNotificationService();
        User user = new User("Reem", "reem@gmail.com", "1234", UserRole.USER);

        service.send(user, "hello");

        assertEquals(1, service.sent().size());
        assertEquals(user, service.sent().get(0).user());
        assertEquals("hello", service.sent().get(0).message());
    }

    @Test
    void sent_returnsImmutableCopy() {
        FakeNotificationService service = new FakeNotificationService();
        User user = new User("Reem", "reem@gmail.com", "1234", UserRole.USER);

        service.send(user, "first");

        assertThrows(UnsupportedOperationException.class, () ->
                service.sent().add(new FakeNotificationService.Sent(user, "second"))
        );
    }

    @Test
    void send_canStoreMultipleNotifications() {
        FakeNotificationService service = new FakeNotificationService();
        User user1 = new User("Reem", "reem@gmail.com", "1234", UserRole.USER);
        User user2 = new User("Ali", "ali@gmail.com", "5678", UserRole.USER);

        service.send(user1, "msg1");
        service.send(user2, "msg2");

        assertEquals(2, service.sent().size());
        assertEquals("msg1", service.sent().get(0).message());
        assertEquals("msg2", service.sent().get(1).message());
    }
}