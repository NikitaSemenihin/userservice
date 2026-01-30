package com.innowise.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusDto {
    @NotBlank
    private boolean status;
}
