package org.example.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userservice.TestcontainersConfiguration;
import org.example.userservice.dto.CreateUserRequestDto;
import org.example.userservice.entity.User;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/users: должен сохранить пользователя в БД и вернуть его")
    void createUser_shouldSaveUserToDatabase() throws Exception {

        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setName("Integration User");
        requestDto.setEmail("integration@user.com");
        requestDto.setAge(40);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Integration User"));


        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("integration@user.com");
    }

    @Test
    @DisplayName("DELETE /api/users/{id}: должен удалить пользователя из БД")
    void deleteUser_shouldRemoveUserFromDatabase() throws Exception {

        User user = userRepository.save(new User("ToDelete", "todelete@user.com", 50));
        Long userId = user.getId();


        assertThat(userRepository.findById(userId)).isPresent();


        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());


        assertThat(userRepository.findById(userId)).isNotPresent();
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }
}