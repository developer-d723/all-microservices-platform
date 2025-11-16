package org.example.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.commondto.dto.event.UserEvent;
import org.example.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final EmailService emailService;

    @KafkaListener(topics = "${app.kafka.user-notifications-topic}", groupId = "notification-group")
    public void listenUserEvents(@Payload UserEvent event) {
        log.info("Получено событие из Kafka: {}", event);
        emailService.sendNotificationEmail(event);
    }
}