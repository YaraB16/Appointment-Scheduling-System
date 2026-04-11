package com.appointment.service.Notification;

import com.appointment.Domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailNotificationService implements NotificationService {

    private final SmtpConfig config;

    public EmailNotificationService(SmtpConfig config) {
        this.config = config;
    }

    @Override
    public void send(User user, String message) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", config.getHost());
            props.put("mail.smtp.port", config.getPort());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            config.getUsername(),
                            config.getPassword()
                    );
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.getUsername()));
            msg.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(user.getEmail())
            );
            msg.setSubject("Appointment Notification");

            msg.setText("Hello " + user.getName() + "\n\n" + message);

            Transport.send(msg);

            System.out.println("Email sent to " + user.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}