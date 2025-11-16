package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.commondto.dto.event.UserEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducerService {

    private static final String TOPIC = "user-notifications";

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendNotificationEvent(UserEvent event) {
        log.info("Sending event to Kafka topic '{}': {}", TOPIC, event);
        kafkaTemplate.send(TOPIC, event);
    }
}