package com.appointment.service.Notification;

import com.appointment.Domain.User;

/**
 * Notification service abstraction.
 * <p>
 * وظيفته: توفير طريقة موحّدة لإرسال الإشعارات للمستخدم (تأكيد حجز، تذكير، تعديل/إلغاء...).
 * </p>
 *
 * <p>
 * ليش Interface؟
 * - عشان نفصل منطق النظام عن طريقة الإرسال (Console/Email/SMS).
 * - عشان نقدر نعمل Mock في الاختبارات باستخدام Mockito بدون إرسال حقيقي.
 * </p>
 *
 * @author team
 * @version 1.0
 */
public interface NotificationService {

    /**
     * Sends a notification message to a user.
     *
     * @param user the target user
     * @param message the message text
     */
    void send(User user, String message);
}