package com.innowise.userservice.model.dto.user;

import java.time.Instant;
import java.time.LocalDate;

public class UserResponseDto {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private boolean active;
    private LocalDate birthDate;

    private Instant createdAt;
    private Instant updatedAt;

}
