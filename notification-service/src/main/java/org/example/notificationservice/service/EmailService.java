package org.example.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.commondto.dto.event.UserEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendNotificationEmail(UserEvent event) {
        String subject;
        String text;

        switch (event.getEventType()) {
            case USER_CREATED:
                subject = "Добро пожаловать!";
                text = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
                break;
            case USER_DELETED:
                subject = "Ваш аккаунт удален";
                text = "Здравствуйте! Ваш аккаунт был удалён.";
                break;
            default:
                log.warn("Неизвестный тип события, письмо не будет отправлено: {}", event.getEventType());
                return;
        }
        sendDirectEmail(event.getEmail(), subject, text);
    }

    public void sendDirectEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@mysite.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Письмо успешно отправлено на {}", to);
        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}: {}", to, e.getMessage());
        }
    }
}