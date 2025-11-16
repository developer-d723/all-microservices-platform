package org.example.userservice.mapper;

import org.example.userservice.dto.CreateUserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto toUserResponseDto(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    @Override
    public User toUser(CreateUserRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        return new User(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getAge()
        );
    }
}