package org.example.userservice.mapper;

import org.example.userservice.dto.CreateUserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.entity.User;

public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);
    User toUser(CreateUserRequestDto requestDto);
}