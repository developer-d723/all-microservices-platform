package org.example.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.example.notificationservice.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class NotificationControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/notifications/email: должен отправить письмо и вернуть статус 200 OK")
    void shouldSendDirectEmailAndReturnOk() throws Exception {

        String recipientEmail = "api-test@example.com";
        Map<String, String> requestBody = Map.of(
                "to", recipientEmail,
                "subject", "Тест через API",
                "text", "Это тело сообщения."
        );


        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());


        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertThat(receivedMessages).hasSize(1);

            MimeMessage receivedMessage = receivedMessages[0];
            assertThat(receivedMessage.getSubject()).isEqualTo("Тест через API");
            assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(recipientEmail);
            assertThat((String) receivedMessage.getContent()).contains("Это тело сообщения.");
        });
    }
}