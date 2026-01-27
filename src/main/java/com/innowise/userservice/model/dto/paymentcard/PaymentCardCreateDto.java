package com.innowise.userservice.model.dto.paymentcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardCreateDto {

    @NotBlank
    private String number;

    @NotNull
    private LocalDate expirationDate;
}
