package org.example.notificationservice.kafka;

import org.awaitility.Awaitility;

import org.example.commondto.dto.event.EventType;
import org.example.commondto.dto.event.UserEvent;
import org.example.notificationservice.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class KafkaConsumerServiceTest extends BaseIntegrationTest {


    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));


    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");
    }

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${app.kafka.user-notifications-topic}")
    private String topicName;

    @Test
    @DisplayName("Должен получить событие USER_CREATED и отправить приветственное письмо")
    void shouldReceiveUserCreatedEventAndSendWelcomeEmail() throws Exception {

        String userEmail = "welcome@test.com";
        UserEvent event = new UserEvent(EventType.USER_CREATED, userEmail);


        kafkaTemplate.send(topicName, event);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertThat(receivedMessages).hasSize(1);

            MimeMessage receivedMessage = receivedMessages[0];
            assertThat(receivedMessage.getSubject()).isEqualTo("Добро пожаловать!");
            assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(userEmail);
            String content = (String) receivedMessage.getContent();
            assertThat(content).contains("Ваш аккаунт на сайте ваш сайт был успешно создан");
        });
    }
}