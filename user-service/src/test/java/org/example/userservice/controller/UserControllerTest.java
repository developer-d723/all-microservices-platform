package org.example.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userservice.dto.CreateUserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users: должен создать пользователя и вернуть статус 201 Created")
    void createUser_shouldReturnCreatedUser() throws Exception {

        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setName("Test User");
        requestDto.setEmail("test@user.com");
        requestDto.setAge(25);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Test User");
        responseDto.setEmail("test@user.com");

        when(userService.createUser(any(CreateUserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @DisplayName("GET /api/users/{id}: должен вернуть пользователя, если он существует")
    void getUserById_whenUserExists_shouldReturnUser() throws Exception {
        Long userId = 1L;
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setName("Found User");

        when(userService.findUserById(userId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    @DisplayName("GET /api/users/{id}: должен вернуть статус 404 Not Found, если пользователь не найден")
    void getUserById_whenUserNotFound_shouldReturnNotFound() throws Exception {

        Long userId = 99L;
        when(userService.findUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));


        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id}: должен вернуть статус 204 No Content при успешном удалении")
    void deleteUser_whenUserExists_shouldReturnNoContent() throws Exception {

        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id}: должен вернуть статус 404 Not Found, если пользователь не найден")
    void deleteUser_whenUserNotFound_shouldReturnNotFound() throws Exception {

        Long userId = 99L;
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}