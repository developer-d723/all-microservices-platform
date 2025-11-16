package org.example.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.commondto.dto.event.UserEvent;
import org.example.userservice.TestcontainersConfiguration;
import org.example.userservice.dto.CreateUserRequestDto;
import org.example.userservice.entity.User;
import org.example.userservice.kafka.KafkaProducerService;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/users: должен сохранить пользователя, вернуть HATEOAS-ответ и отправить событие в Kafka")
    void createUser_shouldSaveUserAndReturnHateoasResponseAndSendKafkaEvent() throws Exception {
        // Arrange
        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setName("Integration User");
        requestDto.setEmail("integration@user.com");
        requestDto.setAge(40);

        // Act
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Integration User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").exists())
                .andExpect(header().exists("Location"));

        // Assert (Проверка БД)
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("integration@user.com");

        // Assert (Проверка вызова Kafka)
        // Используем ArgumentCaptor, чтобы "поймать" то, что было отправлено в Kafka
        ArgumentCaptor<UserEvent> userEventCaptor = ArgumentCaptor.forClass(UserEvent.class);
        // timeout() нужен, т.к. отправка может быть асинхронной
        verify(kafkaProducerService, timeout(1000).times(1)).sendUserEvent(userEventCaptor.capture());

        UserEvent capturedEvent = userEventCaptor.getValue();
        assertThat(capturedEvent.getEmail()).isEqualTo("integration@user.com");
    }

    @Test
    @DisplayName("DELETE /api/users/{id}: должен удалить пользователя и отправить событие в Kafka")
    void deleteUser_shouldRemoveUserAndSendKafkaEvent() throws Exception {
        // Arrange
        User user = userRepository.save(new User("ToDelete", "todelete@user.com", 50));
        Long userId = user.getId();

        // Act
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        // Assert (Проверка БД)
        assertThat(userRepository.findById(userId)).isNotPresent();

        // Assert (Проверка вызова Kafka)
        ArgumentCaptor<UserEvent> userEventCaptor = ArgumentCaptor.forClass(UserEvent.class);
        verify(kafkaProducerService, timeout(1000).times(1)).sendUserEvent(userEventCaptor.capture());
        assertThat(userEventCaptor.getValue().getEmail()).isEqualTo("todelete@user.com");
    }
}