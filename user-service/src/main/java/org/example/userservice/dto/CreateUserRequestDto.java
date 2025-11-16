package org.example.userservice.dto;

import lombok.Data;

@Data
public class CreateUserRequestDto {
    private String name;
    private String email;
    private int age;
}