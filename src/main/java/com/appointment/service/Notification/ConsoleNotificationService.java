package com.appointment.service.Notification;



import com.appointment.Domain.User;

import java.util.Objects;

/**
 * Console-based implementation of NotificationService.
 * <p>
 * وظيفته: أثناء تشغيل البرنامج (CLI) يطبع الإشعارات على الشاشة بدل إرسال Email/SMS.
 * مفيد للتجربة والتشغيل اليدوي. في الاختبارات Unit Tests لا تستخدمه؛ استخدم Mockito mock.
 * </p>
 *
 * @author team
 * @version 1.0
 */
public class ConsoleNotificationService implements NotificationService {

    /**
     * Prints notification message to standard output.
     *
     * @param user target user
     * @param message message text
     */
    @Override
    public void send(User user, String message) {
        Objects.requireNonNull(user, "user is required");
        if (message == null) message = "";

        System.out.println("[NOTIFY] to=" + user.getName() + " msg=" + message);
    }
}