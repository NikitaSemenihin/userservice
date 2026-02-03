package com.innowise.userservice.model.dto.user;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
