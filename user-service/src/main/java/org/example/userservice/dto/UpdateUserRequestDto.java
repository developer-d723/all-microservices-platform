package org.example.userservice.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    private String name;
    private String email;
    private int age;
}