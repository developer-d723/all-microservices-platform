package org.example.userservice.service;

import org.example.userservice.dto.CreateUserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.entity.User;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя: должен вернуть DTO сохраненного пользователя")
    void createUser_shouldReturnUserResponseDto() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setName("John Smith");
        requestDto.setEmail("john@example.com");
        requestDto.setAge(30);

        User userToSave = new User("John Smith", "john@example.com", 30);
        User savedUser = new User("John Smith", "john@example.com", 30);
        savedUser.setId(1L);

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setName("John Smith");


        when(userMapper.toUser(requestDto)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(savedUser);
        when(userMapper.toUserResponseDto(savedUser)).thenReturn(expectedResponse);

        UserResponseDto actualResponse = userService.createUser(requestDto);


        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());

        verify(userRepository, times(1)).save(userToSave);
        verify(userMapper, times(1)).toUser(requestDto);
    }

    @Test
    @DisplayName("Поиск пользователя по ID: должен вернуть пользователя, если он существует")
    void findUserById_whenUserExists_shouldReturnUserDto() {

        Long userId = 1L;
        User foundUser = new User("Jane Smith", "jane@example.com", 25);
        foundUser.setId(userId);
        UserResponseDto expectedDto = new UserResponseDto();
        expectedDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(foundUser));
        when(userMapper.toUserResponseDto(foundUser)).thenReturn(expectedDto);

        UserResponseDto actualDto = userService.findUserById(userId);

        assertEquals(expectedDto.getId(), actualDto.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Поиск пользователя по ID: должен выбросить исключение, если пользователь не найден")
    void findUserById_whenUserNotFound_shouldThrowResourceNotFoundException() {

        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(userId));

        verify(userRepository).findById(userId);
    }
}