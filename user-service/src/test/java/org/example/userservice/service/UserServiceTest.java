package org.example.userservice.service;

import org.example.commondto.dto.event.EventType;
import org.example.commondto.dto.event.UserEvent;
import org.example.userservice.dto.CreateUserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.entity.User;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.kafka.KafkaProducerService;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя: должен вернуть DTO и вызвать Kafka-продюсер")
    void createUser_shouldReturnDtoAndCallKafka() {
        // Arrange
        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setName("John Doe");
        requestDto.setEmail("john@example.com");
        requestDto.setAge(30);

        User userToSave = new User("John Doe", "john@example.com", 30);
        User savedUser = new User(1L, "John Doe", "john@example.com", 30, LocalDateTime.now());

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setName("John Doe");

        when(userMapper.toUser(requestDto)).thenReturn(userToSave);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponseDto(savedUser)).thenReturn(expectedResponse);
        // Настраиваем мок Kafka-продюсера, чтобы ничего не делать при вызове
        doNothing().when(kafkaProducerService).sendUserEvent(any(UserEvent.class));

        // Act
        UserResponseDto actualResponse = userService.createUser(requestDto);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(expectedResponse.getId());

        // Проверяем, что методы моков были вызваны
        verify(userRepository, times(1)).save(userToSave);
        verify(kafkaProducerService, times(1)).sendUserEvent(any(UserEvent.class));
    }

    @Test
    @DisplayName("Удаление пользователя: должен вызвать Kafka-продюсер")
    void deleteUser_shouldCallKafkaProducer() {
        // Arrange
        Long userId = 1L;
        User userToDelete = new User(userId, "Jane Doe", "jane@example.com", 25, LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
        doNothing().when(userRepository).deleteById(userId);
        doNothing().when(kafkaProducerService).sendUserEvent(any(UserEvent.class));

        // Act
        userService.deleteUser(userId);

        // Assert
        // Проверяем, что метод удаления был вызван
        verify(userRepository, times(1)).deleteById(userId);
        // Проверяем, что событие было отправлено в Kafka
        verify(kafkaProducerService, times(1)).sendUserEvent(
                argThat(event -> event.getEventType() == EventType.USER_DELETED &&
                        event.getEmail().equals("jane@example.com"))
        );
    }

    @Test
    @DisplayName("Поиск пользователя по ID: должен выбросить исключение, если пользователь не найден")
    void findUserById_whenNotFound_shouldThrowException() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserById(userId);
        });
    }
}